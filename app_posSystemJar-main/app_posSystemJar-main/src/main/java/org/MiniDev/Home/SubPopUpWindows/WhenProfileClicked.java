package org.MiniDev.Home.SubPopUpWindows;

import DBConnection.DBConnection;
import NetworkUtils.NetworkUtils;
import SqlLoadersAndUpdater.FetchEmployeeHistoryLists;
import UI.*;
import org.MiniDev.Backoffice.EmployeeManagement.EmployeeListManagement.EmpTableActionPages.EmpNewPage;
import org.MiniDev.Cashier.CreateCashDrawerPanel;
import org.MiniDev.Home.MiniDevPOS;
import org.MiniDev.Home.SubPopUpWindows.ProfileClickedUICom.ProfileClickedUICom;
import org.MiniDev.Login.PasswordUtils;
import org.MiniDev.OOP.EmployeeHistoryLists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static UI.UserFinalSettingsVar.*;
import static org.MiniDev.Cashier.CreateTodaySalePanel.GAP;
import static org.MiniDev.Customer.CreateCustomerPanel.getProfilePictureDefault;

public class WhenProfileClicked {

    private static final Log log = LogFactory.getLog(WhenProfileClicked.class);

    protected JDialog profileDialog;
    protected ImageAvatar imageAvatar = new ImageAvatar();
    protected String currentName = "Admin";

