package repository;

import entity.Book;

import java.sql.*;
import java.util.Optional;

public class BookRepository {

    private Connection conecction;

    public BookRepository() throws SQLException {
        // Conectar a la base de datos PostgreSQL
        String url = "jdbc:postgresql://localhost:5432/library";
        String user = "admin";
        String password = "admin";
        conecction = DriverManager.getConnection(url, user, password);
    }

    public boolean addBook(Book book) {

        // Verificar que el ISBN no exista ya en la base de datos
        try {
            // Crear el libro y añadirlo a la base de datos
            String insercion = "INSERT INTO books (title, author, isbn) VALUES (?, ?, ?)";
            PreparedStatement statementInsercion = conecction.prepareStatement(insercion);
            statementInsercion.setString(1, book.getTitle());
            statementInsercion.setString(2, book.getAuthor());
            statementInsercion.setString(3, book.getIsbn());
            statementInsercion.executeUpdate();
            closeConnection();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean findByIsbn(String isbn) {
        try {
            String consultaISBN = "SELECT COUNT(*) FROM books WHERE isbn = ?";
            PreparedStatement statementISBN = conecction.prepareStatement(consultaISBN);
            statementISBN.setString(1, isbn);
            ResultSet resultSet = statementISBN.executeQuery();
            resultSet.next();
            if (resultSet.getInt(1) > 0) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para cerrar la conexión a la base de datos
    public void closeConnection() {
        try {
            if (conecction != null && !conecction.isClosed()) {
                conecction.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
