import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class FineManager {

    // RM 0.50 per day overdue
    private static final double FINE_RATE_PER_DAY = 0.50;

    // Number of days a book is late, based on due date and the date it was/would be returned
    public static long calculateOverdueDays(LocalDate dueDate, LocalDate compareDate) {
        if (dueDate == null || compareDate == null) return 0;
        long days = ChronoUnit.DAYS.between(dueDate, compareDate);
        return days > 0 ? days : 0;
    }

    // Fine if the book were returned TODAY (used for "check my fines" while still borrowed)
    public static double calculateFine(LocalDate dueDate) {
        return calculateFine(dueDate, LocalDate.now());
    }

    // Fine based on a specific return date (used when a book is actually returned)
    public static double calculateFine(LocalDate dueDate, LocalDate returnDate) {
        long overdueDays = calculateOverdueDays(dueDate, returnDate);
        return overdueDays * FINE_RATE_PER_DAY;
    }

    // Prints a fine notice when a book is returned late
    public static void displayFineNotice(String bookTitle, double fine) {
        if (fine > 0) {
            System.out.println("\n⚠️  FINE NOTICE");
            System.out.println("------------------------------------------------------------------");
            System.out.println("\"" + bookTitle + "\" was returned late.");
            System.out.println("Fine charged: RM " + String.format("%.2f", fine));
            System.out.println("------------------------------------------------------------------\n");
        }
    }

    // Simulates a payment transaction (no real money/database involved)
    public static void simulatePayment(User user, double amount) {
        System.out.println("\n--------------------------------------------------");
        System.out.println("PAYMENT SIMULATION");
        System.out.println("--------------------------------------------------");
        System.out.println("User       : " + user.getName() + " (" + user.getUsername() + ")");

        if (amount <= 0) {
            System.out.println("Amount Due : RM 0.00");
            System.out.println("Status     : No outstanding fines. Nothing to pay!");
        } else {
            System.out.println("Amount Due : RM " + String.format("%.2f", amount));
            System.out.println("Status     : PAYMENT SUCCESSFUL (simulated)");
            System.out.println("Receipt No : SIM-" + System.currentTimeMillis());
        }
        System.out.println("--------------------------------------------------\n");
    }
}
