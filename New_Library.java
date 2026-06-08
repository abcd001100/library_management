import java.util.ArrayList;

public class New_Library {

    // ── Abdulaziz's data ──────────────────────────────────────
    private ArrayList<User> users;
    private ArrayList<Book> books;
    private User loggedInUser;

    // ── Shee's data structures ────────────────────────────────
    private BorrowStack<Book> borrowStack;
    private ReservationQueue<User> reservationQueue;

    public New_Library() {
        users = new ArrayList<>();
        books = new ArrayList<>();
        loggedInUser = null;
        borrowStack = new BorrowStack<>();
        reservationQueue = new ReservationQueue<>();
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
                b.renew(hasPending);
                return;
            }
        }
        System.out.println("Book not found.");
    }

    // ==========================================
    //   SHEE — STACK & QUEUE METHODS (unchanged)
    // ==========================================

    public void borrowBook(Book book) {
        if (book.borrow()) {
            borrowStack.push(book);
            System.out.println("Book borrowed! Due date: " + book.getDueDate());
        } else {
            System.out.println("Sorry, no copies available. Consider reserving it.");
        }
    }

    public void returnBook() {
        Book returnedBook = borrowStack.pop();
        if (returnedBook != null) {
            returnedBook.returnBook();
            System.out.println("Book returned successfully: " + returnedBook.getTitle());
        }
    }

    public void reserveBook(User user) {
        reservationQueue.enqueue(user);
        System.out.println(user.getName() + " added to reservation waiting list.");
    }

    public void issueReservedBook() {
        User nextUser = reservationQueue.dequeue();
        if (nextUser != null)
            System.out.println("Book issued to: " + nextUser.getName());
    }

    // Helper for Main.java
    public Book findBook(String title) {
        for (Book b : books) {
            if (b.getTitle().equalsIgnoreCase(title)) return b;
        }
        return null;
    }

    public User getLoggedInUser() { return loggedInUser; }
}