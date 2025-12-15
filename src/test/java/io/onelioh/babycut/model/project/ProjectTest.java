package io.onelioh.babycut.model.project;

import io.onelioh.babycut.model.media.AssetType;
import io.onelioh.babycut.model.media.MediaAsset;
import io.onelioh.babycut.model.timeline.Timeline;
import io.onelioh.babycut.utils.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Project")
class ProjectTest {

    private Project project;

    @BeforeEach
    void setUp() {
        project = new Project();
    }

    // ==================== TESTS DES VALEURS PAR DÉFAUT ====================

    @Test
    @DisplayName("should initialize with default values")
    void shouldInitializeWithDefaultValues() {
        // ASSERT
        assertThat(project.getName()).isNull();
        assertThat(project.getPath()).isNull();
        assertThat(project.getMediaAssets()).isEmpty();
        assertThat(project.getTimelines()).isEmpty();
        assertThat(project.getActiveTimeline()).isNull();
    }

    // ==================== TESTS DE setName() / getName() ====================

    @Test
    @DisplayName("should set and get project name")
    void shouldSetAndGetProjectName() {
        // ACT
        project.setName("My Video Project");

        // ASSERT
        assertThat(project.getName()).isEqualTo("My Video Project");
    }

    // ==================== TESTS DE setPath() / getPath() ====================

    @Test
    @DisplayName("should set and get project path")
    void shouldSetAndGetProjectPath() {
        // ACT
        project.setPath("/home/user/projects/video1");

        // ASSERT
        assertThat(project.getPath()).isEqualTo("/home/user/projects/video1");
    }

    // ==================== TESTS DE addMediaAsset() ====================

    @Test
    @DisplayName("should add media asset")
    void shouldAddMediaAsset() {
        // ARRANGE
        MediaAsset asset = TestFixtures.createDummyAsset();

        // ACT
        project.addMediaAsset(asset);

        // ASSERT
        assertThat(project.getMediaAssets()).hasSize(1);
        assertThat(project.getMediaAssets()).contains(asset);
    }

    @Test
    @DisplayName("should add multiple media assets")
    void shouldAddMultipleMediaAssets() {
        // ARRANGE
        MediaAsset asset1 = TestFixtures.createAsset("video1.mp4", AssetType.VIDEO);
        MediaAsset asset2 = TestFixtures.createAsset("audio1.mp3", AssetType.AUDIO);
        MediaAsset asset3 = TestFixtures.createAsset("image1.png", AssetType.IMAGE);

        // ACT
        project.addMediaAsset(asset1);
        project.addMediaAsset(asset2);
        project.addMediaAsset(asset3);

        // ASSERT
        assertThat(project.getMediaAssets()).hasSize(3);
        assertThat(project.getMediaAssets()).containsExactly(asset1, asset2, asset3);
    }

    // ==================== TESTS DE setMediaAssets() ====================

    @Test
    @DisplayName("should set media assets list")
    void shouldSetMediaAssetsList() {
        // ARRANGE
        MediaAsset asset1 = TestFixtures.createAsset("video1.mp4", AssetType.VIDEO);
        MediaAsset asset2 = TestFixtures.createAsset("video2.mp4", AssetType.VIDEO);
        List<MediaAsset> assets = List.of(asset1, asset2);

        // ACT
        project.setMediaAssets(assets);

        // ASSERT
        assertThat(project.getMediaAssets()).hasSize(2);
        assertThat(project.getMediaAssets()).containsExactly(asset1, asset2);
    }

    @Test
    @DisplayName("should replace existing media assets when setting new list")
    void shouldReplaceExistingMediaAssetsWhenSettingNewList() {
        // ARRANGE
        MediaAsset oldAsset = TestFixtures.createAsset("old.mp4", AssetType.VIDEO);
        project.addMediaAsset(oldAsset);

        MediaAsset newAsset1 = TestFixtures.createAsset("new1.mp4", AssetType.VIDEO);
        MediaAsset newAsset2 = TestFixtures.createAsset("new2.mp4", AssetType.VIDEO);

        // ACT
        project.setMediaAssets(List.of(newAsset1, newAsset2));

        // ASSERT
        assertThat(project.getMediaAssets()).hasSize(2);
        assertThat(project.getMediaAssets()).doesNotContain(oldAsset);
        assertThat(project.getMediaAssets()).containsExactly(newAsset1, newAsset2);
    }

    // ==================== TESTS DE getMediaAssets() ====================

    @Test
    @DisplayName("should return unmodifiable list of media assets")
    void shouldReturnUnmodifiableListOfMediaAssets() {
        // ARRANGE
        MediaAsset asset = TestFixtures.createDummyAsset();
        project.addMediaAsset(asset);

        // ACT & ASSERT
        assertThatThrownBy(() -> project.getMediaAssets().clear())
            .isInstanceOf(UnsupportedOperationException.class);
    }

    // ==================== TESTS DE addTimeline() ====================

    @Test
    @DisplayName("should add timeline")
    void shouldAddTimeline() {
        // ARRANGE
        Timeline timeline = TestFixtures.createEmptyTimeline();

        // ACT
        project.addTimeline(timeline);

        // ASSERT
        assertThat(project.getTimelines()).hasSize(1);
        assertThat(project.getTimelines()).contains(timeline);
    }

