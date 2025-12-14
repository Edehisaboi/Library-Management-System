package test.unit;

import domain.inventory.Holding;
import domain.inventory.HoldingStatus;
import domain.media.Book;
import domain.user.Member;
import policies.rules.StandardLoanRule;
import repo.LoanRepository;
import repo.inmem.InMemoryLoanRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Unit tests focused on verifying the logic of individual methods and classes
 * in isolation, without involving complex service interactions or dependencies.
 */
public class UnitTests {

    /**
     * Executes all unit test cases.
     */
    public static void runAll() {
        System.out.println("=== Running Unit Tests ===");
        testBookCreationAndGetters();
        testMemberValidationBoundaries();
        testLoanRuleDueDateReturnRange();
        testLoanRuleBlockedMember();
        testHoldingStatusTransitionsAndTypes();
        System.out.println("Unit Tests Completed.\n");
    }

    /**
     * Test Case 1: MediaItem Creation (Type and Range)
     * <p>
     * Aim: Verify that a Book object is correctly constructed with the provided
     * attributes.
     * Checks:
     * - Parameter types are handled correctly (implicit in Java, but validated via
     * logic).
     * - Parameter ranges: Year must be > 0.
     * - Return values: Getters return expected types and values.
     * </p>
     */
    private static void testBookCreationAndGetters() {
        System.out.print("1. Test Book Creation & Getters: ");
        try {
            // Test valid range
            Book book = new Book("Test Title", List.of("Author"), 2023, Collections.emptySet(), "123-456", "Publisher");

            // Verify return value types and content
            if (!"Test Title".equals(book.getTitle()))
                throw new AssertionError("Title mismatch");
            if (book.getYear() != 2023)
                throw new AssertionError("Year mismatch");
            if (!(book.getTitle() instanceof String))
                throw new AssertionError("Title return type mismatch");

            // Test invalid range (Year <= 0)
            try {
                new Book("Invalid Year", List.of("A"), -5, Collections.emptySet(), "ISBN", "Pub");
                System.out.println("FAIL - Expected exception for negative year");
                return;
            } catch (IllegalArgumentException e) {
                // Expected
            }

            System.out.println("PASS");
        } catch (Exception e) {
            System.out.println("FAIL - " + e.getMessage());
        }
    }

    /**
     * Test Case 2: Member Validation (Parameter Range)
     * <p>
     * Aim: Ensure that the Member constructor enforces business rules.
     * Checks:
     * - Parameter range: maxConcurrentLoans must be >= 1.
     * - Return value: Constructor should throw exception for out-of-range input.
     * </p>
     */
    private static void testMemberValidationBoundaries() {
        System.out.print("2. Test Member Validation (Range Checks): ");
        try {
            // Test boundary: 0 loans (invalid, must be >= 1)
            try {
                new Member("John", "Doe", "john@example.com", "pass", 0, LocalDate.now());
                System.out.println("FAIL - Expected Exception for 0 loans");
                return;
            } catch (IllegalArgumentException e) {
                // Expected
            }

            // Test boundary: -1 loans (invalid)
            try {
                new Member("John", "Doe", "john@example.com", "pass", -1, LocalDate.now());
                System.out.println("FAIL - Expected Exception for negative loans");
                return;
            } catch (IllegalArgumentException e) {
                // Expected
            }

            // Test valid range
            Member valid = new Member("Jane", "Doe", "jane@example.com", "pass", 1, LocalDate.now());
            if (valid.getMaxConcurrentLoans() != 1)
                throw new AssertionError("Max loans should be 1");

            System.out.println("PASS");
        } catch (Exception e) {
            System.out.println("FAIL - " + e.getMessage());
        }
    }

