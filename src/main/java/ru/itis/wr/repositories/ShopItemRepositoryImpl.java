package ru.itis.wr.repositories;

import ru.itis.wr.entities.ShopItem;
import ru.itis.wr.entities.ShopItemType;
import ru.itis.wr.entities.ShopItemRarity;
import ru.itis.wr.repositories.dataSource.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShopItemRepositoryImpl implements ShopItemRepository {

    private final DatabaseConnection databaseConnection;

    private static final String SAVE_QUERY = """
        INSERT INTO shop_items (name, type, price, image_url, rarity, is_active, created_at) 
        VALUES (?, ?::shop_item_type_enum, ?, ?, ?::shop_item_rarity_enum, ?, ?) 
        RETURNING id
        """;

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM shop_items WHERE id = ?";
    private static final String FIND_ALL_ACTIVE_QUERY = "SELECT * FROM shop_items WHERE is_active = true ORDER BY type, price";
    private static final String FIND_BY_TYPE_QUERY = "SELECT * FROM shop_items WHERE type = ?::shop_item_type_enum AND is_active = true";
    private static final String FIND_BY_RARITY_QUERY = "SELECT * FROM shop_items WHERE rarity = ?::shop_item_rarity_enum AND is_active = true";
    private static final String DEACTIVATE_QUERY = "UPDATE shop_items SET is_active = false WHERE id = ?";

    public ShopItemRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Long save(ShopItem item) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_QUERY)) {

            statement.setString(1, item.getName());
            statement.setString(2, item.getType().name());
            statement.setInt(3, item.getPrice());
            statement.setString(4, item.getImageUrl());
            statement.setString(5, item.getRarity().name());
            statement.setBoolean(6, item.getIsActive());
            statement.setTimestamp(7, Timestamp.valueOf(item.getCreatedAt()));

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("id");
            }
            throw new SQLException("Shop item creation failed");

        } catch (SQLException e) {
            throw new RuntimeException("Error saving shop item", e);
        }
    }

    @Override
    public Optional<ShopItem> findById(Long id) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_QUERY)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapResultSetToShopItem(resultSet));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error finding shop item by id", e);
        }
    }

    @Override
    public List<ShopItem> findAllActive() {
        List<ShopItem> items = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_ACTIVE_QUERY);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                items.add(mapResultSetToShopItem(resultSet));
            }
            return items;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding all active shop items", e);
        }
    }

    @Override
    public List<ShopItem> findByType(ShopItemType type) {
        List<ShopItem> items = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_TYPE_QUERY)) {

            statement.setString(1, type.name());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                items.add(mapResultSetToShopItem(resultSet));
            }
            return items;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding shop items by type", e);
        }
    }

    @Override
    public List<ShopItem> findByRarity(ShopItemRarity rarity) {
        List<ShopItem> items = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_RARITY_QUERY)) {

            statement.setString(1, rarity.name());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                items.add(mapResultSetToShopItem(resultSet));
            }
            return items;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding shop items by rarity", e);
        }
    }

    @Override
    public void deactivate(Long itemId) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DEACTIVATE_QUERY)) {

            statement.setLong(1, itemId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deactivating shop item", e);
        }
    }

    private ShopItem mapResultSetToShopItem(ResultSet resultSet) throws SQLException {
        return new ShopItem(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                ShopItemType.valueOf(resultSet.getString("type")),
                resultSet.getInt("price"),
                resultSet.getString("image_url"),
                ShopItemRarity.valueOf(resultSet.getString("rarity")),
                resultSet.getBoolean("is_active"),
                resultSet.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
