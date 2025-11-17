package io.onelioh.babycut.model.timeline;

import io.onelioh.babycut.model.media.MediaAsset;

/**
 * Élément générique placé sur une timeline.
 *
 * Un TimelineItem possède une position de début et une durée exprimées
 * en secondes dans le temps de la timeline. Les sous-classes concrètes
 * représentent par exemple des clips ou des transitions.
 */
public class ClipItem extends TimelineItem {
    private MediaAsset asset;
    private double sourceIn;


    public ClipItem(double startTime, double sourceIn, double duration, MediaAsset asset) {
        this.startTime = startTime;
        this.sourceIn = sourceIn;
        this.duration = duration;
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
}
