package io.github.sinri.mariner.task.inquisition;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 一个可有限扩展的线程池支撑的带优先级任务处理中心
 */
public class MarinerInquisitor {

    private final ThreadPoolExecutor threadPoolExecutor;

    /**
     * @param corePoolSize          基础线程池大小
     * @param maximumPoolSize       线程池最大的大小
     * @param keepAliveTimeInSecond 线程池中线程最长存活时间
     */
    public MarinerInquisitor(
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTimeInSecond
    ) {
        this.threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTimeInSecond,
                TimeUnit.SECONDS,
                new PriorityBlockingQueue<>()
        );
        this.threadPoolExecutor.allowCoreThreadTimeOut(true);
    }

    public void submitTask(MarinerInquisitionTask inquisitionTask) {
        this.threadPoolExecutor.execute(inquisitionTask);
    }

    public void shutdown() {
        this.threadPoolExecutor.shutdown();
    }

    public void shutdownAndWait() {
        this.shutdown();

        boolean isOver = false;
        while (!isOver) {
            try {
                isOver = this.threadPoolExecutor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                // 线程中断了，基本也死翘翘了，不管了
                break;
            }
        }
    }
}
