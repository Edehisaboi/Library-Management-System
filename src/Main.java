import authentication.Authenticator;
import authentication.LibrarianAuth;
import authentication.MemberAuth;
import authentication.UserSession;
import authentication.UserState;
import cli.Navigator;
import cli.Screen;
import cli.ScreenId;
import cli.screens.GuestDashboard;
import cli.screens.LibrarianDashboard;
import cli.screens.Login;
import cli.screens.MainMenu;
import cli.screens.MemberDashboard;
import cli.screens.Register;
import cli.screens.Search;
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
import util.LoadBooks;

public class Main {
    public static void main(String[] args) {
        // Wiring: repositories
        UserRepository userRepo = new InMemoryUserRepository();
        MediaRepository mediaRepo = new InMemoryMediaRepository();
        InventoryRepository invRepo = new InMemoryInventoryRepository();
        LoanRepository loanRepo = new InMemoryLoanRepository();

        // Session and authentication
        UserSession session = new UserState();
        Authenticator memberAuth = new MemberAuth(userRepo, session);
        Authenticator librarianAuth = new LibrarianAuth(userRepo, session);

        // Policies and services
        LoanRule loanRule = new BookLoanRule(14, 5);
        java.math.BigDecimal perDay = new java.math.BigDecimal("0.50");
        FinePolicy finePolicy = new FlatFinePolicy(perDay, 0);
        ClockProvider clock = ClockProvider.system();

        CatalogService catalog = new CatalogService(mediaRepo, invRepo);
        LoanService loanService = new LoanService(invRepo, loanRepo, loanRule, finePolicy, clock);

        // Load initial media from CSV
        new LoadBooks("src/lib/book_metadata.csv", true).load(catalog, 5);

        // Navigator and screens
        Navigator nav = new Navigator();
        Screen mainMenu = new MainMenu(nav);
        Screen login = new Login(nav, memberAuth, librarianAuth);
        Screen register = new Register(nav, memberAuth, librarianAuth);
        Screen guest = new GuestDashboard(nav);
        Screen memberDash = new MemberDashboard(memberAuth, nav, session);
        Screen librarianDash = new LibrarianDashboard(librarianAuth, nav, session);
        Screen search = new Search(catalog);

        // Register screens
        nav.register(ScreenId.MAIN, mainMenu);
        nav.register(ScreenId.LOGIN, login);
        nav.register(ScreenId.REGISTER, register);
        nav.register(ScreenId.GUEST_DASHBOARD, guest);
        nav.register(ScreenId.MEMBER_DASHBOARD, memberDash);
        nav.register(ScreenId.LIBRARIAN_DASHBOARD, librarianDash);
        nav.register(ScreenId.SEARCH_MEDIA, search);

        // Start app
        nav.navigateTo(ScreenId.MAIN);
    }
}
