package io.github.sinri.mariner.task.chain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class EventRelationship {
    private final Map<String, List<EventHandler<?, ?>>> eventHandlersMap = new ConcurrentHashMap<>();

    <I, O> void registerHandlerForEvent(MarinerEvent<I> inputEvent, EventHandler<I, O> eventHandler) {
        System.out.println("io.github.sinri.mariner.task.chain.EventRelationship.registerHandlerForEvent");
        if (!inputEvent.isDone() && !inputEvent.isFailed()) {
            this.eventHandlersMap.computeIfAbsent(
                            inputEvent.getResultId(),
                            string -> new ArrayList<>()
                    )
                    .add(eventHandler);
        } else {
            MarinerEventChain.getInstance().execute(eventHandler);
        }
    }

    /**
     * Call this method when a result declared finished.
     *
     * @param resultId the ID of the result that just finished
     */
    void callHandlersWhenEventFinished(String resultId) {
        System.out.println("io.github.sinri.mariner.task.chain.EventRelationship.callHandlersWhenEventFinished");
        List<EventHandler<?, ?>> resultConsumers = this.eventHandlersMap.remove(resultId);
        if (resultConsumers != null) {
            resultConsumers.forEach(consumer -> {
                MarinerEventChain.getInstance().execute(consumer);
            });
        }
    }
}
