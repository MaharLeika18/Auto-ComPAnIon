package org.MiniDev.Backoffice.StockManagement.CounterTableActionPages;

import SqlLoadersAndUpdater.UpdateItemIntoDB;
import UI.*;
import Utils.PrinterDiscovery;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.Backoffice.StockManagement.CounterInnerPage;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static UI.UserFinalSettingsVar.*;
import static org.MiniDev.Cashier.CreateTodaySalePanel.GAP;

public class CounterEditDialogPage {

    protected JDialog counterEditDialog;
    protected RoundedTextFieldV2 counterNameField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedComboBox<String> comboBoxActiveYN = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);
    protected RoundedComboBox<String> comboBoxPrinterAddress = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);
    protected RoundedComboBox<String> comboBoxCounterLevel = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);

    public CounterEditDialogPage() {
    }

    public void showCounterEditPanel(int initializedCounterID, String initializedCounterName, int initializedCounterLevel, char initializedChar, String initializedPrinterAddress) {
        counterEditDialog = new JDialog();
        counterEditDialog.setUndecorated(true); // Remove window decorations for custom rounding
        counterEditDialog.setBackground(new Color(0, 0, 0, 0));
        counterEditDialog.setSize(500, 400);
        counterEditDialog.setModal(true); // Make the dialog modal
        counterEditDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        RoundedOptionPane.setRoundedCorners(counterEditDialog, 0);
        counterEditDialog.setLayout(new BorderLayout());

        // Add the panel to the dialog
        counterEditDialog.getContentPane().add(getEditHoldingPanel(initializedCounterID, initializedCounterName, initializedCounterLevel, initializedChar, initializedPrinterAddress));
        counterEditDialog.pack(); // Resize the dialog to fit the content
        counterEditDialog.setLocationRelativeTo(null); // Center the dialog on the screen

        // Set the shape for rounded corners
        counterEditDialog.setShape(new RoundRectangle2D.Double(0, 0, counterEditDialog.getWidth(), counterEditDialog.getHeight(), 0, 0));

        // Add fade-in animation
        DialogFadeIn.fadeIn(counterEditDialog);

        counterEditDialog.setVisible(true); // Show the dialog
    }


    protected RoundedPanel getEditHoldingPanel(int initializedCounterID, String initializedCounterName, int initializedCounterLevel, char initializedChar, String initializedPrinterAddress) {
        // Create the rounded panel with a custom rounded border
        RoundedPanel editPanel = new RoundedPanel(20);
        editPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the content
        editPanel.setPreferredSize(new Dimension(counterEditDialog.getWidth(), counterEditDialog.getHeight()));
        editPanel.setBackground(COLOR_ENTRY_GRAY);
        editPanel.setLayout(new BorderLayout());

        editPanel.add(getHeaderPanel(initializedCounterID), BorderLayout.NORTH);
        editPanel.add(getContentPanel(initializedCounterName, initializedCounterLevel, initializedChar, initializedPrinterAddress), BorderLayout.CENTER);
        editPanel.add(getBottomPanel(initializedCounterID), BorderLayout.SOUTH);

        return editPanel;
    }

    protected RoundedPanel getHeaderPanel(int counterID) {
        RoundedPanel headerPanel = new RoundedPanel(10);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        headerPanel.setPreferredSize(new Dimension(counterEditDialog.getWidth(), 50));
        headerPanel.setOpaque(false);
        headerPanel.setBackground(COLOR_GRAY);

        JLabel label = new JLabel("CounterID: " + counterID);
        label.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 14));
        label.setForeground(COLOR_FONT_GRAY);
        label.setHorizontalAlignment(SwingConstants.CENTER); // Center the text

        headerPanel.add(label, BorderLayout.CENTER);

        return headerPanel;
    }

    protected JPanel getContentPanel(String initializedCounterName, int initializedCounterLevel, char initializedChar, String initializedPrinterAddress) {
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout());

        JPanel upperGap = new JPanel();
        upperGap.setOpaque(false);
        upperGap.setPreferredSize(new Dimension(counterEditDialog.getWidth(), 10));

        RoundedPanel mainCenterPanel = new RoundedPanel(10);
        mainCenterPanel.setLayout(new BorderLayout());
        mainCenterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 12, 10));
        mainCenterPanel.setOpaque(false);
        mainCenterPanel.setBackground(COLOR_GRAY);
        mainCenterPanel.add(mainInfoHoldingPanel(initializedCounterName, initializedCounterLevel, initializedChar, initializedPrinterAddress), BorderLayout.CENTER);

        JPanel bottomGap = new JPanel();
        bottomGap.setOpaque(false);
        bottomGap.setPreferredSize(new Dimension(counterEditDialog.getWidth(), 10));

        contentPanel.add(upperGap, BorderLayout.NORTH);
        contentPanel.add(mainCenterPanel, BorderLayout.CENTER);
        contentPanel.add(bottomGap, BorderLayout.SOUTH);

        return contentPanel;
    }


    protected JPanel mainInfoHoldingPanel(String counterName, int InitializedCounterLevel, char initializedActiveYN, String initializedPrinterAddress) {
        JPanel infoHoldingPanel = new JPanel(new GridLayout(4, 1, GAP, GAP));
        infoHoldingPanel.setOpaque(false);

        initializeComboBoxes(initializedActiveYN, initializedPrinterAddress, InitializedCounterLevel);
        initializeTextValues(counterName);

        infoHoldingPanel.add(createRowTextFields("Counter Name:", counterNameField));
        infoHoldingPanel.add(createComboSelectionFields("Counter Level:", comboBoxCounterLevel));
        infoHoldingPanel.add(createComboSelectionFields("Active YN:", comboBoxActiveYN));
        infoHoldingPanel.add(createComboSelectionFields("Printer Name:", comboBoxPrinterAddress));

        return infoHoldingPanel;
    }

    private void initializeTextValues(String counterName) {
        counterNameField.setText(counterName);
    }

    protected JPanel createComboSelectionFields(String headerName, RoundedComboBox<String> comboBox) {
        JPanel comboPanel = new JPanel(new BorderLayout());
        comboPanel.setOpaque(false);

        JPanel innerLeft = new JPanel(new BorderLayout());
        JPanel innerRight = new JPanel(new BorderLayout());

        innerLeft.setOpaque(false);
        innerRight.setOpaque(false);

        innerLeft.setPreferredSize(new Dimension(140, comboPanel.getHeight()));

        JLabel headerLabel = new JLabel(headerName);
        headerLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13));

        innerLeft.add(headerLabel, BorderLayout.WEST);
        innerRight.add(comboBox, BorderLayout.CENTER);

        comboPanel.add(innerLeft, BorderLayout.WEST);
        comboPanel.add(innerRight, BorderLayout.CENTER);
        return comboPanel;
    }

    protected JPanel createRowTextFields(String headerName, RoundedTextFieldV2 textField) {
        JPanel textFieldPanel = new JPanel(new BorderLayout());
        textFieldPanel.setOpaque(false);

        JPanel innerLeft = new JPanel(new BorderLayout());
        JPanel innerRight = new JPanel(new BorderLayout());

        innerLeft.setOpaque(false);
        innerRight.setOpaque(false);

        innerLeft.setPreferredSize(new Dimension(140, textFieldPanel.getHeight()));

        JLabel headerLabel = new JLabel(headerName);
        headerLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13));

        textField.setHorizontalAlignment(SwingConstants.LEFT);
        textField.setEditable(true);
        textField.setBackground(COLOR_WHITE);
        textField.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 12));

        innerLeft.add(headerLabel, BorderLayout.WEST);
        innerRight.add(textField, BorderLayout.CENTER);

        textFieldPanel.add(innerLeft, BorderLayout.WEST);
        textFieldPanel.add(innerRight, BorderLayout.CENTER);
        return textFieldPanel;
    }

    private void initializeComboBoxes(char initializedActiveYN, String printerAddress, int initializedCounterLevel) {
        List<String> activeYNChars = new ArrayList<>(Arrays.asList("Y", "N"));
        List<String> counterLevels = new ArrayList<>(Arrays.asList("1", "2", "3")); // Keep them as Strings

        comboBoxActiveYN.setEditable(true);
        comboBoxPrinterAddress.setEditable(true);
        comboBoxCounterLevel.setEditable(true);

        initializeComboBox(comboBoxActiveYN, activeYNChars, String.valueOf(initializedActiveYN));
        initializeComboBox(comboBoxPrinterAddress, PrinterDiscovery.getAllPrinters(), printerAddress);
        initializeComboBox(comboBoxCounterLevel, counterLevels, String.valueOf(initializedCounterLevel));
    }


    private void initializeComboBox(RoundedComboBox<String> comboBox, List<String> items, String defaultSelection) {
        String[] itemArray = items.toArray(new String[0]);
        comboBox.setModel(new DefaultComboBoxModel<>(itemArray));
        comboBox.setPlaceholder(defaultSelection);
        comboBox.setSelectedItem(defaultSelection);

        JTextField editorField = (JTextField) comboBox.getEditor().getEditorComponent();
        if (editorField != null) {
            editorField.setForeground(Color.GRAY); // Set initial placeholder color
        }
    }

    protected RoundedPanel getBottomPanel(int initializedID) {
        RoundedPanel bottomPanel = new RoundedPanel(10);
        bottomPanel.setPreferredSize(new Dimension(counterEditDialog.getWidth(), 60));
        bottomPanel.setBackground(COLOR_GRAY);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, GAP));
        bottomPanel.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, GAP, 0));
        buttonPanel.setPreferredSize(new Dimension(215, 45));
        buttonPanel.setOpaque(false);
        buttonPanel.add(updateButtonEditPage(initializedID));
        buttonPanel.add(closeButtonEditPage());

        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        return bottomPanel;
    }

    private boolean processCounterUpdateIntoDatabase(int initializedID) {
        try {
            String counterNameToUpdate = counterNameField.getText();
            Object counterLevelToUpdate = Objects.requireNonNull(comboBoxCounterLevel.getSelectedItem());
            char counterActiveYNToUpdate = Objects.requireNonNull(comboBoxActiveYN.getSelectedItem()).toString().charAt(0);
            String printerNameToUpdate = Objects.requireNonNull(comboBoxPrinterAddress.getSelectedItem()).toString();

            if (counterNameToUpdate.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill all the fields", "Error", JOptionPane.ERROR_MESSAGE);
                return false; // Indicate failure
            } else {

                UpdateItemIntoDB.counterUpdaterIntoDB(
                        initializedID,
                        counterNameToUpdate,
                        Integer.parseInt(counterLevelToUpdate.toString()),
                        counterActiveYNToUpdate,
                        printerNameToUpdate
                );
                return true; // Indicate success
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please fill all the fields", "Error", JOptionPane.ERROR_MESSAGE);
            return false; // Indicate failure
        } catch (Exception e) {
            e.printStackTrace(); // Handle other potential exceptions
            return false; // Indicate failure
        }
    }

    private IconTextButton updateButtonEditPage(int initializedCounterID) {
        IconTextButton updateButton = UIAppButtonFactory.createIconTextButton("Update", "/UpdateIcon.svg", COLOR_GREEN);
        updateButton.addActionListener(e -> {
            if (processCounterUpdateIntoDatabase(initializedCounterID)) {
                new CounterInnerPage().refreshTableAfterCounterUpdater();
                counterEditDialog.dispose();
            }
        });
        return updateButton;
    }

    private RoundedBorderButton closeButtonEditPage() {
        RoundedBorderButton closeButton = UIAppButtonFactory.creatRoundedBorderButton("Close", "/CloseIcon.svg", COLOR_ORANGE, 20);
        closeButton.addActionListener(e -> {
            counterEditDialog.dispose();
        });
        return closeButton;
    }

}
