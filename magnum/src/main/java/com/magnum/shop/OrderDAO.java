package com.magnum.shop;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class OrderDAO {
    private final Connection connection;

    public OrderDAO(Connection connection) {
        this.connection = connection;
    }

    // Transactional method to create an order
    public void createOrder(User user, Map<Product, Integer> cart) throws SQLException {
        connection.setAutoCommit(false); // Start Transaction
        try {
            // 1. Calculate Total
            double total = cart.entrySet().stream()
                    .mapToDouble(e -> e.getKey().getPrice() * e.getValue())
                    .sum();

            // 2. Insert Order
            long orderId = 0;
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO orders (user_id, total_price) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, user.getId());
                ps.setDouble(2, total);
                ps.executeUpdate();
                var rs = ps.getGeneratedKeys();
                if (rs.next()) orderId = rs.getLong(1);
            }

            // 3. Insert Items & Update Stock
            String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, price_at_purchase) VALUES (?, ?, ?, ?)";
            String updateStockSql = "UPDATE product SET quantity = quantity - ? WHERE id = ?";

            try (PreparedStatement itemPs = connection.prepareStatement(itemSql);
                 PreparedStatement stockPs = connection.prepareStatement(updateStockSql)) {

                for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
                    Product p = entry.getKey();
                    int qty = entry.getValue();

                    // Add to batch: Insert Item
                    itemPs.setLong(1, orderId);
                    itemPs.setLong(2, p.getId());
                    itemPs.setInt(3, qty);
                    itemPs.setDouble(4, p.getPrice());
                    itemPs.addBatch();

                    // Add to batch: Update Stock
                    stockPs.setInt(1, qty);
                    stockPs.setLong(2, p.getId());
                    stockPs.addBatch();
                }
                itemPs.executeBatch();
                stockPs.executeBatch();
            }
            connection.commit(); // Commit Transaction
        } catch (SQLException e) {
            connection.rollback(); // Rollback on error
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // COMPLEX JOIN IMPLEMENTATION
    public void getFullOrderDescription(long orderId) {
        String sql = "SELECT o.id as order_id, o.created_at, o.total_price, " +
                "u.username, " +
                "oi.quantity, oi.price_at_purchase, " +
                "p.name as product_name " +
                "FROM orders o " +
                "JOIN users u ON o.user_id = u.id " +
                "JOIN order_items oi ON oi.order_id = o.id " +
                "JOIN product p ON oi.product_id = p.id " +
                "WHERE o.id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            ResultSet rs = ps.executeQuery();

            boolean headerPrinted = false;
            while (rs.next()) {
                if (!headerPrinted) {
                    System.out.println("\n🧾 Order #" + rs.getLong("order_id"));
                    System.out.println("👤 Buyer: " + rs.getString("username"));
                    System.out.println("📅 Date: " + rs.getTimestamp("created_at"));
                    System.out.println("------------------------------------------------");
                    headerPrinted = true;
                }
                System.out.printf(" - %-20s x%d  ($%.2f)\n",
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price_at_purchase"));
            }
            if (headerPrinted) System.out.println("------------------------------------------------");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}