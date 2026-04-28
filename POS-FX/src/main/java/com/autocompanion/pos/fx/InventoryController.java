package com.autocompanion.pos.fx;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class InventoryController {

    // ── FXML paths ────────────────────────────────────────────────
    private static final String FXML_BASE      = "/com/autocompanion/pos/fx/";
    private static final String FXML_DASHBOARD = FXML_BASE + "Dashboard.fxml";
    private static final String FXML_PAI       = FXML_BASE + "Pai.fxml";

    // ── FXML fields ───────────────────────────────────────────────
    @FXML private TextField  searchField;
    @FXML private ScrollPane scrollPane;

    // ── Data ──────────────────────────────────────────────────────
    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private VBox productList;

    // ──────────────────────────────── INIT ────────────────────────
    @FXML
    private void initialize() {
        productList = new VBox(10);
        productList.setStyle("-fx-background-color: transparent; -fx-padding: 0 0 20 0;");
        scrollPane.setContent(productList);
        scrollPane.setFitToWidth(true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterProducts(newVal));

        loadProductsFromDatabase();
    }

    // ─────────────────────────────── DATABASE ─────────────────────
    private void loadProductsFromDatabase() {
        String sql = "SELECT Food_Serial_Number, Food_Name, Food_Price, Stock_Available_Cnt "
                   + "FROM Food_Lists";  // no WHERE filter — show all in inventory

        try (Connection conn = DBConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            products.clear();

            while (rs.next()) {
                Product p = new Product();
                p.setSerialCode(rs.getString("Food_Serial_Number"));
                p.setName(rs.getString("Food_Name"));
                p.setPrice(rs.getDouble("Food_Price"));
                p.setStockAvailableNumber(rs.getInt("Stock_Available_Cnt"));
                products.add(p);
            }

            displayProducts(products);

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load products:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    // ─────────────────────────────── DISPLAY ──────────────────────
    private void displayProducts(ObservableList<Product> list) {
        productList.getChildren().clear();
        for (Product p : list) {
            productList.getChildren().add(CardFactory.createInventoryRow(p));
        }
    }

    // ─────────────────────────────── FILTER ───────────────────────
    private void filterProducts(String key) {
        if (key == null || key.isEmpty()) {
            displayProducts(products);
            return;
        }
        ObservableList<Product> filtered = FXCollections.observableArrayList();
        for (Product p : products) {
            if (p.getName().toLowerCase().contains(key.toLowerCase())
                    || (p.getSerialCode() != null && p.getSerialCode().toLowerCase().contains(key.toLowerCase()))) {
                filtered.add(p);
            }
        }
        displayProducts(filtered);
    }

    // ─────────────────────────────── NAV ──────────────────────────
    @FXML
    private void goToDashboard(ActionEvent event) { navigate(event, FXML_DASHBOARD); }

    @FXML
    private void openPai(ActionEvent event) { navigate(event, FXML_PAI); }

    private void navigate(ActionEvent event, String fxmlPath) {
        URL resource = getClass().getResource(fxmlPath);
        if (resource == null) {
            showAlert("Navigation Error", "Cannot find FXML:\n" + fxmlPath);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            Throwable cause = e;
            while (cause.getCause() != null) cause = cause.getCause();
            showAlert("Navigation Error",
                "Failed to load: " + fxmlPath +
                "\n\nRoot cause: " + cause.getClass().getSimpleName() +
                "\n" + cause.getMessage());
            e.printStackTrace();
        }
    }

    // ──────────────────────────────── UTIL ────────────────────────
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}