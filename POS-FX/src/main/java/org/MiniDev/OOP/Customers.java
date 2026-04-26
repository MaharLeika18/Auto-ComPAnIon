package org.MiniDev.OOP;

import java.sql.Date;

public class Customers {
    private String cID;
    private String customerName;
    private String emailAddress;
    private String phoneNumber;
    private Date registeredDate;
    private double purchasedGrandTotal;
    private byte[] profilePicture; // New field for profile picture
    private String customerAddress;
    private String customerCity;
    private String customerNotes;

    public Customers(String cID, String customerName, String emailAddress, String phoneNumber, Date registeredDate, double purchasedGrandTotal, byte[] profilePicture, String customerAddress, String customerCity, String customerNotes) {
        this.cID = cID;
        this.customerName = customerName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.registeredDate = registeredDate;
        this.purchasedGrandTotal = purchasedGrandTotal;
        this.profilePicture = profilePicture;
        this.customerAddress = customerAddress;
        this.customerCity = customerCity;
        this.customerNotes = customerNotes;
    }

    public String getCustomerNotes() {
        return customerNotes;
    }

    public void setCustomerNotes(String customerNotes) {
        this.customerNotes = customerNotes;
    }

    public String getCustomerCity() {
        return customerCity;
    }

    public void setCustomerCity(String customerCity) {
        this.customerCity = customerCity;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }


    // Getters and setters for all fields
    public String getcID() {
        return cID;
    }

    public void setcID(String cID) {
        this.cID = cID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(Date registeredDate) {
        this.registeredDate = registeredDate;
    }

    public double getPurchasedGrandTotal() {
        return purchasedGrandTotal;
    }

    public void setPurchasedGrandTotal(double purchasedGrandTotal) {
        this.purchasedGrandTotal = purchasedGrandTotal;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }
}
