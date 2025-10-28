package screens;

import authentication.*;
import utilis.KeyboardReader;

import javax.naming.AuthenticationException;


public class Login implements Screen {
    private final KeyboardReader kbr = new KeyboardReader();

    private final Manager manager;
    private final Authenticator memberAuth;
    private final Authenticator librarianAuth;


    public Login(Manager manager, Authenticator memberAuth, Authenticator librarianAuth) {
        this.manager = manager;
        this.memberAuth = memberAuth;
        this.librarianAuth = librarianAuth;
    }

    @Override
    public void display() {
        System.out.println(
                """
                ==== LOGIN TYPE ====
                1. Member Login
                2. Librarian Login
                3. Return to Main Menu
                """
        );

        int choice = kbr.getInt("Select login type", 1, 3);
        if (choice == 3) {
            manager.show("main");
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
                    manager.show("memberDashboard");
                }
                case 2 -> {
                    librarianAuth.login(email, password);
                    System.out.println("✅ Logged in successfully!");
                    manager.show("librarianDashboard");
                }
            }
        } catch (AuthenticationException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }
}
