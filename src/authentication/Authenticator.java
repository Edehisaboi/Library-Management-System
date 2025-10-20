package authentication;

import users.Base;
import javax.naming.AuthenticationException;

public interface Authenticator {
    Base login(String email, String password) throws AuthenticationException;
    Base register(String firstName, String lastName, String email, String password) throws AuthenticationException;
    void logout(Base user);
}
