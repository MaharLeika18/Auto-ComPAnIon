package com.autocompanion.pos.fx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.MiniDev.DBConnection.DBConnection;
import org.MiniDev.OOP.Product;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class DashboardController {
    
    @FXML private FlowPane productGrid;
    @FXML private FlowPane cartGrid;
    @FXML private Label totalLabel;
    @FXML private TextField searchField;
    
    private ObservableList<Product> products = FXCollections.observableArrayList();
    private ObservableList<CartItem> cart = FXCollections.observableArrayList();
    private DecimalFormat df = new DecimalFormat("#,###.00");
    private int currentTellerId = 1;
    
    @FXML
    private void initialize() {
        loadProductsFromDatabase();
        searchField.textProperty().addListener((obs, old, val) -> filterProducts(val));
        cart.addListener((javafx.collections.ListChangeListener.Change<? extends CartItem> c) -> {
            updateCartGrid();
            updateTotal();
        });
    }
    
    private void loadProductsFromDatabase() {
        String sql = "SELECT Food_Serial_Number, Food_Name, Food_Price, Stock_Available_Cnt FROM Food_Lists WHERE Stock_Available_Cnt > 0";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            products.clear();
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getString("Food_Serial_Number"));  // C0000001, C0000002, etc.
                p.setName(rs.getString("Food_Name"));         // Axis Y Big, Axis Y Small, etc.
                p.setPrice(rs.getDouble("Food_Price"));       // 40000, 6800, etc.
                p.setStockAvailableNumber(rs.getInt("Stock_Available_Cnt")); // 3, 8, 1
                products.add(p);
            }
            
            System.out.println("Loaded " + products.size() + " products from Food_Lists");
            displayProductCards();
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Could not load products: " + e.getMessage());
            addSampleProducts();
        }
    }
    
    private void addSampleProducts() {
        Product p1 = new Product();
        p1.setId("SAMPLE001");
        p1.setName("Sample Product 1");
        p1.setPrice(99.99);
        p1.setStockAvailableNumber(10);
        
        Product p2 = new Product();
        p2.setId("SAMPLE002");
        p2.setName("Sample Product 2");
        p2.setPrice(49.99);
        p2.setStockAvailableNumber(25);
        
        products.addAll(p1, p2);
        displayProductCards();
    }
    
    private void displayProductCards() {
        productGrid.getChildren().clear();
        for (Product p : products) {
            VBox card = CardFactory.createProductCard(p, () -> addToCart(p));
            productGrid.getChildren().add(card);
        }
    }
    
    private void filterProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            displayProductCards();
            return;
        }
        
        String lower = keyword.toLowerCase();
        productGrid.getChildren().clear();
        for (Product p : products) {
            if (p.getName().toLowerCase().contains(lower)) {
                VBox card = CardFactory.createProductCard(p, () -> addToCart(p));
                productGrid.getChildren().add(card);
            }
        }
    }
    
    private void addToCart(Product p) {
        if (p.getStockAvailableNumber() <= 0) {
            showAlert("Out of Stock", p.getName() + " is out of stock!");
            return;
        }
        
        for (CartItem item : cart) {
            if (item.getProductId().equals(p.getId())) {
                if (item.getQuantity() + 1 > p.getStockAvailableNumber()) {
                    showAlert("Stock Limit", "Only " + p.getStockAvailableNumber() + " available!");
                    return;
                }
                item.setQuantity(item.getQuantity() + 1);
                updateCartGrid();
                updateTotal();
                return;
            }
        }
        cart.add(new CartItem(p.getId(), p.getName(), p.getPrice(), 1));
    }
    
    private void updateCartGrid() {
        cartGrid.getChildren().clear();
        for (CartItem item : cart) {
            VBox card = CardFactory.createCartCard(
                item,
                () -> { 
                    if (item.getQuantity() > 1) { 
                        item.setQuantity(item.getQuantity() - 1); 
                        updateCartGrid(); 
                        updateTotal(); 
                    } 
                },
                () -> { 
                    Product original = findProductById(item.getProductId()); 
                    if (original != null && item.getQuantity() + 1 <= original.getStockAvailableNumber()) { 
                        item.setQuantity(item.getQuantity() + 1); 
                        updateCartGrid(); 
                        updateTotal(); 
                    } else { 
                        showAlert("Stock Limit", "Cannot add more of " + item.getProductName()); 
                    } 
                },
                () -> { 
                    cart.remove(item); 
                }
            );
            cartGrid.getChildren().add(card);
        }
    }
    
    private Product findProductById(String id) {
        for (Product p : products) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }
    
    @FXML private void clearCart() { 
        cart.clear(); 
    }
    
    @FXML
    private void checkout() {
        if (cart.isEmpty()) {
            showAlert("Cart Empty", "Please add items to cart first!");
            return;
        }
        
        double total = cart.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
        
        StringBuilder orderSummary = new StringBuilder("Order Summary:\n\n");
        for (CartItem item : cart) {
            orderSummary.append(item.getQuantity()).append(" x ").append(item.getProductName())
                       .append(" = ₱").append(df.format(item.getSubtotal())).append("\n");
        }
        orderSummary.append("\nTotal: ₱").append(df.format(total));
        
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Checkout");
        alert.setHeaderText("Confirm Order");
        alert.setContentText(orderSummary.toString());
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                saveOrderToDatabase();
            }
        });
    }
    
    private void saveOrderToDatabase() {
        double total = cart.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String orderDate = LocalDateTime.now().format(formatter);
        
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            // Insert into order_lists (adjust table name if needed)
            String orderSql = "INSERT INTO order_lists (order_date, total_amount, status, teller_id) VALUES (?, ?, 'Completed', ?)";
            PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setString(1, orderDate);
            orderStmt.setDouble(2, total);
            orderStmt.setInt(3, currentTellerId);
            orderStmt.executeUpdate();
            
            // Update stock in Food_Lists
            String updateStockSql = "UPDATE Food_Lists SET Stock_Available_Cnt = Stock_Available_Cnt - ? WHERE Food_Serial_Number = ? AND Stock_Available_Cnt >= ?";
            PreparedStatement stockStmt = conn.prepareStatement(updateStockSql);
            
            for (CartItem item : cart) {
                stockStmt.setInt(1, item.getQuantity());
                stockStmt.setString(2, item.getProductId());
                stockStmt.setInt(3, item.getQuantity());
                stockStmt.addBatch();
            }
            
            stockStmt.executeBatch();
            conn.commit();
            
            showAlert("Success!", "Order placed successfully!\nTotal: ₱" + df.format(total));
            
            // Clear cart and refresh products
            cart.clear();
            loadProductsFromDatabase();
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Checkout failed: " + e.getMessage());
        }
    }
    
    @FXML 
    private void searchProducts() { 
        filterProducts(searchField.getText()); 
    }
    
    @FXML 
    private void refreshProducts() { 
        loadProductsFromDatabase(); 
    }
    
    private void updateTotal() {
        double total = cart.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
        totalLabel.setText("₱" + df.format(total));
    }
    
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    // Cart Item Class
    public static class CartItem {
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
}