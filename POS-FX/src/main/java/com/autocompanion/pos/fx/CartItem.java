package com.autocompanion.pos.fx;

public class CartItem {

    private String productName;
    private double price;
    private int quantity;

    public double getSubtotal() {
        return price * quantity;
    }

    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}