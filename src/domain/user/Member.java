package domain.user;

import util.Validation;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a library member who can borrow items.
 * Tracks loan limits, fines, and membership status.
 */
public final class Member extends User {
    private boolean blocked;
    private BigDecimal outstandingFines;
    private LocalDate membershipExpiry;
    private final int maxConcurrentLoans;

    /**
     * Creates a new Member with default loan limits and expiry.
     *
     * @param firstName first name
     * @param lastName  last name
     * @param email     email address
     * @param password  login password
     */
    public Member(String firstName, String lastName, String email, String password) {
        this(firstName, lastName, email, password, 5, LocalDate.now().plusMonths(1));
    }

    /**
     * Creates a new Member with specific limits.
     *
     * @param firstName          first name
     * @param lastName           last name
     * @param email              email address
     * @param password           login password
     * @param maxConcurrentLoans maximum number of items allowed on loan
     * @param membershipExpiry   date when membership expires
     */
    public Member(String firstName, String lastName, String email, String password, int maxConcurrentLoans,
            LocalDate membershipExpiry) {
        super(firstName, lastName, email, password);
        Validation.require(maxConcurrentLoans >= 1, "maxConcurrentLoans must be >= 1");
        this.maxConcurrentLoans = maxConcurrentLoans;
        this.blocked = false;
        this.outstandingFines = BigDecimal.ZERO;
        this.membershipExpiry = membershipExpiry;
    }

    /**
     * Checks if the member is currently blocked from borrowing.
     * 
     * @return true if blocked, false otherwise
     */
    public boolean isBlocked() {
        return blocked;
    }

    /**
     * Blocks the member from borrowing new items.
     */
    public void block() {
        this.blocked = true;
    }

    /**
     * Unblocks the member, allowing them to borrow again.
     */
    public void unblock() {
        this.blocked = false;
    }

    /**
     * Gets the total amount of unpaid fines.
     * 
     * @return the outstanding fines amount
     */
    public BigDecimal getOutstandingFines() {
        return outstandingFines;
    }

    /**
     * Checks if the membership has expired.
     * 
     * @return true if expired
     */
    public boolean isExpired() {
        return membershipExpiry.isBefore(LocalDate.now());
    }

    /**
     * Extends the membership expiry date.
     * 
     * @param newExpiry the new expiry date
     */
    public void extendMembership(LocalDate newExpiry) {
        if (newExpiry.isAfter(membershipExpiry)) {
            this.membershipExpiry = newExpiry;
        }
    }

    /**
     * Gets the current membership expiry date.
     * 
     * @return the expiry date
     */
    public LocalDate getMembershipExpiry() {
        return membershipExpiry;
    }

    /**
     * Adds a fine to the member's account.
     * 
     * @param amount the amount to add
     */
    public void addFine(BigDecimal amount) {
        Validation.nonNull(amount, "fine amount");
        Validation.require(amount.signum() >= 0, "fine must be >= 0");
        outstandingFines = outstandingFines.add(amount);
    }

    /**
     * Clears all outstanding fines for the member.
     */
    public void clearFines() {
        outstandingFines = BigDecimal.ZERO;
    }

    /**
     * Gets the maximum number of items this member can have on loan at once.
     * 
     * @return the max concurrent loans limit
     */
    public int getMaxConcurrentLoans() {
        return maxConcurrentLoans;
    }

    @Override
    public String role() {
        return "MEMBER";
    }

    @Override
    public String toString() {
        return super.toString() + "\n"
                + "Membership Expiry: " + membershipExpiry + "\n"
                + "Outstanding Fines: " + outstandingFines;
    }
}
