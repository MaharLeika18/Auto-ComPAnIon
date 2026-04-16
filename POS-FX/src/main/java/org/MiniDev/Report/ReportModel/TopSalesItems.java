package org.MiniDev.Report.ReportModel;

import java.util.Date;

public class TopSalesItems {

    private Date saleDate;
    private String saleItem;
    private int saleQty;

    public TopSalesItems(Date saleDate, String saleItem, int saleQty) {
        this.saleDate = saleDate;
        this.saleItem = saleItem;
        this.saleQty = saleQty;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public String getSaleItem() {
        return saleItem;
    }

    public void setSaleItem(String saleItem) {
        this.saleItem = saleItem;
    }

    public int getSaleQty() {
        return saleQty;
    }

    public void setSaleQty(int saleQty) {
        this.saleQty = saleQty;
    }
}
