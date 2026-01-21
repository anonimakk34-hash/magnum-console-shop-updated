package com.magnum.shop;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@SpringBootApplication
public class ShopApplication implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ShopApplication(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n👋 Welcome to the Magnum Console Shop (Main App)!");

        while (true) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Show all products");
            System.out.println("2. Show products by Category");
            System.out.println("3. Buy product (Smart Checkout)");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine();

            if (choice.equals("1")) {
                showAllProducts();
            } else if (choice.equals("2")) {
                showByCategory(sc);
            } else if (choice.equals("3")) {
                buyProduct(sc);
            } else if (choice.equals("4")) {
                System.out.println("Goodbye!");
                System.exit(0);
            } else {
                System.out.println("Invalid option, please try again.");
            }
        }
    }

    // --- 1. SHOW ALL ---
    private void showAllProducts() {
        List<Product> products = productRepository.findAll();
        printProductList(products, "All Products");
    }

    // --- 2. SHOW BY CATEGORY ---
    private void showByCategory(Scanner sc) {
        System.out.println("\n📂 SELECT A CATEGORY:");
        printCategoryList();

        System.out.print("Enter Category ID: ");
        try {
            Long catId = Long.parseLong(sc.nextLine());
            List<Product> products = productRepository.findByCategoryId(catId);

            if (products.isEmpty()) {
                System.out.println("⚠️ This category is empty or does not exist.");
            } else {
                String categoryName = products.get(0).getCategory().getName();
                printProductList(products, categoryName + " Section");
            }
        } catch (Exception e) {
            System.out.println("❌ Invalid input.");
        }
    }

    // --- 3. BUY PRODUCT (SMART CHECKOUT) ---
    private void buyProduct(Scanner sc) {
        System.out.println("\n💳 --- CHECKOUT START ---");

        // Step 1: Show Categories
        System.out.println("📂 SELECT A CATEGORY:");
        printCategoryList();

        System.out.print("Select Category ID (or 0 to exit): ");
        try {
            Long catId = Long.parseLong(sc.nextLine());
            if (catId == 0) return; // User cancelled

            // Step 2: Show Products in that category
            List<Product> products = productRepository.findByCategoryId(catId);
            if (products.isEmpty()) {
                System.out.println("❌ This category is empty. Cannot buy anything.");
                return;
            }

            // Print products so user sees the IDs
            printProductList(products, "Pick a Product");

            // Step 3: Ask for Product ID
            System.out.print("Step 2: Enter Product ID to buy (or 0 to exit): ");
            Long prodId = Long.parseLong(sc.nextLine());

            if (prodId == 0) return; // User cancelled

            // Step 4: Validate Product
            Optional<Product> productOptional = productRepository.findById(prodId);

            if (productOptional.isEmpty()) {
                System.out.println("❌ Error: Product ID #" + prodId + " does not exist!");
                return;
            }

            Product product = productOptional.get();

            // Step 5: Ask Quantity
            System.out.print("Step 3: Enter quantity: ");
            int amountToBuy = Integer.parseInt(sc.nextLine());

            // Step 6: Process
            processTransaction(product, amountToBuy);

        } catch (NumberFormatException e) {
            System.out.println("❌ Error: Please enter valid numbers only!");
        }
        // NOTE: The old code is DELETED from here. The method ends now.
    }

    // --- LOGIC: TRANSACTION CALCULATOR ---
    private void processTransaction(Product product, int amountToBuy) {
        // 1. Check Stock
        if (amountToBuy > product.getQuantity()) {
            System.out.println("❌ Error: Not enough stock! We only have " + product.getQuantity() + " left.");
            return;
        }

        // 2. Calculate Price
        double totalPrice = product.getPrice() * amountToBuy;


        // 4. Update Database
        int newStock = product.getQuantity() - amountToBuy;
        product.setQuantity(newStock);
        productRepository.save(product);

        // 5. Print Receipt
        System.out.println("\n🧾 --- RECEIPT ---");
        System.out.printf("Item: %s\n", product.getName());
        System.out.printf("Qty:  %d\n", amountToBuy);

        System.out.printf("TOTAL: %.2f ₸\n", totalPrice);
        System.out.println("📦 Remaining Stock: " + newStock);
        System.out.println("-------------------");
    }

    // --- HELPER METHODS ---
    private void printCategoryList() {
        List<Category> categories = categoryRepository.findAll();
        for (Category c : categories) {
            System.out.printf(" [%d] %s\n", c.getId(), c.getName());
        }
    }

    private void printProductList(List<Product> products, String title) {
        System.out.println("\n🛒 " + title + ":");
        System.out.println("-----------------------------------------------------");
        for (Product p : products) {
            String catName = (p.getCategory() != null) ? p.getCategory().getName() : "Unknown";
            System.out.printf("#%-2d | %-25s | %8.2f ₸ | Stock: %-3d\n",
                    p.getId(), p.getName(), p.getPrice(), p.getQuantity());
        }
        System.out.println("-----------------------------------------------------");
    }
}