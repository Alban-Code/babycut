package io.onelioh.babycut.viewmodel;

import io.onelioh.babycut.model.media.MediaAsset;
import io.onelioh.babycut.model.project.Project;
import io.onelioh.babycut.model.timeline.Timeline;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProjectViewModel {

    private ObjectProperty<Project> project = new SimpleObjectProperty<>();
    private StringProperty projectName = new SimpleStringProperty();
    private StringProperty projectPath = new SimpleStringProperty();

    private BooleanProperty loaded = new SimpleBooleanProperty(false);
    private BooleanProperty modified = new SimpleBooleanProperty(false);

    private ObservableList<MediaAsset> assets = FXCollections.observableArrayList();
    private ObjectProperty<MediaAsset> currentAsset = new SimpleObjectProperty<>();

    private ObservableList<Timeline> timelines = FXCollections.observableArrayList();
    private ObjectProperty<Timeline> activeTimeline = new SimpleObjectProperty<>();

    // ===================== UTILITAIRES =====================
    public void selectAsset(MediaAsset asset) {
        this.currentAsset.set(asset);
    }

    public void markAsModified() {
        this.modified.set(true);
    }

    public void setProject(Project project) {
        if (project != null) {
            this.project.set(project);
            this.projectName.set(project.getName());
            this.projectPath.set(project.getPath());
            this.loaded.set(true);
            this.assets.setAll(project.getMediaAssets());
            this.timelines.setAll(project.getTimelines());
            this.activeTimeline.set(project.getActiveTimeline());
        } else {
            this.project.set(null);
            this.loaded.set(false);
            this.projectName.set(null);
            this.projectPath.set(null);
            this.modified.set(false);
            this.assets.clear();
            this.timelines.clear();
            this.activeTimeline.set(null);
            this.currentAsset.set(null);
        }
    }

    public void addTimeline(Timeline timeline) {
        this.timelines.add(timeline);
    }

    public Project getProject() {
        return project.get();
    }

    public ObjectProperty<Project> projectProperty() {
        return project;
    }

    public String getProjectName() {
        return projectName.get();
    }

    public StringProperty projectNameProperty() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName.set(projectName);
    }

    public String getProjectPath() {
        return projectPath.get();
    }

    public StringProperty projectPathProperty() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath.set(projectPath);
    }

    public boolean isLoaded() {
        return loaded.get();
    }

    public BooleanProperty loadedProperty() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded.set(loaded);
    }

    public boolean isModified() {
        return modified.get();
    }

    public BooleanProperty modifiedProperty() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified.set(modified);
    }

    public MediaAsset getCurrentAsset() {
        return currentAsset.get();
    }

    public ObjectProperty<MediaAsset> currentAssetProperty() {
        return currentAsset;
    }

    public void setCurrentAsset(MediaAsset currentAsset) {
        this.currentAsset.set(currentAsset);
    }

    public ObservableList<MediaAsset> getAssets() {
        return assets;
    }

    public ObservableList<Timeline> getTimelines() {
        return timelines;
    }

    public Timeline getActiveTimeline() {
        return activeTimeline.get();
    }

    public ObjectProperty<Timeline> activeTimelineProperty() {
        return activeTimeline;
    }

    public void setActiveTimeline(Timeline activeTimeline) {
        this.activeTimeline.set(activeTimeline);
    }
}
