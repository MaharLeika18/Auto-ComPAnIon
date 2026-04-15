package org.MiniDev.OOP;

import java.sql.Date;

public class ExpenseTransactionLists {
    private String expUsedID;
    private Date trnDate;
    private String expCode;
    private String expName;
    private String expRemark;
    private int expAmount;
    private int refundAmount;
    private Date refundDate;
    private char assignedToYN;
    private Date lastUpdatedDate;
    private char attachmentFileYN;
    private String assignedToEmployee;
    private String trnUser;
    private byte[] attachmentImage;

    public ExpenseTransactionLists(String expUsedID, Date trnDate, String expCode, String expName, String expRemark, int expAmount, int refundAmount, Date refundDate, char assignedToYN, Date lastUpdatedDate, char attachmentFileYN, String assignedToEmployee, String trnUser, byte[] attachmentImage) {
        this.expUsedID = expUsedID;
        this.trnDate = trnDate;
        this.expCode = expCode;
        this.expName = expName;
        this.expRemark = expRemark;
        this.expAmount = expAmount;
        this.refundAmount = refundAmount;
        this.refundDate = refundDate;
        this.assignedToYN = assignedToYN;
        this.lastUpdatedDate = lastUpdatedDate;
        this.attachmentFileYN = attachmentFileYN;
        this.assignedToEmployee = assignedToEmployee;
        this.trnUser = trnUser;
        this.attachmentImage = attachmentImage;
    }

    public String getExpUsedID() {
        return expUsedID;
    }

    public Date getTrnDate() {
        return trnDate;
    }

    public String getExpCode() {
        return expCode;
    }

    public String getExpName() {
        return expName;
    }

    public String getExpRemark() {
        return expRemark;
    }

    public int getExpAmount() {
        return expAmount;
    }

    public int getRefundAmount() {
        return refundAmount;
    }

    public Date getRefundDate() {
        return refundDate;
    }

    public char getAssignedToYN() {
        return assignedToYN;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public char getAttachmentFileYN() {
        return attachmentFileYN;
    }

    public String getAssignedToEmployee() {
        if (assignedToEmployee==null){
            return "";
        }
        return assignedToEmployee;
    }

    public String getTrnUser() {
        return trnUser;
    }

    public byte[] getAttachmentImage() {
        return attachmentImage;
    }
}
