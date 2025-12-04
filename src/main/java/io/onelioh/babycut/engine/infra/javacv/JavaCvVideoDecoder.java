package io.onelioh.babycut.engine.infra.javacv;

import io.onelioh.babycut.engine.decoder.*;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.FrameGrabber;

import java.io.File;

public class JavaCvVideoDecoder implements VideoDecoder {

    private FFmpegFrameGrabber frameGrabber;
    private String path;
    private VideoReaderState state;

    public JavaCvVideoDecoder(String filePath) {
        path = filePath;
        state = VideoReaderState.CREATED;
    }

    @Override
    public void openMedia() {
        if (state == VideoReaderState.STARTED) {
            throw new IllegalStateException("Impossible d'ouvrir un nouveau fichier sans fermer le précédent");
        }

        frameGrabber = new FFmpegFrameGrabber(new File(path));
        // frameGrabber.setSampleFormat(AV_SAMPLE_FMT_S16);
        frameGrabber.setAudioChannels(2);
        frameGrabber.setSampleMode(FrameGrabber.SampleMode.SHORT);
        state = VideoReaderState.INITIALIZED;
    }

    @Override
    public void start() throws FFmpegFrameGrabber.Exception, IllegalStateException {
        if (state != VideoReaderState.INITIALIZED) {
            throw new IllegalStateException("Lecture impossible sur un état autre que INITIALIZED: " + state);
        }
        frameGrabber.start();
        state = VideoReaderState.STARTED;
    }

    @Override
    public MediaFrame readNextFrame() {
        if (state != VideoReaderState.STARTED) return null;

        try {
            Frame frame = frameGrabber.grab();

            if (frame == null) {
                // state = VideoReaderState.FINISHED;
                return null;
            }

            if (frame.image == null && frame.samples == null) {
                System.out.println("gros caca");
                return null;
            }

            // CRITIQUE : On clone la frame pour la détacher du Grabber
            // Cela alloue une nouvelle zone mémoire native indépendante
            Frame clonedFrame = frame.clone();

            long timestampMilliseconds = Math.round(frameGrabber.getTimestamp() / 1_000.0);

            if (clonedFrame.image != null) {
                System.out.println("Image");
                return new VideoFrame(clonedFrame, timestampMilliseconds);
            } else if (clonedFrame.samples != null) {
                System.out.println("Audio");
                return new AudioFrame(timestampMilliseconds, clonedFrame, frameGrabber.getSampleRate(), frameGrabber.getAudioChannels());
            }

            // Si la frame n'est ni audio ni vidéo (ex: metadata), on doit la fermer pour éviter la fuite
            clonedFrame.close();
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            // state = VideoReaderState.CLOSED;
            return null;
        }
    }

    @Override
    public void close() {
        if (state == VideoReaderState.CLOSED) return;

        try {
            if (frameGrabber != null) {
                frameGrabber.stop();
                frameGrabber.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            state = VideoReaderState.CLOSED;
        }
    }

    @Override
    public void seek(long milliseconds) {
        // Decoder initialisé ou starté.
        if (state != VideoReaderState.STARTED && state != VideoReaderState.INITIALIZED) {
            return;
        }

        // on évite les valeurs débiles
        if (milliseconds < 0) {
            milliseconds = 0;
        }

        long targetMicros = milliseconds * 1_000L;

        try {
            // FFmpegFrameGrabber : temps en microsecondes
            frameGrabber.setTimestamp(targetMicros);
        } catch (Exception e) {
            e.printStackTrace();
            //    state = VideoReaderState.CLOSED;
        }
    }

}
