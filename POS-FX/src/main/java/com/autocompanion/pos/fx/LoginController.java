package com.autocompanion.pos.fx;

// IMPORT from the old package
import org.MiniDev.Login.AuthenticationService;
import org.MiniDev.OOP.Product;
import org.MiniDev.DBConnection.DBConnection;

public class LoginController {
    
    private void handleLogin() {
        // Calling OLD project's authentication
        boolean isValid = AuthenticationService.fetchAuthenticationCheckWithDatabase(username, password);
        
        if (isValid) {
            // Open dashboard
            openDashboard();
        }
    }
}