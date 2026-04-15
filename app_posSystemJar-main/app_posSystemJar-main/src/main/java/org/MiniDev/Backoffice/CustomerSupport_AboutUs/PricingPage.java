package org.MiniDev.Backoffice.CustomerSupport_AboutUs;

import SqlLoadersAndUpdater.FetchCurrentLicenseInfo;
import UI.CustomPricing.raven.component.PanelPricing;
import UI.CustomPricing.raven.model.Model_Data;
import UI.IconCreator;
import UI.IconTextButton;
import UI.RoundedPanel;
import UI.SvgIcon;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.Backoffice.CustomerSupport_AboutUs.PopUpLicense.PopUpLicense;
import org.MiniDev.OOP.CurrentLicenseInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static UI.IconCreator.createResizedIcon;
import static UI.UserFinalSettingsVar.*;
import static org.MiniDev.Cashier.CreateTodaySalePanel.GAP;

public class PricingPage extends JPanel {

    private static final Log log = LogFactory.getLog(PricingPage.class);
    protected PanelPricing panelPricing;
    protected PanelPricing panelPricing1;
    protected PanelPricing panelPricing2;
    protected JPanel mainHolding;
    public static List<CurrentLicenseInfo> currentLicenseInfos;

    public PricingPage() {
        currentLicenseInfos = new ArrayList<>();
        FetchCurrentLicenseInfo fetchCurrentLicenseInfo = new FetchCurrentLicenseInfo();
        currentLicenseInfos = fetchCurrentLicenseInfo.getCurrentLicenseInfoLists();
    }

    public JPanel initPricingPage() {
        mainHolding = new JPanel(new BorderLayout());
        mainHolding.setOpaque(false);

        RoundedPanel pricingHoldingPanel = new RoundedPanel(10);
        pricingHoldingPanel.setPreferredSize(new Dimension(mainHolding.getWidth(), 450));
        pricingHoldingPanel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        pricingHoldingPanel.setBackground(COLOR_WHITE);

        // Initialize the pricing panels
        panelPricing = new PanelPricing();
        panelPricing1 = new PanelPricing();
        panelPricing2 = new PanelPricing(); // Third pricing panel

        // Set color for panelPricing1
        panelPricing1.setColor1(new Color(196, 104, 250));
        panelPricing1.setColor2(new Color(104, 22, 181));

        // Set color for panelPricing2 (new third panel)
        panelPricing2.setColor1(new Color(235, 111, 102));
        panelPricing2.setColor2(new Color(221, 15, 0));

        // Add items and set price for the first pricing panel (LITE)
        panelPricing.setPrice("LITE", 4.99);
        panelPricing.addItem(new Model_Data(true, "Local Server"));
        panelPricing.addItem(new Model_Data(true, "Basic Update"));
        panelPricing.addItem(new Model_Data(false, "Remote Access"));
        panelPricing.addItem(new Model_Data(false, "ERP"));
        panelPricing.addItem(new Model_Data(false, "Multi-Branches"));
        panelPricing.addItem(new Model_Data(false, "HR App"));
        panelPricing.addEventBuy(ae -> JOptionPane.showMessageDialog(PricingPage.this, "Buy LITE"));

        // Add items and set price for the second pricing panel (PRO)
        panelPricing1.setPrice("PRO", 9.99);
        panelPricing1.addItem(new Model_Data(true, "Cloud Server"));
        panelPricing1.addItem(new Model_Data(true, "Regular Update"));
        panelPricing1.addItem(new Model_Data(true, "Remote Access"));
        panelPricing1.addItem(new Model_Data(true, "Basic ERP"));
        panelPricing1.addItem(new Model_Data(false, "Multi-Branches"));
        panelPricing1.addItem(new Model_Data(false, "HR App"));
        panelPricing1.addEventBuy(ae -> JOptionPane.showMessageDialog(PricingPage.this, "Buy PRO"));

        // Add items and set price for the third pricing panel (ULTIMATE)
        panelPricing2.setPrice("ULTIMATE", 19.99);
        panelPricing2.addItem(new Model_Data(true, "Cloud Server"));
        panelPricing2.addItem(new Model_Data(true, "Regular Update"));
        panelPricing2.addItem(new Model_Data(true, "Remote Access"));
        panelPricing2.addItem(new Model_Data(true, "Advanced ERP"));
        panelPricing2.addItem(new Model_Data(true, "Multi-Branches"));
        panelPricing2.addItem(new Model_Data(true, "HR App"));
        panelPricing2.addEventBuy(ae -> JOptionPane.showMessageDialog(PricingPage.this, "Buy ULTIMATE"));

        // Use GridLayout to arrange panels in a grid with 1 row and 3 columns
        pricingHoldingPanel.setLayout(new GridLayout(1, 3, 20, 0)); // 20px gap between columns

        // Add pricing panels to the pricingHoldingPanel
        pricingHoldingPanel.add(panelPricing);
        pricingHoldingPanel.add(panelPricing1);
        pricingHoldingPanel.add(panelPricing2);

        // Add the pricingHoldingPanel to the mainHolding panel
        mainHolding.add(pricingHoldingPanel, BorderLayout.NORTH);
        mainHolding.add(createCurrentLicenseInfoPage(), BorderLayout.CENTER);

        return mainHolding;
    }

