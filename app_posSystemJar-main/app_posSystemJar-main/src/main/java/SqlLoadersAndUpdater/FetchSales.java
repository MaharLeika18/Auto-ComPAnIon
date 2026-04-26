package SqlLoadersAndUpdater;

import DBConnection.DBConnection;
import org.MiniDev.OOP.DrawerSalesLists;
import org.MiniDev.OOP.TodaySalesLists;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FetchSales {

    public List<TodaySalesLists> getTodaySalesLists() {
        String sql = "CALL vw_OrderSummaryForToday";
        List<TodaySalesLists> salesList = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String orderCode = rs.getString("OrderCode");
                    Timestamp orderStartDate = rs.getTimestamp("StartDate");
                    Timestamp orderEndDate = rs.getTimestamp("EndDate");
                    char orderFinishYN = rs.getString("OrderFinish").charAt(0);
                    String dineInToGoDelivery = rs.getString("DineInToGoDelivery");
                    String paymentMethod = rs.getString("PaymentMethod");
                    String openedCashierName = rs.getString("OpenedCashierName");
                    String closedCashierName = rs.getString("ClosedCashierName");
                    double changeAmount = rs.getDouble("ChangeAmt");
                    double taxAmount = rs.getDouble("TaxAmt");
                    double orderSubTotal = rs.getDouble("OrderSubTotal");

                    // Create an instance of TodaySalesLists for each row
                    TodaySalesLists sales = new TodaySalesLists(orderCode, orderStartDate, orderEndDate, orderFinishYN, dineInToGoDelivery, paymentMethod, openedCashierName, closedCashierName, changeAmount, taxAmount, orderSubTotal);

                    // Add the object to the list
                    salesList.add(sales);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return salesList; // Return the list of TodaySalesLists objects
    }


    public List<DrawerSalesLists> getSaleHistoryAll() {
        String sql = "CALL vw_DrawerSaleHistory";
        List<DrawerSalesLists> getDrawerSaleHistArray = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String tellerName = rs.getString("TellerName");
                char closedStatus = rs.getString("ClosedStatus").charAt(0);

                // Get the single date and convert it to LocalDate
                java.sql.Date sqlDate = rs.getDate("DrawerDate");
                LocalDate drawerDate = sqlDate != null ? sqlDate.toLocalDate() : null;

                double cashSales = rs.getDouble("CashSales");
                double otherPaymentSales = rs.getDouble("OtherPaymentSales");
                double totalSales = rs.getDouble("TotalSales");
                String drawerNotes = rs.getString("DrawerNotes");
                String paymentMethods = rs.getString("PaymentTypes");

                DrawerSalesLists drawerSales = new DrawerSalesLists(
                        tellerName, closedStatus, drawerDate, cashSales, otherPaymentSales, totalSales, drawerNotes, paymentMethods
                );

                getDrawerSaleHistArray.add(drawerSales);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return getDrawerSaleHistArray;
    }

}
