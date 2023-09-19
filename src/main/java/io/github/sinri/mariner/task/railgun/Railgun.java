package io.github.sinri.mariner.task.railgun;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Railgun {
    private static Railgun instance;
    private final ScheduledExecutorService monitor;
    private final ExecutorService workers;
    private final Map<String, Coin<?>> coins = new ConcurrentHashMap<>();
    private Railgun() {
        this.monitor = Executors.newSingleThreadScheduledExecutor();
        this.workers = Executors.newCachedThreadPool();

        // start monitoring
        this.monitor.schedule(this::monitorRoutine, 100, TimeUnit.MILLISECONDS);
    }

    public static void start() {
        instance = new Railgun();
    }

    public static void stop() {
        instance.workers.shutdown();
        instance.monitor.shutdown();
    }

    static <T> Coin<T> fire(Callable<T> callable) {
//        System.out.println("io.github.sinri.mariner.task.railgun.Railgun.fire");
        Future<T> future = instance.workers.submit(callable);
        Coin<T> coin = new Coin<>(future);
        instance.coins.put(coin.coinId(), coin);
        return coin;
    }

    private void monitorRoutine() {
        synchronized (coins) {
            List<String> keys = new ArrayList<>(coins.keySet());
//            if(!keys.isEmpty()) {
//                System.out.println("io.github.sinri.mariner.task.railgun.Railgun.monitorRoutine - total " + keys.size());
//            }
            keys.forEach(key -> {
                var coin = coins.get(key);
                coin.refreshStatus();
                if (coin.isDone() || coin.isFailed()) {
//                    System.out.println("io.github.sinri.mariner.task.railgun.Railgun.monitorRoutine - coin ["+coin.coinId()+"] to handle");
                    // handle for next
                    if (coin.hasCoinHandler()) {
                        Coin<?> firedCoin = fire(coin::handleForNextByWorker);
                        Coin<?> nextCoin = coin.getNextCoin();
                        nextCoin.absorb(firedCoin);
                        coins.put(nextCoin.coinId(), nextCoin);
                    }
                    // remove key
                    this.coins.remove(key);
                }
            });
        }
        this.monitor.schedule(this::monitorRoutine, 100, TimeUnit.MILLISECONDS);
    }
}
