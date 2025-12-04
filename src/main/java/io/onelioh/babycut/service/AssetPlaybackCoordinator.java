package io.onelioh.babycut.service;

import io.onelioh.babycut.engine.player.*;
import io.onelioh.babycut.model.media.MediaAsset;
import io.onelioh.babycut.viewmodel.PlaybackViewModel;
import javafx.application.Platform;
import javafx.scene.image.Image;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class AssetPlaybackCoordinator implements AssetPlayback {

    private final PlaybackViewModel playbackVM;
    private final PreviewPlayerFactory playerFactory;
    private PreviewPlayer player;
    private Runnable pauseOtherPlayer;
    private long durationMilliseconds;
    private final List<Consumer<Image>> frameListeners = new CopyOnWriteArrayList<>();

    private MediaAsset currentAsset;

    public AssetPlaybackCoordinator(PlaybackViewModel playbackVM, PreviewPlayerFactory factory, Runnable pauseOtherPlayer) {
        playerFactory = factory;
        this.playbackVM = playbackVM;
        this.pauseOtherPlayer = pauseOtherPlayer;
    }

    @Override
    public void play() {
        if (pauseOtherPlayer != null) pauseOtherPlayer.run();
        if (player != null) player.play();
        Platform.runLater(() -> playbackVM.setPlaying(true));
    }

    @Override
    public void pause() {
        if (player != null) player.pause();
        Platform.runLater(() -> playbackVM.setPlaying(false));
    }

    @Override
    public void stop() {
        if (player != null) player.stop();
    }

    @Override
    public void seek(long positionMilliseconds) {
        if (player != null) player.seek(positionMilliseconds);
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
        if (player != null) {
            playerFactory.dispose(player);
        }
        Platform.runLater(() -> {
            this.playbackVM.setReady(false);
            this.playbackVM.setCurrentAsset(null);
        });
    }

    @Override
    public void load(MediaAsset asset) {
        if (player != null) {
            playerFactory.dispose(player);
        }

        currentAsset = asset;
        durationMilliseconds = asset.getMediaInfo().getDurationMilliseconds();
        Platform.runLater(() -> {
            this.playbackVM.setDuration(durationMilliseconds);
            this.playbackVM.setCurrentAsset(currentAsset);
            this.playbackVM.setReady(true);
            this.playbackVM.setPlaying(false);
        });
        player = playerFactory.createForAsset(asset);
        player.setOnFrameReady((image) -> {
            Platform.runLater(() -> {
                frameListeners.forEach(listener -> {
                    listener.accept(image);
                });
            });
        });
        player.setOnTimeChanged((timestampMilliseconds) -> {
            Platform.runLater(() -> {
                this.playbackVM.setCurrentTime(timestampMilliseconds);
            });
        });
        player.setOnEndOfMedia(() -> {
            Platform.runLater(() -> {
                this.playbackVM.setCurrentTime(0L);
                this.playbackVM.setPlaying(false);
                if (player != null) {
                    player.pause();
                    player.seek(0L);
                }
            });
        });
        player.seek(0L);
    }

    @Override
    public MediaAsset getCurrentAsset() {
        return currentAsset;
    }

    public void setPauseOtherPlayer(Runnable pauseOtherPlayer) {
        this.pauseOtherPlayer = pauseOtherPlayer;
    }
}
