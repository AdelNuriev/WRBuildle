package ru.itis.wr.repositories;

import ru.itis.wr.entities.DailyChallenge;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyChallengeRepository {
    Long save(DailyChallenge dailyChallenge);
    Optional<DailyChallenge> findById(Long id);
    Optional<DailyChallenge> findByDate(LocalDate date);
    List<DailyChallenge> findRecent(int days);
    void deactivateOldChallenges(LocalDate beforeDate);

}
