package books;

import java.util.*;

public interface BookRepository {
    Book save(Book book);
    Optional<Book> findById(UUID id);
    Optional<Book> findByIsbn(String isbn);
    List<Book> search(BookSearchSpec spec);
    void deleteById(UUID id);

    // Copies
    BookCopy addCopy(UUID bookId);
    List<BookCopy> findCopiesByBookId(UUID bookId);
    Optional<BookCopy> findCopyById(UUID copyId);
    void updateCopy(BookCopy copy);
}
