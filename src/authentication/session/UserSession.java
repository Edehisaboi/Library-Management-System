package authentication.session;

import domain.user.User;
import java.util.Optional;

/**
 * Interface for managing the current user's session state.
 */
public interface UserSession {
    /**
     * Gets the currently logged-in user, if any.
     * 
     * @return Optional containing the User
     */
    Optional<User> getCurrentUser();

    /**
     * Sets the current user (logs them in).
     * 
     * @param user the user to log in
     */
    void login(User user);

    /**
     * Clears the current user (logs them out).
     */
    void logout();

    /**
     * Checks if a user is currently logged in.
     * 
     * @return true if logged in
     */
    boolean isLoggedIn();

    /**
     * Gets the logged-in user, throwing an exception if no one is logged in.
     * 
     * @return the current user
     * @throws IllegalStateException if no user is logged in
     */
    User requireLoggedIn();
}
