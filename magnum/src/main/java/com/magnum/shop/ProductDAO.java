package com.magnum.shop;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAO {
    private final Connection connection;

    public ProductDAO(Connection connection) {
        this.connection = connection;
    }

    // Helper to map ResultSet to Product
    private Product mapRow(ResultSet rs) throws SQLException {
        Category cat = new Category(rs.getLong("cat_id"), rs.getString("cat_name"));
        return new Product(
                rs.getLong("p_id"),
                rs.getString("p_name"),
                rs.getDouble("p_price"),
                rs.getInt("p_quantity"),
                cat
        );
    }

    public List<Product> findAll() throws SQLException {
        List<Product> products = new ArrayList<>();
        // Join with Category to populate the category object
        String sql = "SELECT p.id as p_id, p.name as p_name, p.price as p_price, p.quantity as p_quantity, " +
                "c.id as cat_id, c.name as cat_name " +
                "FROM product p " +
                "LEFT JOIN category c ON p.category_id = c.id " +
                "ORDER BY p.id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                products.add(mapRow(rs));
            }
        }
        return products;
    }

    public List<Product> findByCategoryId(long categoryId) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.id as p_id, p.name as p_name, p.price as p_price, p.quantity as p_quantity, " +
                "c.id as cat_id, c.name as cat_name " +
                "FROM product p " +
                "JOIN category c ON p.category_id = c.id " +
                "WHERE p.category_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, categoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRow(rs));
                }
            }
        }
        return products;
    }

    public Optional<Product> findById(long id) throws SQLException {
        String sql = "SELECT p.id as p_id, p.name as p_name, p.price as p_price, p.quantity as p_quantity, " +
                "c.id as cat_id, c.name as cat_name " +
                "FROM product p " +
                "LEFT JOIN category c ON p.category_id = c.id " +
                "WHERE p.id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    public void save(Product product) throws SQLException {
        if (product.getId() == 0) {
            // INSERT
            String sql = "INSERT INTO product (name, price, quantity, category_id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, product.getName());
                pstmt.setDouble(2, product.getPrice());
                pstmt.setInt(3, product.getQuantity());
                pstmt.setLong(4, product.getCategory().getId());
                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        product.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } else {
            // UPDATE (Usually we only update quantity in this app, but let's update all)
            String sql = "UPDATE product SET name=?, price=?, quantity=?, category_id=? WHERE id=?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, product.getName());
                pstmt.setDouble(2, product.getPrice());
                pstmt.setInt(3, product.getQuantity());
                pstmt.setLong(4, product.getCategory().getId());
                pstmt.setLong(5, product.getId());
                pstmt.executeUpdate();
            }
        }
    }
}