package policies.rules;

import domain.inventory.Holding;
import domain.inventory.HoldingStatus;
import domain.user.Member;
import policies.LoanRule;
import repo.LoanRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Loan rules for DVDs.
 * Typically shorter loan periods with age restrictions or stricter checks.
 */
public final class DVDLoanRule implements LoanRule {
    private final LoanRepository loanRepo;
    private final int loanDays;

    /**
     * Creates a new DVDLoanRule.
     * 
     * @param loanRepo the repository to check active loans
     * @param loanDays number of days for the loan
     */
    public DVDLoanRule(LoanRepository loanRepo, int loanDays) {
        this.loanRepo = loanRepo;
        this.loanDays = loanDays;
    }

    @Override
    public boolean canLoan(Member member, Holding holding) {
        if (member.isBlocked())
            return false;

        if (member.isExpired())
            return false;

        // Check outstanding fines
        if (member.getOutstandingFines().compareTo(BigDecimal.ZERO) > 0)
            return false;

        if (holding.getStatus() != HoldingStatus.AVAILABLE)
            return false;

        // Check concurrent loan limit defined by the Member
        int activeLoans = loanRepo.findActiveByMemberId(member.getId()).size();
        return activeLoans < member.getMaxConcurrentLoans();
    }

    @Override
    public LocalDate dueDate(Member member, Holding holding, LocalDate now) {
        return now.plusDays(loanDays);
    }
}
