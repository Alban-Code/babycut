package io.onelioh.babycut.media.decode;

import org.bytedeco.javacv.Frame;

public class VideoFrame implements MediaFrame {
    private int width;
    private int height;
    private double timestampSeconds;
    private Frame rawFrame;

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
}
