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
     * @param book The Book object to be saved.
     * @return true if the book was saved successfully, false otherwise.
     */
    public boolean saveBook(Book book) {

        if (book.getTitle() == null || book.getTitle().isEmpty()) {
            return false;
        }

        if (book.getAuthor() == null || book.getAuthor().isEmpty()) {
            return false;
        }

        if (book.getIsbn() == null || book.getIsbn().isEmpty()) {
            return false;
        }

        if (bookRepository.findByIsbn(book.getIsbn())) {
            return false;
        }

        boolean result = bookRepository.saveBook(book);
        bookRepository.closeConnection();

        return result;
    }
}
