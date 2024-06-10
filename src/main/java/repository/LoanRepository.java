package repository;

import entity.Loan;

import java.sql.*;

public class LoanRepository {

    private Connection connection;

    /**
     * Constructs a new LoanRepository and establishes a connection to the database.
     */
    public LoanRepository() {
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
     * Saves a loan to the database.
     *
     * @param loan The loan object to be saved.
     * @return true if the loan was saved successfully, false otherwise.
     */
    public boolean saveLoan(Loan loan) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO loans (book_id, user_id, loan_date, due_date) VALUES (?, ?, ?, ?)")) {
            statement.setLong(1, loan.getBookID());
            statement.setLong(2, loan.getUserID());
            statement.setDate(3, Date.valueOf(loan.getLoanDate()));
            statement.setDate(4, Date.valueOf(loan.getReturnDate()));
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
