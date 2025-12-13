package authentication;

import authentication.session.UserSession;
import domain.user.*;
import repo.UserRepository;

import java.util.Objects;
import java.util.Optional;

/**
 * A generic authenticator that can handle login and registration for any User
 * type.
 * Uses a factory approach to create specific user instances.
 *
 * @param <T> the type of User this authenticator handles (Member, Librarian)
 */
public class AuthFactory<T extends User> implements Authenticator {
    private final UserRepository repo;
    private final UserSession state;
    private final Class<T> userType;
    private final UserFactory<T> factory;

    /**
     * Functional interface for creating a user instance.
     */
    @FunctionalInterface
    public interface UserFactory<T> {
        T create(String firstName, String lastName, String email, String password);
    }

    /**
     * Creates a new AuthFactory for a specific user type.
     *
     * @param repo     user repository
     * @param state    user session state
     * @param userType the class object of the user type
     * @param factory  method to create a new instance of T
     */
    public AuthFactory(UserRepository repo, UserSession state, Class<T> userType, UserFactory<T> factory) {
        this.repo = Objects.requireNonNull(repo, "UserRepository cannot be null");
        this.state = Objects.requireNonNull(state, "UserState cannot be null");
        this.userType = Objects.requireNonNull(userType, "UserType cannot be null");
        this.factory = Objects.requireNonNull(factory, "Factory cannot be null");
    }

    @Override
    public User login(String email, String password) throws AuthException {
        Optional<User> userOpt = repo.existsByEmailAndPassword(email, password);
        if (userOpt.isEmpty()) {
            throw new AuthException("Invalid email or password.");
        }
        User user = userOpt.get();

        if (!userType.isInstance(user)) {
            throw new AuthException("Access denied! Not a " + userType.getSimpleName());
        }

        state.login(user);
        return user;
    }

    @Override
    public User register(String firstName, String lastName, String email, String password) throws AuthException {
        if (repo.existsByEmail(email)) {
            throw new AuthException("An account with this email already exists.");
        }
        T newUser = factory.create(firstName, lastName, email, password);
        return repo.save(newUser);
    }

    @Override
    public void logout(User user) {
        Objects.requireNonNull(user, "User cannot be null");
        state.logout();
    }

    // Static factory methods for convenience
    public static AuthFactory<Member> createMemberAuth(UserRepository repo, UserSession state) {
        return new AuthFactory<>(repo, state, Member.class, Member::new);
    }

    public static AuthFactory<Librarian> createLibrarianAuth(UserRepository repo, UserSession state) {
        return new AuthFactory<>(repo, state, Librarian.class, Librarian::new);
    }
}
