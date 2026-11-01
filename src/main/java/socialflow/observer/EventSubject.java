package socialflow.observer;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class EventSubject<E extends SocialEvent> implements Subject<E> {

    private final ConcurrentHashMap<EventType, CopyOnWriteArraySet<Observer<? super E>>>
            observers = new ConcurrentHashMap<>();

    @Override
    public void subscribe(EventType type, Observer<? super E> observer) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(observer);

        observers.computeIfAbsent(
                type,
                ignored -> new CopyOnWriteArraySet<>()
        ).add(observer);
    }

    @Override
    public void unsubscribe(EventType type, Observer<? super E> observer) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(observer);

        Set<Observer<? super E>> subscribers = observers.get(type);

        if (subscribers != null) {
            subscribers.remove(observer);
        }
    }

    @Override
    public void publish(E event) {
        Objects.requireNonNull(event);

        Set<Observer<? super E>> subscribers =
                observers.get(event.type());

        if (subscribers == null) {
            return;
        }

        for (Observer<? super E> observer : subscribers) {
            observer.onEvent(event);
        }
    }

    public int subscriberCount(EventType type) {
        Set<Observer<? super E>> subscribers = observers.get(type);

        return subscribers == null ? 0 : subscribers.size();
    }
}
