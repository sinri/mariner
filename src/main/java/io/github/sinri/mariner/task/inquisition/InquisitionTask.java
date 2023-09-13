package io.github.sinri.mariner.task.inquisition;

import org.jetbrains.annotations.NotNull;

public abstract class InquisitionTask implements Runnable, Comparable<InquisitionTask> {

    public static final int PRIORITY_TASK_BASE = 20;
    public static final int PRIORITY_CALLBACK_BASE = 10;

    public InquisitionTask() {

    }


    abstract public String getReference();

    abstract public Integer getPriority();

    abstract public long getAppliedTime();

    @Override
    public int compareTo(@NotNull InquisitionTask o) {
        int x = Integer.compare(o.getPriority(), this.getPriority());
        if (x == 0) {
            x = Long.compare(o.getAppliedTime(), this.getAppliedTime());
        }
        return x;
    }
}
