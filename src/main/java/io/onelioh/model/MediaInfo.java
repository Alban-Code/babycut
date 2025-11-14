package io.onelioh.model;

import java.util.List;

public class MediaInfo {
    private List<MediaStream> videoStreams;
    private List<MediaStream> audioStreams;
    private double durationSeconds;

    public MediaInfo() {
        this.videoStreams = List.of();
        this.audioStreams = List.of();
        this.durationSeconds = 0;
    }

    public MediaInfo(List<MediaStream> videoStreams, List<MediaStream> audioStreams, double durationSeconds) {
        this.videoStreams = videoStreams;
        this.audioStreams = audioStreams;
        this.durationSeconds = durationSeconds;
    }

    public List<MediaStream> getVideoStreams() { return this.videoStreams; }
    public List<MediaStream> getAudioStreams() { return this.audioStreams; }
    public double getDurationSeconds() { return this.durationSeconds; }


    public List<String> getVideoLabels() {
        return videoStreams.stream().map(MediaStream::label).toList();
    }

    public List<String> getAudioLabels() {
        return audioStreams.stream().map(MediaStream::label).toList();
    }

}
