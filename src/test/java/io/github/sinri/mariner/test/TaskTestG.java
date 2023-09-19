package io.github.sinri.mariner.test;

import io.github.sinri.mariner.logger.MarinerLogger;
import io.github.sinri.mariner.task.railgun.Coin;
import io.github.sinri.mariner.task.railgun.Railgun;

public class TaskTestG {
    public static void main(String[] args) {
        Railgun.start();

        MarinerLogger logger = new MarinerLogger("TaskTestG");

        Coin.withResult(64)
                .compose(x -> {
                    int y = (x / 2 - 1);
                    logger.info(log -> log.attribute("input", x).attribute("output", y));
                    if (y < 0) throw new RuntimeException("MINUS!");
                    return y;
                }, throwable -> {
                    logger.exception(throwable);
                    return 0;
                })
                .compose(x -> {
                    int y = (x / 2 - 1);
                    logger.info(log -> log.attribute("input", x).attribute("output", y));
                    if (y < 0) throw new RuntimeException("MINUS!");
                    return y;
                }, throwable -> {
                    logger.exception(throwable);
                    return 0;
                })
                .compose(x -> {
                    int y = (x / 2 - 1);
                    logger.info(log -> log.attribute("input", x).attribute("output", y));
                    if (y < 0) throw new RuntimeException("MINUS!");
                    return y;
                }, throwable -> {
                    logger.exception(throwable);
                    return 0;
                })
                .compose(x -> {
                    int y = (x / 2 - 1);
                    logger.info(log -> log.attribute("input", x).attribute("output", y));
                    if (y < 0) throw new RuntimeException("MINUS!");
                    return y;
                }, throwable -> {
                    logger.exception(throwable);
                    return 0;
                })
                .compose(x -> {
                    int y = (x / 2 - 1);
                    logger.info(log -> log.attribute("input", x).attribute("output", y));
                    if (y < 0) throw new RuntimeException("MINUS!");
                    return y;
                }, throwable -> {
                    logger.exception(throwable);
                    return 0;
                })
                .compose(x -> {
                    int y = (x / 2 - 1);
                    logger.info(log -> log.attribute("input", x).attribute("output", y));
                    if (y < 0) throw new RuntimeException("MINUS!");
                    return y;
                }, throwable -> {
                    logger.exception(throwable);
                    return 0;
                })
                .compose(x -> {
                    int y = (x / 2 - 1);
                    logger.info(log -> log.attribute("input", x).attribute("output", y));
                    if (y < 0) throw new RuntimeException("MINUS!");
                    return y;
                }, throwable -> {
                    logger.exception(throwable);
                    return 0;
                })
                .compose(x -> {
                    int y = (x / 2 - 1);
                    logger.info(log -> log.attribute("input", x).attribute("output", y));
                    if (y < 0) throw new RuntimeException("MINUS!");
                    return y;
                }, throwable -> {
                    logger.exception(throwable);
                    return 0;
                })
                .compose(x -> {
                    logger.notice(log -> log.attribute("final", x));
                }, throwable -> {
                    logger.exception(throwable, "final");
                });
    }
}
