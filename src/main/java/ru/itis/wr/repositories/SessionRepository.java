package ru.itis.wr.repositories;

import ru.itis.wr.entities.Session;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SessionRepository {
    void save(Session session);
    Optional<Session> findById(String sessionId);
    Optional<Session> findByUserId(Long userId);
    void deleteById(String sessionId);
    void deleteExpiredSessions(LocalDateTime now);
}
