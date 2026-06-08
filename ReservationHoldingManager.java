import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReservationHoldingManager {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int HOLDING_PERIOD_DAYS = 3;

    // Calculates expiration timestamp (Current time + 3 Days)
    public static LocalDateTime calculateHoldExpiry() {
        return LocalDateTime.now().plusDays(HOLDING_PERIOD_DAYS);
    }

    // Displays the confirmation receipt window for the student
    public static void displayHoldStatus(String bookTitle, String studentName, LocalDateTime expiryDate) {
        if (expiryDate == null) return;

        System.out.println("\n------------------------------------------------------------------");
        System.out.println("⏳ RESERVATION HOLD ACTIVATED");
        System.out.println("------------------------------------------------------------------");
        System.out.println("Book Item   : " + bookTitle);
        System.out.println("Held For    : " + studentName);
        System.out.println("Activated On: " + LocalDateTime.now().format(FORMATTER));
        System.out.println("Hold Expires: " + expiryDate.format(FORMATTER));
        System.out.println("Status      : SECURED (This book cannot be borrowed by other users)");
        System.out.println("------------------------------------------------------------------\n");
    }

    // Checks if the 3-day window has expired
    public static boolean isHoldExpired(LocalDateTime expiryDate) {
        if (expiryDate == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(expiryDate);
    }

    // Releases the book back to the general collection if the hold expires
    public static void processExpiredHold(Book book) {
        if (book == null) return;
        
        System.out.println("\n🛑 [SYSTEM NOTICE] 3-Day Reservation Hold Window has EXPIRED.");
        System.out.println("Book Title: \"" + book.getTitle() + "\" is now released back to the general collection.");
        System.out.println("Available inventory updated successfully.\n");
        
        book.setAvailableCopies(book.getAvailableCopies() + 1);
    }
}