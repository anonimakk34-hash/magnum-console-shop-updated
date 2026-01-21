package com.magnum.shop;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseLoader implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public DatabaseLoader(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if (categoryRepository.count() == 0) {


            Category dairy = new Category();
            dairy.setName("Dairy");
            categoryRepository.save(dairy);

            Product milk = new Product();
            milk.setName("Magnum Milk 2.5%");
            milk.setPrice(450.0);
            milk.setQuantity(50);
            milk.setCategory(dairy);
            productRepository.save(milk);

            System.out.println("✅ Database initialized with Dairy and Milk!");
        }
    }
}