package io.github.sinri.mariner.task.chain;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public class MarinerEventChain {
    private static final MarinerEventChain instance = new MarinerEventChain(1);
    private final ScheduledExecutorService scheduler;
    private final EventRelationship relationship;

    private MarinerEventChain(int corePoolSize) {
        this.scheduler = Executors.newScheduledThreadPool(corePoolSize);
        this.relationship = new EventRelationship();
    }

    static MarinerEventChain getInstance() {
        return instance;
    }

    void execute(EventHandler resultGenerator) {
        this.scheduler.execute(resultGenerator);
    }

    void notifyConsumersWhenResultConfirmed(String resultId) {
        this.relationship.callHandlersWhenEventFinished(resultId);
    }

    public static void stop() {
        instance.stopScheduler();
    }

    MarinerEvent registerHead(Supplier<Object> supplier, long delay, TimeUnit unit) {
        EventHandler resultGenerator = new EventHandler(result -> supplier.get());
        this.scheduler.schedule(resultGenerator, delay, unit);
        return resultGenerator.getOutputEvent();
    }

    MarinerEvent registerTail(MarinerEvent previousResult, Function<MarinerEvent, Object> func) {
        EventHandler resultGenerator = new EventHandler(previousResult, func);
        this.relationship.registerHandlerForEvent(previousResult, resultGenerator);
        return resultGenerator.getOutputEvent();
    }

    void stopScheduler() {
        this.scheduler.shutdown();
        while (true) {
            try {
                boolean terminated = this.scheduler.awaitTermination(1, TimeUnit.SECONDS);
                if (terminated) break;
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
