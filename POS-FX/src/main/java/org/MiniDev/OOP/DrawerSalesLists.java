package org.MiniDev.OOP;

import java.time.LocalDate;

public class DrawerSalesLists {
    private String tellerName;
    private char closedStatus;
    private LocalDate drawerDate;
    private double cashSales;
    private double otherPaymentSales;
    private double totalSales;
    private String drawerNotes;
    private String paymentMethods;

    public DrawerSalesLists(String tellerName, char closedStatus, LocalDate drawerDate, double cashSales, double otherPaymentSales, double totalSales, String drawerNotes, String paymentMethods) {
        this.tellerName = tellerName;
        this.closedStatus = closedStatus;
        this.drawerDate = drawerDate;
        this.cashSales = cashSales;
        this.otherPaymentSales = otherPaymentSales;
        this.totalSales = totalSales;
        this.drawerNotes = drawerNotes;
        this.paymentMethods = paymentMethods;
    }

    public String getTellerName() {
        return tellerName;
    }

    public String getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(String paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public void setTellerName(String tellerName) {
        this.tellerName = tellerName;
    }

    public char getClosedStatus() {
        return closedStatus;
    }

    public void setClosedStatus(char closedStatus) {
        this.closedStatus = closedStatus;
    }

    public LocalDate getDrawerDate() {
        return drawerDate;
    }

    public void setDrawerDate(LocalDate drawerDate) {
        this.drawerDate = drawerDate;
    }

    public double getCashSales() {
        return cashSales;
    }

    public void setCashSales(double cashSales) {
        this.cashSales = cashSales;
    }

    public double getOtherPaymentSales() {
        return otherPaymentSales;
    }

    public void setOtherPaymentSales(double otherPaymentSales) {
        this.otherPaymentSales = otherPaymentSales;
    }

    public double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(double totalSales) {
        this.totalSales = totalSales;
    }

    public String getDrawerNotes() {
        return drawerNotes;
    }

    public void setDrawerNotes(String drawerNotes) {
        this.drawerNotes = drawerNotes;
    }
}
