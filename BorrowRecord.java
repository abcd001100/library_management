import java.time.LocalDate;

// Represents one borrow event - used for fines, reports, and analytics
public class BorrowRecord {
    private String bookTitle;
    private String genre;
    private String username;
    private String userFullName;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate; // null while the book is still out
    private double fineAmount;

    public BorrowRecord(String bookTitle, String genre, String username, String userFullName,
                         LocalDate borrowDate, LocalDate dueDate) {
        this.bookTitle = bookTitle;
        this.genre = genre;
        this.username = username;
        this.userFullName = userFullName;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = null;
        this.fineAmount = 0.0;
    }

    // Getters
    public String getBookTitle()    { return bookTitle; }
    public String getGenre()        { return genre; }
    public String getUsername()     { return username; }
    public String getUserFullName() { return userFullName; }
    public LocalDate getBorrowDate(){ return borrowDate; }
    public LocalDate getDueDate()   { return dueDate; }
    public LocalDate getReturnDate(){ return returnDate; }
    public double getFineAmount()   { return fineAmount; }

    // Setters
    public void setDueDate(LocalDate dueDate)       { this.dueDate = dueDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public void setFineAmount(double fineAmount)    { this.fineAmount = fineAmount; }

    @Override
    public String toString() {
        String status = (returnDate == null) ? "ONGOING" : "RETURNED on " + returnDate;
        String fineInfo = (fineAmount > 0) ? " | Fine: RM " + String.format("%.2f", fineAmount) : "";
        return "\"" + bookTitle + "\" (" + genre + ") | Borrower: " + userFullName
                + " | Borrowed: " + borrowDate + " | Due: " + dueDate
                + " | Status: " + status + fineInfo;
    }
}
