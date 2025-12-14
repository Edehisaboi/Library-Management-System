import authentication.Authenticator;
import authentication.AuthFactory;
import authentication.session.UserSession;
import authentication.session.UserState;
import controllers.*;
import domain.user.*;
import policies.FinePolicy;
import policies.fines.FlatFinePolicy;
import policies.rules.StandardLoanRule;
import repo.*;
import repo.inmem.*;
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
        Authenticator memberAuth = AuthFactory.createMemberAuth(userRepo, session);
        Authenticator librarianAuth = AuthFactory.createLibrarianAuth(userRepo, session);

        // 3. Policies & Rules
        // Single rule for all items: 7 days loan period
        StandardLoanRule loanRule = new StandardLoanRule(loanRepo, 7);

        java.math.BigDecimal perDay = new java.math.BigDecimal("0.50");
        FinePolicy finePolicy = new FlatFinePolicy(perDay, 0);
        ClockProvider clock = ClockProvider.system();

        // 4. Services
        CatalogService catalog = new CatalogService(mediaRepo, invRepo);
        LoanService loanService = new LoanService(invRepo, loanRepo, loanRule, finePolicy, clock);

        // 5. Load Initial Data
        LoadMedia loader = new LoadMedia(catalog);
        loader.loadBooks("src/lib/book_metadata.csv", true);
        loader.loadCDs("src/lib/cd_metadata.csv", true);
        loader.loadDVDs("src/lib/dvd_metadata.csv", true);

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
