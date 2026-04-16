package org.MiniDev.Backoffice.GeneralSettings;

import UI.*;
import UI.ToggleMainUI.ToggleButton;
import UI.ToggleMainUI.ToggleListener;
import Utils.VoucherInfo;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.Backoffice.CreateSettingsPanel;
import org.MiniDev.Backoffice.GeneralSettings.SettingsModel.GeneralSettingsModel;
import org.MiniDev.Backoffice.GeneralSettings.SettingsService.GeneralSettingsDataService;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

import static UI.UserFinalSettingsVar.*;
import static org.MiniDev.Cashier.CreateTodaySalePanel.GAP;

public class GeneralSettingsPage extends CreateSettingsPanel {

    protected static RoundedTextFieldV2 storeField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 bizAddressField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 bizAddressLine2Field = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 bizPhoneField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 bizEmailField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 lowStockAlertField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 receiptHeaderField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 receiptFooterField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected static RoundedTextFieldV2 taxRateField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);

    protected static ToggleButton toggleTaxCalculation = new ToggleButton();
    protected static ToggleButton toggleCashCheck = new ToggleButton();
    protected static ToggleButton toggleCardCheck = new ToggleButton();
    protected static ToggleButton toggleMobilePay = new ToggleButton();
    protected static ToggleButton toggleEnableGiftCard = new ToggleButton();
    protected static ToggleButton toggleAdminAccess = new ToggleButton();
    protected static ToggleButton toggleManagerAccess = new ToggleButton();
    protected static ToggleButton toggleCashierAccess = new ToggleButton();
    protected static ToggleButton toggleStockTracking = new ToggleButton();
    protected static ToggleButton toggleAutoReorder = new ToggleButton();
    protected static ToggleButton toggleShowItemized = new ToggleButton();
    protected static ToggleButton togglePrintReceiptsAuto = new ToggleButton();

    protected static ToggleButton toggleEnableEmailNoti = new ToggleButton();
    protected static ToggleButton toggleEnableSMSNoti = new ToggleButton();

    public static GeneralSettingsModel BIZ_DATA;

    public GeneralSettingsPage() {
        GeneralSettingsDataService service = new GeneralSettingsDataService();
        setBusinessSettings(service);
    }

    private void setBusinessSettings(GeneralSettingsDataService service) {
        BIZ_DATA = service.getBusinessDBData();
        setTaxPercentage(BIZ_DATA.getTax_sales_rate(), BIZ_DATA.getTax_cal_YN() == 'Y');

        VoucherInfo vinfo = new VoucherInfo();
        vinfo.setCompanyName(BIZ_DATA.getReceipt_header());
        vinfo.setAddressLine1(BIZ_DATA.getBiz_address());
        vinfo.setAddressLine2(BIZ_DATA.getBiz_townCity());
        vinfo.setEmailAddress("Email: " + BIZ_DATA.getBiz_mail());
        vinfo.setPhoneNumber("Phone: " + BIZ_DATA.getBiz_phNo());
        vinfo.setAfterSaleLabelForVoucher(BIZ_DATA.getReceipt_footer());
    }

    public static void cardRefreshSetter() {
        storeField.setText(BIZ_DATA.getBiz_name());
        bizAddressField.setText(BIZ_DATA.getBiz_address());
        bizAddressLine2Field.setText(BIZ_DATA.getBiz_townCity());
        bizPhoneField.setText(BIZ_DATA.getBiz_phNo());
        bizEmailField.setText(BIZ_DATA.getBiz_mail());
        lowStockAlertField.setText(String.valueOf(BIZ_DATA.getLowStock_num_alert()));
        receiptHeaderField.setText(BIZ_DATA.getReceipt_header());
        receiptFooterField.setText(BIZ_DATA.getReceipt_footer());
        taxRateField.setText(String.valueOf(BIZ_DATA.getTax_sales_rate()));
        toggleTaxCalculation.setSelected(BIZ_DATA.getTax_cal_YN() == 'Y');
        toggleCashCheck.setSelected(BIZ_DATA.getCash_YN() == 'Y');
        toggleCardCheck.setSelected(BIZ_DATA.getBank_YN() == 'Y');
        toggleMobilePay.setSelected(BIZ_DATA.getMobilePay_YN() == 'Y');
        toggleEnableGiftCard.setSelected(BIZ_DATA.getGiftCard_YN() == 'Y');
        toggleAdminAccess.setSelected(BIZ_DATA.getAdmin_access_YN() == 'Y');
        toggleManagerAccess.setSelected(BIZ_DATA.getManager_access_YN() == 'Y');
        toggleCashierAccess.setSelected(BIZ_DATA.getCashier_access_YN() == 'Y');
        toggleStockTracking.setSelected(BIZ_DATA.getStock_tracking_YN() == 'Y');
        toggleAutoReorder.setSelected(BIZ_DATA.getAuto_reorder_YN() == 'Y');
        toggleShowItemized.setSelected(BIZ_DATA.getItemized_receipt_YN() == 'Y');
        togglePrintReceiptsAuto.setSelected(BIZ_DATA.getPrint_receipt_auto_YN() == 'Y');
        toggleEnableEmailNoti.setSelected(BIZ_DATA.getNoti_email_YN() == 'Y');
        toggleEnableSMSNoti.setSelected(BIZ_DATA.getNoti_sms_YN() == 'Y');
    }

    // General Settings Page
    public JPanel generalSettingsPage() {
        // Main RoundedPanel that holds all settings sections
        JPanel mainRoundedPanel = new JPanel();
        mainRoundedPanel.setOpaque(true);
        mainRoundedPanel.setBackground(COLOR_WHITE);
        mainRoundedPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 10));
        mainRoundedPanel.setLayout(new BoxLayout(mainRoundedPanel, BoxLayout.Y_AXIS));

        // Define all settings panels
        JPanel[] settingsPanels = {
                createBusinessInfoSettings(),
                createTaxSettings(BIZ_DATA.getTax_sales_rate()),
                createPaymentSettings(),
                createUserPermissionSettings(),
                createInventorySettings(),
                createReceiptSettings(),
                createNotificationSettings()
        };

        // Add all settings panels with spacing
        for (JPanel panel : settingsPanels) {
            mainRoundedPanel.add(panel);
            mainRoundedPanel.add(createVerticalSpacing(10)); // Add consistent spacing
        }

        // Configure ScrollPane
        JScrollPane settingScrollPane = createCustomScrollPane(mainRoundedPanel);

        // Wrap the scroll pane in a RoundedPanel for aesthetic rounding
        RoundedPanel containerPanel = new RoundedPanel(10);
        containerPanel.setOpaque(false);
        containerPanel.setBackground(COLOR_WHITE);
        containerPanel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        containerPanel.setLayout(new BorderLayout());

        containerPanel.add(topButtonHolding(), BorderLayout.NORTH);
        containerPanel.add(settingScrollPane, BorderLayout.CENTER);

        return containerPanel;
    }

    private JPanel topButtonHolding() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setPreferredSize(new Dimension(700, 50));
        top.add(updateButtonEditPage(), BorderLayout.EAST);
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_LINE_COLOR),
                BorderFactory.createEmptyBorder(0, 0, 10, 0)
        ));


        return top;
    }

    private IconTextButton updateButtonEditPage() {
        IconTextButton updateButton = UIAppButtonFactory.createIconTextButton("Update", "/UpdateIcon.svg", COLOR_GREEN);
        updateButton.addActionListener(e -> {
            collectStoreGenData();

        });
        return updateButton;
    }

    private void collectStoreGenData() {
        System.out.println(storeField.getText() + " " + bizAddressField.getText() + " " + bizAddressLine2Field.getText() + " " +
                bizPhoneField.getText() + " " + bizEmailField.getText());
    }


    // Helper method to create vertical spacing
    private Component createVerticalSpacing(int height) {
        JPanel spacingPanel = new JPanel();
        spacingPanel.setOpaque(false);
        spacingPanel.setBackground(COLOR_WHITE);
        spacingPanel.setPreferredSize(new Dimension(0, height));
        return spacingPanel;
    }

    // Helper method to create a customized JScrollPane
    private JScrollPane createCustomScrollPane(JPanel contentPanel) {
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setOpaque(false);
        scrollPane.setBackground(COLOR_WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUI(new MacOSScrollPane.MacOSScrollBarUI(COLOR_WHITE));
        return scrollPane;
    }


    // Business Information Settings
    private JPanel createBusinessInfoSettings() {
        RoundedPanel businessPanel = new RoundedPanel(10);
        businessPanel.setLayout(new GridLayout(0, 2, 10, 10));
        businessPanel.setOpaque(false); // Set opaque to ensure background color is visible
        businessPanel.setBackground(COLOR_WHITE); // Set white background
        businessPanel.setBorder(new RoundedTitledBorder("Business Information", 15, COLOR_LINE_COLOR, COLOR_BLACK));

        // Add labeled fields
        addLabeledTextField(BIZ_DATA.getBiz_name(), storeField, businessPanel, "Store Name:", "Enter Store Name");
        addLabeledTextField(BIZ_DATA.getBiz_address(), bizAddressField, businessPanel, "Business Address:", "Enter Address");
        addLabeledTextField(BIZ_DATA.getBiz_townCity(), bizAddressLine2Field, businessPanel, "Business Township/ City:", "Enter Address");
        addLabeledTextField(BIZ_DATA.getBiz_phNo(), bizPhoneField, businessPanel, "Business Phone:", "Enter Phone Number");
        addLabeledTextField(BIZ_DATA.getBiz_mail(), bizEmailField, businessPanel, "Business Email:", "Enter Email");

        return businessPanel;
    }


    // Tax Settings
    private JPanel createTaxSettings(double taxRateValue) {
        RoundedPanel taxPanel = new RoundedPanel(10);
        taxPanel.setLayout(new GridLayout(0, 2, 10, 10));
        taxPanel.setOpaque(false);
        taxPanel.setBackground(COLOR_WHITE); // Set white background
        taxPanel.setBorder(new RoundedTitledBorder("Tax Setting", 15, COLOR_LINE_COLOR, COLOR_BLACK));

        taxPanel.add(new JLabel("Enable Tax Calculation:"));

        JPanel holding = new JPanel(new BorderLayout());

        createTogglePanel(holding, toggleTaxCalculation, BIZ_DATA.getTax_cal_YN());

        taxPanel.add(holding);
        taxPanel.add(new JLabel("Sales Tax Rate (%):"));

        taxRateField.setPlaceholder("eg. 0.05");
        taxRateField.setText(String.valueOf(taxRateValue));
        taxRateField.setHorizontalAlignment(SwingConstants.LEFT);

        taxPanel.add(taxRateField);

        return taxPanel;
    }

    // Payment Methods
    private JPanel createPaymentSettings() {
        RoundedPanel paymentPanel = new RoundedPanel(10);
        paymentPanel.setLayout(new GridLayout(0, 2, 10, 10));
        paymentPanel.setOpaque(false);
        paymentPanel.setBackground(COLOR_WHITE); // Set white background
        paymentPanel.setBorder(new RoundedTitledBorder("Payment Methods", 15, COLOR_LINE_COLOR, COLOR_BLACK));

        paymentPanel.add(new JLabel("Accept Cash:"));
        JPanel holding1 = new JPanel(new BorderLayout());

        createTogglePanel(holding1, toggleCashCheck, BIZ_DATA.getCash_YN());
        toggleCashCheck.addToggleSelectedListener(selected -> {
            if (!selected) { // If user tries to turn it OFF
                // Temporarily remove the listener to prevent looping
                toggleCashCheck.setSelected(false);

                // Show warning message
                showErrorMandatorySettings(paymentPanel, "Accepting Cash is required at your store");

                // Force the toggle back to ON
                toggleCashCheck.setSelected(true, false); // false prevents re-triggering listener

                // Re-add the listener after setting back to ON
                reAddToggleListener(toggleCashCheck, paymentPanel, false, "Accepting Cash is required at your store");
            }
        });
        paymentPanel.add(holding1);

        paymentPanel.add(new JLabel("Accept Credit/Debit Cards:"));
        JPanel holding2 = new JPanel(new BorderLayout());

        createTogglePanel(holding2, toggleCardCheck, BIZ_DATA.getBank_YN());
        toggleCardCheck.addToggleSelectedListener(new Consumer<>() {
            private boolean ignoreNextToggle = false; // Flag to prevent looping

            @Override
            public void accept(Boolean selected) {
                if (ignoreNextToggle) {
                    ignoreNextToggle = false; // Reset the flag
                    return; // Skip processing
                }

                if (selected) { // If user tries to turn it ON
                    // Show warning message
                    showErrorMandatorySettings(paymentPanel, "Accepting cards is not available at your store.");

                    // Prevent listener from being triggered again
                    ignoreNextToggle = true;
                    toggleCardCheck.setSelected(false, false); // Set back to OFF without triggering listener
                }
            }
        });
        paymentPanel.add(holding2);

        paymentPanel.add(new JLabel("Enable Mobile Payments:"));
        JPanel holding3 = new JPanel(new BorderLayout());

        createTogglePanel(holding3, toggleMobilePay, BIZ_DATA.getMobilePay_YN());
        paymentPanel.add(holding3);

        paymentPanel.add(new JLabel("Enable Gift Cards:"));
        JPanel holding4 = new JPanel(new BorderLayout());

        createTogglePanel(holding4, toggleEnableGiftCard, BIZ_DATA.getGiftCard_YN());
        toggleEnableGiftCard.addToggleSelectedListener(new Consumer<>() {
            private boolean ignoreNextToggle = false; // Flag to prevent looping

            @Override
            public void accept(Boolean selected) {
                if (ignoreNextToggle) {
                    ignoreNextToggle = false; // Reset the flag
                    return; // Skip processing
                }

                if (selected) { // If user tries to turn it ON
                    // Show warning message
                    showErrorMandatorySettings(paymentPanel, "Accepting cards is not available at your store.");

                    // Prevent listener from being triggered again
                    ignoreNextToggle = true;
                    toggleEnableGiftCard.setSelected(false, false); // Set back to OFF without triggering listener
                }
            }
        });
        paymentPanel.add(holding4);

        return paymentPanel;
    }

    // Method to re-add the listener
    private void reAddToggleListener(ToggleButton toggleButton, JPanel parentPanel, boolean isOffConstant, String message) {
        toggleButton.addToggleSelectedListener(selected -> {
            if (!selected) { // If user tries to turn it OFF
                toggleButton.setSelected(!isOffConstant);

                showErrorMandatorySettings(parentPanel, message);

                toggleButton.setSelected(true, !isOffConstant); // Set back to ON without looping
                reAddToggleListener(toggleButton, parentPanel, isOffConstant, message);
            }
        });
    }

    private void showErrorMandatorySettings(JPanel parentPanel, String message) {
        JOptionPane.showMessageDialog(
                parentPanel,
                message,
                "Mandatory Setting",
                JOptionPane.WARNING_MESSAGE
        );
    }


    private void createTogglePanel(JPanel holdingPanel, ToggleButton toggleButton, char value) {
        holdingPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        holdingPanel.setOpaque(false);

        toggleButton.setPreferredSize(new Dimension(50, 35));
        toggleButton.setMinimumSize(new Dimension(50, 35));
        toggleButton.setMaximumSize(new Dimension(50, 35));

        toggleButton.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        toggleButton.setForeground(COLOR_ORANGE);
        toggleButton.setSelected(value == 'Y');

        holdingPanel.add(toggleButton);
    }

    // User Permissions Settings
    private JPanel createUserPermissionSettings() {
        RoundedPanel permissionPanel = new RoundedPanel(10);
        permissionPanel.setLayout(new GridLayout(0, 2, 10, 10));
        permissionPanel.setOpaque(false);
        permissionPanel.setBackground(COLOR_WHITE); // Set white background
        permissionPanel.setBorder(new RoundedTitledBorder("User Permissions", 15, COLOR_LINE_COLOR, COLOR_BLACK));

        permissionPanel.add(new JLabel("Admin Access:"));
        JPanel holding1 = new JPanel(new BorderLayout());

        createTogglePanel(holding1, toggleAdminAccess, BIZ_DATA.getAdmin_access_YN());
        permissionPanel.add(holding1);

        permissionPanel.add(new JLabel("Manager Access:"));
        JPanel holding2 = new JPanel(new BorderLayout());

        createTogglePanel(holding2, toggleManagerAccess, BIZ_DATA.getManager_access_YN());
        permissionPanel.add(holding2);

        permissionPanel.add(new JLabel("Cashier Access:"));
        JPanel holding3 = new JPanel(new BorderLayout());

        createTogglePanel(holding3, toggleCashierAccess, BIZ_DATA.getCashier_access_YN());
        permissionPanel.add(holding3);

        return permissionPanel;
    }

    // Inventory Settings
    private JPanel createInventorySettings() {
        RoundedPanel inventoryPanel = new RoundedPanel(10);
        inventoryPanel.setLayout(new GridLayout(0, 2, 10, 10));
        inventoryPanel.setOpaque(false);
        inventoryPanel.setBackground(COLOR_WHITE); // Set white background
        inventoryPanel.setBorder(new RoundedTitledBorder("Inventory Settings", 15, COLOR_LINE_COLOR, COLOR_BLACK));

        inventoryPanel.add(new JLabel("Enable Stock Tracking:"));
        JPanel holding1 = new JPanel(new BorderLayout());

        createTogglePanel(holding1, toggleStockTracking, BIZ_DATA.getStock_tracking_YN());
        inventoryPanel.add(holding1);

        addLabeledTextField(String.valueOf(BIZ_DATA.getLowStock_num_alert()), lowStockAlertField, inventoryPanel, "Low Stock Alert Threshold:", "Enter Store Name");

        inventoryPanel.add(new JLabel("Auto-Reorder:"));
        JPanel holding2 = new JPanel(new BorderLayout());

        createTogglePanel(holding2, toggleAutoReorder, BIZ_DATA.getAuto_reorder_YN());
        inventoryPanel.add(holding2);

        return inventoryPanel;
    }

    // Receipt Customization
    private JPanel createReceiptSettings() {
        RoundedPanel receiptPanel = new RoundedPanel(10);
        receiptPanel.setLayout(new GridLayout(0, 2, 10, 10));
        receiptPanel.setOpaque(false);
        receiptPanel.setBackground(COLOR_WHITE); // Set white background
        receiptPanel.setBorder(new RoundedTitledBorder("Receipt Customization", 15, COLOR_LINE_COLOR, COLOR_BLACK));

        addLabeledTextField(BIZ_DATA.getReceipt_header(), receiptHeaderField, receiptPanel, "Receipt Header:", "Your Store Name");
        addLabeledTextField(BIZ_DATA.getReceipt_footer(), receiptFooterField, receiptPanel, "Receipt Footer:", "Thank you for your purchase!");

        receiptPanel.add(new JLabel("Show Itemized Receipt:"));
        JPanel holding = new JPanel(new BorderLayout());

        createTogglePanel(holding, toggleShowItemized, BIZ_DATA.getItemized_receipt_YN());
        receiptPanel.add(holding);

        receiptPanel.add(new JLabel("Print Receipts Automatically:"));
        JPanel holding1 = new JPanel(new BorderLayout());

        createTogglePanel(holding1, togglePrintReceiptsAuto, BIZ_DATA.getPrint_receipt_auto_YN());
        receiptPanel.add(holding1);

        return receiptPanel;
    }

    // Notification Settings
    private JPanel createNotificationSettings() {
        RoundedPanel notificationPanel = new RoundedPanel(10);
        notificationPanel.setLayout(new GridLayout(0, 2, 10, 10));
        notificationPanel.setOpaque(false);
        notificationPanel.setBackground(COLOR_WHITE); // Set white background
        notificationPanel.setBorder(new RoundedTitledBorder("Notification Settings", 15, COLOR_LINE_COLOR, COLOR_BLACK));

        notificationPanel.add(new JLabel("Enable Email Notifications:"));
        JPanel holding1 = new JPanel(new BorderLayout());

        createTogglePanel(holding1, toggleEnableEmailNoti, BIZ_DATA.getNoti_email_YN());
        notificationPanel.add(holding1);

        notificationPanel.add(new JLabel("Enable SMS Notifications:"));
        JPanel holding2 = new JPanel(new BorderLayout());

        createTogglePanel(holding2, toggleEnableSMSNoti, BIZ_DATA.getNoti_sms_YN());
        notificationPanel.add(holding2);

        return notificationPanel;
    }

    //UI Factory
    private void addLabeledTextField(String value, RoundedTextFieldV2 textField, JPanel panel, String labelText, String placeholder) {
        panel.add(new JLabel(labelText));

        textField.setHorizontalAlignment(SwingConstants.LEFT);
        textField.setPlaceholder(placeholder);
        textField.setText(value);
        panel.add(textField);
    }

}
