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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        when(bookRepository.findRepeatedIsbn(isbn)).thenReturn(false);
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
        String expectedMessage = "Title cannot be null or empty";

        // Act
        RuntimeException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                bookService.saveBook(title, author, isbn)
        );

        // Assert
        Assertions.assertEquals(expectedMessage, exception.getMessage());
        verify(bookRepository, never()).saveBook(any(Book.class));
    }

    @Test
    void testSaveBook_EmptyAuthorFailure() {
        // Arrange
        String title = "Cien Años de Soledad";
        String author = "";
        String isbn = "1234567890";
        String expectedMessage = "Author cannot be null or empty";

        // Act
        RuntimeException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                bookService.saveBook(title, author, isbn)
        );

        // Assert
        Assertions.assertEquals(expectedMessage, exception.getMessage());
        verify(bookRepository, never()).saveBook(any(Book.class));
    }

    @Test
    void testSaveBook_EmptyISBNFailure() {
        // Arrange
        String title = "Cien Años de Soledad";
        String author = "Gabriel García Márquez";
        String isbn = "";
        String expectedMessage = "ISBN cannot be null or empty";

        // Act
        RuntimeException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                bookService.saveBook(title, author, isbn)
        );

        // Assert
        Assertions.assertEquals(expectedMessage, exception.getMessage());
        verify(bookRepository, never()).saveBook(any(Book.class));
    }

    @Test
    void testSaveBook_DuplicateISBNFailure() {
        // Arrange
        String title = "Cien Años de Soledad";
        String author = "Gabriel García Márquez";
        String isbn = "1234567890";
        String expectedMessage = "ISBN already in use";

        when(bookRepository.findRepeatedIsbn(isbn)).thenReturn(true);

        // Act
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () ->
                bookService.saveBook(title, author, isbn)
        );

        // Assert
        Assertions.assertEquals(expectedMessage, exception.getMessage());
        verify(bookRepository, times(1)).findRepeatedIsbn(isbn);
        verify(bookRepository, never()).saveBook(any(Book.class));
    }

    @Test
    void testSearchBooksByTitle_Success() {
        // Arrange
        String title = "Cien Años de Soledad";
        Book book1 = new Book("Cien Años de Soledad", "Gabriel García Márquez", "1234567890", true);
        Book book2 = new Book("Cien Años de Soledad", "J.R.R. Tolkien", "9780618346252", true);
        List<Book> expectedBooks = Arrays.asList(book1, book2);
        when(bookRepository.findByTitle(title)).thenReturn(expectedBooks);

        // Act
        List<Book> result = bookService.searchBooksByTitle(title);

        // Assert
        Assertions.assertEquals(expectedBooks, result);
    }

    @Test
    void testSearchBooksByAuthor_Success() {
        // Arrange
        String author = "Gabriel García Márquez";
        Book book1 = new Book("Cien Años de Soledad", "Gabriel García Márquez", "1234567890", true);
        Book book2 = new Book("En agosto nos vemos", "Gabriel García Márquez", "9780618346252", true);
        List<Book> expectedBooks = Arrays.asList(book1, book2);
        when(bookRepository.findByAuthor(author)).thenReturn(expectedBooks);

        // Act
        List<Book> result = bookService.searchBooksByAuthor(author);

        // Assert
        Assertions.assertEquals(expectedBooks, result);
    }

    @Test
    void testSearchBookByIsbn_Success() {
        // Arrange
        String isbn = "1234567890";
        Book book = new Book("Cien Años de Soledad", "Gabriel García Márquez", "1234567890", true);
        when(bookRepository.findByIsbn(isbn)).thenReturn(book);

        // Act
        Book result = bookService.searchBookByIsbn(isbn);

        // Assert
        Assertions.assertEquals(book, result);
    }

    @Test
    void testSearchBooksByTitle_EmptyTitle() {
        // Arrange
        String title = "";
        when(bookRepository.findByTitle(title)).thenReturn(Collections.emptyList());

        // Act
        List<Book> result = bookService.searchBooksByTitle(title);

        // Assert
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testSearchBooksByAuthor_EmptyAuthor() {
        // Arrange
        String author = "";
        when(bookRepository.findByAuthor(author)).thenReturn(Collections.emptyList());

        // Act
        List<Book> result = bookService.searchBooksByAuthor(author);

        // Assert
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testSearchBookByIsbn_InvalidIsbn() {
        // Arrange
        String isbn = "invalid-isbn";
        when(bookRepository.findByIsbn(isbn)).thenReturn(null);

        // Act
        Book result = bookService.searchBookByIsbn(isbn);

        // Assert
        Assertions.assertNull(result);
    }
}