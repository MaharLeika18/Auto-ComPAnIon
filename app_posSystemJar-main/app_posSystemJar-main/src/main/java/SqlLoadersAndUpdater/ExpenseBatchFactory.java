package SqlLoadersAndUpdater;

import DBConnection.DBConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class ExpenseBatchFactory {

    public static void createNewExpenseIntoDB(int batchUserID, String newExpCodeID, String expCatFullName,
                                              String newExpTitle, int newExpRequestAmt, String assignedToEmpName,
                                              byte[] signatureImageBytes, byte[] attachmentImage) {
        String sql = "{CALL sp_CreateNewExpense(?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, batchUserID);
            stmt.setString(2, newExpCodeID);
            stmt.setString(3, expCatFullName);
            stmt.setString(4, newExpTitle);
            stmt.setInt(5, newExpRequestAmt);
            stmt.setString(6, assignedToEmpName);
            stmt.setBytes(7, signatureImageBytes);

            // Set attachments, allowing null values
            stmt.setBytes(8, attachmentImage);


            stmt.execute();

        } catch (SQLException e) {
            System.err.println("Error creating new expense: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void updateExpenseTrnIntoDB(int batchUserID, String expCodeID, String expCatFullName,
                                              String newExpTitle, int newExpRefundAmt, String assignedToEmpName, byte[] attachmentImage) {
        String sql = "{CALL sp_UpdateOldExpense(?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, batchUserID);
            stmt.setString(2, expCodeID);
            stmt.setString(3, expCatFullName);
            stmt.setString(4, newExpTitle);
            stmt.setInt(5, newExpRefundAmt);
            stmt.setString(6, assignedToEmpName);
            stmt.setBytes(7, attachmentImage);

            stmt.execute();

        } catch (SQLException e) {
            System.err.println("Error creating new expense: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void updateExpenseCategoryIntoDB(int tellerID, byte[] expIcon, String expCode, String expName, int allocationThisYear, char activeYN) {
        String sql = "{CALL sp_UpdateExpCatAndBudget(?, ?, ?,?, ?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, tellerID);
            stmt.setBytes(2, expIcon);
            stmt.setString(3, expCode);
            stmt.setString(4, expName);
            stmt.setInt(5, allocationThisYear);
            stmt.setString(6, String.valueOf(activeYN));

            stmt.execute();

        } catch (SQLException e) {
            System.err.println("Error creating new expense: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
