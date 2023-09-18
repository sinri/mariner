package io.github.sinri.mariner.task.inquisition;

import org.jetbrains.annotations.NotNull;

/**
 * 带优先级的任务。
 */
public abstract class MarinerInquisitionTask implements Runnable, Comparable<MarinerInquisitionTask> {
    public MarinerInquisitionTask() {

    }

    /**
     * @return 任务的唯一业务识别引用标识符
     */
    abstract public String getReference();

    /**
     * 优先级数字小的首先运行。
     * @return 优先级
     */
    abstract public int getPriority();

    /**
     * 优先级相同时，先提交的先执行。
     * @return 任务提交时间戳
     */
    abstract public long getAppliedTime();

    @Override
    public int compareTo(@NotNull MarinerInquisitionTask o) {
        int x = Integer.compare(o.getPriority(), this.getPriority());
        if (x == 0) {
            x = Long.compare(o.getAppliedTime(), this.getAppliedTime());
        }
        return x;
    }
}
