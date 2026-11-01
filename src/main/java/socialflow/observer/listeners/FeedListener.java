package socialflow.observer.listeners;

import socialflow.domain.model.Post;
import socialflow.observer.Observer;
import socialflow.observer.SocialEvent;
import socialflow.observer.events.PostCreatedEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FeedListener implements Observer<SocialEvent> {

    private final List<Post> feed = new CopyOnWriteArrayList<>();

    @Override
    public void onEvent(SocialEvent event) {
        if (event instanceof PostCreatedEvent postEvent) {
            feed.add(postEvent.post());
        }
    }

    public List<Post> getFeed() {
        return List.copyOf(feed);
    }
}
