package com.autocompanion.pos.fx;

import java.io.IOException;
import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class InventoryController {

    private static final String FXML_BASE      = "/com/autocompanion/pos/fx/";
    private static final String FXML_DASHBOARD = FXML_BASE + "Dashboard.fxml";
    private static final String FXML_PAI       = FXML_BASE + "Pai.fxml";

    @FXML
    private void goToDashboard(ActionEvent event) { navigate(event, FXML_DASHBOARD); }

    @FXML
    private void goToPai(ActionEvent event) { navigate(event, FXML_PAI); }

    @FXML
    private void goToInventory(ActionEvent event) { /* already here */ }

    private void navigate(ActionEvent event, String fxmlPath) {
        URL resource = getClass().getResource(fxmlPath);
        if (resource == null) {
            showAlert("Navigation Error", "Cannot find FXML:\n" + fxmlPath);
            return;
        }
        try {
            Parent root = FXMLLoader.load(resource);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to load:\n" + fxmlPath + "\n\n" + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}