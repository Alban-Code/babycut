package io.onelioh.babycut.engine.infra.java;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public final class AudioPlayer {
    private SourceDataLine line;

    public void write(int sampleRate, int channels, ShortBuffer samples) {
        try {
            if (line == null || !line.isOpen()) {
                AudioFormat format = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        sampleRate,
                        16,
                        channels,
                        channels * 2,
                        sampleRate,
                        false
                );
                line = AudioSystem.getSourceDataLine(format);
                line.open(format);
                line.start();
                line.flush();
            }

            samples.rewind();
            byte[] data = new byte[samples.capacity() * 2];
            ByteBuffer.wrap(data)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .asShortBuffer()
                    .put(samples);

            line.write(data, 0, data.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stoppe la lecture et vide les tampons. Ne ferme pas la ligne.
     */
    public void stop() {
        if (line != null && line.isRunning()) {
            line.flush();
        }
    }

    /**
     * Ferme la ligne audio et libère les ressources.
     */
    public void close() {
        if (line != null) {
            line.close();
            line = null;
        }
    }

    public void pause() {
        if (line != null && line.isRunning()) {
            line.stop();
        }
    }
}