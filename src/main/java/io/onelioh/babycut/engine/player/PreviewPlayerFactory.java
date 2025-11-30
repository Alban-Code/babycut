package io.onelioh.babycut.engine.player;

import io.onelioh.babycut.engine.infra.javacv.JavaCvPreviewPlayer;
import io.onelioh.babycut.model.media.MediaAsset;

public interface PreviewPlayerFactory {

    PreviewPlayer createForAsset(MediaAsset asset);

    void dispose(PreviewPlayer player);
}
