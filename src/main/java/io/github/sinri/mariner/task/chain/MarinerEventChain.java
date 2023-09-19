package io.github.sinri.mariner.task.chain;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public class MarinerEventChain {
    private static MarinerEventChain instance;
    private final ScheduledExecutorService scheduler;
    private final EventRelationship relationship;

    private MarinerEventChain(int corePoolSize) {
        this.scheduler = Executors.newScheduledThreadPool(corePoolSize);
        this.relationship = new EventRelationship();
    }

    public static void start() {
        instance = new MarinerEventChain(1);
    }

    static MarinerEventChain getInstance() {
        return instance;
    }

    <I, O> void execute(EventHandler<I, O> resultGenerator) {
        this.scheduler.execute(resultGenerator);
    }

    void notifyConsumersWhenResultConfirmed(String resultId) {
        this.scheduler.schedule(() -> relationship.callHandlersWhenEventFinished(resultId), 100, TimeUnit.MILLISECONDS);
//        this.relationship.callHandlersWhenEventFinished(resultId);
    }

    public static void stop() {
        instance.stopScheduler();
    }

    <T> MarinerEvent<T> registerHead(Supplier<T> supplier) {
        System.out.println("io.github.sinri.mariner.task.chain.MarinerEventChain.registerHead(java.util.function.Supplier<T>)");
        EventHandler<Object, T> resultGenerator = new EventHandler<>(result -> supplier.get());
        this.scheduler.execute(resultGenerator);
        return resultGenerator.getOutputEvent();
    }

    <T> MarinerEvent<T> registerHead(Supplier<T> supplier, long delay, TimeUnit unit) {
        System.out.println("io.github.sinri.mariner.task.chain.MarinerEventChain.registerHead(java.util.function.Supplier<T>, long, java.util.concurrent.TimeUnit)");
        EventHandler<Object, T> resultGenerator = new EventHandler<>(result -> supplier.get());
        this.scheduler.schedule(resultGenerator, delay, unit);
        return resultGenerator.getOutputEvent();
    }

    <I, O> MarinerEvent<O> registerTail(MarinerEvent<I> previousResult, Function<MarinerEvent<I>, O> func) {
        System.out.println("io.github.sinri.mariner.task.chain.MarinerEventChain.registerTail");
        EventHandler<I, O> resultGenerator = new EventHandler<>(previousResult, func);
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
