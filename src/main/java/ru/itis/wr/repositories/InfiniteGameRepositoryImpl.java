package ru.itis.wr.repositories;

import ru.itis.wr.entities.InfiniteGame;
import ru.itis.wr.entities.Item;
import ru.itis.wr.helper.RepositoryHelper;
import ru.itis.wr.repositories.dataSource.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InfiniteGameRepositoryImpl implements InfiniteGameRepository {
    private final DatabaseConnection databaseConnection;
    private final ItemRepository itemRepository;
    private final RepositoryHelper repositoryHelper;

    private static final String FIND_BY_USER_ID_QUERY = "SELECT * FROM infinite_games WHERE user_id = ?";
    private static final String SAVE_QUERY = """
        INSERT INTO infinite_games (user_id, current_target_id, score, streak, hints_used, started_at, last_guess_at) 
        VALUES (?, ?, ?, ?, ?, ?, ?) 
        RETURNING id
        """;
    private static final String UPDATE_QUERY = """
        UPDATE infinite_games 
        SET current_target_id = ?, score = ?, streak = ?, hints_used = ?, last_guess_at = ? 
        WHERE id = ?
        """;
    private static final String DELETE_BY_USER_ID_QUERY = "DELETE FROM infinite_games WHERE user_id = ?";
    private static final String SAVE_PREVIOUS_ITEM_QUERY = """
        INSERT INTO infinite_game_previous_items (game_id, item_id, position) 
        VALUES (?, ?, ?)
        """;
    private static final String FIND_PREVIOUS_ITEMS_QUERY = """
        SELECT i.* FROM items i 
        JOIN infinite_game_previous_items igpi ON i.id = igpi.item_id 
        WHERE igpi.game_id = ? 
        ORDER BY igpi.position DESC 
        LIMIT 10
        """;

    public InfiniteGameRepositoryImpl(DatabaseConnection databaseConnection, ItemRepository itemRepository, RepositoryHelper repositoryHelper) {
        this.databaseConnection = databaseConnection;
        this.itemRepository = itemRepository;
        this.repositoryHelper = repositoryHelper;
    }

    @Override
    public Optional<InfiniteGame> findByUserId(Long userId) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_USER_ID_QUERY)) {

            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                InfiniteGame game = mapResultSetToGame(resultSet);
                loadPreviousItems(connection, game);
                return Optional.of(game);
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error finding infinite game by user id", e);
        }
    }

    @Override
    public Long save(InfiniteGame game) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_QUERY)) {

            statement.setLong(1, game.getUserId());
            statement.setLong(2, game.getCurrentTarget().getId());
            statement.setInt(3, game.getScore());
            statement.setInt(4, game.getStreak());
            statement.setInt(5, game.getHintsUsed());
            statement.setTimestamp(6, Timestamp.valueOf(game.getStartedAt()));
            statement.setTimestamp(7, game.getLastGuessAt() != null ?
                    Timestamp.valueOf(game.getLastGuessAt()) : null);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long gameId = resultSet.getLong("id");
                savePreviousItems(connection, gameId, game.getPreviousItems());
                return gameId;
            }
            throw new SQLException("Infinite game creation failed");

        } catch (SQLException e) {
            throw new RuntimeException("Error saving infinite game", e);
        }
    }

    @Override
    public void update(InfiniteGame game) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {

            statement.setLong(1, game.getCurrentTarget().getId());
            statement.setInt(2, game.getScore());
            statement.setInt(3, game.getStreak());
            statement.setInt(4, game.getHintsUsed());
            statement.setTimestamp(5, game.getLastGuessAt() != null ?
                    Timestamp.valueOf(game.getLastGuessAt()) : null);
            statement.setLong(6, game.getId());

            statement.executeUpdate();
            updatePreviousItems(connection, game);

        } catch (SQLException e) {
            throw new RuntimeException("Error updating infinite game", e);
        }
    }

    @Override
    public void deleteByUserId(Long userId) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BY_USER_ID_QUERY)) {

            statement.setLong(1, userId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting infinite game by user id", e);
        }
    }

    private InfiniteGame mapResultSetToGame(ResultSet resultSet) throws SQLException {
        Long targetItemId = resultSet.getLong("current_target_id");
        Item targetItem = itemRepository.findById(targetItemId)
                .orElseThrow(() -> new RuntimeException("Target item not found: " + targetItemId));

        InfiniteGame game = new InfiniteGame(
                resultSet.getLong("user_id"),
                resultSet.getTimestamp("started_at").toLocalDateTime()
        );
        game.setId(resultSet.getLong("id"));
        game.setCurrentTarget(targetItem);
        game.setScore(resultSet.getInt("score"));
        game.setStreak(resultSet.getInt("streak"));
        game.setHintsUsed(resultSet.getInt("hints_used"));
        game.setLastGuessAt(resultSet.getTimestamp("last_guess_at") != null ?
                resultSet.getTimestamp("last_guess_at").toLocalDateTime() : null);

        return game;
    }

    private void loadPreviousItems(Connection connection, InfiniteGame game) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(FIND_PREVIOUS_ITEMS_QUERY)) {
            statement.setLong(1, game.getId());
            ResultSet resultSet = statement.executeQuery();

            List<Item> previousItems = new ArrayList<>();
            while (resultSet.next()) {
                Item item = repositoryHelper.mapResultSetToItem(resultSet);
                previousItems.add(item);
            }
            game.setPreviousItems(previousItems);
        }
    }

    private void savePreviousItems(Connection connection, Long gameId, List<Item> previousItems) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SAVE_PREVIOUS_ITEM_QUERY)) {
            for (int i = 0; i < previousItems.size(); i++) {
                statement.setLong(1, gameId);
                statement.setLong(2, previousItems.get(i).getId());
                statement.setInt(3, i);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void updatePreviousItems(Connection connection, InfiniteGame game) throws SQLException {
        try (PreparedStatement deleteStatement = connection.prepareStatement(
                "DELETE FROM infinite_game_previous_items WHERE game_id = ?")) {
            deleteStatement.setLong(1, game.getId());
            deleteStatement.executeUpdate();
        }
        savePreviousItems(connection, game.getId(), game.getPreviousItems());
    }
}
