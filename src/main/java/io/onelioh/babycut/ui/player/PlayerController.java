package io.onelioh.babycut.ui.player;

import io.onelioh.babycut.media.playback.PreviewPlayer;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.util.function.Consumer;

public class PlayerController {

    @FXML
    private Button playBtn, pauseBtn, stopBtn;
    @FXML
    private Label timeLabel;
    @FXML
    private Slider timeSlider;
    @FXML
    private ImageView videoImageView;
    @FXML
    private StackPane centerPane;

    private Runnable onPlayRequested;
    private Runnable onPauseRequested;
    private Runnable onStopRequested;
    private Consumer<Double> onSeekRequested;

    @FXML
    private void initialize() {
//        setControlsEnabled(false);

        videoImageView.setPreserveRatio(true);
        videoImageView.setSmooth(true);

        videoImageView.fitWidthProperty().bind(centerPane.widthProperty());
        videoImageView.fitHeightProperty().bind(centerPane.heightProperty());

        StackPane.setAlignment(videoImageView, Pos.CENTER);

        playBtn.setOnAction(e -> {
            if (onPlayRequested != null) onPlayRequested.run();
        });
        pauseBtn.setOnAction(e -> {
            if (onPauseRequested != null) onPauseRequested.run();
        });
        stopBtn.setOnAction(e -> {
            if (onStopRequested != null) onStopRequested.run();
        });


        // handlers slider
        timeSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (onSeekRequested != null && !isChanging) {
                System.out.println("Slider, " + timeSlider.getValue());
                onSeekRequested.accept(timeSlider.getValue());
            }
        });
        // Seek si clic direct sur la barre (drag terminé)
        timeSlider.setOnMouseReleased(e -> {
            if (onSeekRequested != null) {
                onSeekRequested.accept(timeSlider.getValue());
            }

        });

    }

    public void setDuration(double totalSec) {
        timeSlider.setDisable(false);
        timeSlider.setMin(0);
        timeSlider.setMax(totalSec);
        timeSlider.setValue(0);
        timeLabel.setText("00:00 / " + formatTime(totalSec));
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

    public void setImage(Image image) {
        videoImageView.setImage(image);
    }

    public void setOnPlayRequested(Runnable onPlayRequested) {
        this.onPlayRequested = onPlayRequested;
    }

    public void setOnPauseRequested(Runnable onPauseRequested) {
        this.onPauseRequested = onPauseRequested;
    }

    public void setOnStopRequested(Runnable onStopRequested) {
        this.onStopRequested = onStopRequested;
    }

    public void setOnSeekRequested(Consumer<Double> onSeekRequested) {
        this.onSeekRequested = onSeekRequested;
    }


}
