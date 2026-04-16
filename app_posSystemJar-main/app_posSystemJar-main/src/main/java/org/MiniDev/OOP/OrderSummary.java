package org.MiniDev.OOP;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class OrderSummary {
    private String orderCode;
    private Timestamp startDate;
    private Timestamp endDate;
    private String orderFinish;
    private BigDecimal orderSubTotal;
    private String orderMode;

    // Constructor
    public OrderSummary(String orderCode,
                        Timestamp startDate,
                        Timestamp endDate,
                        String orderFinish,
                        BigDecimal orderSubTotal,
                        String orderMode) {
        this.orderCode = orderCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.orderFinish = orderFinish;
        this.orderSubTotal = orderSubTotal;
        this.orderMode = orderMode;
    }

    // Getters and Setters
    public String getOrderCode() {
        return orderCode;
    }

    public String getOrderMode() {
        return orderMode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public String getOrderFinish() {
        return orderFinish;
    }

    public void setOrderFinish(String orderFinish) {
        this.orderFinish = orderFinish;
    }

    public BigDecimal getOrderSubTotal() {
        return orderSubTotal;
    }

    public void setOrderSubTotal(BigDecimal orderSubTotal) {
        this.orderSubTotal = orderSubTotal;
    }

    @Override
    public String toString() {
        return "OrderSummary{" +
                "orderCode='" + orderCode + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", orderFinish='" + orderFinish + '\'' +
                ", orderSubTotal=" + orderSubTotal +
                '}';
    }
}
