package io.onelioh.babycut.model.media;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MediaInfo")
class MediaInfoTest {

    // ==================== TESTS DU CONSTRUCTEUR PAR DÉFAUT ====================

    @Test
    @DisplayName("should initialize with empty streams when using default constructor")
    void shouldInitializeWithEmptyStreamsWhenUsingDefaultConstructor() {
        // ACT
        MediaInfo mediaInfo = new MediaInfo();

        // ASSERT
        assertThat(mediaInfo.getVideoStreams()).isEmpty();
        assertThat(mediaInfo.getAudioStreams()).isEmpty();
        assertThat(mediaInfo.getDurationMilliseconds()).isEqualTo(0L);
    }

    // ==================== TESTS DU CONSTRUCTEUR AVEC PARAMÈTRES ====================

    @Test
    @DisplayName("should initialize all fields correctly with parameterized constructor")
    void shouldInitializeAllFieldsCorrectlyWithParameterizedConstructor() {
        // ARRANGE
        MediaStream videoStream = new MediaStream(MediaType.VIDEO, "h264", 0, "eng", "Video Track", 10_000L);
        MediaStream audioStream = new MediaStream(MediaType.AUDIO, "aac", 1, "eng", "Audio Track", 10_000L);

        List<MediaStream> videoStreams = List.of(videoStream);
        List<MediaStream> audioStreams = List.of(audioStream);
        long duration = 10_000L;

        // ACT
        MediaInfo mediaInfo = new MediaInfo(videoStreams, audioStreams, duration);

        // ASSERT
        assertThat(mediaInfo.getVideoStreams()).hasSize(1);
        assertThat(mediaInfo.getAudioStreams()).hasSize(1);
        assertThat(mediaInfo.getDurationMilliseconds()).isEqualTo(10_000L);
    }

    // ==================== TESTS DES GETTERS ====================

    @Test
    @DisplayName("should return unmodifiable list of video streams")
    void shouldReturnUnmodifiableListOfVideoStreams() {
        // ARRANGE
        MediaStream videoStream = new MediaStream(MediaType.VIDEO, "h264", 0, "eng", "Video", 5_000L);
        MediaInfo mediaInfo = new MediaInfo(List.of(videoStream), List.of(), 5_000L);

        // ACT & ASSERT
        assertThatThrownBy(() -> mediaInfo.getVideoStreams().clear())
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("should return unmodifiable list of audio streams")
    void shouldReturnUnmodifiableListOfAudioStreams() {
        // ARRANGE
        MediaStream audioStream = new MediaStream(MediaType.AUDIO, "aac", 0, "eng", "Audio", 5_000L);
        MediaInfo mediaInfo = new MediaInfo(List.of(), List.of(audioStream), 5_000L);

        // ACT & ASSERT
        assertThatThrownBy(() -> mediaInfo.getAudioStreams().clear())
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("should return correct duration")
    void shouldReturnCorrectDuration() {
        // ARRANGE
        MediaInfo mediaInfo = new MediaInfo(List.of(), List.of(), 15_000L);

        // ACT
        long duration = mediaInfo.getDurationMilliseconds();

        // ASSERT
        assertThat(duration).isEqualTo(15_000L);
    }

    // ==================== TESTS DES LABELS ====================

    @Test
    @DisplayName("should return video labels correctly")
    void shouldReturnVideoLabelsCorrectly() {
        // ARRANGE
        MediaStream video1 = new MediaStream(MediaType.VIDEO, "h264", 0, "eng", "Main Video", 10_000L);
        MediaStream video2 = new MediaStream(MediaType.VIDEO, "h265", 1, "fra", "Secondary Video", 10_000L);

        MediaInfo mediaInfo = new MediaInfo(List.of(video1, video2), List.of(), 10_000L);

        // ACT
        List<String> videoLabels = mediaInfo.getVideoLabels();

        // ASSERT
        assertThat(videoLabels).hasSize(2);
        assertThat(videoLabels.get(0)).isEqualTo("VIDEO #0 (h264)");
        assertThat(videoLabels.get(1)).isEqualTo("VIDEO #1 (h265)");
    }

    @Test
    @DisplayName("should return audio labels correctly")
    void shouldReturnAudioLabelsCorrectly() {
        // ARRANGE
        MediaStream audio1 = new MediaStream(MediaType.AUDIO, "aac", 0, "eng", "English Audio", 10_000L);
        MediaStream audio2 = new MediaStream(MediaType.AUDIO, "mp3", 1, "fra", "French Audio", 10_000L);

        MediaInfo mediaInfo = new MediaInfo(List.of(), List.of(audio1, audio2), 10_000L);

        // ACT
        List<String> audioLabels = mediaInfo.getAudioLabels();

        // ASSERT
        assertThat(audioLabels).hasSize(2);
        assertThat(audioLabels.get(0)).isEqualTo("AUDIO #0 (aac)");
        assertThat(audioLabels.get(1)).isEqualTo("AUDIO #1 (mp3)");
    }

    @Test
    @DisplayName("should return empty lists when no streams")
    void shouldReturnEmptyListsWhenNoStreams() {
        // ARRANGE
        MediaInfo mediaInfo = new MediaInfo();

        // ACT
        List<String> videoLabels = mediaInfo.getVideoLabels();
        List<String> audioLabels = mediaInfo.getAudioLabels();

        // ASSERT
        assertThat(videoLabels).isEmpty();
        assertThat(audioLabels).isEmpty();
    }

    // ==================== TESTS AVEC MULTIPLE STREAMS ====================

    @Test
    @DisplayName("should handle multiple video and audio streams")
    void shouldHandleMultipleVideoAndAudioStreams() {
        // ARRANGE
        MediaStream video1 = new MediaStream(MediaType.VIDEO, "h264", 0, "eng", "Video 1", 10_000L);
        MediaStream video2 = new MediaStream(MediaType.VIDEO, "h265", 1, "eng", "Video 2", 10_000L);
        MediaStream audio1 = new MediaStream(MediaType.AUDIO, "aac", 2, "eng", "Audio 1", 10_000L);
        MediaStream audio2 = new MediaStream(MediaType.AUDIO, "mp3", 3, "fra", "Audio 2", 10_000L);
        MediaStream audio3 = new MediaStream(MediaType.AUDIO, "opus", 4, "spa", "Audio 3", 10_000L);

        MediaInfo mediaInfo = new MediaInfo(
            List.of(video1, video2),
            List.of(audio1, audio2, audio3),
            10_000L
        );

        // ASSERT
        assertThat(mediaInfo.getVideoStreams()).hasSize(2);
        assertThat(mediaInfo.getAudioStreams()).hasSize(3);
        assertThat(mediaInfo.getVideoLabels()).hasSize(2);
        assertThat(mediaInfo.getAudioLabels()).hasSize(3);
    }
}
