package io.onelioh.babycut.engine.player;

import javafx.scene.image.Image;

import java.util.function.Consumer;

public interface PreviewPlayer {

    void play();

    void pause();

    void stop();

    void seek(long milliseconds);

    void setOnFrameReady(Consumer<Image> cb);

    void setOnEndOfMedia(Runnable onEndOfMedia);

    void setOnTimeChanged(Consumer<Long> onTimeChanged);
}
