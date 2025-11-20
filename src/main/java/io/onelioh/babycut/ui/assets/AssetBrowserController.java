package io.onelioh.babycut.ui.assets;

import io.onelioh.babycut.model.media.MediaAsset;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.util.List;
import java.util.function.Consumer;

public class AssetBrowserController {

    @FXML
    private ListView<MediaAsset> assetsListView;

    @FXML
    private StreamsController streamsViewController;

    // Callback définie dans l'orchestrateur (AppController)
    private Consumer<MediaAsset> onAddToTimelineRequested;
    private Consumer<MediaAsset> onSimpleClicked;

    @FXML
    private void initialize() {
        assetsListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(MediaAsset item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getPath().getFileName().toString());
                }
            }

        });

        assetsListView.getSelectionModel().selectedItemProperty().addListener((obs, old, asset) -> {
            if (asset != null) {
                streamsViewController.setStreams(asset.getMediaInfo());
                onSimpleClicked.accept(asset);
            }
        });

        assetsListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                MediaAsset asset = assetsListView.getSelectionModel().getSelectedItem();
                if (asset != null && onAddToTimelineRequested != null) {
                    onAddToTimelineRequested.accept(asset);
                }
            }
        });
    }

    public void setAssets(List<MediaAsset> assets) {
        assetsListView.getItems().setAll(assets);
    }

    public void addAsset(MediaAsset asset) {
        assetsListView.getItems().add(asset);
    }

    public void setOnAddToTimelineRequested(Consumer<MediaAsset> onAddToTimelineRequested) {
        this.onAddToTimelineRequested = onAddToTimelineRequested;
    }

    public void setOnSimpleClicked(Consumer<MediaAsset> cb) {
        onSimpleClicked = cb;
    }

    @FXML
    private void onAddToTimelineClicked() {
        MediaAsset asset = assetsListView.getSelectionModel().getSelectedItem();
        if (asset != null && onAddToTimelineRequested != null) {
            onAddToTimelineRequested.accept(asset);
        }
    }
}