    JPanel infoHoldingPanel;

    public JPanel createCurrentLicenseInfoPage() {
        infoHoldingPanel = new JPanel();
        infoHoldingPanel.setLayout(new BorderLayout());
        infoHoldingPanel.setOpaque(false);

        JPanel gapPanel = new JPanel();
        gapPanel.setPreferredSize(new Dimension(infoHoldingPanel.getWidth(), 10));
        gapPanel.setOpaque(false);

        RoundedPanel licenseHoldingMain = new RoundedPanel(10);
        licenseHoldingMain.setLayout(new BorderLayout());
        licenseHoldingMain.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        licenseHoldingMain.setBackground(COLOR_WHITE);

        licenseHoldingMain.add(getHeaderPanel(), BorderLayout.NORTH);
        licenseHoldingMain.add(getCenterHoldingPanel(), BorderLayout.CENTER);

        infoHoldingPanel.add(gapPanel, BorderLayout.NORTH);
        infoHoldingPanel.add(licenseHoldingMain, BorderLayout.CENTER);

        return infoHoldingPanel;
    }

    protected JPanel getHeaderPanel() {
        JPanel headerHoldingPanel = new JPanel(new GridBagLayout());
        headerHoldingPanel.setOpaque(false);
        headerHoldingPanel.setPreferredSize(new Dimension(infoHoldingPanel.getWidth(), 55));
        headerHoldingPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; // Allow the components to fill the space
        gbc.weightx = 1.0; // Allow both panels to expand equally

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, GAP, 0, 0));
        headerPanel.setOpaque(false);

        JPanel editPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        editPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, GAP));
        editPanel.setOpaque(false);

        ImageIcon licenseKeyIcon = new SvgIcon("/KeyInfoIcon.svg", 20).getImageIcon();
        JLabel iconLabel = new JLabel();
        JLabel licenseInfoLabel = new JLabel("License Information");
        licenseInfoLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 18));
        licenseInfoLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0)); // 5px left padding

        iconLabel.setIcon(licenseKeyIcon);

        // Add components to the header panel
        headerPanel.add(iconLabel);
        headerPanel.add(licenseInfoLabel);


        // Add the edit button to the edit panel
        editPanel.add(getKeyEditButton());

        // Add panels to the header holding panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        headerHoldingPanel.add(headerPanel, gbc);

        gbc.gridx = 1;
        headerHoldingPanel.add(editPanel, gbc);

        return headerHoldingPanel;
    }

    protected IconTextButton getKeyEditButton() {
        ImageIcon editIcon = IconCreator.createResizedIcon("/EditWhiteIcon.svg", 14, null);
        IconTextButton keyEditButton = new IconTextButton("Edit License", editIcon, 14, (Color.decode("#09AA29")), 0);
        keyEditButton.setBackground(Color.decode("#09AA29"));
        keyEditButton.setForeground(Color.WHITE);
        keyEditButton.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12));
        keyEditButton.setPreferredSize(new Dimension(100, 38)); // Adjusted size for visibility

        // Set icon on the left side of the button text
        keyEditButton.setIcon(editIcon);
        // Set the text and icon alignment
        keyEditButton.setHorizontalTextPosition(SwingConstants.RIGHT); // Icon on the left
        keyEditButton.setVerticalTextPosition(SwingConstants.CENTER);
        keyEditButton.setHorizontalAlignment(SwingConstants.CENTER);
        keyEditButton.setVerticalAlignment(SwingConstants.CENTER);

        keyEditButton.addActionListener(e -> {
            new PopUpLicense().showPopupWhenEditLicense(currentLicenseInfos);
        });

        return keyEditButton;
    }


    protected JPanel getCenterHoldingPanel() {
        JPanel centerHoldingPanel = new JPanel(new GridLayout(3, 2, 0, 0));
        centerHoldingPanel.setOpaque(false);

        centerHoldingPanel.add(getProductTypePanel(currentLicenseInfos.getFirst().getKeyEditionName()));
        centerHoldingPanel.add(getExpiredDatePanel(currentLicenseInfos.getFirst().getKeyExpiredDate()));
        centerHoldingPanel.add(getStatusPanel(currentLicenseInfos.getFirst().getKeyStatus()));
        centerHoldingPanel.add(getDayRemainingPanel(currentLicenseInfos.getFirst().getDaysRemaining()));
        centerHoldingPanel.add(getLicenseKeyPanel(currentLicenseInfos.getFirst().getKeyCode()));
        centerHoldingPanel.add(getAgreementPanel());

        return centerHoldingPanel;
    }


    protected JPanel createInfoPanel(String label, String info, String iconPath, boolean isRightSideLine, boolean isLink) {
        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, isRightSideLine ? 1 : 0, Color.LIGHT_GRAY));

        // Label panel
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
        labelPanel.setBorder(BorderFactory.createEmptyBorder(0, GAP, 0, 0));
        labelPanel.setOpaque(false);
        labelPanel.add(new JLabel(new SvgIcon(iconPath, 18).getImageIcon()));

        // Add left padding for the label
        JLabel labelComponent = new JLabel(label);
        labelComponent.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0)); // 5px left padding
        labelPanel.add(labelComponent);

        // Info panel
        JPanel infoLabelPanel = new JPanel();
        infoLabelPanel.setLayout(new BoxLayout(infoLabelPanel, BoxLayout.X_AXIS));
        infoLabelPanel.setOpaque(false);

        String iconPathActive = "/ActiveStatusIcon.svg";
        String iconPathInactive = "/InactiveStatusIcon.svg"; // Make sure to use the correct icon path
        JLabel dualIconWithLabel = new JLabel();

        if (currentLicenseInfos.getFirst().isActive()) {
            dualIconWithLabel.setIcon(createResizedIcon(iconPathActive,18,null));
        } else {
            dualIconWithLabel.setIcon(createResizedIcon(iconPathInactive,18,null));
        }

        // Create clickable info label if isLink is true
        if (isLink) {
            JLabel infoLabel = new JLabel("<html><a href=''>" + info + "</a></html>");
            infoLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            infoLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    // Open a custom End User License Agreement dialog
                    showLicenseAgreementDialog();
                }
            });
            infoLabelPanel.add(infoLabel);
        } else {
            dualIconWithLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5)); // Right padding for gap
            infoLabelPanel.add(dualIconWithLabel);
            infoLabelPanel.add(new JLabel(info));
        }

        infoPanel.add(labelPanel);
        infoPanel.add(infoLabelPanel);

        return infoPanel;
    }

    private void showLicenseAgreementDialog() {
        // Create the dialog
        JDialog dialog = new JDialog((Frame) null, "End User License Agreement", true);

        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("End User License Agreement", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JScrollPane scrollPane = getScrollPaneLicenseAgreement();

        dialogPanel.add(titleLabel, BorderLayout.NORTH);
        dialogPanel.add(scrollPane, BorderLayout.CENTER);

        // Option buttons
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(okButton);

        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Set the dialog content and show it
        dialog.setContentPane(dialogPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(null); // Center on screen
        dialog.setVisible(true);
    }

    private static JScrollPane getScrollPaneLicenseAgreement() {
        JTextArea agreementTextArea = new JTextArea("Your license agreement text goes here...");
        agreementTextArea.setLineWrap(true);
        agreementTextArea.setWrapStyleWord(true);
        agreementTextArea.setOpaque(false);
        agreementTextArea.setEditable(false);
        agreementTextArea.setFocusable(false);

        JScrollPane scrollPane = new JScrollPane(agreementTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(400, 300));
        return scrollPane;
    }


    protected JPanel getProductTypePanel(String output) {
        return createInfoPanel("License Edition:", output, "/EditionIcon.svg", true, false);
    }

    protected JPanel getExpiredDatePanel(Timestamp output) {
        // Format the Timestamp to a readable date and time string
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
        String formattedDate = dateFormat.format(output);

        return createInfoPanel("Expired Date:", formattedDate, "/ExpiredIcon.svg", false, false);
    }

    protected JPanel getStatusPanel(String output) {
        return createInfoPanel("Status:", output, "/StatusIcon.svg", true, false);
    }

    protected JPanel getDayRemainingPanel(String output) {
        return createInfoPanel("Days Remaining:", output, "/DayIcon.svg", false, false);
    }


    protected JPanel getLicenseKeyPanel(String output) {
        return createInfoPanel("License Key:", output, "/KeyIcon.svg", true, false);
    }


    protected JPanel getAgreementPanel() {
        return createInfoPanel("License Agreement:", "End User License Agreement", "/AgreementIcon.svg", false, true);
    }

}
