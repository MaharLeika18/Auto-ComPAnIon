package org.MiniDev.OOP;

import java.sql.Timestamp;

public class TodaySalesLists {

    private String orderCode;
    private Timestamp orderStartDate;
    private Timestamp orderEndDate;
    private char orderFinishYN;
    private String dineInToGoDelivery;
    private String paymentMethod;
    private String openedCashierName;
    private String closedCashierName;
    private double changeAmount;
    private double taxAmount;
    private double orderSubTotal;

    public TodaySalesLists(String orderCode, Timestamp orderStartDate, Timestamp orderEndDate, char orderFinishYN, String dineInToGoDelivery, String paymentMethod, String openedCashierName, String closedCashierName, double changeAmount, double taxAmount, double orderSubTotal) {
        this.orderCode = orderCode;
        this.orderStartDate = orderStartDate;
        this.orderEndDate = orderEndDate;
        this.orderFinishYN = orderFinishYN;
        this.dineInToGoDelivery = dineInToGoDelivery;
        this.paymentMethod = paymentMethod;
        this.openedCashierName = openedCashierName;
        this.closedCashierName = closedCashierName;
        this.changeAmount = changeAmount;
        this.taxAmount = taxAmount;
        this.orderSubTotal = orderSubTotal;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Timestamp getOrderStartDate() {
        return orderStartDate;
    }

    public void setOrderStartDate(Timestamp orderStartDate) {
        this.orderStartDate = orderStartDate;
    }

    public Timestamp getOrderEndDate() {
        return orderEndDate;
    }

    public void setOrderEndDate(Timestamp orderEndDate) {
        this.orderEndDate = orderEndDate;
    }

    public char getOrderFinishYN() {
        return orderFinishYN;
    }

    public void setOrderFinishYN(char orderFinishYN) {
        this.orderFinishYN = orderFinishYN;
    }

    public String getDineInToGoDelivery() {
        return dineInToGoDelivery;
    }

    public void setDineInToGoDelivery(String dineInToGoDelivery) {
        this.dineInToGoDelivery = dineInToGoDelivery;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getOpenedCashierName() {
        return openedCashierName;
    }

    public void setOpenedCashierName(String openedCashierName) {
        this.openedCashierName = openedCashierName;
    }

    public String getClosedCashierName() {
        return closedCashierName;
    }

    public void setClosedCashierName(String closedCashierName) {
        this.closedCashierName = closedCashierName;
    }

    public double getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(double changeAmount) {
        this.changeAmount = changeAmount;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getOrderSubTotal() {
        return orderSubTotal;
    }

    public void setOrderSubTotal(double orderSubTotal) {
        this.orderSubTotal = orderSubTotal;
    }
}
