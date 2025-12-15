package io.onelioh.babycut.model.media;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MediaData")
class MediaDataTest {

    // ==================== TESTS DU CONSTRUCTEUR ====================

    @Test
    @DisplayName("should initialize all fields correctly")
    void shouldInitializeAllFieldsCorrectly() {
        // ARRANGE & ACT
        MediaData data = new MediaData(30.0, 1920, 1080, 300, 10_000L);

        // ASSERT
        assertThat(data.getFps()).isEqualTo(30.0);
        assertThat(data.getWidth()).isEqualTo(1920);
        assertThat(data.getHeight()).isEqualTo(1080);
        assertThat(data.getTotalFrames()).isEqualTo(300);
        assertThat(data.getDurationMilliseconds()).isEqualTo(10_000L);
    }

    // ==================== TESTS DES GETTERS ====================

    @Test
    @DisplayName("should return correct fps")
    void shouldReturnCorrectFps() {
        // ARRANGE
        MediaData data = new MediaData(60.0, 1920, 1080, 600, 10_000L);

        // ACT
        double fps = data.getFps();

        // ASSERT
        assertThat(fps).isEqualTo(60.0);
    }

    @Test
    @DisplayName("should return correct width")
    void shouldReturnCorrectWidth() {
        // ARRANGE
        MediaData data = new MediaData(30.0, 3840, 2160, 300, 10_000L);

        // ACT
        int width = data.getWidth();

        // ASSERT
        assertThat(width).isEqualTo(3840);
    }

    @Test
    @DisplayName("should return correct height")
    void shouldReturnCorrectHeight() {
        // ARRANGE
        MediaData data = new MediaData(30.0, 1920, 1080, 300, 10_000L);

        // ACT
        int height = data.getHeight();

        // ASSERT
        assertThat(height).isEqualTo(1080);
    }

    @Test
    @DisplayName("should return correct total frames")
    void shouldReturnCorrectTotalFrames() {
        // ARRANGE
        MediaData data = new MediaData(24.0, 1920, 1080, 240, 10_000L);

        // ACT
        int totalFrames = data.getTotalFrames();

        // ASSERT
        assertThat(totalFrames).isEqualTo(240);
    }

    @Test
    @DisplayName("should return correct duration")
    void shouldReturnCorrectDuration() {
        // ARRANGE
        MediaData data = new MediaData(30.0, 1920, 1080, 300, 15_000L);

        // ACT
        double duration = data.getDurationMilliseconds();

        // ASSERT
        assertThat(duration).isEqualTo(15_000L);
    }

    // ==================== TESTS DES SETTERS ====================

    @Test
    @DisplayName("should update fps correctly")
    void shouldUpdateFpsCorrectly() {
        // ARRANGE
        MediaData data = new MediaData(30.0, 1920, 1080, 300, 10_000L);

        // ACT
        data.setFps(60.0);

        // ASSERT
        assertThat(data.getFps()).isEqualTo(60.0);
    }

    @Test
    @DisplayName("should update width correctly")
    void shouldUpdateWidthCorrectly() {
        // ARRANGE
        MediaData data = new MediaData(30.0, 1920, 1080, 300, 10_000L);

        // ACT
        data.setWidth(3840);

        // ASSERT
        assertThat(data.getWidth()).isEqualTo(3840);
    }

    @Test
    @DisplayName("should update height correctly")
    void shouldUpdateHeightCorrectly() {
        // ARRANGE
        MediaData data = new MediaData(30.0, 1920, 1080, 300, 10_000L);

        // ACT
        data.setHeight(2160);

        // ASSERT
        assertThat(data.getHeight()).isEqualTo(2160);
    }

    @Test
    @DisplayName("should update total frames correctly")
    void shouldUpdateTotalFramesCorrectly() {
        // ARRANGE
        MediaData data = new MediaData(30.0, 1920, 1080, 300, 10_000L);

        // ACT
        data.setTotalFrames(600);

        // ASSERT
        assertThat(data.getTotalFrames()).isEqualTo(600);
    }

    @Test
    @DisplayName("should update duration correctly")
    void shouldUpdateDurationCorrectly() {
        // ARRANGE
        MediaData data = new MediaData(30.0, 1920, 1080, 300, 10_000L);

        // ACT
        data.setDurationMilliseconds(20_000L);

        // ASSERT
        assertThat(data.getDurationMilliseconds()).isEqualTo(20_000L);
    }

    // ==================== TESTS DE RÉSOLUTIONS COMMUNES ====================

    @Test
    @DisplayName("should support 1080p resolution")
    void shouldSupport1080pResolution() {
        // ARRANGE & ACT
        MediaData data = new MediaData(30.0, 1920, 1080, 300, 10_000L);

        // ASSERT
        assertThat(data.getWidth()).isEqualTo(1920);
        assertThat(data.getHeight()).isEqualTo(1080);
    }

    @Test
    @DisplayName("should support 4K resolution")
    void shouldSupport4KResolution() {
        // ARRANGE & ACT
        MediaData data = new MediaData(30.0, 3840, 2160, 300, 10_000L);

        // ASSERT
        assertThat(data.getWidth()).isEqualTo(3840);
        assertThat(data.getHeight()).isEqualTo(2160);
    }

    @Test
    @DisplayName("should support 720p resolution")
    void shouldSupport720pResolution() {
        // ARRANGE & ACT
        MediaData data = new MediaData(30.0, 1280, 720, 300, 10_000L);

        // ASSERT
        assertThat(data.getWidth()).isEqualTo(1280);
        assertThat(data.getHeight()).isEqualTo(720);
    }

    // ==================== TESTS DE FPS COMMUNS ====================

    @Test
    @DisplayName("should support cinema frame rate (24 fps)")
    void shouldSupportCinemaFrameRate() {
        // ARRANGE & ACT
        MediaData data = new MediaData(24.0, 1920, 1080, 240, 10_000L);

        // ASSERT
        assertThat(data.getFps()).isEqualTo(24.0);
    }

    @Test
    @DisplayName("should support standard frame rate (30 fps)")
    void shouldSupportStandardFrameRate() {
        // ARRANGE & ACT
        MediaData data = new MediaData(30.0, 1920, 1080, 300, 10_000L);

        // ASSERT
        assertThat(data.getFps()).isEqualTo(30.0);
    }

    @Test
    @DisplayName("should support high frame rate (60 fps)")
    void shouldSupportHighFrameRate() {
        // ARRANGE & ACT
        MediaData data = new MediaData(60.0, 1920, 1080, 600, 10_000L);

        // ASSERT
        assertThat(data.getFps()).isEqualTo(60.0);
    }

    @Test
    @DisplayName("should support very high frame rate (120 fps)")
    void shouldSupportVeryHighFrameRate() {
        // ARRANGE & ACT
        MediaData data = new MediaData(120.0, 1920, 1080, 1200, 10_000L);

        // ASSERT
        assertThat(data.getFps()).isEqualTo(120.0);
    }

    // ==================== TESTS DE COHÉRENCE ====================

    @Test
    @DisplayName("should calculate frames correctly based on fps and duration")
    void shouldCalculateFramesCorrectlyBasedOnFpsAndDuration() {
        // ARRANGE : 30 fps, 10 secondes = 300 frames
        MediaData data = new MediaData(30.0, 1920, 1080, 300, 10_000L);

        // ASSERT : Vérifier la cohérence
        double expectedFrames = (data.getDurationMilliseconds() / 1000.0) * data.getFps();
        assertThat(data.getTotalFrames()).isEqualTo((int) expectedFrames);
    }
}
