package domain.loan;

import domain.inventory.Holding;
import domain.user.Member;
import util.Validation;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public final class Loan {
    private final UUID id;
    private final Holding holding;
    private final Member borrower;
    private final LocalDate loanedOn;
    private final LocalDate dueOn;
    private LocalDate returnedOn;

    public Loan(Holding holding, Member borrower, LocalDate loanedOn, LocalDate dueOn) {
        this.id = UUID.randomUUID();
        this.holding = Objects.requireNonNull(holding, "holding cannot be null");
        this.borrower = Objects.requireNonNull(borrower, "borrower cannot be null");
        this.loanedOn = Objects.requireNonNull(loanedOn, "loanedOn cannot be null");
        this.dueOn = Objects.requireNonNull(dueOn, "dueOn cannot be null");
        Validation.require(!dueOn.isBefore(loanedOn), "dueOn must be on/after loanedOn");
    }

    public UUID getId() {
        return id;
    }

    public Holding getHolding() {
        return holding;
    }

    public Member getBorrower() {
        return borrower;
    }

    public LocalDate getLoanedOn() {
        return loanedOn;
    }

    public LocalDate getDueOn() {
        return dueOn;
    }

    public LocalDate getReturnedOn() {
        return returnedOn;
    }

    public boolean isReturned() {
        return returnedOn != null;
    }

    public void markReturned(LocalDate date) {
        Validation.require(!isReturned(), "Loan already returned");
        this.returnedOn = Objects.requireNonNull(date, "return date cannot be null");
        Validation.require(!returnedOn.isBefore(loanedOn), "return date cannot be before loan date");
    }
}
