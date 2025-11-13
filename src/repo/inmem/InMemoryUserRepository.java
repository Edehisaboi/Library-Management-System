package repo.inmem;

import domain.user.User;
import repo.UserRepository;

import java.util.*;

public final class InMemoryUserRepository implements UserRepository {
    private final Map<UUID, User> store = new HashMap<>();
    private final Map<String, UUID> byEmail = new HashMap<>();

    @Override
    public User save(User user) {
        store.put(user.getId(), user);
        byEmail.put(user.getEmail().trim().toLowerCase(Locale.ROOT), user.getId());
        return user;
    }

    @Override
    public void delete(User user) {
        store.remove(user.getId());
        byEmail.remove(user.getEmail().trim().toLowerCase(Locale.ROOT));
    }

    @Override
    public boolean existsByEmail(String email) {
        return byEmail.containsKey(email.trim().toLowerCase(Locale.ROOT));
    }

    @Override
    public Optional<User> existsByEmailAndPassword(String email, String password) {
        User user = store.get(byEmail.get(email.trim().toLowerCase(Locale.ROOT)));
        return Optional.ofNullable(user).filter(u -> Objects.equals(u.getPassword(), password));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }
}
