package service;

import entity.Book;
import entity.Loan;
import entity.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.BookRepository;
import repository.LoanRepository;
import repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    private Book availableBook, unavailableBook;
    private User validUser;

    @Mock
    private BookRepository bookRepository;
    @Mock
    private LoanRepository loanRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoanService loanService;

    @BeforeEach
    void setUp() {
        availableBook = new Book("Cien Años de Soledad", "Gabriel García Márquez", "1234567890", true);
        unavailableBook = new Book("El Señor de los Anillos", "J.R.R. Tolkien", "9780618346252", false);
        validUser = new User("John Doe", "example@mail.com");
        loanService = new LoanService(loanRepository, bookRepository, userRepository);
    }

    /**
     * Unit tests for the loanBook method of the LoanService class.
     */
    @Nested
    class LoanBookTests {

        /**
         * Tests the successful case of loaning a book.
         */
        @Test
        void testLoanBook_Success() {
            // Arrange
            int bookId = 1;
            int userId = 1;

            LocalDate loanDate = LocalDate.now();
            LocalDate returnDate = LocalDate.now().plusWeeks(2);

            when(bookRepository.findById(bookId)).thenReturn(availableBook);
            when(userRepository.findById(userId)).thenReturn(validUser);
            when(loanRepository.saveLoan(any(Loan.class))).thenReturn(true);

            // Act
            boolean result = loanService.loanBook(bookId, userId, loanDate, returnDate);

            // Assert
            Assertions.assertTrue(result);
            verify(bookRepository, times(1)).saveBook(availableBook);
            verify(loanRepository, times(1)).saveLoan(any(Loan.class));
        }

        /**
         * Tests the case when the book to be loaned is not found.
         */
        @Test
        void testLoanBook_BookNotFound() {
            // Arrange
            int bookId = 1;
            int userId = 1;
            LocalDate loanDate = LocalDate.now();
            LocalDate returnDate = LocalDate.now().plusWeeks(2);
            String expectedMessage = "Book not found";

            when(bookRepository.findById(bookId)).thenReturn(null);

            // Act
            RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () ->
                    loanService.loanBook(bookId, userId, loanDate, returnDate)
            );

            // Assert
            Assertions.assertEquals(expectedMessage, exception.getMessage());
            verify(bookRepository, never()).saveBook(any(Book.class));
            verify(loanRepository, never()).saveLoan(any(Loan.class));
        }

        /**
         * Tests the case when the user trying to loan a book is not found.
         */
        @Test
        void testLoanBook_UserNotFound() {
            // Arrange
            int bookId = 1;
            int userId = 1;

            LocalDate loanDate = LocalDate.now();
            LocalDate returnDate = LocalDate.now().plusWeeks(2);
            String expectedMessage = "User not found";

            when(bookRepository.findById(bookId)).thenReturn(availableBook);
            when(userRepository.findById(userId)).thenReturn(null);

            // Act
            RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () ->
                    loanService.loanBook(bookId, userId, loanDate, returnDate)
            );

            // Assert
            Assertions.assertEquals(expectedMessage, exception.getMessage());
            verify(bookRepository, never()).saveBook(any(Book.class));
            verify(loanRepository, never()).saveLoan(any(Loan.class));
        }

        /**
         * Tests the case when the book to be loaned is not available.
         */
        @Test
        void testLoanBook_BookNotAvailable() {
            // Arrange
            int bookId = 1;
            int userId = 1;
            LocalDate loanDate = LocalDate.now();
            LocalDate returnDate = LocalDate.now().plusWeeks(2);
            String expectedMessage = "Book is not available for loan";

            when(bookRepository.findById(bookId)).thenReturn(unavailableBook);
            when(userRepository.findById(userId)).thenReturn(validUser);

            // Act
            RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () ->
                    loanService.loanBook(bookId, userId, loanDate, returnDate)
            );

            // Assert
            Assertions.assertEquals(expectedMessage, exception.getMessage());
            verify(bookRepository, never()).saveBook(any(Book.class));
            verify(loanRepository, never()).saveLoan(any(Loan.class));
        }

        /**
         * Tests the case when the loan date is invalid (before the current date).
         */
        @Test
        void testLoanBook_LoanDateInvalid() {
            // Arrange
            int bookId = 1;
            int userId = 1;
            LocalDate loanDate = LocalDate.now().minusDays(2);
            LocalDate returnDate = LocalDate.now().plusWeeks(2);
            String expectedMessage = "Loan date invalid";

            when(bookRepository.findById(bookId)).thenReturn(availableBook);
            when(userRepository.findById(userId)).thenReturn(validUser);

            // Act
            RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () ->
                    loanService.loanBook(bookId, userId, loanDate, returnDate)
            );

            // Assert
            Assertions.assertEquals(expectedMessage, exception.getMessage());
            verify(bookRepository, never()).saveBook(any(Book.class));
            verify(loanRepository, never()).saveLoan(any(Loan.class));
        }

        /**
         * Tests the case when the return date is invalid (before or equal to the loan date).
         */
        @Test
        void testLoanBook_ReturnDateInvalid() {
            // Arrange
            int bookId = 1;
            int userId = 1;
            LocalDate loanDate = LocalDate.now();
            LocalDate returnDate = LocalDate.now();
            String expectedMessage = "Return date must be after the loan date";

            when(bookRepository.findById(bookId)).thenReturn(availableBook);
            when(userRepository.findById(userId)).thenReturn(validUser);

            // Act
            RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () ->
                    loanService.loanBook(bookId, userId, loanDate, returnDate)
            );

            // Assert
            Assertions.assertEquals(expectedMessage, exception.getMessage());
            verify(bookRepository, never()).saveBook(any(Book.class));
            verify(loanRepository, never()).saveLoan(any(Loan.class));
        }

    }

    /**
     * Unit tests for the returnBook method of the LoanService class.
     */
    @Nested
    class ReturnBookTests {

        /**
         * Tests the successful case of returning a book.
         */
        @Test
        void testReturnBook_Success() {
            // Arrange
            int loanId = 1;
            Loan activeLoan = new Loan(1, 1, LocalDate.now(), LocalDate.now().plusDays(2), LocalDate.now().plusDays(2));

            when(loanRepository.findById(loanId)).thenReturn(activeLoan);
            when(bookRepository.findById(activeLoan.getBookID())).thenReturn(unavailableBook);
            when(bookRepository.updateBook(activeLoan.getBookID(), unavailableBook)).thenReturn(true);
            when(loanRepository.updateReturnedDate(loanId, LocalDate.now())).thenReturn(true);

            // Act
            boolean result = loanService.returnBook(loanId);

            // Assert
            Assertions.assertTrue(result);
            verify(loanRepository, times(1)).findById(loanId);
            verify(bookRepository, times(1)).findById(activeLoan.getBookID());
            verify(bookRepository, times(1)).updateBook(activeLoan.getBookID(), unavailableBook);
            verify(loanRepository, times(1)).updateReturnedDate(loanId, LocalDate.now());
        }

        /**
         * Tests the case when the loan to be returned is not found.
         */
        @Test
        void testReturnBook_LoanNotFound() {
            // Arrange
            int loanId = 1;
            String expectedMessage = "Loan not found";

            when(loanRepository.findById(loanId)).thenReturn(null);

            // Act
            RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> loanService.returnBook(loanId));

            // Assert
            Assertions.assertEquals(expectedMessage, exception.getMessage());
            verify(loanRepository, times(1)).findById(loanId);
            verify(bookRepository, never()).findById(anyInt());
            verify(bookRepository, never()).updateBook(anyInt(), any(Book.class));
            verify(loanRepository, never()).updateReturnedDate(anyInt(), any(LocalDate.class));
        }

        /**
         * Tests the case when the book availability update fails upon returning the book.
         */
        @Test
        void testReturnBook_BookAvailabilityUpdateFailed() {
            // Arrange
            int loanId = 1;
            Loan activeLoan = new Loan(1, 1, LocalDate.now(), LocalDate.now().plusDays(2), LocalDate.now().plusDays(2));
            String expectedMessage = "Book availability update failed";

            when(loanRepository.findById(loanId)).thenReturn(activeLoan);
            when(bookRepository.findById(activeLoan.getBookID())).thenReturn(unavailableBook);
            when(bookRepository.updateBook(activeLoan.getBookID(), unavailableBook)).thenReturn(false);

            // Act
            RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> loanService.returnBook(loanId));

            // Assert
            Assertions.assertEquals(expectedMessage, exception.getMessage());
            verify(loanRepository, times(1)).findById(loanId);
            verify(bookRepository, times(1)).findById(activeLoan.getBookID());
            verify(bookRepository, times(1)).updateBook(activeLoan.getBookID(), unavailableBook);
            verify(loanRepository, never()).updateReturnedDate(anyInt(), any(LocalDate.class));
        }

        /**
         * Tests the case when the loan returned date update fails.
         */
        @Test
        void testReturnBook_UpdateReturnedDateFailed() {
            // Arrange
            int loanId = 1;
            Loan activeLoan = new Loan(1, 1, LocalDate.now(), LocalDate.now().plusDays(2), LocalDate.now().plusDays(2));
            String expectedMessage = "Loan returned date update failed";

            when(loanRepository.findById(loanId)).thenReturn(activeLoan);
            when(bookRepository.findById(activeLoan.getBookID())).thenReturn(unavailableBook);
            when(bookRepository.updateBook(activeLoan.getBookID(), unavailableBook)).thenReturn(true);
            when(loanRepository.updateReturnedDate(loanId, LocalDate.now())).thenReturn(false);

            // Act
            RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> loanService.returnBook(loanId));

            // Assert
            Assertions.assertEquals(expectedMessage, exception.getMessage());
            verify(loanRepository, times(1)).findById(loanId);
            verify(bookRepository, times(1)).findById(activeLoan.getBookID());
            verify(bookRepository, times(1)).updateBook(activeLoan.getBookID(), unavailableBook);
            verify(loanRepository, times(1)).updateReturnedDate(loanId, LocalDate.now());
        }
    }

    /**
     * Unit tests for the getLoanHistory method of the LoanService class.
     */
    @Nested
    class GetLoanHistoryTests {

        /**
         * Tests the successful case of getting the loan history of a user.
         */
        @Test
        void testGetLoanHistory_Success() {
            // Arrange
            int userId = 1;
            Loan loan1 = new Loan(1, 1, LocalDate.now(), LocalDate.now().plusDays(2), LocalDate.now().plusDays(2));
            Loan loan2 = new Loan(2, 1, LocalDate.now(), LocalDate.now().plusDays(2), LocalDate.now().plusDays(2));
            List<Loan> expectedLoans = Arrays.asList(loan1, loan2);

            when(userRepository.findById(userId)).thenReturn(validUser);
            when(loanRepository.findByUserId(userId)).thenReturn(expectedLoans);

            // Act
            List<Loan> result = loanService.getLoanHistory(userId);

            // Assert
            Assertions.assertEquals(expectedLoans, result);
            verify(userRepository, times(1)).findById(userId);
            verify(loanRepository, times(1)).findByUserId(userId);
        }

        /**
         * Tests the case when the user whose loan history is to be retrieved is not found.
         */
        @Test
        void testGetLoanHistory_UserNotFound() {
            // Arrange
            int userId = 1;
            String expectedMessage = "User not found";
            when(userRepository.findById(userId)).thenReturn(null);

            // Act
            RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> loanService.getLoanHistory(userId));

            // Assert
            Assertions.assertEquals(expectedMessage, exception.getMessage());
            verify(userRepository, times(1)).findById(userId);
            verify(loanRepository, never()).findByUserId(anyInt());
        }

        /**
         * Tests the case when the user has no loans registered.
         */
        @Test
        void testGetLoanHistory_LoansNotFound() {
            // Arrange
            int userId = 1;
            List<Loan> expectedLoans = new ArrayList<>();
            String expectedMessage = "No loan found for user";

            when(userRepository.findById(userId)).thenReturn(validUser);
            when(loanRepository.findByUserId(userId)).thenReturn(expectedLoans);

            // Act
            RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> loanService.getLoanHistory(userId));

            // Assert
            Assertions.assertEquals(expectedMessage, exception.getMessage());
            verify(userRepository, times(1)).findById(userId);
            verify(loanRepository, times(1)).findByUserId(userId);
        }
    }

}