import java.time.LocalDate;

public class Book {
    private String title;
    private String author;
    private int publicationYear;
    private String genre;
    private int totalCopies;
    private int availableCopies;
    private LocalDate dueDate;

    public Book(String title, String author, int publicationYear,
                String genre, int totalCopies) {
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.genre = genre;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
        this.dueDate = null;
    }

    // Getters
    public String getTitle()         { return title; }
    public String getAuthor()        { return author; }
    public int getPublicationYear()  { return publicationYear; }
    public int getGenre()            { return genre; }
    public int getTotalCopies()      { return totalCopies; }
    public int getAvailableCopies()  { return availableCopies; }
    public LocalDate getDueDate()    { return dueDate; }

    // Setters
    public void setTitle(String title)               { this.title = title; }
    public void setAuthor(String author)             { this.author = author; }
    public void setTotalCopies(int totalCopies)      { this.totalCopies = totalCopies; }
    public void setAvailableCopies(int n)            { this.availableCopies = n; }
    public void setDueDate(LocalDate dueDate)        { this.dueDate = dueDate; }

    // Borrow: reduce available copies, set due date (14 days)
    public boolean borrow() {
        if (availableCopies > 0) {
            availableCopies--;
            dueDate = LocalDate.now().plusDays(14);
            return true;
        }
        return false;
    }

    // Return: increase available copies, clear due date
    public void returnBook() {
        if (availableCopies < totalCopies) {
            availableCopies++;
            dueDate = null;
        }
    }

    // Renew: extend due date by 14 more days
    public boolean renew(boolean hasPendingReservations) {
        if (hasPendingReservations) {
            System.out.println("Cannot renew — there are pending reservations for this book.");
            return false;
        }
        if (dueDate != null) {
            dueDate = dueDate.plusDays(14);
            System.out.println("Book renewed! New due date: " + dueDate);
            return true;
        }
        System.out.println("This book has no active due date.");
        return false;
    }

    // Added for ReservationHoldingManager compatibility
    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    @Override
    public String toString() {
        return "[" + title + "] by " + author + " (" + publicationYear + ") | Genre: " + genre
               + " | Available: " + availableCopies + "/" + totalCopies
               + (dueDate != null ? " | Due: " + dueDate : "");
    }
}