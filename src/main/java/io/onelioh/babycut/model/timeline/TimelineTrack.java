package io.onelioh.babycut.model.timeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Piste d'une timeline, contenant une suite d'éléments.
 *
 * Une TimelineTrack est typée (VIDEO ou AUDIO) et contient une liste
 * ordonnée de TimelineItem (clips, transitions...). Elle correspond à
 * une "track" classique dans un logiciel de montage.
 */
public class TimelineTrack {

    private TrackType type;
    private List<TimelineItem> items = new ArrayList<>();

    public TimelineTrack(TrackType type) {
        this.type = type;
    }

    public TrackType getType() {
        return type;
    }

    public List<TimelineItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addItem(TimelineItem newItem) {
        items.add(newItem);
    }
}
