package io.onelioh.babycut.service;

import io.onelioh.babycut.model.timeline.Timeline;
import io.onelioh.babycut.model.timeline.TimelineItem;
import io.onelioh.babycut.model.timeline.TimelineTrack;
import io.onelioh.babycut.viewmodel.TimelineViewModel;

public class TimelineService {

    private final TimelineViewModel viewmodel;

    public TimelineService(TimelineViewModel viewmodel) {
        this.viewmodel = viewmodel;
    }

    public void deleteItemFromTimeline(TimelineItem item) {
        Timeline timeline = viewmodel.getTimeline();

        for (int trackIndex = 0; trackIndex < timeline.getTracks().size(); trackIndex++) {
            TimelineTrack currentTrack = timeline.getTracks().get(trackIndex);

            if (currentTrack.getItems().contains(item)) {
                currentTrack.removeItem(item);

                viewmodel.getTracks().get(trackIndex).getItems().remove(item);

                viewmodel.selectClip(null);

            }
        }
    }
}
