package io.onelioh.babycut.ui.app;

import io.onelioh.babycut.core.DefaultProjectContext;
import io.onelioh.babycut.core.ProjectContext;
import io.onelioh.babycut.infra.ffmpeg.FfprobeService;
import io.onelioh.babycut.media.playback.JavaCvPlaybackCoordinator;
import io.onelioh.babycut.media.playback.PlaybackCoordinator;
import io.onelioh.babycut.media.playback.PreviewPlayer;
import io.onelioh.babycut.model.media.AssetType;
import io.onelioh.babycut.model.media.MediaAsset;
import io.onelioh.babycut.model.media.MediaInfo;
import io.onelioh.babycut.ui.assets.AssetBrowserController;
import io.onelioh.babycut.ui.player.PlayerController;
import io.onelioh.babycut.ui.timeline.TimelineController;
import io.onelioh.babycut.ui.toolbar.ToolbarController;
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

    private PlaybackCoordinator playbackCoordinator;
    private File lastDirectory = null;



    @FXML
    private void initialize() {
        System.out.println("Init de app controller");
        //

        projectContext = new DefaultProjectContext();
        projectContext.createProject();
        appListener = new AppProjectContextListener(assetsViewController, timelineViewController, playerViewController);
        projectContext.addListener(appListener);

        playbackCoordinator = new JavaCvPlaybackCoordinator();

        toolbarViewController.setAppController(this);

        assetsViewController.setOnAddToTimelineRequested(asset -> {
            projectContext.addTimelineItem(asset);
        });


//        timelineViewController.setOnSeekRequested(seconds -> {
//            seekTimeline(seconds);
//        });

        playerViewController.setOnPlayRequested(() -> {
            playbackCoordinator.play();
        });

        playerViewController.setOnPauseRequested(() -> {
            playbackCoordinator.pause();
        });

        playerViewController.setOnStopRequested(() -> {
            playbackCoordinator.stop();
        });

        playerViewController.setOnSeekRequested((Double seconds) -> {
            playbackCoordinator.seek(seconds);
        });

        assetsViewController.setOnSimpleClicked(asset -> {
            playbackCoordinator.load(asset);
        });

    }

    public void handleNewProject() {
        // TODO: reset Project + Timeline + UI

    }

    public void handleImportMedia() {
        openVideo();
    }

    public void handlePlayTimeline() {
        if (projectContext.getActiveTimeline() == null) {
            System.out.println("Pas de timeline active, rien à lire.");
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

        playbackCoordinator.stop();

        try {

            Task<MediaInfo> ffprobeTask = new Task<>() {
                @Override
                protected MediaInfo call() throws Exception {
                    return FfprobeService.analyze(file);
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

        playbackCoordinator.load(asset);

        playbackCoordinator.setOnTimeChanged(currentSec -> {
            playerViewController.updateTime(currentSec, playbackCoordinator.getDurationSeconds());
        });

        playbackCoordinator.setOnEndOfMedia(() -> {
            playerViewController.updateTime(0.0, playbackCoordinator.getDurationSeconds());
        });

        playbackCoordinator.setOnReady(image -> playerViewController.setImage(image));

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
