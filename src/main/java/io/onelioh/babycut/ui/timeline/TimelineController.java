package io.onelioh.babycut.ui.timeline;

import io.onelioh.babycut.model.timeline.ClipItem;
import io.onelioh.babycut.model.timeline.TimelineItem;
import io.onelioh.babycut.model.timeline.TrackType;
import io.onelioh.babycut.viewmodel.TimelineViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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
    private Pane timelineRoot;
    @FXML
    private ScrollPane timelineScroll;
    @FXML
    private VBox trackLabelsPane;

    private Consumer<Long> onSeekRequested;

    private Line playhead;

    public TimelineController(TimelineViewModel timelineVM) {
        this.timelineVM = timelineVM;
    }

    @FXML
    private void initialize() {
        this.timelineVM.timelineProperty().addListener((obs, oldTimeline, newTimeline) -> {
            System.out.println("listener sur la timeline property" + obs);
            rebuildUI();
        });

        this.timelineVM.pixelsPerSecondProperty().addListener((obs, oldVal, newVal) -> {
            rebuildUI();
        });

        this.timelineVM.needsRefreshProperty().addListener((obs, oldValue, newValue) -> {
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
            trackLabelsPane.getChildren().clear();
            return;
        }

        rebuildTrackLabels();

        double width = this.timelineVM.getMaxTimelineDuration() * this.timelineVM.getPixelsPerSecond() / 1000;

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
        timelineRoot.setMinWidth(width);

        // Lier la hauteur du Pane à celle du VBox pour que le ScrollPane détecte le contenu
        timelineRoot.prefHeightProperty().bind(tracksBox.heightProperty());
        timelineRoot.minHeightProperty().bind(tracksBox.heightProperty());

        // Forcer le ScrollPane à recalculer son contenu
        timelineScroll.setHvalue(0); // Reset la position horizontale
        timelineScroll.setPannable(true); // Activer le pan avec la souris
        timelineScroll.layout(); // Forcer le recalcul du layout

        // DEBUG: voir les dimensions réelles
        System.out.println("=== DEBUG TIMELINE ===");
        System.out.println("Largeur calculée: " + width);
        System.out.println("TracksBox prefWidth: " + tracksBox.getPrefWidth());
        System.out.println("TimelineRoot prefWidth: " + timelineRoot.getPrefWidth());
        System.out.println("TimelineRoot minWidth: " + timelineRoot.getMinWidth());
        System.out.println("ScrollPane width: " + timelineScroll.getWidth());
        System.out.println("ScrollPane viewport width: " + timelineScroll.getViewportBounds().getWidth());

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

        timelineRoot.getChildren().add(playhead);
    }

    private Pane buildTrackPane(TimelineViewModel.TrackViewModel track, double width) {
        Pane trackPane = new Pane();
        trackPane.setPrefHeight(TRACK_HEIGHT);
        trackPane.setMinHeight(TRACK_HEIGHT);
        trackPane.setPrefWidth(width);
        trackPane.setMinWidth(width);

        System.out.println("TRACK " + track.getType() + ": width=" + width + "px");

        // Fond transparent avec une bordure discrète
        trackPane.setStyle("-fx-background-color: transparent; -fx-border-color: #374151;");

        for (TimelineItem item : track.getItems()) {
            if (item instanceof ClipItem clip) {
                StackPane clipNode = buildClipNode(track, clip);
                trackPane.getChildren().add(clipNode);
            }
        }

        return trackPane;
    }

    private StackPane buildClipNode(TimelineViewModel.TrackViewModel track, ClipItem clip) {
        double x = clip.getStartTime() / 1000.0 * this.timelineVM.getPixelsPerSecond();
        double w = clip.getDurationMilliseconds() / 1000.0 * this.timelineVM.getPixelsPerSecond();

        // Ajouter un petit gap pour séparer visuellement les clips
        double gap = 2.0;
        w = Math.max(w - gap, 1);

        System.out.println("CLIP: startTime=" + clip.getStartTime() + "ms, duration=" + clip.getDurationMilliseconds() + "ms → x=" + x + "px, w=" + w + "px");

        StackPane node = new StackPane();
        node.setLayoutX(x);
        node.setLayoutY(2); // Moins d'espace en haut
        node.setPrefHeight(TRACK_HEIGHT - 4); // Hauteur presque complète (moins 4px pour padding)
        node.setPrefWidth(w);
        node.setFocusTraversable(false); // Désactiver le focus bleu

        String baseStyle = track.getType() == TrackType.VIDEO
                ? "-fx-background-color: #3b82f6; -fx-border-color: #1e40af; -fx-border-width: 2;"
                : "-fx-background-color: #22c55e; -fx-border-color: #15803d; -fx-border-width: 2;";

        // Style de sélection (bordure jaune + glow)
        String selectedStyle = track.getType() == TrackType.VIDEO ?
                "-fx-background-color: #3b82f6; -fx-border-color: #fbbf24; -fx-border-width: 3; -fx-effect: dropshadow(gaussian, #fbbf24, 10, 0.6, 0, 0);" :
                "-fx-background-color: #22c55e; -fx-border-color: #fbbf24; -fx-border-width: 3; -fx-effect: dropshadow(gaussian, #fbbf24, 10, 0.6, 0, 0);";

        boolean isSelected = timelineVM.getSelected() == clip;
        String style = isSelected ? selectedStyle : baseStyle;
        node.setStyle(style + "-fx-border-radius: 4; -fx-background-radius: 4;");

        String labelText = clip.getAsset().getPath().getFileName().toString();
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 11;");
        node.getChildren().add(label);

        node.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            e.consume();
            this.timelineVM.selectClip(clip);
        });

        final double[] dragContext = new double[2];

        node.setOnMousePressed(event -> {
//            timelineVM.selectClip(clip);
            dragContext[0] = event.getSceneX();
            dragContext[1] = node.getLayoutX();
            node.setCursor(Cursor.MOVE);
            event.consume();
        });

        node.setOnMouseDragged(event -> {
            double offsetX = event.getSceneX() - dragContext[0];
            double newX = dragContext[1] + offsetX;
            if (newX < 0) {
                newX = 0;
            }
            node.setLayoutX(newX);

            long potentialStartTime = Math.round(newX / timelineVM.getPixelsPerSecond() * 1000);

            if (hasCollision(clip, potentialStartTime, track)) {
                // Collision → bordure rouge
                node.setStyle("-fx-background-color: #ef4444; -fx-border-color: #991b1b; -fx-border-width: 3; -fx-border-radius: 4; -fx-background-radius: 4;");
            } else {
                // Pas de collision → couleur normale (selon le type de track)
                String dragStyle = track.getType() == TrackType.VIDEO
                        ? "-fx-background-color: #60a5fa; -fx-border-color: #2563eb; -fx-border-width: 2;"
                        : "-fx-background-color: #4ade80; -fx-border-color: #16a34a; -fx-border-width: 2;";

                node.setStyle(dragStyle + "-fx-border-radius: 4; -fx-background-radius: 4;");
            }

            event.consume();
        });

        node.setOnMouseReleased(event -> {
            double finalX = node.getLayoutX();

            long newStartTime = Math.round(finalX / timelineVM.getPixelsPerSecond() * 1000);

            if (newStartTime < 0) {
                newStartTime = 0;
            }

            if (hasCollision(clip, newStartTime, track)) {
                System.out.println("Collision détectée, annuler le drag");

                timelineVM.markDirty();

                node.setCursor(Cursor.HAND);
                event.consume();
                return;
            }

            clip.setStartTime(newStartTime);

            // Recalculer le timelineEnd (au cas où le clip est maintenant le plus à droite)
            long newEnd = timelineVM.getTimeline().getTimelineEnd();
            timelineVM.setTimelineEnd(newEnd);

            // Forcer le refresh UI (rebuild complet)
            timelineVM.markDirty();

            // Restaurer le curseur par défaut
            node.setCursor(Cursor.HAND);

            event.consume();
        });

        node.setCursor(Cursor.HAND);

        return node;
    }

    private void handleClick(double x) {
        // Convertir en millisecondes
        long sec = Math.round(x / this.timelineVM.getPixelsPerSecond()) * 1000L;
        if (sec < 0) sec = 0;
        if (sec > this.timelineVM.getTimelineEnd()) sec = this.timelineVM.getTimelineEnd();
        if (onSeekRequested != null) onSeekRequested.accept(sec);
        // updatePlayheadUi(sec);
    }

    private void rebuildTrackLabels() {
        trackLabelsPane.getChildren().clear();

        for (var trackVM: this.timelineVM.getTracks()) {
            Label trackLabel = buildTrackLabel(trackVM);
            trackLabelsPane.getChildren().add(trackLabel);
        }
    }

    private Label buildTrackLabel(TimelineViewModel.TrackViewModel trackVM) {
        // Icône + texte selon le type
        String icon = trackVM.getType() == TrackType.VIDEO ? "🎬" : "🔊";
        String text = trackVM.getType() == TrackType.VIDEO ? " Video" : " Audio";
        String labelText = icon + text;

        Label label = new Label(labelText);
        label.setPrefHeight(TRACK_HEIGHT);
        label.setMinHeight(TRACK_HEIGHT);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER_LEFT);
        label.setStyle("-fx-padding: 0 15; -fx-text-fill: #e2e8f0; -fx-font-size: 14; -fx-font-weight: bold;");

        // Couleur de fond selon le type (même couleur que la track)
        if (trackVM.getType() == TrackType.VIDEO) {
            label.setStyle(label.getStyle() + "-fx-background-color: #111827;");
        } else {
            label.setStyle(label.getStyle() + "-fx-background-color: #020617;");
        }

        return label;
    }

    private boolean hasCollision(ClipItem clipToMove, long newStartTime, TimelineViewModel.TrackViewModel track) {
        long newEnd = newStartTime + clipToMove.getDurationMilliseconds();

        for (var item : track.getItems()) {
            if (item == clipToMove) {
                continue;
            }

            if (item instanceof ClipItem other) {
                long otherStart = other.getStartTime();
                long otherEnd = other.getEndTime();

                if (newStartTime < otherEnd && newEnd > otherStart) {
                    return true;
                }
            }
        }

        return false;
    }


}
