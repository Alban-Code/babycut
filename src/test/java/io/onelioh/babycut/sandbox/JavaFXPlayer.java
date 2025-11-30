package io.onelioh.babycut.sandbox;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.JavaFXFrameConverter;

import javax.sound.sampled.*;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Lecteur Vidéo JavaFX avec JavaCV.
 * Architecture : 3 Threads (Demuxer, Audio, Video).
 * Sync : Audio Master Clock.
 */
public class JavaFXPlayer extends Application {

    // --- UI Elements ---
    private ImageView imageView;
    private Stage primaryStage;

    // --- Controle du flux ---
    private final AtomicBoolean isPlaying = new AtomicBoolean(false);
    private Thread producerThread;
    private Thread audioThread;
    private Thread videoThread;

    // --- Queues (Buffers) ---
    // On limite la taille pour éviter de saturer la RAM
    private final BlockingQueue<Frame> videoQueue = new LinkedBlockingQueue<>(50);
    private final BlockingQueue<Frame> audioQueue = new LinkedBlockingQueue<>(100);
    private static final Frame POISON_PILL = new Frame(); // Marqueur de fin

    // --- Audio Master State ---
    // Permet au thread vidéo de savoir où en est l'audio
    private volatile long currentAudioTimestamp = 0;
    private SourceDataLine soundLine; // La ligne audio matérielle

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        BorderPane root = new BorderPane();

        // Zone d'affichage vidéo (noir par défaut)
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(stage.widthProperty());
        imageView.fitHeightProperty().bind(stage.heightProperty().subtract(50)); // Place pour les boutons

        StackPane videoPane = new StackPane(imageView);
        videoPane.setStyle("-fx-background-color: black;");
        root.setCenter(videoPane);

        // Bouton pour charger un fichier
        Button loadBtn = new Button("Ouvrir Fichier Vidéo");
        loadBtn.setOnAction(e -> chooseFile());

        root.setBottom(loadBtn);

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("JavaCV Player - Audio Master Sync");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> stopPlayback()); // Arrêt propre
        stage.show();
    }

    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi", "*.mkv"));
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            stopPlayback(); // Arrêter la lecture précédente si elle existe
            startPlayback(file.getAbsolutePath());
        }
    }

    private void startPlayback(String filePath) {
        isPlaying.set(true);
        videoQueue.clear();
        audioQueue.clear();

        // 1. Thread Producteur (Demuxer)
        producerThread = new Thread(() -> demux(filePath), "Demuxer-Thread");

        // 2. Thread Audio (Master Clock)
        audioThread = new Thread(this::playAudio, "Audio-Thread");

        // 3. Thread Vidéo (Slave)
        videoThread = new Thread(this::playVideo, "Video-Thread");

        producerThread.start();
        audioThread.start();
        videoThread.start();
    }

    private void stopPlayback() {
        isPlaying.set(false);
        // On insère les pilules pour débloquer les threads en attente sur queue.take()
        videoQueue.offer(POISON_PILL);
        audioQueue.offer(POISON_PILL);

        // Fermeture de la ligne audio si ouverte
        if (soundLine != null && soundLine.isOpen()) {
            soundLine.stop();
            soundLine.close();
        }
    }

    // =========================================================
    // THREAD 1 : DEMUXER (Producteur)
    // =========================================================
    private void demux(String filePath) {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(filePath)) {
            grabber.start();
            System.out.println("[Demuxer] Vidéo chargée : " + filePath);

            while (isPlaying.get()) {
                Frame frame = grabber.grab();
                if (frame == null) break; // Fin du fichier

                // CLONE CRITIQUE : JavaCV réutilise l'objet Frame.
                // Sans clone, les données sont corrompues avant affichage.
                Frame cloned = frame.clone();

                if (frame.image != null) {
                    videoQueue.put(cloned); // Bloque si plein
                } else if (frame.samples != null) {
                    audioQueue.put(cloned); // Bloque si plein
                }
            }

            // Fin du flux
            videoQueue.put(POISON_PILL);
            audioQueue.put(POISON_PILL);
            System.out.println("[Demuxer] Fin du fichier atteinte.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // THREAD 2 : AUDIO PLAYER (Master Clock)
    // =========================================================
    private void playAudio() {
        try {
            // Attente du premier paquet pour configurer la ligne audio
            Frame firstFrame = audioQueue.take();
            if (firstFrame == POISON_PILL) return;

            // Configuration Audio Java Sound
            AudioFormat audioFormat = new AudioFormat(
                    firstFrame.sampleRate, 16, firstFrame.audioChannels, true, true);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            soundLine = (SourceDataLine) AudioSystem.getLine(info);
            soundLine.open(audioFormat);
            soundLine.start();

            // Remettre la frame dans la queue pour ne pas la perdre
            // (ou la traiter directement, ici on simplifie en re-queuant pour la boucle)
            // Note: pour un code de prod, traitez-là ici pour éviter l'ordre incorrect,
            // mais ici on suppose que c'est la seule frame dispo.
            processAudioFrame(firstFrame);

            while (isPlaying.get()) {
                Frame frame = audioQueue.take();
                if (frame == POISON_PILL) break;

                processAudioFrame(frame);

                // MISE A JOUR HORLOGE MAITRE
                // getMicrosecondPosition() retourne le temps exact écoulé sur le matériel audio
                currentAudioTimestamp = soundLine.getMicrosecondPosition();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processAudioFrame(Frame frame) throws LineUnavailableException {
        if (frame.samples != null) {
            ShortBuffer sb = (ShortBuffer) frame.samples[0];
            sb.rewind();
            ByteBuffer outBuffer = ByteBuffer.allocate(sb.capacity() * 2);
            while (sb.hasRemaining()) {
                outBuffer.putShort(sb.get());
            }
            byte[] data = outBuffer.array();
            // BLOQUANT : C'est ici que la vitesse est régulée physiquement
            soundLine.write(data, 0, data.length);
        }
        frame.close(); // Libération mémoire native
    }

    // =========================================================
    // THREAD 3 : VIDEO RENDERER (Slave Sync)
    // =========================================================
    private void playVideo() {
        JavaFXFrameConverter converter = new JavaFXFrameConverter();

        try {
            while (isPlaying.get()) {
                Frame frame = videoQueue.take();
                if (frame == POISON_PILL) break;

                // --- SYNC LOGIC (Audio Master) ---
                long videoTimestamp = frame.timestamp; // En microsecondes
                long audioTimestamp = currentAudioTimestamp; // En microsecondes (Hardware)

                long diff = videoTimestamp - audioTimestamp;

                if (diff > 20000) {
                    // Si la vidéo est en avance de > 20ms, on attend.
                    try {
                        Thread.sleep(diff / 1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                } else if (diff < -50000) {
                    // Si la vidéo est en retard de > 50ms, on drop
                    frame.close(); // Ici on peut fermer tout de suite car on n'affiche pas
                    continue;
                }

                // Conversion et Affichage UI
                // On passe la responsabilité de la fermeture au thread UI
                Platform.runLater(() -> {
                    try {
                        // On convertit les données tant qu'elles sont valides
                        Image img = converter.convert(frame);
                        imageView.setImage(img);
                    } finally {
                        // CRITIQUE : On ferme la frame ICI, seulement après que l'image
                        // ait été créée. On libère la mémoire native C++ sur le thread UI.
                        frame.close();
                    }
                });

                // On ne ferme PLUS frame.close() ici, car runLater est asynchrone !
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}