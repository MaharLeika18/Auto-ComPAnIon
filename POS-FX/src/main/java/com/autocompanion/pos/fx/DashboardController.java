package com.autocompanion.pos.fx;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.MiniDev.DBConnection.DBConnection;
import org.MiniDev.OOP.Product;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DashboardController {

    // ── Product area ──────────────────────────────────────────────
    @FXML private ScrollPane productScrollPane;

    // ── Receipt panel ─────────────────────────────────────────────
    @FXML private TextField receiptNoField;
    @FXML private Label     datelb;
    @FXML private Label     timelb;

    // ── Summary ───────────────────────────────────────────────────
    @FXML private Label     itemQuantityLabel;
    @FXML private Label     subtotalLabel;
    @FXML private TextField discountField;
    @FXML private Label     totalLabel;

    // ── Cart ──────────────────────────────────────────────────────
    @FXML private ScrollPane cartScrollPane;

    // ── Controls ──────────────────────────────────────────────────
    @FXML private TextField searchField;
    @FXML private Button    processPaymentBtn;
    @FXML private Button    inventoryButton;
    @FXML private Button    paiButton;

    // ── Data ──────────────────────────────────────────────────────
    private final ObservableList<Product>  products = FXCollections.observableArrayList();
    private final ObservableList<CartItem> cart     = FXCollections.observableArrayList();
    private final DecimalFormat df = new DecimalFormat("#,###.00");

    private FlowPane productGrid;
    private VBox     cartBox;

    // ─────────────────────────────── FXML paths ───────────────────────────────
    private static final String FXML_BASE        = "/com/autocompanion/pos/fx/";
    private static final String FXML_OWNER_LOGIN = FXML_BASE + "OwnerLogin.fxml";

    // ──────────────────────────────── INIT ────────────────────────────────────
    @FXML
    private void initialize() {
        productGrid = new FlowPane(10, 10);
        productScrollPane.setContent(productGrid);
        productScrollPane.setFitToWidth(true);

        cartBox = new VBox(8);
        cartScrollPane.setContent(cartBox);
        cartScrollPane.setFitToWidth(true);

        startClock();
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterProducts(newVal));
        setupDiscountListener();
        setupTotalClick();
        receiptNoField.setText(generateReceiptNumber());
        loadProductsFromDatabase();
        updateSummary();
    }

    // ─────────────────────────────── TOTAL CLICK ──────────────────────────────
    private void setupTotalClick() {
        totalLabel.setOnMouseClicked(event -> {
            double subtotal = cart.stream().mapToDouble(CartItem::getSubtotal).sum();
            double currentTotal = subtotal - getDiscount();

            TextInputDialog dialog = new TextInputDialog(String.format("%.2f", currentTotal));
            dialog.setTitle("Adjust Total");
            dialog.setHeaderText("Enter new total amount");
            dialog.setContentText("Total (₱):");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(value -> {
                try {
                    double newTotal = Double.parseDouble(value.trim());
                    if (newTotal >= 0 && newTotal <= subtotal) {
                        double newDiscount = subtotal - newTotal;
                        boolean isPercentage = discountField.getText().contains("%");
                        if (isPercentage && subtotal > 0) {
                            double newPercentage = (newDiscount / subtotal) * 100;
                            discountField.setText(String.format("%.2f", newPercentage) + "%");
                        } else {
                            discountField.setText(String.format("%.2f", newDiscount));
                        }
                        updateSummary();
                    } else {
                        showAlert("Invalid Total", "Total must be between ₱0 and ₱" + String.format("%.2f", subtotal));
                    }
                } catch (NumberFormatException e) {
                    showAlert("Error", "Please enter a valid number");
                }
            });
        });
        totalLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold; -fx-cursor: hand;");
        totalLabel.setUnderline(true);
    }

    // ─────────────────────────────── DISCOUNT ─────────────────────────────────
    private void setupDiscountListener() {
        discountField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) { updateSummary(); return; }
            try {
                double d = Double.parseDouble(newVal);
                if (d < 0) discountField.setText("0");
                else        updateSummary();
            } catch (NumberFormatException e) {
                discountField.setText(oldVal);
            }
        });
    }

    private double getDiscount() {
        try {
            String text = discountField.getText();
            return (text == null || text.isEmpty()) ? 0 : Double.parseDouble(text);
        } catch (NumberFormatException e) { return 0; }
    }

    // ──────────────────────────────── CLOCK ───────────────────────────────────
    private void startClock() {
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("hh:mm:ss a");
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            datelb.setText(LocalDate.now().format(dateFmt));
            timelb.setText(LocalTime.now().format(timeFmt));
        }));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private String generateReceiptNumber() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    // ─────────────────────────────── DATABASE ─────────────────────────────────
    private void loadProductsFromDatabase() {
        String sql = "SELECT Food_Serial_Number, Food_Name, Food_Price, Stock_Available_Cnt "
                   + "FROM Food_Lists WHERE Stock_Available_Cnt > 0";
        try (Connection conn = DBConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {
            products.clear();
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getString("Food_Serial_Number"));
                p.setName(rs.getString("Food_Name"));
                p.setPrice(rs.getDouble("Food_Price"));
                p.setStockAvailableNumber(rs.getInt("Stock_Available_Cnt"));
                products.add(p);
            }
            displayProductCards();
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load products:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    // ─────────────────────────────── PRODUCTS ─────────────────────────────────
    private void displayProductCards() {
        productGrid.getChildren().clear();
        for (Product p : products)
            productGrid.getChildren().add(CardFactory.createProductCard(p, () -> addToCart(p)));
    }

    private void filterProducts(String key) {
        productGrid.getChildren().clear();
        for (Product p : products)
            if (key == null || key.isEmpty() || p.getName().toLowerCase().contains(key.toLowerCase()))
                productGrid.getChildren().add(CardFactory.createProductCard(p, () -> addToCart(p)));
    }

    // ──────────────────────────────── CART ────────────────────────────────────
    private void addToCart(Product p) {
        int stock = p.getStockAvailableNumber();
        for (CartItem c : cart) {
            if (c.getProductId().equals(p.getId())) {
                if (c.getQuantity() < stock) c.setQuantity(c.getQuantity() + 1);
                refreshCartUI();
                return;
            }
        }
        cart.add(new CartItem(p.getId(), p.getName(), p.getPrice(), 1, stock));
        refreshCartUI();
    }

    private void refreshCartUI() {
        cart.removeIf(item -> item.getQuantity() <= 0);
        cartBox.getChildren().clear();
        for (CartItem item : cart) {
            cartBox.getChildren().add(CardFactory.createCartRow(
                item,
                () -> { item.setQuantity(item.getQuantity() - 1); refreshCartUI(); },
                () -> { item.setQuantity(item.getQuantity() + 1); refreshCartUI(); },
                (q) -> { item.setQuantity(q);                     refreshCartUI(); },
                () -> { cart.remove(item);                        refreshCartUI(); }
            ));
        }
        updateSummary();
    }

    // ─────────────────────────────── SUMMARY ──────────────────────────────────
    private void updateSummary() {
        int    qty      = cart.stream().mapToInt(CartItem::getQuantity).sum();
        double subtotal = cart.stream().mapToDouble(CartItem::getSubtotal).sum();
        double total    = Math.max(subtotal - getDiscount(), 0);
        itemQuantityLabel.setText(String.valueOf(qty));
        subtotalLabel.setText("₱" + df.format(subtotal));
        totalLabel.setText("₱" + df.format(total));
    }

    // ─────────────────────────────── PAYMENT ──────────────────────────────────
    @FXML
    private void processPayment() {
        if (cart.isEmpty()) {
            showAlert("Checkout", "Cart is empty. Please add items before processing payment.");
            return;
        }

        // Show payment method selection
        Alert paymentAlert = new Alert(Alert.AlertType.CONFIRMATION);
        paymentAlert.setTitle("Select Payment Method");
        paymentAlert.setHeaderText("Choose how the customer will pay:");
        
        ButtonType btnCash = new ButtonType("Cash");
        ButtonType btnEWallet = new ButtonType("E-Wallet");
        ButtonType btnBank = new ButtonType("Bank Transfer");
        ButtonType btnCancel = ButtonType.CANCEL;
        
        paymentAlert.getButtonTypes().setAll(btnCash, btnEWallet, btnBank, btnCancel);
        
        Optional<ButtonType> result = paymentAlert.showAndWait();
        if (!result.isPresent() || result.get() == btnCancel) {
            return;
        }

        String paymentMethod;
        if (result.get() == btnCash) {
            paymentMethod = "CASH";
        } else if (result.get() == btnEWallet) {
            paymentMethod = "E-WALLET";
        } else {
            paymentMethod = "BANK";
        }

        // Calculate totals
        double subtotal = cart.stream().mapToDouble(CartItem::getSubtotal).sum();
        double discount = getDiscount();
        double total = Math.max(subtotal - discount, 0);
        String receiptNo = receiptNoField.getText();

        // Save transaction to database
        saveTransaction(receiptNo, total, paymentMethod, "PENDING");

        // Update stock for each item
        updateStockLevels();

        // Clear cart and generate new receipt
        cart.clear();
        refreshCartUI();
        receiptNoField.setText(generateReceiptNumber());
        
        showAlert("Payment Complete", 
            "Payment of ₱" + df.format(total) + " received via " + 
            (paymentMethod.equals("E-WALLET") ? "E-Wallet" : 
             paymentMethod.equals("BANK") ? "Bank Transfer" : "Cash") + 
            "\n\nReceipt: " + receiptNo + "\n\nTransaction saved as PENDING. Please approve in Logs.");
    }

    private void saveTransaction(String receiptNo, double total, String paymentMethod, String status) {
        String sql = "INSERT INTO transaction_log (transaction_date, receipt_num, total_amount, payment_method, status) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, Integer.parseInt(receiptNo.replace("REC-", "")));
            ps.setDouble(3, total);
            ps.setString(4, paymentMethod);
            ps.setString(5, status);
            
            ps.executeUpdate();
            
            // Get the generated transaction ID
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long transactionId = rs.getLong(1);
                    saveTransactionItems(transactionId);
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to save transaction:\n" + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid receipt number format");
            e.printStackTrace();
        }
    }

    private void saveTransactionItems(long transactionId) {
        String sql = "INSERT INTO transaction_items (transaction_id, product_id, batch_id, quantity_sold, " +
                     "unit_selling_price, unit_cost_at_sale, discount_applied, total_sale_value, total_cost) " +
                     "SELECT ?, p.Food_Main_Counter_ID, pb.batch_id, ?, p.Food_Price, " +
                     "COALESCE(pb.cost_per_unit, 0), 0, ?, ? " +
                     "FROM Food_Lists p " +
                     "LEFT JOIN (SELECT batch_id, product_id, cost_per_unit FROM product_batches " +
                     "WHERE batch_id = (SELECT MIN(batch_id) FROM product_batches WHERE product_id = p.Food_Main_Counter_ID AND quantity_remaining > 0)) pb " +
                     "ON pb.product_id = p.Food_Main_Counter_ID " +
                     "WHERE p.Food_Serial_Number = ?";
        
        try (Connection conn = DBConnection.getConnection()) {
            for (CartItem item : cart) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setLong(1, transactionId);
                    ps.setInt(2, item.getQuantity());
                    ps.setDouble(3, item.getSubtotal());
                    ps.setDouble(4, item.getQuantity() * item.getPrice() * 0.7); // Approximate cost as 70% of price
                    ps.setString(5, item.getProductId());
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to save transaction items:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateStockLevels() {
        String sql = "UPDATE Food_Lists SET Stock_Available_Cnt = Stock_Available_Cnt - ? " +
                     "WHERE Food_Serial_Number = ?";
        
        try (Connection conn = DBConnection.getConnection()) {
            for (CartItem item : cart) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, item.getQuantity());
                    ps.setString(2, item.getProductId());
                    ps.executeUpdate();
                }
            }
            // Reload products to reflect new stock levels
            loadProductsFromDatabase();
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to update stock:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    // ─────────────────────────────── NAVIGATION ───────────────────────────────

    /**
     * Inventory and PAI both gate through the owner login screen.
     * The destination string tells OwnerLoginController where to go after success.
     */
    @FXML
    private void openInventory(ActionEvent event) {
        navigateToOwnerLogin(event, "inventory");
    }

    @FXML
    private void openPai(ActionEvent event) {
        navigateToOwnerLogin(event, "pai");
    }

    // FXML aliases
    @FXML private void goToInventory(ActionEvent event) { openInventory(event); }
    @FXML private void goToPai(ActionEvent event)       { openPai(event);       }
    @FXML private void goToDashboard(ActionEvent event) { /* already here */    }
    @FXML private void onFilterChange()                 { filterProducts(searchField.getText()); }

    private void navigateToOwnerLogin(ActionEvent event, String destination) {
        URL resource = getClass().getResource(FXML_OWNER_LOGIN);
        if (resource == null) {
            showAlert("Navigation Error", "Cannot find OwnerLogin.fxml");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            // Tell the owner login screen where to go on success
            OwnerLoginController controller = loader.getController();
            controller.setDestination(destination);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            Throwable cause = e;
            while (cause.getCause() != null) cause = cause.getCause();
            showAlert("Navigation Error",
                "Failed to load OwnerLogin.fxml\n\nRoot cause: "
                + cause.getClass().getSimpleName() + "\n" + cause.getMessage());
            e.printStackTrace();
        }
    }

    // ──────────────────────────────── UTIL ────────────────────────────────────
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ─────────────────────────────── CART ITEM ────────────────────────────────
    public static class CartItem {

        private final String id;
        private final String name;
        private final double price;
        private final int    maxStock;
        private int qty;

        public CartItem(String id, String name, double price, int qty, int maxStock) {
            this.id       = id;
            this.name     = name;
            this.price    = price;
            this.maxStock = maxStock;
            this.qty      = Math.min(qty, maxStock);
        }

        public String getProductId()     { return id;                                      }
        public String getProductName()   { return name;                                    }
        public double getPrice()         { return price;                                   }
        public int    getQuantity()      { return qty;                                     }
        public int    getMaxStock()      { return maxStock;                                }
        public void   setQuantity(int q) { this.qty = Math.min(Math.max(q, 0), maxStock); }
        public double getSubtotal()      { return price * qty;                             }
    }
}