package authentication;

import java.time.LocalDate;
import java.util.Objects;
import javax.naming.AuthenticationException;

import users.Base;
import users.Member;

public class MemberAuth implements Authenticator {
    private final UserRepository repo;

    public MemberAuth(UserRepository repo) {
        this.repo = Objects.requireNonNull(repo, "UserRepository cannot be null");
    }

    @Override
    public Base login(String email, String password) throws AuthenticationException {
        Base user = repo.existsByEmailAndPassword(email, password);
        if (user == null) {
            throw new AuthenticationException("Invalid email or password!");
        }
        if (!(user instanceof Member)) {
            throw new AuthenticationException("Access denied! This account is not a member");
        }
        return user;
    }

    @Override
    public Base register(String firstName, String lastName, String email, String password)
            throws AuthenticationException {
        if (repo.existsByEmail(email)) {
            throw new AuthenticationException("An account with this email already exists!");
        }

        // Default to a 1-Month membership;
        Member newUser = new Member(firstName, lastName, email, password, LocalDate.now().plusMonths(1));
        return repo.save(newUser);
    }

    @Override
    public void logout(Base user) {
        // todo: control state
        Objects.requireNonNull(user, "User cannot be null");
    }
}
