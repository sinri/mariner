package io.github.sinri.mariner.task.chain;

import java.util.function.Consumer;
import java.util.function.Function;

class EventHandler implements Runnable, Consumer<MarinerEvent> {
    private final Function<MarinerEvent, Object> func;
    private final MarinerEvent result;
    private MarinerEvent previousResult = null;

    public EventHandler(MarinerEvent previousResult, Function<MarinerEvent, Object> func) {
        this.result = new MarinerEvent();
        this.previousResult = previousResult;
        this.func = func;
    }

    public EventHandler(Function<MarinerEvent, Object> func) {
        this(null, func);
    }

    @Override
    public void run() {
        accept(this.previousResult);
        MarinerEventChain.getInstance().notifyConsumersWhenResultConfirmed(this.getResult().getResultId());
    }

    public MarinerEvent getResult() {
        return result;
    }

    @Override
    public void accept(MarinerEvent result) {
        try {
            Object o = this.func.apply(result);
            this.result.declareDone(o);
        } catch (Exception e) {
            this.result.declareFailed(e);
        }
    }
}
