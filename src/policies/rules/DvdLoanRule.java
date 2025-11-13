package policies.rules;

import domain.inventory.Holding;
import domain.inventory.HoldingStatus;
import domain.user.Member;
import policies.LoanRule;

import java.time.LocalDate;

public final class DvdLoanRule implements LoanRule {
    private final int loanDays;

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
