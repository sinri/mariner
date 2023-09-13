package io.github.sinri.mariner.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
}
