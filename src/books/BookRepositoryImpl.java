package books;

import java.util.*;
import java.util.stream.Collectors;

public final class BookRepositoryImpl implements BookRepository {
    private final Map<UUID, Book> books = new HashMap<>();
    private final Map<UUID, BookCopy> copies = new HashMap<>();

    @Override
    public Book save(Book book) {
        books.put(book.getId(), book);
        return book;
    }

    @Override
    public Optional<Book> findById(UUID id) {
        return Optional.ofNullable(books.get(id));
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        if (isbn == null) {
            return Optional.empty();
        }
        String queryIsbn = isbn.replaceAll("[-\\s]", "");
        return books.values().stream()
                .filter(book -> book.getIsbn() != null
                        && book.getIsbn()
                               .replaceAll("[-\\s]", "")
                               .equalsIgnoreCase(queryIsbn))
                .findFirst();
    }

    @Override
    public List<Book> search(BookSearchSpec spec) {
        return books.values().stream()
                .filter(book -> {
                    boolean matches = true;

                    boolean hasTitle = spec.getTitleLike().isPresent();
                    boolean hasAuthor = spec.getAuthorLike().isPresent();

                    boolean titleMatch = !hasTitle
                            || book.getTitle()
                                   .toLowerCase()
                                   .contains(spec.getTitleLike().get().toLowerCase());

                    boolean authorMatch = !hasAuthor
                            || book.getAuthors().stream()
                                   .anyMatch(author ->
                                           author.toLowerCase()
                                                 .contains(spec.getAuthorLike().get().toLowerCase())
                                   );

                    if (hasTitle && hasAuthor) {
                        matches &= (titleMatch || authorMatch);
                    } else {
                        matches &= titleMatch && authorMatch;
                    }

                    if (spec.getCategory().isPresent()) {
                        matches &= book.getCategories().contains(spec.getCategory().get());
                    }

                    if (spec.getIsbn().isPresent()) {
                        String queryIsbn = spec.getIsbn().get().replaceAll("[-\\s]", "");
                        String bookIsbn = book.getIsbn() == null
                                ? ""
                                : book.getIsbn().replaceAll("[-\\s]", "");
                        matches &= bookIsbn.equalsIgnoreCase(queryIsbn);
                    }
                    return matches;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        books.remove(id);
        copies.values().removeIf(copy -> copy.getBookId().equals(id));
    }

    @Override
    public BookCopy addCopy(UUID bookId) {
        if (!books.containsKey(bookId)) {
            throw new NoSuchElementException("Book not found: " + bookId);
        }

        BookCopy copy = new BookCopy(bookId);
        copies.put(copy.getCopyId(), copy);
        return copy;
    }

    @Override
    public List<BookCopy> findCopiesByBookId(UUID bookId) {
        return copies.values().stream()
                .filter(copy -> copy.getBookId().equals(bookId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BookCopy> findCopyById(UUID copyId) {
        return Optional.ofNullable(copies.get(copyId));
    }

    @Override
    public void updateCopy(BookCopy copy) {
        if (!copies.containsKey(copy.getCopyId())) {
            throw new NoSuchElementException("Copy not found");
        }
        copies.put(copy.getCopyId(), copy);
    }
}
