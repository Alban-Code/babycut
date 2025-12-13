package io.onelioh.babycut.viewmodel;

import io.onelioh.babycut.model.timeline.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TimelineViewModel {

    public static class TrackViewModel {
        private TimelineTrack track;
        private ObservableList<TimelineItem> items = FXCollections.observableArrayList();
        private TrackType type;

        public TrackViewModel(TimelineTrack track) {
            this.track = track;
            this.type = track.getType();
            items.setAll(track.getItems());
        }

        public TimelineTrack getTrack() {
            return track;
        }

        public ObservableList<TimelineItem> getItems() {
            return items;
        }

        public TrackType getType() {
            return type;
        }
    }

    // ===================== TIMELINE ET TRACKS =====================

    private ObjectProperty<Timeline> timeline = new SimpleObjectProperty<>();
    private ObservableList<TrackViewModel> tracks = FXCollections.observableArrayList();

    // ===================== SELECTION =====================
    private ObjectProperty<ClipItem> selected = new SimpleObjectProperty<>();

    // ===================== AFFICHAGE =====================

    private long maxTimelineDuration = 3_600_000L;
    private DoubleProperty pixelsPerSecond = new SimpleDoubleProperty(30.0);
    private DoubleProperty scroll = new SimpleDoubleProperty(0.0);
    private LongProperty playheadPosition = new SimpleLongProperty(0);

    // ===================== ETAT =====================
    private BooleanProperty hasTimeline = new SimpleBooleanProperty(false);
    private BooleanProperty modified = new SimpleBooleanProperty(false);
    private LongProperty timelineEnd = new SimpleLongProperty(0);

     // ===================== DIRTY FLAG =====================
     private BooleanProperty needsRefresh = new SimpleBooleanProperty(false);

    // ===================== COMMANDES =====================
    public void addClipToTrack(int trackIndex, ClipItem clip) {
        if (trackIndex >= 0 && trackIndex < tracks.size()) {
            tracks.get(trackIndex).getItems().add(clip);
            markAsModified();
            markDirty();
        }
    }


    // ===================== UTILITAIRES =====================
    public void zoomIn() {
        double current = this.pixelsPerSecond.get();
        pixelsPerSecond.set(Math.min(current * 1.2, 500.0));
    }

    public void zoomOut() {
        double current = this.pixelsPerSecond.get();
        pixelsPerSecond.set(Math.max(current / 1.2, 20.0));
    }

    public void markAsModified() {
        this.modified.set(true);
    }

    public void markDirty() {
        this.needsRefresh.set(!this.needsRefresh.get());
    }


    // ===================== GETTERS/SETTERS =====================

    public Timeline getTimeline() {
        return timeline.get();
    }

    public ObjectProperty<Timeline> timelineProperty() {
        return timeline;
    }

    public void setTimeline(Timeline timeline) {
        if (timeline != null) {
            this.timeline.set(timeline);
            this.tracks.setAll(timeline.getTracks().stream().map(TrackViewModel::new).toList());
            this.hasTimeline.set(true);
            this.selected.set(null);
            this.timelineEnd.set(timeline.getTimelineEnd());
        } else {
            this.timeline.set(null);
            this.tracks.clear();
            this.hasTimeline.set(false);
            this.selected.set(null);
        }
    }

    public ObservableList<TrackViewModel> getTracks() {
        return tracks;
    }

    public ClipItem getSelected() {
        return selected.get();
    }

    public ObjectProperty<ClipItem> selectedProperty() {
        return selected;
    }

    public void selectClip(ClipItem selected) {
        this.selected.set(selected);
        this.markDirty();
    }

    public double getPixelsPerSecond() {
        return pixelsPerSecond.get();
    }

    public DoubleProperty pixelsPerSecondProperty() {
        return pixelsPerSecond;
    }

    public void setPixelsPerSecond(double pixelsPerSecond) {
        this.pixelsPerSecond.set(pixelsPerSecond);
    }

    public double getScroll() {
        return scroll.get();
    }

    public DoubleProperty scrollProperty() {
        return scroll;
    }

    public void setScroll(double scroll) {
        this.scroll.set(scroll);
    }

    public long getPlayheadPosition() {
        return playheadPosition.get();
    }

    public LongProperty playheadPositionProperty() {
        return playheadPosition;
    }

    public void setPlayheadPosition(long playheadPosition) {
        this.playheadPosition.set(playheadPosition);
    }

    public boolean isHasTimeline() {
        return hasTimeline.get();
    }

    public BooleanProperty hasTimelineProperty() {
        return hasTimeline;
    }

    public void setHasTimeline(boolean hasTimeline) {
        this.hasTimeline.set(hasTimeline);
    }

    public boolean isModified() {
        return modified.get();
    }

    public BooleanProperty modifiedProperty() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified.set(modified);
    }

    public long getTimelineEnd() {
        return timelineEnd.get();
    }

    public LongProperty timelineEndProperty() {
        return timelineEnd;
    }

    public void setTimelineEnd(long timelineEnd) {
        this.timelineEnd.set(timelineEnd);
    }

    public BooleanProperty needsRefreshProperty() {
        return needsRefresh;
    }

    public long getMaxTimelineDuration() {
        return this.maxTimelineDuration;
    }

    public void setMaxTimelineDuration(long durationMs) {
        this.maxTimelineDuration = durationMs;
    }
}
