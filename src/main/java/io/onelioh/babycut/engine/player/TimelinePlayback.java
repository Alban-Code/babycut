package io.onelioh.babycut.engine.player;

import io.onelioh.babycut.model.timeline.Timeline;

public interface TimelinePlayback extends Playback {
    void load(Timeline timeline);

    Timeline getCurrentTimeline();
}
