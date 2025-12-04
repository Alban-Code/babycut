package io.onelioh.babycut.engine.decoder;

public class AudioSeekMarker extends AudioFrame {
    public AudioSeekMarker() {
        super();
    }

    public AudioSeekMarker(long timestamps) {
        super(timestamps);
    }

    @Override
    public long getTimestampMilliseconds() {
        return 0;
    }

    @Override
    public boolean isVideo() {
        return false;
    }

    @Override
    public void close() {
        return;
    }
}
