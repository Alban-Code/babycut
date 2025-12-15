package io.onelioh.babycut.viewmodel;

import io.onelioh.babycut.model.timeline.ClipItem;
import io.onelioh.babycut.model.timeline.Timeline;
import io.onelioh.babycut.model.timeline.TimelineTrack;
import io.onelioh.babycut.model.timeline.TrackType;
import io.onelioh.babycut.utils.JavaFXTestBase;
import io.onelioh.babycut.utils.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class TimelineViewModelTest extends JavaFXTestBase {

    private TimelineViewModel viewModel;

    @BeforeEach
    void setUp() {
        viewModel = new TimelineViewModel();
    }

    // ==================== TESTS DU DIRTY FLAG ====================

    @Test
    @DisplayName("should toggle needsRefresh when mark dirty")
    void shouldToggleNeedsRefreshWhenMarkDirty() {
        // ARRANGE: capture initial value
        boolean initialValue = viewModel.needsRefreshProperty().get();

        // ACT: call markDirty()
        viewModel.markDirty();

        // ASSERT: check that value has changed
        assertThat(viewModel.needsRefreshProperty().get()).isNotEqualTo(initialValue);
    }

    @Test
    @DisplayName("should toggle needsRefresh twice")
    void shouldToggleNeedsRefreshTwice() {
        // ARRANGE: capture initial value
        boolean initialValue = viewModel.needsRefreshProperty().get();

        // ACT: call markDirty() twice
        viewModel.markDirty();
        viewModel.markDirty();

        // ASSERT: check that value is the same
        assertThat(viewModel.needsRefreshProperty().get()).isEqualTo(initialValue);
    }

    @Test
    @DisplayName("should call markDirty in selectClip")
    void shouldCallMarkDirtyInSelectClip() {
        // ARRANGE: capture initial value
        boolean initialValue = viewModel.needsRefreshProperty().get();
        ClipItem clip = TestFixtures.createClip(1_000L, 5_000L);

        // ACT: select clip
        viewModel.selectClip(clip);

        // ASSERT: check that value is the same
        assertThat(viewModel.needsRefreshProperty().get()).isNotEqualTo(initialValue);
    }

    // ==================== TESTS DE SÉLECTION ====================

    @Test
    @DisplayName("should update selectedProperty when using selectClip()")
    void shouldUpdateSelectedPropertyWhenUsingSelectClip() {
        // ARRANGE
        ClipItem newValue = TestFixtures.createClip(1_000L, 6_000L);

        // ACT
        viewModel.selectClip(newValue);

        // ASSERT
        assertThat(viewModel.selectedProperty().get()).isEqualTo(newValue);
    }

    @Test
    @DisplayName("should unselect when using selectClip(null)")
    void shouldUnselectWhenUsingNull() {
        // ARRANGE
        ClipItem newValue = TestFixtures.createClip(1_000L, 6_000L);

        // ACT
        viewModel.selectClip(newValue);
        viewModel.selectClip(null);

        // ASSERT
        assertThat(viewModel.selectedProperty().get()).isNull();
    }

    @Test
    @DisplayName("should change selectedProperty when using selectClip()")
    void shouldReplaceSelectedClipWhenUsingSelectClip() {
        // ARRANGE
        ClipItem firstClip = TestFixtures.createClip(7_000L, 8_000L);
        ClipItem secondClip = TestFixtures.createClip(1_000L, 6_000L);

        // ACT
        viewModel.selectClip(firstClip);
        viewModel.selectClip(secondClip);

        // ASSERT
        assertThat(viewModel.selectedProperty().get()).isEqualTo(secondClip);
    }

    // ==================== TESTS DE TIMELINE ====================

    @Test
    @DisplayName("should update all fields when setting timeline")
    void shouldUpdateAllFieldsWhenSettingTimeline() {
        // ARRANGE : Créer une timeline avec des tracks et des clips
        Timeline timeline = TestFixtures.getTimelineWithTracks(5000L);

        // ACT : Définir la timeline
        viewModel.setTimeline(timeline);

        // ASSERT : Vérifier que TOUS les champs sont mis à jour
        assertThat(viewModel.getTimeline()).isEqualTo(timeline);
        assertThat(viewModel.isHasTimeline()).isTrue();
        assertThat(viewModel.getSelected()).isNull();  // Désélection automatique
        assertThat(viewModel.getTimelineEnd()).isEqualTo(5000L);
        assertThat(viewModel.getTracks()).hasSize(2);  // videoTrack + audioTrack
    }

    @Test
    @DisplayName("should create TrackViewModels when setting timeline")
    void shouldCreateTrackViewModelsWhenSettingTimeline() {
        // ARRANGE : Créer une timeline avec 2 tracks
        Timeline timeline = TestFixtures.getTimelineWithEmptyTracks();

        // ACT : Définir la timeline
        viewModel.setTimeline(timeline);

        // ASSERT : Vérifier que les TrackViewModels sont créés
        assertThat(viewModel.getTracks()).hasSize(2);
        assertThat(viewModel.getTracks().get(0).getType()).isEqualTo(TrackType.VIDEO);
        assertThat(viewModel.getTracks().get(1).getType()).isEqualTo(TrackType.AUDIO);
    }

    @Test
    @DisplayName("should reset all fields when setting timeline to null")
    void shouldResetAllFieldsWhenSettingTimelineToNull() {
        // ARRANGE : Créer et définir une timeline, puis sélectionner un clip
        Timeline timeline = TestFixtures.getTimelineWithTracks(5000L);
        viewModel.setTimeline(timeline);
        ClipItem clip = TestFixtures.createClip(0, 3000L);
        viewModel.selectClip(clip);

        // ACT : Réinitialiser avec null
        viewModel.setTimeline(null);

        // ASSERT : Vérifier que tout est réinitialisé
        assertThat(viewModel.getTimeline()).isNull();
        assertThat(viewModel.isHasTimeline()).isFalse();
        assertThat(viewModel.getTracks()).isEmpty();
        assertThat(viewModel.getSelected()).isNull();
    }

    @Test
    @DisplayName("should calculate timelineEnd when setting timeline")
    void shouldCalculateTimelineEndWhenSettingTimeline() {
        // ARRANGE : Créer une timeline dont le dernier clip finit à 8000ms
        Timeline timeline = TestFixtures.getTimelineWithTracks(8000L);

        // ACT : Définir la timeline
        viewModel.setTimeline(timeline);

        // ASSERT : Vérifier que timelineEnd est correct
        assertThat(viewModel.getTimelineEnd()).isEqualTo(8000L);
    }

    // ==================== TESTS DE ZOOM ====================

    @Test
    @DisplayName("should increase pixelsPerSecond when zooming in")
    void shouldIncreasePixelsPerSecondWhenZoomingIn() {
        // ARRANGE : Définir un zoom initial
        viewModel.setPixelsPerSecond(30.0);

        // ACT : Zoomer
        viewModel.zoomIn();

        // ASSERT : Vérifier que le zoom a augmenté de 20%
        assertThat(viewModel.getPixelsPerSecond()).isEqualTo(36.0);  // 30 * 1.2
    }

    @Test
    @DisplayName("should decrease pixelsPerSecond when zooming out")
    void shouldDecreasePixelsPerSecondWhenZoomingOut() {
        // ARRANGE : Définir un zoom initial
        viewModel.setPixelsPerSecond(30.0);

        // ACT : Dézoomer
        viewModel.zoomOut();

        // ASSERT : Vérifier que le zoom a diminué de 20%
        assertThat(viewModel.getPixelsPerSecond()).isEqualTo(25.0);  // 30 / 1.2
    }

    @Test
    @DisplayName("should not exceed max zoom when zooming in")
    void shouldNotExceedMaxZoomWhenZoomingIn() {
        // ARRANGE : Définir un zoom proche du maximum
        viewModel.setPixelsPerSecond(480.0);

        // ACT : Zoomer (480 * 1.2 = 576, mais max = 500)
        viewModel.zoomIn();

        // ASSERT : Vérifier que le zoom est plafonné à 500
        assertThat(viewModel.getPixelsPerSecond()).isEqualTo(500.0);
    }

    @Test
    @DisplayName("should not go below min zoom when zooming out")
    void shouldNotGoBelowMinZoomWhenZoomingOut() {
        // ARRANGE : Définir un zoom proche du minimum
        viewModel.setPixelsPerSecond(22.0);

        // ACT : Dézoomer (22 / 1.2 = 18.33, mais min = 20)
        viewModel.zoomOut();

        // ASSERT : Vérifier que le zoom est plafonné à 20
        assertThat(viewModel.getPixelsPerSecond()).isEqualTo(20.0);
    }

    // ==================== TESTS DE MODIFICATION ====================

    @Test
    @DisplayName("should set modified to true when marking as modified")
    void shouldSetModifiedToTrueWhenMarkingAsModified() {
        // ARRANGE : Vérifier l'état initial
        assertThat(viewModel.isModified()).isFalse();

        // ACT : Marquer comme modifié
        viewModel.markAsModified();

        // ASSERT : Vérifier que modified est true
        assertThat(viewModel.isModified()).isTrue();
    }

    @Test
    @DisplayName("should mark as modified when adding clip to track")
    void shouldMarkAsModifiedWhenAddingClipToTrack() {
        // ARRANGE : Créer une timeline et un clip
        Timeline timeline = TestFixtures.getTimelineWithEmptyTracks();
        viewModel.setTimeline(timeline);
        ClipItem clip = TestFixtures.createClip(0, 5000L);

        // ACT : Ajouter un clip à la première track
        viewModel.addClipToTrack(0, clip);

        // ASSERT : Vérifier que le projet est marqué comme modifié
        assertThat(viewModel.isModified()).isTrue();
    }

    // ==================== TESTS DE TrackViewModel ====================

    @Test
    @DisplayName("should copy items from track to TrackViewModel")
    void shouldCopyItemsFromTrackToTrackViewModel() {
        // ARRANGE : Créer une timeline avec des clips
        Timeline timeline = TestFixtures.getTimelineWithTracks(5000L);

        // ACT : Définir la timeline
        viewModel.setTimeline(timeline);

        // ASSERT : Vérifier que les TrackViewModels contiennent les clips
        assertThat(viewModel.getTracks().get(0).getItems()).hasSize(1);  // 1 clip vidéo
        assertThat(viewModel.getTracks().get(1).getItems()).hasSize(1);  // 1 clip audio
    }

    @Test
    @DisplayName("should preserve track type in TrackViewModel")
    void shouldPreserveTrackTypeInTrackViewModel() {
        // ARRANGE : Créer une timeline avec 2 tracks
        Timeline timeline = TestFixtures.getTimelineWithEmptyTracks();

        // ACT : Définir la timeline
        viewModel.setTimeline(timeline);

        // ASSERT : Vérifier que les types sont corrects
        assertThat(viewModel.getTracks().get(0).getType()).isEqualTo(TrackType.VIDEO);
        assertThat(viewModel.getTracks().get(1).getType()).isEqualTo(TrackType.AUDIO);
    }
}