package service;

import entity.Book;
import repository.BookRepository;

public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public boolean addBook(Book book) {

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

        return bookRepository.addBook(book);
    }
}
