package io.onelioh.babycut.ui.player;

import io.onelioh.babycut.engine.player.AssetPlayback;
import io.onelioh.babycut.viewmodel.PlaybackViewModel;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

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

    private final AssetPlayback assetPlaybackCoordinator;
    private final PlaybackViewModel playbackVM;

    // Pour gérer le drag du slider sans conflit avec le binding
    private boolean isUserDragging = false;

    public PlayerController(AssetPlayback assetPlaybackCoordinator, PlaybackViewModel assetVM) {
        this.assetPlaybackCoordinator = assetPlaybackCoordinator;
        this.playbackVM = assetVM;
    }

    @FXML
    private void initialize() {
        setupVideoView();
        setupBindings();
        setupControls();
        setupFrameListeners();
    }

    private void setupVideoView() {
        videoImageView.setPreserveRatio(true);
        videoImageView.setSmooth(true);

        videoImageView.fitWidthProperty().bind(centerPane.widthProperty());
        videoImageView.fitHeightProperty().bind(centerPane.heightProperty());

        StackPane.setAlignment(videoImageView, Pos.CENTER);
    }

    private void setupBindings() {
        // ===================== TIME LABEL =====================
        timeLabel.textProperty().bind(playbackVM.formattedTimeProperty());

        // ===================== TIME SLIDER =====================
        timeSlider.maxProperty().bind(playbackVM.durationProperty());

        playbackVM.currentTimeProperty().addListener((obs, oldVal, newVal) -> {
            if (!isUserDragging) {
                timeSlider.setValue(newVal.longValue());
            }
        });

        // Détecter le début/fin du drag
        timeSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (isChanging) {
                isUserDragging = true;
            } else {
                isUserDragging = false;
                assetPlaybackCoordinator.seek(Math.round(Math.round(timeSlider.getValue())));
            }
        });

        // Seek sur simple clique
        timeSlider.setOnMouseReleased(e -> {
            if (!isUserDragging) {
                assetPlaybackCoordinator.seek(Math.round(timeSlider.getValue()));
            }
        });

         // ===================== BOUTONS ACTIVES/DESACTIVES =====================
         playBtn.disableProperty().bind(playbackVM.readyProperty().not());
         pauseBtn.disableProperty().bind(playbackVM.readyProperty().not());
         stopBtn.disableProperty().bind(playbackVM.readyProperty().not());
         timeSlider.disableProperty().bind(playbackVM.readyProperty().not());

          // ===================== VISIBILITE PLAY/PAUSE =====================
          playBtn.visibleProperty().bind(playbackVM.playingProperty().not());
          pauseBtn.visibleProperty().bind(playbackVM.playingProperty());

          playBtn.managedProperty().bind(playBtn.visibleProperty());
          pauseBtn.managedProperty().bind(pauseBtn.visibleProperty());
    }

    private void setupControls() {
        playBtn.setOnAction(e -> assetPlaybackCoordinator.play());
        pauseBtn.setOnAction(e -> assetPlaybackCoordinator.pause());
        stopBtn.setOnAction(e -> assetPlaybackCoordinator.stop());
    }

    private void setupFrameListeners() {
        assetPlaybackCoordinator.addFrameListener(this::setImage);
    }

    private void setImage(Image image) {
        videoImageView.setImage(image);
    }
}
