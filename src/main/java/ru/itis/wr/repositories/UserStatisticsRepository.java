package ru.itis.wr.repositories;

import ru.itis.wr.entities.UserStatistics;

import java.util.List;
import java.util.Optional;

public interface UserStatisticsRepository {
    void save(UserStatistics statistics);
    Optional<UserStatistics> findByUserId(Long userId);
    void update(UserStatistics statistics);
    List<UserStatistics> findTopByTotalScore(int limit);
}
