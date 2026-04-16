package org.MiniDev.Report.ReportModel;

import java.util.Date;

public class ItemsSoldModel {
    private Date saleDate;
    private int itemsSold;

    public ItemsSoldModel(Date saleDate, int itemsSold) {
        this.saleDate = saleDate;
        this.itemsSold = itemsSold;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public int getItemsSold() {
        return itemsSold;
    }

    public void setItemsSold(int itemsSold) {
        this.itemsSold = itemsSold;
    }
}
