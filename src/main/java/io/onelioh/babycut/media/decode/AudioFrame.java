package io.onelioh.babycut.media.decode;

import org.bytedeco.javacv.Frame;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public class AudioFrame implements MediaFrame {

    private double timestampSeconds;
    private Frame rawFrame;
    private int nbSamples;
    private int sampleRate;
    private int channels;

    public AudioFrame() {}

    public AudioFrame(double timestampSeconds) {
        this.timestampSeconds = timestampSeconds;
    }

    public AudioFrame(double timestampSeconds, Frame rawFrame, int sampleRate, int channels) {
        this.timestampSeconds = timestampSeconds;
        this.rawFrame = rawFrame;
        this.nbSamples = rawFrame.samples != null ? rawFrame.samples[0].limit() : 0;
        this.sampleRate = sampleRate;
        this.channels = channels;
    }

    public int getSampleRate() { return sampleRate; }

    public int getChannels() { return channels; }

    public int getNbSamples() { return nbSamples; }

    public ByteBuffer interleavedSamples() { return (ByteBuffer) rawFrame.samples[0]; }

    public byte[] getPcm() {
        if (rawFrame == null || rawFrame.samples == null || rawFrame.samples.length == 0) {
            return new byte[0];
        }

        var buffer = rawFrame.samples[0];
        if (buffer == null) {
            return new byte[0];
        }

        if (buffer instanceof ShortBuffer shortBuffer) {
            ShortBuffer duplicate = shortBuffer.duplicate();
            duplicate.rewind();

            // On prépare un tableau de bytes (2x plus grand car 1 short = 2 bytes)
            byte[] pcm = new byte[duplicate.remaining() * 2];

            // LE SECRET EST ICI : On utilise ByteBuffer pour gérer l'Endianness
            ByteBuffer dst = ByteBuffer.wrap(pcm).order(ByteOrder.LITTLE_ENDIAN);

            // On verse les shorts dans le ByteBuffer, qui va les ranger proprement en bytes
            dst.asShortBuffer().put(duplicate);

            return pcm;
        }

        return new byte[0];
    }
    @Override
    public double getTimestampSeconds() {
        return timestampSeconds;
    }

    @Override
    public boolean isVideo() {
        return false;
    }

    @Override
    public void close() {
        rawFrame.close();
    }

    public static AudioFrame endMarker() {
        return new AudioFrame(Double.MAX_VALUE);
    }

    public static AudioSeekMarker seekMarker(double seconds) {
        return new AudioSeekMarker(seconds);
    }

    public Frame getRawFrame() {
        return rawFrame;
    }
}
