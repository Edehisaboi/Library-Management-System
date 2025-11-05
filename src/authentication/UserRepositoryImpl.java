package authentication;

import users.Base;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class UserRepositoryImpl implements UserRepository {
    private final Map<String, Base> users = new HashMap<>();

    @Override
    public Base save(Base user) {
        Objects.requireNonNull(user, "user cannot be null");
        String key = normalize(user.getEmail());
        users.put(key, user);
        return user;
    }

    @Override
    public void delete(Base user) {
        Objects.requireNonNull(user, "user cannot be null");
        String key = normalize(user.getEmail());
        users.remove(key);
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.containsKey(normalize(email));
    }

    @Override
    public Base existsByEmailAndPassword(String email, String password) {
        Base user = users.get(normalize(email));
        if (user == null) return null;
        return Objects.equals(user.getPassword(), password) ? user : null;
    }

    @Override
    public Base findById(UUID id) {
        Objects.requireNonNull(id, "id cannot be null");
        for (Base u : users.values()) {
            if (id.equals(u.getId())) return u;
        }
        return null;
    }

    private static String normalize(String email) {
        Objects.requireNonNull(email, "email cannot be null");
        return email.trim().toLowerCase();
    }
}
