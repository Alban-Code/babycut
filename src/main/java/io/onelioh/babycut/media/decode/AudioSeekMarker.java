package io.onelioh.babycut.media.decode;

public class AudioSeekMarker extends AudioFrame {
    public AudioSeekMarker() {
        super();
    }

    public AudioSeekMarker(double timestamps) {
        super(timestamps);
    }

    @Override
    public double getTimestampSeconds() {
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
