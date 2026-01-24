package com.narith.aims.model;

public class Product {
    private int id;
    private String name;
    private String sku;
    private String category;
    private int quantity;
    private int reorderPoint;
    private double price;
    private String imageUri; // កែពី int imageResId មក String

    public Product(String name, String sku, String category, int quantity, int reorderPoint, double price, String imageUri) {
        this.name = name;
        this.sku = sku;
        this.category = category;
        this.quantity = quantity;
        this.reorderPoint = reorderPoint;
        this.price = price;
        this.imageUri = imageUri;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public String getSku() { return sku; }
    public String getCategory() { return category; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getReorderPoint() { return reorderPoint; }
    public double getPrice() { return price; }
    public String getImageUri() { return imageUri; } // Getter ថ្មី
}