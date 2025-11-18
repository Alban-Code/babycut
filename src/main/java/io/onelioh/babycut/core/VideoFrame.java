package io.onelioh.babycut.core;

import org.bytedeco.javacv.Frame;

public class VideoFrame {
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

    public Frame getRawFrame() {
        return rawFrame;
    }
}
