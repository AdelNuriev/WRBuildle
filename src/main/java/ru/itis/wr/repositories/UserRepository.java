package ru.itis.wr.repositories;

import ru.itis.wr.entities.User;

import java.util.Optional;

public interface UserRepository {
    Long save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    void update(User user);
    boolean existsByEmail(String email);
}
