package io.onelioh.babycut.ui.assets;

import io.onelioh.babycut.core.ProjectContext;
import io.onelioh.babycut.model.media.MediaAsset;
import io.onelioh.babycut.viewmodel.ProjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.util.List;
import java.util.function.Consumer;

public class AssetBrowserController {

    private final ProjectViewModel projectVM;
    private final ProjectContext projectContext;

    @FXML
    private ListView<MediaAsset> assetsListView;

    @FXML
    private StreamsController streamsViewController;

    // Callback définie dans l'orchestrateur (AppController)
    private Consumer<MediaAsset> onSimpleClicked;

    public AssetBrowserController(ProjectViewModel projectVM, ProjectContext projectContext) {
        this.projectVM = projectVM;
        this.projectContext = projectContext;
    }

    @FXML
    private void initialize() {
        assetsListView.setItems(projectVM.getAssets());

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
                if (onSimpleClicked != null) {
                    onSimpleClicked.accept(asset);
                }
            }
        });

        assetsListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                MediaAsset asset = assetsListView.getSelectionModel().getSelectedItem();
                if (asset != null) {
                    projectContext.addTimelineItem(asset);
                }
            }
        });
    }

    public void setOnSimpleClicked(Consumer<MediaAsset> cb) {
        onSimpleClicked = cb;
    }

    @FXML
    private void onAddToTimelineClicked() {
        MediaAsset asset = assetsListView.getSelectionModel().getSelectedItem();
        if (asset != null) {
            projectContext.addTimelineItem(asset);
        }
    }
}
