package org.MiniDev.Report.ReportModel;

import java.math.BigDecimal;
import java.util.Date;

public class ProfitChartDataModel {
    private Date reportDate;
    private BigDecimal expAmount;
    private BigDecimal incomeAmount;
    private BigDecimal profitAmount;

    public ProfitChartDataModel(Date reportDate, BigDecimal expAmount, BigDecimal incomeAmount, BigDecimal profitAmount) {
        this.reportDate = reportDate;
        this.expAmount = expAmount;
        this.incomeAmount = incomeAmount;
        this.profitAmount = profitAmount;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public BigDecimal getExpAmount() {
        return expAmount;
    }

    public void setExpAmount(BigDecimal expAmount) {
        this.expAmount = expAmount;
    }

    public BigDecimal getIncomeAmount() {
        return incomeAmount;
    }

    public void setIncomeAmount(BigDecimal incomeAmount) {
        this.incomeAmount = incomeAmount;
    }

    public BigDecimal getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(BigDecimal profitAmount) {
        this.profitAmount = profitAmount;
    }
}
