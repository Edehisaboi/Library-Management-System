package screens;

import books.*;
import utilis.KeyboardReader;

import java.util.List;


public class Search implements Screen {
    private final KeyboardReader kbr = new KeyboardReader();
    private final BookService bookService;

    public Search(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public void display() {
        String query = kbr.getString("Enter search query (title/author)");

        BookSearchSpec spec = BookSearchSpec.quick(query);
        List<Book> books = bookService.search(spec);

        if (books.isEmpty()) {
            System.out.println("No books found.");
            return;
        }

        for (Book book : books) {
            System.out.println("*****************************************");
            System.out.println(book.toString());
        }
    }
}
