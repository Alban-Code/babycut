package io.onelioh.babycut.model.timeline;

import io.onelioh.babycut.model.media.MediaAsset;
import io.onelioh.babycut.utils.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ClipItem")
public class ClipItemTest {
    // ============================================
    // Tests du constructeur
    // ============================================

    @Test
    @DisplayName("should initialize all fields correctly via constructor")
    void shouldInitializeAllFieldsCorrectly() {
        // ===================== ARRANGE =====================
        MediaAsset asset = TestFixtures.createDummyAsset();
        long startTime = 0L;
        long durationMs = 15_500L;
        double sourceIn = 0.0;

        // ===================== ACT =====================
        ClipItem item = new ClipItem(startTime, sourceIn, durationMs, asset);

        // ===================== ASSERT =====================
        assertThat(item.getDurationMilliseconds()).isEqualTo(15_500L);
        assertThat(item.getSourceIn()).isEqualTo(0.0);
        assertThat(item.getStartTime()).isEqualTo(0L);
        assertThat(item.getAsset()).isEqualTo(asset);
    }

    // ============================================
    // Tests de getEndTime()
    // ============================================

    @Test
    @DisplayName("Should calculate end time as startTime + durationMs")
    void shouldCalculateEndTimeCorrectly() {
        // ===================== ARRANGE =====================
        ClipItem clip = TestFixtures.createClip(15_000L, 60_000L);

        // ===================== ACT =====================
        long endTime = clip.getEndTime();

        // ===================== ASSERT =====================
        assertThat(endTime).isEqualTo(75_000L);
    }

    @Test
    @DisplayName("should return correct end time when start is zero")
    void shouldCalculateEndTimeWhenStartIsZero() {
        // ARRANGE
        ClipItem clip = TestFixtures.createClip(0L, 5_000L);

        // ACT
        long endTime = clip.getEndTime();

        // ASSERT
        assertThat(endTime).isEqualTo(5_000L);
    }

    // ============================================
    // Tests de setStartTime()
    // ============================================

    @Test
    @DisplayName("should update start time when setStartTime is called")
    void shouldUpdateStartTime() {
        // ARRANGE
        ClipItem clip = TestFixtures.createClip(0L, 5_000L);

        // ACT
        clip.setStartTime(2_000);
        long startTime = clip.getStartTime();

        // ASSERT
        assertThat(startTime).isEqualTo(2_000L);
    }

    @Test
    @DisplayName("should update end time when start time changes")
    void shouldUpdateEndTimeWhenStartTimeChanges() {
        // ARRANGE
        ClipItem clip = TestFixtures.createClip(1_000L, 6_000L);

        // ACT
        clip.setStartTime(3_000L);
        long endTime = clip.getEndTime();

        // ASSERT
        assertThat(endTime).isEqualTo(9_000L);
    }

    // ============================================
    // Tests de setDurationMilliseconds()
    // ============================================

    @Test
    @DisplayName("should update duration when setDurationMilliseconds is called")
    void shouldUpdateDuration() {
        // ARRANGE
        ClipItem clip = TestFixtures.createClip(1_000L, 5_000L);

        // ACT
        clip.setDurationMilliseconds(3_000L);
        long durationMs = clip.getDurationMilliseconds();

        // ASSERT
        assertThat(durationMs).isEqualTo(3_000L);
    }

    @Test
    @DisplayName("should update end time when duration changes")
    void shouldUpdateEndTimeWhenDurationChanges() {
        // ARRANGE
        ClipItem clip = TestFixtures.createClip(1_000L, 5_000L);

        // ACT
        clip.setDurationMilliseconds(8000);
        long endTime = clip.getEndTime();

        // ASSERT
        assertThat(endTime).isEqualTo(9_000L);
    }

    // ============================================
    // Tests de setSourceIn()
    // ============================================

    @Test
    @DisplayName("should update sourceIn when setSourceIn is called")
    void shouldUpdateSourceIn() {
        // ARRANGE
        ClipItem clip = TestFixtures.createClip(0L, 0.0, 1_000L, TestFixtures.createDummyAsset());

        // ACT
        clip.setSourceIn(2_500L);
        double sourceIn = clip.getSourceIn();

        // ASSERT
        assertThat(sourceIn).isEqualTo(2500);
    }

    // ============================================
    // Tests de getItemType()
    // ============================================

    @Test
    @DisplayName("should create a clip item that has the good type")
    void shouldCreateClipType() {
        // ARRANGE
        ClipItem clip = TestFixtures.createClip(0L, 0.0, 1_000L, TestFixtures.createDummyAsset());

        // ACT
        TimelineItemType type = clip.getItemType();

        // ASSERT
        assertThat(type).isEqualTo(TimelineItemType.CLIP);
    }


    // ============================================
    // Tests de validation (edge cases)
    // ============================================

    @Test
    @DisplayName("should reject negative startTime")
    void shouldRejectNegativeStartTime() {
        // ARRANGE
        MediaAsset asset = TestFixtures.createDummyAsset();

        // ACT & ASSERT
        assertThatThrownBy(() -> new ClipItem(-1000, 0, 5000, asset)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("startTime cannot be negative");
    }

    @Test
    @DisplayName("should reject negative duration")
    void shouldRejectNegativeDuration() {
        // ARRANGE
        MediaAsset asset = TestFixtures.createDummyAsset();

        // ACT & ASSERT
        assertThatThrownBy(() -> new ClipItem(1000, 0, -5000, asset)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("duration cannot be negative");
    }

    @Test
    @DisplayName("should accept zero startTime")
    void shouldAcceptZeroStartTime() {
        // ARRANGE & ACT
        ClipItem clip = TestFixtures.createClip(0, 5000);

        // ASSERT
        assertThat(clip.getStartTime()).isEqualTo(0);
        assertThat(clip.getEndTime()).isEqualTo(5000);
    }

    @Test
    @DisplayName("should accept zero duration")
    void shouldAcceptZeroDuration() {
        // ARRANGE & ACT
        ClipItem clip = TestFixtures.createClip(1000, 0);

        // ASSERT
        assertThat(clip.getDurationMilliseconds()).isEqualTo(0);
        assertThat(clip.getEndTime()).isEqualTo(1000);
    }

    @Test
    @DisplayName("should reject negative startTime in setter")
    void shouldRejectNegativeStartTimeInSetter() {
        // ARRANGE
        ClipItem clip = TestFixtures.createClip(1000, 5000);

        // ACT & ASSERT
        assertThatThrownBy(() -> clip.setStartTime(-500)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("startTime cannot be negative");
    }

    @Test
    @DisplayName("should reject negative duration in setter")

    void shouldRejectNegativeDurationInSetter() {
        // ARRANGE
        ClipItem clip = TestFixtures.createClip(1000, 5000);

        // ACT & ASSERT
        assertThatThrownBy(() -> clip.setDurationMilliseconds(-1000)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("duration cannot be negative");
    }

    @Test
    @DisplayName("should reject null asset")
    void shouldRejectNullAsset() {
        assertThatThrownBy(() -> new ClipItem(1000, 0, 5000, null)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("asset cannot be null");
    }
}
