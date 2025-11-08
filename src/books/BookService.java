package books;

import users.Member;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class BookService {
    private final BookRepository bookRepo;
    private final int defaultNewCopiesPerTitle;

    private final Map<UUID, ActiveBorrow> activeBorrows = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> availableCopiesCache = new ConcurrentHashMap<>();

    public BookService(BookRepository repo, int defaultNewCopies) {
        this.bookRepo = Objects.requireNonNull(repo, "Book repository is required!");
        this.defaultNewCopiesPerTitle = defaultNewCopies;
    }

    public Book addNewTitle(Book book, int initialCopies) {
        Objects.requireNonNull(book, "Book is required!");

        bookRepo.findByIsbn(book.getIsbn()).ifPresent(existing -> {
            throw new IllegalArgumentException("ISBN already exists: " + existing.getIsbn());
        });

        Book saved = bookRepo.save(book);
        int copiesToCreate = Math.max(0, initialCopies);

        for (int i = 0; i < copiesToCreate; i++) {
            bookRepo.addCopy(saved.getId());
        }

        availableCopiesCache.put(saved.getId(), computeAvailableCopies(saved.getId()));
        return saved;
    }

    public Book addNewTitle(Book book) {
        return addNewTitle(book, defaultNewCopiesPerTitle);
    }

    public List<Book> search(BookSearchSpec spec) {
        BookSearchSpec effectiveSpec = spec == null ? BookSearchSpec.builder().build() : spec;
        return bookRepo.search(effectiveSpec);
    }

    public BookCopy markCopyStatus(UUID copyId, Status status) {
        Objects.requireNonNull(copyId, "Copy ID required!");
        Objects.requireNonNull(status, "Status required!");

        BookCopy copy = bookRepo.findCopyById(copyId)
                .orElseThrow(() -> new NoSuchElementException("Copy not found: " + copyId));

        copy.setStatus(status);
        bookRepo.updateCopy(copy);

        availableCopiesCache.put(copy.getBookId(), computeAvailableCopies(copy.getBookId()));
        return copy;
    }

    public synchronized ActiveBorrow borrowCopy(UUID bookId, Member member, int days) {
        Objects.requireNonNull(bookId, "Book ID required!");
        Objects.requireNonNull(member, "Member required!");

        if (!member.canBorrow()) {
            throw new IllegalStateException("Member cannot borrow! Borrow limit reached or membership expired.");
        }

        boolean alreadyHasCopy = member.getBorrowedBooks().stream()
                .anyMatch(copy -> copy.getBookId().equals(bookId));

        if (alreadyHasCopy) {
            throw new IllegalStateException("Member already has a copy of this book!");
        }

        ensureAvailabilityInitialized(bookId);

        if (availableCopiesCache.getOrDefault(bookId, 0) <= 0) {
            throw new IllegalStateException("No copies available for this book!");
        }

        List<BookCopy> copies = bookRepo.findCopiesByBookId(bookId);

        BookCopy chosenCopy = copies.stream()
                .filter(copy ->
                        copy.getStatus() == Status.AVAILABLE
                                && !activeBorrows.containsKey(copy.getCopyId())
                )
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No available copies found!"));

        if (!member.borrowBook(chosenCopy)) {
            throw new IllegalStateException("Member cannot borrow this book!");
        }

        markCopyStatus(chosenCopy.getCopyId(), Status.BORROWED);

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(days);

        ActiveBorrow borrow = new ActiveBorrow(
                bookId,
                chosenCopy.getCopyId(),
                member.getId(),
                borrowDate,
                dueDate
        );

        activeBorrows.put(chosenCopy.getCopyId(), borrow);
        availableCopiesCache.put(bookId, computeAvailableCopies(bookId));

        return borrow;
    }

    public synchronized boolean returnCopy(UUID copyId, Member member) {
        Objects.requireNonNull(copyId, "Copy ID required!");
        Objects.requireNonNull(member, "Member required!");

        ActiveBorrow borrow = activeBorrows.get(copyId);
        if (borrow == null) {
            return false;
        }

        if (!borrow.memberId().equals(member.getId())) {
            throw new IllegalStateException("Only the borrower can return this copy!");
        }

        BookCopy copy = bookRepo.findCopyById(copyId)
                .orElseThrow(() -> new IllegalStateException("Copy not found!"));

        boolean returned = member.returnBook(copy);

        markCopyStatus(copyId, Status.AVAILABLE);

        activeBorrows.remove(copyId);
        availableCopiesCache.put(copy.getBookId(), computeAvailableCopies(copy.getBookId()));

        return returned;
    }

    public int availableCopies(UUID bookId) {
        Objects.requireNonNull(bookId, "Book ID required!");
        ensureAvailabilityInitialized(bookId);
        return Math.max(0, availableCopiesCache.getOrDefault(bookId, 0));
    }

    public List<ActiveBorrow> borrowsByMember(UUID memberId) {
        Objects.requireNonNull(memberId, "Member ID required!");

        return activeBorrows.values().stream()
                .filter(borrow -> borrow.memberId().equals(memberId))
                .sorted(Comparator.comparing(ActiveBorrow::dueDate))
                .collect(Collectors.toList());
    }

    public boolean isCopyBorrowed(UUID copyId) {
        Objects.requireNonNull(copyId, "Copy ID required!");
        return activeBorrows.containsKey(copyId);
    }

    public Optional<Book> findBookById(UUID id) {
        Objects.requireNonNull(id, "Book ID required!");
        return bookRepo.findById(id);
    }

    public Optional<ActiveBorrow> findBorrowByCopyId(UUID copyId) {
        Objects.requireNonNull(copyId, "Copy ID required!");
        return Optional.ofNullable(activeBorrows.get(copyId));
    }

    private void ensureAvailabilityInitialized(UUID bookId) {
        availableCopiesCache.computeIfAbsent(bookId, this::computeAvailableCopies);
    }

    private int computeAvailableCopies(UUID bookId) {
        List<BookCopy> copies = bookRepo.findCopiesByBookId(bookId);

        long available = copies.stream()
                .filter(copy ->
                        copy.getStatus() == Status.AVAILABLE
                                && !activeBorrows.containsKey(copy.getCopyId())
                )
                .count();

        return (int) Math.max(0, available);
    }
}
