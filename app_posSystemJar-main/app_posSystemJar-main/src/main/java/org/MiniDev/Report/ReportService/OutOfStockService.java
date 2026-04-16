package org.MiniDev.Report.ReportService;

import org.MiniDev.Report.ReportModel.OutOfStockModel;
import org.MiniDev.Report.ReportRepo.OutOfStockRepo;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class OutOfStockService {

    protected OutOfStockRepo repo = new OutOfStockRepo();

    public static Date addDaysToDate(Date fromDate, int dayDiff) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);
        calendar.add(Calendar.DAY_OF_MONTH, dayDiff);

        return calendar.getTime();
    }

    public int getOutOfStock(Date fromDate, Date toDate) {
        return repo.getOutOfStockSet().stream()
                .filter(p -> {
                    Date outDate = p.getOutDate();
                    return outDate != null && !outDate.before(fromDate) && !outDate.after(toDate);
                })
                .map(OutOfStockModel::getOutItemName)  // Map to get the outItemName
                .collect(Collectors.toSet())           // Collect distinct values into a Set
                .size();                             // Return the size of the set, which is the distinct count
    }

    public List<OutOfStockModel> getOutOfStockModelLists(Date fromDate, Date toDate) {
        List<OutOfStockModel> outOfStockModelList;

        outOfStockModelList = repo.getOutOfStockSet().stream()
                .filter(p -> {
                    Date trnDate = p.getOutDate();
                    return trnDate != null && !trnDate.before(fromDate) && !trnDate.after(toDate);
                })
                .collect(Collectors.toList()); // Collect filtered results into a list

        return outOfStockModelList;
    }


    public int getOutOfStockPrevious(Date fromDate, Date toDate) {
        Date fromPreviousDate = fromPreviousDate(fromDate, toDate);
        Date toPreviousDate = toPreviousDate(fromDate);

        int totalOutOfStockPrevious;
        totalOutOfStockPrevious = repo.getOutOfStockSet().stream()
                .filter(p -> {
                    Date outDate = p.getOutDate();
                    return outDate != null && !outDate.before(fromPreviousDate) && !outDate.after(toPreviousDate);
                })
                .map(OutOfStockModel::getOutItemName)
                .collect(Collectors.toSet())           // Collect distinct values into a Set
                .size();

        return totalOutOfStockPrevious;
    }

    public String getStringFormatOfOutOfStock(Date fromDate, Date toDate) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(getOutOfStock(fromDate, toDate));
    }

    public String getRatioFormatOfOutOfStockPrevious(Date fromDate, Date toDate) {
        int totalOutOfStockCnt = getOutOfStock(fromDate, toDate);
        int totalOutOfStockCntPrevious = getOutOfStockPrevious(fromDate, toDate);

        if (totalOutOfStockCntPrevious == 0) {
            return totalOutOfStockCnt > 0 ? "100.00 %" : "0.00 %"; // 100% increase if current sales > 0, otherwise 0% change
        }

        double ratio = getComparisonCurrentVsPreviousRatio(totalOutOfStockCntPrevious, totalOutOfStockCnt);
        // Format to two decimal places
        DecimalFormat df = new DecimalFormat("###.##");
        return df.format(ratio) + " %";
    }

    private static double getComparisonCurrentVsPreviousRatio(int totalOutOfStockCntPrevious, int totalOutOfStockCnt) {
        if (totalOutOfStockCntPrevious > totalOutOfStockCnt) {
            return -100 + ((double) totalOutOfStockCnt / totalOutOfStockCntPrevious) * 100;
        } else {
            return 100 - ((double) totalOutOfStockCntPrevious / totalOutOfStockCnt) * 100;
        }
    }

    public boolean isMoreThanBefore(Date fromDate, Date toDate) {
        int totalOutOfStockCnt = getOutOfStock(fromDate, toDate);
        int totalOutOfStockCntPrevious = getOutOfStockPrevious(fromDate, toDate);
        double comparisonRatio = getComparisonCurrentVsPreviousRatio(totalOutOfStockCntPrevious, totalOutOfStockCnt);
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