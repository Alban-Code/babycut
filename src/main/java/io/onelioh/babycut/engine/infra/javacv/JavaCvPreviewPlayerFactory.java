package io.onelioh.babycut.engine.infra.javacv;

import io.onelioh.babycut.engine.player.PreviewPlayer;
import io.onelioh.babycut.engine.player.PreviewPlayerFactory;
import io.onelioh.babycut.engine.infra.java.AudioPlayer;
import io.onelioh.babycut.model.media.MediaAsset;
import io.onelioh.babycut.model.timeline.Timeline;
import io.onelioh.babycut.ui.utils.converter.VideoFrameToFxImageConverter;

public class JavaCvPreviewPlayerFactory implements PreviewPlayerFactory {
    @Override
    public PreviewPlayer createForAsset(MediaAsset asset) {
        JavaCvVideoDecoder decoder = new JavaCvVideoDecoder(asset.getPath().toString());
        decoder.openMedia();
        VideoFrameToFxImageConverter converter = new VideoFrameToFxImageConverter();
        AudioPlayer audioPlayer = new AudioPlayer();

        return new JavaCvPreviewPlayer(decoder, converter, audioPlayer);
    }

    @Override
    public PreviewPlayer createForTimeline(Timeline timeline) {
        // TODO
        return null;
    }

    @Override
    public void dispose(PreviewPlayer player) {
        if (player == null) return;

        player.stop();
    }
}
