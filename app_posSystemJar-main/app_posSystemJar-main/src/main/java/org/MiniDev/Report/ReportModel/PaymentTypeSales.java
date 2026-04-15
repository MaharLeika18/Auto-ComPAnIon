package org.MiniDev.Report.ReportModel;

import java.math.BigDecimal;
import java.util.Date;

public class PaymentTypeSales {
    private Date saleDate;
    private String paymentType;
    private BigDecimal saleAmount;

    public PaymentTypeSales(Date saleDate, String paymentType, BigDecimal saleAmount) {
        this.saleDate = saleDate;
        this.paymentType = paymentType;
        this.saleAmount = saleAmount;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public BigDecimal getSaleAmount() {
        return saleAmount;
    }

    public void setSaleAmount(BigDecimal saleAmount) {
        this.saleAmount = saleAmount;
    }
}
