package org.MiniDev.Backoffice;

import UI.CustomPopUpFactory;
import UI.RoundedBorderButton;
import UI.RoundedPanel;
import org.MiniDev.Home.CreateHomePanel;
import org.MiniDev.Order.CreateOrderPanel;
import org.MiniDev.Backoffice.CustomerSupport_AboutUs.AboutUsPage;
import org.MiniDev.Backoffice.EmployeeManagement.EmployeeManagementPage;
import org.MiniDev.Backoffice.ExpenseManagement.ExpenseManagementPage;
import org.MiniDev.Backoffice.GeneralSettings.GeneralSettingsPage;
import org.MiniDev.Backoffice.Label.GenerateBarcodeSettings;
import org.MiniDev.Backoffice.StockManagement.StockManagementPage;

import javax.swing.*;
import java.awt.*;

import static UI.UserFinalSettingsVar.*;
import static org.MiniDev.Cashier.CreateTodaySalePanel.GAP;

public class CreateSettingsPanel {

    protected final String[] navigationSettingsLabels = {"Stocks", "Expense Management", "Employee", "General Settings", "Labels", "About Us"};
    protected RoundedBorderButton[] navigationSettingButtons;
    protected RoundedBorderButton lastClickedButtonSetting = null;
    protected CardLayout settingsCardLayout;
    protected JPanel dynamicsPanel;

    public JPanel createSettingsPanel() {
        JPanel settingsMainPane = new JPanel(new BorderLayout());
        settingsMainPane.setBorder(BorderFactory.createEmptyBorder(10, GAP, 5, 10));

        RoundedPanel settingsTopPanel = settingsTopPanel();
        JPanel mainHoldingPane = new JPanel(new BorderLayout());

        dynamicsPanel = new RoundedPanel(10);
        settingsCardLayout = new CardLayout();
        dynamicsPanel.setLayout(settingsCardLayout);
        dynamicsPanel.setOpaque(false);

        // Add pages to the card layout
        dynamicsPanel.add(new StockManagementPage().stocksManagementPage(), navigationSettingsLabels[0]);
        dynamicsPanel.add(new ExpenseManagementPage().createEmployeeManagementPage(), navigationSettingsLabels[1]);
        dynamicsPanel.add(new EmployeeManagementPage().employeeManagementPage(), navigationSettingsLabels[2]);
        dynamicsPanel.add(new GeneralSettingsPage().generalSettingsPage(), navigationSettingsLabels[3]);
        dynamicsPanel.add(new GenerateBarcodeSettings().createGenerateBarcodePage(), navigationSettingsLabels[4]);
        dynamicsPanel.add(new AboutUsPage().aboutUsPage(), navigationSettingsLabels[5]);

        mainHoldingPane.add(Box.createRigidArea(new Dimension(0, 15)), BorderLayout.NORTH);
        mainHoldingPane.add(dynamicsPanel, BorderLayout.CENTER);

        settingsMainPane.add(settingsTopPanel, BorderLayout.NORTH);
        settingsMainPane.add(mainHoldingPane, BorderLayout.CENTER);

        // Start with the first button's animation and show the corresponding card
        startSettingButtonAnimation();

        return settingsMainPane;
    }


    protected RoundedPanel settingsTopPanel() {
        RoundedPanel settingsTopPanel = new RoundedPanel(10);
        settingsTopPanel.setLayout(new BoxLayout(settingsTopPanel, BoxLayout.X_AXIS));
        settingsTopPanel.setBackground(Color.WHITE);
        settingsTopPanel.setPreferredSize(new Dimension(520, 40));
        settingsTopPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        navigationSettingButtons = new RoundedBorderButton[navigationSettingsLabels.length];
        for (int i = 0; i < navigationSettingsLabels.length; i++) {
            navigationSettingButtons[i] = CreateOrderPanel.getNaviButUI(navigationSettingsLabels[i], 125, 30);
            int index = i; // capture the index for the lambda
            navigationSettingButtons[i].addActionListener(e -> SwingUtilities.invokeLater(() -> {
                resetButtonColorsForNavigationButtons();
                lastClickedButtonSetting = navigationSettingButtons[index];
                handleButtonActionForSettingNavigation(index);
            }));
            settingsTopPanel.add(navigationSettingButtons[i]);
        }

        return settingsTopPanel;
    }


    protected void startSettingButtonAnimation() {
        // Highlight the first button and show the corresponding card
        CreateHomePanel.animateBorderColor(navigationSettingButtons[0]);
        // Show the "StockPage" on the card layout directly using dynamicsPanel
        settingsCardLayout.show(dynamicsPanel, navigationSettingsLabels[0]);
    }

    protected void handleButtonActionForSettingNavigation(int index) {
        // Apply active state to the new button
        CreateHomePanel.animateBorderColor(lastClickedButtonSetting);

        // Show the correct page based on the button clicked using dynamicsPanel
        settingsCardLayout.show(dynamicsPanel, navigationSettingsLabels[index]);
        if (index==3){
            GeneralSettingsPage.cardRefreshSetter();
        }
    }

    private void resetButtonColorsForNavigationButtons() {
        for (RoundedBorderButton button : navigationSettingButtons) {
            button.setBorderColor(COLOR_WHITE);
            button.setForeground(COLOR_BLACK);
            button.setOverrideBackgroundColor(COLOR_WHITE);
            button.setOpaque(false);
            button.setContentAreaFilled(false);
        }
    }
}
