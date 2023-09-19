package io.github.sinri.mariner.task.chain;

import java.util.function.Consumer;
import java.util.function.Function;

class EventHandler implements Runnable, Consumer<MarinerEvent> {
    private final Function<MarinerEvent, Object> func;
    private final MarinerEvent outputEvent;
    private final MarinerEvent inputEvent;

    EventHandler(MarinerEvent inputEvent, Function<MarinerEvent, Object> func) {
        this.outputEvent = new MarinerEvent();
        this.inputEvent = inputEvent;
        this.func = func;
    }

    EventHandler(Function<MarinerEvent, Object> func) {
        this(null, func);
    }

    @Override
    public void run() {
        accept(this.inputEvent);
        MarinerEventChain.getInstance().notifyConsumersWhenResultConfirmed(this.getOutputEvent().getResultId());
    }

    public MarinerEvent getOutputEvent() {
        return outputEvent;
    }

    @Override
    public void accept(MarinerEvent result) {
        try {
            Object o = this.func.apply(result);
            this.outputEvent.declareDone(o);
        } catch (Exception e) {
            this.outputEvent.declareFailed(e);
        }
    }
}
