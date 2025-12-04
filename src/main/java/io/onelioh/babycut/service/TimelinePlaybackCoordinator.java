package io.onelioh.babycut.service;

import io.onelioh.babycut.engine.player.PreviewPlayer;
import io.onelioh.babycut.engine.player.PreviewPlayerFactory;
import io.onelioh.babycut.engine.player.TimelinePlayback;
import io.onelioh.babycut.model.timeline.Timeline;
import io.onelioh.babycut.viewmodel.PlaybackViewModel;
import javafx.application.Platform;
import javafx.scene.image.Image;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class TimelinePlaybackCoordinator implements TimelinePlayback {

    private final PlaybackViewModel playbackVM;
    private final PreviewPlayerFactory playerFactory;
    private PreviewPlayer player;
    private Runnable pauseOtherPlayer;
    private long durationMilliseconds;
    private final List<Consumer<Image>> frameListeners = new CopyOnWriteArrayList<>();

    private Timeline currentTimeline;

    public TimelinePlaybackCoordinator(PlaybackViewModel playbackVM, PreviewPlayerFactory playerFactory, Runnable pauseOtherPlayer) {
        this.playbackVM = playbackVM;
        this.playerFactory = playerFactory;
        this.pauseOtherPlayer = pauseOtherPlayer;
    }

    @Override
    public void load(Timeline timeline) {
        // TODO
        if (player != null) {
            playerFactory.dispose(player);
        }
        currentTimeline = timeline;
        durationMilliseconds = timeline.getTimelineEnd();
        Platform.runLater(() -> {
            this.playbackVM.setDuration(durationMilliseconds);
            playbackVM.setPlaying(false);
        });
        player = playerFactory.createForTimeline(currentTimeline);
        player.setOnFrameReady((image) -> {
            frameListeners.forEach(listener -> {
                listener.accept(image);
            });
        });
        player.setOnTimeChanged((timestampMilliseconds) -> {
            Platform.runLater(() -> {
                this.playbackVM.setCurrentTime(timestampMilliseconds);
            });
        });
        player.seek(0L);
    }

    @Override
    public Timeline getCurrentTimeline() {
        return currentTimeline;
    }

    @Override
    public void play() {
        if (pauseOtherPlayer != null) pauseOtherPlayer.run();
        if (player != null) player.play();
        Platform.runLater(() -> {
            playbackVM.setPlaying(true);
        });
    }

    @Override
    public void pause() {
        if (player != null) player.pause();
        Platform.runLater(() -> {
            playbackVM.setPlaying(false);
        });
    }

    @Override
    public void stop() {
        if (player != null) player.stop();
        Platform.runLater(() -> {
            playbackVM.setPlaying(false);
        });
    }

    @Override
    public void seek(long timeMilliseconds) {
        if (player != null) {
            player.seek(timeMilliseconds);
        }
    }

    @Override
    public boolean isReady() {
        return player != null;
    }

    @Override
    public long getDurationMilliseconds() {
        return durationMilliseconds;
    }

    @Override
    public void addFrameListener(Consumer<Image> listener) {
        frameListeners.add(listener);
    }

    @Override
    public void removeFrameListener(Consumer<Image> listener) {
        frameListeners.remove(listener);
    }

    @Override
    public void dispose() {
        if (player != null) playerFactory.dispose(player);
    }

    public void setPauseOtherPlayer(Runnable pauseOtherPlayer) {
        this.pauseOtherPlayer = pauseOtherPlayer;
    }
}
