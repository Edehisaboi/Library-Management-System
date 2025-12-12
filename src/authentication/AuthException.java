package authentication;

/**
 * Custom exception for authentication failures.
 * Used when login or registration fails due to business rules (e.g. wrong
 * password, duplicate email).
 */
public class AuthException extends Exception {
    /**
     * Creates a new AuthException with a message.
     * 
     * @param message failure reason
     */
    public AuthException(String message) {
        super(message);
    }
}
