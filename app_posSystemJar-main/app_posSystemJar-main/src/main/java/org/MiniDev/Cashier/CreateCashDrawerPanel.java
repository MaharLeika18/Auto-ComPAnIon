package org.MiniDev.Cashier;

import DBConnection.DBConnection;
import Security.BatchUserDrawerChecking;
import SqlLoadersAndUpdater.FetchTellerDrawer;
import UI.*;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.Login.Login;
import org.MiniDev.OOP.Drawer;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import static UI.UserFinalSettingsVar.*;

public class CreateCashDrawerPanel extends CreateTodaySalePanel {

    protected static RoundedTextField openingDrawerTextField;
    protected static RoundedTextField cashPaymentSaleTextField;
    protected static RoundedTextField otherPaymentSaleTextField;
    protected static RoundedTextField expectedDrawerTextField;
    protected static RoundedTextField differenceTextField;

    protected static Drawer drawer;
    protected static String initializedOpeningAmountText;
    protected static String initializedCashPaymentSaleText;
    protected static String initializedOtherPaymentSaleText;
    protected static String initializedExpectedDrawerText;
    protected static String initializedDifferenceText;

    protected static JButton closeDrawerButton;
    protected static JButton openDrawerButton;

    static DecimalFormat decimalFormat = new DecimalFormat("#,###");

