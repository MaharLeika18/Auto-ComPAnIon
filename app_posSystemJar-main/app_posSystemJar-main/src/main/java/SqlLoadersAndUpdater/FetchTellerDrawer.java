// FetchTellerDrawer.java
package SqlLoadersAndUpdater;

import org.MiniDev.OOP.Drawer;
import DBConnection.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FetchTellerDrawer {

    public Drawer getDrawerByTellerID(int tellerID) {
        String sql = "SELECT * FROM ClaimableDrawer WHERE TellerID = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, tellerID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Drawer(
                            rs.getInt("TellerID"),
                            rs.getDouble("OpeningDrawerAmount"),
                            rs.getDouble("CashSalesAmount"),
                            rs.getDouble("OtherPaymentSalesAmount"),
                            rs.getString("ClosedStatus").charAt(0),
                            rs.getTimestamp("ClosedDate")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no record is found or if there's an exception
    }

    public int getTellerIDByUsername(String username) {
        String sql = "SELECT TellerID FROM Teller WHERE TellerName = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TellerID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if the username is not found or an error occurs
    }
}
