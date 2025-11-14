package io.onelioh.controller;

import io.onelioh.service.FfprobeService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;

public class AppController {

    @FXML private Button importBtn;
    @FXML private BorderPane root;

    @FXML private PlayerController playerViewController;
    @FXML private StreamsController streamsViewController;
    @FXML private TimelineController timelineViewController;

    private double timelineDurationSeconds = 0.0;
    private MediaPlayer player;
    private File lastDirectory = null;

    @FXML
    private void initialize() {
        importBtn.setOnAction(e -> openVideo());

        timelineViewController.setOnSeekRequested(( Double seconds) -> {
            if (player != null) {
                player.seek(Duration.seconds(seconds));
            }
        });
    }

    private void openVideo() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choisir une vidéo");
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Vidéos", "*.mp4", "*.m4v", "*.mov", "*.mkv", "*.avi", "*.*"));

        if (lastDirectory != null && lastDirectory.exists()) {
            fc.setInitialDirectory(lastDirectory);
        }

        File file = fc.showOpenDialog(root.getScene().getWindow());

        if(file != null) {
            lastDirectory = file.getParentFile();
            loadMedia(file);
        }
    }

    private void loadMedia(File file) {
        if (player != null) {
            try {
                player.dispose();
            } catch (Exception ignored) {}
            player = null;
        }

        try {
            Media media = new Media(file.toURI().toString());
            player = new MediaPlayer(media);

            playerViewController.attachPlayer(player);
            streamsViewController.setStreams(FfprobeService.analyze(file));

            player.setOnReady(() -> {
                double total = player.getTotalDuration().toSeconds();
                timelineDurationSeconds = total;

                playerViewController.onMediaReady(total);

                timelineViewController.buildTimeline(total);

                player.currentTimeProperty().addListener((obs, oldT, newT) -> {
                    timelineViewController.updatePlayhead(newT.toSeconds());

                    player.play(); // lecture auto
                });


            });
        } catch (MediaException e) {
            throw new RuntimeException(e);
        }
    }
}
