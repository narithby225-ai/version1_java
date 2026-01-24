package com.narith.aims.model;

import java.io.Serializable;

public class Transaction implements Serializable {
    private String id;
    private String productId;
    private String productName;
    private String type; // "STOCK_IN" or "STOCK_OUT"
    private int quantity;
    private long timestamp;

    public Transaction() {}

    public Transaction(String productId, String productName, String type, int quantity, long timestamp) {
        this.productId = productId;
        this.productName = productName;
        this.type = type;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
