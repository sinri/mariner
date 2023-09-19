package io.github.sinri.mariner.helper;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MarinerHelper {
    public <T> String joinStringArray(T[] x, String separator) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < x.length; i++) {
            if (i > 0) result.append(separator);
            result.append(x[i].toString());
        }
        return result.toString();
    }

    public String joinStringArray(Collection<?> x, String separator) {
        StringBuilder result = new StringBuilder();

        final int[] i = {0};
        x.forEach(item -> {
            if (i[0] > 0) result.append(separator);
            result.append(item.toString());
            i[0] += 1;
        });

        return result.toString();
    }

    /**
     * @since 2.7
     */
    public String fromUnderScoreCaseToCamelCase(String underScoreCase) {
        if (underScoreCase == null) {
            return null;
        }
        String[] parts = underScoreCase.toLowerCase().split("[\\s_]");
        List<String> camel = new ArrayList<>();
        for (var part : parts) {
            if (part != null && !part.isEmpty() && !part.isBlank()) {
                camel.add(part.substring(0, 1).toUpperCase() + part.substring(1));
            }
        }
        return joinStringArray(camel, "");
    }

    /**
     * @since 2.7
     */
    public String fromCamelCaseToUserScoreCase(String camelCase) {
        if (camelCase == null) {
            return null;
        }
        if (camelCase.isEmpty() || camelCase.isBlank()) {
            return "";
        }
        if (camelCase.length() == 1) {
            return camelCase.toLowerCase();
        }
        List<String> parts = new ArrayList<>();
        StringBuilder part = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            String current = camelCase.substring(i, i + 1);
            if (current.matches("[\\s_]")) continue;
            if (part.length() == 0) {
                part.append(current.toLowerCase());
            } else {
                if (current.matches("[A-Z]")) {
                    parts.add(part.toString());
                    part = new StringBuilder();
                }
                part.append(current.toLowerCase());
            }
        }
        if (part.length() > 0) {
            parts.add(part.toString());
        }
        return joinStringArray(parts, "_");
    }

    /**
     * @param format for example: yyyy-MM-ddTHH:mm:ss
     */
    public String getDateExpression(Date date, String format) {
        if (format == null || format.isEmpty()) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public String jsonify(Object v) {
        StringBuilder sb = new StringBuilder();
        if (v == null) {
            sb.append("null");
        } else if (v instanceof Throwable) {
            Throwable t = (Throwable) v;
            Map<String, Object> map = new TreeMap<>();
            map.put("throwable", t.getClass());
            map.put("message", t.getMessage());

            List<StackTraceElement> stackTraceElementList = new ArrayList<>();
            StackTraceElement[] stackTrace = t.getStackTrace();
            if (stackTrace != null) {
                Collections.addAll(stackTraceElementList, stackTrace);
            }
            map.put("stack", stackTraceElementList);

            sb.append(this.jsonify(map));
        } else if (v instanceof Map) {
            sb.append(this.jsonify((Map<?, ?>) v));
        } else if (v instanceof List) {
            sb.append(this.jsonify((List<?>) v));
        } else if (v instanceof Number) {
            sb.append(v);
        } else if (v instanceof Boolean) {
            sb.append(v);
        } else {
            sb.append("\"").append(String.valueOf(v).replaceAll("\"", "\\\\\"")).append("\"");
        }
        return sb.toString();
    }

    public String jsonify(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder("{");
        AtomicBoolean isFirstItem = new AtomicBoolean(true);
        map.forEach((k, v) -> {
            if (!isFirstItem.get()) {
                sb.append(",");
            }

            sb.append("\"").append(String.valueOf(k).replaceAll("\"", "\\\\\"")).append("\"")
                    .append(":");
            sb.append(this.jsonify(v));
            isFirstItem.set(false);
        });

        return sb.append("}").toString();
    }

    public String jsonify(List<?> list) {
        StringBuilder sb = new StringBuilder("[");
        AtomicBoolean isFirstItem = new AtomicBoolean(true);

        list.forEach(item -> {
            if (!isFirstItem.get()) {
                sb.append(",");
            }
            sb.append(this.jsonify(item));
            isFirstItem.set(false);
        });

        return sb.append("]").toString();
    }
}
