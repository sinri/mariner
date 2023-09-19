package io.github.sinri.mariner.task.chain;

import java.util.function.Consumer;
import java.util.function.Function;

class EventHandler<I, O> implements Runnable, Consumer<MarinerEvent<I>> {
    private final Function<MarinerEvent<I>, O> func;
    private final MarinerEvent<O> outputEvent;
    private final MarinerEvent<I> inputEvent;

    EventHandler(MarinerEvent<I> inputEvent, Function<MarinerEvent<I>, O> func) {
        this.outputEvent = new MarinerEvent<O>();
        this.inputEvent = inputEvent;
        this.func = func;
    }

    EventHandler(Function<MarinerEvent<I>, O> func) {
        this(null, func);
    }

    @Override
    public void run() {
        accept(this.inputEvent);

        MarinerEventChain.getInstance().notifyConsumersWhenResultConfirmed(this.getOutputEvent().getResultId());
    }

    public MarinerEvent<O> getOutputEvent() {
        return outputEvent;
    }

    @Override
    public void accept(MarinerEvent<I> result) {
        try {
            O o = this.func.apply(result);
            this.outputEvent.declareDone(o);
        } catch (Exception e) {
            this.outputEvent.declareFailed(e);
        } finally {
            System.out.println("io.github.sinri.mariner.task.chain.EventHandler.accept");
        }
    }
}
