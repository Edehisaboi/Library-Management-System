import authentication.*;
import books.BookRepository;
import books.BookService;
import books.InMemoryBookRepository;
import screens.*;
import utilis.LoadBooks;

public class Main {
    public static void main(String[] args) {
//        UserSession session = new UserSession();
//
//        UserRepository userRepo = new InMemoryUserRepository();
//        Authenticator memberAuth = new MemberAuth(userRepo, session);
//        Authenticator librarianAuth = new LibrarianAuth(userRepo, session);
//
//        Manager screenManager = new Manager();
//
//        screenManager.register("main", new MainMenu(screenManager));
//        screenManager.register("login", new Login(screenManager, memberAuth, librarianAuth));
//        screenManager.register("register", new Register(screenManager, memberAuth, librarianAuth));
//        screenManager.register("memberDashboard", new MemberDashboard(memberAuth, screenManager, session));
//        screenManager.register("librarianDashboard", new LibrarianDashboard(librarianAuth, screenManager, session));
//        screenManager.register("guest", new GuestScreen());
//        screenManager.register("search", new Search(new BookService()));
//
//        screenManager.show("main");

        BookRepository bookRepo = new InMemoryBookRepository();
        int defaultNewCopiesPerTitle = 10;
        BookService bookService = new BookService(bookRepo, defaultNewCopiesPerTitle);

        String filePath = "src/lib/book_metadata.csv";
        LoadBooks loader = new LoadBooks(filePath, true);
        loader.load(bookService);

        Search search = new Search(bookService);
        search.display();
    }
}
