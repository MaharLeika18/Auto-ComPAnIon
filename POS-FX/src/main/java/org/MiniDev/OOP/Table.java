package org.MiniDev.OOP;

import java.util.Date;

public class Table {

    private String tableName;
    private char tableStatus;
    private Date startDate;
    private Date endDate;
    private char cancelYN;
    private char tempCacheYN;
    private String currentOrderCode;
    private String tempCacheCode;
    private int tableID;

    public Table(String tableName,
                 char tableStatus,
                 Date startDate,
                 Date endDate,
                 char cancelYN,
                 char tempCacheYN,
                 String currentOrderCode,
                 String tempCacheCode,
                 int tableID) {
        this.tableName = tableName;
        this.tableStatus = tableStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cancelYN = cancelYN;
        this.tempCacheYN = tempCacheYN;
        this.currentOrderCode = currentOrderCode;
        this.tempCacheCode = tempCacheCode;
        this.tableID = tableID;
    }

    public String getTableName() {
        return tableName;
    }

    public char getTableStatus() {
        return tableStatus;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public char getCancelYN() {
        return cancelYN;
    }

    public int getTableID() {
        return tableID;
    }

    public char getTempCacheYN() {
        return tempCacheYN;
    }

    public String getCurrentOrderCode() {
        return currentOrderCode;
    }

    public String getTempCacheCode() {
        return tempCacheCode;
    }
}
