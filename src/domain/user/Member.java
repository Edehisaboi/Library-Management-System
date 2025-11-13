package domain.user;

import util.Validation;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class Member extends User {
    private boolean blocked;
    private BigDecimal outstandingFines;
    private LocalDate membershipExpiry;
    private final int maxConcurrentLoans;

    public Member(String firstName, String lastName, String email, String password) {
        this(firstName, lastName, email, password, 5, LocalDate.now().plusMonths(1));
    }

    public Member(String firstName, String lastName, String email, String password, int maxConcurrentLoans,
            LocalDate membershipExpiry) {
        super(firstName, lastName, email, password);
        Validation.require(maxConcurrentLoans >= 1, "maxConcurrentLoans must be >= 1");
        this.maxConcurrentLoans = maxConcurrentLoans;
        this.blocked = false;
        this.outstandingFines = BigDecimal.ZERO;
        this.membershipExpiry = membershipExpiry;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void block() {
        this.blocked = true;
    }

    public void unblock() {
        this.blocked = false;
    }

    public BigDecimal getOutstandingFines() {
        return outstandingFines;
    }

    public boolean isExpired() {
        return membershipExpiry.isBefore(LocalDate.now());
    }

    public void extendMembership(LocalDate newExpiry) {
        if (newExpiry.isAfter(membershipExpiry)) {
            this.membershipExpiry = newExpiry;
        }
    }

    public LocalDate getMembershipExpiry() {
        return membershipExpiry;
    }

    public void addFine(BigDecimal amount) {
        Validation.nonNull(amount, "fine amount");
        Validation.require(amount.signum() >= 0, "fine must be >= 0");
        outstandingFines = outstandingFines.add(amount);
    }

    public void clearFines() {
        outstandingFines = BigDecimal.ZERO;
    }

    public int getMaxConcurrentLoans() {
        return maxConcurrentLoans;
    }

    @Override
    public String role() {
        return "MEMBER";
    }
}
