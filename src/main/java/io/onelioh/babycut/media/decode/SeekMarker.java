package io.onelioh.babycut.media.decode;

public class SeekMarker extends AudioFrame {
    public SeekMarker() {
        super();
    }

    @Override
    public double getTimestampSeconds() {
        return 0;
    }

    @Override
    public boolean isVideo() {
        return false;
    }
}
