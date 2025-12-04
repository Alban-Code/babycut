package io.onelioh.babycut.engine.player;

import javafx.scene.image.Image;

import java.util.function.Consumer;

public interface Playback {

    void play();

    void pause();

    void stop();

    void seek(long timeMilliseconds);

    boolean isReady();

    long getDurationMilliseconds();

    void addFrameListener(Consumer<Image> listener);

    void removeFrameListener(Consumer<Image> listener);

    void dispose();
}
