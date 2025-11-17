package io.onelioh.babycut.controller;

import io.onelioh.babycut.model.media.MediaInfo;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class StreamsController {
    @FXML private ListView<String> videoStreams;
    @FXML private ListView<String> audioStreams;

    public void setStreams(MediaInfo info) {
        videoStreams.getItems().setAll(info.getVideoLabels());
        audioStreams.getItems().setAll(info.getAudioLabels());
    }
}
