package org.MiniDev.Report.ExcelReportFactory.CollectModel;

import java.sql.Timestamp;
import java.util.Date;

public class DamagedDetailsModel {
    private Date reportDate;
    private String expCode;
    private String expCodeName;
    private String assignedToEmp;
    private Timestamp lastUpdateDate;
    private String entryEmp;
    private double damagedAmount;

    public DamagedDetailsModel(Date reportDate, String expCode, String expCodeName, String assignedToEmp, Timestamp lastUpdateDate, String entryEmp, double damagedAmount) {
        this.reportDate = reportDate;
        this.expCode = expCode;
        this.expCodeName = expCodeName;
        this.assignedToEmp = assignedToEmp;
        this.lastUpdateDate = lastUpdateDate;
        this.entryEmp = entryEmp;
        this.damagedAmount = damagedAmount;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public String getExpCode() {
        return expCode;
    }

    public void setExpCode(String expCode) {
        this.expCode = expCode;
    }

    public String getExpCodeName() {
        return expCodeName;
    }

    public void setExpCodeName(String expCodeName) {
        this.expCodeName = expCodeName;
    }

    public String getAssignedToEmp() {
        return assignedToEmp;
    }

    public void setAssignedToEmp(String assignedToEmp) {
        this.assignedToEmp = assignedToEmp;
    }

    public Timestamp getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Timestamp lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getEntryEmp() {
        return entryEmp;
    }

    public void setEntryEmp(String entryEmp) {
        this.entryEmp = entryEmp;
    }

    public double getDamagedAmount() {
        return damagedAmount;
    }

    public void setDamagedAmount(double damagedAmount) {
        this.damagedAmount = damagedAmount;
    }
}
