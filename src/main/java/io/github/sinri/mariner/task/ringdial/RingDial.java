package io.github.sinri.mariner.task.ringdial;

import java.util.Calendar;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class RingDial {
    private final ScheduledExecutorService scheduler;

    private final ExecutorService workerPool;

    public RingDial() {
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
