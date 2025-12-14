package io.onelioh.babycut.model.timeline;

import io.onelioh.babycut.utils.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("TimelineTrack")
class TimelineTrackTest {

    // ============================================
    // Tests du constructeur
    // ============================================

    @Test
    @DisplayName("should initialize with correct type")
    void shouldInitializeWithCorrectType() {
        // ARRANGE & ACT
        TimelineTrack videoTrack = new TimelineTrack(TrackType.VIDEO);
        TimelineTrack audioTrack = new TimelineTrack(TrackType.AUDIO);

        // ASSERT
        assertThat(videoTrack.getType()).isEqualTo(TrackType.VIDEO);
        assertThat(audioTrack.getType()).isEqualTo(TrackType.AUDIO);
    }

    @Test
    @DisplayName("should initialize with empty items list")
    void shouldInitializeWithEmptyItemsList() {
        // ARRANGE & ACT
        TimelineTrack track = new TimelineTrack(TrackType.VIDEO);

        // ASSERT
        assertThat(track.getItems()).isEmpty();
    }

    // ============================================
    // Tests de addItem()
    // ============================================

    @Test
    @DisplayName("should add single item")
    void shouldAddSingleItem() {
        // ARRANGE
        TimelineTrack track = TestFixtures.createTrack(TrackType.VIDEO);
        ClipItem clip = TestFixtures.createClip(0L, 5_000L);

        // ACT
        track.addItem(clip);

        // ASSERT
        assertThat(track.getItems())
            .hasSize(1)
            .containsExactly(clip);
    }

    @Test
    @DisplayName("should add multiple items")
    void shouldAddMultipleItems() {
        // ARRANGE
        TimelineTrack track = TestFixtures.createTrack(TrackType.VIDEO);
        ClipItem clip1 = TestFixtures.createClip(0L, 5_000L);
        ClipItem clip2 = TestFixtures.createClip(6_000L, 4_000L);
        ClipItem clip3 = TestFixtures.createClip(11_000L, 3_000L);

        // ACT
        track.addItem(clip1);
        track.addItem(clip2);
        track.addItem(clip3);

        // ASSERT
        assertThat(track.getItems())
            .hasSize(3)
            .containsExactly(clip1, clip2, clip3);
    }

    @Test
    @DisplayName("should preserve order of added items")
    void shouldPreserveOrderOfAddedItems() {
        // ARRANGE
        TimelineTrack track = TestFixtures.createTrack(TrackType.AUDIO);
        ClipItem clip1 = TestFixtures.createClip(0L, 2_000L);
        ClipItem clip2 = TestFixtures.createClip(5_000L, 3_000L);
        ClipItem clip3 = TestFixtures.createClip(10_000L, 1_000L);

        // ACT
        track.addItem(clip1);
        track.addItem(clip2);
        track.addItem(clip3);

        // ASSERT
        assertThat(track.getItems())
            .containsExactly(clip1, clip2, clip3);
    }

    // ============================================
    // Tests de getItems()
    // ============================================

    @Test
    @DisplayName("should return unmodifiable list of items")
    void shouldReturnUnmodifiableListOfItems() {
        // ARRANGE
        TimelineTrack track = TestFixtures.createTrack(TrackType.VIDEO);
        ClipItem clip = TestFixtures.createClip(0L, 5_000L);
        track.addItem(clip);

        // ACT & ASSERT
        assertThatThrownBy(() -> track.getItems().clear())
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("should return empty list when no items added")
    void shouldReturnEmptyListWhenNoItemsAdded() {
        // ARRANGE
        TimelineTrack track = TestFixtures.createTrack(TrackType.VIDEO);

        // ACT & ASSERT
        assertThat(track.getItems())
            .isNotNull()
            .isEmpty();
    }

    // ============================================
    // Tests de getType()
    // ============================================

    @Test
    @DisplayName("should return correct track type")
    void shouldReturnCorrectTrackType() {
        // ARRANGE
        TimelineTrack videoTrack = TestFixtures.createTrack(TrackType.VIDEO);
        TimelineTrack audioTrack = TestFixtures.createTrack(TrackType.AUDIO);

        // ACT & ASSERT
        assertThat(videoTrack.getType()).isEqualTo(TrackType.VIDEO);
        assertThat(audioTrack.getType()).isEqualTo(TrackType.AUDIO);
    }
}
