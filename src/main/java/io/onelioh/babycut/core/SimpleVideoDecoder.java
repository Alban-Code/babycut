package io.onelioh.babycut.core;

public interface SimpleVideoDecoder {

    void openMedia();

    void start();

    VideoFrame readNextFrame();

    void close();
}
