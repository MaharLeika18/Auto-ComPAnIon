package com.autocompanion.pos.fx;

import java.text.DecimalFormat;

import org.MiniDev.OOP.Product;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// Import CartItem from DashboardController
// import com.autocompanion.pos.fx.DashboardController.CartItem;

public class CardFactory {

    private static DecimalFormat df = new DecimalFormat("#,###.00");

    // Create product card for grid
    public static VBox createProductCard(Product p, Runnable onAddToCart) {
        VBox card = new VBox(8);
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 4);");
        card.setPrefWidth(180);
        card.setAlignment(Pos.CENTER);

        Label nameLabel = new Label(p.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-wrap-text: true;");
        nameLabel.setPrefWidth(160);
        nameLabel.setAlignment(Pos.CENTER);

        Label priceLabel = new Label("₱" + df.format(p.getPrice()));
        priceLabel.setStyle("-fx-text-fill: #2A6B8C; -fx-font-weight: bold; -fx-font-size: 16px;");

        Label stockLabel = new Label("Stock: " + p.getStockAvailableNumber());
        String stockColor = p.getStockAvailableNumber() < 5 ? "red" : "#888";
        stockLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + stockColor);

        Button addBtn = new Button("Add to Cart");
        addBtn.setStyle(
                "-fx-background-color: #2A6B8C; -fx-background-radius: 20; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 12;");
        addBtn.setOnAction(e -> onAddToCart.run());

        card.getChildren().addAll(nameLabel, priceLabel, stockLabel, addBtn);
        return card;
    }

    // Create cart item card
    public static VBox createCartCard(
            DashboardController.CartItem item,
            Runnable onMinus,
            Runnable onPlus,
            Runnable onRemove) {

        VBox card = new VBox(8);
        card.setStyle(
                "-fx-background-color: #f8f9fa; -fx-background-radius: 15; -fx-padding: 12; -fx-border-color: #e0e0e0; -fx-border-radius: 15;");
        card.setPrefWidth(200);
        card.setAlignment(Pos.CENTER);

        Label nameLabel = new Label(item.getProductName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-wrap-text: true;");
        nameLabel.setAlignment(Pos.CENTER);

        Label priceLabel = new Label("₱" + df.format(item.getPrice()));
        priceLabel.setStyle("-fx-text-fill: #2A6B8C; -fx-font-weight: bold;");

        // Quantity controls
        HBox qtyBox = new HBox(8);
        qtyBox.setAlignment(Pos.CENTER);

        Button minusBtn = new Button("-");
        minusBtn.setStyle(
                "-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 15; -fx-font-weight: bold; -fx-padding: 4 10;");
        minusBtn.setOnAction(e -> onMinus.run());

        Label qtyLabel = new Label(String.valueOf(item.getQuantity()));
        qtyLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Button plusBtn = new Button("+");
        plusBtn.setStyle(
                "-fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 15; -fx-font-weight: bold; -fx-padding: 4 10;");
        plusBtn.setOnAction(e -> onPlus.run());

        qtyBox.getChildren().addAll(minusBtn, qtyLabel, plusBtn);

        Label subtotalLabel = new Label("Subtotal: ₱" + df.format(item.getSubtotal()));
        subtotalLabel.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold; -fx-font-size: 12px;");

        Button removeBtn = new Button("Remove");
        removeBtn.setStyle(
                "-fx-background-color: #6c757d; -fx-text-fill: white; -fx-background-radius: 15; -fx-font-size: 11px; -fx-padding: 4 10;");
        removeBtn.setOnAction(e -> onRemove.run());

        card.getChildren().addAll(nameLabel, priceLabel, qtyBox, subtotalLabel, removeBtn);
        return card;
    }
}