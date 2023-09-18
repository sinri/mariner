package io.github.sinri.mariner.test;

import io.github.sinri.mariner.task.ringdial.MarinerCronExpression;
import io.github.sinri.mariner.task.ringdial.MarinerRingDial;
import io.github.sinri.mariner.task.ringdial.MarinerRingDialPlan;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public class TaskTestB {
    public static void main(String[] args) {
        MarinerRingDial ringDial = new RingDialImpl();
        ringDial.start();
    }

    private static class RingDialImpl extends MarinerRingDial {

        @Override
        protected Collection<MarinerRingDialPlan> fetchPlans() {
            return List.of(
                    new MarinerRingDialPlanImpl("every 2 minutes", new MarinerCronExpression("*/2 * * * *"))
            );
        }
    }

    private static class MarinerRingDialPlanImpl implements MarinerRingDialPlan {
        private final String key;
        private final MarinerCronExpression cronExpression;

        public MarinerRingDialPlanImpl(String key, MarinerCronExpression cronExpression) {
            this.key = key();
            this.cronExpression = cronExpression;
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public MarinerCronExpression cronExpression() {
            return cronExpression;
        }

        @Override
        public void run() {
            System.out.println(new Date() + " run " + key + " for " + cronExpression.getRawCronExpression());
        }
    }
}
