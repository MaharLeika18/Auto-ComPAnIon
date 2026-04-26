package org.MiniDev.Login;

import javax.swing.*;

import UI.RoundedPanel;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import net.miginfocom.swing.MigLayout;
import org.MiniDev.Cashier.CreateCashDrawerPanel;
import org.MiniDev.Home.MiniDevPOS;

import java.awt.*;

import static UI.UserFinalSettingsVar.COLOR_LINE_COLOR;


public class Login extends RoundedPanel {

    public static JTextField txtUsername;
    private JPasswordField txtPassword;
    protected JCheckBox chRememberMe;
    protected JButton cmdLogin;
    protected JFrame parentFrame; // Reference to the parent frame
    private JProgressBar progressBar;

    public Login(JFrame parentFrame) {
        this.parentFrame = parentFrame; // Store the reference
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill,insets 20", "[center]", "[center]"));
        txtUsername = new JTextField();
        txtUsername.setFont(new Font(FlatRobotoFont.FAMILY,Font.PLAIN,12));
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font(FlatRobotoFont.FAMILY,Font.PLAIN,12));
        chRememberMe = new JCheckBox("Remember me");
        chRememberMe.setFont(new Font(FlatRobotoFont.FAMILY,Font.BOLD,14));
        cmdLogin = new JButton("Login");
        cmdLogin.setFont(new Font(FlatRobotoFont.FAMILY,Font.PLAIN,14));

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false); // Hide it initially

        // Create the panel with styling
        RoundedPanel panel = new RoundedPanel(10);
        panel.setFont(new Font(FlatRobotoFont.FAMILY,Font.BOLD,13));
        panel.setBorderWidth(1);
        panel.setBorderColor(COLOR_LINE_COLOR);
        panel.setLayout(new MigLayout("wrap,fillx,insets 35 45 30 45", "fill,250:280"));
        panel.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:20;"
                + "[light]background:darken(@background,3%);"
                + "[dark]background:lighten(@background,3%)");

        // Password field styling
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true");

        // Login button styling
        cmdLogin.putClientProperty(FlatClientProperties.STYLE, ""
                + "[light]background:darken(@background,10%);"
                + "[dark]background:lighten(@background,10%);"
                + "borderWidth:0;"
                + "focusWidth:0;"
                + "innerFocusWidth:0");

        // Placeholder text for text fields
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username or email");
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");

        // Title and description labels with styling
        JLabel lbTitle = new JLabel("Welcome back!");
        JLabel description = new JLabel("Please sign in to access your account");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +10");
        description.putClientProperty(FlatClientProperties.STYLE, ""
                + "[light]foreground:lighten(@foreground,30%);"
                + "[dark]foreground:darken(@foreground,30%)");

        // Adding components to the panel
        panel.add(lbTitle);
        panel.add(description);
        JLabel userNameLabel = new JLabel("Username");
        userNameLabel.setFont(new Font(FlatRobotoFont.FAMILY,Font.BOLD,14));
        panel.add(userNameLabel, "gapy 8");
        panel.add(txtUsername);
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font(FlatRobotoFont.FAMILY,Font.BOLD,14));
        panel.add(passwordLabel, "gapy 8");
        panel.add(txtPassword);
        panel.add(chRememberMe, "grow 0");
        panel.add(cmdLogin, "gapy 10");
        panel.add(progressBar, "span 2, grow, gapy 10"); // Add progress bar to the panel

        // Add the panel to the main panel
        add(panel);

        cmdLogin.addActionListener((e) -> {
            String userName = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());

            // Show progress bar while processing
            progressBar.setVisible(true);

            // Create and execute the SwingWorker for authentication
            new AuthenticationWorker(userName, password).execute();
        });
    }

    private class AuthenticationWorker extends SwingWorker<Boolean, Void> {
        private final String username;
        private final String password;

        public AuthenticationWorker(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground() {
            // Perform authentication check
            return fetchAuthenticationCheckWithDatabase(username, password);
        }

        private static boolean isGUICreated = false;

        @Override
        protected void done() {
            try {
                boolean isAuthenticated = get();
                if (isAuthenticated) {
                    SwingUtilities.invokeLater(() -> {
                        if (!isGUICreated) {
                            // Ensure the GUI is created only once
                            isGUICreated = true;
                            CreateCashDrawerPanel.fetchDrawerData();
                            MiniDevPOS.createAndShowGUI(parentFrame);

                            // Close the login window (dispose after creating main GUI)
                            if (parentFrame != null) {
                                parentFrame.dispose();
                            }
                        }
                    });
                } else {
                    // Authentication failed, show an error message
                    showError("Invalid username or password.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showError("An error occurred during authentication.");
            }
        }


        private boolean fetchAuthenticationCheckWithDatabase(String username, String password) {
            // Replace with actual authentication logic (e.g., querying a database)
            try {
                Thread.sleep(2000); // Simulate network delay or database query
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Simulate authentication with database or service
            return AuthenticationService.fetchAuthenticationCheckWithDatabase(username, password);
        }

        public void showError(String message) {
            // Show an error dialog or message to the user
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(parentFrame, message, "Login Error", JOptionPane.ERROR_MESSAGE);
            });
        }
    }

}