package io.onelioh.babycut.media.decode;

import org.bytedeco.javacv.Frame;

public class VideoFrame implements MediaFrame {
    private int width;
    private int height;
    private double timestampSeconds;
    private Frame rawFrame;

    public VideoFrame(double timestampSeconds) {
        this.timestampSeconds = timestampSeconds;
    }

    public VideoFrame(Frame frame, double timestampSeconds) {
        width = frame.imageWidth;
        height = frame.imageHeight;
        this.timestampSeconds = timestampSeconds;
        rawFrame = frame;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getTimestampSeconds() {
        return timestampSeconds;
    }

    @Override
    public boolean isVideo() {
        return true;
    }

    public Frame getRawFrame() {
        return rawFrame;
    }

    @Override
    public void close() {
        rawFrame.close();
    }

    public static VideoFrame endMarker() {
        return new VideoFrame(Double.MAX_VALUE);
    }

    public static VideoSeekMarker seekMarker(double seconds) {
        return new VideoSeekMarker(seconds);
    }
}
