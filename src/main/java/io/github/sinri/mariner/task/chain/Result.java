package io.github.sinri.mariner.task.chain;

import java.util.UUID;
import java.util.function.Function;

public class Result {
    private final String resultId;
    private final MarinerChain chain;
    private boolean done = false;
    private Object result = null;
    private boolean failed = false;
    private Throwable failure = null;

    public Result(MarinerChain chain) {
        this.chain = chain;
        this.resultId = UUID.randomUUID().toString();
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

    public Result chain(Function<Result, Object> func) {
        return this.chain.registerTail(this, func);
    }

    public Result chainForDone(Function<Object, Object> doneFunc) {
        return this.chain.registerTail(this, r -> {
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
}
