import authentication.Authenticator;
import authentication.LibrarianAuth;
import authentication.MemberAuth;
import authentication.session.UserSession;
import authentication.session.UserState;
import controllers.AdminController;
import controllers.AuthController;
import controllers.LibraryController;
import domain.user.Librarian;
import domain.user.Member;
import domain.user.User;
import policies.FinePolicy;
import policies.LoanRule;
import policies.fines.FlatFinePolicy;
import policies.rules.BookLoanRule;
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

public class Main {
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

        // 3. Policies & Services
        LoanRule loanRule = new BookLoanRule(14, 5);
        java.math.BigDecimal perDay = new java.math.BigDecimal("0.50");
        FinePolicy finePolicy = new FlatFinePolicy(perDay, 0);
        ClockProvider clock = ClockProvider.system();

        CatalogService catalog = new CatalogService(mediaRepo, invRepo);
        LoanService loanService = new LoanService(invRepo, loanRepo, loanRule, finePolicy, clock);

        // 4. Load Initial Data
        new LoadMedia(catalog).loadBooks("src/lib/book_metadata.csv", true);
        new LoadMedia(catalog).loadCDs("src/lib/cd_metadata.csv", true);

        // 5. Controllers
        AuthController authController = new AuthController(memberAuth, librarianAuth, session);
        LibraryController libController = new LibraryController(catalog, loanService, memberAuth);
        AdminController adminController = new AdminController(catalog, loanService, librarianAuth, userRepo);

        // 6. Main Application Loop
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
