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

public class FetchPromoCentLists {

    private Logger LOGGER = Logger.getLogger(FetchPromoCentLists.class.getName());

    public List<String> getPromoCentLists() {
        String sql = "select distinct promo_cent_display from promo_backend";
        List<String> promoCentArray = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String promoCentDisplay = rs.getString("promo_cent_display");
                promoCentArray.add(promoCentDisplay);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching", e);
        }
        return promoCentArray;
    }

}
