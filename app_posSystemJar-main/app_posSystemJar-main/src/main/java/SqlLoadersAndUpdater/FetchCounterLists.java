package SqlLoadersAndUpdater;

import DBConnection.DBConnection;
import org.MiniDev.OOP.CounterLists;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Fetches active counter names from the database.
 */
public class FetchCounterLists {

    private Logger LOGGER = Logger.getLogger(FetchCounterLists.class.getName());

    /**
     * Retrieves a list of active counter names from the database.
     *
     * @return a list of active counter names
     */
    public List<String> getCounterLists() {
        String sql = "SELECT Counter_Name FROM Counter_Lists WHERE Active_YN = 'Y' ORDER BY Counter_Name";
        List<String> counterListsArray = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String counterName = rs.getString("Counter_Name");
                counterListsArray.add(counterName);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching counter lists", e);
        }
        return counterListsArray;
    }


    /**
     * Retrieves a list of active counter names from the database.
     *
     * @return a list of active counter names
     */
    public List<CounterLists> getCounterArrayLists() {
        String sql = "SELECT * FROM Counter_Lists ORDER BY Counter_ID";
        List<CounterLists> counterListsArray = new ArrayList<>(); // Initialize the list

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String counterName = rs.getString("Counter_Name");
                int counterID = rs.getInt("Counter_ID");
                int counterLevel = rs.getInt("Counter_Level");
                char activeYN = rs.getString("Active_YN").charAt(0);
                String mainPrinterPortName = rs.getString("MainPrinterPortName");
                String mainPrinterPortAddress = rs.getString("MainPrinterPortAddress");

                CounterLists oneCounter = new CounterLists(counterName, counterID, counterLevel, activeYN, mainPrinterPortName, mainPrinterPortAddress);
                counterListsArray.add(oneCounter); // Add the CounterLists object to the list
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching counter lists", e);
        }
        return counterListsArray;
    }

}
