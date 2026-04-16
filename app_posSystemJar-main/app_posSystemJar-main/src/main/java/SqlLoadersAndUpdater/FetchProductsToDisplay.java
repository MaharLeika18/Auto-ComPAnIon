package SqlLoadersAndUpdater;

import DBConnection.DBConnection;
import org.MiniDev.OOP.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FetchProductsToDisplay {

    private final Logger LOGGER = Logger.getLogger(FetchProductsToDisplay.class.getName());
    // Fetch products from the database
    public List<Product> fetchProductsFromDatabase() {
        String sql = """
                    SELECT FL.Food_Name, FL.Food_Price, FL.Food_Photo, FL.Food_Desc, 
                           FL.Promotion_Percentage, FL.Food_Main_Counter_ID, FL.Stock_Available_Cnt, 
                           CL.Counter_Name, CL.MainPrinterPortName, CL.MainPrinterPortAddress, FL.Food_Serial_Number
                    FROM Food_Lists FL 
                    INNER JOIN Counter_Lists CL ON CL.Counter_ID = FL.Food_Main_Counter_ID 
                    WHERE FL.Stock_Available_Cnt > 0 
                    ORDER BY FL.Food_Name
                """;

        List<Product> products = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Product product = new Product(
                        rs.getString("Counter_Name"),
                        rs.getString("MainPrinterPortName"),
                        rs.getString("MainPrinterPortAddress"),
                        rs.getString("Food_Name"),
                        rs.getDouble("Food_Price"),
                        rs.getBytes("Food_Photo"),
                        rs.getDouble("Promotion_Percentage"),
                        rs.getString("Food_Desc"),
                        rs.getInt("Food_Main_Counter_ID"),
                        rs.getInt("Stock_Available_Cnt"),
                        rs.getString("Food_Serial_Number"));
                products.add(product);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching product list", e);
        }
        return products;
    }
}
