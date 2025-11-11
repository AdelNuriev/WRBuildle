package ru.itis.wr.repositories;

import ru.itis.wr.entities.User;
import ru.itis.wr.entities.Role;
import ru.itis.wr.repositories.dataSource.DatabaseConnection;

import java.sql.*;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {

    private final DatabaseConnection databaseConnection;

    private static final String SAVE_QUERY = """
        INSERT INTO users (username, email, password_hash, salt, role, coins, experience, level, created_at, last_login) 
        VALUES (?, ?, ?, ?, ?::role_enum, ?, ?, ?, ?, ?) 
        RETURNING id
        """;

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String FIND_BY_USERNAME_QUERY = "SELECT * FROM users WHERE username = ?";
    private static final String UPDATE_QUERY = """
        UPDATE users SET username = ?, email = ?, password_hash = ?, salt = ?, role = ?::role_enum, 
        coins = ?, experience = ?, level = ?, last_login = ? WHERE id = ?
        """;
    private static final String EXISTS_BY_EMAIL_QUERY = "SELECT COUNT(*) FROM users WHERE email = ?";

    public UserRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Long save(User user) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_QUERY)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getSalt());
            statement.setString(5, user.getRole().name());
            statement.setInt(6, user.getCoins());
            statement.setInt(7, user.getExperience());
            statement.setInt(8, user.getLevel());
            statement.setTimestamp(9, Timestamp.valueOf(user.getCreatedAt()));
            statement.setTimestamp(10, user.getLastLogin() != null ?
                    Timestamp.valueOf(user.getLastLogin()) : null);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("id");
            }
            throw new SQLException("User creation failed");

        } catch (SQLException e) {
            throw new RuntimeException("Error saving user", e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_QUERY)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapResultSetToUser(resultSet));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by id", e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_EMAIL_QUERY)) {

            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapResultSetToUser(resultSet));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by email", e);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_USERNAME_QUERY)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapResultSetToUser(resultSet));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by username", e);
        }
    }

    @Override
    public void update(User user) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getSalt());
            statement.setString(5, user.getRole().name());
            statement.setInt(6, user.getCoins());
            statement.setInt(7, user.getExperience());
            statement.setInt(8, user.getLevel());
            statement.setTimestamp(9, user.getLastLogin() != null ?
                    Timestamp.valueOf(user.getLastLogin()) : null);
            statement.setLong(10, user.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating user", e);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(EXISTS_BY_EMAIL_QUERY)) {

            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;

        } catch (SQLException e) {
            throw new RuntimeException("Error checking email existence", e);
        }
    }

    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getLong("id"),
                resultSet.getString("username"),
                resultSet.getString("email"),
                resultSet.getString("password_hash"),
                resultSet.getString("salt"),
                Role.valueOf(resultSet.getString("role")),
                resultSet.getInt("coins"),
                resultSet.getInt("experience"),
                resultSet.getInt("level"),
                resultSet.getTimestamp("created_at").toLocalDateTime(),
                resultSet.getTimestamp("last_login") != null ?
                        resultSet.getTimestamp("last_login").toLocalDateTime() : null
        );
    }
}