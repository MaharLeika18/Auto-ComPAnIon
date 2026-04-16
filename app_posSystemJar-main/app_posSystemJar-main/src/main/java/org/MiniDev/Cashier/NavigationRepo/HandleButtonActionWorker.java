package org.MiniDev.Cashier.NavigationRepo;

import UI.RoundedBorderButton;
import org.MiniDev.Cashier.CreateCashierPanel;
import org.MiniDev.Cashier.CreateSalesHistoryPanel;
import org.MiniDev.Cashier.CreateTodaySalePanel;

import javax.swing.*;

// Define the SwingWorker class for handling the button action in the background
public class HandleButtonActionWorker extends SwingWorker<Void, Void> {
    private final RoundedBorderButton button;

    public HandleButtonActionWorker(RoundedBorderButton button) {
        this.button = button;
    }

    @Override
    protected Void doInBackground() throws Exception {
        // Perform the potentially blocking tasks here (e.g., database queries)
        CreateCashierPanel.handleButtonActionForOrderNavigation(button);

        // Refresh panels in the background
        CreateSalesHistoryPanel.refreshDetailsSalesHistPanel();
        CreateTodaySalePanel.refreshDetailsPanel();

        return null;
    }

    @Override
    protected void done() {
        // Update the UI after background work is done
        SwingUtilities.invokeLater(() -> {
            // Any UI updates (like enabling buttons, changing text, etc.) can be done here
        });
    }
}