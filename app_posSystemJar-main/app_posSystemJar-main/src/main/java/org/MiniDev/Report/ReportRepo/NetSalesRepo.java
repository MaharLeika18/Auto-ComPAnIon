package org.MiniDev.Report.ReportRepo;

import DBConnection.DBConnection;
import org.MiniDev.Report.ReportModel.NetSalesModel;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetSalesRepo {

    private final Logger LOGGER = Logger.getLogger(NetSalesRepo.class.getName());

    public Set<NetSalesModel> getNetSalesModelSet() {
        String sql = "SELECT CAST(ol.StartDate AS DATE)                       AS SaleDate,\n" +
                "       SUM(oh.OrderSubTotal) - SUM(oh.OriginalSubTotal) AS NetSales\n" +
                "FROM Order_Hist oh\n" +
                "         LEFT JOIN Order_Lists ol ON ol.OrderCode = oh.OrderCode\n" +
                "WHERE ol.CancelYN = 'N'\n" +
                "GROUP BY CAST(ol.StartDate AS DATE)\n" +
                "ORDER BY 1, 2 DESC;\n";

        Set<NetSalesModel> netSalesModelSet = new HashSet<>(); // Initialize the list

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Date saleDate = rs.getDate("SaleDate");
                BigDecimal netSales = rs.getBigDecimal("NetSales");

                NetSalesModel netSale = new NetSalesModel(saleDate, netSales);
                netSalesModelSet.add(netSale);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching sets", e);
        }
        return netSalesModelSet;
    }
}
