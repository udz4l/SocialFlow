package socialflow.observer.listeners;

import socialflow.domain.model.Post;
import socialflow.domain.visitor.impl.ModerationReport;
import socialflow.domain.visitor.impl.ModerationReportVisitor;
import socialflow.observer.EventType;
import socialflow.observer.EventSubject;
import socialflow.observer.Observer;
import socialflow.observer.SocialEvent;
import socialflow.observer.Subject;
import socialflow.observer.events.ModerationCompletedEvent;
import socialflow.observer.events.PostCreatedEvent;

import java.time.Instant;
import java.util.Objects;

public class ModerationListener
        implements Observer<SocialEvent>, Subject<SocialEvent> {

    private final ModerationReportVisitor visitor;
    private final EventSubject<SocialEvent> resultSubject;

    public ModerationListener(ModerationReportVisitor visitor) {
        this.visitor = Objects.requireNonNull(visitor);
        this.resultSubject = new EventSubject<>();
    }

    @Override
    public void onEvent(SocialEvent event) {
        if (event instanceof PostCreatedEvent postEvent) {
            Post post = postEvent.post();

            ModerationReport report = post.accept(visitor);

            resultSubject.publish(
                    new ModerationCompletedEvent(
                            post.id(),
                            report,
                            Instant.now()
                    )
            );
        }
    }

    @Override
    public void subscribe(
            EventType type,
            Observer<? super SocialEvent> observer
    ) {
        resultSubject.subscribe(type, observer);
    }

    @Override
    public void unsubscribe(
            EventType type,
            Observer<? super SocialEvent> observer
    ) {
        resultSubject.unsubscribe(type, observer);
    }

    @Override
    public void publish(SocialEvent event) {
        resultSubject.publish(event);
    }
}