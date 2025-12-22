package io.onelioh.babycut.command.timeline;

import io.onelioh.babycut.command.Command;
import io.onelioh.babycut.model.timeline.ClipItem;
import io.onelioh.babycut.viewmodel.TimelineViewModel;

public class DeleteClipCommand implements Command {

    private final ClipItem clipToDelete;
    private final TimelineViewModel viewmodel;
    private TimelineViewModel.TrackViewModel trackContainingClip;
    private int indexInTrack;


    public DeleteClipCommand(TimelineViewModel viewmodel, ClipItem clipToDelete) {
        this.clipToDelete = clipToDelete;
        this.viewmodel = viewmodel;

        trackContainingClip =
                viewmodel.getTracks().stream().filter(track -> {
            return track.getItems().stream().anyMatch(item -> item == clipToDelete);
        }).findFirst().get();
    }
    @Override
    public void execute() {
        this.viewmodel.deleteClipFromTrack(trackContainingClip, clipToDelete);
        this.viewmodel.selectClip(null);
        this.viewmodel.setTimelineEnd(viewmodel.getTimeline().getTimelineEnd());
        this.viewmodel.markDirty();
    }

    @Override
    public void undo() {
        this.viewmodel.addClipToTrack();
    }
}
