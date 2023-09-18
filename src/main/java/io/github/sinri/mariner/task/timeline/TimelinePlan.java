package io.github.sinri.mariner.task.timeline;

public interface TimelinePlan {
    static TimelinePlan oneShot(Runnable runnable) {
        return new TimelinePlan() {
            @Override
            public boolean isRepeated() {
                return false;
            }

            @Override
            public boolean shouldRunAt(long scheduledTime) {
                return true;
            }

            @Override
            public void run() {
                runnable.run();
            }
        };
    }

    boolean isRepeated();

    default void declareOneRepeat(long scheduledTime) {
    }

    boolean shouldRunAt(long scheduledTime);

    void run();
}
