package service;

import entity.Book;
import repository.BookRepository;

import java.util.List;

/**
 * This class provides services for managing book operations.
 * It acts as an intermediary between the application and the book repository,
 * handling business logic and validations related to book data.
 */
public class BookService {
    private final BookRepository bookRepository;

    /**
     * Constructs a new BookService instance.
     *
     * @param bookRepository The BookRepository instance to be used for book operations.
     */
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Saves a book to the database.
     *
     * @param title  The title of the book.
     * @param author The author of the book.
     * @param isbn   The ISBN of the book.
     * @return true if the book was saved successfully, false otherwise.
     */
    public boolean saveBook(String title, String author, String isbn) {

        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }

        if (author == null || author.isEmpty()) {
            throw new IllegalArgumentException("Author cannot be null or empty");
        }

        if (isbn == null || isbn.isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be null or empty");
        }

        if (bookRepository.findRepeatedIsbn(isbn)) {
            throw new RuntimeException("ISBN already in use");
        }

        Book book = new Book(title, author, isbn, true);
        boolean result = bookRepository.saveBook(book);
        bookRepository.closeConnection();

        return result;
    }

    /**
     * Searches for books by title.
     *
     * @param title The title of the book.
     * @return List of books matching the title.
     */
    public List<Book> searchBooksByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    /**
     * Searches for books by author.
     *
     * @param author The author of the book.
     * @return List of books matching the author.
     */
    public List<Book> searchBooksByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }

    /**
     * Searches for a book by ISBN.
     *
     * @param isbn The ISBN of the book.
     * @return The book with the matching ISBN.
     */
    public Book searchBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    public Boolean checkBookAvailability(int bookId) {
        if (bookId <= 0) {
            throw new IllegalArgumentException("Book ID must be greater than zero");
        }

        Boolean isAvailable = bookRepository.isBookAvailable(bookId);

        if (isAvailable == null) {
            throw new RuntimeException("Book does not exist or database error occurred");
        }

        return isAvailable;
    }

}



