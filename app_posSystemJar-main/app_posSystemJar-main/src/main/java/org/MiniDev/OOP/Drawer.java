package org.MiniDev.OOP;

import java.sql.Timestamp;

public class Drawer {

    private int userID;
    private double openingAmt;
    private double cashSellingAmt;
    private double otherPaymentAmt;
    private char closedStatusYN;
    private Timestamp closedDate;


    public Drawer(int userID,double openingAmt, double cashSellingAmt, double otherPaymentAmt, char closedStatusYN, Timestamp closedDate) {
        this.userID = userID;
        this.openingAmt = openingAmt;
        this.cashSellingAmt = cashSellingAmt;
        this.otherPaymentAmt = otherPaymentAmt;
        this.closedStatusYN = closedStatusYN;
        this.closedDate = closedDate;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public double getOpeningAmt() {
        return openingAmt;
    }

    public char getClosedStatusYN() {
        return closedStatusYN;
    }

    public void setClosedStatusYN(char closedStatusYN) {
        this.closedStatusYN = closedStatusYN;
    }

    public Timestamp getClosedDate() {
        return closedDate;
    }

    public void setClosedDate(Timestamp closedDate) {
        this.closedDate = closedDate;
    }

    public void setOpeningAmt(double openingAmt) {
        this.openingAmt = openingAmt;
    }

    public double getCashSellingAmt() {
        return cashSellingAmt;
    }

    public void setCashSellingAmt(double cashSellingAmt) {
        this.cashSellingAmt = cashSellingAmt;
    }

    public double getOtherPaymentAmt() {
        return otherPaymentAmt;
    }

    public void setOtherPaymentAmt(double otherPaymentAmt) {
        this.otherPaymentAmt = otherPaymentAmt;
    }

    public boolean thereIsOpeningBalance() {
        return closedStatusYN == 'N' ;
    }


}
