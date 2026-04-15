package org.MiniDev.Cashier;

import UI.RoundedBorderButton;
import UI.RoundedPanel;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.Cashier.NavigationRepo.HandleButtonActionWorker;
import org.MiniDev.Home.CreateHomePanel;

import javax.swing.*;
import java.awt.*;

import static UI.UserFinalSettingsVar.*;

public class CreateCashierPanel extends CreateHomePanel {

    private static final String[] navigationCashierButtonsLabels = {"Cash Drawer", "Today's Sale", "Sale History"};
    private static RoundedBorderButton[] navigationCashierButtons;
    protected static RoundedPanel topCashierNavigationPanel;
    protected static JButton lastClickedCashierNavigationButton = null;
    private static JPanel mainCardHoldingPanel; // Panel for CardLayout
    protected static JPanel cashierPanel;
    private static CardLayout cashierCardLayout;

    public static JPanel createCashierPanel() {
        cashierPanel = new JPanel();
        cashierPanel.setLayout(new BorderLayout());

        JPanel cashierCenterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cashierCenterPanel.setBackground(COLOR_GRAY);
        cashierCenterPanel.setPreferredSize(new Dimension(0, 50));
        cashierCenterPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 15));
        cashierCenterPanel.add(createTopCashierNavigationButtons(), BorderLayout.CENTER);

        startCashierHistoryButtonAnimation();
        cashierCardLayout = new CardLayout();
        mainCardHoldingPanel = new JPanel(cashierCardLayout);
        mainCardHoldingPanel.setBackground(COLOR_GRAY);

        // Create and add the panels to the CardLayout container
        CreateTodaySalePanel createTodaySalePanel = new CreateTodaySalePanel();
        CreateSalesHistoryPanel createSalesHistoryPanel = new CreateSalesHistoryPanel();

        JPanel cashDrawerPanel = CreateCashDrawerPanel.createCashDrawerPanel();
        JPanel todaySalePanel = createTodaySalePanel.createTodaySalePanel();
        JPanel saleHistoryPanel = createSalesHistoryPanel.createSaleHistoryPanel();

        mainCardHoldingPanel.add(cashDrawerPanel, "Cash Drawer");
        mainCardHoldingPanel.add(todaySalePanel, "Today's Sale");
        mainCardHoldingPanel.add(saleHistoryPanel, "Sale History");

        cashierPanel.add(cashierCenterPanel, BorderLayout.NORTH);
        cashierPanel.add(mainCardHoldingPanel, BorderLayout.CENTER);

        return cashierPanel;
    }

    protected static RoundedPanel createTopCashierNavigationButtons() {
        topCashierNavigationPanel = new RoundedPanel(10);
        topCashierNavigationPanel.setLayout(new BoxLayout(topCashierNavigationPanel,BoxLayout.X_AXIS));
        topCashierNavigationPanel.setBackground(COLOR_WHITE);
        topCashierNavigationPanel.setPreferredSize(new Dimension(520, 40));
        topCashierNavigationPanel.setMaximumSize(new Dimension(520, 40));
        topCashierNavigationPanel.setMinimumSize(new Dimension(520, 40));
        topCashierNavigationPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        topCashierNavigationPanel.setOpaque(false);

        // Initialize the navigation buttons array
        navigationCashierButtons = new RoundedBorderButton[navigationCashierButtonsLabels.length];
        for (int i = 0; i < navigationCashierButtonsLabels.length; i++) {
            navigationCashierButtons[i] = createTopNavigationButton(navigationCashierButtonsLabels[i]);
            int index = i; // capture the index for the lambda

            // Add ActionListener to each button
            navigationCashierButtons[i].addActionListener(e -> SwingUtilities.invokeLater(() -> {
                // Reset button colors for navigation buttons
                resetButtonColorsForNavigationButtons();

                // Update last clicked button
                lastClickedCashierNavigationButton = navigationCashierButtons[index];

                // Handle the button action in the background using SwingWorker
                new HandleButtonActionWorker((RoundedBorderButton) e.getSource()).execute();
            }));

            // Add button to the panel
            topCashierNavigationPanel.add(navigationCashierButtons[i]);
        }

        startCashierHistoryButtonAnimation();

        return topCashierNavigationPanel;
    }

    private static void resetButtonColorsForNavigationButtons() {
        for (RoundedBorderButton button : navigationCashierButtons) {
            button.setBorderColor(COLOR_WHITE);
            button.setOverrideBackgroundColor(COLOR_WHITE);
            button.setForeground(COLOR_BLACK);
            button.setOpaque(false);
            button.setContentAreaFilled(false);
        }
    }

    static void startCashierHistoryButtonAnimation() {
        animateBorderColor(navigationCashierButtons[0]);
    }

    public static void handleButtonActionForOrderNavigation(RoundedBorderButton button) {
        // Reset the previously clicked button's color and border
        if (lastClickedCashierNavigationButton != null && lastClickedCashierNavigationButton != button) {
            resetButtonColorsForNavigationButtons(); // Reset all buttons' colors
        }

        // Set the current button as the last clicked button
        lastClickedCashierNavigationButton = button;

        // Apply active state to the new button
        animateBorderColor(button);

        // Update the cashier panel based on the selected button
        if (cashierCardLayout != null && mainCardHoldingPanel != null) {
            cashierCardLayout.show(mainCardHoldingPanel, button.getText());
        } else {
            System.err.println("cashierCardLayout or mainCardHoldingPanel is null!");
        }
    }

    protected static RoundedBorderButton createTopNavigationButton(String buttonName) {
        RoundedBorderButton button = new RoundedBorderButton(buttonName, 85, 30, COLOR_WHITE);
        button.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 11));
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setRoundedArcSize(10);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setMargin(new Insets(7, 4, 7, 4));
        button.setPreferredSize(new Dimension(85, 30));
        button.setMaximumSize(new Dimension(85, 30));
        button.setMinimumSize(new Dimension(85, 30));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFocusPainted(true);
        button.setContentAreaFilled(false);
        button.setRolloverEnabled(false);
        return button;
    }
}
