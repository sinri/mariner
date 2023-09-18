package io.github.sinri.mariner.task.timeline;

import java.util.UUID;
import java.util.concurrent.*;

public class Timeline {
    private static Timeline instance;
    private final TimeUnit scheduleTimeUnit = TimeUnit.MILLISECONDS;
    private final ScheduledExecutorService scheduler;

    private final ExecutorService workerPool;

    private final ConcurrentMap<String, TimelinePlan> plans = new ConcurrentHashMap<>();
    private long schedulePeriod = 100;

    private Timeline() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.workerPool = Executors.newCachedThreadPool();
    }

    private static Timeline getInstance() {
        if (instance == null) {
            System.out.println("[DEBUG] to initialize Timeline");
            instance = new Timeline();
        }
        return instance;
    }

    public static void start() {
        getInstance().startTimeline();
    }

    public static void start(long schedulePeriod) {
        getInstance().schedulePeriod = schedulePeriod;
        getInstance().startTimeline();
    }

    public static void stop() {
        getInstance().stopTimeline();
    }

    public static void register(TimelinePlan plan) {
        getInstance().plans.put(UUID.randomUUID().toString(), plan);
    }

    private void startTimeline() {
        this.scheduler.scheduleAtFixedRate(this::scheduledRoutine, 0, this.schedulePeriod, this.scheduleTimeUnit);
    }

    private void stopTimeline() {
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

    private void scheduledRoutine() {
        final long scheduledTime = System.currentTimeMillis();

        this.plans.keySet().forEach(key -> {
            TimelinePlan plan = this.plans.get(key);

            if (plan.shouldRunAt(scheduledTime)) {
                if (plan.isRepeated()) {
                    plan.declareOneRepeat(scheduledTime);
                } else {
                    this.plans.remove(key);
                }

                this.workerPool.execute(plan::run);
            }
        });
    }
}
