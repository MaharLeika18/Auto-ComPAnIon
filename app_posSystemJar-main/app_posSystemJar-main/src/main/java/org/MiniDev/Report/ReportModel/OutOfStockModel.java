package org.MiniDev.Report.ReportModel;

import java.util.Date;

public class OutOfStockModel {
    private Date outDate;
    private String outItemName;

    public OutOfStockModel(Date outDate, String outItemName) {
        this.outDate = outDate;
        this.outItemName = outItemName;
    }

    public Date getOutDate() {
        return outDate;
    }

    public void setOutDate(Date outDate) {
        this.outDate = outDate;
    }

    public String getOutItemName() {
        return outItemName;
    }

    public void setOutItemName(String outItemName) {
        this.outItemName = outItemName;
    }
}
