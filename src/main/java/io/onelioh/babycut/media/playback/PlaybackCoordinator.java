package io.onelioh.babycut.media.playback;

import io.onelioh.babycut.model.media.MediaAsset;
import javafx.scene.image.WritableImage;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public interface PlaybackCoordinator {
    void load(MediaAsset asset);

    void play();

    void pause();

    void stop();

    void seek(double durationSeconds);

    boolean isReady();

    double getDurationSeconds();

    void setOnReady(Consumer<WritableImage> listener);

    void setOnTimeChanged(Consumer<Double> listener);

    void setOnEndOfMedia(Runnable listener);
}
