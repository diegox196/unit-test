package service;

import entity.Book;
import entity.Loan;
import entity.User;
import repository.BookRepository;
import repository.LoanRepository;
import repository.UserRepository;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides services for managing loan book operations.
 * It acts as an intermediary between the application and the loan repository,
 * handling business logic and validations related to loan data.
 */
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    /**
     * Constructs a new LoanService instance.
     *
     * @param loanRepository The LoanRepository instance to be used for loan operations.
     * @param bookRepository The BookRepository instance to be used for book operations.
     * @param userRepository The UserRepository instance to be used for user operations.
     */
    public LoanService(LoanRepository loanRepository, BookRepository bookRepository, UserRepository userRepository,EmailService emailService) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    /**
     * Registers a loan of a book to a user.
     *
     * @param bookId     The ID of the book being loaned.
     * @param userId     The ID of the user taking the loan.
     * @param loanDate   The date the loan starts.
     * @param returnDate The date the loan is due.
     */
    public boolean loanBook(int bookId, int userId, LocalDate loanDate, LocalDate returnDate) {
        Book book = bookRepository.findById(bookId);
        User user = userRepository.findById(userId);

        if (book == null) {
            throw new RuntimeException("Book not found");
        }

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (!book.isAvailable()) {
            throw new RuntimeException("Book is not available for loan");
        }

        if (loanDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Loan date invalid");
        }

        if (returnDate.isBefore(loanDate) || returnDate.isEqual(loanDate)) {
            throw new IllegalArgumentException("Return date must be after the loan date");
        }

        book.setAvailable(false);
        bookRepository.saveBook(book);

        Loan loan = new Loan(bookId, userId, loanDate, returnDate);
        boolean result = loanRepository.saveLoan(loan);

        bookRepository.closeConnection();
        userRepository.closeConnection();
        loanRepository.closeConnection();

        return result;
    }


    /**
     * Registers the return of a loaned book.
     *
     * @param loanId The ID of the loan being returned.
     */
    public boolean returnBook(int loanId) {
        Loan loan = loanRepository.findById(loanId);
        if (loan == null) {
            throw new RuntimeException("Loan not found");
        }

        Book book = bookRepository.findById(loan.getBookID());
        book.setAvailable(true);

        boolean savedBook = bookRepository.updateBook(loan.getBookID(), book);
        if (!savedBook) {
            throw new RuntimeException("Book availability update failed");
        }
        boolean updatedLoan = loanRepository.updateReturnedDate(loanId, LocalDate.now());
        if (!updatedLoan) {
            throw new RuntimeException("Loan returned date update failed");
        }

        bookRepository.closeConnection();
        loanRepository.closeConnection();

        return true;
    }

    /**
     * Retrieves the loan history of a user.
     *
     * @param userId The ID of the user.
     * @return List of loans associated with the user.
     */
    public List<Loan> getLoanHistory(int userId) {
        User user = userRepository.findById(userId);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        List<Loan> loanList = loanRepository.findByUserId(userId);
        if (loanList.isEmpty()) {
            throw new RuntimeException("No loan found for user");
        }

        userRepository.closeConnection();
        loanRepository.closeConnection();

        return loanList;
    }


    /**
     * Generates a report of books that have not been returned by the specified date.
     * @return List of books that are overdue.
     * @throws IllegalArgumentException if the date is invalid or null.
     */
    public List<Book> generateOverdueBooksReport(String actualDate) {
        if (actualDate == null || actualDate.isEmpty()) {
            throw new IllegalArgumentException("Date cannot be null or empty");
        }
        try {
            Date date = Date.valueOf(actualDate);
            return loanRepository.findOverdueBooks(date);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid date format. Please use 'yyyy-MM-dd'");
        } catch (RuntimeException e) {
            throw new RuntimeException("Connection error", e);
        }
    }


    public boolean SendNotification(int userID, int bookID, String dateReturn) {
        try {
            LocalDate date = LocalDate.parse(dateReturn, DateTimeFormatter.ISO_LOCAL_DATE);

            User user = userRepository.findById(userID);
            if (user == null) {
                return false;
            }

            Book book = bookRepository.findById(bookID);
            if (book == null) {
                return false;
            }

            String emailBody = "Estimado " + user.getName() + ",\n\n"
                    + "Este es un recordatorio de que debe devolver el libro '" + book.getTitle()
                    + "' antes del " + dateReturn + ".\n\n"
                    + "Gracias.";

            return emailService.sendEmail(user.getEmail(), "Recordatorio de Devolución de Libro", emailBody);
        } catch (DateTimeParseException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error de conexión al servidor de correos", e);
        }
    }
}
