package io.github.sinri.mariner.test;

import io.github.sinri.mariner.apostle.Apostle;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class Test2 {
    public static void main(String[] args) throws InterruptedException {
        Apostle apostle = new Apostle(3);
        for (int i = 0; i < 20; i++) {
            int finalI = i;
            apostle.submitTask(new Callable<Object>() {

                @Override
                public Object call() throws Exception {
                    long v = (long) (Math.random() * 3000);
                    System.out.println(System.currentTimeMillis() + " | CALL [" + finalI + "] start, time is " + v);
                    Thread.sleep(v);
                    System.out.println(System.currentTimeMillis() + " | CALL [" + finalI + "] end");
                    return finalI;
                }
            }, new Consumer<Object>() {

                @Override
                public void accept(Object o) {
                    System.out.println(System.currentTimeMillis() + " | TAIL OF [" + o + "]");
                }
            });
        }

        Thread.sleep(10_000L);
        System.out.println(System.currentTimeMillis() + " | TO SHUTDOWN");
        apostle.shutdownAndWait();
        System.out.println(System.currentTimeMillis() + " | NOW SHUTDOWN");
    }
}
