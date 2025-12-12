package authentication;

import java.util.Objects;
import java.util.Optional;

import authentication.session.UserSession;
import domain.user.User;
import domain.user.Member;
import repo.UserRepository;

/**
 * Authenticator implementation for standard Members.
 */
public class MemberAuth implements Authenticator {
    private final UserRepository repo;
    private final UserSession state;

    /**
     * Creates a new MemberAuth.
     * 
     * @param repo  user repository
     * @param state user session state
     */
    public MemberAuth(UserRepository repo, UserSession state) {
        this.repo = Objects.requireNonNull(repo, "UserRepository cannot be null");
        this.state = Objects.requireNonNull(state, "UserState cannot be null");
    }

    @Override
    public User login(String email, String password) throws AuthException {
        Optional<User> user = repo.existsByEmailAndPassword(email, password);
        if (user.isEmpty()) {
            throw new AuthException("Invalid email or password.");
        }
        User member = user.get();
        if (!(member instanceof Member)) {
            throw new AuthException("Access denied!");
        }
        state.login(member);
        return member;
    }

    @Override
    public User register(String firstName, String lastName, String email, String password)
            throws AuthException {
        if (repo.existsByEmail(email)) {
            throw new AuthException("An account with this email already exists.");
        }
        Member newMember = new Member(firstName, lastName, email, password);
        return repo.save(newMember);
    }

    @Override
    public void logout(User member) {
        Objects.requireNonNull(member, "Member cannot be null");
        state.logout();
    }
}
