package com.autocompanion.pos.fx;

public class CartItem {
    private String productId;
    private String productName;
    private double price;
    private int quantity;
    
    public CartItem(String id, String name, double price, int qty) {
        this.productId = id;
        this.productName = name;
        this.price = price;
        this.quantity = qty;
    }
    
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int q) { this.quantity = q; }
    public double getSubtotal() { return price * quantity; }
}