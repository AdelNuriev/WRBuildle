package ru.itis.wr.repositories;

import ru.itis.wr.entities.ChallengeBlock;
import ru.itis.wr.entities.BlockType;
import ru.itis.wr.repositories.dataSource.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChallengeBlockRepositoryImpl implements ChallengeBlockRepository {

    private final DatabaseConnection databaseConnection;

    private static final String SAVE_QUERY = """
        INSERT INTO daily_challenge_blocks (daily_challenge_id, block_type, target_item_id, extra_item_id, settings, created_at) 
        VALUES (?, ?::block_type_enum, ?, ?, ?, ?) 
        RETURNING id
        """;

    private static final String FIND_BY_CHALLENGE_ID_QUERY = """
        SELECT * FROM daily_challenge_blocks 
        WHERE daily_challenge_id = ? 
        ORDER BY block_type
        """;

    private static final String FIND_BY_CHALLENGE_AND_TYPE_QUERY = """
        SELECT * FROM daily_challenge_blocks 
        WHERE daily_challenge_id = ? AND block_type = ?::block_type_enum
        """;

    private static final String DELETE_BY_CHALLENGE_ID_QUERY = "DELETE FROM daily_challenge_blocks WHERE daily_challenge_id = ?";

    public ChallengeBlockRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Long save(ChallengeBlock block) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_QUERY)) {

            statement.setLong(1, block.getDailyChallengeId());
            statement.setString(2, block.getBlockType().name());
            statement.setLong(3, block.getTargetItemId());
            statement.setObject(4, block.getExtraItemId());
            statement.setString(5, block.getSettings());
            statement.setTimestamp(6, Timestamp.valueOf(block.getCreatedAt()));

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("id");
            }
            throw new SQLException("Challenge block creation failed");

        } catch (SQLException e) {
            throw new RuntimeException("Error saving challenge block", e);
        }
    }

    @Override
    public List<ChallengeBlock> findByDailyChallengeId(Long challengeId) {
        List<ChallengeBlock> blocks = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_CHALLENGE_ID_QUERY)) {

            statement.setLong(1, challengeId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                blocks.add(mapResultSetToBlock(resultSet));
            }
            return blocks;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding blocks by challenge id", e);
        }
    }

    @Override
    public Optional<ChallengeBlock> findByChallengeIdAndType(Long challengeId, BlockType blockType) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_CHALLENGE_AND_TYPE_QUERY)) {

            statement.setLong(1, challengeId);
            statement.setString(2, blockType.name());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapResultSetToBlock(resultSet));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error finding block by challenge and type", e);
        }
    }

    @Override
    public void deleteByDailyChallengeId(Long challengeId) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BY_CHALLENGE_ID_QUERY)) {

            statement.setLong(1, challengeId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting blocks by challenge id", e);
        }
    }

    private ChallengeBlock mapResultSetToBlock(ResultSet resultSet) throws SQLException {
        Long extraItemId = resultSet.getObject("extra_item_id") != null ?
                resultSet.getLong("extra_item_id") : null;

        return new ChallengeBlock(
                resultSet.getLong("id"),
                resultSet.getLong("daily_challenge_id"),
                BlockType.valueOf(resultSet.getString("block_type")),
                resultSet.getLong("target_item_id"),
                extraItemId,
                resultSet.getString("settings"),
                resultSet.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
