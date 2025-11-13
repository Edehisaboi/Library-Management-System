package util;

import java.util.Objects;

public final class Validation {
    private Validation() {
    }

    public static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <T> T nonNull(T value, String field) {
        return Objects.requireNonNull(value, field + " cannot be null");
    }

    public static String nonBlank(String value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(field + " cannot be null");
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be blank");
        }
        return trimmed;
    }
}
