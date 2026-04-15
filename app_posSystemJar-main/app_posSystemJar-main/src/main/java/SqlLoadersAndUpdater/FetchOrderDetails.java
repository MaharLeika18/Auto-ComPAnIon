package SqlLoadersAndUpdater;

import DBConnection.DBConnection;
import org.MiniDev.OOP.OrderHistoryLists;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FetchOrderDetails {

    public List<OrderHistoryLists> getLists() {

        List<OrderHistoryLists> orderDetailsArrayList = new ArrayList<>(); // Initialize the list
        String query = "CALL vw_OrderDetails";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    // Convert java.sql.Timestamp to LocalDateTime
                    Timestamp startTimestamp = resultSet.getTimestamp("StartDate");
                    LocalDateTime startDate = startTimestamp != null ? startTimestamp.toLocalDateTime() : null;

                    Timestamp endTimestamp = resultSet.getTimestamp("EndDate");
                    LocalDateTime endDate = endTimestamp != null ? endTimestamp.toLocalDateTime() : null;

                    OrderHistoryLists orderItem = new OrderHistoryLists(
                            resultSet.getBytes("Food_Photo"),
                            resultSet.getString("OrderCode"),
                            resultSet.getString("OrderFood"),
                            resultSet.getInt("OrderQty"),
                            resultSet.getDouble("OrderSubTotal"),
                            resultSet.getDouble("OrderPrice1Qty"),
                            resultSet.getString("TableName"),
                            resultSet.getString("DineInToGoDelivery"),
                            resultSet.getString("OrderFinish").charAt(0),
                            resultSet.getDouble("TotalOrderSubTotal"),
                            resultSet.getString("OrderNote"),
                            startDate,
                            endDate,
                            resultSet.getString("Food_Serial_Number"),
                            resultSet.getString("CustomerName"),
                            resultSet.getString("CID"),
                            resultSet.getInt("TableID"));
                    orderDetailsArrayList.add(orderItem);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderDetailsArrayList;
    }
}
