package SqlLoadersAndUpdater;

import DBConnection.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GetExistingItemSrNo {

    public static Set<String> existingSerialNumbers;

    public static void fetchExistingSerialNumbers() {
        existingSerialNumbers = new HashSet<>();

        String query = "SELECT DISTINCT Food_Serial_Number FROM Food_Lists";
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String serialNumber = resultSet.getString("Food_Serial_Number");
                existingSerialNumbers.add(serialNumber);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> fetchExistingSrCodes() {
        List<String> srCodes = new ArrayList<>();

        String query = "SELECT DISTINCT Food_Serial_Number FROM Food_Lists";
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String serialNumber = resultSet.getString("Food_Serial_Number");
                srCodes.add(serialNumber);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return srCodes;
    }
}
