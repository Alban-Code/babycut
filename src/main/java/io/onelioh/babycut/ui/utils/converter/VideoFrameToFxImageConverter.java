package io.onelioh.babycut.ui.utils.converter;

import io.onelioh.babycut.engine.decoder.VideoFrame;
import javafx.scene.image.Image;
import org.bytedeco.javacv.JavaFXFrameConverter;

public class VideoFrameToFxImageConverter {

    private JavaFXFrameConverter converter;

    public VideoFrameToFxImageConverter() {
        converter = new JavaFXFrameConverter();
    }

    public Image toImage(VideoFrame frame) {
        try {
            // Valider les dimensions avant conversion
            if (frame.getWidth() <= 0 || frame.getHeight() <= 0) {
                System.err.println("[CONVERTER] Invalid frame dimensions: " + frame.getWidth() + "x" + frame.getHeight());
                return null;
            }
            
            // Vérifier que le frame a des données image
            if (frame.getRawFrame() == null || frame.getRawFrame().image == null) {
                System.err.println("[CONVERTER] Frame has no image data");
                return null;
            }
            
            Image image = converter.convert(frame.getRawFrame());
            if (image == null) {
                System.out.println("[CONVERTER] Conversion failed - Image is null");
            }
            return image;
        } catch (Exception e) {
            System.err.println("[CONVERTER] Error converting frame: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}

