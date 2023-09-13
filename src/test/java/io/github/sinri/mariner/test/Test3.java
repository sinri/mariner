package io.github.sinri.mariner.test;

import io.github.sinri.mariner.task.inquisition.InquisitionTask;
import io.github.sinri.mariner.task.inquisition.Inquisitor;

public class Test3 {
    public static void main(String[] args) throws InterruptedException {
        Inquisitor inquisitor = new Inquisitor(2, 4, 5);

        for (int i = 0; i < 20; i++) {
            inquisitor.submitTask(new InquisitionImpl(i, InquisitionTask.PRIORITY_TASK_BASE));
        }

        Thread.sleep(10_000L);
        System.out.println(System.currentTimeMillis() + " | TO SHUTDOWN");
        inquisitor.shutdownAndWait();
        System.out.println(System.currentTimeMillis() + " | NOW SHUTDOWN");
    }

    private static class InquisitionImpl extends InquisitionTask {
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
        public Integer getPriority() {
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
