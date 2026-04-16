package org.MiniDev.Report.ReportModel;

import java.util.Date;

public class TransactionCntModel {
    private Date trnDate;
    private int trnCnt;

    public TransactionCntModel(Date trnDate, int trnCnt) {
        this.trnDate = trnDate;
        this.trnCnt = trnCnt;
    }

    public Date getTrnDate() {
        return trnDate;
    }

    public void setTrnDate(Date trnDate) {
        this.trnDate = trnDate;
    }

    public int getTrnCnt() {
        return trnCnt;
    }

    public void setTrnCnt(int trnCnt) {
        this.trnCnt = trnCnt;
    }
}
