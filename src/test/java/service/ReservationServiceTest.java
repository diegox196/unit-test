package service;

import entity.Book;
import entity.Reservation;
import entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.BookRepository;
import repository.ReservationRepository;
import repository.UserRepository;

import static org.mockito.Mockito.*;

/**
 * Unit tests for the ReservationService class.
 */
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Book availableBook, unavailableBook;
    private User validUser;

    @BeforeEach
    void setUp() {
        availableBook = new Book("Available Book", "Author", "1234567890", true);
        unavailableBook = new Book("Unavailable Book", "Author", "9876543210", false);
        validUser = new User("John Doe", "john.doe@example.com");
        reservationService = new ReservationService(reservationRepository, bookRepository, userRepository);
    }

    /**
     * Unit tests for the reserveBook method.
     */
    @Nested
    class ReserveBookTests {
        /**
         * Tests the successful case of reserving a book.
         */
        @Test
        void testReserveBook_Success() {
            // Arrange
            int bookId = 123;
            int userId = 456;

            when(bookRepository.findById(bookId)).thenReturn(unavailableBook);
            when(userRepository.findById(userId)).thenReturn(validUser);
            when(reservationRepository.saveReservation(any(Reservation.class))).thenReturn(true);

            // Act
            boolean result = reservationService.reserveBook(bookId, userId);

            // Assert
            Assertions.assertTrue(result);
            verify(bookRepository, times(1)).findById(bookId);
            verify(userRepository, times(1)).findById(userId);
            verify(reservationRepository, times(1)).saveReservation(any(Reservation.class));
        }

        /**
         * Tests the case when the book to be reserved is not found.
         */
        @Test
        void testReserveBook_BookNotFound() {
            // Arrange
            int bookId = 123;
            int userId = 456;
            String expectedMessage = "Book not found";

            when(bookRepository.findById(bookId)).thenReturn(null);

            // Act
            RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () ->
                    reservationService.reserveBook(bookId, userId)
            );

            // Assert
            Assertions.assertEquals(expectedMessage, exception.getMessage());
            verify(bookRepository, times(1)).findById(bookId);
            verify(reservationRepository, never()).saveReservation(any(Reservation.class));
        }

        /**
         * Tests the case when the user trying to reserve a book is not found.
         */
        @Test
        void testReserveBook_UserNotFound() {
            // Arrange
            int bookId = 123;
            int userId = 456;
            String expectedMessage = "User not found";

            when(bookRepository.findById(bookId)).thenReturn(unavailableBook);
            when(userRepository.findById(userId)).thenReturn(null);

            // Act
            RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () ->
                    reservationService.reserveBook(bookId, userId)
            );

            // Assert
            Assertions.assertEquals(expectedMessage, exception.getMessage());
            verify(bookRepository, times(1)).findById(bookId);
            verify(userRepository, times(1)).findById(userId);
            verify(reservationRepository, never()).saveReservation(any(Reservation.class));
        }

        /**
         * Tests the case when the book to be reserved is already available (not on loan).
         */
        @Test
        void testReserveBook_BookAvailable() {
            // Arrange
            int bookId = 1;
            int userId = 1;
            String expectedMessage = "Cannot be reserved because the book is not on loan.";

            when(bookRepository.findById(bookId)).thenReturn(availableBook);
            when(userRepository.findById(userId)).thenReturn(validUser);

            // Act
            RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () ->
                    reservationService.reserveBook(bookId, userId)
            );

            // Assert
            Assertions.assertEquals(expectedMessage, exception.getMessage());
            verify(bookRepository, times(1)).findById(bookId);
            verify(userRepository, times(1)).findById(userId);
            verify(reservationRepository, never()).saveReservation(any(Reservation.class));
        }
    }

}