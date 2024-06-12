package service;

import entity.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

    private String title, author, isbn;
    private Book book1, book2;

    @BeforeEach
    void setUp() {
        bookService = new BookService(bookRepository);
        title = "Cien Años de Soledad";
        author = "Gabriel García Márquez";
        isbn = "1234567890";
        book1 = new Book("Cien Años de Soledad", "Gabriel García Márquez", "1234567890", true);
        book2 = new Book("En agosto nos vemos", "Gabriel García Márquez", "9780618346252", true);
    }

    /**
     * Unit tests for the saveBook method of the BookService class.
     */
    @Nested
    class SaveBookTest {

        /**
         * Tests the successful case of saving a new book.
         */
        @Test
        void testSaveBook_Success() {
            // Arrange
            when(bookRepository.findRepeatedIsbn(isbn)).thenReturn(false);
            when(bookRepository.saveBook(any(Book.class))).thenReturn(true);

            // Act
            boolean result = bookService.saveBook(title, author, isbn);

            // Assert
            Assertions.assertTrue(result);
            verify(bookRepository, times(1)).findRepeatedIsbn(isbn);
            verify(bookRepository, times(1)).saveBook(any(Book.class));
        }

        /**
         * Tests the case when the book title is empty while trying to save a book.
         */
        @Test
        void testSaveBook_EmptyTitleFailure() {
            // Arrange
            String emptyTitle = "";
            String expectedMessage = "Title cannot be null or empty";

            // Act
            RuntimeException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                    bookService.saveBook(emptyTitle, author, isbn)
            );

            // Assert
            Assertions.assertEquals(expectedMessage, exception.getMessage());
            verify(bookRepository, never()).saveBook(any(Book.class));
        }

        /**
         * Tests the case when the book author is empty while trying to save a book.
         */
        @Test
        void testSaveBook_EmptyAuthorFailure() {
            // Arrange
            String emptyAuthor = "";
            String expectedMessage = "Author cannot be null or empty";

            // Act
            RuntimeException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                    bookService.saveBook(title, emptyAuthor, isbn)
            );

            // Assert
            Assertions.assertEquals(expectedMessage, exception.getMessage());
            verify(bookRepository, never()).saveBook(any(Book.class));
        }

        /**
         * Tests the case when the book ISBN is empty while trying to save a book.
         */
        @Test
        void testSaveBook_EmptyISBNFailure() {
            // Arrange
            String emptyIsbn = "";
            String expectedMessage = "ISBN cannot be null or empty";

            // Act
            RuntimeException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                    bookService.saveBook(title, author, emptyIsbn)
            );

            // Assert
            Assertions.assertEquals(expectedMessage, exception.getMessage());
            verify(bookRepository, never()).saveBook(any(Book.class));
        }

        /**
         * Tests the case when the book ISBN is already in use while trying to save a book.
         */
        @Test
        void testSaveBook_DuplicateISBNFailure() {
            // Arrange
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
    }

    /**
     * Unit tests for the saveBook method of the BookService class.
     */
    @Nested
    class SearchBookTest {

        /**
         * Tests the successful case of searching for books by title.
         */
        @Test
        void testSearchBooksByTitle_Success() {
            // Arrange
            List<Book> expectedBooks = Arrays.asList(book1, book2);
            when(bookRepository.findByTitle(title)).thenReturn(expectedBooks);

            // Act
            List<Book> result = bookService.searchBooksByTitle(title);

            // Assert
            Assertions.assertEquals(expectedBooks, result);
        }

        /**
         * Tests the successful case of searching for books by author.
         */
        @Test
        void testSearchBooksByAuthor_Success() {
            // Arrange
            List<Book> expectedBooks = Arrays.asList(book1, book2);
            when(bookRepository.findByAuthor(author)).thenReturn(expectedBooks);

            // Act
            List<Book> result = bookService.searchBooksByAuthor(author);

            // Assert
            Assertions.assertEquals(expectedBooks, result);
        }

        /**
         * Tests the successful case of searching for a book by ISBN.
         */
        @Test
        void testSearchBookByIsbn_Success() {
            // Arrange
            when(bookRepository.findByIsbn(isbn)).thenReturn(book1);

            // Act
            Book result = bookService.searchBookByIsbn(isbn);

            // Assert
            Assertions.assertEquals(book1, result);
        }

        /**
         * Tests the case when searching for books with an empty title.
         */
        @Test
        void testSearchBooksByTitle_EmptyTitle() {
            // Arrange
            String emptyTitle = "";
            when(bookRepository.findByTitle(emptyTitle)).thenReturn(Collections.emptyList());

            // Act
            List<Book> result = bookService.searchBooksByTitle(emptyTitle);

            // Assert
            Assertions.assertTrue(result.isEmpty());
        }

        /**
         * Tests the case when searching for books with an empty author.
         */
        @Test
        void testSearchBooksByAuthor_EmptyAuthor() {
            // Arrange
            String emptyAuthor = "";
            when(bookRepository.findByAuthor(emptyAuthor)).thenReturn(Collections.emptyList());

            // Act
            List<Book> result = bookService.searchBooksByAuthor(emptyAuthor);

            // Assert
            Assertions.assertTrue(result.isEmpty());
        }

        /**
         * Tests the case when searching for a book with an invalid ISBN.
         */
        @Test
        void testSearchBookByIsbn_InvalidIsbn() {
            // Arrange
            String invalidIsbn = "invalid-isbn";
            when(bookRepository.findByIsbn(invalidIsbn)).thenReturn(null);

            // Act
            Book result = bookService.searchBookByIsbn(invalidIsbn);

            // Assert
            Assertions.assertNull(result);
        }
    }
}