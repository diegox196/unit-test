package repository;

import entity.Reservation;

import java.sql.*;

/**
 * This class represents a repository for managing reservation data in a database.
 */
public class ReservationRepository {

    private Connection connection;

    /**
     * Constructs a new ReservationRepository and establishes a connection to the database.
     */
    public ReservationRepository() {
        try {
            String url = "jdbc:postgresql://localhost:5432/library";
            String user = "postgres";
            String password = "admin";
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves a reservation to the database.
     *
     * @param reservation The reservation object to be saved.
     * @return true if the reservation was saved successfully, false otherwise.
     */
    public boolean saveReservation(Reservation reservation) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO reservations (book_id, user_id, reservation_date) VALUES (?, ?, ?)")) {
            statement.setLong(1, reservation.getBookId());
            statement.setLong(2, reservation.getUserId());
            statement.setDate(3, Date.valueOf(reservation.getReservationDate()));
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Closes the connection to the database.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
