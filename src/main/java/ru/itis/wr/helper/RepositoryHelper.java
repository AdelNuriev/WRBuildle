package ru.itis.wr.helper;

import ru.itis.wr.entities.UserResult;
import ru.itis.wr.entities.UserStatistics;
import ru.itis.wr.repositories.UserResultRepository;
import ru.itis.wr.repositories.UserStatisticsRepository;

import java.util.Optional;

public class RepositoryHelper {

    public void putUserStatistics(UserStatisticsRepository repository, UserStatistics statistics) {
        Optional<UserStatistics> existing = repository.findByUserId(statistics.getUserId());
        if (existing.isPresent()) {
            UserStatistics existingStats = existing.get();

            existingStats.setTotalGames(statistics.getTotalGames());
            existingStats.setGamesWon(statistics.getGamesWon());
            existingStats.setTotalScore(statistics.getTotalScore());
            existingStats.setDailyStreak(statistics.getDailyStreak());
            existingStats.setLastDailyPlay(statistics.getLastDailyPlay());
            existingStats.setBestDailyScore(statistics.getBestDailyScore());
            existingStats.setUpdatedAt(statistics.getUpdatedAt());
            repository.update(existingStats);
        } else {
            repository.save(statistics);
        }
    }

    public void putUserResult(UserResultRepository repository, UserResult result) {
        Optional<UserResult> existing = repository.findByUserAndDateAndType(
                result.getUserId(), result.getChallengeDate(), result.getBlockType());

        if (existing.isPresent()) {
            UserResult existingResult = existing.get();
            existingResult.setAttempts(result.getAttempts());
            existingResult.setCompleted(result.getCompleted());
            existingResult.setScore(result.getScore());
            existingResult.setCompletedAt(result.getCompletedAt());
            repository.update(existingResult);
        } else {
            repository.save(result);
        }
    }
}
