package socialflow.observer.events;

import socialflow.domain.model.Post;
import socialflow.observer.EventType;
import socialflow.observer.SocialEvent;

import java.time.Instant;
import java.util.Objects;

public record PostCreatedEvent(
        Post post,
        Instant occurredAt
) implements SocialEvent {

    public PostCreatedEvent {
        Objects.requireNonNull(post);
        Objects.requireNonNull(occurredAt);
    }

    @Override
    public EventType type() {
        return EventType.POST_CREATED;
    }
}
