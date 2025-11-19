package ru.itis.wr.helper;

import ru.itis.wr.entities.*;
import ru.itis.wr.repositories.UserResultRepository;
import ru.itis.wr.repositories.UserStatisticsRepository;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public Item mapResultSetToItem(ResultSet resultSet) throws SQLException {
        Array attributesArray = resultSet.getArray("attributes");
        ItemAttributes[] attributes = stringArrayToAttributes(attributesArray);

        return new Item(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                ItemRarity.valueOf(resultSet.getString("rarity")),
                resultSet.getShort("cost"),
                resultSet.getString("icon_url"),
                attributes,
                resultSet.getBoolean("is_active")
        );
    }

    private ItemAttributes[] stringArrayToAttributes(Array array) throws SQLException {
        if (array == null) {
            return new ItemAttributes[0];
        }
        String[] stringArray = (String[]) array.getArray();
        ItemAttributes[] attributes = new ItemAttributes[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            attributes[i] = ItemAttributes.valueOf(stringArray[i]);
        }
        return attributes;
    }
}
