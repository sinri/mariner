package io.github.sinri.mariner.logger;

import java.util.function.Consumer;

public class MarinerLogger {
    private final String topic;
    private final MarinerLogConsumer consumer;

    public MarinerLogger(String topic) {
        this.topic = topic;
        this.consumer = MarinerLogOutputConsumer.getInstance();
    }

    public MarinerLogger(String topic, MarinerLogConsumer consumer) {
        this.topic = topic;
        this.consumer = consumer;
    }

    private void log(MarinerLogLevel level, Consumer<MarinerLog> editor) {
        MarinerLog log = new MarinerLog(level, topic);
        editor.accept(log);
        this.consumer.accept(log);
    }

    public void debug(Consumer<MarinerLog> editor) {
        this.log(MarinerLogLevel.DEBUG, editor);
    }

    public void info(Consumer<MarinerLog> editor) {
        this.log(MarinerLogLevel.INFO, editor);
    }

    public void notice(Consumer<MarinerLog> editor) {
        this.log(MarinerLogLevel.NOTICE, editor);
    }

    public void warning(Consumer<MarinerLog> editor) {
        this.log(MarinerLogLevel.WARNING, editor);
    }

    public void error(Consumer<MarinerLog> editor) {
        this.log(MarinerLogLevel.ERROR, editor);
    }

    public void fatal(Consumer<MarinerLog> editor) {
        this.log(MarinerLogLevel.FATAL, editor);
    }

    public void exception(Throwable throwable) {
        this.error(log -> log
                .attribute("exception", throwable)
        );
    }

    public void exception(Throwable throwable, String message) {
        this.error(log -> log.attribute("msg", message)
                .attribute("exception", throwable)
        );
    }
}
