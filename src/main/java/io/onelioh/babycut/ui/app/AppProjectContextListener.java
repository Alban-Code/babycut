package io.onelioh.babycut.ui.app;

import io.onelioh.babycut.core.ProjectContextListener;
import io.onelioh.babycut.model.media.MediaAsset;
import io.onelioh.babycut.model.project.Project;
import io.onelioh.babycut.model.timeline.Timeline;
import io.onelioh.babycut.ui.assets.AssetBrowserController;
import io.onelioh.babycut.ui.player.PlayerController;
import io.onelioh.babycut.ui.timeline.TimelineController;

import static java.util.Objects.requireNonNull;

public class AppProjectContextListener implements ProjectContextListener {

    private final AssetBrowserController assetBrowserController;
    private final TimelineController timelineController;
    private final PlayerController playerController;


    public AppProjectContextListener(AssetBrowserController assetBrowserController, TimelineController timelineController, PlayerController playerController) {
        this.assetBrowserController = requireNonNull(assetBrowserController);
        this.timelineController = requireNonNull(timelineController);
        this.playerController = requireNonNull(playerController);

    }


    @Override
    public void onProjectChanged(Project project) {

    }

    @Override
    public void onMediaAssetAdded(MediaAsset asset) {
        double totalSec = asset.getMediaInfo().getDurationSeconds();
        assetBrowserController.addAsset(asset);
        playerController.setDuration(totalSec);
        playerController.enableControls();
    }

    @Override
    public void onTimelineActivated(Timeline timeline) {
        timelineController.setTimeline(timeline);
    }

    @Override
    public void onDrawTimeline(Timeline timeline) {
        timelineController.setTimeline(timeline);
    }
}
