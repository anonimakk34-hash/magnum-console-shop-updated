package com.magnum.shop;

public class User {
    private long id;
    private String username;
    private String password;
    private String role; // "ADMIN" or "CUSTOMER"

    public User(long id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.role);
    }

    // Getters...
    public long getId() { return id; }
    public String getUsername() { return username; }
}