package screens;

import authentication.Authenticator;
import utilis.KeyboardReader;

import javax.naming.AuthenticationException;


public class Register implements Screen {
    private final KeyboardReader kbr = new KeyboardReader();
    private final Manager manager;
    private final Authenticator memberAuth;
    private final Authenticator librarianAuth;

    public Register(Manager manager, Authenticator memberAuth, Authenticator librarianAuth) {
        this.manager = manager;
        this.memberAuth = memberAuth;
        this.librarianAuth = librarianAuth;
    }

    @Override
    public void display() {
        System.out.println(
                """
                ===== REGISTER =====
                1. Register as Member
                2. Register as Librarian
                3. Return to Main Menu
                """
        );

        int choice = kbr.getInt("Select an option", 1, 3);
        if (choice == 3) {
            manager.show("main");
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
                    manager.show("login");
                }
                case 2 -> {
                    librarianAuth.register(firstName, lastName, email, password);
                    System.out.println("✅ Librarian account created successfully!");
                    manager.show("login");
                }
            }
        } catch (AuthenticationException e) {
            System.out.println("Registration failed: " + e.getMessage());
            manager.show("register");
        }
    }
}
