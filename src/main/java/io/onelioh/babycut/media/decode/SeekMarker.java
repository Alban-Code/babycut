package io.onelioh.babycut.media.decode;

import org.bytedeco.javacv.Frame;

public class SeekMarker extends AudioFrame {
    public SeekMarker() {
        super();
    }

    public SeekMarker(double timestampSeconds, Frame rawFrame) {
        super(timestampSeconds, rawFrame);
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
