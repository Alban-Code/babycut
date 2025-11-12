package io.onelioh.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;

public class AppController {

    private static final String FFPROBE = "ffprobe";

    @FXML private MediaView mediaView;
    @FXML private Button playBtn, pauseBtn, stopBtn, importBtn;
    @FXML private Label timeLabel;
    @FXML private Slider timeSlider;
    @FXML private StackPane centerPane;

    @FXML private ListView<String> videoStreams;
    @FXML private ListView<String> audioStreams;


    private MediaPlayer player;
    private File lastDirectory = null;

    @FXML
    private void initialize() {
        setControlsEnabled(false);

        importBtn.setOnAction(e -> openVideo());

        playBtn.setOnAction(e -> { if (player != null) player.play();});
        pauseBtn.setOnAction(e -> { if (player != null) player.pause();});
        stopBtn.setOnAction(e -> { if (player != null) player.stop();});
        // Seek quand on lâche le slider
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
        mediaView.setPreserveRatio(true);
        // Le MediaView suit la taille du centre
        mediaView.fitWidthProperty().bind(centerPane.widthProperty());
        mediaView.fitHeightProperty().bind(centerPane.heightProperty());
    }

    private void openVideo() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choisir une vidéo");
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Vidéos", "*.mp4", "*.m4v", "*.mov", "*.mkv", "*.avi", "*.*"));

        if (lastDirectory != null && lastDirectory.exists()) {
            fc.setInitialDirectory(lastDirectory);
        }

        File file = fc.showOpenDialog(mediaView.getScene().getWindow());

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
            mediaView.setMediaPlayer(player);
            refreshStreamsWithFfprobe(file);

            player.setOnReady(() -> {
                setControlsEnabled(true);
                double total = player.getTotalDuration().toSeconds();
                timeSlider.setDisable(false);
                timeSlider.setMin(0);
                timeSlider.setMax(total);
                timeSlider.setValue(0);
                updateTimeLabel(0, total);

                // Avancement → slider + label
                player.currentTimeProperty().addListener((obs, oldT, newT) -> {
                    if (!timeSlider.isValueChanging()) {
                        timeSlider.setValue(newT.toSeconds());
                    }
                    updateTimeLabel(newT.toSeconds(), total);
                });

                // Fin de média → remet à zéro
                player.setOnEndOfMedia(() -> {
                    player.stop();
                    timeSlider.setValue(0);
                    updateTimeLabel(0, total);
                });

                player.play(); // lecture auto au chargement
            });

            player.setOnError(() -> {
                System.err.println("Erreur MediaPlayer: " + player.getError());
                setControlsEnabled(false);
            });
        } catch (MediaException e) {
            throw new RuntimeException(e);
        }
    }

    private void setControlsEnabled(boolean enabled) {
        playBtn.setDisable(!enabled);
        pauseBtn.setDisable(!enabled);
        stopBtn.setDisable(!enabled);
        timeSlider.setDisable(!enabled);
    }

    private void updateTimeLabel(double currentSec, double totalSec) {
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

    private void refreshStreamsWithFfprobe(File file) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    FFPROBE,
                    "-v", "error",
                    "-print_format", "json",
                    "-show_streams",
                    file.getAbsolutePath()
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();

            String json = new String(p.getInputStream().readAllBytes());
            int code = p.waitFor();
            if (code != 0) {
                System.err.println("ffprobe exit code " + code);
                return;
            }

            // Parse JSON
            var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            var root = mapper.readTree(json);
            var streams = root.get("streams");
            if (streams == null || !streams.isArray()) {
                videoStreams.getItems().setAll();
                audioStreams.getItems().setAll();
                return;
            }

            var vids = new java.util.ArrayList<String>();
            var auds = new java.util.ArrayList<String>();

            for (var s : streams) {
                String type = s.path("codec_type").asText(); // "video" | "audio" | "subtitle" ...
                String codec = s.path("codec_name").asText();
                String lang  = s.path("tags").path("language").asText("");
                String title = s.path("tags").path("title").asText("");

                if ("video".equals(type)) {
                    int w = s.path("width").asInt(0);
                    int h = s.path("height").asInt(0);
                    String fr = s.path("avg_frame_rate").asText("");
                    String nice = String.format(
                            "%s %dx%d%s%s%s",
                            (codec.isEmpty() ? "video" : codec),
                            w, h,
                            fr.isEmpty() || "0/0".equals(fr) ? "" : " @" + fr,
                            lang.isEmpty() ? "" : " [" + lang + "]",
                            title.isEmpty() ? "" : " — " + title
                    );
                    vids.add(nice);
                } else if ("audio".equals(type)) {
                    int ch = s.path("channels").asInt(0);
                    int sr = s.path("sample_rate").asInt(0);
                    String layout = s.path("channel_layout").asText("");
                    String nice = String.format(
                            "%s %s%s%s%s",
                            (codec.isEmpty() ? "audio" : codec),
                            ch > 0 ? (ch + "ch") : "",
                            sr > 0 ? (" " + sr + "Hz") : "",
                            layout.isEmpty() ? "" : (" (" + layout + ")"),
                            lang.isEmpty() ? "" : " [" + lang + "]"
                    );
                    auds.add(nice.trim());
                }
            }

            // Mets à jour l’UI (sur le thread JavaFX)
            javafx.application.Platform.runLater(() -> {
                videoStreams.getItems().setAll(vids);
                audioStreams.getItems().setAll(auds);
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
