package util;

import java.time.LocalDate;

public interface ClockProvider {
    LocalDate today();

    static ClockProvider system() {
        return LocalDate::now;
    }

    static ClockProvider fixed(LocalDate date) {
        return () -> date;
    }
}
