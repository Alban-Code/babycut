package io.onelioh.babycut.media.playback;

import io.onelioh.babycut.model.media.MediaAsset;
import javafx.scene.image.Image;

import java.util.function.Consumer;

public class JavaCvPlaybackCoordinator implements PlaybackCoordinator{

    private PreviewPlayerFactory playerFactory;
    private PreviewPlayer player;
    private double durationSeconds = 0.0;
    private Consumer<Image> readyListener;
    private Consumer<Double> timeChangedListener;
    private Runnable endListener;

    public JavaCvPlaybackCoordinator() {
        playerFactory = new JavaCvPreviewPlayerFactory();
    }

    @Override
    public void load(MediaAsset asset) {
        if (player != null) {
            playerFactory.dispose(player);
        }

        durationSeconds = asset.getMediaInfo().getDurationSeconds();
        player = playerFactory.createForAsset(asset);
        if (readyListener != null) {
            player.setOnFrameReady(readyListener);
        }
        if (timeChangedListener != null) {
            player.setOnTimeChanged(timeChangedListener);
        }
        if (endListener != null) {
            player.setOnEndOfMedia(endListener);
        }
        player.seek(0.0);
    }

    @Override
    public void play() {
        if (player != null) player.play();
    }

    @Override
    public void pause() {
        if (player != null) player.pause();
    }

    @Override
    public void stop() {
        if (player != null) player.stop();
    }

    @Override
    public void seek(double durationSeconds) {
        if (player != null) player.seek(durationSeconds);
    }

    @Override
    public boolean isReady() {
        return player != null;
    }

    @Override
    public double getDurationSeconds() {
        return durationSeconds;
    }

    @Override
    public void setOnReady(Consumer<Image> listener) {
        readyListener = listener;
        if (player != null) {
            player.setOnFrameReady(listener);
        }
    }

    @Override
    public void setOnTimeChanged(Consumer<Double> listener) {
        timeChangedListener = listener;
        if (player != null) {
            player.setOnTimeChanged(listener);
        }
    }

    @Override
    public void setOnEndOfMedia(Runnable listener) {
        endListener = listener;
        if (player != null) {
            player.setOnEndOfMedia(listener);
        }
    }
}
