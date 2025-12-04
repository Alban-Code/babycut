package io.onelioh.babycut.model.media;

public class MediaData {
    private int width;
    private int height;
    private double fps;
    private int totalFrames;
    private long durationMilliseconds;

    public MediaData(double fps, int width, int height, int totalFrames, long durationMilliseconds) {
        this.fps = fps;
        this.width = width;
        this.height = height;
        this.totalFrames = totalFrames;
        this.durationMilliseconds = durationMilliseconds;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getFps() {
        return fps;
    }

    public void setFps(double fps) {
        this.fps = fps;
    }

    public int getTotalFrames() {
        return totalFrames;
    }

    public void setTotalFrames(int totalFrames) {
        this.totalFrames = totalFrames;
    }

    public double getDurationMilliseconds() {
        return durationMilliseconds;
    }

    public void setDurationMilliseconds(long durationMilliseconds) {
        this.durationMilliseconds = durationMilliseconds;
    }
}
