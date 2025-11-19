package io.onelioh.babycut.media.playback;

import io.onelioh.babycut.media.decode.SimpleVideoDecoder;
import io.onelioh.babycut.media.decode.VideoFrame;
import io.onelioh.babycut.ui.player.VideoFrameToFxImageConverter;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.function.Consumer;

public class PreviewPlayer {

    private final SimpleVideoDecoder decoder;
    private final VideoFrameToFxImageConverter converter;

    private Thread playbackThread;
    private volatile boolean playing = false;
    private volatile boolean running = false;

    private Consumer<WritableImage> onFrameReady;
    private Consumer<Double> onTimeChanged;
    private Runnable onEndOfMedia;

    private volatile Double pendingSeekSeconds = null;

    public PreviewPlayer(SimpleVideoDecoder decoder, VideoFrameToFxImageConverter converter) {
        this.decoder = decoder;
        this.converter = converter;
    }

    public void play() {
        if (playbackThread == null || !playbackThread.isAlive()) {
            startPlaybackThread();
        }
        playing = true;
    }

    public void pause() {
        playing = false;
    }

    public void stop() {
        running = false;
        playing = false;
    }

    public void seek(double seconds) {
        System.out.println("Je suis dans le seek");
        if (seconds < 0) seconds = 0;
        pendingSeekSeconds = seconds;
    }

    public void setOnFrameReady(Consumer<WritableImage> cb) {
        onFrameReady = cb;
    }

    public void start() {
        if (playbackThread != null && playbackThread.isAlive()) return;
        startPlaybackThread();
        playing = false;
    }

    private void playbackLoop() {

        while (running) {
            Double seekSeconds = pendingSeekSeconds;
            if (seekSeconds != null) {
                System.out.println("seek de fou" + seekSeconds);
                pendingSeekSeconds = null;

                decoder.seek(seekSeconds);

                VideoFrame frame = decoder.readNextFrame();

                if (frame != null) {
                    pushFrameToUi(frame);
                }

                if (onTimeChanged != null && frame != null) {
                    double currentTime = frame.getTimestampSeconds();
                    Platform.runLater(() -> onTimeChanged.accept(currentTime));
                }

                continue;
            }

            if (!playing) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {
                }
                continue;
            }

            VideoFrame frame = decoder.readNextFrame();
            if (frame == null) {
                playing = false;

                if(onEndOfMedia != null) {
                    Platform.runLater(onEndOfMedia);
                }
                continue;
            }

            WritableImage image = converter.toImage(frame);
            if (image == null) {
                continue;
            }

            pushFrameToUi(frame);

            if (onTimeChanged != null) {
                double currentTime = frame.getTimestampSeconds();
                Platform.runLater(() -> onTimeChanged.accept(currentTime));
            }

            // tempo approximative : ex. 30 fps → 33 ms
            try {
                Thread.sleep(16);
            } catch (InterruptedException ignored) {
            }


        }
    }

    private void startPlaybackThread() {
        running = true;
        playbackThread = new Thread(this::playbackLoop);
        playbackThread.setDaemon(true);
        playbackThread.start();
    }

    public void setOnEndOfMedia(Runnable onEndOfMedia) {
        this.onEndOfMedia = onEndOfMedia;
    }

    public void setOnTimeChanged(Consumer<Double> onTimeChanged) {
        this.onTimeChanged = onTimeChanged;
    }

    private void pushFrameToUi(VideoFrame frame) {
        WritableImage image = converter.toImage(frame);
        if (image == null || onFrameReady == null) return;

        WritableImage finalImage = image;
        Platform.runLater(() -> onFrameReady.accept(finalImage));
    }
}
