package org.MiniDev.Report.ReportRepo;

import DBConnection.DBConnection;
import org.MiniDev.Report.ReportModel.PaymentTypeSales;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PieData2Repo {

    private Logger LOGGER = Logger.getLogger(PieData2Repo.class.getName());

    public List<PaymentTypeSales> getPaymentTypeSales() {
        String sql = "SELECT\n" +
                "  CAST( OrderEndDate AS DATE ) AS SaleDate,\n" +
                "  pay_shrt_name AS PaymentType,\n" +
                "  IFNULL( SUM( OrderPayableAmt ), 0 ) AS SaleAmount \n" +
                "FROM\n" +
                "  pay_types pt\n" +
                "  LEFT JOIN Cashier_Hist ch ON TRIM( ch.PaymentMethod ) = TRIM( pt.pay_shrt_name ) \n" +
                "WHERE\n" +
                "  pt.pay_category <> 'Banking' \n" +
                "  AND pt.pay_category <> 'Debt' \n" +
                "  AND pt.pay_category <> 'Split' \n" +
                "GROUP BY\n" +
                "  CAST( OrderEndDate AS DATE ),\n" +
                "  pay_shrt_name\n" +
                "ORDER BY 3 DESC;";

        List<PaymentTypeSales> paymentTypeSalesLists = new ArrayList<>(); // Initialize the list

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Date saleDate = rs.getDate("SaleDate");
                String paymentType = rs.getString("PaymentType");
                BigDecimal saleAmount = rs.getBigDecimal("SaleAmount");

                PaymentTypeSales paymentTypeSale = new PaymentTypeSales(saleDate, paymentType, saleAmount);
                paymentTypeSalesLists.add(paymentTypeSale);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching lists", e);
        }
        return paymentTypeSalesLists;
    }
}
