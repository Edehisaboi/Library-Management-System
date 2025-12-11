package controllers;

import authentication.Authenticator;
import domain.Category;
import domain.media.Book;
import domain.media.MediaItem;
import domain.user.Librarian;
import domain.user.Member;
import domain.user.User;
import infra.ConsoleView;
import repo.UserRepository;
import services.CatalogService;
import services.LoanService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AdminController {
    private final CatalogService catalog;
    private final LoanService loans;
    private final Authenticator librarianAuth;
    private final UserRepository userRepo;
    private final ConsoleView view;

    public AdminController(CatalogService catalog, LoanService loans, Authenticator librarianAuth,
            UserRepository userRepo) {
        this.catalog = catalog;
        this.loans = loans;
        this.librarianAuth = librarianAuth;
        this.userRepo = userRepo;
        this.view = ConsoleView.getInstance();
    }

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
                case 2 -> searchCatalogLogic();
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

    private void searchCatalogLogic() {
        String query = view.promptString("Enter search query (title/creator)");
        var results = catalog.search(new domain.Query(query, query, null));
        if (results.isEmpty()) {
            view.showMessage("No items found.");
        } else {
            view.showMessage("Found " + results.size() + " items.");
            results.forEach(item -> view.showMessage(item.toString()));
        }
        view.pause();
    }

    private void manageInventory() {
        while (true) {
            view.showMessage("""

                    ===== INVENTORY MANAGEMENT =====
                    1. Add New Book
                    2. Add Copy to Existing Title
                    3. Back to Dashboard
                    """);

            int choice = view.promptInt("Select an option", 1, 3);
            if (choice == 3)
                return;

            if (choice == 1) {
                addNewBook();
            } else if (choice == 2) {
                addCopyToExisting();
            }
        }
    }

    private void addNewBook() {
        view.showMessage("\n--- Add New Book ---");
        String title = view.promptString("Title");
        String authorInput = view.promptString("Authors (comma separated)");
        String publisher = view.promptString("Publisher");
        String isbn = view.promptString("ISBN");
        int year = view.promptInt("Year", 1000, 3000);
        String catInput = view.promptString("Categories (comma separated, e.g. FICTION, HISTORY)");
        int copies = view.promptInt("Initial Copies", 0, 100);

        List<String> authors = Arrays.stream(authorInput.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        var bookBuilder = Book.builder()
                .title(title)
                .publisher(publisher)
                .year(year)
                .isbn(isbn);

        authors.forEach(bookBuilder::addAuthor);

        for (String c : catInput.split(",")) {
            try {
                bookBuilder.addCategory(Category.valueOf(c.trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                view.showMessage("Warning: Invalid category '" + c + "' ignored.");
            }
        }

        MediaItem newItem = bookBuilder.build();
        catalog.addTitle(newItem, copies);
        view.showMessage("Book added successfully!");
        view.pause();
    }

    private void addCopyToExisting() {
        // Simple search to find the book first
        String query = view.promptString("Enter title or ISBN to search");
        var results = catalog.search(new domain.Query(query, null, null));

        if (results.isEmpty()) {
            view.showMessage("No items found.");
            view.pause();
            return;
        }

        for (int i = 0; i < results.size(); i++) {
            view.showMessage((i + 1) + ". " + results.get(i).toString());
        }

        int idx = view.promptInt("Select item to add copy to (0 to cancel)", 0, results.size());
        if (idx == 0)
            return;

        MediaItem selected = results.get(idx - 1);
        try {
            catalog.addCopy(selected.getId());
            view.showMessage("Copy added to: " + selected.getTitle());
        } catch (Exception e) {
            view.showError("Failed to add copy: " + e.getMessage());
        }
        view.pause();
    }

    private void manageUsers() {
        List<User> users = userRepo.findAll();
        List<Member> members = users.stream()
                .filter(u -> u instanceof Member)
                .map(u -> (Member) u)
                .collect(Collectors.toList());

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
