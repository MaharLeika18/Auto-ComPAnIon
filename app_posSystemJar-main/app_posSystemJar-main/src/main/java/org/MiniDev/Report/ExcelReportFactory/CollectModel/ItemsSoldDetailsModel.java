package org.MiniDev.Report.ExcelReportFactory.CollectModel;
import java.util.Date;

public class ItemsSoldDetailsModel {
    private Date saleDate;
    private String orderItemName;
    private int orderQtySold;
    private double orderSubTotal;
    private double originalSubTotal;

    public ItemsSoldDetailsModel(Date saleDate, String orderItemName, int orderQtySold, double orderSubTotal, double originalSubTotal) {
        this.saleDate = saleDate;
        this.orderItemName = orderItemName;
        this.orderQtySold = orderQtySold;
        this.orderSubTotal = orderSubTotal;
        this.originalSubTotal = originalSubTotal;
    }

    public double getOriginalSubTotal() {
        return originalSubTotal;
    }

    public void setOriginalSubTotal(double originalSubTotal) {
        this.originalSubTotal = originalSubTotal;
    }

    public double getOrderSubTotal() {
        return orderSubTotal;
    }

    public void setOrderSubTotal(double orderSubTotal) {
        this.orderSubTotal = orderSubTotal;
    }

    public int getOrderQtySold() {
        return orderQtySold;
    }

    public void setOrderQtySold(int orderQtySold) {
        this.orderQtySold = orderQtySold;
    }

    public String getOrderItemName() {
        return orderItemName;
    }

    public void setOrderItemName(String orderItemName) {
        this.orderItemName = orderItemName;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }
}
