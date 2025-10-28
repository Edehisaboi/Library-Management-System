package screens;

import authentication.Authenticator;
import authentication.UserSession;
import users.Base;
import utilis.KeyboardReader;

public class LibrarianDashboard implements Screen {
    private final KeyboardReader kbr = new KeyboardReader();
    private final Authenticator librarianAuth;
    private final Manager manager;
    private final UserSession session;

    public LibrarianDashboard(Authenticator librarianAuth, Manager manager, UserSession session) {
        this.librarianAuth = librarianAuth;
        this.manager = manager;
        this.session = session;
    }

    @Override
    public void display() {
        Base librarian = session.getCurrentUser().orElseThrow(
                () -> new IllegalStateException("User is not logged in")
        );

        while (session.isAuthenticated()) {
            System.out.println(
                    """
                    ===== LIBRARIAN DASHBOARD =====
                    1. Profile
                    2. Search Books
                    3. Manage Books
                    4. View Members
                    5. Logout
                    """
            );

            int choice = kbr.getInt("Select an option", 1, 5);
            switch (choice) {
                case 1 -> System.out.println(librarian.toString());
                case 2 -> manager.show("search");
                case 3 -> manager.show("manageBooks");
                case 4 -> {
                    librarianAuth.logout(librarian);
                    System.out.println("Logged out successfully!");
                    return;
                }
            }
        }
    }
}
