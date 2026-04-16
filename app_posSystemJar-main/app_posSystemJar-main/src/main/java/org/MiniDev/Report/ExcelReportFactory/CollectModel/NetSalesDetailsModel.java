package org.MiniDev.Report.ExcelReportFactory.CollectModel;

import java.util.Date;

public class NetSalesDetailsModel {
    private Date saleDate;
    private double netSales;
    private String orderCode;
    private String orderItemName;
    private int orderQty;
    private double orderSubTotal;
    private double orderPrice1Qty;
    private double originalSubTotal;
    private String orderNotes;

    public NetSalesDetailsModel(Date saleDate, double netSales, String orderCode, String orderItemName, int orderQty, double orderSubTotal, double orderPrice1Qty, double originalSubTotal, String orderNotes) {
        this.saleDate = saleDate;
        this.netSales = netSales;
        this.orderCode = orderCode;
        this.orderItemName = orderItemName;
        this.orderQty = orderQty;
        this.orderSubTotal = orderSubTotal;
        this.orderPrice1Qty = orderPrice1Qty;
        this.originalSubTotal = originalSubTotal;
        this.orderNotes = orderNotes;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public double getNetSales() {
        return netSales;
    }

    public void setNetSales(double netSales) {
        this.netSales = netSales;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getOrderItemName() {
        return orderItemName;
    }

    public void setOrderItemName(String orderItemName) {
        this.orderItemName = orderItemName;
    }

    public int getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(int orderQty) {
        this.orderQty = orderQty;
    }

    public double getOrderSubTotal() {
        return orderSubTotal;
    }

    public void setOrderSubTotal(double orderSubTotal) {
        this.orderSubTotal = orderSubTotal;
    }

    public double getOrderPrice1Qty() {
        return orderPrice1Qty;
    }

    public void setOrderPrice1Qty(double orderPrice1Qty) {
        this.orderPrice1Qty = orderPrice1Qty;
    }

    public double getOriginalSubTotal() {
        return originalSubTotal;
    }

    public void setOriginalSubTotal(double originalSubTotal) {
        this.originalSubTotal = originalSubTotal;
    }

    public String getOrderNotes() {
        return orderNotes;
    }

    public void setOrderNotes(String orderNotes) {
        this.orderNotes = orderNotes;
    }
}
