package authentication;

import users.Base;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class UserSession {
    private final AtomicReference<Base> currentUser = new AtomicReference<>();

    public Optional<Base> getCurrentUser() {
        return Optional.ofNullable(currentUser.get());
    }

    public void setUser(Base user) {
        currentUser.set(user);
    }

    public void clear() {
        currentUser.set(null);
    }

    public boolean isAuthenticated() {
        return currentUser.get() != null;
    }
}
