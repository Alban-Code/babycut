package io.onelioh.babycut.engine.decoder;

public interface MediaFrame {
    long getTimestampMilliseconds();

    boolean isVideo();

    void close();

}
