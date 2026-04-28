package com.autocompanion.pos.fx;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import org.MiniDev.DBConnection.DBConnection;
import org.MiniDev.OOP.Product;

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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class InventoryController {

    private static final String FXML_BASE      = "/com/autocompanion/pos/fx/";
    private static final String FXML_DASHBOARD = FXML_BASE + "Dashboard.fxml";
    private static final String FXML_PAI       = FXML_BASE + "Pai.fxml";

    private static final String STYLE_SELECTED =
        "-fx-background-color: #C8E6C9; -fx-background-radius: 40; " +
        "-fx-padding: 0 16; -fx-effect: dropshadow(gaussian,rgba(0,0,0,.12),6,0,0,3);";
    private static final String STYLE_NORMAL =
        "-fx-background-color: #EEF4ED; -fx-background-radius: 40; " +
        "-fx-padding: 0 16; -fx-effect: dropshadow(gaussian,rgba(0,0,0,.05),4,0,0,2);";

    @FXML private TextField  searchField;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox       productList;
    @FXML private Button     addButton;
    @FXML private Button     editButton;
    @FXML private Button     deleteButton;

    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private Product selectedProduct = null;
    private HBox    selectedRow     = null;

    @FXML
    private void initialize() {
        scrollPane.setFitToWidth(true);
        addButton.setOnAction(e    -> doAdd());
        editButton.setOnAction(e   -> doEdit());
        deleteButton.setOnAction(e -> doDelete());
        searchField.textProperty().addListener(
            (obs, o, n) -> filterProducts(n));
        loadProductsFromDatabase();
    }

    // ── Load ──────────────────────────────────────────────────────────────
    private void loadProductsFromDatabase() {
        String sql =
            "SELECT Food_Main_Counter_ID, Counter_Name, Food_Category, " +
            "       Food_Serial_Number, Food_Name, Food_Price, " +
            "       Food_Original_Price, Tax_Percentage, Promotion_Percentage, " +
            "       Food_Desc, Stock_Available_Cnt, " +
            "       MainPrinterPortName, MainPrinterPortAddress " +
            "FROM Food_Lists";

        try (Connection conn = DBConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            products.clear();
            clearSelection();

            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getString("Food_Main_Counter_ID"));
                p.setCounterName(rs.getString("Counter_Name"));
                p.setFoodDesc(rs.getString("Food_Category"));        // Category → foodDesc
                p.setSerialCode(rs.getString("Food_Serial_Number"));
                p.setName(rs.getString("Food_Name"));
                p.setPrice(rs.getDouble("Food_Price"));
                p.setPromotionPercentage(rs.getDouble("Promotion_Percentage"));
                p.setStockAvailableNumber(rs.getInt("Stock_Available_Cnt"));
                p.setMainPrinterPortName(rs.getString("MainPrinterPortName"));
                p.setMainPrinterPortAddress(rs.getString("MainPrinterPortAddress"));
                products.add(p);
            }
            displayProducts(products);

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load products:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    // ── Display ───────────────────────────────────────────────────────────
    private void displayProducts(ObservableList<Product> list) {
        productList.getChildren().clear();
        clearSelection();
        for (Product p : list) {
            HBox row = CardFactory.createInventoryRow(p);
            row.setStyle(STYLE_NORMAL);
            row.setOnMouseClicked(e -> selectRow(p, row));
            productList.getChildren().add(row);
        }
    }

    private void selectRow(Product p, HBox row) {
        if (selectedRow != null) selectedRow.setStyle(STYLE_NORMAL);
        if (selectedRow == row) { clearSelection(); }
        else { selectedProduct = p; selectedRow = row; row.setStyle(STYLE_SELECTED); }
    }

    private void clearSelection() { selectedProduct = null; selectedRow = null; }

    // ── Add ───────────────────────────────────────────────────────────────
    private void doAdd() {
        ProductDialog.show(null).ifPresent(p -> {
            String sql =
                "INSERT INTO Food_Lists " +
                "  (Food_Main_Counter_ID, Counter_Name, Food_Category, " +
                "   Food_Serial_Number, Food_Name, Food_Price, Food_Original_Price, " +
                "   Tax_Percentage, Promotion_Percentage, Food_Desc, " +
                "   Stock_Count_YN, Stock_Available_Cnt, " +
                "   MainPrinterPortName, MainPrinterPortAddress, CreatedDate) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString   (1,  p.getId());                          // Food_Main_Counter_ID
                ps.setString   (2,  p.getCounterName());                 // Counter_Name
                ps.setString   (3,  p.getFoodDesc());                    // Food_Category
                ps.setString   (4,  p.getSerialCode());                  // Food_Serial_Number
                ps.setString   (5,  p.getName());                        // Food_Name
                ps.setInt      (6,  (int) p.getPrice());                 // Food_Price
                ps.setInt      (7,  (int) p.getPrice());                 // Food_Original_Price (same as price on add)
                ps.setDouble   (8,  0.00);                               // Tax_Percentage default
                ps.setDouble   (9,  p.getPromotionPercentage());         // Promotion_Percentage
                ps.setString   (10, p.getDescription());                 // Food_Desc
                ps.setString   (11, p.getStockAvailableNumber() > 0 ? "Y" : "N"); // Stock_Count_YN
                ps.setInt      (12, p.getStockAvailableNumber());        // Stock_Available_Cnt
                ps.setString   (13, p.getMainPrinterPortName());         // MainPrinterPortName
                ps.setString   (14, p.getMainPrinterPortAddress());      // MainPrinterPortAddress
                ps.setTimestamp(15, Timestamp.valueOf(LocalDateTime.now())); // CreatedDate

                ps.executeUpdate();
                showInfo("Product Added", "\"" + p.getName() + "\" was added successfully.");
                loadProductsFromDatabase();

            } catch (SQLException ex) {
                showAlert("Database Error", "Failed to add product:\n" + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    // ── Edit ──────────────────────────────────────────────────────────────
    private void doEdit() {
        if (selectedProduct == null) {
            showAlert("No Selection", "Please click on a product row first, then press Edit.");
            return;
        }
        ProductDialog.show(selectedProduct).ifPresent(updated -> {
            String sql =
                "UPDATE Food_Lists SET " +
                "  Food_Main_Counter_ID  = ?, " +
                "  Counter_Name          = ?, " +
                "  Food_Category         = ?, " +
                "  Food_Name             = ?, " +
                "  Food_Price            = ?, " +
                "  Promotion_Percentage  = ?, " +
                "  Food_Desc             = ?, " +
                "  Stock_Count_YN        = ?, " +
                "  Stock_Available_Cnt   = ?, " +
                "  MainPrinterPortName   = ?, " +
                "  MainPrinterPortAddress = ? " +
                "WHERE Food_Serial_Number = ?";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1,  updated.getId());
                ps.setString(2,  updated.getCounterName());
                ps.setString(3,  updated.getFoodDesc());
                ps.setString(4,  updated.getName());
                ps.setInt   (5,  (int) updated.getPrice());
                ps.setDouble(6,  updated.getPromotionPercentage());
                ps.setString(7,  updated.getDescription());
                ps.setString(8,  updated.getStockAvailableNumber() > 0 ? "Y" : "N");
                ps.setInt   (9,  updated.getStockAvailableNumber());
                ps.setString(10, updated.getMainPrinterPortName());
                ps.setString(11, updated.getMainPrinterPortAddress());
                ps.setString(12, updated.getSerialCode());              // WHERE

                int rows = ps.executeUpdate();
                if (rows == 0)
                    showAlert("Not Found", "No product with serial \"" + updated.getSerialCode() + "\" found.");
                else {
                    showInfo("Updated", "\"" + updated.getName() + "\" was updated.");
                    loadProductsFromDatabase();
                }

            } catch (SQLException ex) {
                showAlert("Database Error", "Failed to update product:\n" + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    // ── Delete ────────────────────────────────────────────────────────────
    private void doDelete() {
        if (selectedProduct == null) {
            showAlert("No Selection", "Please click on a product row first, then press Delete.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Product");
        confirm.setHeaderText(null);
        confirm.setContentText("Delete \"" + selectedProduct.getName() + "\"? This cannot be undone.");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        String sql = "DELETE FROM Food_Lists WHERE Food_Serial_Number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, selectedProduct.getSerialCode());
            int rows = ps.executeUpdate();
            if (rows == 0)
                showAlert("Not Found", "No product with that serial was found.");
            else {
                showInfo("Deleted", "\"" + selectedProduct.getName() + "\" was deleted.");
                clearSelection();
                loadProductsFromDatabase();
            }
        } catch (SQLException ex) {
            showAlert("Database Error", "Failed to delete product:\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ── Filter ────────────────────────────────────────────────────────────
    private void filterProducts(String key) {
        if (key == null || key.isEmpty()) { displayProducts(products); return; }
        String lower = key.toLowerCase();
        ObservableList<Product> filtered = FXCollections.observableArrayList();
        for (Product p : products) {
            boolean nm = p.getName() != null && p.getName().toLowerCase().contains(lower);
            boolean sm = p.getSerialCode() != null && p.getSerialCode().toLowerCase().contains(lower);
            if (nm || sm) filtered.add(p);
        }
        displayProducts(filtered);
    }

    // ── Nav ───────────────────────────────────────────────────────────────
    @FXML private void goToDashboard(ActionEvent event) { navigate(event, FXML_DASHBOARD); }
    @FXML private void openPai(ActionEvent event)       { navigate(event, FXML_PAI); }

    private void navigate(ActionEvent event, String fxmlPath) {
        URL resource = getClass().getResource(fxmlPath);
        if (resource == null) { showAlert("Navigation Error", "Cannot find FXML:\n" + fxmlPath); return; }
        try {
            Parent root = new FXMLLoader(resource).load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            Throwable cause = e;
            while (cause.getCause() != null) cause = cause.getCause();
            showAlert("Navigation Error", "Failed to load: " + fxmlPath
                + "\n" + cause.getClass().getSimpleName() + ": " + cause.getMessage());
            e.printStackTrace();
        }
    }

    // ── Util ──────────────────────────────────────────────────────────────
    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}