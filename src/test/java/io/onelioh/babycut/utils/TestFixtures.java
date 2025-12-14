package io.onelioh.babycut.utils;

import io.onelioh.babycut.model.media.AssetType;
import io.onelioh.babycut.model.media.MediaAsset;
import io.onelioh.babycut.model.media.MediaInfo;
import io.onelioh.babycut.model.timeline.ClipItem;
import io.onelioh.babycut.model.timeline.Timeline;
import io.onelioh.babycut.model.timeline.TimelineTrack;
import io.onelioh.babycut.model.timeline.TrackType;

import java.nio.file.Path;

public class TestFixtures {

    public static MediaAsset createDummyAsset() {
        return createAsset("dummy-video.mp4", AssetType.VIDEO);
    }

    public static MediaAsset createAsset(String filename, AssetType type) {
        Path path = Path.of("/test/" + filename);
        MediaInfo mediaInfo = createDummyMediaInfo();

        return new MediaAsset(path, type, mediaInfo);
    }

    private static MediaInfo createDummyMediaInfo() {
        return new MediaInfo();
    }

    public static ClipItem createClip(long startTime, double sourceIn, long duration, MediaAsset asset) {
        return new ClipItem(startTime, sourceIn, duration, asset);
    }

    public static ClipItem createClip(long startTime, long duration) {
        return new ClipItem(startTime, 0.0, duration, createDummyAsset());
    }

    public static Timeline createEmptyTimeline() {
        return new Timeline("Test Timeline");
    }

    public static TimelineTrack createTrack(TrackType type) {
        return new TimelineTrack(type);
    }

    public static Timeline getTimelineWithEmptyTracks() {
        Timeline timeline = new Timeline("Timeline");

        TimelineTrack videoTrack = new TimelineTrack(TrackType.VIDEO);
        TimelineTrack audioTrack = new TimelineTrack(TrackType.AUDIO);

        timeline.addTrack(videoTrack);
        timeline.addTrack(audioTrack);

        return timeline;
    }

    public static Timeline getTimelineWithTracks(long durationMs) {
        Timeline timeline = new Timeline("Timeline");

        TimelineTrack videoTrack = new TimelineTrack(TrackType.VIDEO);
        TimelineTrack audioTrack = new TimelineTrack(TrackType.AUDIO);

        MediaAsset asset = createDummyAsset();

        ClipItem videoClip = new ClipItem(0L, 2, durationMs, asset);
        videoTrack.addItem(videoClip);
        ClipItem audioClip = new ClipItem(0L, 2, durationMs, asset);
        audioTrack.addItem(audioClip);

        timeline.addTrack(videoTrack);
        timeline.addTrack(audioTrack);

        return timeline;
    }
}
