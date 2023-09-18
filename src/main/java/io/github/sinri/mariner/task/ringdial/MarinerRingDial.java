package io.github.sinri.mariner.task.ringdial;

import java.util.Calendar;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 致敬大航海时代的计时工具。
 * 定时任务装置，由一个分钟维度调度器和一个无限大小的线程池组成。
 */
public abstract class MarinerRingDial {
    private final ScheduledExecutorService scheduler;

    private final ExecutorService workerPool;

    public MarinerRingDial() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.workerPool = Executors.newCachedThreadPool();
    }

    abstract protected Collection<MarinerRingDialPlan> fetchPlans();

    private void handleEveryMinute(Calendar now) {
        Collection<MarinerRingDialPlan> plans = fetchPlans();
        plans.forEach(plan -> {
            if (plan.cronExpression().match(now)) {
                this.workerPool.execute(plan);
            }
        });
    }

    public void start() {
        this.scheduler.scheduleAtFixedRate(() -> {
            Calendar calendar = Calendar.getInstance();
            handleEveryMinute(calendar);
        }, 0, 1, TimeUnit.MINUTES);
    }

    public void stop() {
        this.scheduler.shutdown();
        while (true) {
            try {
                if (this.scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                    break;
                }
            } catch (InterruptedException e) {
                break;
            }
        }
        this.workerPool.shutdown();
        while (true) {
            try {
                if (this.workerPool.awaitTermination(1, TimeUnit.SECONDS)) {
                    break;
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
