package io.github.sinri.mariner.logger;

import java.util.Map;
import java.util.TreeMap;

public class MarinerLog {
    private final MarinerLogLevel level;
    private final String topic;
    private final Map<String, Object> attributes;
    private final long timestamp;

    public MarinerLog(MarinerLogLevel level, String topic) {
        this.timestamp = System.currentTimeMillis();
        this.topic = topic;
        this.level = level;
        this.attributes = new TreeMap<>();
    }

    public MarinerLog attribute(String name, Object value) {
        this.attributes.put(name, value);
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public MarinerLogLevel getLevel() {
        return level;
    }

    public String getTopic() {
        return topic;
    }
}
