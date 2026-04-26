package org.MiniDev.Report.ReportRepo;

import DBConnection.DBConnection;
import org.MiniDev.Report.ReportModel.MostProfitItems;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PieData3Repo {

    private Logger LOGGER = Logger.getLogger(PieData3Repo.class.getName());

    public List<MostProfitItems> getMostProfitItemsLists() {
        String sql = "SELECT\n" +
                "\tCAST(ol.StartDate AS DATE) AS OrderDate,\n" +
                "\toh.OrderFood,\n" +
                "\tSUM(oh.OrderSubTotal)- SUM(oh.OriginalSubTotal) AS ProfitAmt\n" +
                "FROM\n" +
                "\tOrder_Hist oh\n" +
                "\tLEFT JOIN Order_Lists ol ON ol.OrderCode = oh.OrderCode\n" +
                "GROUP BY\n" +
                "\tCAST(ol.StartDate AS DATE),\n" +
                "\toh.OrderFood\n" +
                "ORDER BY\n" +
                "\t1,2,\n" +
                "\t3 DESC;";

        List<MostProfitItems> mostProfitItemsLists = new ArrayList<>(); // Initialize the list

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Date saleDate = rs.getDate("OrderDate");
                String saleItem = rs.getString("OrderFood");
                BigDecimal profitAmount = rs.getBigDecimal("ProfitAmt");

                MostProfitItems mostProfitItem = new MostProfitItems(saleDate, saleItem, profitAmount);
                mostProfitItemsLists.add(mostProfitItem);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching lists", e);
        }
        return mostProfitItemsLists;
    }
}
