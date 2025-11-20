package io.onelioh.babycut.media.playback;

import io.onelioh.babycut.infra.javacv.JavaCvVideoDecoder;
import io.onelioh.babycut.model.media.MediaAsset;
import io.onelioh.babycut.ui.player.VideoFrameToFxImageConverter;

public class JavaCvPreviewPlayerFactory implements PreviewPlayerFactory{
    @Override
    public PreviewPlayer createForAsset(MediaAsset asset) {
        JavaCvVideoDecoder decoder = new JavaCvVideoDecoder(asset.getPath().toString());
        decoder.openMedia();
        VideoFrameToFxImageConverter converter = new VideoFrameToFxImageConverter();

        return new PreviewPlayer(decoder, converter);
    }

    @Override
    public void dispose(PreviewPlayer player) {
        if (player == null) return;

        player.close();
    }
}
