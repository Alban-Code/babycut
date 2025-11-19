package io.onelioh.babycut.media.decode;

public interface SimpleVideoDecoder {

    void openMedia();

    void start();

    VideoFrame readNextFrame();

    void close();

    void seek(double seconds);
}
