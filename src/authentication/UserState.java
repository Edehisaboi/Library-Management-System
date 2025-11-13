package authentication;

import domain.user.User;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public final class UserState implements UserSession {
    private final AtomicReference<User> currentUser = new AtomicReference<>();

    @Override
    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser.get());
    }

    @Override
    public void login(User user) {
        currentUser.set(user);
    }

    @Override
    public void logout() {
        currentUser.set(null);
    }

    @Override
    public boolean isLoggedIn() {
        return currentUser.get() != null;
    }

    @Override
    public User requireLoggedIn() {
        User u = currentUser.get();
        if (u == null)
            throw new IllegalStateException("No user is logged in.");
        return u;
    }
}
