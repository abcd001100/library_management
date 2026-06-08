import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotificationManager {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void checkAndNotifyNextUser(Book returnedBook, ReservationQueue<User> reservationQueue) {
        // Validation check
        if (returnedBook == null || reservationQueue == null) {
            System.out.println("[SYSTEM ERROR] Notification failed: Invalid book or queue reference.");
            return;
        }

        // Process notification if students are waiting
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
}