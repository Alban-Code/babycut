package io.onelioh.babycut.config;

import io.onelioh.babycut.core.DefaultProjectContext;
import io.onelioh.babycut.core.ProjectContext;
import io.onelioh.babycut.engine.infra.javacv.JavaCvMediaProber;
import io.onelioh.babycut.engine.infra.javacv.JavaCvPreviewPlayerFactory;
import io.onelioh.babycut.engine.player.AssetPlayback;
import io.onelioh.babycut.engine.player.PreviewPlayerFactory;
import io.onelioh.babycut.engine.player.TimelinePlayback;
import io.onelioh.babycut.engine.prober.MediaProber;
import io.onelioh.babycut.service.AssetPlaybackCoordinator;
import io.onelioh.babycut.service.TimelinePlaybackCoordinator;
import io.onelioh.babycut.ui.app.AppController;
import io.onelioh.babycut.ui.assets.AssetBrowserController;
import io.onelioh.babycut.ui.player.PlayerController;
import io.onelioh.babycut.ui.timeline.TimelineController;
import io.onelioh.babycut.viewmodel.PlaybackViewModel;
import io.onelioh.babycut.viewmodel.ProjectViewModel;
import io.onelioh.babycut.viewmodel.TimelineViewModel;
import javafx.util.Callback;

public class AppFactory implements Callback<Class<?>, Object> {

    private final MediaProber mediaProber = new JavaCvMediaProber();

    private final ProjectViewModel projectVM = new ProjectViewModel();
    private final TimelineViewModel timelineVM = new TimelineViewModel();
    private final PlaybackViewModel assetVM = new PlaybackViewModel();
    private final PlaybackViewModel timelinePlaybackVM = new PlaybackViewModel();

    private final ProjectContext projectContext = new DefaultProjectContext(projectVM, timelineVM);
    private final PreviewPlayerFactory playerFactory = new JavaCvPreviewPlayerFactory();

    private AssetPlayback assetCoordinator;
    private TimelinePlayback timelineCoordinator;

    private TimelinePlayback getTimelineCoordinator() {
        if (timelineCoordinator == null) {
            timelineCoordinator = new TimelinePlaybackCoordinator(timelinePlaybackVM, playerFactory, null);
            wireExclusivity();
        }
        return timelineCoordinator;
    }

    private AssetPlayback getAssetCoordinator() {
        if (assetCoordinator == null) {
            assetCoordinator = new AssetPlaybackCoordinator(assetVM, playerFactory, null);
            wireExclusivity();
        }
        return assetCoordinator;
    }

    private void wireExclusivity() {
        if (assetCoordinator != null && timelineCoordinator != null) {
            ((AssetPlaybackCoordinator) assetCoordinator).setPauseOtherPlayer(() -> timelineCoordinator.pause());
            ((TimelinePlaybackCoordinator) timelineCoordinator).setPauseOtherPlayer(() -> assetCoordinator.pause());
        }
    }

    @Override
    public Object call(Class<?> param) {
        if (param == AppController.class) {
            return new AppController(this.projectContext, this.mediaProber, getAssetCoordinator(), assetVM, getTimelineCoordinator(), timelinePlaybackVM);
        }

        if (param == PlayerController.class) {
            return new PlayerController(getAssetCoordinator(), this.assetVM);
        }

        if (param == AssetBrowserController.class) {
            return new AssetBrowserController(this.projectVM, this.projectContext);
        }

        if (param == TimelineController.class) {
            return new TimelineController(this.timelineVM);
        }

        try {
            return param.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Impossible de créer le controller: " + param.getName(), e);
        }
    }
}
