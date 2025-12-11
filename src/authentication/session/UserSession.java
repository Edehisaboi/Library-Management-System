package authentication.session;

import domain.user.User;
import java.util.Optional;

public interface UserSession {
    Optional<User> getCurrentUser();

    void login(User user);

    void logout();

    boolean isLoggedIn();

    User requireLoggedIn();
}
