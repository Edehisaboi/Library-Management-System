package repo;

import domain.user.User;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    void delete(User user);

    boolean existsByEmail(String email);

    Optional<User> existsByEmailAndPassword(String email, String password);

    Optional<User> findById(UUID id);

    List<User> findAll();
}
