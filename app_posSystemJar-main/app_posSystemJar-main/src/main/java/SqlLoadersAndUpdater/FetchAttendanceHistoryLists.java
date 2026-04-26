package SqlLoadersAndUpdater;

import DBConnection.DBConnection;
import org.MiniDev.OOP.AttendanceHistoryLists;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FetchAttendanceHistoryLists {

    private Logger LOGGER = Logger.getLogger(FetchAttendanceHistoryLists.class.getName());

    public List<AttendanceHistoryLists> getAttendanceArrayLists() {
        String sql = "select emp.e_name ,atd.*\n" +
                "from hr_attd_hist atd\n" +
                "left join hr_emp_lists emp on emp.e_id = atd.e_id";

        List<AttendanceHistoryLists> atdListsArray = new ArrayList<>(); // Initialize the list

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int employeeID = rs.getInt("e_id");
                String employeeName = rs.getString("e_name");
                Date attendanceDate = rs.getDate("attd_date");
                Time startTime = rs.getTime("start_time");
                Time endTime = rs.getTime("end_time");
                char absenceYN = rs.getString("absence_yn").charAt(0);

                AttendanceHistoryLists oneAtd = new AttendanceHistoryLists(employeeID, employeeName, attendanceDate, startTime, endTime, absenceYN);
                atdListsArray.add(oneAtd);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching lists", e);
        }
        return atdListsArray;
    }
}
