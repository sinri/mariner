package io.github.sinri.mariner.test;

import io.github.sinri.mariner.logger.MarinerLogger;
import io.github.sinri.mariner.task.chain.MarinerEvent;
import io.github.sinri.mariner.task.chain.MarinerEventChain;

import java.util.concurrent.TimeUnit;

public class TaskTestF {
    public static void main(String[] args) throws InterruptedException {
        MarinerEventChain.start();

        MarinerLogger logger = new MarinerLogger("TaskTestF");

        MarinerEvent.withResult(1)
                .handleEventResult(o -> {
                    logger.info(log -> log.attribute("o", o));
                    return o + 1;
                })
                .handleEventResult(o -> {
                    logger.info(log -> log.attribute("o", o));
                    return o + 1;
                })
                .handleEventResult(o -> {
                    logger.info(log -> log.attribute("o", o));
                    return o + 1;
                })
                .handleEventResult(o -> {
                    logger.info(log -> log.attribute("o", o));
                    return o + 1;
                })
                .handleEventResult(o -> {
                    logger.info(log -> log.attribute("o", o));
                    throw new RuntimeException("runtime error!");
                })
                .handleEventFailure(throwable -> {
                    logger.exception(throwable, "emmm");
                    return 100;
                })
                .handleEvent(result -> {
                    logger.notice(log -> log.attribute("result", result));
                    return 0;
                })
                .handleEvent(o -> {
                    logger.info(log -> log.attribute("o", o));
                    return o + 1;
                }, throwable -> {
                    logger.exception(throwable);
                    return 200;
                })
                .handleEvent(o -> {
                    logger.info(log -> log.attribute("o", o));
                    throw new RuntimeException("!!!");
                }, throwable -> {
                    logger.exception(throwable);
                    return 300;
                })
                .handleEvent(o -> {
                    logger.info(log -> log.attribute("o", o));
                    return o + 1;
                }, throwable -> {
                    logger.exception(throwable);
                    return 300;
                })
                .handleEvent(result -> {
                    logger.notice(log -> log.attribute("result", result));

                    MarinerEvent.withResult("A", 1, TimeUnit.SECONDS)
                            .handleEventResult(s -> {
                                logger.info(log -> log.attribute("s", s));
                                return s + "B";
                            })
                            .handleEventResult(s -> {
                                logger.info(log -> log.attribute("s", s));
                                return s + "C";
                            })
                            .handleEventResult(s -> {
                                logger.info(log -> log.attribute("s", s));
                                return s + "D";
                            });

                    return result;
                });


//        Thread.sleep(5000L);
//        MarinerEventChain.stop();
    }
}
