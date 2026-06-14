import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDate;

public class New_Library {

    // ── Abdulaziz's data ──────────────────────────────────────
    private ArrayList<User> users;
    private ArrayList<Book> books;
    private User loggedInUser;

    // ── Shee's data structures ────────────────────────────────
    private BorrowStack<Book> borrowStack;
    private HashMap<String, ReservationQueue<User>> reservationQueues;

    // ── Abdulwahab / Amir — borrow history for fines & analytics ──
    private ArrayList<BorrowRecord> borrowHistory;

    public New_Library() {
        users = new ArrayList<>();
        books = new ArrayList<>();
        loggedInUser = null;
        borrowStack = new BorrowStack<>();
        reservationQueues = new HashMap<>();
        borrowHistory = new ArrayList<>();
    }

    // ==========================================
    //   ABDULAZIZ — USER MANAGEMENT
    // ==========================================

    public void registerUser(String name, String address, String contact,
                             String email, String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                System.out.println("Username already exists. Choose another.");
                return;
            }
        }
        users.add(new User(name, address, contact, email, username, password));
        System.out.println("User registered successfully! Welcome, " + name + ".");
    }

    // MODIFIED: login() now calls checkDueDateWarnings() right after a
    // successful login so the user immediately sees any overdue alerts or
    // upcoming deadline reminders without having to select a menu option.
    public boolean login(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                loggedInUser = u;
                System.out.println("Login successful! Welcome back, " + u.getName() + ".");
                // ADDED: check and show due date / overdue warnings on login
                checkDueDateWarnings();
                return true;
            }
        }
        System.out.println("Invalid username or password.");
        return false;
    }

    public void updateUserInfo(String newAddress, String newContact, String newEmail) {
        if (loggedInUser == null) { System.out.println("Please log in first."); return; }
        loggedInUser.setAddress(newAddress);
        loggedInUser.setContactNumber(newContact);
        loggedInUser.setEmail(newEmail);
        System.out.println("Personal information updated successfully.");
    }

    public void forgotPassword(String username, String newPassword) {
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                u.setPassword(newPassword);
                System.out.println("Password reset successful for: " + username);
                return;
            }
        }
        System.out.println("Username not found.");
    }

    // ==========================================
    //   ABDULAZIZ — BOOK MANAGEMENT
    // ==========================================

    public void addBook(String title, String author, int year, String genre, int copies) {
        books.add(new Book(title, author, year, genre, copies));
        System.out.println("Book added: " + title);
    }

    public void searchBooks(String keyword) {
        System.out.println("Search results for \"" + keyword + "\":");
        boolean found = false;
        for (Book b : books) {
            if (b.getTitle().toLowerCase().contains(keyword.toLowerCase())
             || b.getAuthor().toLowerCase().contains(keyword.toLowerCase())
             || b.getGenre().toLowerCase().contains(keyword.toLowerCase())) {
                System.out.println("  " + b);
                found = true;
            }
        }
        if (!found) System.out.println("  No books found.");
    }

    public void viewBookDetails(String title) {
        for (Book b : books) {
            if (b.getTitle().equalsIgnoreCase(title)) {
                System.out.println(b);
                return;
            }
        }
        System.out.println("Book not found.");
    }

    public void updateBookInventory(String title, int newCopies) {
        for (Book b : books) {
            if (b.getTitle().equalsIgnoreCase(title)) {
                b.setTotalCopies(newCopies);
                System.out.println("Inventory updated for: " + title);
                return;
            }
        }
        System.out.println("Book not found.");
    }

    // MODIFIED: showDueDate() now also calls checkDueDateWarnings() so the
    // user sees the warning banner alongside the raw due date text.
    public void showDueDate(String title) {
        for (Book b : books) {
            if (b.getTitle().equalsIgnoreCase(title)) {
                if (b.getDueDate() != null) {
                    System.out.println("Due date for \"" + title + "\": " + b.getDueDate());
                    // ADDED: show warning banner for this specific book
                    NotificationManager.checkAndNotifyBorrower(loggedInUser, b.getTitle(), b.getDueDate());
                } else {
                    System.out.println("This book is not currently borrowed.");
                }
                return;
            }
        }
        System.out.println("Book not found.");
    }

    public void renewBook(String title) {
        for (Book b : books) {
            if (b.getTitle().equalsIgnoreCase(title)) {
                ReservationQueue<User> queue = reservationQueues.get(title.toLowerCase());
                boolean hasPending = (queue != null && queue.getSize() > 0);
                if (b.renew(hasPending)) {
                    for (int i = borrowHistory.size() - 1; i >= 0; i--) {
                        BorrowRecord r = borrowHistory.get(i);
                        if (r.getBookTitle().equalsIgnoreCase(title) && r.getReturnDate() == null) {
                            r.setDueDate(b.getDueDate());
                            break;
                        }
                    }
                }
                return;
            }
        }
        System.out.println("Book not found.");
    }

    // ==========================================
    //   SHEE — STACK & QUEUE METHODS
    // ==========================================

    public void borrowBook(Book book) {
        if (loggedInUser == null) {
            System.out.println("Please login first to borrow a book.");
            return;
        }
        if (book.borrow()) {
            borrowStack.push(book);
            BorrowRecord record = new BorrowRecord(
                    book.getTitle(), book.getGenre(),
                    loggedInUser.getUsername(), loggedInUser.getName(),
                    LocalDate.now(), book.getDueDate());
            borrowHistory.add(record);
            System.out.println("Book borrowed! Due date: " + book.getDueDate());
            System.out.println();
            System.out.println("// Internal Stack operation (BorrowStack.push)");
            System.out.println("// PUSH: [" + book.getTitle() + " - " + loggedInUser.getName() + "] -> pushed to top of borrowStack");
            System.out.println("// Stack (top -> bottom): " + borrowStack.toString());
            System.out.println("// Time Complexity: O(1)");
        } else {
            System.out.println("Sorry, no copies available. Consider reserving it.");
        }
    }

    // ADDED: borrowBookAsOverdue() — demo/testing method that simulates a
    // borrow from 'daysAgo' days in the past so the book is already overdue
    // the moment you return it. For example, passing daysAgo=20 means:
    //   simulated borrow date = today - 20 days
    //   due date              = today - 20 + 14 = today - 6 days  (already 6 days overdue)
    //   fine on return        = 6 × RM 0.50 = RM 3.00
    // This is wired to menu option 21 in Main.java.
    public void borrowBookAsOverdue(Book book, int daysAgo) {
        if (loggedInUser == null) {
            System.out.println("Please login first.");
            return;
        }
        if (book.borrowAsOverdue(daysAgo)) {
            borrowStack.push(book);
            LocalDate simulatedBorrowDate = LocalDate.now().minusDays(daysAgo);
            BorrowRecord record = new BorrowRecord(
                    book.getTitle(), book.getGenre(),
                    loggedInUser.getUsername(), loggedInUser.getName(),
                    simulatedBorrowDate, book.getDueDate());
            borrowHistory.add(record);
            System.out.println("[DEMO] Book borrowed with simulated date " + daysAgo + " days ago.");
            System.out.println("[DEMO] Simulated borrow date : " + simulatedBorrowDate);
            System.out.println("[DEMO] Due date              : " + book.getDueDate());
            if (LocalDate.now().isAfter(book.getDueDate())) {
                long overdue = java.time.temporal.ChronoUnit.DAYS.between(book.getDueDate(), LocalDate.now());
                System.out.println("[DEMO] Already overdue by " + overdue + " day(s) -> Fine if returned now: RM "
                        + String.format("%.2f", overdue * 0.50));
            } else {
                System.out.println("[DEMO] Not yet overdue. Return before " + book.getDueDate() + " to avoid fine.");
            }
        } else {
            System.out.println("Sorry, no copies available.");
        }
    }

    public void returnBook(String title) {
        if (loggedInUser == null) {
            System.out.println("Please login first.");
            return;
        }
        Book returnedBook = findBook(title);
        if (returnedBook == null) {
            System.out.println("Book not found.");
            return;
        }
        BorrowRecord record = null;
        for (int i = borrowHistory.size() - 1; i >= 0; i--) {
            BorrowRecord r = borrowHistory.get(i);
            if (r.getBookTitle().equalsIgnoreCase(title)
                    && r.getUsername().equals(loggedInUser.getUsername())
                    && r.getReturnDate() == null) {
                record = r;
                break;
            }
        }
        if (record == null) {
            System.out.println("No active borrow record found for: " + title);
            return;
        }
        borrowStack.pop();
        double fine = FineManager.calculateFine(record.getDueDate(), LocalDate.now());
        record.setReturnDate(LocalDate.now());
        record.setFineAmount(fine);
        returnedBook.returnBook();
        System.out.println("Book returned successfully: " + returnedBook.getTitle());
        System.out.println();
        System.out.println("// Internal Stack operation (BorrowStack.pop)");
        System.out.println("// POP: [" + title + " - " + loggedInUser.getName() + "] -> removed from top of borrowStack");
        System.out.println("// Stack (top -> bottom): " + (borrowStack.isEmpty() ? "empty" : borrowStack.toString()));
        System.out.println("// Time Complexity: O(1)");
        LocalDate dueDate = record.getDueDate();
        LocalDate today = LocalDate.now();
        if (dueDate != null && today.isAfter(dueDate)) {
            System.out.printf("// Overdue check: Due date %s has passed -> Fine = RM %.2f%n", dueDate, fine);
        } else {
            System.out.printf("// Overdue check: Due date %s has not passed -> Fine = RM 0.00%n", dueDate);
        }
        FineManager.displayFineNotice(returnedBook.getTitle(), fine);
        ReservationQueue<User> queue = reservationQueues.get(title.toLowerCase());
        if (queue != null && !queue.isEmpty()) {
            NotificationManager.checkAndNotifyNextUser(returnedBook, queue);
        }
    }

    public void reserveBook(User user, String bookTitle) {
        Book book = findBook(bookTitle);
        if (book == null) {
            System.out.println("Book not found: " + bookTitle);
            return;
        }
        String key = bookTitle.toLowerCase();
        reservationQueues.putIfAbsent(key, new ReservationQueue<>());
        ReservationQueue<User> queue = reservationQueues.get(key);
        queue.enqueue(user);
        System.out.println(user.getName() + " added to reservation waiting list.");
        System.out.println();
        System.out.println("// Internal Queue operation (ReservationQueue.enqueue)");
        System.out.println("// ENQUEUE: [" + user.getName() + "] -> placed at rear of reservationQueue");
        System.out.println("// Queue (front -> rear): [" + user.getName() + "]");
        System.out.println("// Time Complexity: O(1)");
    }

    public void issueReservedBook(String bookTitle) {
        String key = bookTitle.toLowerCase();
        ReservationQueue<User> queue = reservationQueues.get(key);
        if (queue != null && !queue.isEmpty()) {
            User nextUser = queue.dequeue();
            System.out.println("Book issued to: " + nextUser.getName());
        } else {
            System.out.println("No reservations pending for this book.");
        }
    }

    // ==========================================
    //   Amir — FINE CALCULATION
    // ==========================================

    public double calculateMyFines() {
        if (loggedInUser == null) return 0.0;
        double total = 0.0;
        for (BorrowRecord r : borrowHistory) {
            if (r.getUsername().equals(loggedInUser.getUsername()) && r.getReturnDate() == null) {
                total += FineManager.calculateFine(r.getDueDate());
            }
        }
        return total;
    }

    public void payMyFine() {
        if (loggedInUser == null) {
            System.out.println("Please login first.");
            return;
        }
        double fine = calculateMyFines();
        FineManager.simulatePayment(loggedInUser, fine);
    }

    // ADDED: checkDueDateWarnings() — loops over all open borrow records for
    // the currently logged-in user and calls NotificationManager to print
    // a warning if a book is due within 3 days or already overdue.
    // Called automatically on login and when the user selects "Check Due Date".
    public void checkDueDateWarnings() {
        if (loggedInUser == null) return;
        for (BorrowRecord r : borrowHistory) {
            if (r.getUsername().equals(loggedInUser.getUsername())
                    && r.getReturnDate() == null) {
                NotificationManager.checkAndNotifyBorrower(
                        loggedInUser, r.getBookTitle(), r.getDueDate());
            }
        }
    }

    public Book findBook(String title) {
        for (Book b : books) {
            if (b.getTitle().equalsIgnoreCase(title)) return b;
        }
        return null;
    }

    public User getLoggedInUser() { return loggedInUser; }

    public ReservationQueue<User> getReservationQueue(Book book) {
        return reservationQueues.getOrDefault(
            book.getTitle().toLowerCase(), new ReservationQueue<>());
    }

    public ArrayList<Book> getBooks() { return books; }
    public ArrayList<User> getUsers() { return users; }
    public ArrayList<BorrowRecord> getBorrowHistory() { return borrowHistory; }
}
