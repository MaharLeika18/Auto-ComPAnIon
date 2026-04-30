package com.autocompanion.pos.fx;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class OrderLog {

    public enum Payment { CASH, GCASH, BANK }
    public enum Status  { PENDING, APPROVED, REJECTED }

    public static class ItemSnapshot {
        private final String productId;
        private final String productName;
        private final int    quantity;
        private final double unitPrice;

        public ItemSnapshot(String productId, String productName, int quantity, double unitPrice) {
            this.productId   = productId;
            this.productName = productName;
            this.quantity    = quantity;
            this.unitPrice   = unitPrice;
        }

        public String getProductId()   { return productId;            }
        public String getProductName() { return productName;          }
        public int    getQuantity()    { return quantity;             }
        public double getUnitPrice()   { return unitPrice;            }
        public double getSubtotal()    { return unitPrice * quantity; }
    }

    private final String             id;
    private final String             receiptNo;
    private final List<ItemSnapshot> items;
    private final double             total;
    private final Payment            payment;
    private final LocalDateTime      createdAt;
    private       Status             status;
    private       Timestamp          createdDate;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("MMM dd, yyyy  hh:mm a");

    public OrderLog(String receiptNo, List<ItemSnapshot> items, double total, Payment payment) {
        this.id        = UUID.randomUUID().toString();
        this.receiptNo = receiptNo;
        this.items     = items;
        this.total     = total;
        this.payment   = payment;
        this.createdAt = LocalDateTime.now();
        this.status    = Status.PENDING;
        this.createdDate = Timestamp.valueOf(LocalDateTime.now());
    }

    public OrderLog(String receiptNo, List<ItemSnapshot> items, double total, Payment payment, Status status, Timestamp createdDate) {
        this.id        = UUID.randomUUID().toString();
        this.receiptNo = receiptNo;
        this.items     = items;
        this.total     = total;
        this.payment   = payment;
        this.createdAt = createdDate != null ? createdDate.toLocalDateTime() : LocalDateTime.now();
        this.status    = status;
        this.createdDate = createdDate;
    }

    public String             getId()             { return id;                    }
    public String             getReceiptNo()      { return receiptNo;             }
    public List<ItemSnapshot> getItems()          { return items;                 }
    public double             getTotal()          { return total;                 }
    public Payment            getPayment()        { return payment;               }
    public Status             getStatus()         { return status;                }
    public void               setStatus(Status s) { this.status = s;             }
    public String             getCreatedAt()      { return createdAt.format(FMT); }
    public Timestamp          getCreatedDate()    { return createdDate;          }

    public String getPaymentLabel() {
        switch (payment) {
            case CASH:  return "Cash";
            case GCASH: return "GCash";
            case BANK:  return "Bank Transfer";
            default:    return "Unknown";
        }
    }

    public String getStatusLabel() {
        switch (status) {
            case PENDING:  return "Pending";
            case APPROVED: return "Approved";
            case REJECTED: return "Rejected";
            default:       return "Unknown";
        }
    }
}