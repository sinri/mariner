package io.github.sinri.mariner.task;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;

@Deprecated
public class Apostle {
    private final ExecutorService executorService;
    private final ExecutorService callbackService;
    private final ScheduledExecutorService witnessService;

    private final Map<String, Believer> believerMap;

    public Apostle(int nThreads) {
        this.believerMap = new ConcurrentHashMap<>();
        this.executorService = Executors.newFixedThreadPool(nThreads);
        this.witnessService = Executors.newSingleThreadScheduledExecutor();
        this.callbackService = Executors.newWorkStealingPool(nThreads);

        Runnable witnessRunnable = new Runnable() {
            @Override
            public void run() {
                witnessRoutine();
                witnessService.schedule(this, 1, TimeUnit.SECONDS);
            }
        };

        this.witnessService.schedule(witnessRunnable, 1, TimeUnit.SECONDS);
    }

    public void shutdown() {
        this.executorService.shutdown();
    }

    public void shutdownAndWait() {
        this.shutdown();

        boolean isOver = false;
        while (!isOver) {
            try {
                isOver = this.executorService.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                // 线程中断了，基本也死翘翘了，不管了
                break;
            }
        }

        this.callbackService.shutdown();
        isOver = false;
        while (!isOver) {
            try {
                isOver = this.callbackService.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        this.witnessService.shutdown();

//        while (!this.executorService.isTerminated()) {
//            // Wait until all threads are finish,and also you can use "executor.awaitTermination();" to wait
//        }
    }

    public void submitTask(Callable<Object> callable) {
        submitTask(callable, null);
    }

    /**
     * @param callable        在线程池中异步运作的作业
     * @param completeHandler 作业完成之后预定要执行的动作，同样也是扔进线程池处理的
     */
    public void submitTask(Callable<Object> callable, Consumer<Object> completeHandler) {
        Future<Object> future = this.executorService.submit(callable);
        String sn = UUID.randomUUID().toString();
        Believer believer = new Believer(future);
        believer.setCompleteHandler(completeHandler);
        this.believerMap.put(sn, believer);
    }

    private void witnessRoutine() {
        this.believerMap.keySet().forEach(sn -> {
            try {
                Believer believer = this.believerMap.get(sn);

                if (believer == null) {
                    this.believerMap.remove(sn);
                } else if (believer.isOver()) {
                    this.believerMap.remove(sn);

                    Callable<Object> paradisePlan = believer.getParadisePlan();
                    if (paradisePlan != null) {
                        //this.submitTask(paradisePlan);
                        this.callbackService.submit(paradisePlan);
                    }
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                // continue
            }
        });
    }

}