    /**
     * Test Case 3: Loan Rule Due Date (Return Value Range/Type)
     * <p>
     * Aim: Verify that StandardLoanRule calculates the due date correctly.
     * Checks:
     * - Return type: Must be LocalDate.
     * - Return range: Must be strictly after the start date (based on loan days >
     * 0).
     * </p>
     */
    private static void testLoanRuleDueDateReturnRange() {
        System.out.print("3. Test Loan Rule Due Date (Return Range): ");
        try {
            LoanRepository loanRepo = new InMemoryLoanRepository();
            StandardLoanRule rule = new StandardLoanRule(loanRepo, 7);

            Member m = new Member("Jane", "Doe", "jane@example.com", "pass");
            Book b = new Book("Title", List.of("A"), 2022, Collections.emptySet(), "ISBN", "Pub");
            Holding h = new Holding(b);

            LocalDate now = LocalDate.of(2023, 1, 1);

            // Invoke method
            Object result = rule.dueDate(m, h, now);

            // Check Return Type
            if (!(result instanceof LocalDate)) {
                throw new AssertionError("Return type must be LocalDate");
            }

            LocalDate due = (LocalDate) result;

            // Check Return Range (Must be after 'now')
            if (!due.isAfter(now)) {
                throw new AssertionError("Due date must be in the future");
            }

            // Verify specific calculation logic
            if (!due.equals(LocalDate.of(2023, 1, 8))) {
                throw new AssertionError("Expected 2023-01-08, got " + due);
            }
            System.out.println("PASS");
        } catch (Exception e) {
            System.out.println("FAIL - " + e.getMessage());
        }
    }

    /**
     * Test Case 4: Loan Rule Blocked Member (Return Value)
     * <p>
     * Aim: Ensure that StandardLoanRule prevents a loan if the member is explicitly
     * blocked.
     * Checks:
     * - Return value: boolean false when member is blocked.
     * </p>
     */
    private static void testLoanRuleBlockedMember() {
        System.out.print("4. Test Loan Rule Blocked Member: ");
        try {
            LoanRepository loanRepo = new InMemoryLoanRepository();
            StandardLoanRule rule = new StandardLoanRule(loanRepo, 7);

            Member m = new Member("Blocked", "User", "blocked@example.com", "pass");
            m.block();

            Book b = new Book("Title", List.of("A"), 2022, Collections.emptySet(), "ISBN", "Pub");
            Holding h = new Holding(b);

            // Check eligibility
            boolean canLoan = rule.canLoan(m, h);

            // Assert return value
            if (canLoan) {
                throw new AssertionError("Blocked member should not be able to loan");
            }
            System.out.println("PASS");
        } catch (Exception e) {
            System.out.println("FAIL - " + e.getMessage());
        }
    }

    /**
     * Test Case 5: Holding Status Transitions (Type and State)
     * <p>
     * Aim: Validate the state machine of a Holding object.
     * Checks:
     * - Return type: getStatus() returns HoldingStatus enum.
     * - State transitions: AVAILABLE -> ON_LOAN -> AVAILABLE.
     * </p>
     */
    private static void testHoldingStatusTransitionsAndTypes() {
        System.out.print("5. Test Holding Status Transitions: ");
        try {
            Book b = new Book("Title", List.of("A"), 2022, Collections.emptySet(), "ISBN", "Pub");
            Holding h = new Holding(b);

            // Check return type
            if (!(h.getStatus() instanceof HoldingStatus)) {
                throw new AssertionError("getStatus should return HoldingStatus enum");
            }

            // Initial state
            if (h.getStatus() != HoldingStatus.AVAILABLE)
                throw new AssertionError("Initial status should be AVAILABLE");

            // Transition to ON_LOAN
            h.markOnLoan();
            if (h.getStatus() != HoldingStatus.ON_LOAN)
                throw new AssertionError("Status should be ON_LOAN");

            // Transition back to AVAILABLE
            h.markReturned();
            if (h.getStatus() != HoldingStatus.AVAILABLE)
                throw new AssertionError("Status should be AVAILABLE after return");

            System.out.println("PASS");
        } catch (Exception e) {
            System.out.println("FAIL - " + e.getMessage());
        }
    }
}
