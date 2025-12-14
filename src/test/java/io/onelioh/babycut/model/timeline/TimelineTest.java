package io.onelioh.babycut.model.timeline;

import io.onelioh.babycut.model.media.MediaAsset;
import io.onelioh.babycut.utils.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Timeline")
class TimelineTest {

    // ============================================
    // Tests du constructeur
    // ============================================

    @Test
    @DisplayName("should initialize all fields correctly")
    void shouldInitializeAllFieldsCorrectly() {
        // ARRANGE & ACT
        Timeline timeline = TestFixtures.createEmptyTimeline();

        // ASSERT
        assertThat(timeline.getName()).isEqualTo("Test Timeline");
        assertThat(timeline.getTracks()).isEmpty();
    }

    // ============================================
    // Tests du getEndTime
    // ============================================

    @Test
    @DisplayName("should return zero when timeline is empty")
    void shouldReturnZeroWhenTimelineIsEmpty() {
        // ARRANGE
        Timeline timeline = TestFixtures.createEmptyTimeline();

        // ACT & ASSERT
        assertThat(timeline.getTimelineEnd()).isEqualTo(0L);
    }

    // ============================================
    // Tests d'ajout de track'
    // ============================================

    @Test
    @DisplayName("should add new track")
    void shouldAddNewTrack() {
        // ARRANGE
        Timeline timeline = TestFixtures.createEmptyTimeline();
        TimelineTrack videoTrack = TestFixtures.createTrack(TrackType.VIDEO);
        TimelineTrack audioTrack = TestFixtures.createTrack(TrackType.AUDIO);

        // ACT
        timeline.addTrack(videoTrack);
        timeline.addTrack(audioTrack);

        // ASSERT
        assertThat(timeline.getTracks())
            .hasSize(2)
            .containsExactly(videoTrack, audioTrack);
    }

    // ============================================
    // Tests de getTimelineEnd()
    // ============================================

    @Test
    @DisplayName("should calculate correct timeline end from all tracks")
    void shouldCalculateCorrectTimelineEndFromAllTracks() {
        // ARRANGE
        Timeline timeline = TestFixtures.createEmptyTimeline();

        TimelineTrack videoTrack = new TimelineTrack(TrackType.VIDEO);
        TimelineTrack audioTrack = new TimelineTrack(TrackType.AUDIO);

        MediaAsset asset = TestFixtures.createDummyAsset();

        ClipItem videoClip1 = new ClipItem(0L, 0, 10_000L, asset);
        ClipItem videoClip2 = new ClipItem(10_000L, 0, 15_000L, asset);  // End: 25_000
        videoTrack.addItem(videoClip1);
        videoTrack.addItem(videoClip2);

        ClipItem audioClip = new ClipItem(0L, 0, 10_000L, asset);  // End: 10_000
        audioTrack.addItem(audioClip);

        timeline.addTrack(videoTrack);
        timeline.addTrack(audioTrack);

        // ACT & ASSERT
        assertThat(timeline.getTimelineEnd()).isEqualTo(25_000L);
    }

    @Test
    @DisplayName("should return zero when tracks exist but have no items")
    void shouldReturnZeroWhenTracksHaveNoItems() {
        // ARRANGE
        Timeline timeline = TestFixtures.createEmptyTimeline();
        timeline.addTrack(TestFixtures.createTrack(TrackType.VIDEO));
        timeline.addTrack(TestFixtures.createTrack(TrackType.AUDIO));

        // ACT & ASSERT
        assertThat(timeline.getTimelineEnd()).isEqualTo(0L);
    }

    @Test
    @DisplayName("should calculate timeline end with single track")
    void shouldCalculateTimelineEndWithSingleTrack() {
        // ARRANGE
        Timeline timeline = TestFixtures.createEmptyTimeline();
        TimelineTrack track = TestFixtures.createTrack(TrackType.VIDEO);

        ClipItem clip1 = TestFixtures.createClip(0L, 5_000L);
        ClipItem clip2 = TestFixtures.createClip(6_000L, 4_000L);  // End: 10_000
        track.addItem(clip1);
        track.addItem(clip2);

        timeline.addTrack(track);

        // ACT & ASSERT
        assertThat(timeline.getTimelineEnd()).isEqualTo(10_000L);
    }

    // ============================================
    // Tests de getTracks()
    // ============================================

    @Test
    @DisplayName("should return unmodifiable list of tracks")
    void shouldReturnUnmodifiableListOfTracks() {
        // ARRANGE
        Timeline timeline = TestFixtures.createEmptyTimeline();
        TimelineTrack track = TestFixtures.createTrack(TrackType.VIDEO);
        timeline.addTrack(track);

        // ACT & ASSERT
        assertThatThrownBy(() -> timeline.getTracks().clear())
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("should preserve order of added tracks")
    void shouldPreserveOrderOfAddedTracks() {
        // ARRANGE
        Timeline timeline = TestFixtures.createEmptyTimeline();
        TimelineTrack track1 = TestFixtures.createTrack(TrackType.VIDEO);
        TimelineTrack track2 = TestFixtures.createTrack(TrackType.AUDIO);
        TimelineTrack track3 = TestFixtures.createTrack(TrackType.VIDEO);

        // ACT
        timeline.addTrack(track1);
        timeline.addTrack(track2);
        timeline.addTrack(track3);

        // ASSERT
        assertThat(timeline.getTracks())
            .hasSize(3)
            .containsExactly(track1, track2, track3);
    }
}
