package socialflow.observer;

import java.time.Instant;

public interface SocialEvent {

    EventType type();

    Instant occurredAt();
}
