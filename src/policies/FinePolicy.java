package policies;

import domain.loan.Loan;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Strategy interface for calculating fines on overdue loans.
 */
public interface FinePolicy {
    /**
     * Calculates the fine for a given loan as of 'today'.
     * If the loan was already returned, 'today' should be ignored in favor of the
     * return date.
     *
     * @param loan  the loan to calculate fines for
     * @param today the current date (for active loans)
     * @return the calculated fine amount (zero if not overdue)
     */
    BigDecimal fineFor(Loan loan, LocalDate today);
}
