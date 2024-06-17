package repository;

import entity.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO books (title, author, isbn, available) VALUES (?, ?, ?, ?)")) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getIsbn());
            statement.setBoolean(4, book.isAvailable());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates a book in the database.
     *
     * @param bookId The book id.
     * @param book The book object with updated information.
     * @return true if the book was updated successfully, false otherwise.
     */
    public boolean updateBook(int bookId, Book book) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE books SET title = ?, author = ?, isbn = ?, available = ? WHERE id = ?")) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getIsbn());
            statement.setBoolean(4, book.isAvailable());
            statement.setInt(5, bookId); // Update based on existing book ID
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0; // Check if at least one row was updated
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Finds a book by id.
     *
     * @param id The id of the book to search for.
     * @return The book with the matching ISBN, or null if not found.
     */
    public Book findById(int id) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM books WHERE id = ?")) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Book(
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("isbn"),
                        resultSet.getBoolean("available")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks if a book with the given ISBN exists in the database.
     *
     * @param isbn The ISBN of the book to search for.
     * @return true if a book with the given ISBN exists, false otherwise.
     */
    public boolean findRepeatedIsbn(String isbn) {
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
     * Finds books by title.
     *
     * @param title The title of the book to search for.
     * @return List of books matching the title.
     */
    public List<Book> findByTitle(String title) {
        List<Book> books = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM books WHERE title ILIKE ?")) {
            statement.setString(1, "%" + title + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                books.add(new Book(
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("isbn"),
                        resultSet.getBoolean("available")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    /**
     * Finds books by author.
     *
     * @param author The author of the book to search for.
     * @return List of books matching the author.
     */
    public List<Book> findByAuthor(String author) {
        List<Book> books = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM books WHERE author ILIKE ?")) {
            statement.setString(1, "%" + author + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                books.add(new Book(
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("isbn"),
                        resultSet.getBoolean("available")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    /**
     * Finds a book by ISBN.
     *
     * @param isbn The ISBN of the book to search for.
     * @return The book with the matching ISBN, or null if not found.
     */
    public Book findByIsbn(String isbn) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM books WHERE isbn = ?")) {
            statement.setString(1, isbn);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Book(
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("isbn"),
                        resultSet.getBoolean("available")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Checks if a book is available by its ID.
     *
     * @param bookId The ID of the book to check.
     * @return true if the book is available, false if it's checked out or does not exist.
     */
    public Boolean isBookAvailable(int bookId) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT available FROM books WHERE id = ?")) {
            statement.setInt(1, bookId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("available");
            } else {
                return null; // Book does not exist
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // Database error
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


