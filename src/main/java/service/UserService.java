package service;

import entity.User;
import repository.UserRepository;

/**
 * This class provides services for managing user operations.
 */
public class UserService {
    private final UserRepository userRepository;

    /**
     * Constructs a new UserService instance.
     *
     * @param userRepository The UserRepository instance to be used for user operations.
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Registers a new user in the system.
     *
     * @param name  The name of the user.
     * @param email The email of the user.
     * @return true if the user was registered successfully, false otherwise.
     */
    public boolean registerUser(String name, String email) {
        if (name == null || name.isEmpty() || !name.matches("[a-zA-Z\\s]+")) {
            return false;
        }

        if (email == null || email.isEmpty() || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            return false;
        }

        User existingUser = userRepository.findByEmail(email);
        if (existingUser != null) {
            return false;
        }

        User newUser = new User(name, email);
        return userRepository.save(newUser);
    }
}
