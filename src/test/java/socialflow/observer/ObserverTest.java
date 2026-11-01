
package socialflow.observer;

import org.junit.jupiter.api.Test;
import socialflow.domain.model.*;
import socialflow.observer.events.*;
import socialflow.observer.listeners.*;
import socialflow.observer.service.*;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ObserverTest {

    private Post createPost() {
        return new Post(
                "p1",
                "Alice",
                "Hello world",
                List.of()
        );
    }

    @Test
    void eventIsDeliveredToAllSubscribers() {
        EventSubject<SocialEvent> subject = new EventSubject<>();

        AtomicInteger firstCounter = new AtomicInteger();
        AtomicInteger secondCounter = new AtomicInteger();

        Observer<SocialEvent> first =
                event -> firstCounter.incrementAndGet();

        Observer<SocialEvent> second =
                event -> secondCounter.incrementAndGet();

        subject.subscribe(EventType.POST_CREATED, first);
        subject.subscribe(EventType.POST_CREATED, second);

        subject.publish(new PostCreatedEvent(
                createPost(),
                Instant.now()
        ));

        assertEquals(1, firstCounter.get());
        assertEquals(1, secondCounter.get());
    }

    @Test
    void subscriberCanUnsubscribeDuringEventDelivery() {
        EventSubject<SocialEvent> subject = new EventSubject<>();

        AtomicInteger firstCounter = new AtomicInteger();
        AtomicInteger secondCounter = new AtomicInteger();

        Observer<SocialEvent> second =
                event -> secondCounter.incrementAndGet();

        Observer<SocialEvent> first = event -> {
            firstCounter.incrementAndGet();
            subject.unsubscribe(EventType.POST_CREATED, second);
        };

        subject.subscribe(EventType.POST_CREATED, first);
        subject.subscribe(EventType.POST_CREATED, second);

        subject.publish(new PostCreatedEvent(
                createPost(),
                Instant.now()
        ));

        assertEquals(1, firstCounter.get());

        subject.publish(new PostCreatedEvent(
                createPost(),
                Instant.now()
        ));

        assertEquals(2, firstCounter.get());
        assertEquals(1, secondCounter.get());
    }

    @Test
    void subscriberCanSubscribeDuringEventDelivery() {
        EventSubject<SocialEvent> subject = new EventSubject<>();

        AtomicInteger newSubscriberCounter = new AtomicInteger();

        Observer<SocialEvent> newSubscriber =
                event -> newSubscriberCounter.incrementAndGet();

        Observer<SocialEvent> first = event ->
                subject.subscribe(EventType.POST_CREATED, newSubscriber);

        subject.subscribe(EventType.POST_CREATED, first);

        subject.publish(new PostCreatedEvent(
                createPost(),
                Instant.now()
        ));

        assertEquals(0, newSubscriberCounter.get());

        subject.publish(new PostCreatedEvent(
                createPost(),
                Instant.now()
        ));

        assertEquals(1, newSubscriberCounter.get());
    }

    @Test
    void subscribersReceiveOnlyTheirEventType() {
        EventSubject<SocialEvent> subject = new EventSubject<>();

        AtomicInteger counter = new AtomicInteger();

        Observer<SocialEvent> observer =
                event -> counter.incrementAndGet();

        subject.subscribe(EventType.POST_CREATED, observer);

        subject.publish(new ReactionAddedEvent(
                createPost(),
                new Reaction("user1", ReactionType.LIKE),
                Instant.now()
        ));

        assertEquals(0, counter.get());
    }

    @Test
    void analyticsAggregatorPublishesAggregatedEvents() {
        AnalyticsAggregator aggregator = new AnalyticsAggregator();

        AtomicInteger analyticsEvents = new AtomicInteger();

        Observer<SocialEvent> observer =
                event -> analyticsEvents.incrementAndGet();

        aggregator.subscribe(
                EventType.ANALYTICS_UPDATED,
                observer
        );

        aggregator.onEvent(new PostCreatedEvent(
                createPost(),
                Instant.now()
        ));

        aggregator.onEvent(new ReactionAddedEvent(
                createPost(),
                new Reaction("user1", ReactionType.LIKE),
                Instant.now()
        ));

        assertEquals(2, analyticsEvents.get());
        assertEquals(1, aggregator.getTotalPosts());
        assertEquals(1, aggregator.getTotalReactions());
    }

    @Test
    void postServicePublishesPostCreatedEvent() {
        EventSubject<SocialEvent> subject = new EventSubject<>();

        AtomicInteger counter = new AtomicInteger();

        subject.subscribe(
                EventType.POST_CREATED,
                event -> counter.incrementAndGet()
        );

        PostService service = new PostService(subject);

        service.publishPost(createPost());

        assertEquals(1, counter.get());
    }

    @Test
    void reactionServicePublishesReactionAddedEvent() {
        EventSubject<SocialEvent> subject = new EventSubject<>();

        AtomicInteger counter = new AtomicInteger();

        subject.subscribe(
                EventType.REACTION_ADDED,
                event -> counter.incrementAndGet()
        );

        ReactionService service = new ReactionService(subject);

        service.addReaction(
                createPost(),
                new Reaction("user1", ReactionType.LOVE)
        );

        assertEquals(1, counter.get());
    }

    @Test
    void unsubscribedObserverDoesNotReceiveEvents() {
        EventSubject<SocialEvent> subject = new EventSubject<>();

        AtomicInteger counter = new AtomicInteger();

        Observer<SocialEvent> observer =
                event -> counter.incrementAndGet();

        subject.subscribe(EventType.POST_CREATED, observer);
        subject.unsubscribe(EventType.POST_CREATED, observer);

        subject.publish(new PostCreatedEvent(
                createPost(),
                Instant.now()
        ));

        assertEquals(0, counter.get());
    }

    @Test
    void duplicateSubscriptionDoesNotDuplicateDelivery() {
        EventSubject<SocialEvent> subject = new EventSubject<>();

        AtomicInteger counter = new AtomicInteger();

        Observer<SocialEvent> observer =
                event -> counter.incrementAndGet();

        subject.subscribe(EventType.POST_CREATED, observer);
        subject.subscribe(EventType.POST_CREATED, observer);

        subject.publish(new PostCreatedEvent(
                createPost(),
                Instant.now()
        ));

        assertEquals(1, counter.get());
    }
}
