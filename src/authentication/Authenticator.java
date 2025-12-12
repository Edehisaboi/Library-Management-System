package authentication;

import domain.user.User;

/**
 * Interface for handling user authentication (login/register).
 */
public interface Authenticator {
    /**
     * Attempts to log in a user with the given credentials.
     *
     * @param email    user email
     * @param password user password
     * @return the authenticated User object
     * @throws AuthException if login fails (invalid credentials or access denied)
     */
    User login(String email, String password) throws AuthException;

    /**
     * Registers a new user account.
     *
     * @param firstName first name
     * @param lastName  last name
     * @param email     email address
     * @param password  password
     * @return the newly created User object
     * @throws AuthException if registration fails (e.g. email already exists)
     */
    User register(String firstName, String lastName, String email, String password) throws AuthException;

    /**
     * Logs out the specified user.
     * 
     * @param user the user to log out
     */
    void logout(User user);
}
