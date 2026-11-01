package socialflow.observer;

public interface Subject<E extends SocialEvent> {

    void subscribe(EventType type, Observer<? super E> observer);

    void unsubscribe(EventType type, Observer<? super E> observer);

    void publish(E event);
}
