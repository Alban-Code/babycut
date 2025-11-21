package io.onelioh.babycut.media.playback;

import io.onelioh.babycut.media.decode.*;
import io.onelioh.babycut.media.playback.audio.AudioPlayer;
import io.onelioh.babycut.ui.player.VideoFrameToFxImageConverter;
import javafx.application.Platform;
import javafx.scene.image.WritableImage;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

public class PreviewPlayer {

    private final SimpleVideoDecoder decoder;
    private final VideoFrameToFxImageConverter converter;
    private final AudioPlayer audioPlayer;

    private Thread producerThread;
    private volatile boolean playing = false;
    private volatile boolean running = false;
    private volatile boolean seekInProgress = false;

    private Thread audioThread;
    private Thread videoThread;

    private BlockingQueue<VideoFrame> videoQueue;
    private BlockingQueue<AudioFrame> audioQueue;

    private static final int VIDEO_QUEUE_SIZE = 120;
    private static final int AUDIO_QUEUE_SIZE = 60;

    private Consumer<WritableImage> onFrameReady;
    private Consumer<Double> onTimeChanged;
    private Runnable onEndOfMedia;

    private volatile Double pendingSeekSeconds = null;

    public PreviewPlayer(SimpleVideoDecoder decoder, VideoFrameToFxImageConverter converter, AudioPlayer audioPlayer) {
        this.decoder = decoder;
        this.converter = converter;
        this.audioPlayer = audioPlayer;
        videoQueue = new ArrayBlockingQueue<>(VIDEO_QUEUE_SIZE);
        audioQueue = new ArrayBlockingQueue<>(AUDIO_QUEUE_SIZE);
    }

    public void play() {
        if (producerThread == null || !producerThread.isAlive()) {
            startPlaybackThread();
        }
        playing = true;
    }

    public void pause() {
        playing = false;
        audioPlayer.stop();
    }

    public void stop() {
        running = false;
        playing = false;
        audioPlayer.stop();
    }

    public void seek(double seconds) {
        seekInProgress = true;
        audioPlayer.stop();
        if (seconds < 0) seconds = 0;
        pendingSeekSeconds = seconds;
    }

    public void setOnFrameReady(Consumer<WritableImage> cb) {
        onFrameReady = cb;
    }

    public void start() {
        if (producerThread != null && producerThread.isAlive()) return;
        startPlaybackThread();
        playing = false;
    }

    private void producerLoop() {
        while (running) {
            Double seekTarget = pendingSeekSeconds;
            if (seekTarget != null) {
                pendingSeekSeconds = null;

                videoQueue.clear();
                audioQueue.clear();

                decoder.seek(seekTarget);

                try {
                    audioQueue.put(new SeekMarker());
                } catch(Exception ignored) {

                }


                seekInProgress = false;

                continue;
            }

            MediaFrame frame = decoder.readNextFrame();
            if (frame == null) {
                break;
            }
            try {
                if (frame.isVideo()) {
                    videoQueue.put((VideoFrame) frame);
                } else {
                    audioQueue.put((AudioFrame) frame);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

        }
    }

    private void videoConsumerLoop() {
        while(running) {
            if (!playing) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {
                }
                continue;
            }

            try {
                VideoFrame vFrame = videoQueue.take();
                long videoTimestampMicroseconds = (long) vFrame.getTimestampSeconds() * 1_000_000;
                long audioTimeMicroseconds = audioPlayer.getMicrosecondPosition();
                long latency = audioTimeMicroseconds - videoTimestampMicroseconds;
                final long TOLERANCE_MICROSECONDS = 66000;
                if (latency < -TOLERANCE_MICROSECONDS ) {
                    // We are too late, skip this frame
                    System.out.println("Skipping frame due to audio latency: " + latency);
                } else if ( latency > TOLERANCE_MICROSECONDS ) {
                    // We are in advance, wait
                    Thread.sleep(-latency / 1000);
                    pushFrameToUi(vFrame);

                } else {
                    pushFrameToUi(vFrame);
                }
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void audioConsumerLoop() {
        while(running) {
            if (!playing) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {
                }
                continue;
            }
            try {
                AudioFrame aFrame = audioQueue.take();

                if (aFrame instanceof SeekMarker) {
                    audioPlayer.stop();  // Flush du buffer
                    continue;
                }

                audioPlayer.openIfNeeded(aFrame.sampleRate(), aFrame.channels());
                audioPlayer.writeSamples(aFrame.getPcm());
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void startPlaybackThread() {
        running = true;
        producerThread = new Thread(this::producerLoop, "Producer Thread");
        producerThread.setDaemon(true);
        producerThread.start();

        videoThread = new Thread(this::videoConsumerLoop, "Video Consumer Thread");
        videoThread.setDaemon(true);
        videoThread.start();

        audioThread = new Thread(this::audioConsumerLoop, "Audio Consumer Thread");
        audioThread.setDaemon(true);
        audioThread.start();
    }

    public void setOnEndOfMedia(Runnable onEndOfMedia) {
        this.onEndOfMedia = onEndOfMedia;
    }

    public void setOnTimeChanged(Consumer<Double> onTimeChanged) {
        this.onTimeChanged = onTimeChanged;
    }

    public void close() {
        stop();
        if (producerThread != null) {
            try {
                producerThread.join(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        audioPlayer.close();
        decoder.close();
    }

    private void pushFrameToUi(VideoFrame frame) {
        WritableImage image = converter.toImage(frame);
        if (image == null || onFrameReady == null) return;

        WritableImage finalImage = image;
        Platform.runLater(() -> onFrameReady.accept(finalImage));
    }
}
