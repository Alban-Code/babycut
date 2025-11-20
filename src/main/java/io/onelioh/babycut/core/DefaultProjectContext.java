package io.onelioh.babycut.core;

import io.onelioh.babycut.model.media.MediaAsset;
import io.onelioh.babycut.model.media.MediaInfo;
import io.onelioh.babycut.model.media.MediaStream;
import io.onelioh.babycut.model.project.Project;
import io.onelioh.babycut.model.timeline.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultProjectContext implements ProjectContext{

    private Project currentProject;
    private Timeline activeTimeline;
    private List<ProjectContextListener> listeners = new CopyOnWriteArrayList<>();


    @Override
    public Project createProject() {
        currentProject = new Project();

        activeTimeline = new Timeline("default");

        TimelineTrack videoTrack = new TimelineTrack(TrackType.VIDEO);
        TimelineTrack audioTrack = new TimelineTrack(TrackType.AUDIO);

        activeTimeline.addTrack(videoTrack);
        activeTimeline.addTrack(audioTrack);

        notifyProjectChanged(currentProject);
        notifyActiveTimelineAdded(activeTimeline);
        return currentProject;
    }

    @Override
    public void addMediaAsset(MediaAsset newAsset) {
        currentProject.addMediaAsset(newAsset);
        notifyMediaAssetAdded(newAsset);
    }

    @Override
    public void addTimeline(Timeline newTimeline) {
        currentProject.addTimeline(newTimeline);
        activeTimeline = newTimeline;
        notifyActiveTimelineAdded(newTimeline);
    }

    @Override
    public void addTimelineItem(MediaAsset asset) {
        MediaInfo info = asset.getMediaInfo();
        double duration = info.getDurationSeconds();

        double insertionTime = activeTimeline.getTimelineEnd();

        TimelineTrack videoTrack = activeTimeline.getTracks().getFirst();
        TimelineTrack audioTrack = activeTimeline.getTracks().get(1);

        if (!info.getVideoStreams().isEmpty()) {
            ClipItem videoClip = new ClipItem(insertionTime, 0.0, duration, asset);
            videoTrack.addItem(videoClip);
        }

        MediaStream primaryAudio = getAudioStream(info);
        if (primaryAudio != null) {
            ClipItem audioClip = new ClipItem(insertionTime, 0.0, duration, asset);
            audioTrack.addItem(audioClip);
        }

        notifyDrawTimeline(activeTimeline);
    }

    @Override
    public void addTimelineTrack(TimelineTrack newTrack) {

    }


    @Override
    public Timeline getActiveTimeline() {
        return activeTimeline;
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
        if (activeTimeline != null) {
            return activeTimeline.getTracks().getFirst();
        }

        return null;
    }

    @Override
    public TimelineTrack getAudioTrack() {
        if (activeTimeline != null) {
            return activeTimeline.getTracks().get(1);
        }

        return null;
    }

    @Override
    public boolean ensureActiveTimelineExists() {
        return activeTimeline != null;
    }

    @Override
    public void addListener(ProjectContextListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(ProjectContextListener listener) {
        this.listeners.remove(listener);
    }

    private void notifyProjectChanged(Project project) {
        for (ProjectContextListener listener: listeners) {
            listener.onProjectChanged(project);
        }
    }

    private void notifyMediaAssetAdded(MediaAsset asset) {
        for (var listener: listeners) {
            listener.onMediaAssetAdded(asset);
        }
    }

    private void notifyActiveTimelineAdded(Timeline timeline) {
        for (var listener: listeners) {
            listener.onTimelineActivated(timeline);
        }
    }

    private void notifyDrawTimeline(Timeline timeline) {
        for (var listener: listeners) {
            listener.onDrawTimeline(timeline);
        }
    }

    private MediaStream getAudioStream(MediaInfo info) {
        var audioStreams = info.getAudioStreams();

        if (audioStreams.isEmpty()) return null;

        return audioStreams.getFirst();
    }
}
