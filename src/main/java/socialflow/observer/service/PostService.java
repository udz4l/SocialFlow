package socialflow.observer.service;

import socialflow.domain.model.Post;
import socialflow.observer.Subject;
import socialflow.observer.SocialEvent;
import socialflow.observer.events.PostCreatedEvent;

import java.time.Instant;
import java.util.Objects;

public class PostService {

    private final Subject<SocialEvent> eventSubject;

    public PostService(Subject<SocialEvent> eventSubject) {
        this.eventSubject = Objects.requireNonNull(eventSubject);
    }

    public void publishPost(Post post) {
        eventSubject.publish(
                new PostCreatedEvent(post, Instant.now())
        );
    }
}
