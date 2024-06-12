package repository;

import entity.Book;
import entity.Loan;
import entity.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO loans (book_id, user_id, loan_date, expected_return_date, actual_return_date) VALUES (?, ?, ?, ?)"
        )) {
            statement.setLong(1, loan.getBookID());
            statement.setLong(2, loan.getUserID());
            statement.setDate(3, Date.valueOf(loan.getLoanDate()));
            statement.setDate(4, Date.valueOf(loan.getExpectedReturnDate()));
            statement.setDate(5, Date.valueOf(loan.getActualReturnDate()));
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Finds loans by user ID.
     *
     * @param loanId The ID of the loan to search for.
     * @return List of loans associated with the user.
     */
    public Loan findById(int loanId) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM loans WHERE loan_id = ?")) {
            statement.setLong(1, loanId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                new Loan(
                        // Assuming the existence of appropriate constructors and methods in Loan and User classes
                        resultSet.getInt("book_id"),
                        resultSet.getInt("user_id"),
                        resultSet.getDate("loan_date").toLocalDate(),
                        resultSet.getDate("due_date").toLocalDate()
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates the returned date of a loan.
     *
     * @param loanId The ID of the loan to update.
     * @param actualReturnedDate The actual returned date.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateReturnedDate(int loanId, LocalDate actualReturnedDate) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE loans SET actual_return_date = ? WHERE id = ?")) {
            statement.setDate(1, Date.valueOf(actualReturnedDate));
            statement.setLong(2, loanId);
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
