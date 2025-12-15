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
            // Show menu options
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
            // Handle user selection
            switch (choice) {
                case 1 -> view.showMessage(librarian.toString()); // Show profile details
                case 2 -> searchCatalog(); // Search items
                case 3 -> manageInventory(); // Add copies
                case 4 -> manageUsers(); // Block/Unblock members
                case 5 -> viewOverdueLoans(); // View borrowed items that are overdue
                case 6 -> {
                    // Perform logout and exit the dashboard loop
                    librarianAuth.logout(librarian);
                    return;
                }
            }
        }
    }

    /**
     * Handles the search catalog workflow for admins.
     * Uses the catalog service to find items and displays details if selected.
     */
    private void searchCatalog() {
        // Delegate to catalog service for interactive search and selection
        catalog.searchAndSelect(view).ifPresent(item -> {
            // If an item was selected, show its full details
            view.showMessage(item.details());
            view.pause();
        });
    }

    /**
     * Displays the inventory management menu and handles selection.
     * Allows the librarian to add physical copies to existing titles.
     */
    private void manageInventory() {
        while (true) {
            view.showMessage("""

                    ===== INVENTORY MANAGEMENT =====
                    1. Add Copies to Existing Title
                    2. Back to Dashboard
                    """);

            int choice = view.promptInt("Select an option", 1, 2);
            // Return to main dashboard if 'Back' is selected
            if (choice == 2)
                return;

            // Proceed to add copies workflow
            if (choice == 1) {
                addCopyToExisting();
            }
        }
    }

    /**
     * Allows adding physical copies to an existing title found by search.
     * Prompts for the number of copies and updates the inventory.
     */
    private void addCopyToExisting() {
        // Step 1: Find the target book/media item
        catalog.searchAndSelect(view).ifPresent(item -> {
            // Step 2: Ask for quantity
            int count = view.promptInt("Enter number of copies to add", 1, 50);
            try {
                // Step 3: Update inventory via service
                catalog.addCopies(item.getId(), count);
                view.showMessage("Successfully added " + count + " copies to: " + item.getTitle());
            } catch (Exception e) {
                // Handle potential errors (e.g., item not found concurrently)
                view.showError("Failed to add copies: " + e.getMessage());
            }
            view.pause();
        });
    }

    /**
     * Lists all registered members and provides options to manage them.
     * Filters out non-member users (like librarians) from the list.
     */
    private void manageUsers() {
        List<User> users = userRepo.findAll();
        // Filter: Keep only users who are instances of Member
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
        // Display each member with their index and status
        for (int i = 0; i < members.size(); i++) {
            Member m = members.get(i);
            String status = m.isBlocked() ? "[BLOCKED]" : "[ACTIVE]";
            view.showMessage(
                    (i + 1) + ". " + m.getFirstName() + " " + m.getLastName() + " (" + m.getEmail() + ") " + status);
        }

        view.showMessage("0. Back");
        // Prompt for selection to perform actions on a specific member
        int idx = view.promptInt("Select member to update (0 to cancel)", 0, members.size());
        if (idx == 0)
            return;

        // Retrieve the selected member (adjusting for 0-based index)
        Member selected = members.get(idx - 1);
        updateMember(selected);
    }

    /**
     * Toggles the blocked status of a selected member.
     *
     * @param member the member to update
     */
    private void updateMember(Member member) {
        view.showMessage("\nUpdating: " + member.getFirstName() + " " + member.getLastName());
        // Show context-aware option (Unblock if blocked, Block if active)
        view.showMessage("1. " + (member.isBlocked() ? "Unblock Member" : "Block Member"));
        view.showMessage("2. Back");

        int choice = view.promptInt("Choose action", 1, 2);
        if (choice == 1) {
            // Toggle status based on current state
            if (member.isBlocked()) {
                member.unblock();
                view.showMessage("Member unblocked.");
            } else {
                member.block();
                view.showMessage("Member blocked.");
            }
        }
    }

    /**
     * Displays all loans that are currently overdue.
     * Uses the loan service to fetch data based on the current date.
     */
    private void viewOverdueLoans() {
        // Fetch overdue loans from service
        var overdue = loans.overdueLoans();

        if (overdue.isEmpty()) {
            view.showMessage("No overdue loans.");
        } else {
            view.showMessage("\nOverdue Loans:");
            // Iterate and display details for each overdue loan
            overdue.forEach(l -> view.showMessage(
                    "Loan: " + l.getId() + " | Borrower: " + l.getBorrower().getEmail() + " | Due: " + l.getDueOn()));
        }
        view.pause();
    }
}
