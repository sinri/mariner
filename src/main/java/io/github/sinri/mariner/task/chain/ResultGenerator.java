package io.github.sinri.mariner.task.chain;

import java.util.function.Consumer;
import java.util.function.Function;

class ResultGenerator implements Runnable, Consumer<Result> {
    private final MarinerChain timer;
    private final Function<Result, Object> func;
    private final Result result;
    private Result previousResult = null;

    public ResultGenerator(MarinerChain timer, Result previousResult, Function<Result, Object> func) {
        this.result = new Result(timer);
        this.timer = timer;
        this.previousResult = previousResult;
        this.func = func;
    }

    public ResultGenerator(MarinerChain timer, Function<Result, Object> func) {
        this(timer, null, func);
    }

    @Override
    public void run() {
        accept(this.previousResult);
        this.timer.notifyConsumersWhenResultConfirmed(this.getResult().getResultId());
    }

    public Result getResult() {
        return result;
    }

    @Override
    public void accept(Result result) {
        try {
            Object o = this.func.apply(result);
            this.result.declareDone(o);
        } catch (Exception e) {
            this.result.declareFailed(e);
        }
    }
}
