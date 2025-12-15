package controllers;

import authentication.Authenticator;
import domain.loan.Loan;
import domain.media.MediaItem;
import domain.user.Member;
import infrastructure.ConsoleView;
import services.*;

import java.util.List;

/**
 * Controller for general library operations accessible by Members and Guests.
 * Handles Searching, Borrowing, and Returning items.
 */
public class LibraryController {
    private final CatalogService catalog;
    private final LoanService loans;
    private final Authenticator memberAuth;
    private final ConsoleView view;

    /**
     * Creates a new LibraryController.
     *
     * @param catalog    service for catalog operations
     * @param loans      service for loan operations
     * @param memberAuth authenticator for member actions (logout)
     */
    public LibraryController(CatalogService catalog, LoanService loans,
            Authenticator memberAuth) {
        this.catalog = catalog;
        this.loans = loans;
        this.memberAuth = memberAuth;
        this.view = ConsoleView.getInstance();
    }

    /**
     * Displays the dashboard for Guest users.
     * Limited to searching the catalog.
     */
    public void guestDashboard() {
        while (true) {
            view.showMessage("""

                    ===== GUEST DASHBOARD =====
                    1. Search Catalog
                    2. Back to Main Menu
                    """);

            int choice = view.promptInt("Select an option", 1, 2);
            if (choice == 1) {
                searchCatalog(null); // Pass null to indicate guest mode
            } else {
                return; // Exit to main loop (which will likely re-trigger auth or exit)
            }
        }
    }

    /**
     * Displays the dashboard for logged-in Members.
     * Full access to search, borrow, return, and profile.
     * 
     * @param member the currently logged-in member
     */
    public void memberDashboard(Member member) {
        while (true) {
            view.showMessage("""

                    ===== MEMBER DASHBOARD =====
                    1. Profile
                    2. Search Catalog
                    3. View My Loans
                    4. Logout
                    """);

            int choice = view.promptInt("Select an option", 1, 4);
            switch (choice) {
                case 1 -> view.showMessage(member.toString()); // Show profile info
                case 2 -> searchCatalog(member); // Browse items
                case 3 -> viewLoans(member); // Manage active loans
                case 4 -> {
                    // Clear session and return to main loop
                    memberAuth.logout(member);
                    return;
                }
            }
        }
    }

    /**
     * Handles searching for items and borrowing them if a member is present.
     *
     * @param member the currently logged-in member (or null if guest)
     */
    private void searchCatalog(Member member) {
        // Launch search UI and if an item is selected, proceed to details view
        catalog.searchAndSelect(view).ifPresent(item -> showItemDetails(item, member));
    }

    /**
     * Shows item details and offers borrowing options.
     * Prevents guests from borrowing by checking if member is null.
     *
     * @param item   the media item to display
     * @param member the current user context
     */
    private void showItemDetails(MediaItem item, Member member) {
        while (true) {
            view.showMessage("\n===== ITEM DETAILS =====");
            view.showMessage(item.details());

            view.showMessage("\n1. Borrow this item");
            view.showMessage("2. Cancel");

            int choice = view.promptInt("Select an option", 1, 2);
            if (choice == 2)
                return;

            if (choice == 1) {
                // Check if user is authenticated as a Member
                if (member == null) {
                    view.showError("You must be a registered member to borrow items. Please register or login.");
                    view.pause();
                } else {
                    // Attempt the loan process
                    try {
                        loans.loanFirstAvailableCopy(item.getId(), member);
                        view.showMessage("Successfully borrowed: " + item.getTitle());
                        view.pause();
                        return;
                    } catch (Exception e) {
                        // Handle loan failures (e.g., limit reached, fines, no stock)
                        view.showError("Could not borrow: " + e.getMessage());
                        view.pause();
                    }
                }
            }
        }
    }

    /**
     * Displays active loans and handles returns.
     * Allows members to select a loan to return and displays any fines incurred.
     *
     * @param member the member whose loans to view
     */
    private void viewLoans(Member member) {
        while (true) {
            // Fetch up-to-date active loans
            List<Loan> active = loans.activeLoans(member.getId());
            if (active.isEmpty()) {
                view.showMessage("No active loans.");
                view.pause();
                return;
            }

            view.showMessage("\nActive Loans:");
            // List loans with index for selection
            for (int i = 0; i < active.size(); i++) {
                Loan loan = active.get(i);
                view.showMessage(
                        (i + 1) + ". " + loan.getHolding().getItem().getTitle() + " | Due: " + loan.getDueOn());
            }

            view.showMessage("0. Back to Dashboard");
            int choice = view.promptInt("Select a loan to return (or 0 to cancel)", 0, active.size());

            if (choice == 0) {
                return;
            }

            // Map selection back to Loan object (0-based index)
            Loan selected = active.get(choice - 1);

            // Attempt to return the selected loan and calculate fines
            try {
                var fine = loans.returnCopy(selected.getId());
                view.showMessage("Returned successfully: " + selected.getHolding().getItem().getTitle());
                // If fine > 0, notify the user
                if (fine.signum() > 0) {
                    view.showMessage("You were charged a fine of £" + fine);
                }
                view.pause();
            } catch (Exception e) {
                view.showError("Error returning item: " + e.getMessage());
                view.pause();
            }
        }
    }
}
