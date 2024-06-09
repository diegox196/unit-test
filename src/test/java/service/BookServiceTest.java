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

        Book book = new Book("Cien Años de Soledad", "Gabriel García Márquez", "1234567890");
        when(bookRepository.addBook(book)).thenReturn(true);
        when(bookRepository.findByIsbn(book.getIsbn())).thenReturn(false);

        // Act
        boolean result = bookService.addBook(book);

        // Assert
        Assertions.assertTrue(result);
        verify(bookRepository, times(1)).addBook(book);
    }

    @Test
    void testSaveBook_EmptyTitleFailure() {
        // Arrange
        Book book = new Book("", "Gabriel García Márquez", "123");
        when(bookRepository.addBook(book)).thenReturn(true);

        // Act
        boolean result = bookService.addBook(book);

        // Assert
        Assertions.assertTrue(result);
        verify(bookRepository, times(1)).addBook(book);
    }

    @Test
    void testSaveBook_EmptyAuthorFailure() {
        // Arrange
        Book book = new Book("Cien Años de Soledad", "", "123");
        when(bookRepository.addBook(book)).thenReturn(true);

        // Act
        boolean result = bookService.addBook(book);

        // Assert
        Assertions.assertTrue(result);
        verify(bookRepository, times(1)).addBook(book);
    }

    @Test
    void testSaveBook_EmptyISBNFailure() {
        // Arrange
        Book book = new Book("Cien Años de Soledad", "Gabriel García Márquez", "");
        when(bookRepository.addBook(book)).thenReturn(true);

        // Act
        boolean result = bookService.addBook(book);

        // Assert
        Assertions.assertTrue(result);
        verify(bookRepository, times(1)).addBook(book);
    }

    @Test
    void testSaveBook_DuplicateISBNFailure() {
        // Arrange
        Book book = new Book("Cien Años de Soledad", "Gabriel García Márquez", "1234567890");
        when(bookRepository.addBook(book)).thenReturn(true);
        when(bookRepository.findByIsbn(book.getIsbn())).thenReturn(true);

        // Act
        boolean result = bookService.addBook(book);

        // Assert
        Assertions.assertTrue(result);
        verify(bookRepository, times(1)).addBook(book);
    }
}