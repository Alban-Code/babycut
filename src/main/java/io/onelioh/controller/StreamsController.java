package io.onelioh.controller;

import io.onelioh.model.MediaInfo;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class StreamsController {
    @FXML private ListView<String> videoStreams;
    @FXML private ListView<String> audioStreams;

    public void setStreams(MediaInfo info) {
        videoStreams.getItems().setAll(info.getVideoLabels());
        audioStreams.getItems().setAll(info.getAudioLables());
    }
}
