package util;

import java.util.Objects;

/**
 * Utility class for common validation checks.
 * Helps reduce boilerplate null/empty checks.
 */
public final class Validation {
    private Validation() {
    }

    /**
     * Throws IllegalArgumentException if the condition is false.
     *
     * @param condition boolean expression to check
     * @param message   exception message
     */
    public static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Ensures an object is not null.
     *
     * @param value the object to check
     * @param field name of the field for the error message
     * @param <T>   type of the object
     * @return the object if not null
     */
    public static <T> T nonNull(T value, String field) {
        return Objects.requireNonNull(value, field + " cannot be null");
    }

    /**
     * Ensures a string is not null and not empty/blank.
     *
     * @param value string to check
     * @param field name of the field for the error message
     * @return the trimmed string
     */
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
