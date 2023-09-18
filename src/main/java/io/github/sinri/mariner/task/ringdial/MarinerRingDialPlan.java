package io.github.sinri.mariner.task.ringdial;

public interface MarinerRingDialPlan extends Runnable {
    String key();

    MarinerCronExpression cronExpression();
}
