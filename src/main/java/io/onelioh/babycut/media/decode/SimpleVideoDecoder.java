package io.onelioh.babycut.media.decode;

public interface SimpleVideoDecoder {

    void openMedia();

    void start();

    MediaFrame readNextFrame();

    void close();

    void seek(double seconds);
}
