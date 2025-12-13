package controllers;

import authentication.Authenticator;
import domain.user.*;
import infrastructure.ConsoleView;
import repo.UserRepository;
import services.*;

import java.util.List;

/**
 * Controller for Librarian-specific actions.
 * Handles inventory management, user management, and administrative reports.
 */
public class AdminController {
    private final CatalogService catalog;
    private final LoanService loans;
    private final Authenticator librarianAuth;
    private final UserRepository userRepo;
    private final ConsoleView view;

    /**
     * Creates a new AdminController.
     *
     * @param catalog       service for managing media
     * @param loans         service for managing loans
     * @param librarianAuth authenticator for librarian actions
     * @param userRepo      repository for user management
     */
    public AdminController(CatalogService catalog, LoanService loans, Authenticator librarianAuth,
            UserRepository userRepo) {
        this.catalog = catalog;
        this.loans = loans;
        this.librarianAuth = librarianAuth;
        this.userRepo = userRepo;
        this.view = ConsoleView.getInstance();
    }

    /**
     * Displays the main dashboard for a logged-in Librarian.
     * 
     * @param librarian the current librarian
     */
    public void librarianDashboard(Librarian librarian) {
        while (true) {
            view.showMessage("""

                    ===== LIBRARIAN DASHBOARD =====
                    1. Profile
                    2. Search Catalog
                    3. Manage Inventory
                    4. Manage Users
                    5. View Overdue Loans
                    6. Logout
                    """);

            int choice = view.promptInt("Select an option", 1, 6);
            switch (choice) {
                case 1 -> view.showMessage(librarian.toString());
                case 2 -> searchCatalog();
                case 3 -> manageInventory();
                case 4 -> manageUsers();
                case 5 -> viewOverdueLoans();
                case 6 -> {
                    librarianAuth.logout(librarian);
                    return;
                }
            }
        }
    }

    // Handles the search catalog workflow for admins
    private void searchCatalog() {
        catalog.searchAndSelect(view).ifPresent(item -> {
            view.showMessage(item.details());
            view.pause();
        });
    }

    // Displays the inventory management menu and handles selection
    private void manageInventory() {
        while (true) {
            view.showMessage("""

                    ===== INVENTORY MANAGEMENT =====
                    1. Add Copies to Existing Title
                    2. Back to Dashboard
                    """);

            int choice = view.promptInt("Select an option", 1, 2);
            if (choice == 2)
                return;

            if (choice == 1) {
                addCopyToExisting();
            }
        }
    }

    // Allows adding physical copies to an existing title found by search
    private void addCopyToExisting() {
        // Simple search to find the book first
        catalog.searchAndSelect(view).ifPresent(item -> {
            int count = view.promptInt("Enter number of copies to add", 1, 50);
            try {
                catalog.addCopies(item.getId(), count);
                view.showMessage("Successfully added " + count + " copies to: " + item.getTitle());
            } catch (Exception e) {
                view.showError("Failed to add copies: " + e.getMessage());
            }
            view.pause();
        });
    }

    // Lists all registered members and provides options to manage them
    private void manageUsers() {
        List<User> users = userRepo.findAll();
        // Filter only Member objects, ignore Librarians
        List<Member> members = users.stream()
                .filter(u -> u instanceof Member)
                .map(u -> (Member) u)
                .toList();

        if (members.isEmpty()) {
            view.showMessage("No registered members found.");
            view.pause();
            return;
        }

        view.showMessage("\n===== MEMBER LIST =====");
        for (int i = 0; i < members.size(); i++) {
            Member m = members.get(i);
            String status = m.isBlocked() ? "[BLOCKED]" : "[ACTIVE]";
            view.showMessage(
                    (i + 1) + ". " + m.getFirstName() + " " + m.getLastName() + " (" + m.getEmail() + ") " + status);
        }

        view.showMessage("0. Back");
        int idx = view.promptInt("Select member to update (0 to cancel)", 0, members.size());
        if (idx == 0)
            return;

        Member selected = members.get(idx - 1);
        updateMember(selected);
    }

    // Toggles the blocked status of a selected member
    private void updateMember(Member member) {
        view.showMessage("\nUpdating: " + member.getFirstName() + " " + member.getLastName());
        view.showMessage("1. " + (member.isBlocked() ? "Unblock Member" : "Block Member"));
        view.showMessage("2. Back");

        int choice = view.promptInt("Choose action", 1, 2);
        if (choice == 1) {
            if (member.isBlocked()) {
                member.unblock();
                view.showMessage("Member unblocked.");
            } else {
                member.block();
                view.showMessage("Member blocked.");
            }
        }
    }

    // Displays all loans that are currently overdue
    private void viewOverdueLoans() {
        var overdue = loans.overdueLoans();
        if (overdue.isEmpty()) {
            view.showMessage("No overdue loans.");
        } else {
            view.showMessage("\nOverdue Loans:");
            overdue.forEach(l -> view.showMessage(
                    "Loan: " + l.getId() + " | Borrower: " + l.getBorrower().getEmail() + " | Due: " + l.getDueOn()));
        }
        view.pause();
    }
}
