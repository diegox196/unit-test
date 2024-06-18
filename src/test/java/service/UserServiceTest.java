package service;

import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.UserRepository;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void registerUser_Success() {
        // Arrange
        String name = "Juan Perez";
        String email = "juan.perez@example.com";
        when(userRepository.findByEmail(email)).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(true);

        // Act
        boolean result = userService.registerUser(name, email);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_InvalidEmail() {
        // Arrange
        String name = "Juan Perez";
        String email = "juan.perez";

        // Act
        boolean result = userService.registerUser(name, email);

        // Assert
        assertFalse(result);
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_EmptyName() {
        // Arrange
        String name = "";
        String email = "juan.perez@example.com";

        // Act
        boolean result = userService.registerUser(name, email);

        // Assert
        assertFalse(result);
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_DuplicateEmail() {
        // Arrange
        String name = "Juan Perez";
        String email = "juan.perez@example.com";
        when(userRepository.findByEmail(email)).thenReturn(new User(name, email));

        // Act
        boolean result = userService.registerUser(name, email);

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_InvalidName() {
        // Arrange
        String name = "Juan#Perez";
        String email = "juan.perez@example.com";

        // Act
        boolean result = userService.registerUser(name, email);

        // Assert
        assertFalse(result);
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}
