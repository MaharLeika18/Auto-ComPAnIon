package SqlLoadersAndUpdater;

import DBConnection.DBConnection;
import org.MiniDev.OOP.CustomerManager;
import org.MiniDev.OOP.Customers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class CustomerLoader {

    public static void loadCustomerData() {
        String query = "SELECT * FROM Customer_Lists ORDER BY PurchasedGrandTotal DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            CustomerManager customerManager = CustomerManager.getInstance(); // Get the singleton instance

            while (rs.next()) {
                String customerID = rs.getString("CID");
                String customerName = rs.getString("CustomerName");
                String email = rs.getString("Email");
                byte[] profilePictureBytes = rs.getBytes("ProfilePicture");
                BufferedImage profilePicture = null;
                if (profilePictureBytes != null) {
                    try (ByteArrayInputStream bais = new ByteArrayInputStream(profilePictureBytes)) {
                        profilePicture = ImageIO.read(bais);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                String telNo = rs.getString("TeleNo");
                double purchasedGrandTotal = rs.getDouble("PurchasedGrandTotal");
                Date registeredDate = rs.getDate("RegisteredDate");
                String customerAddress = rs.getString("Address");
                String customerCity = rs.getString("City");
                String customerNotes = rs.getString("Notes");

                Customers customer = new Customers(customerID, customerName, email, telNo, registeredDate, purchasedGrandTotal, profilePictureBytes, customerAddress, customerCity, customerNotes);
                customerManager.addCustomer(customer);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
