package io.onelioh.babycut.core;

import javafx.embed.swing.SwingFXUtils;

import javafx.scene.image.WritableImage;
import org.bytedeco.javacv.Java2DFrameConverter;
import java.awt.image.BufferedImage;

public class VideoFrameToFxImageConverter {

    private Java2DFrameConverter converter;
    private WritableImage image;

    public VideoFrameToFxImageConverter() {
        converter = new Java2DFrameConverter();
    }

    public WritableImage toImage(VideoFrame frame) {
        BufferedImage bImage = converter.convert(frame.getRawFrame());
        if (bImage == null) return null;
        int w = bImage.getWidth();
        int h = bImage.getHeight();
        if (image == null || image.getHeight() != h || image.getWidth() != w) {
            image = new WritableImage(w, h);
        }
        SwingFXUtils.toFXImage(bImage, image);
        return image;
    }
}

