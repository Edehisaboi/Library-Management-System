package repo;

import domain.loan.Loan;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepository {
    Loan save(Loan loan);

    void update(Loan loan);

    Optional<Loan> findById(UUID id);

    List<Loan> findActiveByMemberId(UUID memberId);

    List<Loan> findOverdue(LocalDate today);
}
