package ru.itis.wr.repositories;

import ru.itis.wr.entities.UserResult;
import ru.itis.wr.entities.BlockType;
import ru.itis.wr.repositories.dataSource.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserResultRepositoryImpl implements UserResultRepository {

    private final DatabaseConnection databaseConnection;

    private static final String SAVE_QUERY = """
        INSERT INTO user_results (user_id, challenge_date, block_type, attempts, completed, score, completed_at) 
        VALUES (?, ?, ?::block_type_enum, ?, ?, ?, ?) 
        RETURNING id
        """;

    private static final String FIND_BY_USER_DATE_TYPE_QUERY = """
        SELECT * FROM user_results 
        WHERE user_id = ? AND challenge_date = ? AND block_type = ?::block_type_enum
        """;

    private static final String FIND_BY_USER_AND_DATE_QUERY = """
        SELECT * FROM user_results 
        WHERE user_id = ? AND challenge_date = ? 
        ORDER BY block_type
        """;

    private static final String FIND_RECENT_BY_USER_QUERY = """
        SELECT * FROM user_results 
        WHERE user_id = ? AND challenge_date >= ? 
        ORDER BY challenge_date DESC, block_type
        """;

    private static final String GET_TOTAL_SCORE_QUERY = """
        SELECT COALESCE(SUM(score), 0) as total_score 
        FROM user_results 
        WHERE user_id = ? AND challenge_date = ?
        """;

    private static final String UPDATE_QUERY = """
        UPDATE user_results 
        SET attempts = ?, completed = ?, score = ?, completed_at = ? 
        WHERE id = ?
        """;

    public UserResultRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Long save(UserResult result) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_QUERY)) {

            statement.setLong(1, result.getUserId());
            statement.setDate(2, Date.valueOf(result.getChallengeDate()));
            statement.setString(3, result.getBlockType().name());
            statement.setInt(4, result.getAttempts());
            statement.setBoolean(5, result.getCompleted());
            statement.setInt(6, result.getScore());
            statement.setTimestamp(7, result.getCompletedAt() != null ?
                    Timestamp.valueOf(result.getCompletedAt()) : null);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("id");
            }
            throw new SQLException("User result creation failed");

        } catch (SQLException e) {
            throw new RuntimeException("Error saving user result", e);
        }
    }

    @Override
    public Optional<UserResult> findByUserAndDateAndType(Long userId, LocalDate date, BlockType blockType) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_USER_DATE_TYPE_QUERY)) {

            statement.setLong(1, userId);
            statement.setDate(2, Date.valueOf(date));
            statement.setString(3, blockType.name());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapResultSetToUserResult(resultSet));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user result", e);
        }
    }

    @Override
    public List<UserResult> findByUserIdAndDate(Long userId, LocalDate date) {
        List<UserResult> results = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_USER_AND_DATE_QUERY)) {

            statement.setLong(1, userId);
            statement.setDate(2, Date.valueOf(date));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                results.add(mapResultSetToUserResult(resultSet));
            }
            return results;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user results by date", e);
        }
    }

    @Override
    public List<UserResult> findRecentByUserId(Long userId, int days) {
        List<UserResult> results = new ArrayList<>();
        LocalDate startDate = LocalDate.now().minusDays(days - 1);

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_RECENT_BY_USER_QUERY)) {

            statement.setLong(1, userId);
            statement.setDate(2, Date.valueOf(startDate));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                results.add(mapResultSetToUserResult(resultSet));
            }
            return results;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding recent user results", e);
        }
    }

    @Override
    public Integer getTotalScoreByUserAndDate(Long userId, LocalDate date) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_TOTAL_SCORE_QUERY)) {

            statement.setLong(1, userId);
            statement.setDate(2, Date.valueOf(date));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("total_score");
            }
            return 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error getting total score", e);
        }
    }

    @Override
    public void update(UserResult result) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {

            statement.setInt(1, result.getAttempts());
            statement.setBoolean(2, result.getCompleted());
            statement.setInt(3, result.getScore());
            statement.setTimestamp(4, result.getCompletedAt() != null ?
                    Timestamp.valueOf(result.getCompletedAt()) : null);
            statement.setLong(5, result.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating user result", e);
        }
    }

    private UserResult mapResultSetToUserResult(ResultSet resultSet) throws SQLException {
        return new UserResult(
                resultSet.getLong("id"),
                resultSet.getLong("user_id"),
                resultSet.getDate("challenge_date").toLocalDate(),
                BlockType.valueOf(resultSet.getString("block_type")),
                resultSet.getInt("attempts"),
                resultSet.getBoolean("completed"),
                resultSet.getInt("score"),
                resultSet.getTimestamp("completed_at") != null ?
                        resultSet.getTimestamp("completed_at").toLocalDateTime() : null
        );
    }
}
