package org.MiniDev.OOP;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AttendanceHistoryLists {
    private int employeeID;
    private String employeeName;
    private Date attendanceDate;
    private Time startTime;
    private Time endTime;
    private char absenceYN;

    public AttendanceHistoryLists(int employeeID, String employeeName, Date attendanceDate, Time startTime, Time endTime, char absenceYN) {
        this.employeeID = employeeID;
        this.employeeName = employeeName;
        this.attendanceDate = attendanceDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.absenceYN = absenceYN;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public Date getAttendanceDate() {
        return attendanceDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public String getFormattedStartTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        if (startTime == null) {
            return "";
        } else {
            return sdf.format(startTime);
        }
    }

    public String getFormattedEndTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        if (endTime == null) {
            return "";
        } else {
            return sdf.format(endTime);
        }
    }

    public char getAbsenceYN() {
        return absenceYN;
    }

    public double getWorkingHours() {
        if (endTime == null) {
            return 0;
        } else {
            return endTime.getTime() - startTime.getTime();
        }
    }

    public String getAbsenceStatus() {
        if (getAbsenceYN() == 'Y') {
            return "Presence";
        } else {
            return "Not Absence";
        }
    }

    public String getActiveStatus() {
        char absenceStatus = getAbsenceYN();

        if (absenceStatus == 'Y') {
            return (endTime == null) ? "Active" : "Inactive";
        }

        return "Inactive";
    }


}
