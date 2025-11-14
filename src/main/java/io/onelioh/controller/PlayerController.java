package io.onelioh.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class PlayerController {

    @FXML private MediaView mediaView;
    @FXML private Button playBtn, pauseBtn, stopBtn;
    @FXML private Label timeLabel;
    @FXML private Slider timeSlider;
    @FXML private StackPane centerPane;

    private MediaPlayer player;

    @FXML
    private void initialize() {
        setControlsEnabled(false);

        // handlers boutons
        playBtn.setOnAction(e -> { if (player != null) player.play();});
        pauseBtn.setOnAction(e -> { if (player != null) player.pause();});
        stopBtn.setOnAction(e -> { if (player != null) player.stop();});

        // handlers slider
        timeSlider.setDisable(true);
        timeSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging && player != null && player.getStatus() != MediaPlayer.Status.UNKNOWN) {
                player.seek(Duration.seconds(timeSlider.getValue()));
            }
        });
        // Seek si clic direct sur la barre (drag terminé)
        timeSlider.setOnMouseReleased(e -> {
            if (player != null) {
                player.seek(Duration.seconds(timeSlider.getValue()));
            }
        });

    }

    public void attachPlayer(MediaPlayer player) {
        this.player = player;
        mediaView.setMediaPlayer(player);
        mediaView.setPreserveRatio(true);

        mediaView.fitWidthProperty().bind(centerPane.widthProperty());
        mediaView.fitHeightProperty().bind(centerPane.heightProperty());

    }

    public void onMediaReady(double totalSec) {
        setControlsEnabled(true);
        double total = player.getTotalDuration().toSeconds();
        timeSlider.setDisable(false);
        timeSlider.setMin(0);
        timeSlider.setMax(total);
        timeSlider.setValue(0);
        updateTime(0, total);

        // Avancement → slider + label
        player.currentTimeProperty().addListener((obs, oldT, newT) -> {
            if (!timeSlider.isValueChanging()) {
                timeSlider.setValue(newT.toSeconds());
            }
            updateTime(newT.toSeconds(), total);
        });

        // Fin de média → remet à zéro
        player.setOnEndOfMedia(() -> {
            player.stop();
            timeSlider.setValue(0);
            updateTime(0, total);
        });
    }

    public void updateTime(double currentSec, double totalSec) {
        if (!timeSlider.isValueChanging()) {
            timeSlider.setValue(currentSec);
        }
        timeLabel.setText(formatTime(currentSec) + " / " + formatTime(totalSec));
    }

    private String formatTime(double sec) {
        if (Double.isNaN(sec) || sec < 0) sec = 0;
        int s = (int) Math.floor(sec);
        int h = s / 3600;
        int m = (s % 3600) / 60;
        int ss = s % 60;
        if (h > 0) return String.format("%d:%02d:%02d", h, m, ss);
        return String.format("%02d:%02d", m, ss);
    }

    private void setControlsEnabled(boolean enabled) {
        playBtn.setDisable(!enabled);
        pauseBtn.setDisable(!enabled);
        stopBtn.setDisable(!enabled);
        timeSlider.setDisable(!enabled);
    }
}
