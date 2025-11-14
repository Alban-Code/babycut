package io.onelioh.controller;

import io.onelioh.model.MediaInfo;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TimelineController {

    private static final double PIXELS_PER_SECOND = 80.0;

    @FXML private StackPane timelineRoot;
    @FXML private ScrollPane timelineScroll;

    private double duration;
    private Consumer<Double> onSeekRequested;

    private Pane videoTrackPane;
    private Pane audioTrackPane;
    private Line playhead;

    public void buildTimeline(MediaInfo mediaInfo) {
        this.duration = mediaInfo.getDurationSeconds();
        timelineRoot.getChildren().clear();

        double width = Math.max(400, this.duration * PIXELS_PER_SECOND);

        // Créer autant de pistes sur le logiciel qu'il y en a dans la vidéo importer

        VBox tracksBox = new VBox(2);
        tracksBox.setFillWidth(true);

        var videoStreams = mediaInfo.getVideoStreams();

        var seekHandler = (EventHandler<MouseEvent>) e -> {
            double x = e.getX();
            handleClick(x);
        };

        for (int i = 0; i < videoStreams.size(); i++) {
            videoTrackPane = new Pane();
            videoTrackPane.setPrefHeight(40);
            videoTrackPane.setMinHeight(40);
            videoTrackPane.setStyle("-fx-background-color: #111827; -fx-border-color: #374151");

            StackPane videoClip = new StackPane();
            videoClip.setLayoutX(0);
            videoClip.setLayoutY(5);
            videoClip.setPrefHeight(30);
            videoClip.setPrefWidth(width);
            videoClip.setStyle("-fx-background-color: #3b82f6; -fx-border-color: #60a5fa;");

            Label videoLabel = new Label("Vidéo " + (i+1));
            videoLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11;");
            videoClip.getChildren().add(videoLabel);

            videoTrackPane.getChildren().add(videoClip);
            videoTrackPane.setOnMouseClicked(seekHandler);

            tracksBox.getChildren().add(videoTrackPane);
        }

        var audioStreams = mediaInfo.getAudioStreams();

        for (int i = 0; i < audioStreams.size(); i++) {
            audioTrackPane = new Pane();
            audioTrackPane.setPrefHeight(40);
            audioTrackPane.setMinHeight(40);
            audioTrackPane.setStyle("-fx-background-color: #020617; -fx-border-color: #374151;");

            StackPane audioClip = new StackPane();
            audioClip.setLayoutX(0);
            audioClip.setLayoutY(5);
            audioClip.setPrefHeight(30);
            audioClip.setPrefWidth(width);
            audioClip.setStyle("-fx-background-color: #22c55e; -fx-border-color: #4ade80;");

            Label audioLabel = new Label("Audio " + (i+1));
            audioLabel.setStyle("-fx-text-fill: #022c22; -fx-font-size: 11;");
            audioClip.getChildren().add(audioLabel);


            audioTrackPane.getChildren().add(audioClip);
            audioTrackPane.setOnMouseClicked(seekHandler);
            tracksBox.getChildren().add(audioTrackPane);
        }

        tracksBox.setPrefWidth(width);

        timelineRoot.getChildren().add(tracksBox);
        timelineRoot.setPrefWidth(width);

        playhead = new Line();
        playhead.setStartY(0);
        playhead.setStroke(Color.RED);
        playhead.setStrokeWidth(2);
        playhead.endYProperty().bind(tracksBox.heightProperty());
        playhead.setManaged(false);

        StackPane.setAlignment(playhead, Pos.TOP_LEFT );
        timelineRoot.getChildren().add(playhead);




        // position initiale du playhead
        updatePlayhead(0.0);
    }

    public void updatePlayhead(double currentSec) {
        double x = currentSec * PIXELS_PER_SECOND;

        if (playhead != null) {
            playhead.setStartX(x);
            playhead.setEndX(x);
        }

    }

    public void setOnSeekRequested(Consumer<Double> handler) {
        this.onSeekRequested = handler;
    }

    private void handleClick(double x) {
        double sec = x / PIXELS_PER_SECOND;
        if (sec < 0) sec = 0;
        if (sec > duration) sec = duration;
        if (onSeekRequested != null) onSeekRequested.accept(sec);
    }
}
