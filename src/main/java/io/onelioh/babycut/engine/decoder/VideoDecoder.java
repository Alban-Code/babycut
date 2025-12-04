package io.onelioh.babycut.engine.decoder;

public interface VideoDecoder {

    void openMedia();

    void start() throws Exception;

    MediaFrame readNextFrame();

    void close();

    void seek(long milliseconds);
}
