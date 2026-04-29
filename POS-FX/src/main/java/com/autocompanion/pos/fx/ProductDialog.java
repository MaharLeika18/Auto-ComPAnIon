package com.autocompanion.pos.fx;

import java.util.Optional;

import org.MiniDev.OOP.Product;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class ProductDialog {

    private static final String FONT_SEMI  = "Montserrat SemiBold";
    private static final String FONT_REG   = "Montserrat Regular";
    private static final String COLOR_DARK = "#1c4f43";
    private static final String COLOR_BG   = "#EEF4ED";

    public static Optional<Product> show(Product existing) {
        boolean isEdit = existing != null;
        String title   = isEdit ? "Edit Product" : "Add New Product";

        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.getDialogPane().setStyle(
            "-fx-background-color: " + COLOR_BG + "; -fx-background-radius: 16;");

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font(FONT_SEMI, 22));
        titleLabel.setStyle("-fx-text-fill: " + COLOR_DARK + ";");

        // ── Fields ────────────────────────────────────────────────────────
        // Required (NOT NULL, no default)
        TextField counterIdField    = styledField("e.g. CTR-001");
        TextField counterNameField  = styledField("e.g. Main Counter");
        TextField categoryField     = styledField("e.g. Beverages");
        TextField serialField       = styledField("e.g. PRD-001");
        TextField priceField        = styledField("e.g. 150");
        TextField origPriceField    = styledField("e.g. 150");
        TextField printerNameField  = styledField("e.g. EPSON_TM88");
        TextField printerAddrField  = styledField("e.g. COM3  or  192.168.1.100");

        // Optional (nullable or has default)
        TextField nameField         = styledField("Product display name");
        TextField taxField          = styledField("0.00");
        TextField promoField        = styledField("0.00");
        TextField stockField        = styledField("0");
        TextField descField         = styledField("Short description (optional)");

        // Pre-fill for edit
        if (isEdit) {
            counterIdField.setText(nullSafe(existing.getId()));
            counterNameField.setText(nullSafe(existing.getCounterName()));
            categoryField.setText(nullSafe(existing.getFoodDesc()));   // Food_Category → foodDesc
            serialField.setText(nullSafe(existing.getSerialCode()));
            nameField.setText(nullSafe(existing.getName()));
            priceField.setText(String.valueOf((int) existing.getPrice()));
            origPriceField.setText(String.valueOf((int) existing.getPrice()));
            taxField.setText(String.valueOf(existing.getPromotionPercentage()));
            promoField.setText(String.valueOf(existing.getPromotionPercentage()));
            stockField.setText(String.valueOf(existing.getStockAvailableNumber()));
            printerNameField.setText(nullSafe(existing.getMainPrinterPortName()));
            printerAddrField.setText(nullSafe(existing.getMainPrinterPortAddress()));
            descField.setText(nullSafe(existing.getDescription()));
        }

        // ── Grid ──────────────────────────────────────────────────────────
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(12);
        grid.setPadding(new Insets(16, 24, 10, 24));

        // Required fields first (marked with *)
        addRow(grid,  0, "* Counter ID",          counterIdField);
        addRow(grid,  1, "* Counter Name",         counterNameField);
        addRow(grid,  2, "* Category",             categoryField);
        addRow(grid,  3, "* Serial Number",        serialField);
        addRow(grid,  4, "* Price (₱)",            priceField);
        addRow(grid,  5, "* Original Price (₱)",   origPriceField);
        addRow(grid,  6, "* Printer Name",         printerNameField);
        addRow(grid,  7, "* Printer Address",      printerAddrField);
        // Optional fields
        addRow(grid,  8, "  Product Name",         nameField);
        addRow(grid,  9, "  Tax %",                taxField);
        addRow(grid, 10, "  Promo %",              promoField);
        addRow(grid, 11, "  Stock Count",          stockField);
        addRow(grid, 12, "  Description",          descField);

        // Required note
        Label note = new Label("* Required fields");
        note.setStyle("-fx-font-size: 11px; -fx-text-fill: #888888;");
        grid.add(note, 0, 13, 2, 1);

        // ── Buttons ───────────────────────────────────────────────────────
        Button confirmBtn = new Button(isEdit ? "Save Changes" : "Add Product");
        confirmBtn.setStyle(
            "-fx-background-color: #1C4F43; -fx-background-radius: 20; " +
            "-fx-text-fill: white; -fx-font-family: '" + FONT_SEMI + "'; " +
            "-fx-font-size: 14px; -fx-padding: 8 24; -fx-cursor: hand;");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle(
            "-fx-background-color: #B3B3B3; -fx-background-radius: 20; " +
            "-fx-text-fill: white; -fx-font-family: '" + FONT_SEMI + "'; " +
            "-fx-font-size: 14px; -fx-padding: 8 24; -fx-cursor: hand;");

        HBox buttonBox = new HBox(12, cancelBtn, confirmBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(8, 0, 0, 0));
        grid.add(buttonBox, 0, 14, 2, 1);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().lookupButton(ButtonType.OK).setVisible(false);
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setVisible(false);

        GridPane wrapper = new GridPane();
        wrapper.add(titleLabel, 0, 0);
        GridPane.setMargin(titleLabel, new Insets(16, 24, 4, 24));
        wrapper.add(grid, 0, 1);
        dialog.getDialogPane().setContent(wrapper);

        final boolean[] confirmed = {false};

        confirmBtn.setOnAction(e -> {
            if (!validate(counterIdField, counterNameField, categoryField,
                          serialField, priceField, origPriceField,
                          printerNameField, printerAddrField,
                          taxField, promoField, stockField)) return;
            confirmed[0] = true;
            dialog.setResult(buildProduct(existing,
                counterIdField, counterNameField, categoryField, serialField,
                nameField, priceField, origPriceField,
                taxField, promoField, stockField,
                printerNameField, printerAddrField, descField));
            dialog.close();
        });

        cancelBtn.setOnAction(e -> { dialog.setResult(null); dialog.close(); });
        dialog.setResultConverter(btn -> null);

        dialog.showAndWait();
        if (confirmed[0] && dialog.getResult() != null)
            return Optional.of(dialog.getResult());
        return Optional.empty();
    }

    // ── Validation ────────────────────────────────────────────────────────
    private static boolean validate(
            TextField counterId, TextField counterName, TextField category,
            TextField serial, TextField price, TextField origPrice,
            TextField printerName, TextField printerAddr,
            TextField tax, TextField promo, TextField stock) {

        StringBuilder e = new StringBuilder();

        if (counterId.getText().trim().isEmpty())   e.append("• Counter ID is required.\n");
        if (counterName.getText().trim().isEmpty())  e.append("• Counter Name is required.\n");
        if (category.getText().trim().isEmpty())     e.append("• Category is required.\n");
        if (serial.getText().trim().isEmpty())       e.append("• Serial Number is required.\n");
        if (printerName.getText().trim().isEmpty())  e.append("• Printer Name is required.\n");
        if (printerAddr.getText().trim().isEmpty())  e.append("• Printer Address is required.\n");

        if (!parseIntOk(price.getText()))      e.append("• Price must be a whole number.\n");
        if (!parseIntOk(origPrice.getText()))  e.append("• Original Price must be a whole number.\n");
        if (!parseDecOk(tax.getText()))        e.append("• Tax % must be a number (e.g. 12.00).\n");
        if (!parseDecOk(promo.getText()))      e.append("• Promo % must be a number (e.g. 0.00).\n");
        if (!parseIntOk(stock.getText()))      e.append("• Stock Count must be a whole number.\n");

        if (e.length() > 0) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Please fix the following");
            a.setHeaderText(null);
            a.setContentText(e.toString());
            a.showAndWait();
            return false;
        }
        return true;
    }

    // ── Build Product ─────────────────────────────────────────────────────
    private static Product buildProduct(
            Product base,
            TextField counterIdF, TextField counterNameF, TextField categoryF,
            TextField serialF, TextField nameF,
            TextField priceF, TextField origPriceF,
            TextField taxF, TextField promoF, TextField stockF,
            TextField printerNameF, TextField printerAddrF, TextField descF) {

        Product p = (base != null) ? base : new Product();
        p.setId(counterIdF.getText().trim());                       // Food_Main_Counter_ID
        p.setCounterName(counterNameF.getText().trim());            // Counter_Name
        p.setFoodDesc(categoryF.getText().trim());                  // Food_Category → foodDesc
        p.setSerialCode(serialF.getText().trim());                  // Food_Serial_Number
        p.setName(nameF.getText().trim());                          // Food_Name
        p.setPrice(Integer.parseInt(priceF.getText().trim()));      // Food_Price
        p.setPromotionPercentage(Double.parseDouble(promoF.getText().trim())); // Promotion_Percentage (also used for orig price below)
        p.setStockAvailableNumber(Integer.parseInt(stockF.getText().trim()));  // Stock_Available_Cnt
        p.setMainPrinterPortName(printerNameF.getText().trim());    // MainPrinterPortName
        p.setMainPrinterPortAddress(printerAddrF.getText().trim()); // MainPrinterPortAddress
        return p;
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private static TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(300);
        tf.setStyle(
            "-fx-background-radius: 20; -fx-border-width: 0; " +
            "-fx-background-color: white; " +
            "-fx-font-family: 'Montserrat Regular'; " +
            "-fx-font-size: 13px; -fx-text-fill: #1c4f43; -fx-padding: 7 12;");
        return tf;
    }

    private static void addRow(GridPane grid, int row, String labelText, TextField field) {
        Label lbl = new Label(labelText);
        lbl.setStyle(
            "-fx-font-family: 'Montserrat SemiBold'; " +
            "-fx-font-size: 12px; -fx-text-fill: #1c4f43;");
        lbl.setPrefWidth(160);
        grid.add(lbl, 0, row);
        grid.add(field, 1, row);
    }

    private static boolean parseIntOk(String s) {
        try { return Integer.parseInt(s.trim()) >= 0; }
        catch (NumberFormatException e) { return false; }
    }

    private static boolean parseDecOk(String s) {
        try { return Double.parseDouble(s.trim()) >= 0; }
        catch (NumberFormatException e) { return false; }
    }

    private static String nullSafe(String s) { return s != null ? s : ""; }
}