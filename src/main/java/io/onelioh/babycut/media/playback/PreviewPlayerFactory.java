package io.onelioh.babycut.media.playback;

import io.onelioh.babycut.model.media.MediaAsset;

public interface PreviewPlayerFactory {

    PreviewPlayer createForAsset(MediaAsset asset);

    void dispose(PreviewPlayer player);
}
