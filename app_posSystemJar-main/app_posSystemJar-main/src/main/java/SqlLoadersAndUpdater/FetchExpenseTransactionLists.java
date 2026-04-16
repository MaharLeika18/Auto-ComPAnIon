package SqlLoadersAndUpdater;

import DBConnection.DBConnection;
import org.MiniDev.OOP.ExpenseTransactionLists;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FetchExpenseTransactionLists {

    private Logger LOGGER = Logger.getLogger(FetchExpenseTransactionLists.class.getName());

    public List<ExpenseTransactionLists> getExpenseTransactionLists() {
        String sql = "call vw_ExpenseTransactions"; // Call Only CancelYN = 'N'

        List<ExpenseTransactionLists> expListsArray = new ArrayList<>(); // Initialize the list

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String expUsedId = rs.getString("exp_used_id");
                Date trnDate = rs.getDate("exp_date");
                String expCode = rs.getString("exp_code");
                String expCodeName = rs.getString("exp_code_name");
                String expRemark = rs.getString("exp_remark");
                int expAmount = rs.getInt("exp_amount");
                int refundAmount = rs.getInt("refund_amount");
                Date refundDate = rs.getDate("refund_date");
                char assignedToYN = rs.getString("assigned_to_yn").charAt(0);
                Date lastUpdatedDate = rs.getDate("last_updated_date");
                char attachmentFile = rs.getString("attachment").charAt(0);
                String assignedToEmployee = rs.getString("assigned_to");
                String trnUser = rs.getString("trn_user");
                byte[] attachmentImage = rs.getBytes("attach_image");


                ExpenseTransactionLists oneExpense = new ExpenseTransactionLists(expUsedId, trnDate, expCode,
                        expCodeName, expRemark, expAmount, refundAmount, refundDate, assignedToYN,
                        lastUpdatedDate, attachmentFile, assignedToEmployee, trnUser, attachmentImage
                );
                expListsArray.add(oneExpense);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching lists", e);
        }
        return expListsArray;
    }
}
