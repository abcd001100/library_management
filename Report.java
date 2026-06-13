import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Report {

    // ==========================================
    //   BASIC REPORTS
    // ==========================================

    // Inventory report - lists every book and its availability
    public static void generateInventoryReport(ArrayList<Book> books) {
        System.out.println("\n================ LIBRARY INVENTORY REPORT ================");
        if (books.isEmpty()) {
            System.out.println("No books in the system.");
        } else {
            for (Book b : books) {
                System.out.println("  " + b);
            }
        }
        System.out.println("------------------------------------------------------------------");
        System.out.println("Total titles: " + books.size());
        System.out.println("===========================================================\n");
    }

    // Registered users report
    public static void generateUserReport(ArrayList<User> users) {
        System.out.println("\n================ REGISTERED USERS REPORT =================");
        if (users.isEmpty()) {
            System.out.println("No registered users.");
        } else {
            for (User u : users) {
                System.out.println("  " + u);
            }
        }
        System.out.println("------------------------------------------------------------------");
        System.out.println("Total users: " + users.size());
        System.out.println("===========================================================\n");
    }

    // Full borrow/return history log
    public static void generateBorrowHistoryReport(ArrayList<BorrowRecord> history) {
        System.out.println("\n================ BORROW HISTORY REPORT ===================");
        if (history.isEmpty()) {
            System.out.println("No borrowing activity recorded yet.");
        } else {
            for (BorrowRecord r : history) {
                System.out.println("  " + r);
            }
        }
        System.out.println("===========================================================\n");
    }

    // ==========================================
    //   AMIR — POPULAR BOOKS / GENRES ANALYTICS
    // ==========================================

    public static void generatePopularBooksReport(ArrayList<BorrowRecord> history) {
        System.out.println("\n============ POPULAR BOOKS & GENRES ANALYTICS ============");

        if (history.isEmpty()) {
            System.out.println("No borrowing activity recorded yet.");
            System.out.println("===========================================================\n");
            return;
        }

        Map<String, Integer> bookCounts = new HashMap<>();
        Map<String, Integer> genreCounts = new HashMap<>();

        for (BorrowRecord r : history) {
            bookCounts.merge(r.getBookTitle(), 1, Integer::sum);
            genreCounts.merge(r.getGenre(), 1, Integer::sum);
        }

        System.out.println("-- Most Borrowed Books --");
        bookCounts.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .forEach(e -> System.out.println("  " + e.getKey() + " : " + e.getValue() + " time(s)"));

        System.out.println("\n-- Most Borrowed Genres --");
        genreCounts.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .forEach(e -> System.out.println("  " + e.getKey() + " : " + e.getValue() + " time(s)"));

        System.out.println("===========================================================\n");
    }

    // ==========================================
    //   AMIR — USER BEHAVIOR ANALYTICS
    // ==========================================

    public static void generateUserActivityReport(ArrayList<BorrowRecord> history) {
        System.out.println("\n============== USER BEHAVIOR ANALYTICS ===================");

        if (history.isEmpty()) {
            System.out.println("No borrowing activity recorded yet.");
            System.out.println("===========================================================\n");
            return;
        }

        Map<String, Integer> userCounts = new HashMap<>();
        Map<String, Double> userFines = new HashMap<>();

        for (BorrowRecord r : history) {
            userCounts.merge(r.getUserFullName(), 1, Integer::sum);
            userFines.merge(r.getUserFullName(), r.getFineAmount(), Double::sum);
        }

        System.out.println("-- Books Borrowed per User --");
        userCounts.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .forEach(e -> System.out.println("  " + e.getKey() + " : " + e.getValue() + " book(s)"));

        System.out.println("\n-- Total Fines Incurred per User --");
        userFines.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .forEach(e -> System.out.println("  " + e.getKey() + " : RM " + String.format("%.2f", e.getValue())));

        System.out.println("===========================================================\n");
    }
}
