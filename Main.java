import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        New_Library library = new New_Library();
        Scanner sc = new Scanner(System.in);

        // Seed some sample data so you can demo immediately
        library.addBook("The Great Gatsby", "F. Scott Fitzgerald", 1925, "Fiction", 3);
        library.addBook("Clean Code", "Robert Martin", 2008, "Technology", 2);
        library.registerUser("Abdulaziz", "KL", "0123456789", "az@email.com", "aziz", "pass123");

        int choice;
        do {
            System.out.println("\n===== LIBRARY MANAGEMENT SYSTEM =====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Update My Info");
            System.out.println("4. Forgot Password");
            System.out.println("5. Add Book");
            System.out.println("6. Search Books");
            System.out.println("7. View Book Details");
            System.out.println("8. Borrow Book");
            System.out.println("9. Return Book");
            System.out.println("10. Reserve Book");
            System.out.println("11. Check Due Date");
            System.out.println("12. Renew Book");
            System.out.println("13. Issue Reserved Book");
            System.out.println("14. View My Fines");
            System.out.println("15. Pay Fine");
            System.out.println("16. Inventory Report");
            System.out.println("17. Registered Users Report");
            System.out.println("18. Borrow History Report");
            System.out.println("19. Popular Books / Genres Analytics");
            System.out.println("20. User Behavior Analytics");
            System.out.println("0. Exit");
            System.out.print("Choose: ");
            choice = sc.nextInt(); sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Name: "); String name = sc.nextLine();
                    System.out.print("Address: "); String addr = sc.nextLine();
                    System.out.print("Contact: "); String contact = sc.nextLine();
                    System.out.print("Email: "); String email = sc.nextLine();
                    System.out.print("Username: "); String uname = sc.nextLine();
                    System.out.print("Password: "); String pass = sc.nextLine();
                    library.registerUser(name, addr, contact, email, uname, pass);
                    break;
                case 2:
                    System.out.print("Username: "); String lu = sc.nextLine();
                    System.out.print("Password: "); String lp = sc.nextLine();
                    library.login(lu, lp);
                    break;
                case 3:
                    System.out.print("New Address: "); String na = sc.nextLine();
                    System.out.print("New Contact: "); String nc = sc.nextLine();
                    System.out.print("New Email: "); String ne = sc.nextLine();
                    library.updateUserInfo(na, nc, ne);
                    break;
                case 4:
                    System.out.print("Username: "); String fu = sc.nextLine();
                    System.out.print("New Password: "); String fp = sc.nextLine();
                    library.forgotPassword(fu, fp);
                    break;
                case 5:
                    System.out.print("Title: "); String bt = sc.nextLine();
                    System.out.print("Author: "); String ba = sc.nextLine();
                    System.out.print("Year: "); int by = sc.nextInt(); sc.nextLine();
                    System.out.print("Genre: "); String bg = sc.nextLine();
                    System.out.print("Copies: "); int bc = sc.nextInt(); sc.nextLine();
                    library.addBook(bt, ba, by, bg, bc);
                    break;
                case 6:
                    System.out.print("Search keyword: "); String kw = sc.nextLine();
                    library.searchBooks(kw);
                    break;
                case 7:
                    System.out.print("Book title: "); String vt = sc.nextLine();
                    library.viewBookDetails(vt);
                    break;
                case 8:
                    System.out.print("Book title to borrow: "); String bbt = sc.nextLine();
                    Book bb = library.findBook(bbt);
                    if (bb != null) library.borrowBook(bb);
                    else System.out.println("Book not found.");
                    break;
                case 9:
                    System.out.print("Enter book title to return: ");
                    String returnTitle = sc.nextLine();
                    Book returnedBook = library.findBook(returnTitle);
                    
                    if (returnedBook != null) {
                        library.returnBook();
                        
                        // EDITED PART: Triggers notification module upon book return
                        NotificationManager.checkAndNotifyNextUser(returnedBook, library.getReservationQueue(returnedBook));
                    } else {
                        System.out.println("Book not found.");
                    }
                    break;
                case 10:
                    User currentUser = library.getLoggedInUser();
                    if (currentUser != null) library.reserveBook(currentUser);
                    else System.out.println("Please login first.");
                    break;
                case 11:
                    System.out.print("Book title: "); String dt = sc.nextLine();
                    library.showDueDate(dt);
                    break;
                case 12:
                    System.out.print("Book title to renew: "); String rt = sc.nextLine();
                    library.renewBook(rt);
                    break;
                case 13:
                    System.out.print("Enter book title to issue from reservation: ");
                    String issueTitle = sc.nextLine();
                    Book targetBook = library.findBook(issueTitle);
                    
                    if (targetBook != null) {
                        ReservationQueue<User> queue = library.getReservationQueue(targetBook);
                        if (queue != null && !queue.isEmpty()) {
                            User nextStudent = queue.peek();
                            
                            // EDITED PART: Calculates hold duration and displays hold banner
                            java.time.LocalDateTime expiry = ReservationHoldingManager.calculateHoldExpiry();
                            ReservationHoldingManager.displayHoldStatus(targetBook.getTitle(), nextStudent.getName(), expiry);
                        }
                        library.issueReservedBook();
                    } else {
                        System.out.println("Book not found.");
                    }
                    break;

                // ===== ABDULWAHAB — FINE CALCULATION & PAYMENT =====
                case 14:
                    if (library.getLoggedInUser() == null) {
                        System.out.println("Please login first.");
                    } else {
                        double fine = library.calculateMyFines();
                        System.out.println("\n----------------------------------------");
                        System.out.println("Outstanding fines for " + library.getLoggedInUser().getName() + ":");
                        System.out.println("RM " + String.format("%.2f", fine));
                        System.out.println("----------------------------------------\n");
                    }
                    break;
                case 15:
                    if (library.getLoggedInUser() == null) {
                        System.out.println("Please login first.");
                    } else {
                        System.out.println("Choose Payment Method:");
                        System.out.println("1. Online Payment");
                        System.out.println("2. Pay at Library");
                        int payChoice = sc.nextInt(); sc.nextLine();

                        double fineAmount = library.calculateMyFines();

                        if (payChoice == 1) {
                            System.out.println("Processing Online Payment...");
                            FineManager.simulatePayment(library.getLoggedInUser(), fineAmount);
                        } else if (payChoice == 2) {
                            System.out.println("Paying at Library Counter...");
                            FineManager.simulatePayment(library.getLoggedInUser(), fineAmount);
                        } else {
                            System.out.println("Invalid option.");
                        }
                    }
                    break;

                // ===== ABDULWAHAB — REPORTS =====
                case 16:
                    Report.generateInventoryReport(library.getBooks());
                    break;
                case 17:
                    Report.generateUserReport(library.getUsers());
                    break;
                case 18:
                    Report.generateBorrowHistoryReport(library.getBorrowHistory());
                    break;

                // ===== AMIR — ANALYTICS =====
                case 19:
                    Report.generatePopularBooksReport(library.getBorrowHistory());
                    break;
                case 20:
                    Report.generateUserActivityReport(library.getBorrowHistory());
                    break;

                case 0:
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } while (choice != 0);

        sc.close();
    }
}
