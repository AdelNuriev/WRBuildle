package ru.itis.wr.repositories;

import ru.itis.wr.entities.InfiniteGame;

import java.util.Optional;

public interface InfiniteGameRepository {
    Optional<InfiniteGame> findByUserId(Long userId);
    Long save(InfiniteGame game);
    void update(InfiniteGame game);
    void deleteByUserId(Long userId);
}
