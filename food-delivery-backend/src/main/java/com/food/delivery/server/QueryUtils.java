package com.food.delivery.server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class QueryUtils {
    private QueryUtils() {
    }

    public static Map<String, String> parseQuery(String raw) {
        if (raw == null || raw.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> result = new HashMap<>();
        for (String pair : raw.split("&")) {
            int idx = pair.indexOf('=');
            if (idx > 0) {
                String key = decode(pair.substring(0, idx));
                String value = decode(pair.substring(idx + 1));
                result.put(key, value);
            }
        }
        return result;
    }

    public static Map<String, String> parseBody(String body) {
        // Expecting application/x-www-form-urlencoded for simplicity
        return parseQuery(body);
    }

    public static Optional<Long> getLong(Map<String, String> params, String key) {
        try {
            return params.containsKey(key) ? Optional.of(Long.parseLong(params.get(key))) : Optional.empty();
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(key + " must be a number");
        }
    }

    public static Optional<Integer> getInt(Map<String, String> params, String key) {
        try {
            return params.containsKey(key) ? Optional.of(Integer.parseInt(params.get(key))) : Optional.empty();
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(key + " must be a number");
        }
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }
}
