package org.MiniDev.Report.ReportService;

import org.MiniDev.Report.ReportModel.ItemsSoldModel;
import org.MiniDev.Report.ReportRepo.ItemsSoldRepo;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class ItemsSoldService {

    protected ItemsSoldRepo repo = new ItemsSoldRepo();

    public static Date addDaysToDate(Date fromDate, int dayDiff) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);
        calendar.add(Calendar.DAY_OF_MONTH, dayDiff);

        return calendar.getTime();
    }

    public int getTotalItemsSold(Date fromDate, Date toDate) {
        return repo.getItemsSoldSet().stream()
                .filter(p -> {
                    Date saleDate = p.getSaleDate();
                    return saleDate != null && !saleDate.before(fromDate) && !saleDate.after(toDate);
                })
                .map(ItemsSoldModel::getItemsSold)
                .reduce(0, Integer::sum);
    }


    public int getTotalItemsSoldPrevious(Date fromDate, Date toDate) {
        Date fromPreviousDate = fromPreviousDate(fromDate, toDate);
        Date toPreviousDate = toPreviousDate(fromDate);

        int totalItemsSoldPrevious;
        totalItemsSoldPrevious = repo.getItemsSoldSet().stream()
                .filter(p -> {
                    Date saleDate = p.getSaleDate();
                    return saleDate != null && !saleDate.before(fromPreviousDate) && !saleDate.after(toPreviousDate);
                })
                .map(ItemsSoldModel::getItemsSold)
                .reduce(0, Integer::sum);

        return totalItemsSoldPrevious;
    }

    public String getStringFormatOfItemsSold(Date fromDate, Date toDate) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(getTotalItemsSold(fromDate, toDate));
    }

    public String getRatioFormatOfTotalItemsSold(Date fromDate, Date toDate) {
        int totalItemsSold = getTotalItemsSold(fromDate, toDate);
        int totalItemsSoldPrevious = getTotalItemsSoldPrevious(fromDate, toDate);

        // Check if previous total items sold is zero to avoid division by zero
        if (totalItemsSoldPrevious == 0) {
            return totalItemsSold > 0 ? "100.00 %" : "0.00 %"; // 100% increase if current sales > 0, otherwise 0% change
        }

        double ratio = getComparisonCurrentVsPreviousRatio(totalItemsSoldPrevious, totalItemsSold);
        // Format to two decimal places
        DecimalFormat df = new DecimalFormat("###.##");
        return df.format(ratio) + " %";
    }

    private static double getComparisonCurrentVsPreviousRatio(int totalItemsSoldPrevious, int totalItemsSold) {
        if (totalItemsSoldPrevious > totalItemsSold) {
            // Previous sales are greater, calculate negative percentage decrease
            return -100 + ((double) totalItemsSold / totalItemsSoldPrevious) * 100;
        } else {
            // Current sales are greater or equal, calculate positive percentage increase
            return 100 - ((double) totalItemsSoldPrevious / totalItemsSold) * 100;
        }
    }

    public boolean isMoreThanBefore(Date fromDate, Date toDate) {
        int totalItemsSold = getTotalItemsSold(fromDate, toDate);
        int totalItemsSoldPrevious = getTotalItemsSoldPrevious(fromDate, toDate);
        double comparisonRatio = getComparisonCurrentVsPreviousRatio(totalItemsSoldPrevious, totalItemsSold);
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