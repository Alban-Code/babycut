package io.onelioh.model;

import java.util.List;

public class MediaInfo {
    private List<MediaStream> videoStreams;
    private List<MediaStream> audioStreams;



    public MediaInfo(List<MediaStream> videoStreams, List<MediaStream> audioStreams) {
        this.videoStreams = videoStreams;
        this.audioStreams = audioStreams;

    }

    public List<MediaStream> getVideoStreams() { return this.videoStreams; }
    public List<MediaStream> getAudioStreams() { return this.audioStreams; }


    public List<String> getVideoLabels() {
        return videoStreams.stream().map(MediaStream::label).toList();
    }

    public List<String> getAudioLables() {
        return audioStreams.stream().map(MediaStream::label).toList();
    }

}