    @Test
    @DisplayName("should add multiple timelines")
    void shouldAddMultipleTimelines() {
        // ARRANGE
        Timeline timeline1 = new Timeline("Timeline 1");
        Timeline timeline2 = new Timeline("Timeline 2");
        Timeline timeline3 = new Timeline("Timeline 3");

        // ACT
        project.addTimeline(timeline1);
        project.addTimeline(timeline2);
        project.addTimeline(timeline3);

        // ASSERT
        assertThat(project.getTimelines()).hasSize(3);
        assertThat(project.getTimelines()).containsExactly(timeline1, timeline2, timeline3);
    }

    // ==================== TESTS DE setTimelines() ====================

    @Test
    @DisplayName("should set timelines list")
    void shouldSetTimelinesList() {
        // ARRANGE
        Timeline timeline1 = new Timeline("Timeline 1");
        Timeline timeline2 = new Timeline("Timeline 2");
        List<Timeline> timelines = List.of(timeline1, timeline2);

        // ACT
        project.setTimelines(timelines);

        // ASSERT
        assertThat(project.getTimelines()).hasSize(2);
        assertThat(project.getTimelines()).containsExactly(timeline1, timeline2);
    }

    @Test
    @DisplayName("should replace existing timelines when setting new list")
    void shouldReplaceExistingTimelinesWhenSettingNewList() {
        // ARRANGE
        Timeline oldTimeline = new Timeline("Old Timeline");
        project.addTimeline(oldTimeline);

        Timeline newTimeline1 = new Timeline("New Timeline 1");
        Timeline newTimeline2 = new Timeline("New Timeline 2");

        // ACT
        project.setTimelines(List.of(newTimeline1, newTimeline2));

        // ASSERT
        assertThat(project.getTimelines()).hasSize(2);
        assertThat(project.getTimelines()).doesNotContain(oldTimeline);
        assertThat(project.getTimelines()).containsExactly(newTimeline1, newTimeline2);
    }

    // ==================== TESTS DE getTimelines() ====================

    @Test
    @DisplayName("should return unmodifiable list of timelines")
    void shouldReturnUnmodifiableListOfTimelines() {
        // ARRANGE
        Timeline timeline = TestFixtures.createEmptyTimeline();
        project.addTimeline(timeline);

        // ACT & ASSERT
        assertThatThrownBy(() -> project.getTimelines().clear())
            .isInstanceOf(UnsupportedOperationException.class);
    }

    // ==================== TESTS DE setActiveTimeline() / getActiveTimeline() ====================

    @Test
    @DisplayName("should set and get active timeline")
    void shouldSetAndGetActiveTimeline() {
        // ARRANGE
        Timeline timeline = TestFixtures.createEmptyTimeline();
        project.addTimeline(timeline);

        // ACT
        project.setActiveTimeline(timeline);

        // ASSERT
        assertThat(project.getActiveTimeline()).isEqualTo(timeline);
    }

    @Test
    @DisplayName("should change active timeline")
    void shouldChangeActiveTimeline() {
        // ARRANGE
        Timeline timeline1 = new Timeline("Timeline 1");
        Timeline timeline2 = new Timeline("Timeline 2");
        project.addTimeline(timeline1);
        project.addTimeline(timeline2);

        // ACT
        project.setActiveTimeline(timeline1);
        project.setActiveTimeline(timeline2);

        // ASSERT
        assertThat(project.getActiveTimeline()).isEqualTo(timeline2);
    }

    @Test
    @DisplayName("should allow null active timeline")
    void shouldAllowNullActiveTimeline() {
        // ARRANGE
        Timeline timeline = TestFixtures.createEmptyTimeline();
        project.addTimeline(timeline);
        project.setActiveTimeline(timeline);

        // ACT
        project.setActiveTimeline(null);

        // ASSERT
        assertThat(project.getActiveTimeline()).isNull();
    }

    // ==================== TESTS D'INTÉGRATION ====================

    @Test
    @DisplayName("should support complete project setup")
    void shouldSupportCompleteProjectSetup() {
        // ARRANGE
        MediaAsset video1 = TestFixtures.createAsset("video1.mp4", AssetType.VIDEO);
        MediaAsset video2 = TestFixtures.createAsset("video2.mp4", AssetType.VIDEO);
        MediaAsset audio1 = TestFixtures.createAsset("audio1.mp3", AssetType.AUDIO);

        Timeline timeline1 = new Timeline("Main Timeline");
        Timeline timeline2 = new Timeline("Secondary Timeline");

        // ACT
        project.setName("My Complete Project");
        project.setPath("/home/user/projects/complete");

        project.addMediaAsset(video1);
        project.addMediaAsset(video2);
        project.addMediaAsset(audio1);

        project.addTimeline(timeline1);
        project.addTimeline(timeline2);
        project.setActiveTimeline(timeline1);

        // ASSERT
        assertThat(project.getName()).isEqualTo("My Complete Project");
        assertThat(project.getPath()).isEqualTo("/home/user/projects/complete");
        assertThat(project.getMediaAssets()).hasSize(3);
        assertThat(project.getTimelines()).hasSize(2);
        assertThat(project.getActiveTimeline()).isEqualTo(timeline1);
    }
}
