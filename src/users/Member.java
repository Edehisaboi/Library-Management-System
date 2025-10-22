package users;

import authentication.AccessLevel;
import books.BookCopy;

import java.time.LocalDate;
import java.util.*;


public class Member extends Base {
    private final Set<BookCopy> borrowedBooks = new HashSet<>();
    private LocalDate membershipExpiry;
    private static final int MAX_BOOKS_BORROWED = 5;

    public Member(
            String firstName,
            String lastName,
            String email,
            String password,
            LocalDate membershipExpiry
    ) {
        super(firstName, lastName, email, password);
        this.membershipExpiry = Objects.requireNonNull(membershipExpiry, "Membership expiry date cannot be null!");
    }

    @Override
    public AccessLevel getAccessLevel() {
        return AccessLevel.MEMBER;
    }

    public boolean canBorrow() {
        return borrowedBooks.size() < MAX_BOOKS_BORROWED && !isExpired();
    }

    public boolean isExpired() {
        return membershipExpiry.isBefore(LocalDate.now());
    }

    public void extendMembership(LocalDate newExpiry) {
        Objects.requireNonNull(newExpiry, "New expiry date cannot be null!");
        if (newExpiry.isAfter(membershipExpiry)) {
            this.membershipExpiry = newExpiry;
        }
    }

    public LocalDate getMembershipExpiry() {
        return membershipExpiry;
    }

    public Set<BookCopy> getBorrowedBooks() {
        return Collections.unmodifiableSet(borrowedBooks);
    }

    public boolean borrowBook(BookCopy book) {
        Objects.requireNonNull(book, "BookCopy cannot be null!");
        if (!canBorrow()) {
            return false;
        }
        return borrowedBooks.add(book);
    }

    public boolean returnBook(BookCopy book) {
        Objects.requireNonNull(book, "BookCopy cannot be null!");
        return borrowedBooks.remove(book);
    }

    public int getBorrowedBookCount() {
        return borrowedBooks.size();
    }

    @Override
    public String toString() {
        return super.toString() + "\n"
             + "Role: " + getAccessLevel() + "\n"
             + "Membership Expiry: " + membershipExpiry + "\n"
             + "Borrowed Books: " + borrowedBooks.size() + "/" + MAX_BOOKS_BORROWED + "\n"
             + "Status: " + (isExpired() ? "Expired" : "Active");
    }
}
