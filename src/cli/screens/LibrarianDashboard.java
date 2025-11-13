package cli.screens;

import authentication.Authenticator;
import authentication.UserSession;
import cli.Navigator;
import cli.Screen;
import cli.ScreenId;
import domain.user.User;
import util.KeyboardReader;

public class LibrarianDashboard implements Screen {
    private final KeyboardReader kbr = new KeyboardReader();
    private final Authenticator librarianAuth;
    private final Navigator nav;
    private final UserSession session;

    public LibrarianDashboard(Authenticator librarianAuth, Navigator navigator, UserSession session) {
        this.librarianAuth = librarianAuth;
        this.nav = navigator;
        this.session = session;
    }

    @Override
    public void display() {
        User librarian = session.getCurrentUser().orElseThrow(
                () -> new IllegalStateException("User is not logged in"));

        while (session.isLoggedIn()) {
            System.out.println("""
                    ===== LIBRARIAN DASHBOARD =====
                    1. Profile
                    2. Search Media
                    3. Manage Books
                    4. View Members
                    5. Logout
                    """.trim());

            int choice = kbr.getInt("Select an option", 1, 5);
            switch (choice) {
                case 1 -> System.out.println(librarian.toString());
                case 2 -> nav.navigateTo(ScreenId.SEARCH_MEDIA);
                case 3 -> System.out.println("Manage Books (not implemented)");
                case 4 -> System.out.println("Viewing members...");
                case 5 -> {
                    librarianAuth.logout(librarian);
                    System.out.println("Logged out successfully!");
                    nav.navigateTo(ScreenId.MAIN);
                    return;
                }
            }
        }
    }
}
