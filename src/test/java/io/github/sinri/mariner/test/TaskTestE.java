package io.github.sinri.mariner.test;

import io.github.sinri.mariner.task.timeline.ChainedTaskResult;
import io.github.sinri.mariner.task.timeline.ChainedTaskResultTransformer;
import io.github.sinri.mariner.task.timeline.Timeline;

import java.util.function.Function;

public class TaskTestE {
    public static void main(String[] args) {
        Timeline.start();

        Function<ChainedTaskResult, ChainedTaskResult> nextFunc = lastResult -> {
            if (lastResult.isDone()) {
                System.out.println("last result is " + lastResult.getResult());
                return ChainedTaskResult.doneTask((int) (lastResult.getResult()) + 1);
            } else {
                System.out.println("last result failed as " + lastResult.getFailure());
                return ChainedTaskResult.doneTask(0);
            }
        };

//        ChainedTaskResultTransformer transformer = ChainedTaskResultTransformer.create(1)
//                .addNext(nextFunc)
//                .addNext(nextFunc);

        ChainedTaskResultTransformer.create(1)
                .addNext((r, e) -> {
                    if (e != null) {
                        int x = (int) r + 1;
                        System.out.println("r is " + r + " and x is " + x);
                        return ChainedTaskResult.doneTask(x);
                    } else {
                        System.out.println("e: " + e);
                        return ChainedTaskResult.doneTask(0);
                    }
                })
                .fire();

    }
}
