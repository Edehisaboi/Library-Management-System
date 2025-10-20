package books;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryBookRepository implements BookRepository {
    private final Map<UUID, Book> books = new HashMap<>();
    private final Map<UUID, BookCopy> copies = new HashMap<>();

    @Override
    public Book save(Book book) { books.put(book.getID(), book); return book; }

    @Override
    public Optional<Book> findById(UUID id) { return Optional.ofNullable(books.get(id)); }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return books.values().stream().filter(b -> b.getIsbn().equals(isbn)).findFirst();
    }

    @Override
    public List<Book> search(BookSearchSpec spec) {
        return books.values().stream()
                .filter(b -> {
                    boolean ok = true;

                    if (spec.titleLike.isPresent()) {
                        ok &= b.getTitle().toLowerCase()
                                .contains(spec.titleLike.get().toLowerCase());
                    }

                    if (spec.authorLike.isPresent()) {
                        String authorQuery = spec.authorLike.get().toLowerCase();
                        ok &= b.getAuthors().stream()
                                .anyMatch(a -> a.toLowerCase().contains(authorQuery));
                    }

                    if (spec.category.isPresent()) {
                        ok &= b.getCategories().contains(spec.category.get());
                    }

                    if (spec.isbn.isPresent()) {
                        String queryIsbn = spec.isbn.get().replaceAll("[-\\s]", "");
                        ok &= b.getIsbn().replaceAll("[-\\s]", "")
                                .equalsIgnoreCase(queryIsbn);
                    }

                    return ok;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        books.remove(id);
        copies.values().removeIf(c -> c.getBookId().equals(id));
    }

    @Override
    public BookCopy addCopy(UUID bookId) {
        if (!books.containsKey(bookId)) throw new NoSuchElementException("Book not found: " + bookId);
        BookCopy copy = new BookCopy(bookId);
        copies.put(copy.getCopyId(), copy);
        return copy;
    }

    @Override
    public List<BookCopy> findCopiesByBookId(UUID bookId) {
        return copies.values().stream().filter(c -> c.getBookId().equals(bookId)).collect(Collectors.toList());
    }

    @Override
    public Optional<BookCopy> findCopyById(UUID copyId) {
        return Optional.ofNullable(copies.get(copyId));
    }

    @Override
    public void updateCopy(BookCopy copy) {
        if (!copies.containsKey(copy.getCopyId())) throw new NoSuchElementException("Copy not found");
        copies.put(copy.getCopyId(), copy);
    }
}
