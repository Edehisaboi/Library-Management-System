package policies;

import domain.inventory.Holding;
import domain.user.Member;

import java.time.LocalDate;

public interface LoanRule {
    boolean canLoan(Member member, Holding holding);

    LocalDate dueDate(Member member, Holding holding, LocalDate now);
}
