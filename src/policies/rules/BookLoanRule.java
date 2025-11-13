package policies.rules;

import domain.inventory.Holding;
import domain.inventory.HoldingStatus;
import domain.user.Member;
import policies.LoanRule;

import java.time.LocalDate;

public final class BookLoanRule implements LoanRule {
    private final int loanDays;
    private final int maxConcurrent;

    public BookLoanRule(int loanDays, int maxConcurrent) {
        this.loanDays = loanDays;
        this.maxConcurrent = maxConcurrent;
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

    public int maxConcurrent() {
        return maxConcurrent;
    }
}
