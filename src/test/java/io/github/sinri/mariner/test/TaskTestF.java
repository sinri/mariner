package io.github.sinri.mariner.test;

import io.github.sinri.mariner.task.chain.MarinerEvent;
import io.github.sinri.mariner.task.chain.MarinerEventChain;

public class TaskTestF {
    public static void main(String[] args) throws InterruptedException {
        MarinerEventChain chain = MarinerEventChain.getInstance();

        MarinerEvent.withResult(1)
                .handleEventResult(o -> {
                    System.out.println("o : " + o);
                    return (int) o + 1;
                })
                .handleEventResult(o -> {
                    System.out.println("o : " + o);
                    return (int) o + 1;
                })
                .handleEventResult(o -> {
                    System.out.println("o : " + o);
                    return (int) o + 1;
                })
                .handleEventResult(o -> {
                    System.out.println("o : " + o);
                    return (int) o + 1;
                })
                .handleEventResult(o -> {
                    System.out.println("o : " + o);
                    throw new RuntimeException("runtime error!");
                })
                .handleEventFailure(throwable -> {
                    System.out.println(throwable.getMessage());
                    return 100;
                })
                .handleEvent(result -> {
                    System.out.println(result);
                    return result;
                });


        Thread.sleep(1000L);
        chain.stop();
    }
}
