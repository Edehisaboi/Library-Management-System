package controllers;

import authentication.*;
import authentication.session.UserSession;
import infrastructure.ConsoleView;

/**
 * Controller for handling User Authentication flows.
 * Manages Login and Registration for Members and Librarians.
 */
public class AuthController {
    private final Authenticator memberAuth;
    private final Authenticator librarianAuth;
    private final UserSession session;
    private final ConsoleView view;

    /**
     * Creates a new AuthController.
     *
     * @param memberAuth    authenticator for members
     * @param librarianAuth authenticator for librarians
     * @param session       session manager
     */
    public AuthController(Authenticator memberAuth, Authenticator librarianAuth, UserSession session) {
        this.memberAuth = memberAuth;
        this.librarianAuth = librarianAuth;
        this.session = session;
        this.view = ConsoleView.getInstance();
    }

    /**
     * Starts the main authentication loop.
     * Displays the welcome screen and handles login/register inputs.
     * Loops until a user is logged in or the application exits.
     */
    public void processAuth() {
        // Continue loop until a valid session is established
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
            // Route based on user selection
            switch (choice) {
                case 1 -> login(memberAuth, "Member");
                case 2 -> login(librarianAuth, "Librarian");
                case 3 -> register(memberAuth, "Member");
                case 4 -> register(librarianAuth, "Librarian");
                case 5 -> {
                    return; // Proceed as Guest (Main will handle this if not logged in)
                }
                case 6 -> {
                    // Graceful shutdown
                    view.showMessage("Goodbye!");
                    System.exit(0);
                }
            }
        }
    }

    /**
     * Handles the login flow for a specific user role.
     * Prompts for credentials and attempts to authenticate via the provided
     * Authenticator.
     *
     * @param auth the authenticator strategy to use (Member vs Librarian)
     * @param role the name of the role for display purposes
     */
    private void login(Authenticator auth, String role) {
        view.showMessage("\n=== " + role.toUpperCase() + " LOGIN ===");

        // Collect credentials
        String email = view.promptString("Enter email");
        String password = view.promptString("Enter password");

        try {
            // Attempt login - throws AuthException on failure
            auth.login(email, password);
            view.showMessage("Logged in successfully!");
        } catch (AuthException e) {
            // Handle known authentication errors (invalid pass, not found, etc.)
            view.showError("Login failed, " + e.getMessage());
            view.pause();
        }
    }

    /**
     * Handles the registration flow for a new user.
     * Collects personal details and creates a new account.
     *
     * @param auth the authenticator strategy to use
     * @param role the role being registered
     */
    private void register(Authenticator auth, String role) {
        view.showMessage("\n=== " + role.toUpperCase() + " REGISTRATION ===");

        // Strict validation for new accounts inputs
        String firstName = view.promptName("Enter first name");
        String lastName = view.promptName("Enter last name");
        String email = view.promptEmail("Enter email");
        // Enforce minimum password complexity (length only in this case)
        String password = view.promptString("Enter password (min 8 chars)", "MIN", 8);

        try {
            // Attempt to create the user account
            auth.register(firstName, lastName, email, password);
            view.showMessage("Account created! You can now login.");
        } catch (AuthException e) {
            // Handle registration errors (e.g., email already exists)
            view.showError("Registration failed, " + e.getMessage());
        }
        view.pause();
    }
}
