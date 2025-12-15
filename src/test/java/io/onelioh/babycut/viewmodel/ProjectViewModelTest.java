package io.onelioh.babycut.viewmodel;

import io.onelioh.babycut.model.media.AssetType;
import io.onelioh.babycut.model.media.MediaAsset;
import io.onelioh.babycut.model.project.Project;
import io.onelioh.babycut.model.timeline.Timeline;
import io.onelioh.babycut.utils.JavaFXTestBase;
import io.onelioh.babycut.utils.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ProjectViewModelTest extends JavaFXTestBase {

    private ProjectViewModel viewModel;

    @BeforeEach
    void setUp() {
        viewModel = new ProjectViewModel();
    }

    // ==================== TESTS DES VALEURS PAR DÉFAUT ====================

    @Test
    @DisplayName("should initialize with default values")
    void shouldInitializeWithDefaultValues() {
        // ASSERT : Vérifier les valeurs par défaut
        assertThat(viewModel.getProject()).isNull();
        assertThat(viewModel.getProjectName()).isNull();
        assertThat(viewModel.getProjectPath()).isNull();
        assertThat(viewModel.isLoaded()).isFalse();
        assertThat(viewModel.isModified()).isFalse();
        assertThat(viewModel.getCurrentAsset()).isNull();
        assertThat(viewModel.getActiveTimeline()).isNull();
        assertThat(viewModel.getAssets()).isEmpty();
        assertThat(viewModel.getTimelines()).isEmpty();
    }

    // ==================== TESTS DE setProject() ====================

    @Test
    @DisplayName("should update all fields when setting project")
    void shouldUpdateAllFieldsWhenSettingProject() {
        // ARRANGE : Créer un projet avec des données
        Project project = createTestProject();

        // ACT : Définir le projet
        viewModel.setProject(project);

        // ASSERT : Vérifier que TOUS les champs sont mis à jour
        assertThat(viewModel.getProject()).isEqualTo(project);
        assertThat(viewModel.getProjectName()).isEqualTo("Test Project");
        assertThat(viewModel.getProjectPath()).isEqualTo("/test/path");
        assertThat(viewModel.isLoaded()).isTrue();
        assertThat(viewModel.getAssets()).hasSize(2);  // 2 assets dans le projet de test
        assertThat(viewModel.getTimelines()).hasSize(1);  // 1 timeline
        assertThat(viewModel.getActiveTimeline()).isNotNull();
    }

    @Test
    @DisplayName("should reset all fields when setting project to null")
    void shouldResetAllFieldsWhenSettingProjectToNull() {
        // ARRANGE : Créer et définir un projet
        Project project = createTestProject();
        viewModel.setProject(project);

        // Sélectionner un asset pour vérifier la réinitialisation
        MediaAsset asset = viewModel.getAssets().get(0);
        viewModel.selectAsset(asset);

        // ACT : Réinitialiser avec null
        viewModel.setProject(null);

        // ASSERT : Vérifier que tout est réinitialisé
        assertThat(viewModel.getProject()).isNull();
        assertThat(viewModel.getProjectName()).isNull();
        assertThat(viewModel.getProjectPath()).isNull();
        assertThat(viewModel.isLoaded()).isFalse();
        assertThat(viewModel.isModified()).isFalse();
        assertThat(viewModel.getAssets()).isEmpty();
        assertThat(viewModel.getTimelines()).isEmpty();
        assertThat(viewModel.getActiveTimeline()).isNull();
        assertThat(viewModel.getCurrentAsset()).isNull();
    }

    @Test
    @DisplayName("should populate assets list from project")
    void shouldPopulateAssetsListFromProject() {
        // ARRANGE : Créer un projet avec des assets
        Project project = createTestProject();

        // ACT : Définir le projet
        viewModel.setProject(project);

        // ASSERT : Vérifier que les assets sont copiés
        assertThat(viewModel.getAssets()).hasSize(2);
        assertThat(viewModel.getAssets()).containsExactlyElementsOf(project.getMediaAssets());
    }

    @Test
    @DisplayName("should populate timelines list from project")
    void shouldPopulateTimelinesListFromProject() {
        // ARRANGE : Créer un projet avec des timelines
        Project project = createTestProject();

        // ACT : Définir le projet
        viewModel.setProject(project);

        // ASSERT : Vérifier que les timelines sont copiées
        assertThat(viewModel.getTimelines()).hasSize(1);
        assertThat(viewModel.getTimelines()).containsExactlyElementsOf(project.getTimelines());
    }

    @Test
    @DisplayName("should set active timeline from project")
    void shouldSetActiveTimelineFromProject() {
        // ARRANGE : Créer un projet avec une timeline active
        Project project = createTestProject();
        Timeline expectedTimeline = project.getActiveTimeline();

        // ACT : Définir le projet
        viewModel.setProject(project);

        // ASSERT : Vérifier que la timeline active est définie
        assertThat(viewModel.getActiveTimeline()).isEqualTo(expectedTimeline);
    }

    // ==================== TESTS DE selectAsset() ====================

    @Test
    @DisplayName("should select asset")
    void shouldSelectAsset() {
        // ARRANGE : Créer un asset
        MediaAsset asset = TestFixtures.createDummyAsset();

        // ACT : Sélectionner l'asset
        viewModel.selectAsset(asset);

        // ASSERT : Vérifier que l'asset est sélectionné
        assertThat(viewModel.getCurrentAsset()).isEqualTo(asset);
    }

    @Test
    @DisplayName("should replace selected asset when selecting another")
    void shouldReplaceSelectedAssetWhenSelectingAnother() {
        // ARRANGE : Créer deux assets
        MediaAsset firstAsset = TestFixtures.createAsset("video1.mp4", AssetType.VIDEO);
        MediaAsset secondAsset = TestFixtures.createAsset("video2.mp4", AssetType.VIDEO);

        // ACT : Sélectionner le premier, puis le second
        viewModel.selectAsset(firstAsset);
        viewModel.selectAsset(secondAsset);

        // ASSERT : Seul le second doit être sélectionné
        assertThat(viewModel.getCurrentAsset()).isEqualTo(secondAsset);
    }

    @Test
    @DisplayName("should allow deselecting asset by passing null")
    void shouldAllowDeselectingAssetByPassingNull() {
        // ARRANGE : Sélectionner un asset
        MediaAsset asset = TestFixtures.createDummyAsset();
        viewModel.selectAsset(asset);

        // ACT : Désélectionner
        viewModel.selectAsset(null);

        // ASSERT
        assertThat(viewModel.getCurrentAsset()).isNull();
    }

    // ==================== TESTS DE markAsModified() ====================

    @Test
    @DisplayName("should set modified to true when marking as modified")
    void shouldSetModifiedToTrueWhenMarkingAsModified() {
        // ARRANGE : Vérifier l'état initial
        assertThat(viewModel.isModified()).isFalse();

        // ACT : Marquer comme modifié
        viewModel.markAsModified();

        // ASSERT
        assertThat(viewModel.isModified()).isTrue();
    }

    @Test
    @DisplayName("should keep modified flag when marking multiple times")
    void shouldKeepModifiedFlagWhenMarkingMultipleTimes() {
        // ACT : Marquer plusieurs fois
        viewModel.markAsModified();
        viewModel.markAsModified();
        viewModel.markAsModified();

        // ASSERT : Le flag doit rester true
        assertThat(viewModel.isModified()).isTrue();
    }

    // ==================== TESTS DE addTimeline() ====================

    @Test
    @DisplayName("should add timeline to timelines list")
    void shouldAddTimelineToTimelinesList() {
        // ARRANGE : Créer une timeline
        Timeline timeline = TestFixtures.createEmptyTimeline();

        // ACT : Ajouter la timeline
        viewModel.addTimeline(timeline);

        // ASSERT : Vérifier que la timeline a été ajoutée
        assertThat(viewModel.getTimelines()).hasSize(1);
        assertThat(viewModel.getTimelines()).contains(timeline);
    }

    @Test
    @DisplayName("should add multiple timelines")
    void shouldAddMultipleTimelines() {
        // ARRANGE : Créer plusieurs timelines
        Timeline timeline1 = new Timeline("Timeline 1");
        Timeline timeline2 = new Timeline("Timeline 2");

        // ACT : Ajouter les timelines
        viewModel.addTimeline(timeline1);
        viewModel.addTimeline(timeline2);

        // ASSERT : Vérifier que les deux sont ajoutées
        assertThat(viewModel.getTimelines()).hasSize(2);
        assertThat(viewModel.getTimelines()).containsExactly(timeline1, timeline2);
    }

    // ==================== TESTS DES SETTERS INDIVIDUELS ====================

    @Test
    @DisplayName("should update projectName property")
    void shouldUpdateProjectNameProperty() {
        // ACT
        viewModel.setProjectName("New Project Name");

        // ASSERT
        assertThat(viewModel.getProjectName()).isEqualTo("New Project Name");
    }

    @Test
    @DisplayName("should update projectPath property")
    void shouldUpdateProjectPathProperty() {
        // ACT
        viewModel.setProjectPath("/new/path");

        // ASSERT
        assertThat(viewModel.getProjectPath()).isEqualTo("/new/path");
    }

    @Test
    @DisplayName("should update loaded property")
    void shouldUpdateLoadedProperty() {
        // ACT
        viewModel.setLoaded(true);

        // ASSERT
        assertThat(viewModel.isLoaded()).isTrue();
    }

    @Test
    @DisplayName("should update modified property")
    void shouldUpdateModifiedProperty() {
        // ACT
        viewModel.setModified(true);

        // ASSERT
        assertThat(viewModel.isModified()).isTrue();
    }

    @Test
    @DisplayName("should update activeTimeline property")
    void shouldUpdateActiveTimelineProperty() {
        // ARRANGE
        Timeline timeline = TestFixtures.createEmptyTimeline();

        // ACT
        viewModel.setActiveTimeline(timeline);

        // ASSERT
        assertThat(viewModel.getActiveTimeline()).isEqualTo(timeline);
    }

    // ==================== MÉTHODE HELPER ====================

    /**
     * Crée un projet de test avec des données pour les tests.
     */
    private Project createTestProject() {
        Project project = new Project();
        project.setName("Test Project");
        project.setPath("/test/path");

        // Ajouter des assets
        MediaAsset asset1 = TestFixtures.createAsset("video1.mp4", AssetType.VIDEO);
        MediaAsset asset2 = TestFixtures.createAsset("audio1.mp3", AssetType.AUDIO);
        project.addMediaAsset(asset1);
        project.addMediaAsset(asset2);

        // Créer et ajouter une timeline
        Timeline timeline = TestFixtures.createEmptyTimeline();
        project.addTimeline(timeline);
        project.setActiveTimeline(timeline);

        return project;
    }
}
