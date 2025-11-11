package ru.itis.wr.repositories;

import ru.itis.wr.entities.UserPurchase;
import ru.itis.wr.repositories.dataSource.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserPurchaseRepositoryImpl implements UserPurchaseRepository {

    private final DatabaseConnection databaseConnection;

    private static final String SAVE_QUERY = """
        INSERT INTO user_purchases (user_id, shop_item_id, purchased_at, is_equipped) 
        VALUES (?, ?, ?, ?) 
        RETURNING id
        """;

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM user_purchases WHERE id = ?";
    private static final String FIND_BY_USER_ID_QUERY = """
        SELECT up.*, si.name as item_name, si.type as item_type, si.image_url 
        FROM user_purchases up 
        JOIN shop_items si ON up.shop_item_id = si.id 
        WHERE up.user_id = ? 
        ORDER BY up.purchased_at DESC
        """;

    private static final String FIND_BY_USER_AND_ITEM_QUERY = """
        SELECT * FROM user_purchases 
        WHERE user_id = ? AND shop_item_id = ?
        """;

    private static final String FIND_EQUIPPED_BY_USER_QUERY = """
        SELECT up.*, si.name as item_name, si.type as item_type, si.image_url 
        FROM user_purchases up 
        JOIN shop_items si ON up.shop_item_id = si.id 
        WHERE up.user_id = ? AND up.is_equipped = true
        """;

    private static final String UPDATE_QUERY = """
        UPDATE user_purchases 
        SET is_equipped = ? 
        WHERE id = ?
        """;

    private static final String USER_OWNS_ITEM_QUERY = """
        SELECT COUNT(*) FROM user_purchases 
        WHERE user_id = ? AND shop_item_id = ?
        """;

    public UserPurchaseRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Long save(UserPurchase purchase) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_QUERY)) {

            statement.setLong(1, purchase.getUserId());
            statement.setLong(2, purchase.getShopItemId());
            statement.setTimestamp(3, Timestamp.valueOf(purchase.getPurchasedAt()));
            statement.setBoolean(4, purchase.getIsEquipped());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("id");
            }
            throw new SQLException("User purchase creation failed");

        } catch (SQLException e) {
            throw new RuntimeException("Error saving user purchase", e);
        }
    }

    @Override
    public Optional<UserPurchase> findById(Long id) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_QUERY)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapResultSetToPurchase(resultSet));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user purchase by id", e);
        }
    }

    @Override
    public List<UserPurchase> findByUserId(Long userId) {
        List<UserPurchase> purchases = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_USER_ID_QUERY)) {

            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                purchases.add(mapResultSetToPurchase(resultSet));
            }
            return purchases;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user purchases by user id", e);
        }
    }

    @Override
    public Optional<UserPurchase> findByUserAndItem(Long userId, Long shopItemId) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_USER_AND_ITEM_QUERY)) {

            statement.setLong(1, userId);
            statement.setLong(2, shopItemId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapResultSetToPurchase(resultSet));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user purchase by user and item", e);
        }
    }

    @Override
    public List<UserPurchase> findEquippedByUserId(Long userId) {
        List<UserPurchase> purchases = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_EQUIPPED_BY_USER_QUERY)) {

            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                purchases.add(mapResultSetToPurchase(resultSet));
            }
            return purchases;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding equipped purchases", e);
        }
    }

    @Override
    public void update(UserPurchase purchase) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {

            statement.setBoolean(1, purchase.getIsEquipped());
            statement.setLong(2, purchase.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating user purchase", e);
        }
    }

    @Override
    public boolean userOwnsItem(Long userId, Long shopItemId) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(USER_OWNS_ITEM_QUERY)) {

            statement.setLong(1, userId);
            statement.setLong(2, shopItemId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;

        } catch (SQLException e) {
            throw new RuntimeException("Error checking if user owns item", e);
        }
    }

    private UserPurchase mapResultSetToPurchase(ResultSet resultSet) throws SQLException {
        return new UserPurchase(
                resultSet.getLong("id"),
                resultSet.getLong("user_id"),
                resultSet.getLong("shop_item_id"),
                resultSet.getTimestamp("purchased_at").toLocalDateTime(),
                resultSet.getBoolean("is_equipped")
        );
    }
}
