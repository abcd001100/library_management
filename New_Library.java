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

    // FIX 2: Changed from a single global reservationQueue to a per-book
    //         HashMap so each book has its own independent waiting list.
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

    public boolean login(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                loggedInUser = u;
                System.out.println("Login successful! Welcome back, " + u.getName() + ".");
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

    public void showDueDate(String title) {
        for (Book b : books) {
            if (b.getTitle().equalsIgnoreCase(title)) {
                if (b.getDueDate() != null)
                    System.out.println("Due date for \"" + title + "\": " + b.getDueDate());
                else
                    System.out.println("This book is not currently borrowed.");
                return;
            }
        }
        System.out.println("Book not found.");
    }

    public void renewBook(String title) {
        for (Book b : books) {
            if (b.getTitle().equalsIgnoreCase(title)) {
                // FIX 2 side-effect: check the correct per-book queue for pending reservations
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

            // Log this borrow for fine calculation & analytics
            BorrowRecord record = new BorrowRecord(
                    book.getTitle(), book.getGenre(),
                    loggedInUser.getUsername(), loggedInUser.getName(),
                    LocalDate.now(), book.getDueDate());
            borrowHistory.add(record);

            System.out.println("Book borrowed! Due date: " + book.getDueDate());

            // Internal stack debug output
            System.out.println();
            System.out.println("// Internal Stack operation (BorrowStack.push)");
            System.out.println("// PUSH: [" + book.getTitle() + " - " + loggedInUser.getName() + "] -> pushed to top of borrowStack");
            System.out.println("// Stack (top -> bottom): " + borrowStack.toString());
            System.out.println("// Time Complexity: O(1)");
        } else {
            System.out.println("Sorry, no copies available. Consider reserving it.");
        }
    }

    // FIX 1: returnBook() now accepts the book title so it can correctly
    //         identify which book is being returned, print the full internal
    //         stack debug log, show the overdue fine check, and trigger the
    //         per-book reservation notification — all in one place.
    //         The duplicate notification call in Main.java case 9 is removed.
    public void returnBook(String title) {
        if (loggedInUser == null) {
            System.out.println("Please login first.");
            return;
        }

        // Find the book in the catalogue
        Book returnedBook = findBook(title);
        if (returnedBook == null) {
            System.out.println("Book not found.");
            return;
        }

        // Find the most recent open borrow record for this user and book
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

        // Pop from borrow stack and update record
        borrowStack.pop();

        double fine = FineManager.calculateFine(record.getDueDate(), LocalDate.now());
        record.setReturnDate(LocalDate.now());
        record.setFineAmount(fine);

        returnedBook.returnBook();
        System.out.println("Book returned successfully: " + returnedBook.getTitle());

        // Internal stack debug output (matches Image 1)
        System.out.println();
        System.out.println("// Internal Stack operation (BorrowStack.pop)");
        System.out.println("// POP: [" + title + " - " + loggedInUser.getName() + "] -> removed from top of borrowStack");
        System.out.println("// Stack (top -> bottom): " + (borrowStack.isEmpty() ? "empty" : borrowStack.toString()));
        System.out.println("// Time Complexity: O(1)");

        // Overdue fine check output (matches Image 1)
        LocalDate dueDate = record.getDueDate();
        LocalDate today = LocalDate.now();
        if (dueDate != null && today.isAfter(dueDate)) {
            System.out.printf("// Overdue check: Due date %s has passed -> Fine = RM %.2f%n", dueDate, fine);
        } else {
            System.out.printf("// Overdue check: Due date %s has not passed -> Fine = RM 0.00%n", dueDate);
        }

        // Show fine notice if overdue
        FineManager.displayFineNotice(returnedBook.getTitle(), fine);

        // Notify next reserved user from this book's own queue (only once, here)
        ReservationQueue<User> queue = reservationQueues.get(title.toLowerCase());
        if (queue != null && !queue.isEmpty()) {
            NotificationManager.checkAndNotifyNextUser(returnedBook, queue);
        }
    }

    // FIX 2: reserveBook() now takes the book title so each book gets its
    //         own reservation queue, and prints the full Queue debug log
    //         matching Image 4.
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

        // Internal queue debug output (matches Image 4)
        System.out.println();
        System.out.println("// Internal Queue operation (ReservationQueue.enqueue)");
        System.out.println("// ENQUEUE: [" + user.getName() + "] -> placed at rear of reservationQueue");
        System.out.println("// Queue (front -> rear): [" + user.getName() + "]");
        System.out.println("// Time Complexity: O(1)");
    }

    // FIX 2 side-effect: issueReservedBook() now takes the book title so it
    //                    dequeues from the correct per-book queue.
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
    //   ABDULWAHAB — FINE CALCULATION
    // ==========================================

    // Calculates total outstanding fines for the currently logged-in user
    // based on books they still have out that are past their due date
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

    // Helper for Main.java
    public Book findBook(String title) {
        for (Book b : books) {
            if (b.getTitle().equalsIgnoreCase(title)) return b;
        }
        return null;
    }

    public User getLoggedInUser() { return loggedInUser; }

    // Updated: returns the per-book reservation queue from the HashMap
    public ReservationQueue<User> getReservationQueue(Book book) {
        return reservationQueues.getOrDefault(book.getTitle().toLowerCase(), new ReservationQueue<>());
    }

    // Accessors for Reports / Analytics (Abdulwahab & Amir)
    public ArrayList<Book> getBooks() { return books; }
    public ArrayList<User> getUsers() { return users; }
    public ArrayList<BorrowRecord> getBorrowHistory() { return borrowHistory; }
}
