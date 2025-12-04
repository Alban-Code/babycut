package io.onelioh.babycut.engine.player;

import io.onelioh.babycut.model.media.MediaAsset;

public interface AssetPlayback extends Playback {
    void load(MediaAsset asset);

    MediaAsset getCurrentAsset();
}
