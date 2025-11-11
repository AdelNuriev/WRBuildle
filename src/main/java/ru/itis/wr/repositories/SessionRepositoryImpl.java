package ru.itis.wr.repositories;

import ru.itis.wr.entities.Session;
import ru.itis.wr.repositories.dataSource.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class SessionRepositoryImpl implements SessionRepository {

    private final DatabaseConnection databaseConnection;

    private static final String SAVE_QUERY = "INSERT INTO sessions (session_id, user_id, expire_at) VALUES (?, ?, ?)";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM sessions WHERE session_id = ?";
    private static final String FIND_BY_USER_ID_QUERY = "SELECT * FROM sessions WHERE user_id = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM sessions WHERE session_id = ?";
    private static final String DELETE_EXPIRED_QUERY = "DELETE FROM sessions WHERE expire_at < ?";

    public SessionRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public void save(Session session) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_QUERY)) {

            statement.setString(1, session.getSessionId());
            statement.setLong(2, session.getUserId());
            statement.setTimestamp(3, Timestamp.valueOf(session.getExpireAt()));

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error saving session", e);
        }
    }

    @Override
    public Optional<Session> findById(String sessionId) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_QUERY)) {

            statement.setString(1, sessionId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(new Session(
                        resultSet.getString("session_id"),
                        resultSet.getLong("user_id"),
                        resultSet.getTimestamp("expire_at").toLocalDateTime()
                ));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error finding session by id", e);
        }
    }

    @Override
    public Optional<Session> findByUserId(Long userId) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_USER_ID_QUERY)) {

            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(new Session(
                        resultSet.getString("session_id"),
                        resultSet.getLong("user_id"),
                        resultSet.getTimestamp("expire_at").toLocalDateTime()
                ));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error finding session by user id", e);
        }
    }

    @Override
    public void deleteById(String sessionId) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID_QUERY)) {

            statement.setString(1, sessionId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting session", e);
        }
    }

    @Override
    public void deleteExpiredSessions(LocalDateTime now) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_EXPIRED_QUERY)) {

            statement.setTimestamp(1, Timestamp.valueOf(now));
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting expired sessions", e);
        }
    }
}