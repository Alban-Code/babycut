package io.onelioh.babycut.engine.decoder;

import org.bytedeco.javacv.Frame;

public class VideoSeekMarker extends VideoFrame {
    public VideoSeekMarker(long timestampSeconds) {
        super(timestampSeconds);
    }

    public VideoSeekMarker(Frame frame, long timestampSeconds) {
        super(frame, timestampSeconds);
    }
}
