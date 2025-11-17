package io.onelioh.babycut.controller;

import io.onelioh.babycut.model.media.MediaInfo;
import io.onelioh.babycut.model.timeline.*;
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

import java.util.function.Consumer;

public class TimelineController {

    private static final double PIXELS_PER_SECOND = 80.0;
    private static final double TRACK_HEIGHT = 40.0;
    private static final double CLIP_HEIGHT = 30.0;

    @FXML
    private StackPane timelineRoot;
    @FXML
    private ScrollPane timelineScroll;

    private Timeline timeline;
    private double totalDuration = 0.0;
    private Consumer<Double> onSeekRequested;

    private Line playhead;

    @FXML
    private void initialize() {}

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
        rebuildFromModel();
    }

    public void updatePlayhead(double currentSec) {}

    public void setOnSeekRequested(Consumer<Double> handler) {
        this.onSeekRequested = handler;
    }

    private void rebuildFromModel() {
        timelineRoot.getChildren().clear();

        if (timeline == null || timeline.getTracks().isEmpty()) {
            totalDuration = 0.0;
            return;
        }

        totalDuration = timeline.getTimelineEnd();
        double width = Math.max(totalDuration * PIXELS_PER_SECOND, 400);

        VBox tracksBox = new VBox(2);
        tracksBox.setFillWidth(true);
        tracksBox.setPrefWidth(width);

        for (TimelineTrack track : timeline.getTracks()) {
            Pane trackPane = buildTrackPane(track, width);
            tracksBox.getChildren().add(trackPane);

            trackPane.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                double x = e.getX();
                handleClick(x);
            });
        }

        timelineRoot.getChildren().add(tracksBox);
        timelineRoot.setPrefWidth(width);

        // playhead
        playhead = new Line();
        playhead.setStartY(0);
        playhead.setStroke(Color.RED);
        playhead.setStrokeWidth(2);
        playhead.endYProperty().bind(tracksBox.heightProperty());
        playhead.setManaged(false);

        StackPane.setAlignment(playhead, Pos.TOP_LEFT);
        timelineRoot.getChildren().add(playhead);

        // position initiale
        updatePlayhead(0.0);
    }

    private Pane buildTrackPane(TimelineTrack track, double width) {
        Pane trackPane = new Pane();
        trackPane.setPrefHeight(TRACK_HEIGHT);
        trackPane.setMinHeight(TRACK_HEIGHT);

        if (track.getType() == TrackType.VIDEO) {
            trackPane.setStyle("-fx-background-color: #111827; -fx-border-color: #374151;");
        } else {
            trackPane.setStyle("-fx-background-color: #020617; -fx-border-color: #374151;");
        }

        for (TimelineItem item : track.getItems()) {
            if (item instanceof ClipItem clip) {
                StackPane clipNode = buildClipNode(track, clip);
                trackPane.getChildren().add(clipNode);
            }
        }

        return trackPane;
    }

    private StackPane buildClipNode(TimelineTrack track, ClipItem clip) {
        double x = clip.getStartTime() * PIXELS_PER_SECOND;
        double w = clip.getDuration() * PIXELS_PER_SECOND;

        StackPane node = new StackPane();
        node.setLayoutX(x);
        node.setLayoutY(5);
        node.setPrefHeight(CLIP_HEIGHT);
        node.setPrefWidth(w);

        if (track.getType() == TrackType.VIDEO) {
            node.setStyle("-fx-background-color: #3b82f6; -fx-border-color: #60a5fa;");
        } else {
            node.setStyle("-fx-background-color: #22c55e; -fx-border-color: #4ade80;");
        }

        String labelText = clip.getAsset().getPath().getFileName().toString();
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 11;");
        node.getChildren().add(label);

        // clic sur le clip → seek au début du clip
        node.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            e.consume();
            double seconds = clip.getStartTime();
            if (onSeekRequested != null) {
                onSeekRequested.accept(seconds);
            }
            updatePlayhead(seconds);
        });

        return node;
    }

    private void handleClick(double x) {
        double sec = x / PIXELS_PER_SECOND;
        if (sec < 0) sec = 0;
        if (sec > totalDuration) sec = totalDuration;
        if (onSeekRequested != null) onSeekRequested.accept(sec);
        updatePlayhead(sec);
    }


}
