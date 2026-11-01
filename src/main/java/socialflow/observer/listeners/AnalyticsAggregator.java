package socialflow.observer.listeners;

import socialflow.observer.EventSubject;
import socialflow.observer.EventType;
import socialflow.observer.Observer;
import socialflow.observer.SocialEvent;
import socialflow.observer.Subject;
import socialflow.observer.events.AnalyticsUpdatedEvent;
import socialflow.observer.events.PostCreatedEvent;
import socialflow.observer.events.ReactionAddedEvent;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class AnalyticsAggregator
        implements Observer<SocialEvent>, Subject<SocialEvent> {

    private final EventSubject<SocialEvent> analyticsSubject =
            new EventSubject<>();

    private final AtomicLong totalPosts = new AtomicLong();
    private final AtomicLong totalReactions = new AtomicLong();

    @Override
    public void onEvent(SocialEvent event) {
        if (event instanceof PostCreatedEvent) {
            totalPosts.incrementAndGet();
        } else if (event instanceof ReactionAddedEvent) {
            totalReactions.incrementAndGet();
        } else {
            return;
        }

        publish(new AnalyticsUpdatedEvent(
                totalPosts.get(),
                totalReactions.get(),
                Instant.now()
        ));
    }

    @Override
    public void subscribe(
            EventType type,
            Observer<? super SocialEvent> observer
    ) {
        analyticsSubject.subscribe(type, observer);
    }

    @Override
    public void unsubscribe(
            EventType type,
            Observer<? super SocialEvent> observer
    ) {
        analyticsSubject.unsubscribe(type, observer);
    }

    @Override
    public void publish(SocialEvent event) {
        analyticsSubject.publish(Objects.requireNonNull(event));
    }

    public long getTotalPosts() {
        return totalPosts.get();
    }

    public long getTotalReactions() {
        return totalReactions.get();
    }
}
