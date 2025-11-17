package io.onelioh.babycut.model.timeline;

/**
 * Élément générique placé sur une timeline.
 *
 * Un TimelineItem possède une position de début et une durée exprimées
 * en secondes dans le temps de la timeline. Les sous-classes concrètes
 * représentent par exemple des clips ou des transitions.
 */
public abstract class TimelineItem {
    protected double startTime;
    protected double duration;

    public double getStartTime() {
        return startTime;
    }

    public double getDuration() {
        return duration;
    }

    public double getEndTime() {
        return duration + startTime;
    }

    public abstract TimelineItemType getItemType();
}
