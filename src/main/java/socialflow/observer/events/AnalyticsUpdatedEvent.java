package socialflow.observer.events;

import socialflow.observer.EventType;
import socialflow.observer.SocialEvent;

import java.time.Instant;
import java.util.Objects;

public record AnalyticsUpdatedEvent(
        long totalPosts,
        long totalReactions,
        Instant occurredAt
) implements SocialEvent {

    public AnalyticsUpdatedEvent {
        if (totalPosts < 0 || totalReactions < 0) {
            throw new IllegalArgumentException("Analytics cannot be negative");
        }

        Objects.requireNonNull(occurredAt);
    }

    @Override
    public EventType type() {
        return EventType.ANALYTICS_UPDATED;
    }
}
