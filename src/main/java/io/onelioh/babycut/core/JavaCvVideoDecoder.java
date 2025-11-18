package io.onelioh.babycut.core;

import io.onelioh.babycut.model.datas.MediaData;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;

import java.io.File;

public class JavaCvVideoDecoder implements SimpleVideoDecoder {

    private FFmpegFrameGrabber frameGrabber;
    private String path;
    private MediaData data;
    private VideoReaderState state;
    private int framesRead;

    public JavaCvVideoDecoder(String filePath) {
        path = filePath;
        state = VideoReaderState.CREATED;
        framesRead = 0;
    }

    @Override
    public void openMedia() {
        File file = new File(path);
        frameGrabber = new FFmpegFrameGrabber(file);
        start();
    }

    @Override
    public void start() {
        try {
            frameGrabber.start();
            state = VideoReaderState.STARTED;
            data = new MediaData(
                    frameGrabber.getFrameRate(),
                    frameGrabber.getImageWidth(),
                    frameGrabber.getImageHeight(),
                    frameGrabber.getLengthInFrames(),
                    frameGrabber.getLengthInTime() / 1_000_000.0);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public VideoFrame readNextFrame() {
        if (state != VideoReaderState.STARTED) return null;

        try {
            Frame frame = frameGrabber.grabImage();
            if (frame == null) {
                state = VideoReaderState.FINISHED;
                return null;
            }
            double timestampSeconds = frameGrabber.getTimestamp() / 1_000_000.0;
            VideoFrame vFrame = new VideoFrame(frame, timestampSeconds);
            framesRead++;
            return vFrame;

        } catch (Exception e) {
            e.printStackTrace();
            state = VideoReaderState.CLOSED;
            return null;
        }
    }

    @Override
    public void close() {
        if (frameGrabber == null) return;

        try {
            frameGrabber.release();
            state = VideoReaderState.CLOSED;
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
