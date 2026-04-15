package org.MiniDev.Report.ReportRepo;

import DBConnection.DBConnection;
import org.MiniDev.Report.ReportModel.ItemsSoldModel;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemsSoldRepo {

    private final Logger LOGGER = Logger.getLogger(ItemsSoldRepo.class.getName());

    public Set<ItemsSoldModel> getItemsSoldSet() {
        String sql = "SELECT cast(ol.StartDate as date) as SaleDate,\n" +
                "       sum(oh.OrderQty)           as ItemsSold\n" +
                "FROM Order_Hist oh\n" +
                "         left join Order_Lists ol on ol.OrderCode = oh.OrderCode\n" +
                "GROUP BY cast(ol.StartDate as date)\n" +
                "ORDER BY 1;\n";

        Set<ItemsSoldModel> itemsSoldModelSet = new HashSet<>(); // Initialize the list

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Date saleDate = rs.getDate("SaleDate");
                int itemsSold = rs.getInt("ItemsSold");

                ItemsSoldModel netSale = new ItemsSoldModel(saleDate, itemsSold);
                itemsSoldModelSet.add(netSale);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching sets", e);
        }
        return itemsSoldModelSet;
    }
}
