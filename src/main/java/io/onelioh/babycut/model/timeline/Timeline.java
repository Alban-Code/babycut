package io.onelioh.babycut.model.timeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Représente une timeline (séquence de montage) dans le projet.
 *
 * Une Timeline est composée de plusieurs pistes (TimelineTrack) et
 * peut être vue comme une séquence indépendante (ex : "Montage final",
 * "Version courte", etc.).
 */
public class Timeline {

    private String name;
    private List<TimelineTrack> tracks = new ArrayList<>();

    public Timeline(String name) {

        this.name = name;
    }


    public String getName() {
        return name;
    }

    public List<TimelineTrack> getTracks() {
        return Collections.unmodifiableList(tracks);
    }

    public void addTrack(TimelineTrack newTrack) {
        tracks.add(newTrack);
    }

    public double getTimelineEnd() {
        double endTime = 0.0;
        for (TimelineTrack track : getTracks()) {
            for (TimelineItem item : track.getItems()) {
                endTime = Math.max(item.getEndTime(), endTime);
            }
        }

        return endTime;
    }
}
