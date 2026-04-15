package org.MiniDev.Backoffice.ExpenseManagement.TransactionPage.TrnhistTableActionPages;

import DBConnection.DBConnection;
import UI.CustomComfirmation.message.MessageDialog;
import org.MiniDev.Backoffice.ExpenseManagement.TransactionPage.NewExpTransactionPage;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ExpTrnHistDeletePage {

    public ExpTrnHistDeletePage() {
    }

    public void showConfirmationToCancelExpTrnHist(String expUsedID, JPanel parentPanel) {
        if (expUsedID == null) {
            System.out.println("Serial value is null");
            return;
        }

        // Get the parent JFrame from the JPanel
        JFrame parentFrame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, parentPanel);
        if (parentFrame == null) {
            System.out.println("Parent frame not found.");
            return;
        }

        // Create the confirmation dialog with the parent frame
        MessageDialog obj = new MessageDialog(parentFrame);
        obj.showMessage("Cancel this batch " + expUsedID + "?",
                "All data will be lost if you press the OK button.\nYou can restore any time within 30 days starting from now.");

        if (obj.getMessageType() == MessageDialog.MessageType.OK) {
            // Execute the cancellation in a background thread
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    cancelTransactionIntoDatabaseUpdater(expUsedID);
                    return null;
                }

                @Override
                protected void done() {
                    NewExpTransactionPage.refreshExpSummaryAndHistTables();
                }
            };
            worker.execute();
        }

        obj.dispose(); // Dispose of the dialog regardless of user choice
    }

    private void cancelTransactionIntoDatabaseUpdater(String expUsedID) {
        String sql = "UPDATE exp_trn_hist SET trn_cancel_yn = 'Y', last_updated_date = CURRENT_TIMESTAMP WHERE exp_used_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Set the parameters
            stmt.setString(1, expUsedID);
            int rowsUpdated = stmt.executeUpdate(); // Execute update
            if (rowsUpdated > 0) {
                System.out.println("Transaction cancelled successfully for ID: " + expUsedID);
            } else {
                System.out.println("No transaction found with ID: " + expUsedID);
            }
        } catch (SQLException e) {
            System.err.println("SQL error occurred while cancelling transaction: " + e.getMessage());
            e.printStackTrace(); // Log the exception for further diagnosis
        }
    }
}
