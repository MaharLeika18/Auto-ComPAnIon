package org.MiniDev.Report.ExcelReportFactory.CollectRepo;

import DBConnection.DBConnection;
import org.MiniDev.Report.ExcelReportFactory.CollectModel.ItemsSoldDetailsModel;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemsSoldDetailsRepo {

    private final Logger LOGGER = Logger.getLogger(ItemsSoldDetailsRepo.class.getName());

    public List<ItemsSoldDetailsModel> getItemsSoldDetailsLists() {
        String sql = "SELECT\n" +
                "\tCAST(ol.StartDate AS DATE) AS SaleDate,\n" +
                "\toh.OrderFood,\n" +
                "\tSUM(oh.OrderQty) AS OrderQty,\n" +
                "\tSUM(oh.OrderSubTotal) AS OrderSubTotal,\n" +
                "\tSUM(oh.OriginalSubTotal) AS OriginalSubTotal\n" +
                "FROM\n" +
                "\tOrder_Hist oh\n" +
                "\tLEFT JOIN Order_Lists ol ON ol.OrderCode = oh.OrderCode\n" +
                "GROUP BY\n" +
                "\tCAST(ol.StartDate AS DATE), oh.OrderFood\n";

        List<ItemsSoldDetailsModel> itemsSoldDetailsList = new ArrayList<>(); // Initialize the list

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Date saleDate = rs.getDate("SaleDate");
                String orderItemName = rs.getString("OrderFood");
                int orderQtySold = rs.getInt("OrderQty");
                double orderSubTotal = rs.getDouble("OrderSubTotal");
                double originalSubTotal = rs.getDouble("OriginalSubTotal");

                ItemsSoldDetailsModel itemsSold = new ItemsSoldDetailsModel(saleDate, orderItemName, orderQtySold, orderSubTotal, originalSubTotal);
                itemsSoldDetailsList.add(itemsSold);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching sets", e);
        }
        return itemsSoldDetailsList;
    }
}
