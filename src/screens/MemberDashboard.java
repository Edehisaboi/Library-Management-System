package screens;

import authentication.Authenticator;
import authentication.UserSession;
import users.Base;
import utilis.KeyboardReader;

public class MemberDashboard implements Screen {
    private final KeyboardReader reader = new KeyboardReader();
    private final Authenticator memberAuth;
    private final Manager manager;
    private final UserSession session;

    public MemberDashboard(Authenticator memberAuth, Manager manager, UserSession session) {
        this.memberAuth = memberAuth;
        this.manager = manager;
        this.session = session;
    }

    @Override
    public void display() {
        Base member = session.getCurrentUser().orElseThrow(
                () -> new IllegalStateException("User is not logged in")
        );

        while (session.isAuthenticated()) {
            System.out.println(
                    """
                    ===== MEMBER DASHBOARD =====
                    1. Profile
                    2. Search Books
                    3. Borrow a Book
                    4. Return a Book
                    5. View Borrowed Books
                    6. Logout
                    """
            );

            int choice = reader.getInt("Select an option", 1, 6);
            switch (choice) {
                case 1 -> System.out.println(member.toString());
                case 2 -> manager.show("search");
                case 3 -> System.out.println("Borrowing a book...");
                case 4 -> System.out.println("Returning a book...");
                case 5 -> System.out.println("Borrowed books...");
                case 6 -> {
                    memberAuth.logout(member);
                    System.out.println("Logged out successfully!");
                    return;
                }
            }
        }
    }
}
