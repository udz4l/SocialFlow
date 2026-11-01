package socialflow.observer.listeners;

import org.junit.jupiter.api.Test;
import socialflow.domain.model.Post;
import socialflow.domain.visitor.impl.ModerationReportVisitor;
import socialflow.observer.events.PostCreatedEvent;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModerationIntegrationTest {

    @Test
    void postCreationShouldTriggerModerationAndPublishReport() {
        ModerationListener moderationListener =
                new ModerationListener(
                        new ModerationReportVisitor(
                                Set.of("spam", "scam", "toxic")
                        )
                );

        ModerationResultListener resultListener =
                new ModerationResultListener();

        moderationListener.subscribe(
                socialflow.observer.EventType.MODERATION_COMPLETED,
                resultListener
        );

        Post post = new Post(
                "post-1",
                "Alice",
                "This post contains spam content",
                List.of()
        );

        moderationListener.onEvent(
                new PostCreatedEvent(post, Instant.now())
        );

        assertEquals(1, resultListener.reports().size());

        assertEquals(
                1,
                resultListener.reports().getFirst().flaggedCount()
        );
    }
}