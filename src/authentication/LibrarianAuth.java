package authentication;

import java.util.Objects;
import java.util.Optional;
import javax.naming.AuthenticationException;

import domain.user.User;
import domain.user.Librarian;
import repo.UserRepository;

public class LibrarianAuth implements Authenticator {
    private final UserRepository repo;
    private final UserSession state;

    public LibrarianAuth(UserRepository repo, UserSession state) {
        this.repo = Objects.requireNonNull(repo, "UserRepository cannot be null");
        this.state = Objects.requireNonNull(state, "UserState cannot be null");
    }

    @Override
    public User login(String email, String password) throws AuthenticationException {
        Optional<User> user = repo.existsByEmailAndPassword(email, password);
        if (user.isEmpty()) {
            throw new AuthenticationException("Invalid email or password.");
        }
        User librarian = user.get();

        if (!(librarian instanceof Librarian)) {
            throw new AuthenticationException("Access denied. Librarian account required.");
        }
        state.login(librarian);
        return librarian;
    }

    @Override
    public User register(String firstName, String lastName, String email, String password)
            throws AuthenticationException {
        if (repo.existsByEmail(email)) {
            throw new AuthenticationException("An account with this email already exists.");
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
