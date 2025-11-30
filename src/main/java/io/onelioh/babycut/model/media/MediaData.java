package io.onelioh.babycut.model.media;

public class MediaData {
    private int width;
    private int height;
    private double fps;
    private int totalFrames;
    private double duration;

    public MediaData(double fps, int width, int height, int totalFrames, double duration) {
        this.fps = fps;
        this.width = width;
        this.height = height;
        this.totalFrames = totalFrames;
        this.duration = duration;
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

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }
}
