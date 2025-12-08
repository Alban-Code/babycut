package io.onelioh.babycut.ui.toolbar;

import io.onelioh.babycut.ui.app.AppController;
import javafx.fxml.FXML;

public class ToolbarController {

    private AppController appController;

    public void setAppController(AppController newAppController) {
        appController = newAppController;
    }

    @FXML
    private void onNewProjectClicked() {
        if (appController != null) {
            appController.handleNewProject();
        }
    }

    @FXML
    private void onImportMediaClicked() {
        if (appController != null) {
            appController.handleImportMedia();        }
    }

    @FXML
    private void onPlayTimelineClicked() {

    }

    @FXML
    private void onCutClicked() {

    }
}
