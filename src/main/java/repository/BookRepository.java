package repository;

import entity.Book;
import java.sql.*;

/**
 * This class represents a repository for managing book data in a database.
 */
public class BookRepository {

    private Connection connection;

    /**
     * Constructs a new BookRepository and establishes a connection to the database.
     */
    public BookRepository() {
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
     * Saves a book to the database.
     *
     * @param book The book object to be saved.
     * @return true if the book was saved successfully, false otherwise.
     */
    public boolean saveBook(Book book) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO books (title, author, isbn) VALUES (?, ?, ?)")) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getIsbn());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if a book with the given ISBN exists in the database.
     *
     * @param isbn The ISBN of the book to search for.
     * @return true if a book with the given ISBN exists, false otherwise.
     */
    public boolean findByIsbn(String isbn) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM books WHERE isbn = ?")) {
            statement.setString(1, isbn);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;
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
