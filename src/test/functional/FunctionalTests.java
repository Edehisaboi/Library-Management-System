package test.functional;

import domain.Query;
import domain.loan.Loan;
import domain.media.Book;
import domain.media.MediaItem;
import domain.user.Member;
import policies.FinePolicy;
import policies.fines.FlatFinePolicy;
import policies.rules.StandardLoanRule;
import repo.InventoryRepository;
import repo.LoanRepository;
import repo.MediaRepository;
import repo.inmem.InMemoryInventoryRepository;
import repo.inmem.InMemoryLoanRepository;
import repo.inmem.InMemoryMediaRepository;
import services.CatalogService;
import services.LoanService;
import util.ClockProvider;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Functional tests focused on verifying end-to-end workflows and interactions
 * between
 * multiple services (Catalog, Loan) and repositories to ensure system features
 * work as expected.
 */
public class FunctionalTests {

    /**
     * Executes all functional test cases.
     */
    public static void runAll() {
        System.out.println("=== Running Functional Tests ===");
        testCatalogSearch();
        testLoanWorkflow();
        testLoanLimitEnforcement();
        testReturnWorkflowAndFines();
        testInventoryAvailability();
        System.out.println("Functional Tests Completed.\n");
    }

    /**
     * Test Case 1: Catalog Add & Search Integration
     * <p>
     * Aim: Verify that a book added via the CatalogService can be successfully
     * retrieved
     * using the search functionality with matching criteria.
     * </p>
     */
    private static void testCatalogSearch() {
        System.out.print("1. Test Catalog Add & Search: ");
        try {
            // Setup fresh services
            CatalogService catalog = setupCatalog();
            Book b = new Book("Functional Book", List.of("Func Author"), 2023, Collections.emptySet(), "111", "Pub");

            // Action: Add title to catalog
            catalog.addTitle(b, 1);

            // Action: Search for the title
            List<MediaItem> results = catalog.search(new Query("Functional", null, null));

            // Assertions
            if (results.size() != 1)
                throw new AssertionError("Expected 1 result");
            if (!results.get(0).getTitle().equals("Functional Book"))
                throw new AssertionError("Title mismatch");

            System.out.println("PASS");
        } catch (Exception e) {
            System.out.println("FAIL - " + e.getMessage());
        }
    }

