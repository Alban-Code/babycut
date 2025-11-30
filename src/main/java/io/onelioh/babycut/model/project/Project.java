package io.onelioh.babycut.model.project;

import io.onelioh.babycut.model.media.MediaAsset;
import io.onelioh.babycut.model.timeline.Timeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Représente un projet de montage vidéo.
 * <p>
 * Un Project contient la liste des médias importés (MediaAsset) et
 * une ou plusieurs timelines (séquences de montage).
 * Il sert de structure principale pour sauvegarder et charger les projets.
 */
public class Project {
    private List<MediaAsset> mediaAssets = new ArrayList<>();
    private List<Timeline> timelines = new ArrayList<>();

    public List<MediaAsset> getMediaAssets() {
        return Collections.unmodifiableList(mediaAssets);
    }

    public List<Timeline> getTimelines() {
        return Collections.unmodifiableList(timelines);
    }

    public void setTimelines(List<Timeline> timelines) {
        this.timelines = new ArrayList<>(timelines);
    }

    public void setMediaAssets(List<MediaAsset> mediaAssets) {
        this.mediaAssets = new ArrayList<>(mediaAssets);
    }

    public void addMediaAsset(MediaAsset newMediaAsset) {
        mediaAssets.add(newMediaAsset);
    }

    public void addTimeline(Timeline newTimeline) {
        timelines.add(newTimeline);
    }

}
