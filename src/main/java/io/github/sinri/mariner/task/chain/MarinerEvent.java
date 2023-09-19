package io.github.sinri.mariner.task.chain;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class MarinerEvent {
    private final String resultId;
    private boolean done = false;
    private Object result = null;
    private boolean failed = false;
    private Throwable failure = null;

    public MarinerEvent() {
        this.resultId = UUID.randomUUID().toString();
    }

    public static MarinerEvent withResult(Object result, long delay, TimeUnit unit) {
        return MarinerEventChain.getInstance().registerHead(
                () -> result,
                delay,
                unit
        );
    }

    public static MarinerEvent withResult(Object result) {
        return MarinerEventChain.getInstance().registerHead(
                () -> result,
                0,
                TimeUnit.MILLISECONDS
        );
    }

    public static MarinerEvent withFailure(Throwable failure) {
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

    public Object getResult() {
        return result;
    }

    public Throwable getFailure() {
        return failure;
    }

    void declareDone(Object result) {
        this.done = true;
        this.result = result;
    }

    void declareFailed(Throwable failure) {
        this.failed = true;
        this.failure = failure;
    }

    /**
     * @param doneFunc   when input event done: if this function is not null, execute it with the result of input event to generate result for output event; or use result of input event directly.
     * @param failedFunc when input event failed: if this function is not null, execute it with the failure of input event to generate result for output event; or wrap the failure of input event and throw out.
     * @return the output event handled
     */
    public MarinerEvent handleEvent(Function<Object, Object> doneFunc, Function<Throwable, Object> failedFunc) {
        return handleEvent(r -> {
            if (r.isDone()) {
                try {
                    if (doneFunc != null) {
                        return doneFunc.apply(r.getResult());
                    } else {
                        return r.getResult();
                    }
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            }
            if (r.isFailed()) {
                try {
                    if (failedFunc != null) {
                        return failedFunc.apply(r.getFailure());
                    } else {
                        throw new RuntimeException(r.getFailure());
                    }
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            }
            throw new RuntimeException("NEVER THIS");
        });
    }


    public MarinerEvent handleEvent(Function<MarinerEvent, Object> func) {
        return MarinerEventChain.getInstance().registerTail(this, func);
    }

    public MarinerEvent handleEventResult(Function<Object, Object> doneFunc) {
        return this.handleEvent(doneFunc, null);
    }

    public MarinerEvent handleEventFailure(Function<Throwable, Object> failedFunc) {
        return this.handleEvent(null, failedFunc);
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
