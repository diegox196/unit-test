package service;

import entity.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.BookRepository;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookService(bookRepository);
    }

    @Test
    void testSaveBook_Success() {
        // Arrange
        String title = "Cien Años de Soledad";
        String author = "Gabriel García Márquez";
        String isbn = "1234567890";
        when(bookRepository.findByIsbn(isbn)).thenReturn(false);
        when(bookRepository.saveBook(any(Book.class))).thenReturn(true);

        // Act
        boolean result = bookService.saveBook(title, author, isbn);

        // Assert
        Assertions.assertTrue(result);
        verify(bookRepository, times(1)).saveBook(any(Book.class));
    }

    @Test
    void testSaveBook_EmptyTitleFailure() {
        // Arrange
        String title = "";
        String author = "Gabriel García Márquez";
        String isbn = "1234567890";
        when(bookRepository.saveBook(any(Book.class))).thenReturn(true);

        // Act
        boolean result = bookService.saveBook(title, author, isbn);

        // Assert
        Assertions.assertTrue(result);
        verify(bookRepository, times(1)).saveBook(any(Book.class));
    }

    @Test
    void testSaveBook_EmptyAuthorFailure() {
        // Arrange
        String title = "Cien Años de Soledad";
        String author = "";
        String isbn = "1234567890";
        when(bookRepository.saveBook(any(Book.class))).thenReturn(true);

        // Act
        boolean result = bookService.saveBook(title, author, isbn);

        // Assert
        Assertions.assertTrue(result);
        verify(bookRepository, times(1)).saveBook(any(Book.class));
    }

    @Test
    void testSaveBook_EmptyISBNFailure() {
        // Arrange
        String title = "Cien Años de Soledad";
        String author = "Gabriel García Márquez";
        String isbn = "";
        when(bookRepository.saveBook(any(Book.class))).thenReturn(true);

        // Act
        boolean result = bookService.saveBook(title, author, isbn);

        // Assert
        Assertions.assertTrue(result);
        verify(bookRepository, times(1)).saveBook(any(Book.class));
    }

    @Test
    void testSaveBook_DuplicateISBNFailure() {
        // Arrange
        String title = "Cien Años de Soledad";
        String author = "Gabriel García Márquez";
        String isbn = "1234567890";

        when(bookRepository.saveBook(any(Book.class))).thenReturn(true);
        when(bookRepository.findByIsbn(isbn)).thenReturn(true);

        // Act
        boolean result = bookService.saveBook(title, author, isbn);

        // Assert
        Assertions.assertTrue(result);
        verify(bookRepository, times(1)).saveBook(any(Book.class));
    }
}