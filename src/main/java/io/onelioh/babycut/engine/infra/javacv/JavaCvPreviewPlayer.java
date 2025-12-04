package io.onelioh.babycut.engine.infra.javacv;

import io.onelioh.babycut.engine.decoder.*;
import io.onelioh.babycut.engine.infra.java.AudioPlayer;
import io.onelioh.babycut.engine.player.PreviewPlayer;
import io.onelioh.babycut.ui.utils.converter.VideoFrameToFxImageConverter;
import javafx.scene.image.Image;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class JavaCvPreviewPlayer implements PreviewPlayer {

    // --- CONSTANTES ---
    // Estimation de la latence du buffer audio JavaSound (généralement entre 40ms et 80ms)
    // On retarde la vidéo de cette valeur pour qu'elle colle au son entendu.
    private static final long AUDIO_LATENCY_GUESS_MICROS = 300_000;

    // --- Composants ---
    private final VideoDecoder decoder;
    private final VideoFrameToFxImageConverter converter;
    private final AudioPlayer audioPlayer;

    // --- États internes ---
    private volatile boolean isRunning = false; // Threads vivants
    private volatile boolean isPlaying = false; // Lecture en cours
    private volatile Long seekRequest = null; // Demande de saut
    private volatile boolean isScrubbing = false; // Mode image par image
    private boolean endOfStream = false;

    // --- Synchronization ---
    private volatile long masterClockMicros = 0;
    private volatile long lastSystemTimeNano = 0;

    private final Object pauseLock = new Object();
    private final Object statusLock = new Object();

    // --- Threads et Queues
    private Thread producerThread;
    private Thread audioThread;
    private Thread videoThread;

    private final BlockingQueue<VideoFrame> videoQueue = new ArrayBlockingQueue<>(60);
    private final BlockingQueue<AudioFrame> audioQueue = new ArrayBlockingQueue<>(100);

    private Consumer<Image> onFrameReady;
    private Consumer<Long> onTimeChanged;
    private Runnable onEndOfMedia;

    public JavaCvPreviewPlayer(VideoDecoder decoder, VideoFrameToFxImageConverter converter, AudioPlayer audioPlayer) {
        this.decoder = decoder;
        this.converter = converter;
        this.audioPlayer = audioPlayer;
    }

    // ============================= COMMANDES =============================

    @Override
    public void play() {
        synchronized (statusLock) {
            if (!isRunning) startThreads();

            isPlaying = true;
            isScrubbing = false;

            lastSystemTimeNano = System.nanoTime();

            wakeUpThreads();
        }
    }

    @Override
    public void pause() {
        synchronized (statusLock) {
            isPlaying = false;
            audioPlayer.stop();
        }
    }

    @Override
    public void stop() {
        synchronized (statusLock) {
            isRunning = false;
            isPlaying = false;

            wakeUpThreads();
        }
        stopThreadsSafely();
        decoder.close();
        audioPlayer.close();
    }

    @Override
    public void seek(long milliseconds) {
        if (!isRunning) startThreads();

        synchronized (statusLock) {
            seekRequest = Math.max(0, milliseconds);

            videoQueue.clear();
            audioQueue.clear();
            audioPlayer.stop();

            masterClockMicros = milliseconds * 1_000;
            lastSystemTimeNano = System.nanoTime();

            wakeUpThreads();
        }
    }

    // ============================= Setters ==============================

    @Override
    public void setOnFrameReady(Consumer<Image> cb) {
        onFrameReady = cb;
    }

    @Override
    public void setOnEndOfMedia(Runnable onEndOfMedia) {
        this.onEndOfMedia = onEndOfMedia;
    }

    @Override
    public void setOnTimeChanged(Consumer<Long> onTimeChanged) {
        this.onTimeChanged = onTimeChanged;
    }

    // ============================= THREADS (Logique interne booléenne) =============================

    private void producerLoop() {
        while (isRunning) {
            try {
                // SEEK prioritaire
                if (seekRequest != null) {
                    processSeek();
                    continue;
                }

                // PAUSE
                if (!isPlaying) {
                    synchronized (pauseLock) {
                        // Tant que ça ne joue pas et qu'il n'y a pas de seek mais que ça tourne on est en pause
                        while (!isPlaying && seekRequest == null && isRunning) {
                            pauseLock.wait();
                        }
                    }
                    // S'il y a un seek ou que ça ne tourne plus, on reprend au début de la boucle
                    if (seekRequest != null || !isRunning) continue;
                }

                // LECTURE
                MediaFrame frame = decoder.readNextFrame();
                if (frame == null) {
                    if (!endOfStream) {
                        videoQueue.offer(VideoFrame.endMarker());
                        audioQueue.offer(AudioFrame.endMarker());
                        endOfStream = true;
                    }
                    Thread.sleep(100);
                    continue;
                }

                endOfStream = false;

                if (frame instanceof VideoFrame) videoQueue.put((VideoFrame) frame);
                else if (frame instanceof AudioFrame) audioQueue.put((AudioFrame) frame);

            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void videoLoop() {
        while (isRunning) {
            try {
                if (!isPlaying && !isScrubbing) {
                    synchronized (pauseLock) {
                        while (!isPlaying && !isScrubbing && isRunning) {
                            pauseLock.wait();
                        }
                    }
                }

                if (!isRunning) break;

                VideoFrame vFrame = videoQueue.poll(100, TimeUnit.MILLISECONDS);
                if (vFrame == null) continue;

                // Fin de fichier
                if (vFrame.getTimestampMilliseconds() == Long.MAX_VALUE) {
                    if (onEndOfMedia != null) onEndOfMedia.run();
                    continue;
                }

                // Scrubbing (une image et pause)
                if (isScrubbing) {
                    isScrubbing = false;
                    sendToUi(vFrame);
                    continue;
                }

                // Lecture normale
                if (isPlaying) {
                    long vidTime = (long) (vFrame.getTimestampMilliseconds() * 1_000);
                    while (isPlaying && seekRequest == null) {
                        long now = System.nanoTime();
                        long deltaNano = now - lastSystemTimeNano;

                        long estimatedAudioTime = (masterClockMicros + (deltaNano / 1000)) - AUDIO_LATENCY_GUESS_MICROS;
                        long diff = vidTime - estimatedAudioTime;

                        if (diff <= 0) break;

                        if (diff > 10_000) {
                            Thread.sleep(diff / 1000);
                        } else Thread.yield();
                    }

                    if (seekRequest != null || !isPlaying) {
                        vFrame.close();
                        continue;
                    }
                    sendToUi(vFrame);
                } else {
                    vFrame.close();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void audioLoop() {
        while (isRunning) {
            try {
                if (!isPlaying) {
                    synchronized (pauseLock) {
                        while (!isPlaying && isRunning) pauseLock.wait();
                    }
                }
                if (!isRunning) break;

                AudioFrame aFrame = audioQueue.poll(100, TimeUnit.MILLISECONDS);
                if (aFrame == null) continue;

                // Fin de fichier
                if (aFrame.getTimestampMilliseconds() == Long.MAX_VALUE) {
                    continue;
                }

                if (aFrame instanceof AudioSeekMarker) {
                    audioPlayer.stop();
                    continue;
                }

                if (isPlaying) {
                    audioPlayer.write(aFrame.getSampleRate(), aFrame.getChannels(), (java.nio.ShortBuffer) aFrame.getRawFrame().samples[0]);

                    masterClockMicros = (long) (aFrame.getTimestampMilliseconds() * 1_000);
                    lastSystemTimeNano = System.nanoTime();
                }
                aFrame.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    // =============================== HELPERS ================================

    private void processSeek() {
        long target = seekRequest;
        seekRequest = null;
        endOfStream = false;

        videoQueue.clear();
        audioQueue.clear();

        decoder.seek(target);
        audioQueue.offer(AudioFrame.seekMarker(target));

        MediaFrame frame = null;
        while (true) {
            frame = decoder.readNextFrame();
            if (frame == null) break;

            if (frame.getTimestampMilliseconds() >= target - 0.1) {
                break; // Trouvé !
            }
            frame.close();
        }
    }

    private void wakeUpThreads() {
        synchronized (pauseLock) {
            pauseLock.notifyAll();
        }
    }

    private void startThreads() {
        if (isRunning) return;

        isRunning = true;
        try {
            decoder.start();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        producerThread = new Thread(this::producerLoop, "Producer");
        videoThread = new Thread(this::videoLoop, "VideoConsumer");
        audioThread = new Thread(this::audioLoop, "AudioConsumer");

        producerThread.setDaemon(true);
        videoThread.setDaemon(true);
        audioThread.setDaemon(true);

        producerThread.start();
        videoThread.start();
        audioThread.start();
    }

    private void stopThreadsSafely() {
        try {
            if (producerThread != null) producerThread.join(500);
            if (videoThread != null) videoThread.join(500);
            if (audioThread != null) audioThread.join(500);
        } catch (Exception ignored) {

        }
    }

    private void sendToUi(VideoFrame frame) {
        if (onFrameReady == null) {
            frame.close();
            return;
        }
        try {
            Image img = converter.toImage(frame);
            onFrameReady.accept(img);
            if (onTimeChanged != null) {
                onTimeChanged.accept(frame.getTimestampMilliseconds());
            }
        } catch (Exception ignored) {
        } finally {
            frame.close();
        }
    }


}
