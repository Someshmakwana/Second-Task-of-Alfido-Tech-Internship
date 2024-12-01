import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

class Book {
    private int id;
    private String title;
    private String author;
    private boolean isBorrowed;
    private LocalDate dueDate;

    public Book(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isBorrowed = false;
        this.dueDate = null;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isBorrowed() {
        return isBorrowed;
    }

    public void borrowBook(int daysToBorrow) {
        isBorrowed = true;
        dueDate = LocalDate.now().plusDays(daysToBorrow);
    }

    public void returnBook() {
        isBorrowed = false;
        dueDate = null;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public long calculateFine() {
        if (isBorrowed && LocalDate.now().isAfter(dueDate)) {
            return ChronoUnit.DAYS.between(dueDate, LocalDate.now()) * 10; // Fine is 10 units per day
        }
        return 0;
    }

    @Override
    public String toString() {
        return id + "," + title + "," + author + "," + isBorrowed + "," + (dueDate != null ? dueDate : "N/A");
    }

    public static Book fromString(String data) {
        String[] parts = data.split(",");
        Book book = new Book(Integer.parseInt(parts[0]), parts[1], parts[2]);
        book.isBorrowed = Boolean.parseBoolean(parts[3]);
        if (!parts[4].equals("N/A")) {
            book.dueDate = LocalDate.parse(parts[4]);
        }
        return book;
    }
}

public class LibraryManagementSystem {
    private static final String FILE_NAME = "books.txt";
    private static List<Book> books = new ArrayList<>();

    public static void loadBooks() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                books.add(Book.fromString(line));
            }
        } catch (IOException e) {
            System.out.println("Error loading books: " + e.getMessage());
        }
    }

    public static void saveBooks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Book book : books) {
                writer.write(book.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving books: " + e.getMessage());
        }
    }

    public static void addBook(Book book) {
        books.add(book);
        saveBooks();
        System.out.println("Book added successfully!");
    }

    public static void removeBook(int id) {
        books.removeIf(book -> book.getId() == id);
        saveBooks();
        System.out.println("Book removed successfully!");
    }

    public static void borrowBook(int id, int daysToBorrow) {
        for (Book book : books) {
            if (book.getId() == id && !book.isBorrowed()) {
                book.borrowBook(daysToBorrow);
                saveBooks();
                System.out.println("Book borrowed successfully! Due date: " + book.getDueDate());
                return;
            }
        }
        System.out.println("Book not available for borrowing.");
    }

    public static void returnBook(int id) {
        for (Book book : books) {
            if (book.getId() == id && book.isBorrowed()) {
                long fine = book.calculateFine();
                book.returnBook();
                saveBooks();
                System.out.println("Book returned successfully!");
                if (fine > 0) {
                    System.out.println("Fine incurred: " + fine + " units");
                }
                return;
            }
        }
        System.out.println("Book not found or not borrowed.");
    }

    public static void displayBooks() {
        for (Book book : books) {
            System.out.println(book);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        loadBooks();

        while (true) {
            System.out.println("\n--- Library Management System ---");
            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. Borrow Book");
            System.out.println("4. Return Book");
            System.out.println("5. Display Books");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1: // Add Book
                    System.out.print("Enter Book ID: ");
                    int id = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter Book Title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter Author Name: ");
                    String author = scanner.nextLine();
                    addBook(new Book(id, title, author));
                    break;

                case 2: // Remove Book
                    System.out.print("Enter Book ID to remove: ");
                    removeBook(scanner.nextInt());
                    break;

                case 3: // Borrow Book
                    System.out.print("Enter Book ID to borrow: ");
                    id = scanner.nextInt();
                    System.out.print("Enter number of days to borrow: ");
                    int daysToBorrow = scanner.nextInt();
                    borrowBook(id, daysToBorrow);
                    break;

                case 4: // Return Book
                    System.out.print("Enter Book ID to return: ");
                    id = scanner.nextInt();
                    returnBook(id);
                    break;

                case 5: // Display Books
                    displayBooks();
                    break;

                case 6: // Exit
                    System.out.println("Exiting... Thank you!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
}
