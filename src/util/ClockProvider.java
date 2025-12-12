package util;

import java.time.LocalDate;

/**
 * Functional interface for providing the current date.
 */
public interface ClockProvider {
    /**
     * Gets the current date.
     * 
     * @return today's date
     */
    LocalDate today();

    /**
     * Returns a provider that uses the system clock.
     * 
     * @return system clock provider
     */
    static ClockProvider system() {
        return LocalDate::now;
    }

    /**
     * Returns a provider that always returns a fixed date.
     * 
     * @param date the fixed date to return
     * @return fixed clock provider
     */
    static ClockProvider fixed(LocalDate date) {
        return () -> date;
    }
}
