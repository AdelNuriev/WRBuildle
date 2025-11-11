package ru.itis.wr.repositories.dataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionImpl implements DatabaseConnection {
    private String url;
    private String user;
    private String password;

    public DatabaseConnectionImpl(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("❌ PostgreSQL JDBC Driver not found. Add it to your dependencies.", e);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
