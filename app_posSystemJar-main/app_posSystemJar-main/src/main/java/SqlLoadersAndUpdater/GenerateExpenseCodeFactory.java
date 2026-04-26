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

public class GenerateExpenseCodeFactory {

    public static final Lock lockExp = new ReentrantLock();
    private static final String PREFIX = "#EXP";

    public static String getMaxExpenseCodeFromDatabase() {
        String maxExpCode = null;
        String sql = "SELECT max(exp_used_id) AS max_exp_used_id FROM exp_trn_hist";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                maxExpCode = rs.getString("max_exp_used_id");
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace(); // Optionally log to a file or monitoring system
        }

        return maxExpCode;
    }

    public static String generateNewExpCode() {
        lockExp.lock();
        try {
            String maxExpCode = getMaxExpenseCodeFromDatabase();
            String newExpCode;

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyMM");
            String currentMonth = dateFormat.format(new Date());
            String currentYear = currentMonth.substring(0, 2); // YY
            String currentMonthPart = currentMonth.substring(2); // MM

            int sequenceNumber = 1;

            // Check if there's an existing max code and parse it
            if (maxExpCode != null && maxExpCode.startsWith(PREFIX)) {
                String existingYear = maxExpCode.substring(4, 6); // YY
                String existingMonth = maxExpCode.substring(6, 8); // MM

                // Reset the sequence if the month or year has changed
                if (!existingYear.equals(currentYear) || !existingMonth.equals(currentMonthPart)) {
                    sequenceNumber = 1; // Reset for new month/year
                } else {
                    String sequencePart = maxExpCode.substring(8); // XXXX part
                    try {
                        sequenceNumber = Integer.parseInt(sequencePart) + 1; // Increment the sequence number
                    } catch (NumberFormatException e) {
                        throw new IllegalStateException("Invalid expense code format in database.");
                    }

                    // Check if the sequence exceeds 9999
                    if (sequenceNumber > 9999) {
                        throw new IllegalStateException("Maximum expense code limit reached for the month.");
                    }
                }
            }

            newExpCode = String.format("%s%s%s%04d", PREFIX, currentYear, currentMonthPart, sequenceNumber);

            // Ensure the new code is unique
            while (isExpCodeExists(newExpCode)) {
                sequenceNumber++;
                if (sequenceNumber > 9999) {
                    throw new IllegalStateException("Maximum expense code limit reached for the month.");
                }
                newExpCode = String.format("%s%s%s%04d", PREFIX, currentYear, currentMonthPart, sequenceNumber);
            }

            return newExpCode;
        } finally {
            lockExp.unlock();
        }
    }


    public static boolean isExpCodeExists(String expCode) {
        String query = "SELECT COUNT(*) FROM exp_trn_hist WHERE SUBSTRING_INDEX(exp_used_id, '-', 1) = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, expCode);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
