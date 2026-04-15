package SqlLoadersAndUpdater;

import DBConnection.DBConnection;
import org.MiniDev.OOP.CurrentLicenseInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FetchCurrentLicenseInfo {

    private Logger LOGGER = Logger.getLogger(FetchCurrentLicenseInfo.class.getName());

    public List<CurrentLicenseInfo> getCurrentLicenseInfoLists() {
        String sql = "SELECT * FROM key_me";

        List<CurrentLicenseInfo> currentLicenseInfoLists = new ArrayList<>(); // Initialize the list

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String currentKeyCode = rs.getString("cur_key_code");
                String editionName = rs.getString("edition_name");
                int keyDays = rs.getInt("key_days");
                Timestamp registerDate = rs.getTimestamp("key_reg_date");
                Timestamp keyExpDate = rs.getTimestamp("key_exp_date");
                String keyRegName = rs.getString("key_reg_usr");
                String keyRegEmail = rs.getString("key_reg_mail");

                CurrentLicenseInfo currentKey = new CurrentLicenseInfo(currentKeyCode, editionName, keyDays, registerDate, keyExpDate, keyRegName, keyRegEmail);
                currentLicenseInfoLists.add(currentKey);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching lists", e);
        }
        return currentLicenseInfoLists;
    }
}
