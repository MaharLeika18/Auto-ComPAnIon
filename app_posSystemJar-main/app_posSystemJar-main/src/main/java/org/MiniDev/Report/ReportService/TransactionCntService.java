package org.MiniDev.Report.ReportService;

import org.MiniDev.Report.ReportModel.TransactionCntModel;
import org.MiniDev.Report.ReportRepo.TransactionCntRepo;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TransactionCntService {

    protected TransactionCntRepo repo = new TransactionCntRepo();

    public static Date addDaysToDate(Date fromDate, int dayDiff) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);
        calendar.add(Calendar.DAY_OF_MONTH, dayDiff);

        return calendar.getTime();
    }

    public List<TransactionCntModel> getTransactionCntDetailsLists(Date fromDate, Date toDate) {
        List<TransactionCntModel> transactionCntModelList;

        transactionCntModelList = repo.getTransactionCntSet().stream()
                .filter(p -> {
                    Date trnDate = p.getTrnDate();
                    return trnDate != null && !trnDate.before(fromDate) && !trnDate.after(toDate);
                })
                .collect(Collectors.toList()); // Collect filtered results into a list

        return transactionCntModelList;
    }


    public int getTransactionCnt(Date fromDate, Date toDate) {
        return repo.getTransactionCntSet().stream()
                .filter(p -> {
                    Date trnDate = p.getTrnDate();
                    return trnDate != null && !trnDate.before(fromDate) && !trnDate.after(toDate);
                })
                .map(TransactionCntModel::getTrnCnt)
                .reduce(0, Integer::sum);
    }


    public int getTransactionCntPrevious(Date fromDate, Date toDate) {
        Date fromPreviousDate = fromPreviousDate(fromDate, toDate);
        Date toPreviousDate = toPreviousDate(fromDate);

        int totalTransactionCntPrevious;
        totalTransactionCntPrevious = repo.getTransactionCntSet().stream()
                .filter(p -> {
                    Date trnDate = p.getTrnDate();
                    return trnDate != null && !trnDate.before(fromPreviousDate) && !trnDate.after(toPreviousDate);
                })
                .map(TransactionCntModel::getTrnCnt)
                .reduce(0, Integer::sum);

        return totalTransactionCntPrevious;
    }

    public String getStringFormatOfTransactionCnt(Date fromDate, Date toDate) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(getTransactionCnt(fromDate, toDate));
    }

    public String getRatioFormatOfTransactionCntPrevious(Date fromDate, Date toDate) {
        int totalTransactionCnt = getTransactionCnt(fromDate, toDate);
        int totalTransactionCntPrevious = getTransactionCntPrevious(fromDate, toDate);

        if (totalTransactionCntPrevious == 0) {
            return totalTransactionCnt > 0 ? "100.00 %" : "0.00 %"; // 100% increase if current sales > 0, otherwise 0% change
        }

        double ratio = getComparisonCurrentVsPreviousRatio(totalTransactionCntPrevious, totalTransactionCnt);
        // Format to two decimal places
        DecimalFormat df = new DecimalFormat("###.##");
        return df.format(ratio) + " %";
    }

    private static double getComparisonCurrentVsPreviousRatio(int totalTransactionPrevious, int totalTransactionCtn) {
        if (totalTransactionPrevious > totalTransactionCtn) {
            return -100 + ((double) totalTransactionCtn / totalTransactionPrevious) * 100;
        } else {
            return 100 - ((double) totalTransactionPrevious / totalTransactionCtn) * 100;
        }
    }

    public boolean isMoreThanBefore(Date fromDate, Date toDate) {
        int totalTransactionCnt = getTransactionCnt(fromDate, toDate);
        int totalTransactionCntPrevious = getTransactionCntPrevious(fromDate, toDate);
        double comparisonRatio = getComparisonCurrentVsPreviousRatio(totalTransactionCntPrevious, totalTransactionCnt);
        return comparisonRatio > 0; // Compare with 0.0 directly as it's a double
    }


    public Date fromPreviousDate(Date fromDate, Date toDate) {
        long diffInMillis = fromDate.getTime() - toDate.getTime();
        int dayDiff = (int) -(diffInMillis / (1000 * 60 * 60 * 24)); // Milliseconds to days
        return addDaysToDate(fromDate, (-dayDiff - 1));
    }

    public Date toPreviousDate(Date fromDate) {
        return addDaysToDate(fromDate, -1);
    }

}