public class Library {
    // Abdulaziz will put his User and Book inventory arrays/lists up here.
    
    // 1. YOUR VARIABLES: I need to instantiate my data structures here.
    private BorrowStack<Book> borrowStack;
    private ReservationQueue<User> reservationQueue;

    // 2. CONSTRUCTOR: Initialize my structures when the Library is created.
    public Library() {
        this.borrowStack = new BorrowStack<>();
        this.reservationQueue = new ReservationQueue<>();
    }

    // ==========================================
    //       SHEE'S INTEGRATION METHODS
    // ==========================================

    // BORROW A BOOK (Uses your Stack)
    public void borrowBook(Book book) {
        borrowStack.push(book);
        System.out.println("Book borrowed successfully! Added to borrow history.");
    }

    // RETURN A BOOK (Uses your Stack)
    public void returnBook() {
        Book returnedBook = borrowStack.pop();
        if (returnedBook != null) {
            System.out.println("Book returned successfully!");
        }
    }

    // RESERVE A BOOK (Uses your Queue)
    public void reserveBook(User user) {
        reservationQueue.enqueue(user);
        System.out.println(user.getName() + " has been added to the reservation waiting list.");
    }

    // ISSUE A RESERVED BOOK (Uses your Queue)
    public void issueReservedBook() {
        User nextUser = reservationQueue.dequeue();
        if (nextUser != null) {
            System.out.println("Book issued to: " + nextUser.getName());
        }
    }
}
