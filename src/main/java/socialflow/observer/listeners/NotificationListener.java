package socialflow.observer.listeners;

import socialflow.observer.Observer;
import socialflow.observer.SocialEvent;
import socialflow.observer.events.PostCreatedEvent;
import socialflow.observer.events.ReactionAddedEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationListener implements Observer<SocialEvent> {

    private final List<String> notifications =
            new CopyOnWriteArrayList<>();

    @Override
    public void onEvent(SocialEvent event) {
        if (event instanceof PostCreatedEvent postEvent) {
            notifications.add(
                    "New post by " + postEvent.post().author()
            );
        }

        if (event instanceof ReactionAddedEvent reactionEvent) {
            notifications.add(
                    reactionEvent.reaction().userId()
                            + " reacted to post "
                            + reactionEvent.post().id()
            );
        }
    }

    public List<String> getNotifications() {
        return List.copyOf(notifications);
    }
}
