package org.MiniDev.Report.ReportModel;

import java.math.BigDecimal;
import java.util.Date;

public class MostProfitItems {
    private Date saleDate;
    private String profitItem;
    private BigDecimal profitAmount;

    public MostProfitItems(Date saleDate, String profitItem, BigDecimal profitAmount) {
        this.saleDate = saleDate;
        this.profitItem = profitItem;
        this.profitAmount = profitAmount;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public String getProfitItem() {
        return profitItem;
    }

    public void setProfitItem(String profitItem) {
        this.profitItem = profitItem;
    }

    public BigDecimal getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(BigDecimal profitAmount) {
        this.profitAmount = profitAmount;
    }
}
