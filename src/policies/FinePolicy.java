package policies;

import domain.loan.Loan;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface FinePolicy {
    BigDecimal fineFor(Loan loan, LocalDate today);
}
