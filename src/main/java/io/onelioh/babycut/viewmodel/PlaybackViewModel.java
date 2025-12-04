package io.onelioh.babycut.viewmodel;

import io.onelioh.babycut.engine.player.PlaybackState;
import io.onelioh.babycut.model.media.MediaAsset;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;

public class PlaybackViewModel {

    private final LongProperty currentTime = new SimpleLongProperty(0L);
    private final LongProperty duration = new SimpleLongProperty(0L);
    private final BooleanProperty playing = new SimpleBooleanProperty(false);
    private final DoubleProperty playbackSpeed = new SimpleDoubleProperty(1.0);
    // Etat du lecteur
    private final ObjectProperty<PlaybackState> playbackState = new SimpleObjectProperty<>(PlaybackState.IDLE);
    // Asset lu
    private final ObjectProperty<MediaAsset> currentAsset = new SimpleObjectProperty<>();
    private final BooleanProperty ready = new SimpleBooleanProperty(false);

    private static String formatTime(long milliseconds) {
        if (milliseconds < 0) milliseconds = 0;
        int s = (int) Math.floor(milliseconds / 1000.0);
        int h = s / 3600;
        int m = (s % 3600) / 60;
        int ss = s % 60;
        if (h > 0) return String.format("%d:%02d:%02d", h, m, ss);
        return String.format("%02d:%02d", m, ss);
    }

    // ======================= temps formaté ==========================

    private final StringBinding formattedTime = Bindings.createStringBinding(() -> formatTime(currentTime.get()) + " / " + formatTime(duration.get()), currentTime, duration);

    public StringBinding formattedTimeProperty() {
        return formattedTime;
    }

    // ======================= Getter/setter currenTime ==========================

    public long getCurrentTime() {
        return currentTime.get();
    }

    public LongProperty currentTimeProperty() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime.set(currentTime);
    }

    // ======================= Getter/setter duration ==========================

    public long getDuration() {
        return duration.get();
    }

    public LongProperty durationProperty() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration.set(duration);
    }

    // ======================= Getter/setter playing ==========================

    public boolean isPlaying() {
        return playing.get();
    }

    public BooleanProperty playingProperty() {
        return playing;
    }

    public void setPlaying(boolean isPlaying) {
        this.playing.set(isPlaying);
    }

    // ======================= Getter/setter playbackSpeed ==========================

    public double getPlaybackSpeed() {
        return playbackSpeed.get();
    }

    public DoubleProperty playbackSpeedProperty() {
        return playbackSpeed;
    }

    public void setPlaybackSpeed(double playbackSpeed) {
        this.playbackSpeed.set(playbackSpeed);
    }

    // ======================= Getter/setter playbackState ==========================

    public PlaybackState getPlaybackState() {
        return playbackState.get();
    }

    public ObjectProperty<PlaybackState> playbackStateProperty() {
        return playbackState;
    }

    public void setPlaybackState(PlaybackState state) {
        this.playbackState.set(state);
    }

    // ======================= Getter/setter currentAsset ==========================

    public MediaAsset getCurrentAsset() {
        return currentAsset.get();
    }

    public ObjectProperty<MediaAsset> currentAssetProperty() {
        return currentAsset;
    }

    public void setCurrentAsset(MediaAsset asset) {
        this.currentAsset.set(asset);
    }

    // ======================= Getter/setter ready ==========================

    public boolean isReady() {
        return ready.get();
    }

    public BooleanProperty readyProperty() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready.set(ready);
    }
}
