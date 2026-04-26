package SqlLoadersAndUpdater;

import DBConnection.DBConnection;
import org.MiniDev.OOP.ExpenseCategoryLists;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FetchExpenseCategoryLists {

    private Logger LOGGER = Logger.getLogger(FetchExpenseCategoryLists.class.getName());

    public List<ExpenseCategoryLists> getExpenseCategoryLists() {
        String sql = "SELECT ec.*, IFNULL(ecb.bdgt_yearly_amt, 0) AS bdgt_yearly_amt\n" +
                "FROM exp_codes ec \n" +
                "LEFT JOIN exp_budget_conso ecb ON ecb.exp_code = ec.exp_code \n" +
                "    AND ecb.bdgt_year = YEAR(CURDATE());\n" +
                "\n";

        List<ExpenseCategoryLists> expenseCategoryLists = new ArrayList<>(); // Initialize the list

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                byte[] icon = rs.getBytes("icon");
                String expCode = rs.getString("exp_code");
                String expName = rs.getString("exp_code_name");
                Date lastUpdatedDate = rs.getDate("last_updated_date");
                char currentUsedYN = rs.getString("used_yn").charAt(0);
                int currentYearBudgetAmount = rs.getInt("bdgt_yearly_amt");


                ExpenseCategoryLists oneExpense = new ExpenseCategoryLists(icon, expCode, expName, lastUpdatedDate, currentUsedYN, currentYearBudgetAmount);
                expenseCategoryLists.add(oneExpense);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching lists", e);
        }
        return expenseCategoryLists;
    }

    public List<String> getExpCatNames() {
        String sql = "select distinct exp_code_name from exp_codes where used_yn = 'Y' order by exp_code_name";
        List<String> expCatNames = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String expCatName = rs.getString("exp_code_name");
                expCatNames.add(expCatName);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching expCat lists", e);
        }
        return expCatNames;
    }
}
