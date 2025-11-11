package ru.itis.wr.repositories;

import ru.itis.wr.entities.Item;
import ru.itis.wr.entities.ItemRarity;
import ru.itis.wr.entities.ItemAttributes;
import ru.itis.wr.repositories.dataSource.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemRepositoryImpl implements ItemRepository {

    private final DatabaseConnection databaseConnection;

    private static final String SAVE_QUERY = """
        INSERT INTO items (name, rarity, cost, icon_url, attributes, is_active) 
        VALUES (?, ?::item_rarity_enum, ?, ?, ?, ?) 
        RETURNING id
        """;

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM items WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM items WHERE is_active = true";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM items WHERE name ILIKE ? AND is_active = true";
    private static final String FIND_BY_RARITY_QUERY = "SELECT * FROM items WHERE rarity = ?::item_rarity_enum AND is_active = true";
    private static final String FIND_BY_COST_RANGE_QUERY = "SELECT * FROM items WHERE cost BETWEEN ? AND ? AND is_active = true";
    private static final String UPDATE_QUERY = """
        UPDATE items SET name = ?, rarity = ?::item_rarity_enum, cost = ?, 
        icon_url = ?, attributes = ?, is_active = ? WHERE id = ?
        """;
    private static final String DEACTIVATE_QUERY = "UPDATE items SET is_active = false WHERE id = ?";

    public ItemRepositoryImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Long save(Item item) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_QUERY)) {

            statement.setString(1, item.getName());
            statement.setString(2, item.getRarity().name());
            statement.setInt(3, item.getCost());
            statement.setString(4, item.getIconUrl());
            statement.setArray(5, connection.createArrayOf("varchar", attributesToStringArray(item.getAttributes())));
            statement.setBoolean(6, item.isActive());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("id");
            }
            throw new SQLException("Item creation failed");

        } catch (SQLException e) {
            throw new RuntimeException("Error saving item", e);
        }
    }

    @Override
    public Optional<Item> findById(Long id) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_QUERY)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapResultSetToItem(resultSet));
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error finding item by id", e);
        }
    }

    @Override
    public List<Item> findAll() {
        List<Item> items = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_QUERY);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                items.add(mapResultSetToItem(resultSet));
            }
            return items;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding all items", e);
        }
    }

    @Override
    public List<Item> findByNameContaining(String name) {
        List<Item> items = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_NAME_QUERY)) {

            statement.setString(1, "%" + name + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                items.add(mapResultSetToItem(resultSet));
            }
            return items;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding items by name", e);
        }
    }

    @Override
    public List<Item> findByRarity(ItemRarity rarity) {
        List<Item> items = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_RARITY_QUERY)) {

            statement.setString(1, rarity.name());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                items.add(mapResultSetToItem(resultSet));
            }
            return items;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding items by rarity", e);
        }
    }

    @Override
    public List<Item> findByCostRange(Integer minCost, Integer maxCost) {
        List<Item> items = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_COST_RANGE_QUERY)) {

            statement.setInt(1, minCost);
            statement.setInt(2, maxCost);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                items.add(mapResultSetToItem(resultSet));
            }
            return items;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding items by cost range", e);
        }
    }

    @Override
    public void update(Item item) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {

            statement.setString(1, item.getName());
            statement.setString(2, item.getRarity().name());
            statement.setInt(3, item.getCost());
            statement.setString(4, item.getIconUrl());
            statement.setArray(5, connection.createArrayOf("varchar", attributesToStringArray(item.getAttributes())));
            statement.setBoolean(6, item.isActive());
            statement.setLong(7, item.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating item", e);
        }
    }

    @Override
    public void deactivate(Long itemId) {
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DEACTIVATE_QUERY)) {

            statement.setLong(1, itemId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deactivating item", e);
        }
    }

    private String[] attributesToStringArray(ItemAttributes[] attributes) {
        if (attributes == null || attributes.length == 0) {
            return new String[0];
        }
        String[] stringArray = new String[attributes.length];
        for (int i = 0; i < attributes.length; i++) {
            stringArray[i] = attributes[i].name();
        }
        return stringArray;
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

    private Item mapResultSetToItem(ResultSet resultSet) throws SQLException {
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
}