package org.MiniDev.Report.ExcelReportFactory.CollectRepo;

import DBConnection.DBConnection;
import org.MiniDev.Report.ExcelReportFactory.CollectModel.NetSalesDetailsModel;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetSalesDetailsRepo {

    private final Logger LOGGER = Logger.getLogger(NetSalesDetailsRepo.class.getName());

    public List<NetSalesDetailsModel> getNetSalesDetailsSet() {
        String sql = "SELECT\n" +
                "\tCAST(ol.StartDate AS DATE) AS SaleDate,\n" +
                "\t(oh.OrderSubTotal - OriginalSubTotal) AS NetSales,\n" +
                "\toh.*\n" +
                "FROM\n" +
                "\tOrder_Hist oh\n" +
                "\tLEFT JOIN Order_Lists ol ON ol.OrderCode = oh.OrderCode\n" +
                "WHERE\n" +
                "\tol.CancelYN = 'N'\n" +
                "\n";

        List<NetSalesDetailsModel> netSalesDetailsModelSet = new ArrayList<>(); // Initialize the list

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Date saleDate = rs.getDate("SaleDate");
                double netSales = rs.getDouble("NetSales");
                String orderCode = rs.getString("OrderCode");
                String orderItemName = rs.getString("OrderFood");
                int orderQty = rs.getInt("OrderQty");
                double orderSubTotal = rs.getDouble("OrderSubTotal");
                double orderPrice1Qty = rs.getDouble("OrderPrice1Qty");
                double originalSubTotal = rs.getDouble("OriginalSubTotal");
                String orderNotes = rs.getString("OrderNote");

                NetSalesDetailsModel netSale = new NetSalesDetailsModel(saleDate, netSales, orderCode, orderItemName, orderQty, orderSubTotal, orderPrice1Qty, originalSubTotal, orderNotes);
                netSalesDetailsModelSet.add(netSale);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching sets", e);
        }
        return netSalesDetailsModelSet;
    }
}
