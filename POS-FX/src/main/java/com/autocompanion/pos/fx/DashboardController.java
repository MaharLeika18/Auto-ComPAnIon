package com.autocompanion.pos.fx;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
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
    private static final String FXML_BASE      = "/com/autocompanion/pos/fx/";
    private static final String FXML_INVENTORY = FXML_BASE + "Inventory.fxml";
    private static final String FXML_PAI       = FXML_BASE + "Pai.fxml";

    // ──────────────────────────────── INIT ────────────────────────────────────
    @FXML
    private void initialize() {

        // Product grid
        productGrid = new FlowPane(10, 10);
        productScrollPane.setContent(productGrid);
        productScrollPane.setFitToWidth(true);

        // Cart list
        cartBox = new VBox(8);
        cartScrollPane.setContent(cartBox);
        cartScrollPane.setFitToWidth(true);

        startClock();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterProducts(newVal));

        setupDiscountListener();

        receiptNoField.setText(generateReceiptNumber());

        loadProductsFromDatabase();
        updateSummary();
    }

    // ─────────────────────────────── DISCOUNT ─────────────────────────────────
    private void setupDiscountListener() {
        discountField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                updateSummary();
                return;
            }
            try {
                double d = Double.parseDouble(newVal);
                if (d < 0) {
                    discountField.setText("0");
                } else {
                    updateSummary();
                }
            } catch (NumberFormatException e) {
                discountField.setText(oldVal); // revert invalid input
            }
        });
    }

    private double getDiscount() {
        try {
            String text = discountField.getText();
            return (text == null || text.isEmpty()) ? 0 : Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // ──────────────────────────────── CLOCK ───────────────────────────────────
    private void startClock() {
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("hh:mm:ss a");

        Timeline clock = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> {
                datelb.setText(LocalDate.now().format(dateFmt));
                timelb.setText(LocalTime.now().format(timeFmt));
            })
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private String generateReceiptNumber() {
        return "REC-" + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
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
        for (Product p : products) {
            productGrid.getChildren().add(
                CardFactory.createProductCard(p, () -> addToCart(p))
            );
        }
    }

    private void filterProducts(String key) {
        productGrid.getChildren().clear();
        for (Product p : products) {
            if (key == null || key.isEmpty()
                    || p.getName().toLowerCase().contains(key.toLowerCase())) {
                productGrid.getChildren().add(
                    CardFactory.createProductCard(p, () -> addToCart(p))
                );
            }
        }
    }

    // ──────────────────────────────── CART ────────────────────────────────────
    private void addToCart(Product p) {
        for (CartItem c : cart) {
            if (c.getProductId().equals(p.getId())) {
                c.setQuantity(c.getQuantity() + 1);
                refreshCartUI();
                return;
            }
        }
        cart.add(new CartItem(p.getId(), p.getName(), p.getPrice(), 1));
        refreshCartUI();
    }

    private void refreshCartUI() {
        // Remove items that reached 0 quantity
        cart.removeIf(item -> item.getQuantity() <= 0);

        cartBox.getChildren().clear();

        for (CartItem item : cart) {
            cartBox.getChildren().add(
                CardFactory.createCartRow(
                    item,
                    () -> { item.setQuantity(item.getQuantity() - 1); refreshCartUI(); },  // decrement
                    () -> { item.setQuantity(item.getQuantity() + 1); refreshCartUI(); },  // increment
                    (q) -> { item.setQuantity(q);                     refreshCartUI(); },  // manual qty
                    () -> { cart.remove(item);                        refreshCartUI(); }   // remove
                )
            );
        }

        updateSummary();
    }

    // ─────────────────────────────── SUMMARY ──────────────────────────────────
    private void updateSummary() {
        int    qty      = cart.stream().mapToInt(CartItem::getQuantity).sum();
        double subtotal = cart.stream().mapToDouble(CartItem::getSubtotal).sum();
        double discount = getDiscount();
        double total    = Math.max(subtotal - discount, 0);

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
        showAlert("Checkout", "Payment processing...");
        // TODO: implement payment logic here
    }

  // ─────────────────────────────── NAVIGATION ───────────────────────────────

@FXML
private void openInventory(ActionEvent event) {
    navigate(event, FXML_INVENTORY);
}

@FXML
private void openPai(ActionEvent event) {
    navigate(event, FXML_PAI);
}

/**
 * These are aliases so your FXML won't crash
 * (in case it's using different method names)
 */
@FXML
private void goToInventory(ActionEvent event) {
    openInventory(event); // reuse existing logic
}
@FXML
private void goToPai(ActionEvent event) {
    openPai(event); // reuse existing logic
}
@FXML
private void goToDashboard(ActionEvent event) {
    // Already in dashboard → do nothing
}

/**
 * Filter trigger (for ComboBox / Button if used in FXML)
 */
@FXML
private void onFilterChange() {
    filterProducts(searchField.getText());
}

private void navigate(ActionEvent event, String fxmlPath) {
    URL resource = getClass().getResource(fxmlPath);

    if (resource == null) {
        showAlert("Navigation Error", "Cannot find FXML file:\n" + fxmlPath);
        return;
    }

    try {
        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    } catch (IOException e) {
        // Print the ROOT cause, not just the surface message
        Throwable cause = e;
        while (cause.getCause() != null) cause = cause.getCause();

        showAlert("Navigation Error",
            "Failed to load: " + fxmlPath +
            "\n\nRoot cause: " + cause.getClass().getSimpleName() +
            "\n" + cause.getMessage());

        e.printStackTrace(); // full trace in console
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
        private int qty;

        public CartItem(String id, String name, double price, int qty) {
            this.id    = id;
            this.name  = name;
            this.price = price;
            this.qty   = qty;
        }

        public String getProductId()   { return id;           }
        public String getProductName() { return name;         }
        public double getPrice()       { return price;        }
        public int    getQuantity()    { return qty;          }
        public void   setQuantity(int q) { this.qty = q;      }
        public double getSubtotal()    { return price * qty;  }
    }
}