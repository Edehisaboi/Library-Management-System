package policies.rules;

import domain.inventory.Holding;
import domain.inventory.HoldingStatus;
import domain.user.Member;
import policies.LoanRule;
import repo.LoanRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A standard loan rule that applies to ALL media items.
 * Enforces member loan limits and a fixed loan period for everything.
 */
public class StandardLoanRule implements LoanRule {
    private final LoanRepository loanRepo;
    private final int loanDays;

    /**
     * Creates a new StandardLoanRule.
     *
     * @param loanRepo the repository used to count active loans
     * @param loanDays fixed number of days for all loans
     */
    public StandardLoanRule(LoanRepository loanRepo, int loanDays) {
        this.loanRepo = Objects.requireNonNull(loanRepo);
        this.loanDays = loanDays;
    }

    @Override
    public boolean canLoan(Member member, Holding holding) {
        // 1. Basic Member Checks
        if (member.isBlocked() || member.isExpired()) {
            return false;
        }

        // 2. Fines Check (Strict: no borrowing if any fines exist)
        if (member.getOutstandingFines().compareTo(BigDecimal.ZERO) > 0) {
            return false;
        }

        // 3. Item Availability
        if (holding.getStatus() != HoldingStatus.AVAILABLE) {
            return false;
        }

        // 4. Global Limit Check (Member's personal limit)
        // This rule treats all items equally, so we only care about the total count.
        int activeLoans = loanRepo.findActiveByMemberId(member.getId()).size();
        return activeLoans < member.getMaxConcurrentLoans();
    }

    @Override
    public LocalDate dueDate(Member member, Holding holding, LocalDate now) {
        return now.plusDays(loanDays);
    }
}
