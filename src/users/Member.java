package users;

import java.time.LocalDate;
import java.util.*;

public class Member extends Base {
    private final Set<String> borrowedBooks = new HashSet<>(); // todo: update String to BOOK class
    private LocalDate membershipExpiry;
    private final int MAX_BOOKS_BORROWED = 5;

    public Member(String firstName, String lastName, String email, String password, LocalDate membershipExpiry) {
        super(firstName, lastName, email, password);
        this.membershipExpiry = membershipExpiry;
    }

    public boolean canBorrow() {
        return borrowedBooks.size() < MAX_BOOKS_BORROWED && !isExpired();
    }

    public boolean isExpired() {
        return membershipExpiry != null && membershipExpiry.isBefore(LocalDate.now());
    }

    public LocalDate getMembershipExpiry() { return membershipExpiry; }
    public void extendMembership(LocalDate newExpiry) { this.membershipExpiry = newExpiry; }
}
