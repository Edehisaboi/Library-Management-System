package authentication;

import users.Base;

import java.util.UUID;

public interface UserRepository {
    Base save(Base user);
    void delete(Base user);

    boolean existsByEmail(String email);
    Base existsByEmailAndPassword(String email, String password);
    Base findById(UUID id);
}
