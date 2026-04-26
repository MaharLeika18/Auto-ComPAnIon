package org.MiniDev.Report.ReportService;

import org.MiniDev.Report.ReportModel.DamagedDataModel;
import org.MiniDev.Report.ReportRepo.DamagedDataRepo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Objects;

public class DamagedDataService {

    protected DamagedDataRepo repo = new DamagedDataRepo();

    public static Date addDaysToDate(Date fromDate, int dayDiff) {
        return ItemsSoldService.addDaysToDate(fromDate, dayDiff);  // Return the modified date
    }

    public BigDecimal getDamagedTotal(Date fromDate, Date toDate) {
        BigDecimal totalDamaged;
        totalDamaged = repo.getDamagedDataModelSet().stream()
                .filter(p -> {
                    Date reportDate = p.getReportDate();
                    return reportDate != null && !reportDate.before(fromDate) && !reportDate.after(toDate);
                })
                .map(DamagedDataModel::getDamagedAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalDamaged;
    }

    public BigDecimal getDamagedTotalPrevious(Date fromDate, Date toDate) {
        Date fromPreviousDate = fromPreviousDate(fromDate, toDate);
        Date toPreviousDate = toPreviousDate(fromDate);

        BigDecimal damagedTotalPrevious;
        damagedTotalPrevious = repo.getDamagedDataModelSet().stream()
                .filter(p -> {
                    Date reportDate = p.getReportDate();
                    return reportDate != null && !reportDate.before(fromPreviousDate) && !reportDate.after(toPreviousDate);
                })
                .map(DamagedDataModel::getDamagedAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return damagedTotalPrevious;
    }

    public String getStringFormatOfDamaged(Date fromDate, Date toDate) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(getDamagedTotal(fromDate, toDate));
    }

    public String getRatioFormatOfDamaged(Date fromDate, Date toDate) {
        BigDecimal damagedTotal = getDamagedTotal(fromDate, toDate);
        BigDecimal damagedTotalPrevious = getDamagedTotalPrevious(fromDate, toDate);
        if (damagedTotalPrevious.compareTo(BigDecimal.ZERO) == 0) {
            return "0.00 %";
        }
        BigDecimal ratio = getComparisonCurrentVsPreviousRatio(damagedTotalPrevious, damagedTotal);
        DecimalFormat df = new DecimalFormat("###.##");
        return df.format(ratio) + " %";
    }

    private static BigDecimal getComparisonCurrentVsPreviousRatio(BigDecimal damagedTotalPrevious, BigDecimal damagedTotal) {
        if (damagedTotalPrevious.compareTo(BigDecimal.ZERO) == 0) {
            return damagedTotal.compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.valueOf(100) : BigDecimal.ZERO;
        }

        BigDecimal ratio;
        if (damagedTotalPrevious.compareTo(damagedTotal) > 0) {
            ratio = BigDecimal.valueOf(-100).add(
                    damagedTotal.divide(damagedTotalPrevious, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
            );
        } else {
            ratio = BigDecimal.valueOf(100).subtract(
                    damagedTotalPrevious.divide(damagedTotal, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
            );
        }
        return ratio;
    }


    public boolean isMoreThanBefore(Date fromDate, Date toDate) {
        BigDecimal damagedTotal = getDamagedTotal(fromDate, toDate);
        BigDecimal damagedTotalPrevious = getDamagedTotalPrevious(fromDate, toDate);
        BigDecimal comparisonRatio = getComparisonCurrentVsPreviousRatio(damagedTotalPrevious, damagedTotal);
        return comparisonRatio.compareTo(BigDecimal.ZERO) > 0;
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