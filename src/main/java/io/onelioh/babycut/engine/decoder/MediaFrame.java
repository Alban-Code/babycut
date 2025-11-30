package io.onelioh.babycut.engine.decoder;

public interface MediaFrame {
    double getTimestampSeconds();

    boolean isVideo();

    void close();

}
