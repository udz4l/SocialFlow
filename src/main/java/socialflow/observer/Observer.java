package socialflow.observer;

@FunctionalInterface
public interface Observer<E extends SocialEvent> {

    void onEvent(E event);
}
