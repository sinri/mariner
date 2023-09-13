package io.github.sinri.mariner.task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

@Deprecated
public class Believer {
    private final Future<Object> future;
    private Consumer<Object> completeHandler = null;

    public Believer(Future<Object> future) {
        this.future = future;
    }


    public Believer setCompleteHandler(Consumer<Object> completeHandler) {
        this.completeHandler = completeHandler;
        return this;
    }

    public Future<Object> getFuture() {
        return future;
    }

    public boolean isOver() {
        return future.isDone();
    }

    public void finalJudgement() throws ExecutionException, InterruptedException {
        if (future.isDone()) {
            if (this.completeHandler != null) {
                Object o;
                o = future.get();
                this.completeHandler.accept(o);
            }
        }
    }

    public Callable<Object> getParadisePlan() throws ExecutionException, InterruptedException {
        if (!future.isDone()) throw new RuntimeException();
        if (this.completeHandler == null) return null;
        Object o = future.get();
        return () -> {
            completeHandler.accept(o);
            return null;
        };
    }
}
