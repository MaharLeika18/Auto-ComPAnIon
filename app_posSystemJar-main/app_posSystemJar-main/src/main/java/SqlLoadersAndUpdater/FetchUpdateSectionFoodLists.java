package SqlLoadersAndUpdater;

import DBConnection.DBConnection;
import org.MiniDev.OOP.UpdateSectionFoodLists;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FetchUpdateSectionFoodLists {

    public List<UpdateSectionFoodLists> getUpdateSectionFoodLists() {
        String sql = "SELECT * FROM Food_Lists ORDER BY CreatedDate DESC, Stock_Available_Cnt DESC, Food_Name ASC";
        List<UpdateSectionFoodLists> foodListsToUpdate = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    byte[] foodPhoto = rs.getBytes("Food_Photo");
                    int foodMainCounterID = rs.getInt("Food_Main_Counter_ID");
                    String counterName = rs.getString("Counter_Name");
                    String foodCategory = rs.getString("Food_Category");
                    String foodSerialNumber = rs.getString("Food_Serial_Number");
                    String foodName = rs.getString("Food_Name");
                    double foodPrice = rs.getDouble("Food_Price");
                    double foodOriginalPrice = rs.getDouble("Food_Original_Price");
                    double taxPercentage = rs.getDouble("Tax_Percentage");
                    double promotionPercentage = rs.getDouble("Promotion_Percentage");
                    String foodDesc = rs.getString("Food_Desc");
                    char stockCountYN = rs.getString("Stock_Count_YN").charAt(0);
                    int stockAvailableCnt = rs.getInt("Stock_Available_Cnt");
                    String mainPrinterPortName = rs.getString("MainPrinterPortName");
                    String mainPrinterPortAddress = rs.getString("MainPrinterPortAddress");
                    Date createdDate = rs.getDate("CreatedDate");

                    UpdateSectionFoodLists foodListsToUpdateSingleList = new UpdateSectionFoodLists(
                            foodPhoto, foodMainCounterID, counterName, foodCategory, foodSerialNumber, foodName, foodPrice,
                            foodOriginalPrice, taxPercentage, promotionPercentage, foodDesc, stockCountYN, stockAvailableCnt,
                            mainPrinterPortName, mainPrinterPortAddress, createdDate);
                    foodListsToUpdate.add(foodListsToUpdateSingleList);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return foodListsToUpdate; // Return the list of TodaySalesLists objects
    }

}
