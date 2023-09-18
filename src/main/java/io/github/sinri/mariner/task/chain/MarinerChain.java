package io.github.sinri.mariner.task.chain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public class MarinerChain {
    private final ScheduledExecutorService scheduler;
    private final Map<String, List<ResultGenerator>> consumerMap;

    public MarinerChain() {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.consumerMap = new ConcurrentHashMap<>();
    }

    public Result registerHead(Supplier<Object> supplier, long delay, TimeUnit unit) {
        ResultGenerator resultGenerator = new ResultGenerator(this, result -> supplier.get());
        this.scheduler.schedule(resultGenerator, delay, unit);
        return resultGenerator.getResult();
    }

    public Result registerTail(Result previousResult, Function<Result, Object> func) {
        ResultGenerator resultGenerator = new ResultGenerator(this, previousResult, func);
        this.consumerMap.computeIfAbsent(previousResult.getResultId(), string -> new ArrayList<>())
                .add(resultGenerator);
        return resultGenerator.getResult();
    }

    void notifyConsumersWhenResultConfirmed(String resultId) {
        List<ResultGenerator> resultConsumers = this.consumerMap.remove(resultId);
        if (resultConsumers != null) {
            resultConsumers.forEach(this.scheduler::execute);
        }
    }


    public void stop() {
        this.scheduler.shutdown();
        while (true) {
            try {
                boolean terminated = this.scheduler.awaitTermination(1, TimeUnit.SECONDS);
                if (terminated) break;
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
