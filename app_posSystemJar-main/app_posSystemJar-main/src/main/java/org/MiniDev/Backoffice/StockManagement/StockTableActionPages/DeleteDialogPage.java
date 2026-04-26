package org.MiniDev.Backoffice.StockManagement.StockTableActionPages;

import DBConnection.DBConnection;
import UI.CustomComfirmation.message.MessageDialog;
import org.MiniDev.Backoffice.StockManagement.StockInnerPage;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteDialogPage {

    public DeleteDialogPage() {
    }

    public void showConfirmationDialog(String serialValue, JPanel parentPanel) {
        if (serialValue == null) {
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
        MessageDialog dialog = new MessageDialog(parentFrame);
        dialog.showMessage("Delete this item " + serialValue + "?",
                "All data will be lost if you press the OK button.\nYou can restore any time within 30 days starting from now.");

        if (dialog.getMessageType() == MessageDialog.MessageType.OK) {
            // Execute the deletion in a background thread
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    deleteItemFromDatabase(serialValue);
                    return null;
                }

                @Override
                protected void done() {
                    new StockInnerPage().refreshTableAfterItemsUpdater(); // Refresh table after deletion
                }
            };
            worker.execute(); // Start the worker
        }

        dialog.dispose(); // Dispose of the dialog after handling
    }

    private void deleteItemFromDatabase(String serialValue) {
        String sql = "DELETE FROM Food_Lists WHERE Food_Serial_Number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Set the parameter
            stmt.setString(1, serialValue);
            int rowsAffected = stmt.executeUpdate(); // Execute delete operation

            // Provide feedback
            if (rowsAffected > 0) {
                System.out.println("Item deleted successfully.");
            } else {
                System.out.println("No item found with serial number: " + serialValue);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // You might want to log this or notify the user
        }
    }
}
