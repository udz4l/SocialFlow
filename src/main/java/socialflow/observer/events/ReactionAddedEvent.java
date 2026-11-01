package socialflow.observer.events;

import socialflow.domain.model.Post;
import socialflow.domain.model.Reaction;
import socialflow.observer.EventType;
import socialflow.observer.SocialEvent;

import java.time.Instant;
import java.util.Objects;

public record ReactionAddedEvent(
        Post post,
        Reaction reaction,
        Instant occurredAt
) implements SocialEvent {

    public ReactionAddedEvent {
        Objects.requireNonNull(post);
        Objects.requireNonNull(reaction);
        Objects.requireNonNull(occurredAt);
    }

    @Override
    public EventType type() {
        return EventType.REACTION_ADDED;
    }
}
