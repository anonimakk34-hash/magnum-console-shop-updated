package com.magnum.shop;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private long id;
    private String name;
    // We don't strictly need the list of products for this specific console logic,
    // but we'll keep the field structure similar to the original.
    private List<Product> products = new ArrayList<>();

    public Category() {}

    public Category(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
}
