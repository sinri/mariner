package io.github.sinri.mariner.task.chain;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class MarinerEvent<T> {
    private final String resultId;
    private boolean done = false;
    private T result = null;
    private boolean failed = false;
    private Throwable failure = null;

    public MarinerEvent() {
        this.resultId = UUID.randomUUID().toString();
    }

    public static <R> MarinerEvent<R> withResult(R result, long delay, TimeUnit unit) {
        return MarinerEventChain.getInstance().registerHead(
                () -> result,
                delay,
                unit
        );
    }

    public static <R> MarinerEvent<R> withResult(R result) {
        return MarinerEventChain.getInstance().registerHead(() -> result);
    }

    public static MarinerEvent<Object> withFailure(Throwable failure) {
        return MarinerEventChain.getInstance().registerHead(
                () -> {
                    throw new RuntimeException(failure);
                },
                0,
                TimeUnit.MILLISECONDS
        );
    }

    public String getResultId() {
        return resultId;
    }

    public boolean isFailed() {
        return failed;
    }

    public boolean isDone() {
        return done;
    }

    public T getResult() {
        return result;
    }

    public Throwable getFailure() {
        return failure;
    }

    void declareDone(T result) {
        this.done = true;
        this.result = result;
    }

    void declareFailed(Throwable failure) {
        this.failed = true;
        this.failure = failure;
    }

    /**
     * @param doneFunc   when input event done: execute it with the result of input event to generate result for output event;
     * @param failedFunc when input event failed: execute it with the failure of input event to generate result for output event.
     * @return the output event handled
     */
    public <R> MarinerEvent<R> handleEvent(@NotNull Function<T, R> doneFunc, @NotNull Function<Throwable, R> failedFunc) {
        return handleEvent(r -> {
            if (r.isDone()) {
                try {
                    return doneFunc.apply(r.getResult());
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            }
            if (r.isFailed()) {
                try {
                    return failedFunc.apply(r.getFailure());
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            }
            throw new RuntimeException("NEVER THIS");
        });
    }


    public <R> MarinerEvent<R> handleEvent(Function<MarinerEvent<T>, R> func) {
        return MarinerEventChain.getInstance().registerTail(this, func);
    }

    public <R> MarinerEvent<R> handleEventResult(Function<T, R> doneFunc) {
        return this.handleEvent(doneFunc, throwable -> null);
    }

    public MarinerEvent<T> handleEventFailure(Function<Throwable, T> failedFunc) {
        return this.handleEvent(t -> getResult(), failedFunc);
    }


    @Override
    public String toString() {
        return "Result{" +
                "resultId='" + resultId + '\'' +
                ", done=" + done +
                ", result=" + result +
                ", failed=" + failed +
                ", failure=" + failure +
                '}';
    }
}
