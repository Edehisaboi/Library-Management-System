package authentication;

import java.util.Objects;
import java.util.Optional;

import authentication.session.UserSession;
import domain.user.User;
import domain.user.Librarian;
import repo.UserRepository;

/**
 * Authenticator implementation for Librarian users.
 */
public class LibrarianAuth implements Authenticator {
    private final UserRepository repo;
    private final UserSession state;

    /**
     * Creates a new LibrarianAuth.
     * 
     * @param repo  user repository
     * @param state user session state
     */
    public LibrarianAuth(UserRepository repo, UserSession state) {
        this.repo = Objects.requireNonNull(repo, "UserRepository cannot be null");
        this.state = Objects.requireNonNull(state, "UserState cannot be null");
    }

    @Override
    public User login(String email, String password) throws AuthException {
        Optional<User> user = repo.existsByEmailAndPassword(email, password);
        if (user.isEmpty()) {
            throw new AuthException("Invalid email or password.");
        }
        User librarian = user.get();

        if (!(librarian instanceof Librarian)) {
            throw new AuthException("Access denied!");
        }
        state.login(librarian);
        return librarian;
    }

    @Override
    public User register(String firstName, String lastName, String email, String password)
            throws AuthException {
        if (repo.existsByEmail(email)) {
            throw new AuthException("An account with this email already exists.");
        }
        Librarian newLibrarian = new Librarian(firstName, lastName, email, password);
        return repo.save(newLibrarian);
    }

    @Override
    public void logout(User librarian) {
        Objects.requireNonNull(librarian, "Librarian cannot be null");
        state.logout();
    }
}
