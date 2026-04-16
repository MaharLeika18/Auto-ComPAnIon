package SqlLoadersAndUpdater;

import DBConnection.DBConnection;
import org.MiniDev.OOP.ExpenseReportLists;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FetchExpenseReportLists {

    private Logger LOGGER = Logger.getLogger(FetchExpenseReportLists.class.getName());

    public List<ExpenseReportLists> getExpenseReportLists() {
        String sql = "call vw_ExpenseReport"; // Call Only CancelYN = 'N'

        List<ExpenseReportLists> expenseReportLists = new ArrayList<>(); // Initialize the list

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Date expDate = rs.getDate("exp_date");
                String expCode = rs.getString("exp_code");
                String expName = rs.getString("exp_code_name");
                int expAmt = rs.getInt("exp_amount");
                int refundAmt = rs.getInt("refund_amount");
                int executedAmt = rs.getInt("executed_amount");

                ExpenseReportLists oneExpense = new ExpenseReportLists(expDate,expCode,expName,expAmt,refundAmt,executedAmt);
                expenseReportLists.add(oneExpense);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching lists", e);
        }
        return expenseReportLists;
    }
}
