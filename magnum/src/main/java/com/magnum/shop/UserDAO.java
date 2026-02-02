package com.magnum.shop;

import java.sql.*;
import java.util.Optional;

public class UserDAO {
    private final Connection connection;

    public UserDAO(Connection connection) { this.connection = connection; }

    public Optional<User> login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password); // Note: Plain text for demo only. Use BCrypt in production.
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(new User(
                        rs.getLong("id"), rs.getString("username"),
                        rs.getString("password"), rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}