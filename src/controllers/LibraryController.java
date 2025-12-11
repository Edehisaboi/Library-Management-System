package controllers;

import authentication.Authenticator;
import domain.Query;
import domain.loan.Loan;
import domain.media.MediaItem;
import domain.user.Member;
import infra.ConsoleView;
import services.CatalogService;
import services.LoanService;

import java.util.List;

public class LibraryController {
    private final CatalogService catalog;
    private final LoanService loans;
    private final Authenticator memberAuth;
    private final ConsoleView view;

    public LibraryController(CatalogService catalog, LoanService loans,
            Authenticator memberAuth) {
        this.catalog = catalog;
        this.loans = loans;
        this.memberAuth = memberAuth;
        this.view = ConsoleView.getInstance();
    }

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
                return;
            }
        }
    }

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
                case 1 -> view.showMessage(member.toString());
                case 2 -> searchCatalog(member);
                case 3 -> viewLoans(member);
                case 4 -> {
                    memberAuth.logout(member);
                    return;
                }
            }
        }
    }

    private void searchCatalog(Member member) {
        String query = view.promptString("Enter search query (title/creator or blank for all)", true);
        Query q = new Query(query, query, null);
        List<MediaItem> results = catalog.search(q);

        if (results.isEmpty()) {
            view.showMessage("No items found.");
            view.pause();
            return;
        }

        while (true) {
            view.showMessage("\nFound " + results.size() + " items:");
            for (int i = 0; i < results.size(); i++) {
                MediaItem item = results.get(i);
                int available = catalog.availableCount(item.getId());
                view.showMessage((i + 1) + ". " + item.toString() + " | Available: " + available);
            }

            view.showMessage("0. Back to Dashboard");
            int choice = view.promptInt("Select an item to borrow (or 0 to cancel)", 0, results.size());

            if (choice == 0) {
                return;
            }

            MediaItem selected = results.get(choice - 1);
            if (member == null) {
                view.showError("You must be a registered member to borrow items. Please register or login.");
                view.pause();
            } else {
                try {
                    loans.loanFirstAvailableCopy(selected.getId(), member);
                    view.showMessage("Successfully borrowed: " + selected.getTitle());
                    view.pause();
                    return;
                } catch (Exception e) {
                    view.showError("Could not borrow: " + e.getMessage());
                    view.pause();
                }
            }
        }
    }

    private void viewLoans(Member member) {
        while (true) {
            List<Loan> active = loans.activeLoans(member.getId());
            if (active.isEmpty()) {
                view.showMessage("No active loans.");
                view.pause();
                return;
            }

            view.showMessage("\nActive Loans:");
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

            Loan selected = active.get(choice - 1);
            try {
                var fine = loans.returnCopy(selected.getId());
                view.showMessage("Returned successfully: " + selected.getHolding().getItem().getTitle());
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
