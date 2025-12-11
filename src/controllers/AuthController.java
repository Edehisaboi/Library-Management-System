package controllers;

import authentication.Authenticator;
import authentication.AuthException;
import authentication.session.UserSession;
import infra.ConsoleView;

public class AuthController {
    private final Authenticator memberAuth;
    private final Authenticator librarianAuth;
    private final UserSession session;
    private final ConsoleView view;

    public AuthController(Authenticator memberAuth, Authenticator librarianAuth, UserSession session) {
        this.memberAuth = memberAuth;
        this.librarianAuth = librarianAuth;
        this.session = session;
        this.view = ConsoleView.getInstance();
    }

    public void processAuth() {
        while (!session.isLoggedIn()) {
            view.showMessage("""

                    ===== WELCOME TO THE LIBRARY =====
                    1. Login as Member
                    2. Login as Librarian
                    3. Register as Member
                    4. Register as Librarian
                    5. Continue as Guest
                    6. Exit
                    """);

            int choice = view.promptInt("Select an option", 1, 6);
            switch (choice) {
                case 1 -> login(memberAuth, "Member");
                case 2 -> login(librarianAuth, "Librarian");
                case 3 -> register(memberAuth, "Member");
                case 4 -> register(librarianAuth, "Librarian");
                case 5 -> {
                    return; // Proceed as Guest (Main will handle this if not logged in)
                }
                case 6 -> {
                    view.showMessage("Goodbye!");
                    System.exit(0);
                }
            }
        }
    }

    private void login(Authenticator auth, String role) {
        view.showMessage("\n=== " + role.toUpperCase() + " LOGIN ===");
        String email = view.promptString("Enter email");
        String password = view.promptString("Enter password");

        try {
            auth.login(email, password);
            view.showMessage("Logged in successfully!");
        } catch (AuthException e) {
            view.showError("Login failed, " + e.getMessage());
            view.pause();
        }
    }

    private void register(Authenticator auth, String role) {
        view.showMessage("\n=== " + role.toUpperCase() + " REGISTRATION ===");

        // Strict validation for new accounts
        String firstName = view.promptName("Enter first name");
        String lastName = view.promptName("Enter last name");
        String email = view.promptEmail("Enter email");
        String password = view.promptString("Enter password (min 8 chars)", "MIN", 8);

        try {
            auth.register(firstName, lastName, email, password);
            view.showMessage("Account created! You can now login.");
        } catch (AuthException e) {
            view.showError("Registration failed, " + e.getMessage());
        }
        view.pause();
    }
}
