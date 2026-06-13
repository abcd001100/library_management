import java.util.ArrayList;
import java.time.LocalDate;

public class New_Library {

    // ── Abdulaziz's data ──────────────────────────────────────
    private ArrayList<User> users;
    private ArrayList<Book> books;
    private User loggedInUser;

    // ── Shee's data structures ────────────────────────────────
    private BorrowStack<Book> borrowStack;
    private ReservationQueue<User> reservationQueue;

    // ── Abdulwahab / Amir — borrow history for fines & analytics ──
    private ArrayList<BorrowRecord> borrowHistory;

    public New_Library() {
        users = new ArrayList<>();
        books = new ArrayList<>();
        loggedInUser = null;
        borrowStack = new BorrowStack<>();
        reservationQueue = new ReservationQueue<>();
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

    // Due date notification
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

    // Book renewal
    public void renewBook(String title) {
        for (Book b : books) {
            if (b.getTitle().equalsIgnoreCase(title)) {
                boolean hasPending = reservationQueue.getSize() > 0;
                if (b.renew(hasPending)) {
                    // Keep the matching open borrow record's due date in sync
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
        } else {
            System.out.println("Sorry, no copies available. Consider reserving it.");
        }
    }

    public void returnBook() {
        Book returnedBook = borrowStack.pop();
        if (returnedBook != null) {

            // Find the matching open borrow record for this book (most recent unreturned)
            BorrowRecord record = null;
            for (int i = borrowHistory.size() - 1; i >= 0; i--) {
                BorrowRecord r = borrowHistory.get(i);
                if (r.getBookTitle().equals(returnedBook.getTitle()) && r.getReturnDate() == null) {
                    record = r;
                    break;
                }
            }

            double fine = 0.0;
            if (record != null) {
                fine = FineManager.calculateFine(record.getDueDate(), LocalDate.now());
                record.setReturnDate(LocalDate.now());
                record.setFineAmount(fine);
            }

            returnedBook.returnBook();
            System.out.println("Book returned successfully: " + returnedBook.getTitle());

            // Show fine notice if the book was overdue
            FineManager.displayFineNotice(returnedBook.getTitle(), fine);

            // EDITED PART: Automatically checks for waiting reservations right upon popping the return stack
            if (!reservationQueue.isEmpty()) {
                NotificationManager.checkAndNotifyNextUser(returnedBook, reservationQueue);
            }
        }
    }

    public void reserveBook(User user) {
        reservationQueue.enqueue(user);
        System.out.println(user.getName() + " added to reservation waiting list.");
    }

    public void issueReservedBook() {
        User nextUser = reservationQueue.dequeue();
        if (nextUser != null) {
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

    // EDITED PART: Accessor method allowing Main.java to read Shee's reservation queue layout directly
    public ReservationQueue<User> getReservationQueue(Book book) {
        return this.reservationQueue;
    }

    // Accessors for Reports / Analytics (Abdulwahab & Amir)
    public ArrayList<Book> getBooks() { return books; }
    public ArrayList<User> getUsers() { return users; }
    public ArrayList<BorrowRecord> getBorrowHistory() { return borrowHistory; }
}
