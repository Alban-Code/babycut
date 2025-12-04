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
import io.onelioh.babycut.ui.player.PlayerController;
import io.onelioh.babycut.viewmodel.PlaybackViewModel;
import javafx.util.Callback;

public class AppFactory implements Callback<Class<?>, Object> {

    private final ProjectContext projectContext = new DefaultProjectContext();
    private final MediaProber mediaProber = new JavaCvMediaProber();

    private final PlaybackViewModel assetVM = new PlaybackViewModel();
    private final PlaybackViewModel timelineVM = new PlaybackViewModel();

    private final PreviewPlayerFactory playerFactory = new JavaCvPreviewPlayerFactory();

    private AssetPlayback assetCoordinator;
    private TimelinePlayback timelineCoordinator;

    private TimelinePlayback getTimelineCoordinator() {
        if (timelineCoordinator == null) {
            timelineCoordinator = new TimelinePlaybackCoordinator(timelineVM, playerFactory, null);
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
            return new AppController(this.projectContext, this.mediaProber, getAssetCoordinator(), assetVM, getTimelineCoordinator(), timelineVM);
        }

        if (param == PlayerController.class) {
            return new PlayerController(getAssetCoordinator(), this.assetVM);
        }

        try {
            return param.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Impossible de créer le controller: " + param.getName(), e);
        }
    }
}
