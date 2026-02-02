package com.magnum.shop;

import java.sql.Connection;

public class DAOFactory {
    private static final Connection conn = DatabaseConnection.getInstance().getConnection();

    public static ProductDAO getProductDAO() {
        return new ProductDAO(conn);
    }

    public static CategoryDAO getCategoryDAO() {
        return new CategoryDAO(conn);
    }

    public static UserDAO getUserDAO() {
        return new UserDAO(conn);
    }

    public static OrderDAO getOrderDAO() {
        return new OrderDAO(conn);
    }
}
