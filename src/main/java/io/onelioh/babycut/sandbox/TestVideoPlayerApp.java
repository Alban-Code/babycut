package io.onelioh.babycut.sandbox;

import io.onelioh.babycut.core.JavaCvVideoDecoder;
import io.onelioh.babycut.core.VideoFrame;
import io.onelioh.babycut.core.VideoFrameToFxImageConverter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TestVideoPlayerApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(800); // par ex

        Pane root = new StackPane(imageView);

        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setTitle("Test Video Player");
        primaryStage.show();


        JavaCvVideoDecoder decoder = new JavaCvVideoDecoder("D:\\replays LoL\\Juillet.mp4");
        decoder.openMedia();

        VideoFrameToFxImageConverter converter = new VideoFrameToFxImageConverter();

        Thread playbackThread = new Thread(() -> {
            while(true) {
                VideoFrame newFrame = decoder.readNextFrame();
                if (newFrame == null) break;
                WritableImage imageFx = converter.toImage(newFrame);
                if (imageFx == null) {
                    continue;
                } else {
                    Platform.runLater(() -> imageView.setImage(imageFx));
                }
                try {
                    Thread.sleep(15);
                } catch (InterruptedException ignored) {
                }
            }
            decoder.close();
        });
        playbackThread.setDaemon(true);
        playbackThread.start();

    }
}
