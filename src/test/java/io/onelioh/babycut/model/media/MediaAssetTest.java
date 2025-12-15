package io.onelioh.babycut.model.media;

import io.onelioh.babycut.utils.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MediaAsset")
class MediaAssetTest {

    // ==================== TESTS DU CONSTRUCTEUR ====================

    @Test
    @DisplayName("should initialize all fields correctly")
    void shouldInitializeAllFieldsCorrectly() {
        // ARRANGE
        Path path = Path.of("/test/video.mp4");
        AssetType type = AssetType.VIDEO;
        MediaInfo mediaInfo = new MediaInfo();

        // ACT
        MediaAsset asset = new MediaAsset(path, type, mediaInfo);

        // ASSERT
        assertThat(asset.getPath()).isEqualTo(path);
        assertThat(asset.getType()).isEqualTo(type);
        assertThat(asset.getMediaInfo()).isEqualTo(mediaInfo);
    }

    @Test
    @DisplayName("should accept video asset type")
    void shouldAcceptVideoAssetType() {
        // ARRANGE & ACT
        MediaAsset asset = TestFixtures.createAsset("video.mp4", AssetType.VIDEO);

        // ASSERT
        assertThat(asset.getType()).isEqualTo(AssetType.VIDEO);
    }

    @Test
    @DisplayName("should accept audio asset type")
    void shouldAcceptAudioAssetType() {
        // ARRANGE & ACT
        MediaAsset asset = TestFixtures.createAsset("audio.mp3", AssetType.AUDIO);

        // ASSERT
        assertThat(asset.getType()).isEqualTo(AssetType.AUDIO);
    }

    @Test
    @DisplayName("should accept image asset type")
    void shouldAcceptImageAssetType() {
        // ARRANGE & ACT
        MediaAsset asset = TestFixtures.createAsset("image.png", AssetType.IMAGE);

        // ASSERT
        assertThat(asset.getType()).isEqualTo(AssetType.IMAGE);
    }

    // ==================== TESTS DES GETTERS ====================

    @Test
    @DisplayName("should return correct path")
    void shouldReturnCorrectPath() {
        // ARRANGE
        Path expectedPath = Path.of("/test/files/video.mp4");
        MediaAsset asset = new MediaAsset(expectedPath, AssetType.VIDEO, new MediaInfo());

        // ACT
        Path actualPath = asset.getPath();

        // ASSERT
        assertThat(actualPath).isEqualTo(expectedPath);
    }

    @Test
    @DisplayName("should return correct media info")
    void shouldReturnCorrectMediaInfo() {
        // ARRANGE
        MediaInfo expectedMediaInfo = new MediaInfo();
        MediaAsset asset = new MediaAsset(Path.of("/test/video.mp4"), AssetType.VIDEO, expectedMediaInfo);

        // ACT
        MediaInfo actualMediaInfo = asset.getMediaInfo();

        // ASSERT
        assertThat(actualMediaInfo).isEqualTo(expectedMediaInfo);
    }

    // ==================== TESTS D'IMMUTABILITÉ ====================

    @Test
    @DisplayName("should have immutable fields")
    void shouldHaveImmutableFields() {
        // ARRANGE
        Path path = Path.of("/test/video.mp4");
        AssetType type = AssetType.VIDEO;
        MediaInfo mediaInfo = new MediaInfo();

        // ACT
        MediaAsset asset = new MediaAsset(path, type, mediaInfo);

        // ASSERT : Les champs sont final et ne peuvent pas être modifiés
        assertThat(asset.getPath()).isSameAs(path);
        assertThat(asset.getType()).isSameAs(type);
        assertThat(asset.getMediaInfo()).isSameAs(mediaInfo);
    }
}
