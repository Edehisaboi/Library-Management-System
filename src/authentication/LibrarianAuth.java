package authentication;

import java.time.LocalDate;
import java.util.Objects;
import javax.naming.AuthenticationException;

import users.Base;
import users.Librarian;


public class LibrarianAuth implements Authenticator {
    private final UserRepository repo;

    public LibrarianAuth(UserRepository repo) {
        this.repo = Objects.requireNonNull(repo, "UserRepository cannot be null");
    }

    @Override
    public Base login(String email, String password) throws AuthenticationException {
        Base user = repo.existsByEmailAndPassword(email, password);
        if (user == null) {
            throw new AuthenticationException("Invalid email or password!");
        }
        return user;
    }

    @Override
    public Base register(String firstName, String lastName, String email, String password)
            throws AuthenticationException {
        if (repo.existsByEmail(email)) {
            throw new AuthenticationException("An account with this email already exists!");
        }

        Librarian newUser = new Librarian(firstName, lastName, email, password, LocalDate.now());
        return repo.save(newUser);
    }

    @Override
    public void logout(Base user) {
        // todo: control state
        Objects.requireNonNull(user, "User cannot be null");
    }
}
