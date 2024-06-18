package entity;

import java.time.LocalDate;

public class Reservation {
    private int bookId, userId;
    private LocalDate reservationDate;

    public Reservation(int bookId, int userId, LocalDate reservationDate) {
        this.bookId = bookId;
        this.userId = userId;
        this.reservationDate = reservationDate;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }
}
