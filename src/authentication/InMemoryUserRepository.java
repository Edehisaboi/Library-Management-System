package authentication;

import java.util.*;
import users.Base;

public class InMemoryUserRepository implements UserRepository {
    private final Map<String, Base> users = new HashMap<>();

    @Override
    public Base save(Base user) {
        users.put(user.getEmail(), user);
        return user;
    }

    @Override
    public void delete(Base user) {
        users.remove(user.getEmail());
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.containsKey(email);
    }

    @Override
    public Base existsByEmailAndPassword(String email, String password) {
        Base user = users.get(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    @Override
    public Base findById(UUID id) {
        return users.values()
                .stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
