package cli.screens;

import authentication.Authenticator;
import cli.Screen;
import cli.Navigator;
import cli.ScreenId;
import util.KeyboardReader;

import javax.naming.AuthenticationException;

public class Login implements Screen {
    private final KeyboardReader kbr = new KeyboardReader();

    private final Navigator nav;
    private final Authenticator memberAuth;
    private final Authenticator librarianAuth;

    public Login(Navigator navigator, Authenticator memberAuth, Authenticator librarianAuth) {
        this.nav = navigator;
        this.memberAuth = memberAuth;
        this.librarianAuth = librarianAuth;
    }

    @Override
    public void display() {
        System.out.println("""
                ==== LOGIN TYPE ====
                1. Member Login
                2. Librarian Login
                3. Return to Main Menu
                """.trim());

        int choice = kbr.getInt("Select an option", 1, 3);
        if (choice == 3) {
            nav.navigateTo(ScreenId.MAIN);
            return;
        }

        System.out.println("\n=== LOGIN ===");
        String email = kbr.getString("Enter email");
        String password = kbr.getString("Enter password");

        try {
            switch (choice) {
                case 1 -> {
                    memberAuth.login(email, password);
                    System.out.println("✅ Logged in successfully!");
                    nav.navigateTo(ScreenId.MEMBER_DASHBOARD);
                }
                case 2 -> {
                    librarianAuth.login(email, password);
                    System.out.println("✅ Logged in successfully!");
                    nav.navigateTo(ScreenId.LIBRARIAN_DASHBOARD);
                }
            }
        } catch (AuthenticationException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }
}
