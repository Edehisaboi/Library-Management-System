package policies;

import domain.inventory.Holding;
import domain.user.Member;

import java.time.LocalDate;

/**
 * Strategy interface for determining if a member can borrow an item
 * and calculating the due date.
 */
public interface LoanRule {
    /**
     * Checks if a member is eligible to borrow a specific holding.
     *
     * @param member  the member attempting to borrow
     * @param holding the item to be borrowed
     * @return true if allowed, false otherwise
     */
    boolean canLoan(Member member, Holding holding);

    /**
     * Calculates the due date for a loan starting 'now'.
     *
     * @param member  the borrower
     * @param holding the borrowed item
     * @param now     the start date of the loan
     * @return the calculated due date
     */
    LocalDate dueDate(Member member, Holding holding, LocalDate now);
}
