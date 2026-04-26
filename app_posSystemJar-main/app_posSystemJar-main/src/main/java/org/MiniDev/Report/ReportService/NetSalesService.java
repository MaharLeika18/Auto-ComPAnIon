package org.MiniDev.Report.ReportService;

import org.MiniDev.Report.ReportModel.NetSalesModel;
import org.MiniDev.Report.ReportRepo.NetSalesRepo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Objects;

public class NetSalesService {

    protected NetSalesRepo repo = new NetSalesRepo();

    public static Date addDaysToDate(Date fromDate, int dayDiff) {
        return ItemsSoldService.addDaysToDate(fromDate, dayDiff);  // Return the modified date
    }

    public BigDecimal getTotalNetSales(Date fromDate, Date toDate) {
        // Initialize the total amount
        BigDecimal totalNetSales;
        // Loop through the filtered sales data and sum the netSales
        totalNetSales = repo.getNetSalesModelSet().stream()
                .filter(p -> {
                    Date saleDate = p.getSaleDate();
                    return saleDate != null && !saleDate.before(fromDate) && !saleDate.after(toDate);
                })
                .map(NetSalesModel::getNetSales) // Extract netSales
                .filter(Objects::nonNull) // Ensure it's not null
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Sum the netSales

        return totalNetSales;
    }

    public BigDecimal getTotalNetSalesPrevious(Date fromDate, Date toDate) {
        Date fromPreviousDate = fromPreviousDate(fromDate, toDate);
        Date toPreviousDate = toPreviousDate(fromDate);
        // Initialize the total amount
        BigDecimal totalNetSalesPrevious;
        // Loop through the filtered sales data and sum the netSales
        totalNetSalesPrevious = repo.getNetSalesModelSet().stream()
                .filter(p -> {
                    Date saleDate = p.getSaleDate();
                    return saleDate != null && !saleDate.before(fromPreviousDate) && !saleDate.after(toPreviousDate);
                })
                .map(NetSalesModel::getNetSales) // Extract netSales
                .filter(Objects::nonNull) // Ensure it's not null
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Sum the netSales

        return totalNetSalesPrevious;
    }

    public String getStringFormatOfNetSales(Date fromDate, Date toDate) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(getTotalNetSales(fromDate, toDate));
    }

    public String getRatioFormatOfNetSales(Date fromDate, Date toDate) {
        BigDecimal totalNetSales = getTotalNetSales(fromDate, toDate);
        BigDecimal totalNetSalesPrevious = getTotalNetSalesPrevious(fromDate, toDate);

        // Check if previous total net sales is zero to avoid division by zero
        if (totalNetSalesPrevious.compareTo(BigDecimal.ZERO) == 0) {
            return "0.00 %"; // Or you could return "Infinity %" if preferred
        }
        BigDecimal ratio = getComparisonCurrentVsPreviousRatio(totalNetSalesPrevious, totalNetSales);
        // Format to two decimal places
        DecimalFormat df = new DecimalFormat("###.##");
        return df.format(ratio) + " %";
    }

    private static BigDecimal getComparisonCurrentVsPreviousRatio(BigDecimal totalNetSalesPrevious, BigDecimal totalNetSales) {
        // Handle case where totalNetSalesPrevious is zero to avoid division by zero
        if (totalNetSalesPrevious.compareTo(BigDecimal.ZERO) == 0) {
            // If previous sales are zero, assume 100% increase if current sales > 0, or 0% change if current sales are also zero
            return totalNetSales.compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.valueOf(100) : BigDecimal.ZERO;
        }

        BigDecimal ratio;
        if (totalNetSalesPrevious.compareTo(totalNetSales) > 0) {
            // Previous sales are greater, calculate negative percentage decrease
            ratio = BigDecimal.valueOf(-100).add(
                    totalNetSales.divide(totalNetSalesPrevious, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
            );
        } else {
            // Current sales are greater or equal, calculate positive percentage increase
            ratio = BigDecimal.valueOf(100).subtract(
                    totalNetSalesPrevious.divide(totalNetSales, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
            );
        }
        return ratio;
    }


    public boolean isProfit(Date fromDate, Date toDate) {
        BigDecimal totalNetSales = getTotalNetSales(fromDate, toDate);
        BigDecimal totalNetSalesPrevious = getTotalNetSalesPrevious(fromDate, toDate);
        BigDecimal comparisonRatio = getComparisonCurrentVsPreviousRatio(totalNetSalesPrevious, totalNetSales);
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