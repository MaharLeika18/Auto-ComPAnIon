package SqlLoadersAndUpdater;

import DBConnection.DBConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import static UI.UserFinalSettingsVar.TAX_PERCENTAGE;

public class UpdateItemIntoDB {

    public static void itemUpdaterIntoDB(byte[] image, String serialNumber,
                                         String itemName, String itemCat,
                                         int sellPrice, int buyPrice, int stockCnt,
                                         String counterName, double promoCet,
                                         String description) {
        String sql = "{CALL sp_UpdateFood(?, ?, ?, ?,?,?, ?, ?, ?,?,?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setBytes(1, image);
            stmt.setString(2, serialNumber);
            stmt.setString(3, itemName);
            stmt.setString(4, itemCat);
            stmt.setInt(5, sellPrice);
            stmt.setInt(6, buyPrice);
            stmt.setInt(7, stockCnt);
            stmt.setString(8, counterName);
            stmt.setDouble(9, promoCet);
            stmt.setString(10, description);
            stmt.setDouble(11, TAX_PERCENTAGE);
            // Execute the stored procedure
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void counterUpdaterIntoDB(int counterID, String counterName, int counterLevel, char counterActiveYN, String printerName) {
        String sql = "{CALL sp_UpdateCounter(?, ?, ?, ?,?,? )}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, counterID);
            stmt.setString(2, counterName);
            stmt.setInt(3, counterLevel);
            stmt.setString(4, String.valueOf(counterActiveYN));
            stmt.setString(5, printerName);
            stmt.setString(6, printerName);
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
