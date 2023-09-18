package io.github.sinri.mariner.test;

import io.github.sinri.mariner.task.inquisition.MarinerInquisitionTask;
import io.github.sinri.mariner.task.inquisition.MarinerInquisitor;

public class TaskTestA {
    public static void main(String[] args) throws InterruptedException {
        MarinerInquisitor inquisitor = new MarinerInquisitor(2, 4, 5);

        for (int i = 0; i < 20; i++) {
            inquisitor.submitTask(new InquisitionImpl(i, 100));
        }

        Thread.sleep(10_000L);
        System.out.println(System.currentTimeMillis() + " | TO SHUTDOWN");
        inquisitor.shutdownAndWait();
        System.out.println(System.currentTimeMillis() + " | NOW SHUTDOWN");
    }

    private static class InquisitionImpl extends MarinerInquisitionTask {
        private final int inquisitionId;
        private final int priority;
        private final long appliedTime;

        public InquisitionImpl(int inquisitionId, int priority) {
            super();
            this.inquisitionId = inquisitionId;
            this.priority = priority;
            this.appliedTime = System.currentTimeMillis();
        }

        @Override
        public long getAppliedTime() {
            return appliedTime;
        }

        @Override
        public String getReference() {
            return String.valueOf(inquisitionId);
        }

        @Override
        public int getPriority() {
            return priority;
        }

        @Override
        public void run() {
            long v = (long) (Math.random() * 5000);
            System.out.println(System.currentTimeMillis() + " | CALL [" + inquisitionId + "] <" + appliedTime + "> start, time is " + v);
            try {
                Thread.sleep(v);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(System.currentTimeMillis() + " | CALL [" + inquisitionId + "]<" + appliedTime + "> end");
        }
    }
}
