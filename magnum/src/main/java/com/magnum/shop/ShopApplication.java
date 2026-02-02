package com.magnum.shop;

import java.util.*;

public class ShopApplication {
    // Session state
    private static User currentUser = null;
    private static final Scanner sc = new Scanner(System.in);

    // DAOs via Factory
    private static final ProductDAO productDAO = DAOFactory.getProductDAO();
    private static final OrderDAO orderDAO = DAOFactory.getOrderDAO();
    private static final UserDAO userDAO = DAOFactory.getUserDAO();

    public static void main(String[] args) {
        System.out.println("👋 Welcome to Shop v2.0 (Solid, Patterns, Secure)");

        while (true) {
            if (currentUser == null) {
                loginMenu();
            } else {
                mainMenu();
            }
        }
    }

    private static void loginMenu() {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Username: ");
        String user = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();

        userDAO.login(user, pass).ifPresentOrElse(
                (u) -> {
                    currentUser = u;
                    System.out.println("✅ Login successful! Welcome " + u.getUsername());
                },
                () -> System.out.println("❌ Invalid credentials") // Lambda
        );
    }

    private static void mainMenu() {
        System.out.println("\n--- MAIN MENU (" + currentUser.getUsername() + ") ---");
        System.out.println("1. List Products");
        System.out.println("2. Buy Product");

        // Secured Endpoint: Only Admin sees this
        if (currentUser.isAdmin()) {
            System.out.println("3. [ADMIN] View Order Details");
        }

        System.out.println("4. Logout");
        System.out.print("Choice: ");

        String choice = sc.nextLine();

        try {
            switch (choice) {
                case "1" -> listProducts(); // Lambda-style switch (Java 14+)
                case "2" -> buyProduct();
                case "3" -> {
                    if (currentUser.isAdmin()) viewOrderDetails();
                    else System.out.println("⛔ Access Denied.");
                }
                case "4" -> currentUser = null;
                default -> System.out.println("Invalid option");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void listProducts() throws Exception {
        // Lambda Usage: forEach
        productDAO.findAll().forEach(p ->
                System.out.printf("%d. %s - $%.2f (Stock: %d)\n", p.getId(), p.getName(), p.getPrice(), p.getQuantity())
        );
    }

    private static void buyProduct() throws Exception {
        System.out.print("Enter Product ID: ");
        // Data Validation: Parse Long
        long pid;
        try { pid = Long.parseLong(sc.nextLine()); } catch (Exception e) { System.out.println("Invalid ID"); return; }

        Optional<Product> pOpt = productDAO.findById(pid);

        // Lambda Usage: ifPresent
        pOpt.ifPresentOrElse(product -> {
            System.out.print("Enter Quantity: ");
            int qty = Integer.parseInt(sc.nextLine());

            // Domain Logic Validation
            if (qty <= 0 || qty > product.getQuantity()) {
                System.out.println("❌ Invalid quantity.");
                return;
            }

            Map<Product, Integer> cart = new HashMap<>();
            cart.put(product, qty);

            try {
                orderDAO.createOrder(currentUser, cart);
                System.out.println("✅ Purchase successful!");
            } catch (Exception e) {
                System.out.println("Transaction failed: " + e.getMessage());
            }

        }, () -> System.out.println("Product not found."));
    }

    private static void viewOrderDetails() {
        System.out.print("Enter Order ID to inspect: ");
        long oid = Long.parseLong(sc.nextLine());
        orderDAO.getFullOrderDescription(oid);
    }
}