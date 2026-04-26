package com.autocompanion.pos.fx;

import org.MiniDev.Login.AuthenticationService;

import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private StackPane overlayPane;

    private Stage primaryStage;

    @FXML
    private void initialize() {
        // remove when done testing :3
        txtUsername.setText("admin");
        txtPassword.setText("admin123");

        btnLogin.setOnAction(e -> handleLogin());
        txtUsername.setOnAction(e -> handleLogin());
        txtPassword.setOnAction(e -> handleLogin());
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        setLoginButtonState(true);

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return AuthenticationService.fetchAuthenticationCheckWithDatabase(username, password);
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue()) {
                showOverlay("LoginSuccess.fxml", true);
            } else {
                setLoginButtonState(false);
                showOverlay("LoginError.fxml", false);
            }
        });

        task.setOnFailed(e -> {
            setLoginButtonState(false);
            showOverlay("LoginError.fxml", false);
        });

        new Thread(task).start();
    }

    private void setLoginButtonState(boolean loading) {
        btnLogin.setDisable(loading);
        btnLogin.setText(loading ? "Logging in..." : "Login");

        if (progressIndicator != null) {
            progressIndicator.setVisible(loading);
        }
    }

    // overlay
    private void showOverlay(String fxml, boolean isSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent overlay = loader.load();
            overlay.setOpacity(0);
            overlayPane.getChildren().add(overlay);

            // fade in
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), overlay);
            fadeIn.setToValue(1);
            fadeIn.play();

            if (isSuccess) {
                overlay.setOnMouseClicked(e -> openDashboard());
            } else {
                Button btn = (Button) overlay.lookup("#btnTryAgain");
                if (btn != null) {
                    btn.setOnAction(e -> removeOverlay(overlay));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeOverlay(Parent overlay) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(250), overlay);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> overlayPane.getChildren().remove(overlay));
        fadeOut.play();
    }

    private void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();
            primaryStage = (Stage) btnLogin.getScene().getWindow();
            primaryStage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}