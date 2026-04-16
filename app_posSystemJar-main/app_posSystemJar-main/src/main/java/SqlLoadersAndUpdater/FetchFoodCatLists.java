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

public class FetchFoodCatLists {

    private final Logger LOGGER = Logger.getLogger(FetchFoodCatLists.class.getName());

    public List<String> getFoodCatNames() {
        String sql = "select distinct food_cat_name from food_cat order by food_cat_name";
        List<String> foodCatNamesArray = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String foodCategory = rs.getString("food_cat_name");
                foodCatNamesArray.add(foodCategory);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching food category name", e);
        }
        return foodCatNamesArray;
    }
}
