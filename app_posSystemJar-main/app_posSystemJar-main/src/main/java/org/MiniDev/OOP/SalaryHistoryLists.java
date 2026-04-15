package org.MiniDev.OOP;

import java.sql.Date;

public class SalaryHistoryLists {
    private Date payDate;
    private String employeeName;
    private int employeeId;
    private String nrc;
    private String departmentName;
    private int fixedSalaryAmount;
    private int bonusAmount;
    private int incentiveAmount;
    private int reducedAmount;
    private int finalCalculatedAmount;

    public SalaryHistoryLists(Date payDate, String employeeName, int employeeId, String nrc, String departmentName, int fixedSalaryAmount, int bonusAmount, int incentiveAmount, int reducedAmount, int finalCalculatedAmount) {
        this.payDate = payDate;
        this.employeeName = employeeName;
        this.employeeId = employeeId;
        this.nrc = nrc;
        this.departmentName = departmentName;
        this.fixedSalaryAmount = fixedSalaryAmount;
        this.bonusAmount = bonusAmount;
        this.incentiveAmount = incentiveAmount;
        this.reducedAmount = reducedAmount;
        this.finalCalculatedAmount = finalCalculatedAmount;
    }

    public int getBonusIncentive() {
        return bonusAmount + incentiveAmount;
    }

    public Date getPayDate() {
        return payDate;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getNrc() {
        return nrc;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public int getFixedSalaryAmount() {
        return fixedSalaryAmount;
    }

    public int getBonusAmount() {
        return bonusAmount;
    }

    public int getIncentiveAmount() {
        return incentiveAmount;
    }

    public int getReducedAmount() {
        return reducedAmount;
    }

    public int getFinalCalculatedAmount() {
        return finalCalculatedAmount;
    }
}