    /**
     * Creates the main panel for cash drawer.
     *
     * @return A JPanel containing the entire cash drawer panel.
     */
    protected static JPanel createCashDrawerPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, GAP, GAP, GAP));

        mainPanel.add(createHeaderLabel("Drawer Amount Summary"), BorderLayout.NORTH);
        mainPanel.add(createCashDrawerSettingsPanel(), BorderLayout.CENTER);
        mainPanel.add(createSettingsBottomPanel(), BorderLayout.SOUTH);

        return mainPanel;
    }


    protected static RoundedPanel createCashDrawerSettingsPanel() {
        var cashDrawerSettingsMainPane = new RoundedPanel(10);
        cashDrawerSettingsMainPane.setLayout(new BorderLayout());
        cashDrawerSettingsMainPane.setBorder(BorderFactory.createEmptyBorder(GAP, 3, GAP, 3));
        cashDrawerSettingsMainPane.setBackground(COLOR_WHITE);

        cashDrawerSettingsMainPane.add(createDifferencePane(), BorderLayout.SOUTH);
        cashDrawerSettingsMainPane.add(createTopLayerPane(drawer.thereIsOpeningBalance(), drawer), BorderLayout.CENTER);

        return cashDrawerSettingsMainPane;
    }


    public static FetchTellerDrawer fetchTellerDrawer = new FetchTellerDrawer();

    public static void refreshDrawerData() {
        Drawer newDrawer = fetchTellerDrawer.getDrawerByTellerID(tellerID);
        initializedOpeningAmountText = String.valueOf(decimalFormat.format(newDrawer.getOpeningAmt()));
        initializedCashPaymentSaleText = String.valueOf(decimalFormat.format(newDrawer.getCashSellingAmt()));
        initializedOtherPaymentSaleText = String.valueOf(decimalFormat.format(newDrawer.getOtherPaymentAmt()));
        initializedExpectedDrawerText = String.valueOf(decimalFormat.format(newDrawer.getOpeningAmt() + newDrawer.getCashSellingAmt()));
        initializedDifferenceText = String.valueOf(decimalFormat.format(newDrawer.getOpeningAmt() + newDrawer.getCashSellingAmt()));

        openingDrawerTextField.setText(initializedOpeningAmountText);
        cashPaymentSaleTextField.setText(initializedCashPaymentSaleText);
        otherPaymentSaleTextField.setText(initializedOtherPaymentSaleText);
        expectedDrawerTextField.setText(initializedExpectedDrawerText);
        differenceTextField.setText(initializedDifferenceText);

        //This is the components of CreateTodaySalePanel
        cashPaymentAmountNumber.setText(decimalFormat.format(newDrawer.getCashSellingAmt()) + " Ks");
        otherPaymentAmountNumber.setText(decimalFormat.format(newDrawer.getOtherPaymentAmt()) + " Ks");
    }


    public static int tellerID;

    public static void fetchDrawerData() {
        if (drawer != null) {
            drawer = fetchTellerDrawer.getDrawerByTellerID(tellerID);
        } else {
            String username = Login.txtUsername.getText().trim();
            tellerID = CreateCashDrawerPanel.fetchTellerDrawer.getTellerIDByUsername(username);

            if (tellerID != -1) {
                drawer = fetchTellerDrawer.getDrawerByTellerID(tellerID);

                if (drawer.getOpeningAmt() == 0) {
                    initializedOpeningAmountText = "0";
                } else {
                    initializedOpeningAmountText = String.valueOf(decimalFormat.format(drawer.getOpeningAmt()));
                }

                initializedCashPaymentSaleText = String.valueOf(decimalFormat.format(drawer.getCashSellingAmt()));
                initializedOtherPaymentSaleText = String.valueOf(decimalFormat.format(drawer.getOtherPaymentAmt()));
                initializedExpectedDrawerText = String.valueOf(decimalFormat.format(drawer.getOpeningAmt() + drawer.getCashSellingAmt()));
                initializedDifferenceText = String.valueOf(decimalFormat.format(drawer.getOpeningAmt() + drawer.getCashSellingAmt()));

                if (drawer != null) {
                    // Drawer data has been successfully fetched and assigned
                } else {
                    // Handle the case where the drawer data could not be fetched
                    JOptionPane.showMessageDialog(null, "Drawer data not found for the current Teller ID.");
                }
            } else {
                // Handle the case where the teller ID could not be retrieved
                JOptionPane.showMessageDialog(null, "Teller ID not found for the given username.");
            }
        }
    }


    protected static JPanel createTopLayerPane(boolean thereIsOpeningBalance, Drawer drawer) {

        JPanel topLayerMainPane = new JPanel(new GridLayout(4, 1, 0, 0));
        topLayerMainPane.setBorder(BorderFactory.createEmptyBorder(0, GAP, 0, GAP));
        topLayerMainPane.setBackground(COLOR_WHITE);

        if (!thereIsOpeningBalance) {
            topLayerMainPane.add(createPanel("Opening Drawer Amount", true, COLOR_GRAY, initializedOpeningAmountText, drawer));
            topLayerMainPane.add(createPanel("Cash Payment Sale", false, COLOR_WHITE, initializedCashPaymentSaleText, drawer));
            topLayerMainPane.add(createPanel("Other Payment Sale", false, COLOR_GRAY, initializedOtherPaymentSaleText, drawer));
            topLayerMainPane.add(createPanel("Expected Drawer Amount", false, COLOR_WHITE, initializedExpectedDrawerText, drawer));
        } else {
            topLayerMainPane.add(createPanel("Opening Drawer Amount", false, COLOR_GRAY, initializedOpeningAmountText, drawer));
            topLayerMainPane.add(createPanel("Cash Payment Sale", false, COLOR_WHITE, initializedCashPaymentSaleText, drawer));
            topLayerMainPane.add(createPanel("Other Payment Sale", false, COLOR_GRAY, initializedOtherPaymentSaleText, drawer));
            topLayerMainPane.add(createPanel("Expected Drawer Amount", false, COLOR_WHITE, initializedExpectedDrawerText, drawer));
        }


        return topLayerMainPane;
    }

    private static RoundedTextField createFormattedTextField(boolean isEditable, Color rowPanelColor, String initializedValue) {
        // Create the NumberFormat and DocumentFilter
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setGroupingUsed(true); // Enable grouping (e.g., commas)

        NumberFormatDocumentFilter filter = new NumberFormatDocumentFilter(numberFormat);

        // Create the RoundedTextField
        RoundedTextField textField = new RoundedTextField(10, 2, COLOR_LINE_COLOR);
        textField.setPreferredSize(new Dimension(200, 40));
        textField.setBackground(rowPanelColor);
        textField.setEditable(isEditable);
        textField.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 14)); // Default font size
        textField.setText(initializedValue);

        // Apply the DocumentFilter
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(filter);

        return textField;
    }

    private static RoundedPanel createPanel(String labelText, boolean isEditable, Color rowPanelColor, String initializedValue, Drawer initializedDrawer) {
        RoundedPanel panel = new RoundedPanel(10);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, GAP, 0, GAP));
        panel.setBackground(rowPanelColor);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13)); // Default font size

        // Create the formatted text field
        RoundedTextField textField = createFormattedTextField(isEditable, rowPanelColor, initializedValue);

        // Update the specific text field references
        switch (labelText) {
            case "Opening Drawer Amount" -> openingDrawerTextField = textField;
            case "Cash Payment Sale" -> cashPaymentSaleTextField = textField;
            case "Other Payment Sale" -> otherPaymentSaleTextField = textField;
            case "Expected Drawer Amount" -> expectedDrawerTextField = textField;
        }


        JPanel labelPanel = createLabelPanel(label);
        JPanel textFieldPanel = createTextFieldPanel(textField);

        panel.add(labelPanel, BorderLayout.WEST);
        panel.add(textFieldPanel, BorderLayout.EAST);

        // Ensure the panel is refreshed
        panel.revalidate();
        panel.repaint();

        return panel;
    }


    private static JPanel createLabelPanel(JLabel label) {
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPanel.setBorder(BorderFactory.createEmptyBorder(GAP, 0, 0, 0));
        labelPanel.setOpaque(false); // Ensure the label panel is transparent
        labelPanel.add(label);
        return labelPanel;
    }

    private static JPanel createTextFieldPanel(RoundedTextField textField) {
        JPanel textFieldPanel = new JPanel();
        textFieldPanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 5));
        textFieldPanel.setOpaque(false); // Ensure the text field panel is transparent
        textFieldPanel.add(textField);
        return textFieldPanel;
    }


    protected static JPanel createDifferencePane() {
        JPanel differencePanel = new JPanel(new BorderLayout());
        differencePanel.setBackground(COLOR_WHITE);
        differencePanel.setPreferredSize(new Dimension(0, 60));

        Border matteBorder = BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_LINE_COLOR);
        Border emptyBorder = BorderFactory.createEmptyBorder(0, GAP * 2, 0, GAP * 2);
        Border combinedBorder = BorderFactory.createCompoundBorder(matteBorder, emptyBorder);
        differencePanel.setBorder(combinedBorder);

        JLabel differenceLabel = new JLabel("Difference");
        differenceLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 15)); // Default font size

        differenceTextField = new RoundedTextField(10, 2, COLOR_LINE_COLOR);
        differenceTextField.setPreferredSize(new Dimension(200, 40));
        differenceTextField.setBackground(COLOR_WHITE);
        differenceTextField.setEditable(false);
        differenceTextField.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 14)); // Default font size
        differenceTextField.setText(initializedDifferenceText);

        JPanel labelPanel = createLabelPanel(differenceLabel);
        labelPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));
        JPanel textFieldPanel = createTextFieldPanel(differenceTextField);
        textFieldPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 5));

        differencePanel.add(labelPanel, BorderLayout.WEST);
        differencePanel.add(textFieldPanel, BorderLayout.EAST);

        // Ensure the panel is refreshed
        differencePanel.revalidate();
        differencePanel.repaint();

        return differencePanel;
    }

    protected static JPanel createSettingsBottomPanel() {
        JPanel settingsBottomPane = new JPanel(new BorderLayout());
        settingsBottomPane.setPreferredSize(new Dimension(0, 300));

        settingsBottomPane.add(createHeaderLabel("Remarks"), BorderLayout.NORTH);
        settingsBottomPane.add(createClearancePanel(), BorderLayout.CENTER);

        return settingsBottomPane;
    }

    protected static JPanel createClearancePanel() {
        JPanel clearancePanel = new JPanel(new BorderLayout());
        clearancePanel.add(createTextFieldAndButtonPanel(drawer.thereIsOpeningBalance()), BorderLayout.NORTH);
        return clearancePanel;
    }

    protected static JPanel buttonPanel;


    /**
     * Creates a panel containing a text field and a button.
     *
     * @param thereIsOpeningBalance Boolean indicating if there is an opening balance.
     * @return JPanel containing the text field and button.
     */
    protected static JPanel createTextFieldAndButtonPanel(boolean thereIsOpeningBalance) {
        // Create the main panel with GridLayout for text field and button
        JPanel textFieldAndButtonPanel = new JPanel(new GridLayout(2, 1));
        textFieldAndButtonPanel.setPreferredSize(new Dimension(0, 150));

        // Create the text field panel with BorderLayout
        JPanel textFieldPanel = new JPanel(new BorderLayout());
        textFieldPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Create and initialize button panel
        buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(GAP, 0, GAP, 0));

        // Add the text area to the text field panel
        textFieldPanel.add(createClosingRemarksTextArea(), BorderLayout.WEST);

        // Add the appropriate button to the button panel based on the opening balance
        if (thereIsOpeningBalance) {
            buttonPanel.add(createClosingDrawerButton(), BorderLayout.WEST);
        } else {
            buttonPanel.add(createOpenDrawerButton(), BorderLayout.WEST);
        }

        // Add panels to the main panel
        textFieldAndButtonPanel.add(textFieldPanel);
        textFieldAndButtonPanel.add(buttonPanel);

        return textFieldAndButtonPanel;
    }


    protected static RoundedTextArea remarkArea;

    protected static RoundedTextArea createClosingRemarksTextArea() {
        int borderRadius = 10;

        remarkArea = new RoundedTextArea(borderRadius, COLOR_LINE_COLOR, 88);
        remarkArea.setEditable(true);
        remarkArea.setBackground(COLOR_WHITE);

        // Set the number of rows to 2
        remarkArea.setRows(2);
        remarkArea.setPlaceholder("Enter counter remarks.");

        // Optionally set line wrap and word wrap
        remarkArea.setLineWrap(true);
        remarkArea.setWrapStyleWord(true);

        // Adjust the preferred size to accommodate the border and two rows of text
        remarkArea.setPreferredSize(new Dimension(400, 60)); // Adjust height as needed for two rows

        // Optionally set padding within the text area
        remarkArea.setMargin(new Insets(10, 10, 10, 10));

        return remarkArea;
    }

    protected static void getLiveExpectedAmount(Drawer drawer) {
        double expectedAmount = 0.0;
        String text = expectedDrawerTextField.getText().trim();

        if (text.isBlank()) {
            expectedAmount = 0.0;
        } else {
            try {
                // Parse the string using NumberFormat to handle commas or other formatting
                NumberFormat format = NumberFormat.getNumberInstance(Locale.US); // Use appropriate Locale
                Number number = format.parse(text);
                expectedAmount = number.doubleValue();
            } catch (ParseException e) {
                // Handle the error (optional: log it or notify the user)
                e.printStackTrace();
                expectedAmount = 0.0; // Default to 0.0 if parsing fails
            }
        }

        // Calculate the difference and set it in the differenceTextField
        double difference = drawer.getCashSellingAmt() + drawer.getOpeningAmt() - expectedAmount;
        differenceTextField.setText(decimalFormat.format(difference));
    }


    protected static void closeDrawerIntoDatabase() {
        String sql = "{CALL sp_CloseDrawer(?,?)}";
        String closedRemarks = remarkArea.getText();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, tellerID);
            stmt.setString(2, closedRemarks);

            // Log the parameters being passed
            LOGGER.info("Closing drawer for TellerID: " + tellerID + " with remarks: " + closedRemarks);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("Successfully updated ClosedDrawerAmount for tellerID: " + tellerID);
            } else {
                LOGGER.warning("No rows affected. Please check the tellerID: " + tellerID);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating ClosedDrawerAmount for tellerID: " + tellerID, e);
        }
    }


    protected static void clearLabelsAfterDrawerClose() {
        remarkArea.setText("");
        openingDrawerTextField.setText("0");
        cashPaymentSaleTextField.setText("0");
        otherPaymentSaleTextField.setText("0");
        expectedDrawerTextField.setText("0");
        differenceTextField.setText("0");

        openingDrawerAmountNumber.setText("0 Ks");
        otherPaymentAmountNumber.setText("0 Ks");
        cashPaymentAmountNumber.setText("0 Ks");
    }


    /**
     * Parses a string representing a currency value, removing any commas or formatting.
     *
     * @param input The string to parse.
     * @return The parsed double value.
     * @throws ParseException if the input string cannot be parsed.
     */
    private static double parseCurrency(String input) throws ParseException {
        if (input.isEmpty()) {
            return 0;
        } else {
            NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
            Number number = format.parse(input);
            return number.doubleValue();
        }
    }


    private static final Logger LOGGER = Logger.getLogger(CreateCashDrawerPanel.class.getName());

    /**
     * Creates and returns the "Close Drawer" button.
     *
     * @return JButton for closing the drawer.
     */
    protected static JButton createClosingDrawerButton() {
        if (closeDrawerButton == null) {
            closeDrawerButton = new IconTextButton("Close Drawer", null, 10, COLOR_BLUE, 0);
            closeDrawerButton.setBackground(COLOR_BLUE);
            closeDrawerButton.setForeground(COLOR_WHITE);
            closeDrawerButton.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));
            closeDrawerButton.setPreferredSize(new Dimension(250, 40));

            closeDrawerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    switchButton(createOpenDrawerButton()); // Ensure using createOpenDrawerButton
                    openingDrawerTextField.setEditable(true);
                    closeDrawerIntoDatabase();
                    clearLabelsAfterDrawerClose();
                    BatchUserDrawerChecking.updateDrawer();
                }
            });
        }
        return closeDrawerButton;
    }

    /**
     *
     *
     * Creates and returns the "Open Drawer" button.
     *
     * @return JButton for opening the drawer.
     */
    public static JButton createOpenDrawerButton() {
        if (openDrawerButton == null) {
            openDrawerButton = new IconTextButton("Open Drawer", null, 10, COLOR_GREEN, 0);
            openDrawerButton.setBackground(COLOR_GREEN);
            openDrawerButton.setForeground(COLOR_WHITE);
            openDrawerButton.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));
            openDrawerButton.setPreferredSize(new Dimension(250, 40));

            openDrawerButton.addActionListener(e -> {
                try {
                    double userInput = parseCurrency(openingDrawerTextField.getText());
                    switchButton(createClosingDrawerButton()); // Ensure using createClosingDrawerButton
                    openWithUserInputIntoDatabase(userInput, tellerID);
                    openingDrawerTextField.setEditable(false);
                    remarkArea.setText("");
                    updateOpeningAmount(userInput);
                    DialogCollection.showCustomDialog(new RoundedPanel(10), "Opened drawer with " + openingDrawerTextField.getText() + " successfully.", "Opened Successfully");
                    BatchUserDrawerChecking.updateDrawer();
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input format. Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    LOGGER.log(Level.WARNING, "Failed to parse user input: " + openingDrawerTextField.getText(), ex);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "An error occurred while opening the drawer. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    LOGGER.log(Level.SEVERE, "Error occurred while opening the drawer.", ex);
                }
            });
        }
        return openDrawerButton;
    }

    /**
     * Switches the button in the button panel based on the provided button.
     *
     * @param newButton JButton to be added to the button panel.
     */
    protected static void switchButton(JButton newButton) {
        if (buttonPanel != null && newButton != null) {
            buttonPanel.removeAll();
            buttonPanel.add(newButton, BorderLayout.WEST);
            buttonPanel.revalidate();
            buttonPanel.repaint();
        } else {
            LOGGER.warning("Button panel or new button is not initialized.");
        }
    }

    /**
     * Updates the OpeningDrawerAmount in the ClaimableDrawer table for a specific teller.
     *
     * @param userInput The amount to set for the opening drawer.
     */
    protected static void openWithUserInputIntoDatabase(double userInput, int tellerID) {
        String sql = "{CALL sp_OpenDrawer(?, ?, ?)}"; // Ensure correct parameter count
        String openRemarks = remarkArea.getText(); // Make sure remarkArea is properly initialized

        try (Connection connection = DBConnection.getConnection();
             CallableStatement stmt = connection.prepareCall(sql)) {

            // Set the parameters in the correct order and with the correct data types
            stmt.setDouble(1, userInput);
            stmt.setString(2, openRemarks);
            stmt.setInt(3, tellerID);

            // Execute the stored procedure
            boolean hasResults = stmt.execute();

            // Check if the execution resulted in an update count
            if (!hasResults) {
                int updateCount = stmt.getUpdateCount();
                if (updateCount > 0) {
                    LOGGER.info("Successfully updated OpeningDrawerAmount for tellerID: " + tellerID);
                } else {
                    LOGGER.warning("Please check the procedure execution.");
                }
            } else {
                // Handle if a result set is returned, if applicable
                LOGGER.info("Procedure executed, but no result set was returned.");
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating OpeningDrawerAmount for tellerID: " + tellerID, e);
        }
    }

    protected static boolean isDrawerClosed(int tellerID) {
        String sql = "SELECT ClosedStatus FROM ClaimableDrawer WHERE tellerID = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, tellerID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String state = rs.getString("ClosedStatus"); // Assuming there's a column for drawer state
                    return "Y".equals(state); // Adjust based on actual state values
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking drawer state for tellerID: " + tellerID, e);
        }
        return false;
    }


}
