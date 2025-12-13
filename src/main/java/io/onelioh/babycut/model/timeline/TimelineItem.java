package io.onelioh.babycut.model.timeline;

/**
 * Élément générique placé sur une timeline.
 *
 * Un TimelineItem possède une position de début et une durée exprimées
 * en secondes dans le temps de la timeline. Les sous-classes concrètes
 * représentent par exemple des clips ou des transitions.
 */
public abstract class TimelineItem {
    protected long startTime;
    protected long durationMilliseconds;

    public long getStartTime() {
        return startTime;
    }

    public long getDurationMilliseconds() {
        return durationMilliseconds;
    }

    public long getEndTime() {
        return durationMilliseconds + startTime;
    }

    public void setDurationMilliseconds(long durationMilliseconds) {
        this.durationMilliseconds = durationMilliseconds;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public abstract TimelineItemType getItemType();
}
