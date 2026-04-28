package com.autocompanion.pos.fx;

import java.text.DecimalFormat;
import java.util.function.Consumer;

import org.MiniDev.OOP.Product;

import com.autocompanion.pos.fx.DashboardController.CartItem;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class CardFactory {

    private static final DecimalFormat df = new DecimalFormat("#,###.00");

    // ─────────────────────────────────────────────────────────────────
    // PRODUCT CARD  (grid on the left)
    // ─────────────────────────────────────────────────────────────────
    public static VBox createProductCard(Product p, Runnable onAddToCart) {
        VBox card = new VBox(8);
        card.setStyle(
            "-fx-background-color: #EEF4ED; " +
            "-fx-background-radius: 15; " +
            "-fx-padding: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 8, 0, 0, 4);");
        card.setPrefWidth(180);
        card.setAlignment(Pos.CENTER);

        Label nameLabel = new Label(p.getName());
        nameLabel.setStyle(
            "-fx-font-family: 'Montserrat SemiBold'; " +
            "-fx-font-size: 13px; " +
            "-fx-wrap-text: true; " +
            "-fx-text-fill: #1c4f43;");
        nameLabel.setPrefWidth(160);
        nameLabel.setAlignment(Pos.CENTER);

        Label priceLabel = new Label("₱" + df.format(p.getPrice()));
        priceLabel.setStyle(
            "-fx-font-family: 'Montserrat SemiBold'; " +
            "-fx-font-size: 16px; " +
            "-fx-text-fill: #649e8f;");

        boolean lowStock = p.getStockAvailableNumber() < 5;
        Label stockLabel = new Label("Stock: " + p.getStockAvailableNumber());
        stockLabel.setStyle(
            "-fx-font-size: 11px; " +
            "-fx-text-fill: " + (lowStock ? "#e05252" : "#888888") + ";");

        Button addBtn = new Button("Add to Cart");
        addBtn.setStyle(
            "-fx-background-color: #1FFFCB; " +
            "-fx-background-radius: 20; " +
            "-fx-text-fill: #1c4f43; " +
            "-fx-font-family: 'Montserrat SemiBold'; " +
            "-fx-font-size: 12px; " +
            "-fx-padding: 6 14;");
        addBtn.setOnAction(e -> onAddToCart.run());

        if (p.getStockAvailableNumber() == 0) {
            addBtn.setDisable(true);
            addBtn.setStyle(addBtn.getStyle() +
                "-fx-background-color: #cccccc; -fx-text-fill: #888888;");
        }

        card.getChildren().addAll(nameLabel, priceLabel, stockLabel, addBtn);
        return card;
    }

    // ─────────────────────────────────────────────────────────────────
    // CART ROW  (receipt panel on the right)
    // ─────────────────────────────────────────────────────────────────
    public static VBox createCartRow(
            CartItem item,
            Runnable  onMinus,
            Runnable  onPlus,
            Consumer<Integer> onQtyTyped,
            Runnable  onRemove) {

        VBox row = new VBox(6);
        row.setStyle(
            "-fx-background-color: #EEF4ED; " +
            "-fx-background-radius: 12; " +
            "-fx-padding: 10 14; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 6, 0, 0, 2);");
        row.setPrefWidth(440);

        Label nameLabel = new Label(item.getProductName());
        nameLabel.setStyle(
            "-fx-font-family: 'Montserrat SemiBold'; " +
            "-fx-font-size: 13px; " +
            "-fx-text-fill: #1c4f43; " +
            "-fx-wrap-text: true;");
        nameLabel.setMaxWidth(240);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label subtotalLabel = new Label("₱" + df.format(item.getSubtotal()));
        subtotalLabel.setStyle(
            "-fx-font-family: 'Montserrat SemiBold'; " +
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #649e8f;");

        Button removeBtn = new Button("✕");
        removeBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #e05252; " +
            "-fx-font-size: 13px; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 0 0 0 8;");
        removeBtn.setOnAction(e -> onRemove.run());

        HBox topLine = new HBox(4, nameLabel, spacer, subtotalLabel, removeBtn);
        topLine.setAlignment(Pos.CENTER_LEFT);

        Button minusBtn = makeQtyBtn("−");
        minusBtn.setOnAction(e -> onMinus.run());

        TextField qtyField = new TextField(String.valueOf(item.getQuantity()));
        qtyField.setPrefWidth(52);
        qtyField.setAlignment(Pos.CENTER);
        qtyField.setStyle(
            "-fx-font-family: 'Montserrat Regular'; " +
            "-fx-font-size: 13px; " +
            "-fx-background-radius: 8; " +
            "-fx-border-color: #bdbdbd; " +
            "-fx-border-radius: 8; " +
            "-fx-text-fill: #1c4f43;");

        qtyField.setOnAction(e -> commitQty(qtyField, onQtyTyped));
        qtyField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) commitQty(qtyField, onQtyTyped);
        });

        Button plusBtn = makeQtyBtn("+");
        plusBtn.setOnAction(e -> onPlus.run());

        Label unitPrice = new Label("@ ₱" + df.format(item.getPrice()) + " each");
        unitPrice.setStyle("-fx-font-size: 11px; -fx-text-fill: #aaaaaa;");

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        HBox bottomLine = new HBox(6, minusBtn, qtyField, plusBtn, spacer2, unitPrice);
        bottomLine.setAlignment(Pos.CENTER_LEFT);

        row.getChildren().addAll(topLine, bottomLine);
        return row;
    }

    // ─────────────────────────────────────────────────────────────────
    // INVENTORY ROW  (table rows in inventory view)
    //
    //  ┌──────────────────────────────────────────────────────────────────┐
    //  │  Product Name   │  Serial Code  │  ₱ price  │  qty  │  status  │
    //  └──────────────────────────────────────────────────────────────────┘
    // ─────────────────────────────────────────────────────────────────────
    public static HBox createInventoryRow(Product p) {
        HBox row = new HBox();
        row.setPrefWidth(1656);
        row.setPrefHeight(56);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle(
            "-fx-background-color: #EEF4ED; " +
            "-fx-background-radius: 40; " +
            "-fx-padding: 0 16; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 2);");

        // Product name — 290px to match your FXML label at x=125
        Label nameLabel = makeRowLabel(p.getName(), 290, true);

        // Serial code — 230px
        Label idLabel = makeRowLabel(
            p.getSerialCode() != null ? p.getSerialCode() : (p.getId() != null ? p.getId() : "—"),
            230, false);

        // Category — 180px (counterName is the closest field you have)
        Label categoryLabel = makeRowLabel(
            p.getCounterName() != null ? p.getCounterName() : "—",
            180, false);

        // Supplier — 177px (no supplier field, placeholder)
        Label supplierLabel = makeRowLabel("—", 177, false);

        // Storage LOC — 223px (no field, placeholder)
        Label storageLabel = makeRowLabel("—", 223, false);

        // Quantity in stock — 264px
        int stock = p.getStockAvailableNumber();
        boolean lowStock = stock > 0 && stock < 5;
        boolean outOfStock = stock == 0;
        Label qtyLabel = makeRowLabel(String.valueOf(stock), 264, false);
        qtyLabel.setStyle(qtyLabel.getStyle() +
            "-fx-text-fill: " + (outOfStock ? "#e05252" : lowStock ? "#e08c52" : "#1c4f43") + ";");

        // Retail price — remaining space
        Label priceLabel = makeRowLabel("₱" + df.format(p.getPrice()), 150, true);
        priceLabel.setStyle(priceLabel.getStyle() + "-fx-text-fill: #649e8f;");

        row.getChildren().addAll(
            nameLabel, idLabel, categoryLabel,
            supplierLabel, storageLabel, qtyLabel, priceLabel
        );
        return row;
    }

    // ─────────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────────
    private static Label makeRowLabel(String text, double width, boolean semibold) {
        Label lbl = new Label(text != null ? text : "—");
        lbl.setPrefWidth(width);
        lbl.setStyle(
            "-fx-font-family: 'Montserrat " + (semibold ? "SemiBold" : "Regular") + "'; " +
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #1c4f43;");
        return lbl;
    }

    private static Button makeQtyBtn(String text) {
        Button btn = new Button(text);
        btn.setPrefSize(30, 30);
        btn.setStyle(
            "-fx-background-color: #EEF4ED; " +
            "-fx-background-radius: 50; " +
            "-fx-text-fill: #1c4f43; " +
            "-fx-font-size: 16px; " +
            "-fx-cursor: hand;");
        return btn;
    }

    private static void commitQty(TextField field, Consumer<Integer> onQtyTyped) {
        try {
            int val = Integer.parseInt(field.getText().trim());
            if (val >= 1) onQtyTyped.accept(val);
            else field.setText("1");
        } catch (NumberFormatException ex) {
            field.setText("1");
        }
    }
}