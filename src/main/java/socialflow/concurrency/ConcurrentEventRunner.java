package socialflow.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class ConcurrentEventRunner implements AutoCloseable {

    private final ExecutorService executor;

    public ConcurrentEventRunner(int threadCount) {
        if (threadCount < 1) {
            throw new IllegalArgumentException(
                    "Thread count must be positive"
            );
        }

        this.executor = Executors.newFixedThreadPool(threadCount);
    }

    public void runProducers(
            int producerCount,
            int eventsPerProducer,
            BiConsumer<Integer, Integer> eventAction
    ) {
        if (producerCount < 1 || eventsPerProducer < 0) {
            throw new IllegalArgumentException(
                    "Invalid producer or event count"
            );
        }

        Objects.requireNonNull(eventAction);

        List<Future<?>> futures = new ArrayList<>();

        for (int producerId = 0; producerId < producerCount; producerId++) {
            int currentProducerId = producerId;

            Future<?> future = executor.submit(() -> {
                for (int eventIndex = 0;
                     eventIndex < eventsPerProducer;
                     eventIndex++) {

                    eventAction.accept(
                            currentProducerId,
                            eventIndex
                    );
                }
            });

            futures.add(future);
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException exception) {
                futures.forEach(task -> task.cancel(true));
                Thread.currentThread().interrupt();

                throw new IllegalStateException(
                        "Event processing was interrupted",
                        exception
                );
            } catch (ExecutionException exception) {
                throw new IllegalStateException(
                        "Producer failed",
                        exception.getCause()
                );
            }
        }
    }

    @Override
    public void close() {
        executor.shutdown();

        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();

                if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                    throw new IllegalStateException(
                            "Executor did not terminate"
                    );
                }
            }
        } catch (InterruptedException exception) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Executor shutdown was interrupted",
                    exception
            );
        }
    }
}
