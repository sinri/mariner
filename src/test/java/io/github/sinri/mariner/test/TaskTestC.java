package io.github.sinri.mariner.test;

import io.github.sinri.mariner.task.timeline.Timeline;
import io.github.sinri.mariner.task.timeline.TimelinePlan;

import java.util.function.Function;

public class TaskTestC {
    public static void main(String[] args) throws InterruptedException {
        Timeline.start();

        Timeline.register(new TimelinePlanImpl());
        Timeline.register(new TimelinePlanImpl());

        Thread.sleep(1000L);
        Timeline.stop();
    }

    private static class TimelinePlanImpl implements TimelinePlan {

        @Override
        public boolean isRepeated() {
            return false;
        }

        @Override
        public void declareOneRepeat(long scheduledTime) {
            // pass
        }

        @Override
        public boolean shouldRunAt(long scheduledTime) {
            return true;
        }

        @Override
        public void run() {
            System.out.println("[" + System.currentTimeMillis() + "] PLAN EXECUTED");
        }
    }

    private static class Piece<T> implements TimelinePlan {
        private final Function<Void, T> func;

        public Piece(Function<Void, T> func) {
            this.func = func;
        }

        @Override
        public boolean isRepeated() {
            return false;
        }

        @Override
        public void declareOneRepeat(long scheduledTime) {

        }

        @Override
        public boolean shouldRunAt(long scheduledTime) {
            return true;
        }

        @Override
        public void run() {
            System.out.println("[" + System.currentTimeMillis() + "] PIECE EXECUTED");

            T t = executeForResult();


        }

        protected T executeForResult() {
            T t = this.func.apply(null);
            return t;
        }
    }
}
