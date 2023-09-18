package io.github.sinri.mariner.test;

import io.github.sinri.mariner.task.timeline.Fruit;
import io.github.sinri.mariner.task.timeline.Timeline;

public class TaskTestD {
    public static void main(String[] args) throws InterruptedException {
        Timeline.start();

        Fruit.sinceSuccess(1)
                .afterSuccess(o -> {
                    return (int) o + 1;
                })
                .afterSuccess(o -> {
                    return (int) o + 1;
                })
                .onCompletion(o -> {
                    System.out.println("finally o is " + o);
                }, throwable -> {
                    System.out.println("error: " + throwable);
                });

        Thread.sleep(2000L);
        Timeline.stop();
    }
}
