package SqlLoadersAndUpdater;

import DBConnection.DBConnection;
import org.MiniDev.OOP.SalaryHistoryLists;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FetchSalaryHistoryLists {

    private Logger LOGGER = Logger.getLogger(FetchSalaryHistoryLists.class.getName());

    public List<SalaryHistoryLists> getSalaryArrayLists() {
        String sql = "select emp.e_name, emp.department, emp.nrc, sal.*\n" +
                "from hr_salary_hist sal\n" +
                "left join hr_emp_lists emp on emp.e_id = sal.e_id";

        List<SalaryHistoryLists> salListsArray = new ArrayList<>(); // Initialize the list

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Date payDate = rs.getDate("sal.salary_date");
                String employeeName = rs.getString("emp.e_name");
                int employeeId = rs.getInt("sal.e_id");
                String nrc = rs.getString("emp.nrc");
                String departmentName = rs.getString("emp.department");
                int fixedSalaryAmount = rs.getInt("sal.fixed_salary_amount");
                int bonusAmount = rs.getInt("sal.bonus_amount");
                int incentiveAmount = rs.getInt("sal.incentive_amount");
                int reducedAmount = rs.getInt("sal.reduced_salary_amount");
                int finalCalculatedAmount = rs.getInt("sal.final_calculated_amount");

                SalaryHistoryLists oneSal = new SalaryHistoryLists(payDate, employeeName, employeeId, nrc, departmentName, fixedSalaryAmount, bonusAmount, incentiveAmount, reducedAmount, finalCalculatedAmount);
                salListsArray.add(oneSal);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching lists", e);
        }
        return salListsArray;
    }
}
