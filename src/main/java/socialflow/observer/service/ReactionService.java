package socialflow.observer.service;

import socialflow.domain.model.Post;
import socialflow.domain.model.Reaction;
import socialflow.observer.Subject;
import socialflow.observer.SocialEvent;
import socialflow.observer.events.ReactionAddedEvent;

import java.time.Instant;
import java.util.Objects;

public class ReactionService {

    private final Subject<SocialEvent> eventSubject;

    public ReactionService(Subject<SocialEvent> eventSubject) {
        this.eventSubject = Objects.requireNonNull(eventSubject);
    }

    public void addReaction(Post post, Reaction reaction) {
        eventSubject.publish(
                new ReactionAddedEvent(post, reaction, Instant.now())
        );
    }
}
