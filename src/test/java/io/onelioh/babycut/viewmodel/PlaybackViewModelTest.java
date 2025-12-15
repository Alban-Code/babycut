package io.onelioh.babycut.viewmodel;

import io.onelioh.babycut.engine.player.PlaybackState;
import io.onelioh.babycut.model.media.MediaAsset;
import io.onelioh.babycut.utils.JavaFXTestBase;
import io.onelioh.babycut.utils.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PlaybackViewModelTest extends JavaFXTestBase {

    private PlaybackViewModel viewModel;

    @BeforeEach
    void setUp() {
        viewModel = new PlaybackViewModel();
    }

    // ==================== TESTS DES PROPERTIES DE BASE ====================

    @Test
    @DisplayName("should initialize with default values")
    void shouldInitializeWithDefaultValues() {
        // ASSERT : Vérifier les valeurs par défaut
        assertThat(viewModel.getCurrentTime()).isEqualTo(0L);
        assertThat(viewModel.getDuration()).isEqualTo(0L);
        assertThat(viewModel.isPlaying()).isFalse();
        assertThat(viewModel.getPlaybackSpeed()).isEqualTo(1.0);
        assertThat(viewModel.getPlaybackState()).isEqualTo(PlaybackState.IDLE);
        assertThat(viewModel.getCurrentAsset()).isNull();
        assertThat(viewModel.isReady()).isFalse();
    }

    @Test
    @DisplayName("should update currentTime property")
    void shouldUpdateCurrentTimeProperty() {
        // ACT
        viewModel.setCurrentTime(5000L);

        // ASSERT
        assertThat(viewModel.getCurrentTime()).isEqualTo(5000L);
    }

    @Test
    @DisplayName("should update duration property")
    void shouldUpdateDurationProperty() {
        // ACT
        viewModel.setDuration(10000L);

        // ASSERT
        assertThat(viewModel.getDuration()).isEqualTo(10000L);
    }

    @Test
    @DisplayName("should update playing property")
    void shouldUpdatePlayingProperty() {
        // ACT
        viewModel.setPlaying(true);

        // ASSERT
        assertThat(viewModel.isPlaying()).isTrue();
    }

    @Test
    @DisplayName("should update playbackSpeed property")
    void shouldUpdatePlaybackSpeedProperty() {
        // ACT
        viewModel.setPlaybackSpeed(2.0);

        // ASSERT
        assertThat(viewModel.getPlaybackSpeed()).isEqualTo(2.0);
    }

    @Test
    @DisplayName("should update playbackState property")
    void shouldUpdatePlaybackStateProperty() {
        // ACT
        viewModel.setPlaybackState(PlaybackState.PLAYING);

        // ASSERT
        assertThat(viewModel.getPlaybackState()).isEqualTo(PlaybackState.PLAYING);
    }

    @Test
    @DisplayName("should update currentAsset property")
    void shouldUpdateCurrentAssetProperty() {
        // ARRANGE
        MediaAsset asset = TestFixtures.createDummyAsset();

        // ACT
        viewModel.setCurrentAsset(asset);

        // ASSERT
        assertThat(viewModel.getCurrentAsset()).isEqualTo(asset);
    }

    @Test
    @DisplayName("should update ready property")
    void shouldUpdateReadyProperty() {
        // ACT
        viewModel.setReady(true);

        // ASSERT
        assertThat(viewModel.isReady()).isTrue();
    }

    // ==================== TESTS DU FORMATAGE DU TEMPS ====================

    @Test
    @DisplayName("should format time correctly for seconds only")
    void shouldFormatTimeCorrectlyForSecondsOnly() {
        // ARRANGE
        viewModel.setCurrentTime(0L);
        viewModel.setDuration(30_000L);  // 30 secondes

        // ACT : Récupérer le temps formaté via le binding
        String formattedTime = viewModel.formattedTimeProperty().get();

        // ASSERT : Format attendu : "00:00 / 00:30"
        assertThat(formattedTime).isEqualTo("00:00 / 00:30");
    }

    @Test
    @DisplayName("should format time correctly for minutes and seconds")
    void shouldFormatTimeCorrectlyForMinutesAndSeconds() {
        // ARRANGE
        viewModel.setCurrentTime(90_000L);   // 1 minute 30 secondes
        viewModel.setDuration(180_000L);     // 3 minutes

        // ACT
        String formattedTime = viewModel.formattedTimeProperty().get();

        // ASSERT : Format attendu : "01:30 / 03:00"
        assertThat(formattedTime).isEqualTo("01:30 / 03:00");
    }

    @Test
    @DisplayName("should format time correctly with hours")
    void shouldFormatTimeCorrectlyWithHours() {
        // ARRANGE
        viewModel.setCurrentTime(3_661_000L);   // 1 heure, 1 minute, 1 seconde
        viewModel.setDuration(7_200_000L);      // 2 heures

        // ACT
        String formattedTime = viewModel.formattedTimeProperty().get();

        // ASSERT : Format attendu : "1:01:01 / 2:00:00"
        assertThat(formattedTime).isEqualTo("1:01:01 / 2:00:00");
    }

    @Test
    @DisplayName("should format negative time as zero")
    void shouldFormatNegativeTimeAsZero() {
        // ARRANGE
        viewModel.setCurrentTime(-5000L);  // Temps négatif (cas d'erreur)
        viewModel.setDuration(10_000L);

        // ACT
        String formattedTime = viewModel.formattedTimeProperty().get();

        // ASSERT : Le temps négatif doit être traité comme 0
        assertThat(formattedTime).isEqualTo("00:00 / 00:10");
    }

    @Test
    @DisplayName("should update formatted time when currentTime changes")
    void shouldUpdateFormattedTimeWhenCurrentTimeChanges() {
        // ARRANGE
        viewModel.setCurrentTime(0L);
        viewModel.setDuration(60_000L);
        String initialFormatted = viewModel.formattedTimeProperty().get();

        // ACT : Changer le currentTime
        viewModel.setCurrentTime(30_000L);
        String updatedFormatted = viewModel.formattedTimeProperty().get();

        // ASSERT : Le binding doit avoir mis à jour automatiquement
        assertThat(initialFormatted).isEqualTo("00:00 / 01:00");
        assertThat(updatedFormatted).isEqualTo("00:30 / 01:00");
    }

    // ==================== TESTS DES STATES PLAYBACK ====================

    @Test
    @DisplayName("should transition from IDLE to PLAYING")
    void shouldTransitionFromIdleToPlaying() {
        // ARRANGE : État initial IDLE
        assertThat(viewModel.getPlaybackState()).isEqualTo(PlaybackState.IDLE);

        // ACT : Passer à PLAYING
        viewModel.setPlaybackState(PlaybackState.PLAYING);
        viewModel.setPlaying(true);

        // ASSERT
        assertThat(viewModel.getPlaybackState()).isEqualTo(PlaybackState.PLAYING);
        assertThat(viewModel.isPlaying()).isTrue();
    }

    @Test
    @DisplayName("should transition from PLAYING to PAUSED")
    void shouldTransitionFromPlayingToPaused() {
        // ARRANGE : Commencer en PLAYING
        viewModel.setPlaybackState(PlaybackState.PLAYING);
        viewModel.setPlaying(true);

        // ACT : Mettre en pause
        viewModel.setPlaybackState(PlaybackState.PAUSED);
        viewModel.setPlaying(false);

        // ASSERT
        assertThat(viewModel.getPlaybackState()).isEqualTo(PlaybackState.PAUSED);
        assertThat(viewModel.isPlaying()).isFalse();
    }

    @Test
    @DisplayName("should set ready when asset is loaded")
    void shouldSetReadyWhenAssetIsLoaded() {
        // ARRANGE
        MediaAsset asset = TestFixtures.createDummyAsset();

        // ACT : Charger un asset et marquer comme prêt
        viewModel.setCurrentAsset(asset);
        viewModel.setReady(true);

        // ASSERT
        assertThat(viewModel.getCurrentAsset()).isEqualTo(asset);
        assertThat(viewModel.isReady()).isTrue();
    }
}
