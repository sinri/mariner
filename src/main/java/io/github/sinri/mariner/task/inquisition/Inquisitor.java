package io.github.sinri.mariner.task.inquisition;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Inquisitor {

    private final ThreadPoolExecutor threadPoolExecutor;


    public Inquisitor(
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTimeInSecond
    ) {
        this.threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTimeInSecond,
                TimeUnit.SECONDS,
                new PriorityBlockingQueue<Runnable>()
        );
    }

    public void submitTask(InquisitionTask inquisitionTask) {
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

//        while (!this.executorService.isTerminated()) {
//            // Wait until all threads are finish,and also you can use "executor.awaitTermination();" to wait
//        }
    }
}
