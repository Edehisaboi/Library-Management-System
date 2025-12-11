package services;

import domain.inventory.Holding;
import domain.inventory.HoldingStatus;
import domain.loan.Loan;
import domain.user.Member;
import policies.FinePolicy;
import policies.rules.BookLoanRule;
import policies.LoanRule;
import repo.InventoryRepository;
import repo.LoanRepository;
import util.ClockProvider;
import util.Validation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

public final class LoanService {
    private final InventoryRepository invRepo;
    private final LoanRepository loanRepo;
    private final LoanRule loanRule;
    private final FinePolicy finePolicy;
    private final ClockProvider clock;

    public LoanService(InventoryRepository invRepo, LoanRepository loanRepo, LoanRule loanRule, FinePolicy finePolicy,
            ClockProvider clock) {
        this.invRepo = Objects.requireNonNull(invRepo, "invRepo");
        this.loanRepo = Objects.requireNonNull(loanRepo, "loanRepo");
        this.loanRule = Objects.requireNonNull(loanRule, "loanRule");
        this.finePolicy = Objects.requireNonNull(finePolicy, "finePolicy");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public Loan loanCopy(UUID holdingId, Member member) {
        Objects.requireNonNull(member, "member");
        Holding h = invRepo.findById(holdingId)
                .orElseThrow(() -> new NoSuchElementException("Holding not found: " + holdingId));
        Validation.require(h.getStatus() == HoldingStatus.AVAILABLE, "Holding not available");

        if (loanRule instanceof BookLoanRule bookRule) {
            int active = loanRepo.findActiveByMemberId(member.getId()).size();
            Validation.require(active < bookRule.maxConcurrent(), "Member has reached max concurrent loans");
        }

        Validation.require(loanRule.canLoan(member, h), "Loan rule denied");
        h.markOnLoan();
        invRepo.update(h);

        LocalDate now = clock.today();
        LocalDate due = loanRule.dueDate(member, h, now);
        Loan loan = new Loan(h, member, now, due);
        return loanRepo.save(loan);
    }

    /**
     * Marks a loan as returned, updates the holding status, and applies any
     * overdue fine to the member.
     *
     * @param loanId the loan identifier
     * @return the fine charged for this loan on return (zero if none)
     */
    public BigDecimal returnCopy(UUID loanId) {
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new NoSuchElementException("Loan not found: " + loanId));
        Validation.require(!loan.isReturned(), "Already returned");
        Holding h = invRepo.findById(loan.getHolding().getId())
                .orElseThrow(() -> new IllegalStateException("Holding not found for loan"));
        LocalDate today = clock.today();
        loan.markReturned(today);
        h.markReturned();
        invRepo.update(h);
        loanRepo.update(loan);

        BigDecimal fine = finePolicy.fineFor(loan, today);
        if (fine.signum() > 0) {
            loan.getBorrower().addFine(fine);
        }
        return fine;
    }

    public BigDecimal fine(UUID loanId) {
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new NoSuchElementException("Loan not found: " + loanId));
        return finePolicy.fineFor(loan, clock.today());
    }

    public List<Loan> activeLoans(UUID memberId) {
        return loanRepo.findActiveByMemberId(memberId);
    }

    public List<Loan> overdueLoans() {
        return loanRepo.findOverdue(clock.today());
    }

    /**
     * Convenience method: loan the first available copy of the given media item
     * for the member.
     */
    public Loan loanFirstAvailableCopy(UUID mediaId, Member member) {
        Objects.requireNonNull(member, "member");
        List<Holding> holdings = invRepo.findByMediaId(mediaId);
        Holding available = holdings.stream()
                .filter(h -> h.getStatus() == HoldingStatus.AVAILABLE)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No available copies for media: " + mediaId));
        return loanCopy(available.getId(), member);
    }
}
