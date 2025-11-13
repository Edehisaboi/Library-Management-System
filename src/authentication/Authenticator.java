package authentication;

import domain.user.User;
import javax.naming.AuthenticationException;

public interface Authenticator {
    User login(String email, String password) throws AuthenticationException;

    User register(String firstName, String lastName, String email, String password) throws AuthenticationException;

    void logout(User user);
}
