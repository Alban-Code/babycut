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
    private AppProjectContextListener appListener;
    private final AssetPlayback assetPlaybackCoordinator;
    private final PlaybackViewModel assetVM;
    private final TimelinePlayback timelinePlaybackCoordinator;
    private final PlaybackViewModel timelineVM;

    private File lastDirectory = null;

    private MediaProber fileProbe;

    public AppController(ProjectContext projectContext, MediaProber fileProbe, AssetPlayback assetPlaybackCoordinator, PlaybackViewModel assetVM, TimelinePlayback timelinePlaybackCoordinator, PlaybackViewModel timelineVM) {
        this.projectContext = projectContext;
        this.fileProbe = fileProbe;
        this.assetPlaybackCoordinator = assetPlaybackCoordinator;
        this.assetVM = assetVM;
        this.timelinePlaybackCoordinator = timelinePlaybackCoordinator;
        this.timelineVM = timelineVM;
    }



    @FXML
    private void initialize() {

        projectContext.createProject();
        appListener = new AppProjectContextListener(assetsViewController, timelineViewController, playerViewController);
        projectContext.addListener(appListener);

        toolbarViewController.setAppController(this);

        assetsViewController.setOnAddToTimelineRequested(asset -> {
            projectContext.addTimelineItem(asset);
        });


//        timelineViewController.setOnSeekRequested(seconds -> {
//            seekTimeline(seconds);
//        });

        assetsViewController.setOnSimpleClicked(assetPlaybackCoordinator::load);

//        playbackCoordinator.setOnEndOfMedia(() -> {
//            playerViewController.updateTime(0.0, playbackCoordinator.getDurationMilliseconds());
//        });

    }

    public void handleNewProject() {
        // TODO: reset Project + Timeline + UI

    }

    public void handleImportMedia() {
        openVideo();
    }

    public void handlePlayTimeline() {
        if (projectContext.getActiveTimeline() == null) {
            // System.out.println("Pas de timeline active, rien à lire.");
            return;
        }

        // si tu veux repartir du début à chaque fois :
        // currentTimelineSeconds = 0.0;

        // playFromTimeline(currentTimelineSeconds);
    }

    public void handleCutAtPlayhead() {
//        System.out.println("Cut à la position actuelle");
//
//        if (player == null || masterTimeline == null) {
//            return;
//        }
//
//        double seconds = player.getCurrentTime().toSeconds();
//        timelineViewController.cutAt(seconds); // tu l’implémenteras dans TimelineController
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



//    private ClipItem findClipAtTime(Timeline timeline, double timelineSeconds) {
//        if (timeline == null || timeline.getTracks().isEmpty()) return null;
//
//        TimelineTrack videoTrack = timeline.getTracks().getFirst();
//        for (TimelineItem item : videoTrack.getItems()) {
//            if (item instanceof ClipItem clip) {
//                double start = clip.getStartTime();
//                double end = clip.getEndTime();
//                if (timelineSeconds >= start && timelineSeconds < end) {
//                    return clip;
//                }
//            }
//
//        }
//        return null;
//    }

//    private ClipItem findNextClipAfter(Timeline timeline, ClipItem current) {
//        if (timeline == null || timeline.getTracks().isEmpty()) return null;
//
//        TimelineTrack videoTrack = timeline.getTracks().getFirst();
//        for (TimelineItem item : videoTrack.getItems()) {
//            if (item instanceof ClipItem clip) {
//                double start = clip.getStartTime();
//                if (current.getEndTime() == start) {
//                    return clip;
//                }
//            }
//
//        }
//        return null;
//        if (timeline == null || timeline.getTracks().isEmpty() || current == null) return null;
//
//        TimelineTrack videoTrack = timeline.getTracks().getFirst();
//        ClipItem next = null;
//        double after = current.getEndTime();
//
//        for (TimelineItem item : videoTrack.getItems()) {
//            if (item instanceof ClipItem clip) {
//                if (clip.getStartTime() >= after) {
//                    if (next == null || clip.getStartTime() < next.getStartTime()) {
//                        next = clip;
//                    }
//                }
//            }
//        }
//        return next;
//    }

//    private void playFromTimeline(double timelineSeconds) {
//        if (activeTimeline == null) return;
//
//        ClipItem clip = findClipAtTime(activeTimeline, timelineSeconds);
//        if (clip == null) return;
//
//        currentPlayingClip = clip;
//        currentTimelineSeconds = timelineSeconds;
//
//        double localOffset = timelineSeconds - clip.getStartTime();
//        double sourceTime = clip.getSourceIn() + localOffset;
//
//        if (player != null) {
//            try {
//                player.stop();
//            } catch (Exception ignored) {
//            }
//            player = null;
//        }
//
//        Media media = new Media(clip.getAsset().getPath().toUri().toString());
//        player = new MediaPlayer(media);
//        playerViewController.attachPlayer(player);
//
//        player.setOnReady(() -> {
//            player.seek(Duration.seconds(sourceTime));
//            player.play();
//
//            player.currentTimeProperty().addListener((obs, oldT, newT) -> {
//                double tInFile = newT.toSeconds();
//                double tInTimeline = clip.getStartTime() + (tInFile - sourceTime);
//                currentTimelineSeconds = tInTimeline;
//                timelineViewController.updatePlayhead(tInTimeline);
//            });
//        });
//
//        player.setOnEndOfMedia(() -> {
//            ClipItem next = findNextClipAfter(activeTimeline, currentPlayingClip);
//            if (next != null) {
//                playFromTimeline(next.getStartTime());
//            }
//        });
//    }
//
//    private void seekTimeline(double timelineSeconds) {
//        currentTimelineSeconds = timelineSeconds;
//        timelineViewController.updatePlayhead(currentTimelineSeconds);
//
//        if (activeTimeline == null) return;
//
//        ClipItem clip = findClipAtTime(activeTimeline, timelineSeconds);
//
//        if (clip == null) return;
//
//        double localOffset = timelineSeconds - clip.getStartTime();
//        double sourceTime = clip.getSourceIn() + localOffset;
//
//        String wantedUrl = clip.getAsset().getPath().toUri().toString();
//        boolean needsNewPlayer = (player == null);
//
//        if (!needsNewPlayer) {
//            String currentUrl = player.getMedia().getSource();
//            needsNewPlayer = !currentUrl.equals(wantedUrl);
//        }
//
//        if (needsNewPlayer) {
//            if (player != null) {
//                try { player.dispose(); } catch (Exception ignored) {}
//            }
//            Media media = new Media(wantedUrl);
//            player = new MediaPlayer(media);
//            playerViewController.attachPlayer(player);
//
//            player.setOnReady(() -> player.seek(Duration.seconds(sourceTime)));
//        } else {
//            player.seek(Duration.seconds(sourceTime));
//        }
//    }


}
