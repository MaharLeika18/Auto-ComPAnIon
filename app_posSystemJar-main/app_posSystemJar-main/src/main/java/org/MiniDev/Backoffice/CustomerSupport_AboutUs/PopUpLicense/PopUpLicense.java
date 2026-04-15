package org.MiniDev.Backoffice.CustomerSupport_AboutUs.PopUpLicense;

import DBConnection.DBConnection;
import UI.*;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.Home.SubPopUpWindows.ProfileClickedUICom.ProfileClickedUICom;
import org.MiniDev.OOP.CurrentLicenseInfo;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.util.List;

import static UI.IconCreator.createResizedIcon;
import static UI.UserFinalSettingsVar.*;
import static org.MiniDev.Cashier.CreateTodaySalePanel.GAP;

public class PopUpLicense {

    public static JDialog editDialog;
    public static RoundedPanel editPanel;
    protected RoundedTextFieldV2 yourNameField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 emailField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 licenseKeyField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);

    public PopUpLicense() {

    }

    public void showPopupWhenEditLicense(List<CurrentLicenseInfo> currentLicenseInfos) {
        editDialog = new JDialog();
        editDialog.setUndecorated(true); // Remove window decorations for custom rounding
        editDialog.setBackground(new Color(0, 0, 0, 0));
        editDialog.setSize(500, 340);
        editDialog.setModal(true); // Make the dialog modal
        editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        RoundedOptionPane.setRoundedCorners(editDialog, 0);
        editDialog.setLayout(new BorderLayout());

        // Add the panel to the dialog
        editDialog.getContentPane().add(getEditHoldingPanel(currentLicenseInfos));
        editDialog.pack(); // Resize the dialog to fit the content
        editDialog.setLocationRelativeTo(null); // Center the dialog on the screen

        // Set the shape for rounded corners
        editDialog.setShape(new RoundRectangle2D.Double(0, 0, editDialog.getWidth(), editDialog.getHeight(), 0, 0));

        // Add fade-in animation
        DialogFadeIn.fadeIn(editDialog);

        editDialog.setVisible(true); // Show the dialog
    }

    protected RoundedPanel getEditHoldingPanel(List<CurrentLicenseInfo> currentLicenseInfos) {
        // Create the rounded panel with a custom rounded border
        editPanel = new RoundedPanel(20);
        editPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the content
        editPanel.setPreferredSize(new Dimension(editDialog.getWidth(), editDialog.getHeight()));
        editPanel.setBackground(COLOR_GRAY);
        editPanel.setLayout(new BorderLayout());
        editPanel.setBorderWidth(1);
        editPanel.setBorderColor(COLOR_PANEL_BORDER_SILVER);

        editPanel.add(closeButtPanel(), BorderLayout.NORTH);
        editPanel.add(getCenterPanel(currentLicenseInfos), BorderLayout.CENTER);
        editPanel.add(changeButtPanel(currentLicenseInfos), BorderLayout.SOUTH);

        return editPanel;
    }

    protected JPanel getCenterPanel(List<CurrentLicenseInfo> currentLicenseInfos) {
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(0, GAP, 0, GAP));

        center.add(headerPanel(), BorderLayout.NORTH);
        center.add(leftPanel(), BorderLayout.WEST);
        center.add(rightPanel(currentLicenseInfos), BorderLayout.CENTER);

        return center;
    }

    protected JPanel headerPanel() {
        // Create a new panel with BoxLayout, aligned vertically (Y_AXIS)
        JPanel header = new JPanel();
        header.setPreferredSize(new Dimension(300, 60));  // Preferred size for the header panel
        header.setOpaque(false);  // Make the background of the panel transparent
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));  // Vertical alignment for the labels

        // Create two labels
        JLabel headerLabel = new JLabel("Thank you for choosing MiniDev POS.");
        JLabel descLabel = new JLabel("Please input your license key to activate.");

        // Set fonts for both labels
        headerLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 14));
        descLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 14));

        // Center align both labels horizontally and vertically
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setVerticalAlignment(SwingConstants.CENTER);

        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        descLabel.setVerticalAlignment(SwingConstants.CENTER);

        // Align the labels vertically within the panel using BoxLayout
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Horizontally center the first label
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Horizontally center the second label

        // Add the labels to the panel
        header.add(headerLabel);
        header.add(Box.createVerticalStrut(10));
        header.add(descLabel);

        return header;
    }


    protected JPanel rightPanel(List<CurrentLicenseInfo> currentLicenseInfos) {
        JPanel right = new JPanel(new GridLayout(3, 1, 12, 12));
        right.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
        right.setOpaque(false);

        right.add(getInputRowPanel(yourNameField, currentLicenseInfos.get(0).getKeyRegName(), "Your Name:", "eg. Mg Mg"));
        right.add(getInputRowPanel(emailField, currentLicenseInfos.get(0).getKeyRegEmail(), "Email:", "eg. mgmg@gmail.com"));
        right.add(getInputRowPanel(licenseKeyField, currentLicenseInfos.get(0).getKeyCode(), "License Key:", "eg. A000-B000-C000-D000"));

        return right;
    }

    protected JPanel getInputRowPanel(RoundedTextFieldV2 field, String currentValue, String labelName, String placeHolderValue) {
        // Create the main rowPanel with BorderLayout
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setOpaque(false); // Panel will have transparent background

        // Create the inner panels for left (label) and right (field)
        JPanel innerLeft = new JPanel(new BorderLayout());
        innerLeft.setOpaque(false); // Make innerLeft transparent (background won't interfere)
        innerLeft.setPreferredSize(new Dimension(100, 200));
        innerLeft.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel innerRight = new JPanel();
        innerRight.setOpaque(false); // Ensure innerRight panel is transparent
        innerRight.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Use FlowLayout to avoid stretching

        // Create the label for the field
        JLabel label = new JLabel(labelName);
        label.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 12));

        // Configure the field
        field.setHorizontalAlignment(SwingConstants.LEFT);
        field.setText(currentValue);
        field.setPreferredSize(new Dimension(230, 38)); // Set preferred size for the field
        field.setMaximumSize(new Dimension(230, 38));
        field.setMinimumSize(new Dimension(230, 38));
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeHolderValue);

        // Add the label to the left panel
        innerLeft.add(label, BorderLayout.NORTH);

        // Add the field to the right panel
        innerRight.add(field); // Add the field without BorderLayout.CENTER

        // Add the inner panels to the rowPanel
        rowPanel.add(innerLeft, BorderLayout.WEST); // Changed from NORTH to WEST to avoid vertical stacking
        rowPanel.add(innerRight, BorderLayout.CENTER);

        return rowPanel;
    }


    protected JPanel leftPanel() {
        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(100, 200));

        SvgIcon keyIcon = new SvgIcon("/LockedKey.svg", 100); // Ensure this path is correct
        JLabel keyIconLabel = new JLabel(keyIcon.getImageIcon());

        left.add(keyIconLabel, BorderLayout.WEST);

        return left;
    }


    protected JPanel closeButtPanel() {
        JPanel closePanel = new JPanel(new BorderLayout());
        closePanel.setOpaque(false);
        closePanel.setPreferredSize(new Dimension(550, 40));
        closePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 1));
        closePanel.add(ProfileClickedUICom.getCloseThisWindowButt(editDialog), BorderLayout.EAST);

        return closePanel;
    }

    public IconTextButton getSubmitButton(List<CurrentLicenseInfo> currentLicenseInfos) {
        ImageIcon updateIcon = createResizedIcon("/UpdateIcon.svg", 20, null);
        IconTextButton updateButton = new IconTextButton("Submit", updateIcon, 14, (COLOR_BUTTON_BLUE), 0);
        updateButton.setBackground(COLOR_BUTTON_BLUE);
        updateButton.setForeground(COLOR_WHITE);
        updateButton.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));
        updateButton.setPreferredSize(new Dimension(100, 38));

        updateButton.setIcon(updateIcon);

        updateButton.setHorizontalTextPosition(SwingConstants.RIGHT); // Icon on the left
        updateButton.setVerticalTextPosition(SwingConstants.CENTER);
        updateButton.setHorizontalAlignment(SwingConstants.CENTER);
        updateButton.setVerticalAlignment(SwingConstants.CENTER);

        updateButton.addActionListener(e -> {
            checkInputIsBlank(currentLicenseInfos);
        });

        return updateButton;
    }

    protected JPanel changeButtPanel(List<CurrentLicenseInfo> currentLicenseInfos) {
        JPanel changePanel = new JPanel(new BorderLayout());
        changePanel.setOpaque(false);
        changePanel.setPreferredSize(new Dimension(550, 45));
        changePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 7, 10));

        changePanel.add(getSubmitButton(currentLicenseInfos), BorderLayout.EAST);
        return changePanel;
    }

    private void checkInputIsBlank(List<CurrentLicenseInfo> currentLicenseInfos) {
        if (currentLicenseInfos.get(0).getKeyCode().equals(licenseKeyField.getText())) {
            JOptionPane.showMessageDialog(editDialog, "Your License Code should be new.", "Invalid Code", JOptionPane.ERROR_MESSAGE);
        } else if (yourNameField.getText().isBlank() || emailField.getText().isBlank() || licenseKeyField.getText().isBlank()) {
            // Show error message if any field is blank
            JOptionPane.showMessageDialog(editDialog, "All fields must be filled out.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } else {
            // If all fields are filled, call the method to update the database
            boolean isUpdateSuccessful = updateKeyMeIntoDB(yourNameField.getText(), emailField.getText(), licenseKeyField.getText());

            if (isUpdateSuccessful) {
                JOptionPane.showMessageDialog(editDialog, "License Registration is complete", "License Registration Completed", JOptionPane.INFORMATION_MESSAGE);
                editDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(editDialog, "An error occurred while registering the license. Contact to the provider.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    public static boolean updateKeyMeIntoDB(String regName, String regEmail, String regNewKeyCode) {
        String sql = "{CALL sp_UpdateKeyMe(?, ?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            // Set parameters for the stored procedure
            stmt.setString(1, regNewKeyCode);
            stmt.setString(2, regName);
            stmt.setString(3, regEmail);

            // Execute the stored procedure
            stmt.execute();  // Use executeUpdate for DML (insert, update, delete)

            return true; // Indicate that the update was successful

        } catch (SQLTransientConnectionException e) {
            // Handle SQLTransientConnectionException (temporary connection issue)
            System.err.println("Temporary connection issue while updating the key: " + e.getMessage());
            e.printStackTrace();
            return false; // Indicate that the update failed due to connection issues

        } catch (SQLException e) {
            // Handle general SQL exceptions (e.g., invalid query, constraints violations, etc.)
            System.err.println("SQL error while updating key: " + e.getMessage());
            e.printStackTrace();
            return false; // Indicate that the update failed due to a SQL error

        } catch (Exception e) {
            // Handle any other unexpected exceptions
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return false; // Indicate failure due to unexpected error
        }

    }

}
