package io.onelioh.babycut.engine.player;

import io.onelioh.babycut.model.media.MediaAsset;
import javafx.scene.image.Image;


import java.util.function.Consumer;


public interface PlaybackCoordinator {
    void load(MediaAsset asset);

    void play();

    void pause();

    void stop();

    void seek(double durationSeconds);

    boolean isReady();

    double getDurationSeconds();

    void setOnReady(Consumer<Image> listener);

    void setOnTimeChanged(Consumer<Double> listener);

    void setOnEndOfMedia(Runnable listener);
}
