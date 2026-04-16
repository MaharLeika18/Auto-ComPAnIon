package org.MiniDev.Backoffice.StockManagement.CounterTableActionPages;

import UI.*;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import static UI.UserFinalSettingsVar.*;
import static org.MiniDev.Cashier.CreateTodaySalePanel.GAP;

public class CounterViewDialogPage {

    protected JDialog counterViewDialog;
    protected RoundedTextFieldV2 counterNameField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 counterActiveYNField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 counterLevelField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 counterPrinterAddressField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);


    public CounterViewDialogPage() {
    }

    public void showCounterViewPanel(int initializedCounterID, String initializedCounterName, int initializedCounterLevel, char initializedChar, String initializedPrinterAddress) {
        counterViewDialog = new JDialog();
        counterViewDialog.setUndecorated(true); // Remove window decorations for custom rounding
        counterViewDialog.setBackground(new Color(0, 0, 0, 0));
        counterViewDialog.setSize(500, 400);
        counterViewDialog.setModal(true); // Make the dialog modal
        counterViewDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        RoundedOptionPane.setRoundedCorners(counterViewDialog, 0);
        counterViewDialog.setLayout(new BorderLayout());

        // Add the panel to the dialog
        counterViewDialog.getContentPane().add(getEditHoldingPanel(initializedCounterID, initializedCounterName, initializedCounterLevel, initializedChar, initializedPrinterAddress));
        counterViewDialog.pack(); // Resize the dialog to fit the content
        counterViewDialog.setLocationRelativeTo(null); // Center the dialog on the screen

        // Set the shape for rounded corners
        counterViewDialog.setShape(new RoundRectangle2D.Double(0, 0, counterViewDialog.getWidth(), counterViewDialog.getHeight(), 0, 0));

        // Add fade-in animation
        DialogFadeIn.fadeIn(counterViewDialog);

        counterViewDialog.setVisible(true); // Show the dialog
    }


    protected RoundedPanel getEditHoldingPanel(int initializedCounterID, String initializedCounterName, int initializedCounterLevel, char initializedChar, String initializedPrinterAddress) {
        // Create the rounded panel with a custom rounded border
        RoundedPanel editPanel = new RoundedPanel(20);
        editPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the content
        editPanel.setPreferredSize(new Dimension(counterViewDialog.getWidth(), counterViewDialog.getHeight()));
        editPanel.setBackground(COLOR_ENTRY_GRAY);
        editPanel.setLayout(new BorderLayout());

        editPanel.add(getHeaderPanel(initializedCounterID), BorderLayout.NORTH);
        editPanel.add(getContentPanel(initializedCounterName, initializedCounterLevel, initializedChar, initializedPrinterAddress), BorderLayout.CENTER);
        editPanel.add(getBottomPanel(), BorderLayout.SOUTH);

        return editPanel;
    }

    protected RoundedPanel getHeaderPanel(int counterID) {
        RoundedPanel headerPanel = new RoundedPanel(10);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        headerPanel.setPreferredSize(new Dimension(counterViewDialog.getWidth(), 50));
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
        upperGap.setPreferredSize(new Dimension(counterViewDialog.getWidth(), 10));

        RoundedPanel mainCenterPanel = new RoundedPanel(10);
        mainCenterPanel.setLayout(new BorderLayout());
        mainCenterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 12, 10));
        mainCenterPanel.setOpaque(false);
        mainCenterPanel.setBackground(COLOR_GRAY);
        mainCenterPanel.add(mainInfoHoldingPanel(initializedCounterName, initializedCounterLevel, initializedChar, initializedPrinterAddress), BorderLayout.CENTER);

        JPanel bottomGap = new JPanel();
        bottomGap.setOpaque(false);
        bottomGap.setPreferredSize(new Dimension(counterViewDialog.getWidth(), 10));

        contentPanel.add(upperGap, BorderLayout.NORTH);
        contentPanel.add(mainCenterPanel, BorderLayout.CENTER);
        contentPanel.add(bottomGap, BorderLayout.SOUTH);

        return contentPanel;
    }


    protected JPanel mainInfoHoldingPanel(String counterName, int initializedCounterLevel, char initializedActiveYN, String initializedPrinterAddress) {
        JPanel infoHoldingPanel = new JPanel(new GridLayout(4, 1, GAP, GAP));
        infoHoldingPanel.setOpaque(false);

        initializeShowingFields();
        initializeTextValues(counterName, initializedCounterLevel, initializedActiveYN, initializedPrinterAddress);

        infoHoldingPanel.add(createRowTextFields("Counter Name:", counterNameField));
        infoHoldingPanel.add(createRowTextFields("Counter Level:", counterLevelField));
        infoHoldingPanel.add(createRowTextFields("Active YN:", counterActiveYNField));
        infoHoldingPanel.add(createRowTextFields("Printer Name:", counterPrinterAddressField));
        return infoHoldingPanel;
    }

    private void initializeTextValues(String counterName, int initializedCounterLevel, char initializedActiveYN, String initializedPrinterAddress) {
        counterNameField.setText(counterName);
        counterLevelField.setText(String.valueOf(initializedCounterLevel));
        counterActiveYNField.setText(String.valueOf(initializedActiveYN));
        counterPrinterAddressField.setText(initializedPrinterAddress);
    }

    private void initializeShowingFields() {
        counterNameField.setHorizontalAlignment(SwingConstants.LEFT);
        counterLevelField.setHorizontalAlignment(SwingConstants.LEFT);
        counterActiveYNField.setHorizontalAlignment(SwingConstants.LEFT);
        counterPrinterAddressField.setHorizontalAlignment(SwingConstants.LEFT);


        // Set editable to false and make non-focusable
        counterNameField.setEditable(false);
        counterLevelField.setEditable(false);
        counterActiveYNField.setEditable(false);
        counterPrinterAddressField.setEditable(false);

        // Prevent focus and cursor from appearing
        counterNameField.setFocusable(false);
        counterLevelField.setFocusable(false);
        counterActiveYNField.setFocusable(false);
        counterPrinterAddressField.setFocusable(false);

        // Set background colors
        counterNameField.setBackground(COLOR_WHITE);
        counterLevelField.setBackground(COLOR_WHITE);
        counterActiveYNField.setBackground(COLOR_WHITE);
        counterPrinterAddressField.setBackground(COLOR_WHITE);
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
        textField.setEnabled(false);
        textField.setBackground(COLOR_WHITE);

        innerLeft.add(headerLabel, BorderLayout.WEST);
        innerRight.add(textField, BorderLayout.CENTER);

        textFieldPanel.add(innerLeft, BorderLayout.WEST);
        textFieldPanel.add(innerRight, BorderLayout.CENTER);
        return textFieldPanel;
    }

    protected RoundedPanel getBottomPanel() {
        RoundedPanel bottomPanel = new RoundedPanel(10);
        bottomPanel.setPreferredSize(new Dimension(counterViewDialog.getWidth(), 60));
        bottomPanel.setBackground(COLOR_GRAY);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, GAP));
        bottomPanel.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setPreferredSize(new Dimension(100, 45));
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButtonViewPage(), BorderLayout.CENTER);

        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        return bottomPanel;
    }


    private RoundedBorderButton closeButtonViewPage() {
        RoundedBorderButton closeButton = UIAppButtonFactory.creatRoundedBorderButton("Close", "/CloseIcon.svg", COLOR_ORANGE, 20);
        closeButton.addActionListener(e -> {
            counterViewDialog.dispose();
        });
        return closeButton;
    }

}
