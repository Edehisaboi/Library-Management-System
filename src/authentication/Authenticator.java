package authentication;

import domain.user.User;

public interface Authenticator {
    User login(String email, String password) throws AuthException;

    User register(String firstName, String lastName, String email, String password) throws AuthException;

    void logout(User user);
}