    protected RoundedTextFieldV2 empIDField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 nameField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 jobTitleField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);

    private final RoundedPasswordField txtPassword = new RoundedPasswordField(10, 1, COLOR_LINE_COLOR);
    private final RoundedPasswordField confirmPassword = new RoundedPasswordField(10, 1, COLOR_LINE_COLOR);

    public WhenProfileClicked() {
    }

    public void showProfileClickerUI() {
        profileDialog = new JDialog();
        profileDialog.setUndecorated(true); // Remove window decorations for custom rounding
        profileDialog.setBackground(new Color(0, 0, 0, 0));
        profileDialog.setSize(550, 400);
        profileDialog.setModal(true); // Make the dialog modal
        profileDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        RoundedOptionPane.setRoundedCorners(profileDialog, 0); // Set a more prominent rounding radius
        profileDialog.setLayout(new BorderLayout());

        // Add the panel to the dialog
        profileDialog.getContentPane().add(getEditHoldingPanel());
        profileDialog.pack(); // Resize the dialog to fit the content
        profileDialog.setLocationRelativeTo(null); // Center the dialog on the screen

        // Set the shape for rounded corners after the dialog is packed
        profileDialog.setShape(new RoundRectangle2D.Double(0, 0, profileDialog.getWidth(), profileDialog.getHeight(), 0, 0));

        // Add fade-in animation
        //DialogFadeIn.fadeIn(profileDialog);

        profileDialog.setVisible(true); // Show the dialog
    }


    protected RoundedPanel getEditHoldingPanel() {
        // Create the rounded panel with a custom rounded border
        RoundedPanel profileDialogPane = new RoundedPanel(20);
        profileDialogPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the content
        profileDialogPane.setPreferredSize(new Dimension(profileDialog.getWidth(), profileDialog.getHeight()));
        profileDialogPane.setBackground(COLOR_PANEL_GRAY);
        profileDialogPane.setBorderWidth(1);
        profileDialogPane.setBorderColor(COLOR_PANEL_BORDER_SILVER);
        profileDialogPane.setLayout(new BorderLayout());
        profileDialogPane.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        profileDialogPane.add(getLeftPanel(), BorderLayout.WEST);
        profileDialogPane.add(getRightPanel(), BorderLayout.CENTER);

        return profileDialogPane;
    }


    protected JPanel getLeftPanel() {
        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(190, 430));
        left.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, COLOR_LINE_COLOR));
        left.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add the top panel to the left panel
        left.add(ProfileClickedUICom.getCurrentUserImagePanel(currentName, imageAvatar, getProfilePictureDefault()), BorderLayout.NORTH);

        return left;
    }


    protected JPanel getRightPanel() {
        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        right.add(closeButtPanel(), BorderLayout.NORTH);
        right.add(getCenterPanel(), BorderLayout.CENTER);
        right.add(createChangeButtonPanel(), BorderLayout.SOUTH);

        return right;
    }

    protected JPanel getCenterPanel() {
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(0, GAP, 0, GAP));
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        center.add(Box.createVerticalStrut(15));
        center.add(ProfileClickedUICom.getProfileField("Login ID", empIDField, getEmpLoginIDName(CreateCashDrawerPanel.tellerID)));
        center.add(Box.createVerticalStrut(15));
        center.add(ProfileClickedUICom.getProfileField("Login Name", nameField, getEmpLoginIDName(CreateCashDrawerPanel.tellerID)));
        center.add(Box.createVerticalStrut(15));
        center.add(ProfileClickedUICom.getProfileField("Login Role", jobTitleField, getEmpLoginIDName(CreateCashDrawerPanel.tellerID)));
        center.add(Box.createVerticalStrut(15));
        center.add(ProfileClickedUICom.getPasswordResetField("Password", txtPassword, "Password"));
        center.add(Box.createVerticalStrut(15));
        center.add(ProfileClickedUICom.getPasswordResetField("", confirmPassword, "Confirm Password"));
        center.add(Box.createVerticalStrut(15));

        return center;
    }

    public static String getEmpLoginIDName(int ID) {
        FetchEmployeeHistoryLists fetchEmployeeHistoryLists = new FetchEmployeeHistoryLists();
        List<EmployeeHistoryLists> empLists = fetchEmployeeHistoryLists.getEmployeeHistoryLists();

        return empLists.stream().filter(p -> p.getEmployeeId() == ID).findFirst().get().getEmployeeNameID();
    }

    protected JPanel closeButtPanel() {
        JPanel closePanel = new JPanel(new BorderLayout());
        closePanel.setOpaque(false);
        closePanel.setPreferredSize(new Dimension(550, 40));
        closePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 1));
        closePanel.add(ProfileClickedUICom.getCloseThisWindowButt(profileDialog), BorderLayout.EAST);

        return closePanel;
    }

    protected JPanel createChangeButtonPanel() {
        JPanel changePanel = new JPanel(new BorderLayout());
        changePanel.setOpaque(false);
        changePanel.setPreferredSize(new Dimension(550, 45));
        changePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 7, 10));

        IconTextButton submitButton = ProfileClickedUICom.getSubmitButton();
        submitButton.addActionListener(e -> handlePasswordChange());

        changePanel.add(submitButton, BorderLayout.EAST);

        return changePanel;
    }

    private void handlePasswordChange() {
        char[] password = txtPassword.getPassword();
        char[] confirmPasswordArray = confirmPassword.getPassword();

        if (password.length == 0 || confirmPasswordArray.length == 0) {
            JOptionPane.showMessageDialog(MiniDevPOS.frame, "Password fields cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!java.util.Arrays.equals(password, confirmPasswordArray)) {
            JOptionPane.showMessageDialog(MiniDevPOS.frame, "Passwords do not match. Please try again.", "Validation Error", JOptionPane.ERROR_MESSAGE);
        } else {
            dbBatchProcessReset(CreateCashDrawerPanel.tellerID, password);
            profileDialog.setVisible(false);
            // Consider securely wiping the password array after use
            JOptionPane.showMessageDialog(MiniDevPOS.frame, "Your password has been successfully updated.", "Success", JOptionPane.INFORMATION_MESSAGE);

            profileDialog.dispose();
        }

        // Clear the password arrays to avoid keeping sensitive information in memory
        java.util.Arrays.fill(password, '\0');
        java.util.Arrays.fill(confirmPasswordArray, '\0');
    }


    public void dbBatchProcessReset(int empID,
                                    char[] newRawPassword) {
        String sql = "{CALL sp_ResetTellerPassword(?, ?)}";
        String p_HashedPassword = PasswordUtils.hashPassword(new String(newRawPassword));

        try (java.sql.Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, empID);
            stmt.setString(2, p_HashedPassword);

            // Execute the stored procedure
            stmt.execute();

        } catch (SQLException e) {
            System.err.println("SQL Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Message: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to execute stored procedure. See logs for details.");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Unexpected error occurred while processing.");
        }


    }


}
