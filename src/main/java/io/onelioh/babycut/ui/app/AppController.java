package io.onelioh.babycut.ui.app;

import io.onelioh.babycut.infra.javacv.JavaCvVideoDecoder;
import io.onelioh.babycut.media.playback.PreviewPlayer;
import io.onelioh.babycut.ui.assets.AssetBrowser;
import io.onelioh.babycut.ui.player.PlayerController;
import io.onelioh.babycut.ui.player.VideoFrameToFxImageConverter;
import io.onelioh.babycut.ui.timeline.TimelineController;
import io.onelioh.babycut.ui.toolbar.ToolbarController;
import io.onelioh.babycut.model.media.AssetType;
import io.onelioh.babycut.model.media.MediaAsset;
import io.onelioh.babycut.model.media.MediaInfo;
import io.onelioh.babycut.model.media.MediaStream;
import io.onelioh.babycut.model.project.Project;
import io.onelioh.babycut.model.timeline.*;
import io.onelioh.babycut.infra.ffmpeg.FfprobeService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;

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
    private AssetBrowser assetsViewController;


    private Project project;
    private Timeline activeTimeline;

    private double timelineDurationSeconds = 0.0;
    private PreviewPlayer player;
    private File lastDirectory = null;

    private double currentTimelineSeconds = 0.0;
    private ClipItem currentPlayingClip;

    @FXML
    private void initialize() {
        System.out.println("Init de app controller");
        project = new Project();

        toolbarViewController.setAppController(this);

        assetsViewController.setOnAddToTimelineRequested(asset -> {
            // Quand l'utilisateur veut ajouter un asset à la timeline
            ensureActiveTimelineExists();
            addAssetAsClipsToTimeline(asset, activeTimeline);
            timelineViewController.setTimeline(activeTimeline);
        });


//        timelineViewController.setOnSeekRequested(seconds -> {
//            seekTimeline(seconds);
//        });

        setupPlayer();

    }

    public void handleNewProject() {
        // TODO: reset Project + Timeline + UI

    }

    public void handleImportMedia() {
        openVideo();
    }

    public void handlePlayTimeline() {
        if (activeTimeline == null) {
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
        if (player != null) {
            try {
                player.stop();
            } catch (Exception ignored) {
            }
            player = null;
        }

        try {
            Media media = new Media(file.toURI().toString());

            JavaCvVideoDecoder decoder = new JavaCvVideoDecoder(file.getPath());
            decoder.openMedia();
            VideoFrameToFxImageConverter converter = new VideoFrameToFxImageConverter();

            player = new PreviewPlayer(decoder, converter);
            player.setOnFrameReady((image) -> {
                playerViewController.setImage(image);
            });

            player.start();

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

//            player.setOnReady(() -> {
//                double total = player.getTotalDuration().toSeconds();
//                timelineDurationSeconds = total;
//
//                // juste pour que ton PlayerController puisse afficher la durée / slider
//                playerViewController.onMediaReady(total);
//            });


        } catch (MediaException e) {
            throw new RuntimeException(e);
        }
    }

    private void onMediaAnalyzed(File file, MediaInfo mediaInfo) {
        // création de l'asset avec le fichier importé
        AssetType type = mediaInfo.getAudioStreams().isEmpty() ? AssetType.AUDIO : AssetType.VIDEO;
        MediaAsset asset = new MediaAsset(file.toPath(), type, mediaInfo);

        double totalSec = asset.getMediaInfo().getDurationSeconds();

        // ajout de l'asset au projet
        project.addMediaAsset(asset);

        assetsViewController.setAssets(project.getMediaAssets());

        playerViewController.setDuration(totalSec);
        player.setOnTimeChanged(currentSec -> {
            playerViewController.updateTime(currentSec, totalSec);
        });
        player.setOnEndOfMedia(() -> {
            // comportement type MediaPlayer : curseur à la fin ou retour au début
            // ici, par ex. on reste à la fin :
            playerViewController.updateTime(totalSec, totalSec);
        });
    }

    private void ensureActiveTimelineExists() {
        if (activeTimeline != null) return;

        activeTimeline = new Timeline("Timeline 1");

        TimelineTrack videoTrack = new TimelineTrack(TrackType.VIDEO);
        TimelineTrack audioTrack = new TimelineTrack(TrackType.AUDIO);

        activeTimeline.addTrack(videoTrack);
        activeTimeline.addTrack(audioTrack);

        project.addTimeline(activeTimeline);
    }

    private void addAssetAsClipsToTimeline(MediaAsset asset, Timeline timeline) {
        MediaInfo info = asset.getMediaInfo();
        double duration = info.getDurationSeconds();

        double insertionTime = timeline.getTimelineEnd();

        TimelineTrack videoTrack = timeline.getTracks().getFirst();
        TimelineTrack audioTrack = timeline.getTracks().get(1);

        if (!info.getVideoStreams().isEmpty()) {
            ClipItem videoClip = new ClipItem(insertionTime, 0.0, duration, asset);
            videoTrack.addItem(videoClip);
        }

        MediaStream primaryAudio = getAudioTrack(info);
        if (primaryAudio != null) {
            ClipItem audioClip = new ClipItem(insertionTime, 0.0, duration, asset);
            audioTrack.addItem(audioClip);
        }
    }

    private MediaStream getAudioTrack(MediaInfo info) {
        var audioStreams = info.getAudioStreams();

        if (audioStreams.isEmpty()) return null;

        return audioStreams.getFirst();
    }

    private ClipItem findClipAtTime(Timeline timeline, double timelineSeconds) {
        if (timeline == null || timeline.getTracks().isEmpty()) return null;

        TimelineTrack videoTrack = timeline.getTracks().getFirst();
        for (TimelineItem item : videoTrack.getItems()) {
            if (item instanceof ClipItem clip) {
                double start = clip.getStartTime();
                double end = clip.getEndTime();
                if (timelineSeconds >= start && timelineSeconds < end) {
                    return clip;
                }
            }

        }
        return null;
    }

    private ClipItem findNextClipAfter(Timeline timeline, ClipItem current) {
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
        if (timeline == null || timeline.getTracks().isEmpty() || current == null) return null;

        TimelineTrack videoTrack = timeline.getTracks().getFirst();
        ClipItem next = null;
        double after = current.getEndTime();

        for (TimelineItem item : videoTrack.getItems()) {
            if (item instanceof ClipItem clip) {
                if (clip.getStartTime() >= after) {
                    if (next == null || clip.getStartTime() < next.getStartTime()) {
                        next = clip;
                    }
                }
            }
        }
        return next;
    }

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

    private void setupPlayer() {

        playerViewController.setOnPlayRequested(() -> {
            player.play();
        });

        playerViewController.setOnPauseRequested(() -> {
            player.pause();
        });

        playerViewController.setOnStopRequested(() -> {
            player.stop();
        });

        playerViewController.setOnSeekRequested((Double seconds) -> {
            player.seek(seconds);
        });
    }


}
