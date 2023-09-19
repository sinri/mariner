package io.github.sinri.mariner.task.chain;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class MarinerEvent {
    public MarinerEvent() {
        this.resultId = UUID.randomUUID().toString();
    }

    public static MarinerEvent withResult(Object result) {
        return MarinerEventChain.getInstance().registerHead(
                () -> result,
                0,
                TimeUnit.MILLISECONDS
        );
    }

    private final String resultId;
    private boolean done = false;
    private Object result = null;
    private boolean failed = false;
    private Throwable failure = null;

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

    public MarinerEvent handleEvent(Function<MarinerEvent, Object> func) {
        return MarinerEventChain.getInstance().registerTail(this, func);
    }

    public MarinerEvent handleEventResult(Function<Object, Object> doneFunc) {
        return MarinerEventChain.getInstance().registerTail(this, r -> {
            if (r.isDone()) {
                try {
                    return doneFunc.apply(r.getResult());
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            }
            throw new RuntimeException(r.getFailure());
        });
    }

    public MarinerEvent handleEventFailure(Function<Throwable, Object> failedFunc) {
        return MarinerEventChain.getInstance().registerTail(this, r -> {
            if (r.isFailed()) {
                try {
                    return failedFunc.apply(r.getFailure());
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            }
            return this.getResult();
        });
    }

    public MarinerEvent handle(Function<Object, Object> doneFunc, Function<Throwable, Object> failedFunc) {
        return MarinerEventChain.getInstance().registerTail(this, r -> {
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
