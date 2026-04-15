package org.MiniDev.Home.ButtonHandler;

import org.MiniDev.Home.MiniDevPOS;
import org.MiniDev.Order.CreateOrderPanel;
import org.MiniDev.Report.CreateReportsPanel;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;

import static UI.UserFinalSettingsVar.COLOR_ORANGE;
import static org.MiniDev.Home.MiniDevPOS.resetButtonBorder;

public class ButtonHandler {

    private static final Map<JButton, String> buttonPanelMap = new HashMap<>();

    static {
        buttonPanelMap.put(MiniDevPOS.homeButton, "Home");
        buttonPanelMap.put(MiniDevPOS.customersButton, "Customers");
        buttonPanelMap.put(MiniDevPOS.tablesButton, "Tables");
        buttonPanelMap.put(MiniDevPOS.cashierButton, "Cashier");
        buttonPanelMap.put(MiniDevPOS.ordersButton, "Orders");
        buttonPanelMap.put(MiniDevPOS.reportsButton, "Reports");
        buttonPanelMap.put(MiniDevPOS.settingsButton, "Backoffice");
        buttonPanelMap.put(MiniDevPOS.selectTablesButton, "SelectTables");
    }

    public static void handleButtonAction(JButton button) {
        // Reset the previous button's background and border
        MiniDevPOS.refreshTableAll();


        if (MiniDevPOS.currentButton != null) {
            resetButtonBorder(MiniDevPOS.currentButton);
        }

        // Set the current button as the active button
        MiniDevPOS.currentButton = button;
        MiniDevPOS.panelName = buttonPanelMap.get(button);

        if (MiniDevPOS.panelName != null) {
            MiniDevPOS.showPanel(MiniDevPOS.panelName);

            // Apply specific actions based on panel name
            switch (MiniDevPOS.panelName) {
                case "Orders":
                    CreateOrderPanel.updateOrderHistPanel(true);
                    CreateOrderPanel.resetNavigationButtonAnimation();
                    break;
                case "Reports":
                    CreateReportsPanel.pieChartsStartAnimation();
                    break;
            }

            // Apply border animation only if the selected panel is not 'SelectTables'
            if (!MiniDevPOS.panelName.equals("SelectTables")) {
                MiniDevPOS.animateBorderColor(button, COLOR_ORANGE);
            }

            // Fill the button with color to indicate it is active
            button.setBackground(Color.decode("#fff2e8")); // Set your desired fill color here
            button.setOpaque(true); // Make it opaque
            button.setContentAreaFilled(true); // Ensure it fills the background
        }
    }
}
