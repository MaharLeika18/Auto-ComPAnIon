package org.MiniDev.OOP;

import java.time.LocalDateTime;

public class OrderHistoryLists {
    private byte[] foodPhoto; // Store photo as byte array
    private String orderCode;
    private String orderFood;
    private String foodSerialCode;
    private int orderQty;
    private double orderSubTotal;
    private double orderPrice1Qty;
    private String tableName;
    private String dineInToGoDelivery;
    private char orderFinish;
    private double totalOrderSubTotal;
    private String orderNote;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String customerName;
    private String customerID;
    private int tableID;

    public OrderHistoryLists(byte[] foodPhoto, String orderCode, String orderFood, int orderQty, double orderSubTotal,
                             double orderPrice1Qty, String tableName, String dineInToGoDelivery, char orderFinish,
                             double totalOrderSubTotal, String orderNote, LocalDateTime startDate, LocalDateTime endDate,
                             String foodSerialCode, String customerName, String customerID, int tableID) {
        this.foodPhoto = foodPhoto;
        this.orderCode = orderCode;
        this.orderFood = orderFood;
        this.orderQty = orderQty;
        this.orderSubTotal = orderSubTotal;
        this.orderPrice1Qty = orderPrice1Qty;
        this.tableName = tableName;
        this.dineInToGoDelivery = dineInToGoDelivery;
        this.orderFinish = orderFinish;
        this.totalOrderSubTotal = totalOrderSubTotal;
        this.orderNote = orderNote;
        this.startDate = startDate;
        this.endDate = endDate;
        this.foodSerialCode = foodSerialCode;
        this.customerName = customerName;
        this.customerID = customerID;
        this.tableID = tableID;
    }



    public OrderHistoryLists() {
    }

    // Copy constructor that copies all the fields from another OrderHistoryLists object
    public OrderHistoryLists(OrderHistoryLists orderHistoryLists) {
        if (orderHistoryLists.foodPhoto != null) {
            this.foodPhoto = orderHistoryLists.foodPhoto.clone();
        } else {
            this.foodPhoto = null;  // or use a default value
        }
        this.orderCode = orderHistoryLists.orderCode;
        this.orderFood = orderHistoryLists.orderFood;
        this.foodSerialCode = orderHistoryLists.foodSerialCode;
        this.orderQty = orderHistoryLists.orderQty;
        this.orderSubTotal = orderHistoryLists.orderSubTotal;
        this.orderPrice1Qty = orderHistoryLists.orderPrice1Qty;
        this.tableName = orderHistoryLists.tableName;
        this.dineInToGoDelivery = orderHistoryLists.dineInToGoDelivery;
        this.orderFinish = orderHistoryLists.orderFinish;
        this.totalOrderSubTotal = orderHistoryLists.totalOrderSubTotal;
        this.orderNote = orderHistoryLists.orderNote;
        this.startDate = orderHistoryLists.startDate;
        this.endDate = orderHistoryLists.endDate;
        this.customerName = orderHistoryLists.customerName;
        this.customerID = orderHistoryLists.customerID;
        this.tableID = orderHistoryLists.tableID;
    }

    public String getFoodSerialCode() {
        return foodSerialCode;
    }

    public int getTableID() {
        return tableID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public byte[] getFoodPhoto() {
        return foodPhoto;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public String getOrderFood() {
        return orderFood;
    }

    public int getOrderQty() {
        return orderQty;
    }

    public String getOrderNote() {
        return orderNote;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public double getOrderSubTotal() {
        return orderSubTotal;
    }

    public double getOrderPrice1Qty() {
        return orderPrice1Qty;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public String toString() {
        return "OrderHistoryLists{" +
                ", orderCode='" + orderCode + '\'' +
                ", orderFood='" + orderFood + '\'' +
                ", foodSerialCode='" + foodSerialCode + '\'' +
                ", orderQty=" + orderQty +
                ", orderSubTotal=" + orderSubTotal +
                ", orderPrice1Qty=" + orderPrice1Qty +
                ", tableName='" + tableName + '\'' +
                ", dineInToGoDelivery='" + dineInToGoDelivery + '\'' +
                ", orderFinish=" + orderFinish +
                ", totalOrderSubTotal=" + totalOrderSubTotal +
                ", orderNote='" + orderNote + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", customerName='" + customerName + '\'' +
                ", customerID='" + customerID + '\'' +
                ", tableID=" + tableID +
                '}';
    }

    public String getDineInToGoDelivery() {
        return dineInToGoDelivery;
    }

    public char getOrderFinish() {
        return orderFinish;
    }

    public double getTotalOrderSubTotal() {
        return totalOrderSubTotal;
    }

    public void setFoodPhoto(byte[] foodPhoto) {
        this.foodPhoto = foodPhoto;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public void setOrderFood(String orderFood) {
        this.orderFood = orderFood;
    }

    public void setFoodSerialCode(String foodSerialCode) {
        this.foodSerialCode = foodSerialCode;
    }

    public void setOrderQty(int orderQty) {
        this.orderQty = orderQty;
    }

    public void setOrderSubTotal(double orderSubTotal) {
        this.orderSubTotal = orderSubTotal;
    }

    public void setOrderPrice1Qty(double orderPrice1Qty) {
        this.orderPrice1Qty = orderPrice1Qty;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setDineInToGoDelivery(String dineInToGoDelivery) {
        this.dineInToGoDelivery = dineInToGoDelivery;
    }

    public void setOrderFinish(char orderFinish) {
        this.orderFinish = orderFinish;
    }

    public void setTotalOrderSubTotal(double totalOrderSubTotal) {
        this.totalOrderSubTotal = totalOrderSubTotal;
    }

    public void setOrderNote(String orderNote) {
        this.orderNote = orderNote;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public void setTableID(int tableID) {
        this.tableID = tableID;
    }
}
