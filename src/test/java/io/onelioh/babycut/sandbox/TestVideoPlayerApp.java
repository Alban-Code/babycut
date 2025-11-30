package io.onelioh.babycut.sandbox;

import io.onelioh.babycut.engine.infra.javacv.JavaCvVideoDecoder;
import io.onelioh.babycut.engine.infra.javacv.JavaCvPreviewPlayer;
import io.onelioh.babycut.engine.infra.java.AudioPlayer;
import io.onelioh.babycut.ui.utils.converter.VideoFrameToFxImageConverter;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
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



        JavaCvVideoDecoder decoder = new JavaCvVideoDecoder("D:\\replays LoL\\Daniel.mp4");
        decoder.openMedia();
        decoder.start(); // On préchauffe le four MAINTENANT !
        VideoFrameToFxImageConverter converter = new VideoFrameToFxImageConverter();
        AudioPlayer audioPlayer = new AudioPlayer();

        JavaCvPreviewPlayer player = new JavaCvPreviewPlayer(decoder, converter, audioPlayer);
        player.setOnFrameReady(imageView::setImage);
        player.play();
    }
}