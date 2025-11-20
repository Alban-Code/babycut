package io.onelioh.babycut.core;

import io.onelioh.babycut.model.media.MediaAsset;
import io.onelioh.babycut.model.project.Project;
import io.onelioh.babycut.model.timeline.Timeline;
import io.onelioh.babycut.model.timeline.TimelineItem;
import io.onelioh.babycut.model.timeline.TimelineTrack;

import java.util.List;

public interface ProjectContext {

    // Commandes
    public Project createProject();

    public void addMediaAsset(MediaAsset newAsset);

    public void addTimeline(Timeline newTimeline);

    public void addTimelineItem(MediaAsset asset);

    public void addTimelineTrack(TimelineTrack newTrack);

    // consultations
    Timeline getActiveTimeline();

    Project getCurrentProject();

    List<MediaAsset> getMediaAssets();

    TimelineTrack getVideoTrack();

    TimelineTrack getAudioTrack();

    boolean ensureActiveTimelineExists();


    // notifications
    void addListener(ProjectContextListener listener);

    void removeListener(ProjectContextListener listener);


}
