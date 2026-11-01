package socialflow.observer.listeners;

import socialflow.domain.visitor.impl.ModerationReport;
import socialflow.observer.Observer;
import socialflow.observer.SocialEvent;
import socialflow.observer.events.ModerationCompletedEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModerationResultListener implements Observer<SocialEvent> {

    private final List<ModerationReport> reports =
            new CopyOnWriteArrayList<>();

    @Override
    public void onEvent(SocialEvent event) {
        if (event instanceof ModerationCompletedEvent moderationEvent) {
            reports.add(moderationEvent.report());
        }
    }

    public List<ModerationReport> reports() {
        return List.copyOf(reports);
    }
}