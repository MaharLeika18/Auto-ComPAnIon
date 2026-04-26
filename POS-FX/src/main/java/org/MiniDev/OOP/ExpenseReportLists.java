package org.MiniDev.OOP;

import java.sql.Date;

public class ExpenseReportLists {
    private Date expDate;
    private String expCode;
    private String expName;
    private double expAmount;
    private double refundAmount;
    private double executedAmount;

    public ExpenseReportLists(Date expDate,String expCode, String expName, double expAmount, double refundAmount, double executedAmount) {
        this.expDate = expDate;
        this.expCode = expCode;
        this.expName = expName;
        this.expAmount = expAmount;
        this.refundAmount = refundAmount;
        this.executedAmount = executedAmount;
    }

    public String getExpCode() {
        return expCode;
    }

    public String getExpName() {
        return expName;
    }

    public double getExpAmount() {
        return expAmount;
    }

    public Date getExpDate() {
        return expDate;
    }

    public double getRefundAmount() {
        return refundAmount;
    }

    public double getExecutedAmount() {
        return executedAmount;
    }
}
