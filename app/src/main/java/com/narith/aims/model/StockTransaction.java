package com.narith.aims.model;

public class StockTransaction {
    private int id;
    private int productId;
    private String productName;
    private String type; // IMPORT, EXPORT, SALE, PURCHASE
    private int quantity;
    private double unitPrice;
    private double totalAmount;
    private String date;
    private String note;
    private String userName;

    public StockTransaction(int id, int productId, String productName, String type, 
                           int quantity, double unitPrice, double totalAmount, 
                           String date, String note, String userName) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.type = type;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = totalAmount;
        this.date = date;
        this.note = note;
        this.userName = userName;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}
