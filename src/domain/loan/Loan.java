package domain.loan;

import domain.inventory.Holding;
import domain.user.Member;
import util.Validation;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a loan transaction where a member borrows a holding.
 * Tracks the borrower, the item, and relevant dates.
 */
public final class Loan {
    private final UUID id;
    private final Holding holding;
    private final Member borrower;
    private final LocalDate loanedOn;
    private final LocalDate dueOn;
    private LocalDate returnedOn;

    /**
     * Creates a new Loan.
     *
     * @param holding  the specific item being borrowed
     * @param borrower the member borrowing the item
     * @param loanedOn date the loan starts
     * @param dueOn    date the item is due back
     */
    public Loan(Holding holding, Member borrower, LocalDate loanedOn, LocalDate dueOn) {
        this.id = UUID.randomUUID();
        this.holding = Validation.nonNull(holding, "holding");
        this.borrower = Validation.nonNull(borrower, "borrower");
        this.loanedOn = Validation.nonNull(loanedOn, "loanedOn");
        this.dueOn = Validation.nonNull(dueOn, "dueOn");
        Validation.require(!dueOn.isBefore(loanedOn), "dueOn must be on/after loanedOn");
    }

    /**
     * Gets the unique ID of the loan.
     * 
     * @return the UUID
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the holding associated with this loan.
     * 
     * @return the holding
     */
    public Holding getHolding() {
        return holding;
    }

    /**
     * Gets the member who borrowed the item.
     * 
     * @return the borrower
     */
    public Member getBorrower() {
        return borrower;
    }

    /**
     * Gets the start date of the loan.
     * 
     * @return loan start date
     */
    public LocalDate getLoanedOn() {
        return loanedOn;
    }

    /**
     * Gets the due date of the loan.
     * 
     * @return due date
     */
    public LocalDate getDueOn() {
        return dueOn;
    }

    /**
     * Gets the date the item was returned.
     * 
     * @return return date, or null if not yet returned
     */
    public LocalDate getReturnedOn() {
        return returnedOn;
    }

    /**
     * Checks if the loan has been returned.
     * 
     * @return true if returned
     */
    public boolean isReturned() {
        return returnedOn != null;
    }

    /**
     * Marks the loan as returned on a specific date.
     * 
     * @param date the return date
     */
    public void markReturned(LocalDate date) {
        Validation.require(!isReturned(), "Loan already returned");
        this.returnedOn = Validation.nonNull(date, "return date");
        Validation.require(!returnedOn.isBefore(loanedOn), "return date cannot be before loan date");
    }
}
