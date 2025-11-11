package ru.itis.wr.repositories;

import ru.itis.wr.entities.UserStatistics;
import ru.itis.wr.repositories.dataSource.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserStatisticsRepositoryImpl implements UserStatisticsRepository {

    private final DatabaseConnection databaseConnection;

    private static final String SAVE_QUERY = """
        INSERT INTO user_statistics (user_id, total_games, games_won, total_score, daily_streak, last_daily_play, best_daily_score, updated_at) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?) 
        RETURNING id
        """;

    private static final String FIND_BY_USER_ID_QUERY = "SELECT * FROM user_statistics WHERE user_id = ?";
    private static final String UPDATE_QUERY = """
        UPDATE user_statistics 
        SET total_games = ?, games_won = ?, total_score = ?, daily_streak = ?, 
            last_daily_play = ?, best_daily_score = ?, updated_at = ? 
        WHERE id = ?
        """;

    private static final String FIND_TOP_BY_SCORE_QUERY = """
        SELECT us.*, u.username 
        FROM user_statistics us 
        JOIN users u ON us.user_id = u.id 
        ORDER BY us.total_score DESC 
        LIMIT ?
        """;

    public UserStatisticsRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public void save(UserStatistics statistics) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_QUERY)) {

            statement.setLong(1, statistics.getUserId());
            statement.setInt(2, statistics.getTotalGames());
            statement.setInt(3, statistics.getGamesWon());
            statement.setInt(4, statistics.getTotalScore());
            statement.setInt(5, statistics.getDailyStreak());
            statement.setDate(6, statistics.getLastDailyPlay() != null ?
                    Date.valueOf(statistics.getLastDailyPlay()) : null);
            statement.setInt(7, statistics.getBestDailyScore());
            statement.setTimestamp(8, Timestamp.valueOf(statistics.getUpdatedAt()));

            statement.executeQuery();

        } catch (SQLException e) {
            throw new RuntimeException("Error saving user statistics", e);
        }
    }

    @Override
    public Optional<UserStatistics> findByUserId(Long userId) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_USER_ID_QUERY)) {

            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapResultSetToStatistics(resultSet));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user statistics by user id", e);
        }
    }

    @Override
    public void update(UserStatistics statistics) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {

            statement.setInt(1, statistics.getTotalGames());
            statement.setInt(2, statistics.getGamesWon());
            statement.setInt(3, statistics.getTotalScore());
            statement.setInt(4, statistics.getDailyStreak());
            statement.setDate(5, statistics.getLastDailyPlay() != null ?
                    Date.valueOf(statistics.getLastDailyPlay()) : null);
            statement.setInt(6, statistics.getBestDailyScore());
            statement.setTimestamp(7, Timestamp.valueOf(statistics.getUpdatedAt()));
            statement.setLong(8, statistics.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating user statistics", e);
        }
    }

    @Override
    public List<UserStatistics> findTopByTotalScore(int limit) {
        List<UserStatistics> statistics = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_TOP_BY_SCORE_QUERY)) {

            statement.setInt(1, limit);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                statistics.add(mapResultSetToStatistics(resultSet));
            }
            return statistics;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding top statistics", e);
        }
    }

    private UserStatistics mapResultSetToStatistics(ResultSet resultSet) throws SQLException {
        return new UserStatistics(
                resultSet.getLong("id"),
                resultSet.getLong("user_id"),
                resultSet.getInt("total_games"),
                resultSet.getInt("games_won"),
                resultSet.getInt("total_score"),
                resultSet.getInt("daily_streak"),
                resultSet.getDate("last_daily_play") != null ?
                        resultSet.getDate("last_daily_play").toLocalDate() : null,
                resultSet.getInt("best_daily_score"),
                resultSet.getTimestamp("updated_at").toLocalDateTime()
        );
    }
}
