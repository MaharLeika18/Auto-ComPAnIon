package com.autocompanion.pos.fx;

// JavaFX imports
import org.MiniDev.Login.AuthenticationService;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    
    // These MUST match the fx:id in your Login.fxml
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private CheckBox chkRememberMe;
    @FXML private ProgressIndicator progressIndicator;
    
    @FXML
    private void initialize() {
        // Setup placeholder text
        txtUsername.setPromptText("Enter your username or email");
        txtPassword.setPromptText("Enter your password");
        
        // Set button action
        btnLogin.setOnAction(event -> handleLogin());
    }
    
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();
        
        // Disable button and show loading
        btnLogin.setDisable(true);
        btnLogin.setText("Logging in...");
        if (progressIndicator != null) {
            progressIndicator.setVisible(true);
        }
        
        // Background task for authentication
        Task<Boolean> loginTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                // Call old project's authentication
                return AuthenticationService.fetchAuthenticationCheckWithDatabase(username, password);
            }
        };
        
        // What happens when task succeeds
        loginTask.setOnSucceeded(event -> {
            boolean isValid = loginTask.getValue();
            if (isValid) {
                openDashboard();
            } else {
                showError("Invalid username or password!");
                btnLogin.setDisable(false);
                btnLogin.setText("Login");
                if (progressIndicator != null) {
                    progressIndicator.setVisible(false);
                }
            }
        });
        
        // What happens when task fails
        loginTask.setOnFailed(event -> {
            showError("Login error: " + loginTask.getException().getMessage());
            btnLogin.setDisable(false);
            btnLogin.setText("Login");
            if (progressIndicator != null) {
                progressIndicator.setVisible(false);
            }
        });
        
        // Run the task in background thread
        new Thread(loginTask).start();
    }
    
    private void openDashboard() {
        try {
            // Load dashboard FXML (create this next)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("MiniDev POS - Dashboard");
            stage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not open dashboard: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Failed");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}