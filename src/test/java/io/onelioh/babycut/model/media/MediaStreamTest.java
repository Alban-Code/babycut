package io.onelioh.babycut.model.media;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MediaStream")
class MediaStreamTest {

    // ==================== TESTS DU CONSTRUCTEUR ====================

    @Test
    @DisplayName("should initialize all fields correctly")
    void shouldInitializeAllFieldsCorrectly() {
        // ARRANGE & ACT
        MediaStream stream = new MediaStream(
            MediaType.VIDEO,
            "h264",
            0,
            "eng",
            "Main Video Track",
            10_000L
        );

        // ASSERT
        assertThat(stream.getType()).isEqualTo(MediaType.VIDEO);
        assertThat(stream.getCodec()).isEqualTo("h264");
        assertThat(stream.getIndex()).isEqualTo(0);
        assertThat(stream.getLang()).isEqualTo("eng");
        assertThat(stream.getDescription()).isEqualTo("Main Video Track");
        assertThat(stream.getDurationMilliseconds()).isEqualTo(10_000L);
    }

    // ==================== TESTS DES GETTERS ====================

    @Test
    @DisplayName("should return correct type")
    void shouldReturnCorrectType() {
        // ARRANGE
        MediaStream videoStream = new MediaStream(MediaType.VIDEO, "h264", 0, "eng", "Video", 5_000L);
        MediaStream audioStream = new MediaStream(MediaType.AUDIO, "aac", 1, "eng", "Audio", 5_000L);

        // ASSERT
        assertThat(videoStream.getType()).isEqualTo(MediaType.VIDEO);
        assertThat(audioStream.getType()).isEqualTo(MediaType.AUDIO);
    }

    @Test
    @DisplayName("should return correct codec")
    void shouldReturnCorrectCodec() {
        // ARRANGE
        MediaStream stream = new MediaStream(MediaType.VIDEO, "h265", 0, "eng", "Video", 5_000L);

        // ACT
        String codec = stream.getCodec();

        // ASSERT
        assertThat(codec).isEqualTo("h265");
    }

    @Test
    @DisplayName("should return correct index")
    void shouldReturnCorrectIndex() {
        // ARRANGE
        MediaStream stream = new MediaStream(MediaType.AUDIO, "aac", 3, "eng", "Audio", 5_000L);

        // ACT
        int index = stream.getIndex();

        // ASSERT
        assertThat(index).isEqualTo(3);
    }

    @Test
    @DisplayName("should return correct language")
    void shouldReturnCorrectLanguage() {
        // ARRANGE
        MediaStream stream = new MediaStream(MediaType.AUDIO, "aac", 0, "fra", "French Audio", 5_000L);

        // ACT
        String lang = stream.getLang();

        // ASSERT
        assertThat(lang).isEqualTo("fra");
    }

    @Test
    @DisplayName("should return correct description")
    void shouldReturnCorrectDescription() {
        // ARRANGE
        MediaStream stream = new MediaStream(MediaType.VIDEO, "h264", 0, "eng", "1080p Main Video", 5_000L);

        // ACT
        String description = stream.getDescription();

        // ASSERT
        assertThat(description).isEqualTo("1080p Main Video");
    }

    @Test
    @DisplayName("should return correct duration")
    void shouldReturnCorrectDuration() {
        // ARRANGE
        MediaStream stream = new MediaStream(MediaType.VIDEO, "h264", 0, "eng", "Video", 15_000L);

        // ACT
        long duration = stream.getDurationMilliseconds();

        // ASSERT
        assertThat(duration).isEqualTo(15_000L);
    }

    // ==================== TESTS DE label() ====================

    @Test
    @DisplayName("should format video label correctly")
    void shouldFormatVideoLabelCorrectly() {
        // ARRANGE
        MediaStream stream = new MediaStream(MediaType.VIDEO, "h264", 0, "eng", "Video Track", 10_000L);

        // ACT
        String label = stream.label();

        // ASSERT
        assertThat(label).isEqualTo("VIDEO #0 (h264)");
    }

    @Test
    @DisplayName("should format audio label correctly")
    void shouldFormatAudioLabelCorrectly() {
        // ARRANGE
        MediaStream stream = new MediaStream(MediaType.AUDIO, "aac", 1, "eng", "Audio Track", 10_000L);

        // ACT
        String label = stream.label();

        // ASSERT
        assertThat(label).isEqualTo("AUDIO #1 (aac)");
    }

    @Test
    @DisplayName("should format label with different codec")
    void shouldFormatLabelWithDifferentCodec() {
        // ARRANGE
        MediaStream stream = new MediaStream(MediaType.VIDEO, "vp9", 2, "eng", "Video", 8_000L);

        // ACT
        String label = stream.label();

        // ASSERT
        assertThat(label).isEqualTo("VIDEO #2 (vp9)");
    }

    @Test
    @DisplayName("should format label with high index")
    void shouldFormatLabelWithHighIndex() {
        // ARRANGE
        MediaStream stream = new MediaStream(MediaType.AUDIO, "opus", 15, "spa", "Spanish Audio", 12_000L);

        // ACT
        String label = stream.label();

        // ASSERT
        assertThat(label).isEqualTo("AUDIO #15 (opus)");
    }

    // ==================== TESTS DE DIFFÉRENTS CODECS ====================

    @Test
    @DisplayName("should support common video codecs")
    void shouldSupportCommonVideoCodecs() {
        // ARRANGE & ACT
        MediaStream h264 = new MediaStream(MediaType.VIDEO, "h264", 0, "eng", "Video", 5_000L);
        MediaStream h265 = new MediaStream(MediaType.VIDEO, "h265", 1, "eng", "Video", 5_000L);
        MediaStream vp9 = new MediaStream(MediaType.VIDEO, "vp9", 2, "eng", "Video", 5_000L);
        MediaStream av1 = new MediaStream(MediaType.VIDEO, "av1", 3, "eng", "Video", 5_000L);

        // ASSERT
        assertThat(h264.getCodec()).isEqualTo("h264");
        assertThat(h265.getCodec()).isEqualTo("h265");
        assertThat(vp9.getCodec()).isEqualTo("vp9");
        assertThat(av1.getCodec()).isEqualTo("av1");
    }

    @Test
    @DisplayName("should support common audio codecs")
    void shouldSupportCommonAudioCodecs() {
        // ARRANGE & ACT
        MediaStream aac = new MediaStream(MediaType.AUDIO, "aac", 0, "eng", "Audio", 5_000L);
        MediaStream mp3 = new MediaStream(MediaType.AUDIO, "mp3", 1, "eng", "Audio", 5_000L);
        MediaStream opus = new MediaStream(MediaType.AUDIO, "opus", 2, "eng", "Audio", 5_000L);
        MediaStream flac = new MediaStream(MediaType.AUDIO, "flac", 3, "eng", "Audio", 5_000L);

        // ASSERT
        assertThat(aac.getCodec()).isEqualTo("aac");
        assertThat(mp3.getCodec()).isEqualTo("mp3");
        assertThat(opus.getCodec()).isEqualTo("opus");
        assertThat(flac.getCodec()).isEqualTo("flac");
    }
}
