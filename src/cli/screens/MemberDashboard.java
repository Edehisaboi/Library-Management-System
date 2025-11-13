package cli.screens;

import authentication.Authenticator;
import authentication.UserSession;
import cli.Navigator;
import cli.Screen;
import cli.ScreenId;
import domain.user.User;
import util.KeyboardReader;

public class MemberDashboard implements Screen {
    private final KeyboardReader reader = new KeyboardReader();
    private final Authenticator memberAuth;
    private final Navigator nav;
    private final UserSession session;

    public MemberDashboard(Authenticator memberAuth, Navigator navigator, UserSession session) {
        this.memberAuth = memberAuth;
        this.nav = navigator;
        this.session = session;
    }

    @Override
    public void display() {
        User member = session.getCurrentUser().orElseThrow(
                () -> new IllegalStateException("User is not logged in"));

        while (session.isLoggedIn()) {
            System.out.println("""
                    ===== MEMBER DASHBOARD =====
                    1. Profile
                    2. Search Media
                    3. Borrow a Book
                    4. Return a Book
                    5. View Borrowed Books
                    6. Logout
                    """.trim());

            int choice = reader.getInt("Select an option", 1, 6);
            switch (choice) {
                case 1 -> System.out.println(member.toString());
                case 2 -> nav.navigateTo(ScreenId.SEARCH_MEDIA);
                case 3 -> System.out.println("Borrowing a book...");
                case 4 -> System.out.println("Returning a book...");
                case 5 -> System.out.println("Borrowed books...");
                case 6 -> {
                    memberAuth.logout(member);
                    System.out.println("Logged out successfully!");
                    nav.navigateTo(ScreenId.MAIN);
                    return;
                }
            }
        }
    }
}
