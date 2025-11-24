package io.onelioh.babycut.media.decode;

import org.bytedeco.javacv.Frame;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public class AudioFrame implements MediaFrame {

    private double timestampSeconds;
    private Frame rawFrame;
    private int nbSamples;
    private int sampleRate;
    private int channels;

    public AudioFrame() {}

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

        if (buffer instanceof ByteBuffer byteBuffer) {
            ByteBuffer duplicate = byteBuffer.duplicate();
            duplicate.rewind();
            byte[] pcm = new byte[duplicate.remaining()];
            duplicate.get(pcm);
            return pcm;
        }

        if (buffer instanceof ShortBuffer shortBuffer) {
            ShortBuffer duplicate = shortBuffer.duplicate();
            duplicate.rewind();
            byte[] pcm = new byte[duplicate.remaining() * 2];
            int index = 0;
            while (duplicate.hasRemaining()) {
                short sample = duplicate.get();
                pcm[index++] = (byte) (sample & 0xFF);          // LSB
                pcm[index++] = (byte) ((sample >>> 8) & 0xFF);  // MSB
            }
            return pcm;
        }

        throw new IllegalStateException("Unsupported audio sample buffer type: " + buffer.getClass().getName());
    }

    @Override
    public double getTimestampSeconds() {
        return timestampSeconds;
    }

    @Override
    public boolean isVideo() {
        return false;
    }
}
