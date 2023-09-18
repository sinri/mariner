package io.github.sinri.mariner.task.timeline;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ChainedTaskResultTransformer {

    private ChainedTaskResult baseTaskResult = ChainedTaskResult.doneTask(null);
    private Function<ChainedTaskResult, ChainedTaskResult> func = null;
    private ChainedTaskResultTransformer previousTransformer;
    private ChainedTaskResultTransformer nextTransformer;
    private ChainedTaskResultTransformer() {
    }

    public static ChainedTaskResultTransformer create(Object seed) {
        ChainedTaskResultTransformer transformer = new ChainedTaskResultTransformer();
        transformer.baseTaskResult = ChainedTaskResult.doneTask(seed);
        return transformer;
    }

    public ChainedTaskResultTransformer addNext(ChainedTaskResultTransformer nextTransformer) {
        this.nextTransformer = nextTransformer;
        nextTransformer.previousTransformer = this;
        return this.nextTransformer;
    }

    public ChainedTaskResultTransformer addNext(Function<ChainedTaskResult, ChainedTaskResult> nextFunc) {
        ChainedTaskResultTransformer transformer = new ChainedTaskResultTransformer();
        transformer.func = nextFunc;
        transformer.previousTransformer = this;

        this.nextTransformer = transformer;
        return this.nextTransformer;
    }

    public ChainedTaskResultTransformer addNext(BiFunction<Object, Throwable, ChainedTaskResult> nextFunc) {
        ChainedTaskResultTransformer transformer = new ChainedTaskResultTransformer();
        transformer.func = new Function<ChainedTaskResult, ChainedTaskResult>() {
            @Override
            public ChainedTaskResult apply(ChainedTaskResult chainedTaskResult) {
                return nextFunc.apply(
                        chainedTaskResult.isDone() ? chainedTaskResult.getResult() : null,
                        chainedTaskResult.isFailed() ? chainedTaskResult.getFailure() : null
                );
            }
        };
        transformer.previousTransformer = this;

        this.nextTransformer = transformer;
        return this.nextTransformer;
    }

    // run this in a worker thread
    public void fire() {
        ChainedTaskResultTransformer t = this;
        while (t.previousTransformer != null) {
            t = t.previousTransformer;
        }

        ChainedTaskResult chainedTaskResult;
        if (t.func != null) {
            chainedTaskResult = t.func.apply(t.baseTaskResult);
        } else {
            chainedTaskResult = t.baseTaskResult;
        }
        while (t.nextTransformer != null) {
            t = t.nextTransformer;

            t.baseTaskResult = chainedTaskResult;
            if (t.func != null) {
                chainedTaskResult = t.func.apply(chainedTaskResult);
            }
        }
    }


}
