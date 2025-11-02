package books;

import users.Member;

import java.util.*;
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

public class BookService {
    private final BookRepository bookRepo;
    private final int defaultNewCopiesPerTitle;

    private final Map<UUID, ActiveBorrow> activeBorrows = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> availableCopiesCache = new ConcurrentHashMap<>();

    public BookService(BookRepository repo, int defaultNewCopies) {
        this.bookRepo = Objects.requireNonNull(repo);
        this.defaultNewCopiesPerTitle = defaultNewCopies;
    }

    public Book addNewTitle(Book book, int initialCopies) {
        bookRepo.findByIsbn(book.getIsbn()).ifPresent(b -> {
            throw new IllegalArgumentException("ISBN already exists: " + b.getIsbn());
        });
        Book saved = bookRepo.save(book);
        for (int i = 0; i < Math.max(0, initialCopies); i++) bookRepo.addCopy(saved.getId());

        availableCopiesCache.put(saved.getId(), initialCopies);
        return saved;
    }

    public Book addNewTitle(Book book) {
        return addNewTitle(book, defaultNewCopiesPerTitle);
    }

    public List<Book> search(BookSearchSpec spec) {
        return bookRepo.search(spec == null ? BookSearchSpec.builder().build() : spec);
    }

    public BookCopy markCopyStatus(UUID copyId, Status status) {
        BookCopy copy = bookRepo.findCopyById(copyId).orElseThrow();
        copy.setStatus(status);
        bookRepo.updateCopy(copy);
        return copy;
    }

    public synchronized ActiveBorrow borrowCopy(UUID bookId, Member member, int days) {
        Objects.requireNonNull(bookId, "Book ID required!");
        Objects.requireNonNull(member, "Member required!");

        if (!member.canBorrow()) {
            throw new IllegalStateException("Member cannot borrow! Borrow limit reached or membership expired.");
        }

        if (
                member.getBorrowedBooks().stream()
                        .anyMatch(copy -> copy.getBookId().equals(bookId))
        ) {
            throw new IllegalStateException("Member already has a copy of this book!");
        }

        ensureAvailabilityInitialized(bookId);

        if (availableCopiesCache.getOrDefault(bookId, 0) <= 0) {
            throw new IllegalStateException("No copies available for this book!");
        }

        List<BookCopy> copies = bookRepo.findCopiesByBookId(bookId);
        BookCopy chosenCopy = copies.stream()
                .filter(c -> c.getStatus() == Status.AVAILABLE && !activeBorrows.containsKey(c.getCopyId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No available copies found!"));

        if (!member.borrowBook(chosenCopy)) {
            throw new IllegalStateException("Member cannot borrow this book!");
        }

        ActiveBorrow borrow = new ActiveBorrow(bookId, chosenCopy.getCopyId(), member.getId(), LocalDate.now(), LocalDate.now().plusDays(days));

        activeBorrows.put(chosenCopy.getCopyId(), borrow);
        availableCopiesCache.put(bookId, availableCopiesCache.getOrDefault(bookId, 0) - 1);

        return borrow;
    }

    public synchronized  boolean returnCopy(UUID copyId, Member member) {
        Objects.requireNonNull(copyId, "Copy ID required!");
        Objects.requireNonNull(member, "Member required!");

        ActiveBorrow borrow = activeBorrows.get(copyId);
        if (borrow == null) return false;
        if (!borrow.memberId().equals(member.getId())){
            throw new IllegalStateException("Only the borrower can return this copy!");
        }

        BookCopy copy = bookRepo.findCopyById(copyId)
                        .orElseThrow(() -> new IllegalStateException("Copy not found!"));

        boolean returnedCopy = member.returnBook(copy);

        activeBorrows.remove(copyId);
        availableCopiesCache.merge(copy.getBookId(), 1, Integer::sum);

        return returnedCopy;
    }

    public int availableCopies(UUID bookId) {
        ensureAvailabilityInitialized(bookId);
        return Math.max(0, availableCopiesCache.getOrDefault(bookId, 0));
    }

    public List<ActiveBorrow> borrowsByMember(UUID memberId) {
        Objects.requireNonNull(memberId, "Member ID required!");
        return activeBorrows.values().stream()
                .filter(b -> b.memberId().equals(memberId))
                .sorted(Comparator.comparing(ActiveBorrow::dueDate))
                .collect(Collectors.toList());
    }

    public boolean isCopyBorrowed(UUID copyId) {
        return activeBorrows.containsKey(copyId);
    }

    private void ensureAvailabilityInitialized(UUID bookId) {
        availableCopiesCache.computeIfAbsent(bookId, id -> {
            List<BookCopy> copies = bookRepo.findCopiesByBookId(id);
            long out = copies.stream().filter(c -> activeBorrows.containsKey(c.getCopyId())).count();
            return Math.max(0, copies.size() - (int) out);
        });
    }
}
