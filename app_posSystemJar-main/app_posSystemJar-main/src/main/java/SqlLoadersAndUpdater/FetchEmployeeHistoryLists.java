package SqlLoadersAndUpdater;

import DBConnection.DBConnection;
import org.MiniDev.OOP.EmployeeHistoryLists;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FetchEmployeeHistoryLists {

    private Logger LOGGER = Logger.getLogger(FetchEmployeeHistoryLists.class.getName());

    public List<EmployeeHistoryLists> getEmployeeHistoryLists() {
        String sql = "SELECT * FROM hr_emp_lists emp\n" +
                "LEFT JOIN Teller t on t.tellerid = emp.e_id;";

        List<EmployeeHistoryLists> empListsArray = new ArrayList<>(); // Initialize the list

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                byte[] employeePhoto = rs.getBytes("e_photo");
                int employeeId = rs.getInt("e_id");
                String employeeName = rs.getString("e_name");
                String hashedPassword = "......";
                String departmentName = rs.getString("department"); // Add this line
                String telNo = rs.getString("tel_no");
                Date hireDate = rs.getDate("hire_date");
                Date retiredDate = rs.getDate("retired_date");
                String nrc = rs.getString("nrc");
                Date birthDate = rs.getDate("birth_date");
                String homeAddress = rs.getString("home_address");
                String fatherName = rs.getString("father_name");
                int currentFixedSalaryAmount = rs.getInt("current_fixed_salary_amount");
                char activeYN = rs.getString("active_yn").charAt(0);
                char genderMW = rs.getString("gender_m/w").charAt(0);
                String maritalStatus = rs.getString("marital_status");

                // Create an instance of EmployeeHistoryLists with the departmentName
                EmployeeHistoryLists oneEmp = new EmployeeHistoryLists(
                        employeePhoto,
                        employeeId,
                        employeeName,
                        hashedPassword,
                        departmentName,  // Include departmentName
                        telNo,
                        hireDate,
                        retiredDate,
                        nrc,
                        birthDate,
                        homeAddress,
                        fatherName,
                        currentFixedSalaryAmount,
                        activeYN,
                        genderMW,
                        maritalStatus
                );
                empListsArray.add(oneEmp);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching employee history lists", e);
        }
        return empListsArray; // Ensure this matches the variable name
    }
    
    public List<String> getEmpNames() {
        String sql = "select distinct e_name from hr_emp_lists where active_yn = 'Y' order by e_name";
        List<String> empNames = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String oneEmpName = rs.getString("e_name");
                empNames.add(oneEmpName);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching EmpNames", e);
        }
        return empNames;
    }
}
