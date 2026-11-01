package socialflow.concurrency;

import org.junit.jupiter.api.Test;
import socialflow.domain.model.*;
import socialflow.domain.visitor.impl.EngagementVisitor;
import socialflow.observer.*;
import socialflow.observer.events.*;
import socialflow.observer.listeners.*;
import socialflow.observer.service.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrencyTest {

    @Test
    void fourProducersProcessFourThousandEventsWithoutLoss() {
        EventSubject<SocialEvent> subject = new EventSubject<>();

        PostService postService = new PostService(subject);
        ReactionService reactionService = new ReactionService(subject);

        FeedListener feed = new FeedListener();
        NotificationListener notifications = new NotificationListener();
        TrendingListener trending = new TrendingListener();
        AnalyticsAggregator analytics = new AnalyticsAggregator();

        subject.subscribe(EventType.POST_CREATED, feed);
        subject.subscribe(EventType.POST_CREATED, notifications);
        subject.subscribe(EventType.POST_CREATED, analytics);

        subject.subscribe(EventType.REACTION_ADDED, notifications);
        subject.subscribe(EventType.REACTION_ADDED, trending);
        subject.subscribe(EventType.REACTION_ADDED, analytics);

        ConcurrentLinkedQueue<Post> generatedPosts =
                new ConcurrentLinkedQueue<>();

        try (ConcurrentEventRunner runner =
                     new ConcurrentEventRunner(4)) {

            runner.runProducers(4, 1000, (producerId, eventIndex) -> {
                if (eventIndex % 2 == 0) {
                    int postNumber = eventIndex / 2;

                    String postId = producerId + "-" + postNumber;

                    Reaction reaction = new Reaction(
                            "user-" + producerId,
                            ReactionType.LIKE
                    );

                    Post post = new Post(
                            postId,
                            "User-" + producerId,
                            "Stress test post",
                            List.of(
                                    new Comment(
                                            "comment-" + postId,
                                            "User-" + producerId,
                                            "Test comment"
                                    ),
                                    reaction
                            )
                    );

                    generatedPosts.add(post);

                    postService.publishPost(post);
                    reactionService.addReaction(post, reaction);
                }
            });
        }

        assertEquals(2000, feed.getFeed().size());
        assertEquals(4000, notifications.getNotifications().size());

        assertEquals(2000, trending.getCount(ReactionType.LIKE));

        assertEquals(2000, analytics.getTotalPosts());
        assertEquals(2000, analytics.getTotalReactions());

        Community community = new Community(
                "Stress Test Community",
                List.of(
                        new Group(
                                "Stress Test Group",
                                new ArrayList<>(generatedPosts)
                        )
                )
        );

        long engagement = community.accept(
                new EngagementVisitor()
        );

        assertEquals(4000L, engagement);
    }

    @Test
    void runnerRejectsInvalidThreadCount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ConcurrentEventRunner(0)
        );
    }

    @Test
    void runnerRejectsInvalidProducerCount() {
        try (ConcurrentEventRunner runner =
                     new ConcurrentEventRunner(2)) {

            assertThrows(
                    IllegalArgumentException.class,
                    () -> runner.runProducers(0, 100, (id, index) -> {})
            );
        }
    }
}
