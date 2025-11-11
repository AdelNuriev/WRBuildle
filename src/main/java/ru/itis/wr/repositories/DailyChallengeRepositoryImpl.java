package ru.itis.wr.repositories;

import ru.itis.wr.entities.DailyChallenge;
import ru.itis.wr.repositories.dataSource.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DailyChallengeRepositoryImpl implements DailyChallengeRepository {

    private final DatabaseConnection databaseConnection;

    private static final String SAVE_QUERY = """
        INSERT INTO daily_challenges (challenge_date, created_by, created_at, is_active) 
        VALUES (?, ?, ?, ?) 
        RETURNING id
        """;

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM daily_challenges WHERE id = ?";
    private static final String FIND_BY_DATE_QUERY = "SELECT * FROM daily_challenges WHERE challenge_date = ?";
    private static final String FIND_RECENT_QUERY = """
        SELECT * FROM daily_challenges 
        WHERE challenge_date >= ? 
        ORDER BY challenge_date DESC 
        LIMIT ?
        """;

    private static final String DEACTIVATE_OLD_QUERY = "UPDATE daily_challenges SET is_active = false WHERE challenge_date < ?";

    public DailyChallengeRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Long save(DailyChallenge challenge) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_QUERY)) {

            statement.setDate(1, Date.valueOf(challenge.getChallengeDate()));
            statement.setLong(2, challenge.getCreatedBy());
            statement.setTimestamp(3, Timestamp.valueOf(challenge.getCreatedAt()));
            statement.setBoolean(4, challenge.getIsActive());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("id");
            }
            throw new SQLException("Daily challenge creation failed");

        } catch (SQLException e) {
            throw new RuntimeException("Error saving daily challenge", e);
        }
    }

    @Override
    public Optional<DailyChallenge> findById(Long id) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_QUERY)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapResultSetToChallenge(resultSet));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error finding daily challenge by id", e);
        }
    }

    @Override
    public Optional<DailyChallenge> findByDate(LocalDate date) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_DATE_QUERY)) {

            statement.setDate(1, Date.valueOf(date));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapResultSetToChallenge(resultSet));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error finding daily challenge by date", e);
        }
    }

    @Override
    public List<DailyChallenge> findRecent(int days) {
        List<DailyChallenge> challenges = new ArrayList<>();
        LocalDate startDate = LocalDate.now().minusDays(days - 1);

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_RECENT_QUERY)) {

            statement.setDate(1, Date.valueOf(startDate));
            statement.setInt(2, days);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                challenges.add(mapResultSetToChallenge(resultSet));
            }
            return challenges;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding recent daily challenges", e);
        }
    }

    @Override
    public void deactivateOldChallenges(LocalDate beforeDate) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DEACTIVATE_OLD_QUERY)) {

            statement.setDate(1, Date.valueOf(beforeDate));
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deactivating old challenges", e);
        }
    }

    private DailyChallenge mapResultSetToChallenge(ResultSet resultSet) throws SQLException {
        return new DailyChallenge(
                resultSet.getLong("id"),
                resultSet.getDate("challenge_date").toLocalDate(),
                resultSet.getLong("created_by"),
                resultSet.getTimestamp("created_at").toLocalDateTime(),
                resultSet.getBoolean("is_active")
        );
    }
}
