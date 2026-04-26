package org.MiniDev.Report.ReportModel;

import java.math.BigDecimal;
import java.util.Date;

public class DamagedDataModel {
    private Date reportDate;
    private BigDecimal damagedAmount;

    public DamagedDataModel(Date reportDate, BigDecimal damagedAmount) {
        this.reportDate = reportDate;
        this.damagedAmount = damagedAmount;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public BigDecimal getDamagedAmount() {
        return damagedAmount;
    }

    public void setDamagedAmount(BigDecimal damagedAmount) {
        this.damagedAmount = damagedAmount;
    }
}
