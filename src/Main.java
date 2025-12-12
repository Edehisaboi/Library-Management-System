import authentication.Authenticator;
import authentication.LibrarianAuth;
import authentication.MemberAuth;
import authentication.session.UserSession;
import authentication.session.UserState;
import controllers.AdminController;
import controllers.AuthController;
import controllers.LibraryController;
import domain.media.Book;
import domain.media.CD;
import domain.media.DVD;
import domain.user.Librarian;
import domain.user.Member;
import domain.user.User;
import policies.FinePolicy;
import policies.rules.LoanDispatcher;
import policies.LoanRule;
import policies.fines.FlatFinePolicy;
import policies.rules.BookLoanRule;
import policies.rules.CDLoanRule;
import policies.rules.DVDLoanRule;
import repo.InventoryRepository;
import repo.LoanRepository;
import repo.MediaRepository;
import repo.UserRepository;
import repo.inmem.InMemoryInventoryRepository;
import repo.inmem.InMemoryLoanRepository;
import repo.inmem.InMemoryMediaRepository;
import repo.inmem.InMemoryUserRepository;
import services.CatalogService;
import services.LoanService;
import util.ClockProvider;
import util.LoadMedia;

import java.util.Optional;

/**
 * Main entry point for the Library Management System.
 * Handles dependency injection and the main application loop.
 */
public class Main {
    /**
     * Application start method.
     * Wires repositories, services, and controllers, then starts the main loop.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // 1. Wiring Repositories
        UserRepository userRepo = new InMemoryUserRepository();
        MediaRepository mediaRepo = new InMemoryMediaRepository();
        InventoryRepository invRepo = new InMemoryInventoryRepository();
        LoanRepository loanRepo = new InMemoryLoanRepository();

        // 2. Authentication & Session
        UserSession session = new UserState();
        Authenticator memberAuth = new MemberAuth(userRepo, session);
        Authenticator librarianAuth = new LibrarianAuth(userRepo, session);

        // 3. Policies & Rules
        // Create specific rules
        // Book: 14 days loan, checks active loans vs member limit using loanRepo
        LoanRule bookRule = new BookLoanRule(loanRepo, 14);
        // DVD: 7 days loan, also checks limits
        LoanRule dvdRule = new DVDLoanRule(loanRepo, 7);
        // CD: 7 days loan, also checks limits
        LoanRule cdRule = new CDLoanRule(loanRepo, 7);

        // Create Dispatcher and register rules
        LoanDispatcher loanDispatcher = new LoanDispatcher();
        loanDispatcher.register(Book.class, bookRule);
        loanDispatcher.register(DVD.class, dvdRule);
        loanDispatcher.register(CD.class, cdRule);

        java.math.BigDecimal perDay = new java.math.BigDecimal("0.50");
        FinePolicy finePolicy = new FlatFinePolicy(perDay, 0);
        ClockProvider clock = ClockProvider.system();

        // 4. Services
        CatalogService catalog = new CatalogService(mediaRepo, invRepo);
        LoanService loanService = new LoanService(invRepo, loanRepo, loanDispatcher, finePolicy, clock);

        // 5. Load Initial Data
        new LoadMedia(catalog).loadBooks("src/lib/book_metadata.csv", true);
        new LoadMedia(catalog).loadCDs("src/lib/cd_metadata.csv", true);

        // 6. Controllers
        AuthController authController = new AuthController(memberAuth, librarianAuth, session);
        LibraryController libController = new LibraryController(catalog, loanService, memberAuth);
        AdminController adminController = new AdminController(catalog, loanService, librarianAuth, userRepo);

        // 7. Main Application Loop
        while (true) {
            if (!session.isLoggedIn()) {
                authController.processAuth();

                // If still not logged in after processAuth returns,
                // it might mean user chose Guest Search
                if (!session.isLoggedIn()) {
                    libController.guestDashboard();
                }
            } else {
                // User is logged in
                Optional<User> currentUser = session.getCurrentUser();
                if (currentUser.isPresent()) {
                    User user = currentUser.get();
                    if (user instanceof Member m) {
                        libController.memberDashboard(m);
                    } else if (user instanceof Librarian l) {
                        adminController.librarianDashboard(l);
                    }
                }
            }
        }
    }
}
