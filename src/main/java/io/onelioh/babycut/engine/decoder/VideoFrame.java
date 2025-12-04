package io.onelioh.babycut.engine.decoder;

import org.bytedeco.javacv.Frame;

public class VideoFrame implements MediaFrame {
    private int width;
    private int height;
    private long timestampMilliseconds;
    private Frame rawFrame;

    public VideoFrame(long timestampMilliseconds) {
        this.timestampMilliseconds = timestampMilliseconds;
    }

    public VideoFrame(Frame frame, long timestampMilliseconds) {
        width = frame.imageWidth;
        height = frame.imageHeight;
        this.timestampMilliseconds = timestampMilliseconds;
        rawFrame = frame;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getTimestampMilliseconds() {
        return timestampMilliseconds;
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
        return new VideoFrame(Long.MAX_VALUE);
    }

    public static VideoSeekMarker seekMarker(long seconds) {
        return new VideoSeekMarker(seconds);
    }
}
