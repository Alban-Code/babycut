package io.onelioh.controller;

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

    public void buildTimeline(double durationSeconds) {
        this.duration = durationSeconds;
        timelineRoot.getChildren().clear();

        double width = Math.max(400, durationSeconds * PIXELS_PER_SECOND);

        VBox tracksBox = new VBox(2);
        tracksBox.setFillWidth(true);

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

        Label videoLabel = new Label("Vidéo 1");
        videoLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11;");
        videoClip.getChildren().add(videoLabel);

        videoTrackPane.getChildren().add(videoClip);

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

        Label audioLabel = new Label("Audio 1");
        audioLabel.setStyle("-fx-text-fill: #022c22; -fx-font-size: 11;");
        audioClip.getChildren().add(audioLabel);


        audioTrackPane.getChildren().add(audioClip);

        tracksBox.getChildren().addAll(videoTrackPane, audioTrackPane);
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

        var seekHandler = (EventHandler<MouseEvent>) e -> {
            double x = e.getX();
            handleClick(x);
        };
        videoTrackPane.setOnMouseClicked(seekHandler);
        audioTrackPane.setOnMouseClicked(seekHandler);

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
