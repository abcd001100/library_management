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

    // ADDED: displayOverdueWarning() — prints a warning banner when a book is
    // nearing its due date (3 days or fewer remaining) but NOT yet overdue.
    // Called from New_Library.checkDueDateWarnings() on login and when
    // the user selects "Check Due Date".
    // Example output:
    //   ⚠️  DUE DATE REMINDER
    //   "Clean Code" is due in 2 day(s) on 2026-06-16. Please return it soon!
    public static void displayOverdueWarning(String bookTitle, LocalDate dueDate, long daysRemaining) {
        System.out.println("\n⚠️  DUE DATE REMINDER");
        System.out.println("------------------------------------------------------------------");
        System.out.println("\"" + bookTitle + "\" is due in " + daysRemaining
                + " day(s) on " + dueDate + ".");
        System.out.println("Please return it soon to avoid a fine of RM 0.50 per overdue day.");
        System.out.println("------------------------------------------------------------------\n");
    }

    // ADDED: displayAlreadyOverdueWarning() — prints a warning banner when a
    // book is already past its due date while still borrowed.
    // Called from New_Library.checkDueDateWarnings() on login and when
    // the user selects "Check Due Date".
    // Example output:
    //   🚨  OVERDUE ALERT
    //   "Clean Code" was due on 2026-06-10. You are 4 day(s) overdue.
    //   Current fine: RM 2.00. Return immediately to stop the fine growing.
    public static void displayAlreadyOverdueWarning(String bookTitle, LocalDate dueDate,
                                                     long overdueDays, double currentFine) {
        System.out.println("\n🚨  OVERDUE ALERT");
        System.out.println("------------------------------------------------------------------");
        System.out.println("\"" + bookTitle + "\" was due on " + dueDate + ".");
        System.out.println("You are " + overdueDays + " day(s) overdue.");
        System.out.println("Current fine accumulating: RM " + String.format("%.2f", currentFine));
        System.out.println("Please return immediately to stop the fine from growing.");
        System.out.println("------------------------------------------------------------------\n");
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
