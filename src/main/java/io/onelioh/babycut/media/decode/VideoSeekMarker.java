package io.onelioh.babycut.media.decode;

import org.bytedeco.javacv.Frame;

public class VideoSeekMarker extends VideoFrame {
    public VideoSeekMarker(double timestampSeconds) {
        super(timestampSeconds);
    }

    public VideoSeekMarker(Frame frame, double timestampSeconds) {
        super(frame, timestampSeconds);
    }
}
