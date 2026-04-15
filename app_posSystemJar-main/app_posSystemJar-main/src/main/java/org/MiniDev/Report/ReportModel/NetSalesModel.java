package org.MiniDev.Report.ReportModel;

import java.math.BigDecimal;
import java.util.Date;

public class NetSalesModel {
    private Date saleDate;
    private BigDecimal netSales;

    public NetSalesModel(Date saleDate, BigDecimal netSales) {
        this.saleDate = saleDate;
        this.netSales = netSales;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public BigDecimal getNetSales() {
        return netSales;
    }

    public void setNetSales(BigDecimal netSales) {
        this.netSales = netSales;
    }
}
