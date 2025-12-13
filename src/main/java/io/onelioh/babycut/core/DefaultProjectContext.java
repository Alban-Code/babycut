package io.onelioh.babycut.core;

import io.onelioh.babycut.model.media.MediaAsset;
import io.onelioh.babycut.model.media.MediaInfo;
import io.onelioh.babycut.model.media.MediaStream;
import io.onelioh.babycut.model.project.Project;
import io.onelioh.babycut.model.timeline.ClipItem;
import io.onelioh.babycut.model.timeline.Timeline;
import io.onelioh.babycut.model.timeline.TimelineTrack;
import io.onelioh.babycut.model.timeline.TrackType;
import io.onelioh.babycut.viewmodel.ProjectViewModel;
import io.onelioh.babycut.viewmodel.TimelineViewModel;

import java.util.List;

public class DefaultProjectContext implements ProjectContext {

    private Project currentProject;
    private final ProjectViewModel projectViewModel;
    private final TimelineViewModel timelineViewModel;

    public DefaultProjectContext(ProjectViewModel projectVM, TimelineViewModel timelineVM) {
        this.projectViewModel = projectVM;
        this.timelineViewModel = timelineVM;
    }


    @Override
    public Project createProject() {
        currentProject = new Project();
        currentProject.setName("Default Project");
        currentProject.setPath("path à définir");
        Timeline activeTimeline = new Timeline("default");

        TimelineTrack videoTrack = new TimelineTrack(TrackType.VIDEO);
        TimelineTrack audioTrack = new TimelineTrack(TrackType.AUDIO);

        activeTimeline.addTrack(videoTrack);
        activeTimeline.addTrack(audioTrack);

        currentProject.addTimeline(activeTimeline);
        currentProject.setActiveTimeline(activeTimeline);

        this.projectViewModel.setProject(currentProject);
        this.timelineViewModel.setTimeline(activeTimeline);

        return currentProject;
    }

    @Override
    public void addMediaAsset(MediaAsset newAsset) {
        currentProject.addMediaAsset(newAsset);
        this.projectViewModel.getAssets().add(newAsset);
    }

    @Override
    public void addTimeline(Timeline newTimeline) {
        currentProject.addTimeline(newTimeline);

        projectViewModel.getTimelines().add(newTimeline);
        projectViewModel.setActiveTimeline(newTimeline);
        timelineViewModel.setTimeline(newTimeline);
    }

    @Override
    public void addTimelineItem(MediaAsset asset) {
        Timeline activeTimeline = currentProject.getActiveTimeline();
        if (activeTimeline == null) return;

        MediaInfo info = asset.getMediaInfo();
        long duration = info.getDurationMilliseconds();
        long insertionTime = activeTimeline.getTimelineEnd();

        TimelineTrack videoTrack = activeTimeline.getTracks().getFirst();
        TimelineTrack audioTrack = activeTimeline.getTracks().get(1);

        // Créer et ajouter les clips au model, en gardant les références
        ClipItem videoClip = null;
        ClipItem audioClip = null;

        if (!info.getVideoStreams().isEmpty()) {
            videoClip = new ClipItem(insertionTime, 0.0, duration, asset);
            videoTrack.addItem(videoClip);
        }

        MediaStream primaryAudio = getAudioStream(info);
        if (primaryAudio != null) {
            audioClip = new ClipItem(insertionTime, 0.0, duration, asset);
            audioTrack.addItem(audioClip);
        }

        // Mettre à jour timelineEnd AVANT d'ajouter au ViewModel
        this.timelineViewModel.setTimelineEnd(activeTimeline.getTimelineEnd());

        // Maintenant ajouter au ViewModel (déclenche rebuildUI avec le bon timelineEnd)
        if (videoClip != null) {
            this.timelineViewModel.addClipToTrack(0, videoClip);
        }
        if (audioClip != null) {
            this.timelineViewModel.addClipToTrack(1, audioClip);
        }
    }

    @Override
    public void addTimelineTrack(TimelineTrack newTrack) {

    }


    @Override
    public Timeline getActiveTimeline() {
        return currentProject != null ? currentProject.getActiveTimeline() : null;
    }

    @Override
    public Project getCurrentProject() {
        return currentProject;
    }

    @Override
    public List<MediaAsset> getMediaAssets() {
        if (currentProject == null) return null;

        return currentProject.getMediaAssets();
    }

    @Override
    public TimelineTrack getVideoTrack() {
        Timeline activeTimeline = getActiveTimeline();
        if (activeTimeline != null && !activeTimeline.getTracks().isEmpty()) {
            return activeTimeline.getTracks().getFirst();
        }

        return null;
    }

    @Override
    public TimelineTrack getAudioTrack() {
        Timeline activeTimeline = getActiveTimeline();
        if (activeTimeline != null && activeTimeline.getTracks().size() > 1) {
            return activeTimeline.getTracks().get(1);
        }

        return null;
    }

    @Override
    public boolean ensureActiveTimelineExists() {
        return getActiveTimeline() != null;
    }

    private MediaStream getAudioStream(MediaInfo info) {
        var audioStreams = info.getAudioStreams();

        if (audioStreams.isEmpty()) return null;

        return audioStreams.getFirst();
    }
}
