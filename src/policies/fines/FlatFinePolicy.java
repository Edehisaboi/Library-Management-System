package policies.fines;

import domain.loan.Loan;
import policies.FinePolicy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * A simple fine policy that charges a flat rate per day overdue.
 * Supports a grace period and a maximum fine cap.
 */
public final class FlatFinePolicy implements FinePolicy {
    private final BigDecimal perDay;
    private final int graceDays;
    private final BigDecimal maxFine;

    /**
     * Creates a policy with a daily rate and grace period.
     * 
     * @param perDay    fine amount per day
     * @param graceDays number of days before fines start accumulating
     */
    public FlatFinePolicy(BigDecimal perDay, int graceDays) {
        this(perDay, graceDays, null);
    }

    /**
     * Creates a policy with a daily rate, grace period, and maximum cap.
     * 
     * @param perDay    fine amount per day
     * @param graceDays number of days before fines start
     * @param maxFine   maximum total fine allowed (can be null for no limit)
     */
    public FlatFinePolicy(BigDecimal perDay, int graceDays, BigDecimal maxFine) {
        this.perDay = perDay;
        this.graceDays = graceDays;
        this.maxFine = maxFine;
    }

    @Override
    public BigDecimal fineFor(Loan loan, LocalDate today) {
        if (loan.isReturned()) {
            today = loan.getReturnedOn();
        }
        long daysOver = Math.max(0, ChronoUnit.DAYS.between(loan.getDueOn(), today) - graceDays);
        BigDecimal fine = perDay.multiply(BigDecimal.valueOf(daysOver));
        if (maxFine != null && fine.compareTo(maxFine) > 0) {
            return maxFine;
        }
        return fine;
    }
}
