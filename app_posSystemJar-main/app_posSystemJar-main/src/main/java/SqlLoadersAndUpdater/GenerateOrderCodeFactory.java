package SqlLoadersAndUpdater;

import DBConnection.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GenerateOrderCodeFactory {

    public static final Lock lock = new ReentrantLock();
    private static final String PREFIX = "#";

    public static String getMaxOrderCodeFromDatabase() {
        String maxOrderCode = null;
        String sql = "SELECT MAX(OrderCode) AS maxOrderCode FROM Order_Lists";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                maxOrderCode = rs.getString("maxOrderCode");
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }

        return maxOrderCode;
    }

    public static String generateNewOrderCode() {
        lock.lock();
        try {
            String maxOrderCode = getMaxOrderCodeFromDatabase();
            String newOrderCode;

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyMM");
            String currentMonth = dateFormat.format(new Date());
            String currentYear = currentMonth.substring(0, 2); // YY
            String currentMonthPart = currentMonth.substring(2); // MM

            int sequenceNumber = 1;

            // Check if there's an existing code for the current month
            if (maxOrderCode != null && maxOrderCode.matches(PREFIX + currentYear + currentMonthPart + "\\d{6}")) {
                String sequencePart = maxOrderCode.substring(6);
                try {
                    sequenceNumber = Integer.parseInt(sequencePart) + 1; // Increment the sequence number
                } catch (NumberFormatException e) {
                    throw new IllegalStateException("Invalid order code format in database.");
                }

                // Ensure the sequence does not exceed 999999
                if (sequenceNumber > 999999) {
                    throw new IllegalStateException("Maximum order code limit reached for the month.");
                }
            }

            newOrderCode = String.format("%s%s%s%06d", PREFIX, currentYear, currentMonthPart, sequenceNumber);

            // Ensure the new code is unique
            while (isOrderCodeExists(newOrderCode)) {
                sequenceNumber++;

                if (sequenceNumber > 999999) {
                    throw new IllegalStateException("Maximum order code limit reached for the month.");
                }

                newOrderCode = String.format("%s%s%s%06d", PREFIX, currentYear, currentMonthPart, sequenceNumber);
            }

            return newOrderCode;
        } finally {
            lock.unlock();
        }
    }


    public static boolean isOrderCodeExists(String orderCode) {
        String query = "SELECT COUNT(*) FROM Order_Lists WHERE OrderCode = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, orderCode);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
