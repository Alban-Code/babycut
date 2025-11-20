package io.onelioh.babycut.core;

import io.onelioh.babycut.model.media.MediaAsset;
import io.onelioh.babycut.model.project.Project;
import io.onelioh.babycut.model.timeline.Timeline;
import io.onelioh.babycut.model.timeline.TimelineItem;
import io.onelioh.babycut.model.timeline.TimelineTrack;

public interface ProjectContextListener {

    void onProjectChanged(Project project);

    void onMediaAssetAdded(MediaAsset asset);

    void onTimelineActivated(Timeline timeline);

    void onDrawTimeline(Timeline timeline);
}
