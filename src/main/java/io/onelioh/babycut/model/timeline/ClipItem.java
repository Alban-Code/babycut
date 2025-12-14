package io.onelioh.babycut.model.timeline;

import io.onelioh.babycut.model.media.MediaAsset;

/**
 * Élément générique placé sur une timeline.
 * <p>
 * Un TimelineItem possède une position de début et une durée exprimées
 * en secondes dans le temps de la timeline. Les sous-classes concrètes
 * représentent par exemple des clips ou des transitions.
 */
public class ClipItem extends TimelineItem {
    private MediaAsset asset;
    /**
     * Point d'entrée dans le média source (en millisecondes).
     * Représente à quel moment du fichier source on commence la lecture.
     * Exemple :
     * - sourceIn = 0 : on lit le fichier source depuis le début
     * - sourceIn = 2000 : on lit le fichier source à partir de 2 secondes
     * Utilisé pour le "trim" : découper une portion du média source.
     */
    private double sourceIn;


    public ClipItem(long startTime, double sourceIn, long durationMilliseconds, MediaAsset asset) {
        if (startTime < 0L) {
            throw new IllegalArgumentException("startTime cannot be negative, got: " + startTime);
        }
        if (durationMilliseconds < 0L) {
            throw new IllegalArgumentException("duration cannot be negative, got: " + durationMilliseconds);
        }
        if (asset == null) {
            throw new IllegalArgumentException("asset cannot be null");
        }
        this.startTime = startTime;
        this.sourceIn = sourceIn;
        this.durationMilliseconds = durationMilliseconds;
        this.asset = asset;
    }

    public MediaAsset getAsset() {
        return asset;
    }

    @Override
    public TimelineItemType getItemType() {
        return TimelineItemType.CLIP;
    }

    public double getSourceIn() {
        return sourceIn;
    }

    public void setSourceIn(double sourceIn) {
        this.sourceIn = sourceIn;
    }
}
