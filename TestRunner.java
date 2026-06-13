import java.time.LocalDate;

// Simple self-contained test runner (no JUnit required).
// Run with: java TestRunner
public class TestRunner {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("===== RUNNING TEST CASES =====\n");

        testFineCalculationOverdue();
        testFineCalculationNotOverdue();
        testBorrowStackOrder();
        testReservationQueueOrder();
        testBookBorrowAndReturn();
        testBookRenewalWithAndWithoutReservations();
        testRegisterAndLogin();

        System.out.println("\n===== TEST SUMMARY =====");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
    }

    private static void check(String testName, boolean condition, String detail) {
        if (condition) {
            System.out.println("[PASS] " + testName);
            passed++;
        } else {
            System.out.println("[FAIL] " + testName + " -> " + detail);
            failed++;
        }
    }

    // Test 1: A book 5 days overdue should incur a fine of RM 2.50 (RM0.50/day)
    private static void testFineCalculationOverdue() {
        LocalDate dueDate = LocalDate.now().minusDays(5);
        LocalDate returnDate = LocalDate.now();
        double fine = FineManager.calculateFine(dueDate, returnDate);
        check("Fine calculation - 5 days overdue", fine == 2.50, "Expected 2.50, got " + fine);
    }

    // Test 2: A book returned before/on the due date should have no fine
    private static void testFineCalculationNotOverdue() {
        LocalDate dueDate = LocalDate.now().plusDays(2);
        LocalDate returnDate = LocalDate.now();
        double fine = FineManager.calculateFine(dueDate, returnDate);
        check("Fine calculation - returned early", fine == 0.0, "Expected 0.00, got " + fine);
    }

    // Test 3: BorrowStack should be LIFO (last in, first out) - O(1) push/pop
    private static void testBorrowStackOrder() {
        BorrowStack<String> stack = new BorrowStack<>();
        stack.push("Book A");
        stack.push("Book B");
        String popped = stack.pop();
        check("BorrowStack LIFO order", "Book B".equals(popped), "Expected 'Book B', got " + popped);
        check("BorrowStack size after pop", stack.getSize() == 1, "Expected size 1, got " + stack.getSize());
    }

    // Test 4: ReservationQueue should be FIFO (first in, first out) - O(1) enqueue/dequeue
    private static void testReservationQueueOrder() {
        ReservationQueue<String> queue = new ReservationQueue<>();
        queue.enqueue("Ali");
        queue.enqueue("Mona");
        String first = queue.dequeue();
        check("ReservationQueue FIFO order", "Ali".equals(first), "Expected 'Ali', got " + first);
        check("ReservationQueue size after dequeue", queue.getSize() == 1, "Expected size 1, got " + queue.getSize());
    }

    // Test 5: Borrowing reduces available copies; returning restores them
    private static void testBookBorrowAndReturn() {
        Book book = new Book("Test Book", "Test Author", 2020, "Test Genre", 2);
        boolean borrowed = book.borrow();
        check("Borrow reduces available copies", borrowed && book.getAvailableCopies() == 1,
                "Expected availableCopies=1, got " + book.getAvailableCopies());

        book.returnBook();
        check("Return restores available copies", book.getAvailableCopies() == 2,
                "Expected availableCopies=2, got " + book.getAvailableCopies());
    }

    // Test 6: Renewal is blocked if there are pending reservations, allowed otherwise
    private static void testBookRenewalWithAndWithoutReservations() {
        Book book = new Book("Renew Book", "Author X", 2021, "Genre X", 1);
        book.borrow();

        boolean renewedWithPending = book.renew(true);
        check("Renewal blocked with pending reservations", !renewedWithPending,
                "Expected renewal to fail when reservations are pending");

        boolean renewedWithoutPending = book.renew(false);
        check("Renewal allowed without pending reservations", renewedWithoutPending,
                "Expected renewal to succeed when no reservations are pending");
    }

    // Test 7: Registering and logging in with a new user works end-to-end
    private static void testRegisterAndLogin() {
        New_Library library = new New_Library();
        library.registerUser("Test User", "Test Address", "0100000000",
                "test@email.com", "testuser", "testpass");

        boolean loginOk = library.login("testuser", "testpass");
        check("Login succeeds with correct credentials", loginOk, "Expected login to succeed");

        boolean loginFail = library.login("testuser", "wrongpass");
        check("Login fails with wrong password", !loginFail, "Expected login to fail");
    }
}
