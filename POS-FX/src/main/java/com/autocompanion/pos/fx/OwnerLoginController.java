package com.autocompanion.pos.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class OwnerLoginController {

    @FXML private TextField     txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button        btnLogin;
    @FXML private Label         errorLabel;
    @FXML private StackPane     overlayPane;

    // ── Hardcoded owner credentials ───────────────────────────────────────────
    private static final String OWNER_USERNAME = "owner";
    private static final String OWNER_PASSWORD = "owner123";

    // ── Set by whoever opens this screen ─────────────────────────────────────
    // "inventory" or "pai"
    private String destination;

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @FXML
    private void initialize() {
        errorLabel.setVisible(false);
        btnLogin.setOnAction(this::handleLogin);
        txtUsername.setOnAction(this::handleLogin);
        txtPassword.setOnAction(this::handleLogin);
    }

    private void handleLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (OWNER_USERNAME.equals(username) && OWNER_PASSWORD.equals(password)) {
            errorLabel.setVisible(false);
            navigateToDestination(event);
        } else {
            errorLabel.setText("Invalid owner credentials.");
            errorLabel.setVisible(true);
            shake(errorLabel);
        }
    }

    private void navigateToDestination(ActionEvent event) {
        String fxmlPath;
        if ("inventory".equals(destination)) {
            fxmlPath = "/com/autocompanion/pos/fx/Inventory.fxml";
        } else if ("pai".equals(destination)) {
            fxmlPath = "/com/autocompanion/pos/fx/Pai.fxml";
        } else {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            errorLabel.setText("Failed to open screen: " + e.getMessage());
            errorLabel.setVisible(true);
            e.printStackTrace();
        }
    }

    // Simple horizontal shake animation for wrong password feedback
    private void shake(Node node) {
        javafx.animation.TranslateTransition shake =
            new javafx.animation.TranslateTransition(Duration.millis(60), node);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }

    @FXML
    private void goBackToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/autocompanion/pos/fx/Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}