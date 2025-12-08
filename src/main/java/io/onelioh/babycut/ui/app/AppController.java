package io.onelioh.babycut.ui.app;

import io.onelioh.babycut.core.ProjectContext;
import io.onelioh.babycut.engine.player.AssetPlayback;
import io.onelioh.babycut.engine.player.TimelinePlayback;
import io.onelioh.babycut.engine.prober.MediaProber;
import io.onelioh.babycut.model.media.AssetType;
import io.onelioh.babycut.model.media.MediaAsset;
import io.onelioh.babycut.model.media.MediaInfo;
import io.onelioh.babycut.ui.assets.AssetBrowserController;
import io.onelioh.babycut.ui.player.PlayerController;
import io.onelioh.babycut.ui.timeline.TimelineController;
import io.onelioh.babycut.ui.toolbar.ToolbarController;
import io.onelioh.babycut.viewmodel.PlaybackViewModel;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.MediaException;
import javafx.stage.FileChooser;

import java.io.File;

public class AppController {


    @FXML
    private BorderPane root;

    @FXML
    private PlayerController playerViewController;
    @FXML
    private TimelineController timelineViewController;
    @FXML
    private ToolbarController toolbarViewController;
    @FXML
    private AssetBrowserController assetsViewController;

    private ProjectContext projectContext;
    private final AssetPlayback assetPlaybackCoordinator;
    private final PlaybackViewModel assetVM;
    private final TimelinePlayback timelinePlaybackCoordinator;
    private final PlaybackViewModel timelinePlaybackVM;

    private File lastDirectory = null;

    private MediaProber fileProbe;

    public AppController(ProjectContext projectContext, MediaProber fileProbe, AssetPlayback assetPlaybackCoordinator, PlaybackViewModel assetVM, TimelinePlayback timelinePlaybackCoordinator, PlaybackViewModel timelineVM) {
        this.projectContext = projectContext;
        this.fileProbe = fileProbe;
        this.assetPlaybackCoordinator = assetPlaybackCoordinator;
        this.assetVM = assetVM;
        this.timelinePlaybackCoordinator = timelinePlaybackCoordinator;
        this.timelinePlaybackVM = timelineVM;
    }



    @FXML
    private void initialize() {
        projectContext.createProject();
        toolbarViewController.setAppController(this);

        assetsViewController.setOnSimpleClicked(assetPlaybackCoordinator::load);
    }

    public void handleNewProject() {
        // TODO: reset Project + Timeline + UI

    }

    public void handleImportMedia() {
        openVideo();
    }

    private void openVideo() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choisir une vidéo");
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Vidéos", "*.mp4", "*.m4v", "*.mov", "*.mkv", "*.avi", "*.*"));

        if (lastDirectory != null && lastDirectory.exists()) {
            fc.setInitialDirectory(lastDirectory);
        }

        File file = fc.showOpenDialog(root.getScene().getWindow());

        if (file != null) {
            lastDirectory = file.getParentFile();
            loadMedia(file);
        }
    }

    private void loadMedia(File file) {
        try {

            Task<MediaInfo> ffprobeTask = new Task<>() {
                @Override
                protected MediaInfo call() throws Exception {
                    return fileProbe.analyze(file);
                }
            };

            ffprobeTask.setOnSucceeded(e -> {
                // une fois le traitement par ffprobe fini
                MediaInfo info = ffprobeTask.getValue();

                onMediaAnalyzed(file, info);
            });

            ffprobeTask.setOnFailed(e -> {
                ffprobeTask.getException().printStackTrace();
            });

            Thread t = new Thread(ffprobeTask);
            t.setDaemon(true);
            t.start();


        } catch (MediaException e) {
            throw new RuntimeException(e);
        }
    }

    private void onMediaAnalyzed(File file, MediaInfo mediaInfo) {
        AssetType type = mediaInfo.getAudioStreams().isEmpty() ? AssetType.AUDIO : AssetType.VIDEO;
        MediaAsset asset = new MediaAsset(file.toPath(), type, mediaInfo);

        assetPlaybackCoordinator.load(asset);

        projectContext.addMediaAsset(asset);
    }
}
