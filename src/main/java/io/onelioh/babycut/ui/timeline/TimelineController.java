package io.onelioh.babycut.ui.timeline;

import io.onelioh.babycut.model.timeline.*;
import io.onelioh.babycut.viewmodel.TimelineViewModel;
import javafx.beans.binding.Bindings;
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

    private final TimelineViewModel timelineVM;

    private static final double TRACK_HEIGHT = 40.0;
    private static final double CLIP_HEIGHT = 30.0;

    @FXML
    private StackPane timelineRoot;
    @FXML
    private ScrollPane timelineScroll;

    private Consumer<Long> onSeekRequested;

    private Line playhead;

    public TimelineController(TimelineViewModel timelineVM) {
        this.timelineVM = timelineVM;
    }

    @FXML
    private void initialize() {
        this.timelineVM.timelineProperty().addListener((obs, oldTimeline, newTimeline) -> {
            rebuildUI();
        });

        this.timelineVM.pixelsPerSecondProperty().addListener((obs, oldVal, newVal) -> {
            rebuildUI();
        });
    }

    public void updatePlayheadUi(long currentSec) {
        double x = (currentSec / 1000.0) * this.timelineVM.getPixelsPerSecond();

        if (playhead != null) {
            // La ligne reste verticale de x à x, on la décale juste
            playhead.setStartX(0);
            playhead.setEndX(0);
            playhead.setLayoutX(x);
        }
    }

    public void setOnSeekRequested(Consumer<Long> handler) {
        this.onSeekRequested = handler;
    }

    private void rebuildUI() {
        timelineRoot.getChildren().clear();

        if (this.timelineVM.getTimeline() == null || this.timelineVM.getTracks().isEmpty()) {
            return;
        }

        double width = Math.max(this.timelineVM.getTimelineEnd() * this.timelineVM.getPixelsPerSecond(), 400);

        VBox tracksBox = new VBox(2);
        tracksBox.setFillWidth(true);
        tracksBox.setPrefWidth(width);

        for (var track : this.timelineVM.getTracks()) {
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
        playhead.setStartX(0);
        playhead.setEndX(0);
        playhead.setStartY(0);
        playhead.setStroke(Color.RED);
        playhead.setStrokeWidth(2);
        playhead.endYProperty().bind(tracksBox.heightProperty());
        playhead.setManaged(false);

        playhead.layoutXProperty().bind(
                Bindings.createDoubleBinding(
                        () -> this.timelineVM.getPlayheadPosition() / 1000.0 * this.timelineVM.getPixelsPerSecond(),
                        this.timelineVM.playheadPositionProperty(),
                        this.timelineVM.pixelsPerSecondProperty()
                )
        );

        StackPane.setAlignment(playhead, Pos.TOP_LEFT);
        timelineRoot.getChildren().add(playhead);
    }

    private Pane buildTrackPane(TimelineViewModel.TrackViewModel track, double width) {
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

    private StackPane buildClipNode(TimelineViewModel.TrackViewModel track, ClipItem clip) {
        double x = clip.getStartTime() * this.timelineVM.getPixelsPerSecond();
        double w = clip.getDurationMilliseconds() * this.timelineVM.getPixelsPerSecond();

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

            this.timelineVM.selectClip(clip);
            long seconds = clip.getStartTime();
            if (onSeekRequested != null) {
                onSeekRequested.accept(seconds);
            }
            updatePlayheadUi(seconds);
        });

        return node;
    }

    private void handleClick(double x) {
        // Convertir en millisecondes
        long sec = Math.round(x / this.timelineVM.getPixelsPerSecond()) * 1000L;
        if (sec < 0) sec = 0;
        if (sec > this.timelineVM.getTimelineEnd()) sec = this.timelineVM.getTimelineEnd();
        if (onSeekRequested != null) onSeekRequested.accept(sec);
        updatePlayheadUi(sec);
    }


}
