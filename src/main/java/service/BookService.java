package service;

import entity.Book;
import repository.BookRepository;

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
            return false;
        }

        if (author == null || author.isEmpty()) {
            return false;
        }

        if (isbn == null || isbn.isEmpty()) {
            return false;
        }

        if (bookRepository.findByIsbn(isbn)) {
            return false;
        }

        Book book = new Book(title, author, isbn, true);
        boolean result = bookRepository.saveBook(book);
        bookRepository.closeConnection();

        return result;
    }
}
