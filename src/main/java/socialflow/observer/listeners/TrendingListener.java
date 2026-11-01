package socialflow.observer.listeners;

import socialflow.domain.model.ReactionType;
import socialflow.observer.Observer;
import socialflow.observer.SocialEvent;
import socialflow.observer.events.ReactionAddedEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class TrendingListener implements Observer<SocialEvent> {

    private final Map<ReactionType, LongAdder> reactionCounts =
            new ConcurrentHashMap<>();

    @Override
    public void onEvent(SocialEvent event) {
        if (event instanceof ReactionAddedEvent reactionEvent) {
            reactionCounts.computeIfAbsent(
                    reactionEvent.reaction().type(),
                    ignored -> new LongAdder()
            ).increment();
        }
    }

    public long getCount(ReactionType type) {
        LongAdder counter = reactionCounts.get(type);
        return counter == null ? 0 : counter.sum();
    }

    public Map<ReactionType, Long> getStatistics() {
        Map<ReactionType, Long> result = new ConcurrentHashMap<>();

        reactionCounts.forEach(
                (type, counter) -> result.put(type, counter.sum())
        );

        return Map.copyOf(result);
    }
}
