package io.github.sinri.mariner.test;

import io.github.sinri.mariner.task.chain.MarinerChain;
import io.github.sinri.mariner.task.chain.Result;

import java.util.concurrent.TimeUnit;

public class TaskTestF {
    public static void main(String[] args) throws InterruptedException {
        MarinerChain chain = new MarinerChain();

        Result result1 = chain.registerHead(() -> 1, 1, TimeUnit.MILLISECONDS);
        Result result2 = chain.registerTail(result1, r -> {
            if (r.isDone()) {
                int x = (int) r.getResult();
                System.out.println("x is " + x);
                return x + 1;
            }
            throw new RuntimeException(r.getFailure());
        });
        chain.registerTail(result2, r -> {
                    if (r.isDone()) {
                        int x = (int) r.getResult();
                        System.out.println("x is " + x);
                        return x + 1;
                    }
                    throw new RuntimeException(r.getFailure());
                })
                .chain(r -> {
                    if (r.isDone()) {
                        int x = (int) r.getResult();
                        System.out.println("x is " + x);
                        return x + 1;
                    }
                    throw new RuntimeException(r.getFailure());
                })
                .chainForDone(x -> {
                    System.out.println("x is " + x);
                    return (int) x + 1;
                });

        Thread.sleep(1000L);
        chain.stop();
    }
}
