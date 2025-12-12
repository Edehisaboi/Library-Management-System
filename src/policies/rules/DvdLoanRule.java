package policies.rules;

import domain.inventory.Holding;
import domain.inventory.HoldingStatus;
import domain.user.Member;
import policies.LoanRule;

import java.time.LocalDate;

/**
 * Loan rules for DVDs.
 * Typically shorter loan periods and fewer restrictions than books.
 */
public final class DvdLoanRule implements LoanRule {
    private final int loanDays;

    /**
     * Creates a new DvdLoanRule.
     * 
     * @param loanDays number of days for the loan
     */
    public DvdLoanRule(int loanDays) {
        this.loanDays = loanDays;
    }

    @Override
    public boolean canLoan(Member member, Holding holding) {
        if (member.isBlocked())
            return false;
        return holding.getStatus() == HoldingStatus.AVAILABLE;
    }

    @Override
    public LocalDate dueDate(Member member, Holding holding, LocalDate now) {
        return now.plusDays(loanDays);
    }
}
