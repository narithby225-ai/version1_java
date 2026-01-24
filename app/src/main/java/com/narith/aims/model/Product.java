package com.narith.aims.model;

public class Product {
    private int id;
    private String name;
    private String sku;
    private String category;
    private int quantity;      // ចំនួនសរុបក្នុងស្តុក (Stock)
    private int reorderPoint;
    private double price;
    private String imageUri;
    private String dateAdded;
    private String expiryDate;

    // --- Field សម្រាប់ Cart (សំខាន់សម្រាប់ប៊ូតុង + / -) ---
    private int cartQuantity = 1;

    // Constructor ពេញលេញ (សម្រាប់ទាញចេញពី Database)
    public Product(int id, String name, String sku, String category, int quantity, int reorderPoint, double price, String imageUri, String dateAdded, String expiryDate) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.category = category;
        this.quantity = quantity;
        this.reorderPoint = reorderPoint;
        this.price = price;
        this.imageUri = imageUri;
        this.dateAdded = dateAdded;
        this.expiryDate = expiryDate;
    }

    // Constructor សម្រាប់បង្កើតថ្មី (Add New)
    public Product(String name, String sku, String category, int quantity, int reorderPoint, double price, String imageUri, String dateAdded, String expiryDate) {
        this.name = name;
        this.sku = sku;
        this.category = category;
        this.quantity = quantity;
        this.reorderPoint = reorderPoint;
        this.price = price;
        this.imageUri = imageUri;
        this.dateAdded = dateAdded;
        this.expiryDate = expiryDate;
    }

    // --- Getters & Setters ទូទៅ ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getReorderPoint() { return reorderPoint; }
    public void setReorderPoint(int reorderPoint) { this.reorderPoint = reorderPoint; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }

    // *** មុខងារដោះស្រាយ Error ក្នុង CartAdapter: បន្ថែម Method នេះ ***
    public String getImageRes() {
        return (this.imageUri != null) ? this.imageUri : "";
    }

    public String getDateAdded() { return dateAdded; }
    public void setDateAdded(String dateAdded) { this.dateAdded = dateAdded; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    // --- Methods សម្រាប់ Cart ---
    public int getCartQuantity() {
        return cartQuantity;
    }

    public void setCartQuantity(int cartQuantity) {
        this.cartQuantity = cartQuantity;
    }

    // --- Methods សម្រាប់ពិនិត្យស្ថានភាពទំនិញ ---
    public String getStatus() {
        return com.narith.aims.database.DatabaseHelper.getProductStatus(this.dateAdded, this.expiryDate);
    }

    public boolean isNew() {
        return "NEW".equals(getStatus());
    }

    public boolean isExpiringSoon() {
        return "EXPIRING_SOON".equals(getStatus());
    }

    public boolean isExpired() {
        return "EXPIRED".equals(getStatus());
    }

    public boolean isOld() {
        return "OLD".equals(getStatus());
    }

    public long getDaysUntilExpiry() {
        return com.narith.aims.database.DatabaseHelper.getDaysDifference(
            com.narith.aims.database.DatabaseHelper.getCurrentDate(), 
            this.expiryDate
        );
    }
}