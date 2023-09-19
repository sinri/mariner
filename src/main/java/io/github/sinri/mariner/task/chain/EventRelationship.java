package io.github.sinri.mariner.task.chain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

class EventRelationship {
    private final Map<String, List<EventHandler>> consumerMap = new ConcurrentHashMap<>();

    private final WeakHashMap<String, MarinerEvent> allResultMap = new WeakHashMap<>();

    void linkResultAndConsumer(MarinerEvent previousResult, EventHandler resultGenerator) {
        this.allResultMap.putIfAbsent(previousResult.getResultId(), previousResult);
        if (!previousResult.isDone() && !previousResult.isFailed()) {
            this.consumerMap.computeIfAbsent(previousResult.getResultId(), string -> new ArrayList<>())
                    .add(resultGenerator);
        } else {
            MarinerEventChain.getInstance().execute(resultGenerator);
        }

    }

    /**
     * Call this method when a result declared finished.
     *
     * @param resultId the ID of the result that just finished
     */
    void notifyConsumersWhenResultConfirmed(String resultId) {
        List<EventHandler> resultConsumers = this.consumerMap.remove(resultId);

        MarinerEvent result = allResultMap.get(resultId);

        if (resultConsumers != null) {
            resultConsumers.forEach(consumer -> {
                MarinerEventChain.getInstance().execute(consumer);
            });
        }
    }
}
