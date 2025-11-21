package io.onelioh.babycut.media.playback.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public final class AudioPlayer {
    private SourceDataLine line;

    public void openIfNeeded(int sampleRate, int channels) {
        if (line != null) {
            return;
        }
        AudioFormat format = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                sampleRate,
                16,
                channels,
                channels * 2,
                sampleRate,
                false // little-endian
        );
        try {
            line = AudioSystem.getSourceDataLine(format);
            line.open(format);
            line.start();
        } catch (LineUnavailableException e) {
            throw new IllegalStateException("Impossible d’ouvrir la sortie audio", e);
        }
    }

    public void writeSamples(byte[] pcm) {
        if (line == null || pcm == null || pcm.length == 0) return;
        line.write(pcm, 0, pcm.length);
    }

    public void stop() {
        if (line != null && line.isRunning()) {
            line.stop();
            line.flush();
        }
    }

    public void close() {
        if (line != null) {
            stop();
            line.close();
            line = null;
        }
    }

    public long getMicrosecondPosition() {
        return line.getMicrosecondPosition();
    }
}