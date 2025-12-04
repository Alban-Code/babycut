package io.onelioh.babycut.engine.player;

import io.onelioh.babycut.model.media.MediaAsset;
import io.onelioh.babycut.model.timeline.Timeline;

public interface PreviewPlayerFactory {

    PreviewPlayer createForAsset(MediaAsset asset);

    PreviewPlayer createForTimeline(Timeline timeline);

    void dispose(PreviewPlayer player);
}
