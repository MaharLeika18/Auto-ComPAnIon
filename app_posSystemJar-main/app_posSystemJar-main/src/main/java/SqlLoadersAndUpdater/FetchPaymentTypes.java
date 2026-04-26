package SqlLoadersAndUpdater;

import DBConnection.DBConnection;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FetchPaymentTypes {

    private Logger LOGGER = Logger.getLogger(FetchPaymentTypes.class.getName());

    public List<String> getShortNamesOfPaymentTypes() {
        String sql = "SELECT pay_shrt_name FROM pay_types WHERE pay_category IN ('Pay') ORDER BY pay_code";
        List<String> shortNamesArray = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String paymentName = rs.getString("pay_shrt_name");
                shortNamesArray.add(paymentName);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching payment names", e);
        }
        return shortNamesArray;
    }

}