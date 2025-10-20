package books;

import java.util.*;

public class BookService {
    private final BookRepository repo;
    private final int defaultNewCopies;

    public BookService(BookRepository repo, int defaultNewCopies) {
        this.repo = Objects.requireNonNull(repo);
        this.defaultNewCopies = defaultNewCopies;
    }

    public Book addNewTitle(Book book, int initialCopies) {
        repo.findByIsbn(book.getIsbn()).ifPresent(b -> {
            throw new IllegalArgumentException("ISBN already exists: " + b.getIsbn());
        });
        Book saved = repo.save(book);
        int count = Math.max(0, initialCopies);
        for (int i = 0; i < count; i++) repo.addCopy(saved.getID());
        return saved;
    }

    public Book addNewTitle(Book book) {
        return addNewTitle(book, defaultNewCopies);
    }

    public List<Book> search(BookSearchSpec spec) { return repo.search(spec); }

    public int availableCopies(UUID bookId) {
        return (int) repo.findCopiesByBookId(bookId).stream()
                .filter(c -> c.getStatus() == Status.AVAILABLE).count();
    }

    public BookCopy markCopyStatus(UUID copyId, Status status) {
        BookCopy copy = repo.findCopyById(copyId).orElseThrow();
        copy.setStatus(status);
        repo.updateCopy(copy);
        return copy;
    }
}
