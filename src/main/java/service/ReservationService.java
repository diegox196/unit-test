package service;

import entity.Book;
import entity.Reservation;
import entity.User;
import repository.BookRepository;
import repository.ReservationRepository;
import repository.UserRepository;

import java.time.LocalDate;

public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    /**
     * Constructs a new LoanService instance.
     *
     * @param reservationRepository The ReservationRepository instance to be used for reservation operations.
     * @param bookRepository The BookRepository instance to be used for book operations.
     * @param userRepository The UserRepository instance to be used for user operations.
     */
    public ReservationService(ReservationRepository reservationRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    /**
     * Makes a reservation for a book.
     *
     * @param bookId The ID of the book to be reserved.
     * @param userId The ID of the user making the reservation.
     * @return The created reservation.
     */
    public boolean reserveBook(int bookId, int userId) {
        Book book = bookRepository.findById(bookId);
        User user = userRepository.findById(userId);

        if (book == null) {
            throw new RuntimeException("Book not found");
        }

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (book.isAvailable()) {
            throw new RuntimeException("Cannot be reserved because the book is not on loan.");
        }

        Reservation reservation = new Reservation(bookId, userId, LocalDate.now());
        boolean result = reservationRepository.saveReservation(reservation);

        bookRepository.closeConnection();
        userRepository.closeConnection();
        reservationRepository.closeConnection();

        return result;
    }

}
