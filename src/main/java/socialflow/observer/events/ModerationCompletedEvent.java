package socialflow.observer.events;

import socialflow.domain.visitor.impl.ModerationReport;
import socialflow.observer.EventType;
import socialflow.observer.SocialEvent;

import java.time.Instant;
import java.util.Objects;

public record ModerationCompletedEvent(
        String postId,
        ModerationReport report,
        Instant occurredAt
) implements SocialEvent {

    public ModerationCompletedEvent {
        Objects.requireNonNull(postId);
        Objects.requireNonNull(report);
        Objects.requireNonNull(occurredAt);
    }

    @Override
    public EventType type() {
        return EventType.MODERATION_COMPLETED;
    }
}