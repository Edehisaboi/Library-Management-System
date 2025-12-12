package repo;

import domain.user.User;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

/**
 * Interface for managing user accounts (Members and Librarians).
 */
public interface UserRepository {
    /**
     * Saves or updates a user.
     * 
     * @param user the user to save
     * @return the saved user
     */
    User save(User user);

    /**
     * Deletes a user from the repository.
     * 
     * @param user the user to delete
     */
    void delete(User user);

    /**
     * Checks if a user exists with the given email.
     * 
     * @param email the email to check
     * @return true if exists
     */
    boolean existsByEmail(String email);

    /**
     * Checks credentials and returns the user if valid.
     * 
     * @param email    login email
     * @param password login password
     * @return an Optional containing the User if credentials match
     */
    Optional<User> existsByEmailAndPassword(String email, String password);

    /**
     * Finds a user by their unique ID.
     * 
     * @param id the user UUID
     * @return an Optional containing the user if found
     */
    Optional<User> findById(UUID id);

    /**
     * Retrieves all registered users.
     * 
     * @return list of all users
     */
    List<User> findAll();
}
