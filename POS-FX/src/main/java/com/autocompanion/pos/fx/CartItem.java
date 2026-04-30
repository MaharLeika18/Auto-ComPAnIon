package com.autocompanion.pos.fx;

public class CartItem {

    private final String id;
    private final String name;
    private final double price;
    private final int maxStock; // ← ADD THIS
    private int qty;

    public CartItem(String id, String name, double price, int qty, int maxStock) { // ← add maxStock param
        this.id       = id;
        this.name     = name;
        this.price    = price;
        this.qty      = qty;
        this.maxStock = maxStock; // ← ADD THIS
    }

    public String getProductId()     { return id;       }
    public String getProductName()   { return name;     }
    public double getPrice()         { return price;    }
    public int    getQuantity()      { return qty;      }
    public int    getMaxStock()      { return maxStock; } // ← ADD THIS
    public void   setQuantity(int q) { this.qty = Math.min(q, maxStock); } // ← cap here
    public double getSubtotal()      { return price * qty; }
}