    /**
     * Test Case 2: Standard Loan Workflow
     * <p>
     * Aim: Simulate a standard user journey: finding an item and borrowing it.
     * Verify that the loan is created correctly and inventory availability
     * decreases.
     * </p>
     */
    private static void testLoanWorkflow() {
        System.out.print("2. Test Loan Item Workflow: ");
        try {
            var ctx = setupContext();
            Book b = new Book("Loan Book", List.of("Auth"), 2023, Collections.emptySet(), "222", "Pub");
            // Add 1 copy to inventory
            ctx.catalog.addTitle(b, 1);

            Member member = new Member("Loan", "User", "loan@test.com", "pass");

            // Action: Perform the loan
            Loan loan = ctx.loanService.loanFirstAvailableCopy(b.getId(), member);

            // Assertions
            if (loan == null)
                throw new AssertionError("Loan should not be null");
            if (!loan.getBorrower().equals(member))
                throw new AssertionError("Borrower mismatch");
            // Availability should drop to 0
            if (ctx.catalog.availableCount(b.getId()) != 0)
                throw new AssertionError("Available count should be 0");

            System.out.println("PASS");
        } catch (Exception e) {
            System.out.println("FAIL - " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test Case 3: Loan Limit Enforcement
     * <p>
     * Aim: Verify that the system correctly enforces the maximum concurrent loan
     * limit
     * per member (e.g., stopping a member with limit 1 from borrowing a 2nd item).
     * </p>
     */
    private static void testLoanLimitEnforcement() {
        System.out.print("3. Test Loan Limit Enforcement: ");
        try {
            var ctx = setupContext();
            // Create member with max 1 loan
            Member member = new Member("Limit", "User", "limit@test.com", "pass", 1, LocalDate.now().plusYears(1));

            Book b1 = new Book("Book 1", List.of("A"), 2023, Collections.emptySet(), "1", "P");
            Book b2 = new Book("Book 2", List.of("A"), 2023, Collections.emptySet(), "2", "P");

            ctx.catalog.addTitle(b1, 1);
            ctx.catalog.addTitle(b2, 1);

            // First loan should succeed (0 -> 1 active loans)
            ctx.loanService.loanFirstAvailableCopy(b1.getId(), member);

            // Second loan should fail (1 -> 2 would exceed limit of 1)
            try {
                ctx.loanService.loanFirstAvailableCopy(b2.getId(), member);
                System.out.println("FAIL - Should have thrown exception for limit reached");
            } catch (IllegalArgumentException e) {
                // Expected failure
                System.out.println("PASS");
            }
        } catch (Exception e) {
            System.out.println("FAIL - " + e.getMessage());
        }
    }

    /**
     * Test Case 4: Return Workflow and Fine Calculation
     * <p>
     * Aim: Simulate a late return scenario. Using a mock ClockProvider,
     * fast-forward time
     * to ensure the system correctly identifies the loan as overdue and calculates
     * fines.
     * </p>
     */
    private static void testReturnWorkflowAndFines() {
        System.out.print("4. Test Return and Fines: ");
        try {
            // Setup with a fixed clock to control "Today"
            ClockProvider fixedClock = new ClockProvider() {
                @Override
                public LocalDate today() {
                    return LocalDate.of(2023, 1, 1); // Start date
                }
            };

            InventoryRepository inv = new InMemoryInventoryRepository();
            LoanRepository loanRepo = new InMemoryLoanRepository();
            // Rule: 7 days loan. Due date will be Jan 8th.
            StandardLoanRule rule = new StandardLoanRule(loanRepo, 7);
            // Policy: $1.00 per day fine
            FinePolicy finePolicy = new FlatFinePolicy(new BigDecimal("1.00"), 0);

            LoanService service = new LoanService(inv, loanRepo, rule, finePolicy, fixedClock);
            CatalogService catalog = new CatalogService(new InMemoryMediaRepository(), inv);

            Book b = new Book("Fine Book", List.of("A"), 2023, Collections.emptySet(), "F", "P");
            catalog.addTitle(b, 1);
            Member m = new Member("Fine", "User", "fine@test.com", "pass");

            // Action: Borrow item on Jan 1st
            Loan loan = service.loanFirstAvailableCopy(b.getId(), m);

            // Simulate time passing: Return on Jan 10th (2 days after Jan 8th due date)
            ClockProvider lateClock = new ClockProvider() {
                @Override
                public LocalDate today() {
                    return LocalDate.of(2023, 1, 10);
                }
            };

            // Re-instantiate service with late clock to simulate return environment
            LoanService lateService = new LoanService(inv, loanRepo, rule, finePolicy, lateClock);

            // Action: Return item
            BigDecimal fine = lateService.returnCopy(loan.getId());

            // Assertions: 2 days late * $1.00 = $2.00
            if (fine.compareTo(new BigDecimal("2.00")) != 0) {
                throw new AssertionError("Expected fine 2.00, got " + fine);
            }
            if (!loan.isReturned())
                throw new AssertionError("Loan should be marked returned");

            System.out.println("PASS");
        } catch (Exception e) {
            System.out.println("FAIL - " + e.getMessage());
        }
    }

    /**
     * Test Case 5: Inventory Availability Consistency
     * <p>
     * Aim: Verify that the available copy count of a title is correctly maintained
     * throughout a cycle of adding stock, borrowing, and returning items.
     * </p>
     */
    private static void testInventoryAvailability() {
        System.out.print("5. Test Inventory Availability: ");
        try {
            var ctx = setupContext();
            Book b = new Book("Inv Book", List.of("A"), 2023, Collections.emptySet(), "I", "P");
            // Add 2 copies initially
            ctx.catalog.addTitle(b, 2);

            // Expect 2 available
            if (ctx.catalog.availableCount(b.getId()) != 2)
                throw new AssertionError("Should start with 2");

            Member m = new Member("M", "M", "m@m.com", "p");
            // Borrow 1 copy
            Loan l1 = ctx.loanService.loanFirstAvailableCopy(b.getId(), m);

            // Expect 1 available
            if (ctx.catalog.availableCount(b.getId()) != 1)
                throw new AssertionError("Should be 1 after loan");

            // Return copy
            ctx.loanService.returnCopy(l1.getId());

            // Expect 2 available again
            if (ctx.catalog.availableCount(b.getId()) != 2)
                throw new AssertionError("Should be 2 after return");

            System.out.println("PASS");
        } catch (Exception e) {
            System.out.println("FAIL - " + e.getMessage());
        }
    }

    // --- Helper Methods ---

    private static CatalogService setupCatalog() {
        return new CatalogService(new InMemoryMediaRepository(), new InMemoryInventoryRepository());
    }

    private static TestContext setupContext() {
        InventoryRepository inv = new InMemoryInventoryRepository();
        LoanRepository loan = new InMemoryLoanRepository();
        MediaRepository media = new InMemoryMediaRepository();
        StandardLoanRule rule = new StandardLoanRule(loan, 7);
        FinePolicy fines = new FlatFinePolicy(new BigDecimal("0.50"), 0);
        ClockProvider clock = ClockProvider.system();

        CatalogService cat = new CatalogService(media, inv);
        LoanService ls = new LoanService(inv, loan, rule, fines, clock);
        return new TestContext(cat, ls);
    }

    record TestContext(CatalogService catalog, LoanService loanService) {
    }
}
