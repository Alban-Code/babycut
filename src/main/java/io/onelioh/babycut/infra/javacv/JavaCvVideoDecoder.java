package io.onelioh.babycut.infra.javacv;

import io.onelioh.babycut.media.decode.AudioFrame;
import io.onelioh.babycut.media.decode.SimpleVideoDecoder;
import io.onelioh.babycut.media.decode.VideoFrame;
import io.onelioh.babycut.media.decode.VideoReaderState;
import io.onelioh.babycut.model.datas.MediaData;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

import static org.bytedeco.ffmpeg.global.avutil.AV_SAMPLE_FMT_S16;

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
        frameGrabber.setSampleFormat(AV_SAMPLE_FMT_S16);
        frameGrabber.setSampleMode(FrameGrabber.SampleMode.SHORT);
        frameGrabber.setAudioChannels(2);
        frameGrabber.setSampleRate(44100);
        start();
    }

    @Override
    public void start() {
        try {
            frameGrabber.start();
            state = VideoReaderState.STARTED;
            data = new MediaData(frameGrabber.getFrameRate(), frameGrabber.getImageWidth(), frameGrabber.getImageHeight(), frameGrabber.getLengthInFrames(), frameGrabber.getLengthInTime() / 1_000_000.0);


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
    public AudioFrame readNextAudioFrame() {
        if (state != VideoReaderState.STARTED) return null;

        try {
            Frame frame = frameGrabber.grabSamples();
            if (frame == null) {
                return null;
            }
            double timestampSeconds = frameGrabber.getTimestamp() / 1_000_000.0;
            AudioFrame aFrame = new AudioFrame(timestampSeconds, frame);
            return aFrame;

        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    public void seek(double seconds) {
        if (frameGrabber == null) {
            return;
        }

        // on évite les valeurs débiles
        if (seconds < 0) {
            seconds = 0;
        }

        // si tu veux, tu peux clamp à la durée connue
        if (data != null) {
            double durationSec = data.getDuration(); // adapte au vrai getter
            if (durationSec > 0 && seconds > durationSec) {
                seconds = durationSec;
            }
        }

        long targetMicros = (long) (seconds * 1_000_000L);

        try {
            // FFmpegFrameGrabber : temps en microsecondes
            frameGrabber.setTimestamp(targetMicros);

            // On considère qu'on est toujours en lecture possible
            state = VideoReaderState.STARTED;

            // Optionnel : mettre à jour une estimation de framesRead
            if (data != null) {
                double durationSec = data.getDuration();      // adapte au vrai getter
                int totalFrames = data.getTotalFrames();           // adapte au vrai getter

                if (durationSec > 0 && totalFrames > 0) {
                    double ratio = seconds / durationSec;
                    framesRead = (int) Math.round(ratio * totalFrames);
                } else {
                    framesRead = 0;
                }
            } else {
                framesRead = 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            state = VideoReaderState.CLOSED;
        }
    }
}
