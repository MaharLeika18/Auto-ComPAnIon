package SqlLoadersAndUpdater;

import DBConnection.DBConnection;
import org.MiniDev.OOP.TellerLists;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FetchTellerLists {

    public List<TellerLists> getTellerLists() {
        String sql = "SELECT TellerID, TellerName FROM Teller;";
        List<TellerLists> tellerLists = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int tellerID = rs.getInt("TellerID");
                    String tellerName = rs.getString("TellerName");

                    TellerLists tellers = new TellerLists(tellerID, tellerName);
                    tellerLists.add(tellers);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tellerLists;
    }
}
