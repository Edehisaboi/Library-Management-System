package repo;

import domain.loan.Loan;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface for managing Loan transactions.
 */
public interface LoanRepository {
    /**
     * Saves a new loan.
     * 
     * @param loan the loan to save
     * @return the saved loan
     */
    Loan save(Loan loan);

    /**
     * Updates an existing loan.
     * 
     * @param loan the loan with updated state
     */
    void update(Loan loan);

    /**
     * Finds a loan by its unique ID.
     * 
     * @param id the loan UUID
     * @return an Optional containing the loan if found
     */
    Optional<Loan> findById(UUID id);

    /**
     * Finds all active (unreturned) loans for a specific member.
     * 
     * @param memberId the member UUID
     * @return list of active loans
     */
    List<Loan> findActiveByMemberId(UUID memberId);

    /**
     * Finds all loans that are currently unreturned and past their due date.
     * 
     * @param today the reference date for determining overdue status
     * @return list of overdue loans
     */
    List<Loan> findOverdue(LocalDate today);
}
