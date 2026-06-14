import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class NotificationManager {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // EXISTING — Stage 2 notification: fires when a book is returned and the
    // next reserved user needs to be told it is now available. Unchanged.
    public static void checkAndNotifyNextUser(Book returnedBook, ReservationQueue<User> reservationQueue) {
        if (returnedBook == null || reservationQueue == null) {
            System.out.println("[SYSTEM ERROR] Notification failed: Invalid book or queue reference.");
            return;
        }
        if (!reservationQueue.isEmpty()) {
            User nextUserInLine = reservationQueue.peek();
            if (nextUserInLine != null) {
                String currentTimestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
                System.out.println("\n==================================================================");
                System.out.println("🔔 SYSTEM NOTIFICATION ALERT | " + currentTimestamp);
                System.out.println("==================================================================");
                System.out.println("To Student   : " + nextUserInLine.getName() + " (" + nextUserInLine.getUsername() + ")");
                System.out.println("Email Sent To: " + nextUserInLine.getEmail());
                System.out.println("Message      : Good news! The book you reserved is now available.");
                System.out.println("Book Details : \"" + returnedBook.getTitle() + "\" by " + returnedBook.getAuthor());
                System.out.println("------------------------------------------------------------------");
                System.out.println("⏳ NOTICE: This item will be placed on a 3-Day Reservation Hold.");
                System.out.println("   Please collect it from the counter before the holding window expires.");
                System.out.println("==================================================================\n");
            }
        }
    }

    // ADDED — Stage 1 notification: fires on login and on "Check Due Date"
    // for every open borrow record belonging to the logged-in user.
    // Three scenarios are handled:
    //
    //   A) Due date is MORE than 3 days away  → no message (not urgent yet)
    //   B) Due date is within 3 days          → ⚠️  DUE DATE REMINDER printed
    //   C) Due date has already passed        → 🚨  OVERDUE ALERT printed
    //
    // This method is called from New_Library.checkDueDateWarnings() which
    // loops over the borrow history and passes each open record in turn.
    public static void checkAndNotifyBorrower(User user, String bookTitle,
                                               LocalDate dueDate) {
        if (user == null || dueDate == null) return;

        LocalDate today = LocalDate.now();
        long daysUntilDue = ChronoUnit.DAYS.between(today, dueDate);

        if (daysUntilDue < 0) {
            // Scenario C — already overdue
            long overdueDays = Math.abs(daysUntilDue);
            double currentFine = overdueDays * 0.50;
            FineManager.displayAlreadyOverdueWarning(bookTitle, dueDate, overdueDays, currentFine);

        } else if (daysUntilDue <= 3) {
            // Scenario B — due within 3 days
            FineManager.displayOverdueWarning(bookTitle, dueDate, daysUntilDue);
        }
        // Scenario A — more than 3 days remaining, stay silent
    }
}
