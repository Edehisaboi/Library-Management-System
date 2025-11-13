package cli.screens;

import authentication.Authenticator;
import cli.Navigator;
import cli.Screen;
import cli.ScreenId;
import util.KeyboardReader;

import javax.naming.AuthenticationException;

public class Register implements Screen {
    private final KeyboardReader kbr = new KeyboardReader();
    private final Navigator nav;
    private final Authenticator memberAuth;
    private final Authenticator librarianAuth;

    public Register(Navigator navigator, Authenticator memberAuth, Authenticator librarianAuth) {
        this.nav = navigator;
        this.memberAuth = memberAuth;
        this.librarianAuth = librarianAuth;
    }

    @Override
    public void display() {
        System.out.println("""
                ===== REGISTER =====
                1. Register as Member
                2. Register as Librarian
                3. Return to Main Menu
                """.trim());

        int choice = kbr.getInt("Select an option", 1, 3);
        if (choice == 3) {
            nav.navigateTo(ScreenId.MAIN);
            return;
        }

        String firstName = kbr.getString("Enter first name");
        String lastName = kbr.getString("Enter last name");
        String email = kbr.getString("Enter email");
        String password = kbr.getString("Enter password (min 4 characters)", "MIN", 4);

        try {
            switch (choice) {
                case 1 -> {
                    memberAuth.register(firstName, lastName, email, password);
                    System.out.println("✅ Member account created successfully!");
                    nav.navigateTo(ScreenId.LOGIN);
                }
                case 2 -> {
                    librarianAuth.register(firstName, lastName, email, password);
                    System.out.println("✅ Librarian account created successfully!");
                    nav.navigateTo(ScreenId.LOGIN);
                }
            }
        } catch (AuthenticationException e) {
            System.out.println("Registration failed: " + e.getMessage());
            nav.navigateTo(ScreenId.REGISTER);
        }
    }
}
