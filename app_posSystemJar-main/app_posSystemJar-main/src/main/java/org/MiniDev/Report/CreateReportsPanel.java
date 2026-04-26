package org.MiniDev.Report;

import UI.*;

import Utils.DateCalculator;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.Report.ExcelReportFactory.CollectModel.DamagedDetailsModel;
import org.MiniDev.Report.ExcelReportFactory.CollectModel.ItemsSoldDetailsModel;
import org.MiniDev.Report.ExcelReportFactory.CollectModel.NetSalesDetailsModel;
import org.MiniDev.Report.ExcelReportFactory.CollectService.DamagedDetailsService;
import org.MiniDev.Report.ExcelReportFactory.CollectService.ItemsSoldDetailsService;
import org.MiniDev.Report.ExcelReportFactory.CollectService.NetSalesDetailsService;
import org.MiniDev.Report.ExcelReportFactory.ExcelReportFactory;
import org.MiniDev.Report.ReportModel.*;
import org.MiniDev.Report.ReportService.*;
import raven.chart.ChartLegendRenderer;
import raven.chart.data.category.DefaultCategoryDataset;
import raven.chart.data.pie.DefaultPieDataset;
import raven.chart.line.LineChart;
import raven.chart.pie.PieChart;
import raven.datetime.component.date.DatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static UI.UserFinalSettingsVar.*;
import static org.MiniDev.Cashier.CreateTodaySalePanel.*;


public class CreateReportsPanel {
    protected static JLabel netSalesNumber = new JLabel("0");
    protected static JLabel itemsSoldNumber = new JLabel("0");
    protected static JLabel damagedItemNumber = new JLabel("0");
    protected static JLabel totalTransactionNumber = new JLabel("0");
    protected static JLabel outOfStockItemsCountNumber = new JLabel("0");

    protected static JLabel netSalesPercentagePreviousCompared = new JLabel("  0.00%");
    protected static JLabel itemsSoldPercentagePreviousCompared = new JLabel("  0.00%");
    protected static JLabel damagedItemPercentagePreviousCompared = new JLabel("  0.00%");
    protected static JLabel totalTransactionPercentagePreviousCompared = new JLabel("  0.00%");
    protected static JLabel outOfStockItemsPercentagePreviousCompared = new JLabel("  0.00%");

    protected static CustomLineChart lineChart;
    protected static PieChart pieChart1;
    protected static PieChart pieChart2;
    protected static PieChart pieChart3;
    protected static LocalDate[] dates;
    protected static RoundedPanel topSalesPieChartMainPane;
    protected static RoundedPanel outOfStockItemsPieChart;
    protected static RoundedPanel paymentTypePieChartMainPane;


    public CreateReportsPanel() {
    }

    public JPanel createReportsPanel() {
        JPanel reportPanel = new JPanel();
        reportPanel.setLayout(new BorderLayout());

        reportPanel.add(createTopReportPanel(), BorderLayout.NORTH);
        reportPanel.add(createReportCenterMainPanel(), BorderLayout.CENTER);

        return reportPanel;
    }

    private static JPanel createReportCenterMainPanel() {
        JPanel reportCenterPanel = new JPanel(new BorderLayout());
        reportCenterPanel.add(createTopLayerReportPanel(), BorderLayout.NORTH);
        reportCenterPanel.add(createReportCenterInnerMainPanel(), BorderLayout.CENTER);
        return reportCenterPanel;
    }

    private static JPanel createReportCenterInnerMainPanel() {
        JPanel reportCenterInnerMainPanel = new JPanel(new BorderLayout());
        reportCenterInnerMainPanel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, 5, 10));


        reportCenterInnerMainPanel.add(createGraphsHoldingPanel(), BorderLayout.CENTER);
        reportCenterInnerMainPanel.add(createBottomLineChartPanel(), BorderLayout.SOUTH);

        return reportCenterInnerMainPanel;
    }


    private static JPanel createGraphsHoldingPanel() {
        JPanel graphsHoldingPanel = new JPanel(new GridLayout(1, 3, GAP, 0));
        graphsHoldingPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, GAP, 0));

        graphsHoldingPanel.add(createPieChart1());
        graphsHoldingPanel.add(createPieChart2());
        graphsHoldingPanel.add(createPieChart3());
        return graphsHoldingPanel;
    }

    private static Date getToday() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    private static Date getThirtyDaysAgo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        return calendar.getTime();
    }


    // Generate pie chart data based on the date range (Top 5 items)
    public static DefaultPieDataset<String> createPieData1(Date fromDate, Date toDate) {

        PieData1Service service = new PieData1Service();

        // Get filtered sales items within the given date range
        List<TopSalesItems> defaultLists = service.getTopSalesItemsLists(fromDate, toDate);

        // Aggregate sales by item name
        Map<String, Integer> salesAggregation = defaultLists.stream()
                .collect(Collectors.groupingBy(
                        TopSalesItems::getSaleItem, // Group by sale item name
                        Collectors.summingInt(TopSalesItems::getSaleQty) // Sum the sale quantities
                ));

        // Sort by value (quantity sold) in descending order and take top 5
        List<Map.Entry<String, Integer>> topItems = salesAggregation.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue())) // Sort by sales quantity
                .limit(6) // Take top 5 items
                .toList();


        return getStringDefaultPieDatasetInteger(topItems);
    }


    // Generate pie chart data based on the date range (Top 5 items)
    public static DefaultPieDataset<String> createPieData2(Date fromDate, Date toDate) {
        PieData2Service service = new PieData2Service();

        // Get filtered sales items within the given date range
        List<PaymentTypeSales> defaultLists = service.getPaymentTypesSales(fromDate, toDate);

        // Aggregate sales by item name (sum the profit amount using BigDecimal)
        Map<String, BigDecimal> salesAggregation = defaultLists.stream()
                .collect(Collectors.groupingBy(
                        PaymentTypeSales::getPaymentType, // Group by sale item name
                        Collectors.reducing(BigDecimal.ZERO, PaymentTypeSales::getSaleAmount, BigDecimal::add) // Sum profit amounts
                ));

        // Sort by value (profit amount) in descending order and take top 5
        List<Map.Entry<String, BigDecimal>> topItems = salesAggregation.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue())) // Sort by profit amount
                .limit(6) // Take top 5 items
                .toList();


        return getStringDefaultPieDatasetBigDecimal(topItems);
    }

    private static DefaultPieDataset<String> getStringDefaultPieDatasetBigDecimal(List<Map.Entry<String, BigDecimal>> topItems) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        // Add the top 5 aggregated sales data to the dataset, truncating long item names with "..."
        for (Map.Entry<String, BigDecimal> entry : topItems) {
            String itemName = entry.getKey();

            // Truncate the item name to 12 characters and add "..." if it exceeds 12 characters
            if (itemName.length() > 17) {
                itemName = itemName.substring(0, 17) + "..."; // Truncate and add "..."
            }

            dataset.setValue(itemName, entry.getValue());
        }
        return dataset;
    }

    private static DefaultPieDataset<String> getStringDefaultPieDatasetInteger(List<Map.Entry<String, Integer>> topItems) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        // Add the top 5 aggregated sales data to the dataset, replacing long item names with truncated version + "..."
        for (Map.Entry<String, Integer> entry : topItems) {
            String itemName = entry.getKey();

            // Truncate the item name to 15 characters and add "..." if it exceeds 14 characters
            if (itemName.length() > 17) {
                itemName = itemName.substring(0, 17) + "..."; // Truncate and add "..."
            }
            dataset.setValue(itemName, entry.getValue());
        }
        return dataset;
    }

    // Generate pie chart data based on the date range (Top 7 most profitable items)
    public static DefaultPieDataset<String> createPieData3(Date fromDate, Date toDate) {
        PieData3Service service = new PieData3Service();

        // Get filtered sales items within the given date range
        List<MostProfitItems> defaultLists = service.getMostProfitItemsLists(fromDate, toDate);

        // Aggregate sales by item name (sum the profit amount using BigDecimal)
        Map<String, BigDecimal> salesAggregation = defaultLists.stream()
                .collect(Collectors.groupingBy(
                        MostProfitItems::getProfitItem, // Group by sale item name
                        Collectors.reducing(BigDecimal.ZERO, MostProfitItems::getProfitAmount, BigDecimal::add) // Sum profit amounts
                ));

        // Sort by value (profit amount) in descending order and take top 5
        List<Map.Entry<String, BigDecimal>> topItems = salesAggregation.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue())) // Sort by profit amount
                .limit(6) // Take top 5 items
                .toList();


        return getStringDefaultPieDatasetBigDecimal(topItems);
    }

    public static PieChart createPie1(String title, DefaultPieDataset<String> dataset) {
        // Create and configure PieChart instance
        pieChart1 = new PieChart();
        pieChart1.setBackground(COLOR_WHITE);
        pieChart1.setOpaque(false);
        pieChart1.setChartType(PieChart.ChartType.DONUT_CHART);
        pieChart1.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12));

        JLabel header = new JLabel(title);
        header.putClientProperty(FlatClientProperties.STYLE, "font:+1");
        header.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 14));
        pieChart1.setHeader(header);

        // Define colors for this specific pie chart
        Color[] colors = {
                Color.decode("#f87171"),
                Color.decode("#fb923c"),
                Color.decode("#fbbf24"),
                Color.decode("#a3e635"),
                Color.decode("#34d399"),
                Color.decode("#22d3ee"),
                Color.decode("#818cf8")
        };

        // Add custom colors to the chart
        for (Color color : colors) {
            pieChart1.getChartColor().addColor(color);
        }

        // Set the provided dataset
        pieChart1.setDataset(dataset);

        return pieChart1;
    }

    public static PieChart createPie2(String title, DefaultPieDataset<String> dataset) {
        // Create and configure PieChart instance
        pieChart2 = new PieChart();
        pieChart2.setBackground(COLOR_WHITE);
        pieChart2.setOpaque(false);
        pieChart2.setChartType(PieChart.ChartType.DEFAULT);
        pieChart2.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 11));

        JLabel header = new JLabel(title);
        header.putClientProperty(FlatClientProperties.STYLE, "font:+1");
        header.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 14));
        pieChart2.setHeader(header);

        // Define colors for this specific pie chart
        Color[] colors = {
                Color.decode("#f87171"),
                Color.decode("#fb923c"),
                Color.decode("#fbbf24"),
                Color.decode("#a3e635"),
                Color.decode("#34d399"),
                Color.decode("#22d3ee"),
                Color.decode("#818cf8")
        };

        // Add custom colors to the chart
        for (Color color : colors) {
            pieChart2.getChartColor().addColor(color);
        }

        // Set the provided dataset
        pieChart2.setDataset(dataset);

        return pieChart2;
    }

    public static PieChart createPie3(String title, DefaultPieDataset<String> dataset) {
        // Create and configure PieChart instance
        pieChart3 = new PieChart();
        pieChart3.setBackground(COLOR_WHITE);
        pieChart3.setOpaque(false);
        pieChart3.setChartType(PieChart.ChartType.DEFAULT);
        pieChart3.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 11));

        JLabel header = new JLabel(title);
        header.putClientProperty(FlatClientProperties.STYLE, "font:+1");
        header.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 14));
        pieChart3.setHeader(header);

        // Define colors for this specific pie chart
        Color[] colors = {
                Color.decode("#f87171"),
                Color.decode("#fb923c"),
                Color.decode("#fbbf24"),
                Color.decode("#a3e635"),
                Color.decode("#34d399"),
                Color.decode("#22d3ee"),
                Color.decode("#818cf8")
        };

        // Add custom colors to the chart
        for (Color color : colors) {
            pieChart3.getChartColor().addColor(color);
        }

        // Set the provided dataset
        pieChart3.setDataset(dataset);

        return pieChart3;
    }

    private static RoundedPanel createPieChart1() {
        // Initialize the main rounded panel
        topSalesPieChartMainPane = new RoundedPanel(10);
        topSalesPieChartMainPane.setBackground(COLOR_WHITE); // Ensure this is the correct white color
        topSalesPieChartMainPane.setLayout(new BorderLayout());

        // Create the dataset for this chart
        DefaultPieDataset<String> dataset = createPieData1(getThirtyDaysAgo(), getToday());

        pieChart1 = createPie1("Top Sales Items", dataset);

        // Add pieChart1 directly to the RoundedPanel
        topSalesPieChartMainPane.add(pieChart1, BorderLayout.CENTER);

        return topSalesPieChartMainPane;
    }

    private static RoundedPanel createPieChart2() {
        paymentTypePieChartMainPane = new RoundedPanel(10);
        paymentTypePieChartMainPane.setBackground(COLOR_WHITE);
        paymentTypePieChartMainPane.setLayout(new BorderLayout());

        // Create the dataset for this chart
        DefaultPieDataset<String> dataset = createPieData2(getThirtyDaysAgo(), getToday());

        // Create and configure pieChart1
        pieChart2 = createPie2("Payment Sales", dataset);

        // Add pieChart1 directly to the RoundedPanel
        paymentTypePieChartMainPane.add(pieChart2, BorderLayout.CENTER);

        return paymentTypePieChartMainPane;
    }


    private static RoundedPanel createPieChart3() {
        outOfStockItemsPieChart = new RoundedPanel(10);
        outOfStockItemsPieChart.setBackground(COLOR_WHITE);
        outOfStockItemsPieChart.setLayout(new BorderLayout());

        DefaultPieDataset<String> dataset = createPieData3(getThirtyDaysAgo(), getToday());

        pieChart3 = createPie3("Most Profitable Items", dataset);

        // Add pieChart1 directly to the RoundedPanel
        outOfStockItemsPieChart.add(pieChart3, BorderLayout.CENTER);

        return outOfStockItemsPieChart;
    }

    public static void updatePieDataset(PieChart pieChart1, PieChart pieChart2, PieChart pieChart3, Date fromDate, Date toDate) {
        pieChart1.removeAll();
        pieChart2.removeAll();
        pieChart3.removeAll();

        // Get the new data (top 5 items) from the service
        DefaultPieDataset<String> newDataset1 = createPieData1(fromDate, toDate);
        DefaultPieDataset<String> newDataset2 = createPieData2(fromDate, toDate);
        DefaultPieDataset<String> newDataset3 = createPieData3(fromDate, toDate);

        pieChart1 = createPie1("Top Sales Items", newDataset1);
        pieChart1.invalidate();  // Revalidate the pie chart if necessary
        pieChart1.repaint();     // Repaint to ensure the chart is updated
        topSalesPieChartMainPane.add(pieChart1, BorderLayout.CENTER);
        topSalesPieChartMainPane.revalidate();
        topSalesPieChartMainPane.repaint();

        pieChart2 = createPie2("Payment Type Sales", newDataset2);
        pieChart2.invalidate();  // Revalidate the pie chart if necessary
        pieChart2.repaint();     // Repaint to ensure the chart is updated
        paymentTypePieChartMainPane.add(pieChart2, BorderLayout.CENTER);
        paymentTypePieChartMainPane.revalidate();
        paymentTypePieChartMainPane.repaint();

        pieChart3 = createPie3("Most Profit Items", newDataset3);
        pieChart3.invalidate();  // Revalidate the pie chart if necessary
        pieChart3.repaint();     // Repaint to ensure the chart is updated
        outOfStockItemsPieChart.add(pieChart3, BorderLayout.CENTER);
        outOfStockItemsPieChart.revalidate();
        outOfStockItemsPieChart.repaint();
    }

    private void updateNetSales(Date fromDate, Date toDate) {
        NetSalesService netSalesService = new NetSalesService();
        netSalesNumber.setText(netSalesService.getStringFormatOfNetSales(fromDate, toDate));
        netSalesPercentagePreviousCompared.setText(netSalesService.getRatioFormatOfNetSales(fromDate, toDate));
        if (netSalesPercentagePreviousCompared.getText().equals("0.00 %") && netSalesNumber.getText().equals("0")) {
            netSalesPercentagePreviousCompared.setForeground(COLOR_FONT_GRAY);
        } else if (netSalesPercentagePreviousCompared.getText().equals("0.00 %") && !netSalesNumber.getText().equals("0")) {
            netSalesPercentagePreviousCompared.setForeground(COLOR_GREEN);
            netSalesPercentagePreviousCompared.setText("100.00 %");
        } else {
            if (netSalesService.isProfit(fromDate, toDate)) {
                netSalesPercentagePreviousCompared.setForeground(COLOR_GREEN);
            } else if (!netSalesNumber.getText().equals("0")) {
                netSalesPercentagePreviousCompared.setText("100.00 %");
                netSalesPercentagePreviousCompared.setForeground(COLOR_GREEN);
            } else {
                netSalesPercentagePreviousCompared.setForeground(COLOR_ERROR_RED);
            }
        }
    }

    private void updateItemsSold(Date fromDate, Date toDate) {
        ItemsSoldService itemsSoldService = new ItemsSoldService();
        itemsSoldNumber.setText(itemsSoldService.getStringFormatOfItemsSold(fromDate, toDate));
        itemsSoldPercentagePreviousCompared.setText(itemsSoldService.getRatioFormatOfTotalItemsSold(fromDate, toDate));
        if (itemsSoldPercentagePreviousCompared.getText().equals("0.00 %")) {
            itemsSoldPercentagePreviousCompared.setForeground(COLOR_FONT_GRAY);
        } else {
            if (itemsSoldService.isMoreThanBefore(fromDate, toDate)) {
                itemsSoldPercentagePreviousCompared.setForeground(COLOR_GREEN);
            } else {
                itemsSoldPercentagePreviousCompared.setForeground(COLOR_ERROR_RED);
            }
        }
    }

    private void updateDamaged(Date fromDate, Date toDate) {
        DamagedDataService damagedDataService = new DamagedDataService();
        damagedItemNumber.setText(damagedDataService.getStringFormatOfDamaged(fromDate, toDate));
        damagedItemPercentagePreviousCompared.setText(damagedDataService.getRatioFormatOfDamaged(fromDate, toDate));
        if (damagedItemPercentagePreviousCompared.getText().equals("0.00 %") && !damagedItemNumber.getText().equals("0")) {
            damagedItemPercentagePreviousCompared.setForeground(COLOR_ERROR_RED);
            damagedItemPercentagePreviousCompared.setText("100.00 %");
        } else {
            if (damagedDataService.isMoreThanBefore(fromDate, toDate)) {
                damagedItemPercentagePreviousCompared.setForeground(COLOR_GREEN);
            } else if (damagedItemNumber.getText().equals("0")) {
                damagedItemPercentagePreviousCompared.setForeground(COLOR_GREEN);
            } else {
                damagedItemPercentagePreviousCompared.setForeground(COLOR_ERROR_RED);
            }
        }
    }

    private void updateTrnCnt(Date fromDate, Date toDate) {
        TransactionCntService transactionCntService = new TransactionCntService();
        totalTransactionNumber.setText(transactionCntService.getStringFormatOfTransactionCnt(fromDate, toDate));
        totalTransactionPercentagePreviousCompared.setText(transactionCntService.getRatioFormatOfTransactionCntPrevious(fromDate, toDate));
        if (totalTransactionPercentagePreviousCompared.getText().equals("0.00 %")) {
            totalTransactionPercentagePreviousCompared.setForeground(COLOR_ERROR_RED);
        } else {
            if (transactionCntService.isMoreThanBefore(fromDate, toDate)) {
                totalTransactionPercentagePreviousCompared.setForeground(COLOR_GREEN);
            } else {
                totalTransactionPercentagePreviousCompared.setForeground(COLOR_ERROR_RED);
            }
        }
    }

    private void updateOutOfStock(Date fromDate, Date toDate) {
        OutOfStockService outOfStockService = new OutOfStockService();
        outOfStockItemsCountNumber.setText(outOfStockService.getStringFormatOfOutOfStock(fromDate, toDate));
        outOfStockItemsPercentagePreviousCompared.setText(outOfStockService.getRatioFormatOfOutOfStockPrevious(fromDate, toDate));
        if (outOfStockItemsPercentagePreviousCompared.getText().equals("0.00 %")) {
            outOfStockItemsPercentagePreviousCompared.setForeground(COLOR_ERROR_RED);
        } else {
            if (outOfStockService.isMoreThanBefore(fromDate, toDate)) {
                outOfStockItemsPercentagePreviousCompared.setForeground(COLOR_GREEN);
            } else {
                outOfStockItemsPercentagePreviousCompared.setForeground(COLOR_ERROR_RED);
            }
        }
    }

    private void updateMiniReports(Date fromDate, Date toDate) {
        updateNetSales(fromDate, toDate);
        updateItemsSold(fromDate, toDate);
        updateDamaged(fromDate, toDate);
        updateTrnCnt(fromDate, toDate);
        updateOutOfStock(fromDate, toDate);
    }

    private static Component createBottomLineChartPanel() {
        // Create the RoundedPanel with specific rounded corners
        RoundedPanel lineChartPanel = new RoundedPanel(10);
        lineChartPanel.setBackground(COLOR_WHITE);
        lineChartPanel.setLayout(new BorderLayout()); // Ensure proper expansion
        lineChartPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 1, 5));
        lineChartPanel.setPreferredSize(new Dimension(700, 260)); // Ensure enough space

        // Create the CustomLineChart and configure its type and background
        lineChart = new CustomLineChart();
        lineChart.setChartType(LineChart.ChartType.LINE);
        lineChart.setBackground(COLOR_WHITE);
        lineChart.setOpaque(false);

        // ❌ Remove Fixed Width (700x260) to let it expand
        lineChart.setPreferredSize(null);
        lineChart.setMinimumSize(null);
        lineChart.setMaximumSize(null);

        // Force layout update
        lineChart.revalidate();
        lineChart.repaint();

        // Header label
        JLabel header = new JLabel("Profit Data (This Month)");
        header.putClientProperty(FlatClientProperties.STYLE, "font:+1");
        header.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 14));
        lineChart.setHeader(header);

        // Add the LineChart to the RoundedPanel
        lineChartPanel.add(lineChart, BorderLayout.CENTER);

        // Create the line chart data (ensure this method properly initializes the data)
        createOrUpdateLineChartData();

        return lineChartPanel;
    }


    private static void createOrUpdateLineChartData() {
        // Create a new instance of the dataset for the updated chart data
        DefaultCategoryDataset<String, String> categoryDataset = new DefaultCategoryDataset<>();

        // Get current date and date range for the current month
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date fromDate = calendar.getTime();  // First day of current month

        calendar.add(Calendar.MONTH, 1);  // Move to next month
        calendar.set(Calendar.DAY_OF_MONTH, 1);  // Set to the first day of next month
        calendar.add(Calendar.DATE, -1);  // Move back one day to get the last day of the current month
        Date toDate = calendar.getTime();  // Last day of current month

        // Fetch and process the data within the date range
        List<ProfitChartDataModel> dataList = getChartDataInRange(fromDate, toDate);

        // Add data to the dataset
        populateChartDataset(categoryDataset, dataList, fromDate, toDate);

        // Set the updated dataset to the chart
        lineChart.setCategoryDataset(categoryDataset);

        // Optionally, update the chart colors or other properties if needed
        lineChart.getChartColor().addColor(Color.decode("#38bdf8"), Color.decode("#fb7185"), Color.decode("#34d399"));

        // Repaint the chart to apply the updates
        lineChart.repaint();
    }


    private static List<ProfitChartDataModel> getChartDataInRange(Date fromDate, Date toDate) {
        // Initialize ChartDataService to fetch the latest data
        ChartDataService service = new ChartDataService();
        // Get filtered data within the given date range
        return service.getProfitChartDataModelLists(fromDate, toDate);
    }

    private static void populateChartDataset(DefaultCategoryDataset<String, String> categoryDataset,
                                             List<ProfitChartDataModel> dataList,
                                             Date fromDate, Date toDate) {
        // Define date format for UI
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat uiDf = new SimpleDateFormat("dd-MMMM");

        // Loop through the date range (using Calendar to increment days)
        Calendar loopCalendar = Calendar.getInstance();
        loopCalendar.setTime(fromDate);  // Set to start of the month

        while (!loopCalendar.getTime().after(toDate)) {
            String date = df.format(loopCalendar.getTime());

            // Find the ProfitChartDataModel for the current date
            ProfitChartDataModel currentData = dataList.stream()
                    .filter(p -> df.format(p.getReportDate()).equals(date))
                    .findFirst()
                    .orElse(null);  // If no data is found, return null

            // Initialize values
            BigDecimal income = BigDecimal.valueOf(0.0);
            BigDecimal expense = BigDecimal.valueOf(0.0);
            BigDecimal profit = BigDecimal.valueOf(0.0);

            // If there's data for the current date, get the values
            if (currentData != null) {
                income = currentData.getIncomeAmount();
                expense = currentData.getExpAmount();
                profit = currentData.getProfitAmount();
            }

            // Add formatted values to the dataset
            categoryDataset.addValue(formatNumberToMultiThousands(income), "Income", uiDf.format(loopCalendar.getTime()));
            categoryDataset.addValue(formatNumberToMultiThousands(expense), "Expense", uiDf.format(loopCalendar.getTime()));
            categoryDataset.addValue(formatNumberToMultiThousands(profit), "Profit", uiDf.format(loopCalendar.getTime()));

            // Move to the next day
            loopCalendar.add(Calendar.DATE, 1);
        }

        // Control the legend display
        try {
            // Get the start and end date in dataset format
            String startDateStr = categoryDataset.getColumnKey(0).toString();
            String endDateStr = categoryDataset.getColumnKey(categoryDataset.getColumnCount() - 1).toString();
            Date startDate = uiDf.parse(startDateStr);  // Parse dates from the dataset keys
            Date endDate = uiDf.parse(endDateStr);

            DateCalculator dcal = new DateCalculator(startDate, endDate);
            long diff = dcal.getDifferenceDays();

            double d = Math.ceil((diff / 10f));
            lineChart.setLegendRenderer(new ChartLegendRenderer() {
                @Override
                public Component getLegendComponent(Object legend, int index) {
                    // Only display every 'd'-th legend item (or adjust logic as needed)
                    if (index % d == 0) {
                        return super.getLegendComponent(legend, index);  // Render the legend component
                    } else {
                        return null;  // Skip rendering this legend item
                    }
                }
            });
        } catch (ParseException e) {
            System.err.println(e);
        }

        lineChart.repaint();

    }


    private static double formatNumberToMultiThousands(BigDecimal number) {
        return (number.doubleValue() / 1_000);
    }


    public static void pieChartsStartAnimation() {
        pieChart1.startAnimation();
        pieChart2.startAnimation();
        pieChart3.startAnimation();
        lineChart.startAnimation();
    }

    protected static JPanel createTopLayerReportPanel() {
        JPanel topLayerReportPanel = new JPanel(new GridLayout(1, 4, GAP, 0));
        topLayerReportPanel.setBorder(BorderFactory.createEmptyBorder(5, GAP, 0, 10));
        topLayerReportPanel.setPreferredSize(new Dimension(0, 120));

        topLayerReportPanel.add(createRoundedReportPanel("Net Sales", netSalesNumber, netSalesPercentagePreviousCompared, "/NetSaleIconTag.svg", "Net Sales"));
        topLayerReportPanel.add(createRoundedReportPanel("Items Sold", itemsSoldNumber, itemsSoldPercentagePreviousCompared, "/ItemSoldIconTag.svg", "Items Sold"));
        topLayerReportPanel.add(createRoundedReportPanel("Damaged", damagedItemNumber, damagedItemPercentagePreviousCompared, "/DamagedIconTag.svg", "Damaged Items"));
        topLayerReportPanel.add(createRoundedReportPanel("Transactions", totalTransactionNumber, totalTransactionPercentagePreviousCompared, "/TransactionIconTag.svg", "Transactions"));
        topLayerReportPanel.add(createRoundedReportPanel("Out Of Stock", outOfStockItemsCountNumber, outOfStockItemsPercentagePreviousCompared, "/AvgItemSoldIconTag.svg", "Out of Stock"));

        return topLayerReportPanel;
    }

    private static void doViewReport(String reportType, DatePicker datePicker) {
        LocalDate[] dates = datePicker.getSelectedDateRange();
        Date fromDate = null;
        Date toDate = null;
        if (dates != null) {
            LocalDate startDate = dates[0];
            LocalDate endDate = dates[1];
            fromDate = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            toDate = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }

        switch (reportType) {
            case "Net Sales":
                downloadNetSaleViewReport(fromDate, toDate);
                break;
            case "Items Sold":
                downloadItemsSoldViewReport(fromDate, toDate);
                break;
            case "Damaged Items":
                downloadDamagedViewReport(fromDate, toDate);
                break;
            case "Transactions":
                downloadTransactionCntViewReport(fromDate, toDate);
                break;
            case "Out of Stock":
                downloadOutOfStockDetailsViewReport(fromDate, toDate);
                break;
            default:
                JOptionPane.showMessageDialog(null, "No report available for: " + reportType);
                break;
        }
    }


    private static void downloadNetSaleViewReport(Date fromDate, Date toDate) {

        NetSalesDetailsService service = new NetSalesDetailsService();
        List<NetSalesDetailsModel> netSalesLists = service.getNetSalesLists(fromDate, toDate);

        String filePath = "NetSalesData.xls";  // Define the file path for the exported file
        ExcelReportFactory.exportNetSalesToExcel(netSalesLists, filePath);  // Export data to Excel

        // Open the exported Excel file after creation
        File excelFile = new File(filePath);
        if (excelFile.exists()) {
            try {
                // Open the file using the default application associated with Excel files
                Desktop.getDesktop().open(excelFile);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Unable to open the exported Excel file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "The exported file was not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private static void downloadItemsSoldViewReport(Date fromDate, Date toDate) {
        ItemsSoldDetailsService service = new ItemsSoldDetailsService();
        List<ItemsSoldDetailsModel> itemSoldModelList = service.getItemsSoldDetailsList(fromDate, toDate);

        String filePath = "NetSalesData.xls";  // Define the file path for the exported file
        ExcelReportFactory.exportItemsSoldToExcel(itemSoldModelList, filePath);  // Export data to Excel

        // Open the exported Excel file after creation
        File excelFile = new File(filePath);
        if (excelFile.exists()) {
            try {
                // Open the file using the default application associated with Excel files
                Desktop.getDesktop().open(excelFile);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Unable to open the exported Excel file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "The exported file was not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void downloadDamagedViewReport(Date fromDate, Date toDate) {
        DamagedDetailsService service = new DamagedDetailsService();
        List<DamagedDetailsModel> damagedDetailsModelList = service.getDamagedDetailsModelLists(fromDate, toDate);

        String filePath = "DamagedData.xls";  // Define the file path for the exported file
        ExcelReportFactory.exportDamagedListToExcel(damagedDetailsModelList, filePath);  // Export data to Excel

        // Open the exported Excel file after creation
        File excelFile = new File(filePath);
        if (excelFile.exists()) {
            try {
                // Open the file using the default application associated with Excel files
                Desktop.getDesktop().open(excelFile);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Unable to open the exported Excel file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "The exported file was not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void downloadTransactionCntViewReport(Date fromDate, Date toDate) {
        TransactionCntService service = new TransactionCntService();
        List<TransactionCntModel> transactionCntModelList = service.getTransactionCntDetailsLists(fromDate, toDate);

        String filePath = "TransactionCountData.xls";  // Define the file path for the exported file
        ExcelReportFactory.exportTransactionsCntToExcel(transactionCntModelList, filePath);  // Export data to Excel

        // Open the exported Excel file after creation
        File excelFile = new File(filePath);
        if (excelFile.exists()) {
            try {
                // Open the file using the default application associated with Excel files
                Desktop.getDesktop().open(excelFile);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Unable to open the exported Excel file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "The exported file was not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void downloadOutOfStockDetailsViewReport(Date fromDate, Date toDate) {
        OutOfStockService service = new OutOfStockService();
        List<OutOfStockModel> outOfStockModelList = service.getOutOfStockModelLists(fromDate, toDate);

        String filePath = "OutOfStockData.xls";  // Define the file path for the exported file
        ExcelReportFactory.exportOutOfStockToExcel(outOfStockModelList, filePath);  // Export data to Excel

        // Open the exported Excel file after creation
        File excelFile = new File(filePath);
        if (excelFile.exists()) {
            try {
                // Open the file using the default application associated with Excel files
                Desktop.getDesktop().open(excelFile);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Unable to open the exported Excel file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "The exported file was not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private static RoundedPanel createRoundedReportPanel(String title, JLabel numberLabel,
                                                         JLabel percentagePreviousTimeRangeComparedWith,
                                                         String iconPath, String reportType) {
        RoundedPanel holdingPane = new RoundedPanel(10);
        holdingPane.setLayout(new BorderLayout());
        holdingPane.setBackground(COLOR_WHITE);
        holdingPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 5));

        PanelRound leftPane = new PanelRound();
        leftPane.setLayout(new BorderLayout());
        leftPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        leftPane.setRoundTopLeft(10);
        leftPane.setRoundBottomLeft(10);

        PanelRound rightPane = new PanelRound();
        rightPane.setLayout(new BorderLayout());
        rightPane.setRoundTopRight(10);
        rightPane.setRoundBottomRight(10);
        rightPane.setPreferredSize(new Dimension(50, 0));

        // Title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12));
        titleLabel.setForeground(COLOR_FONT_GRAY);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        leftPane.add(titleLabel, BorderLayout.NORTH);

        // Amount label
        numberLabel.setHorizontalAlignment(SwingConstants.LEFT);
        numberLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 20));
        leftPane.add(numberLabel, BorderLayout.CENTER);

        // Percentage label
        percentagePreviousTimeRangeComparedWith.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12));
        percentagePreviousTimeRangeComparedWith.setForeground(Color.GRAY);
        percentagePreviousTimeRangeComparedWith.setHorizontalAlignment(SwingConstants.LEFT);
        leftPane.add(percentagePreviousTimeRangeComparedWith, BorderLayout.SOUTH);

        // View Report Button
        LinkButton viewReportButton = getLinkButtons();

        viewReportButton.addActionListener(e -> {
            doViewReport(reportType, datePicker);
        });


        // Icon
        ImageIcon iconTag = IconCreator.createResizedIcon(iconPath, 23, null);
        JLabel iconLabel = new JLabel(iconTag);
        iconLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        iconLabel.setVerticalAlignment(SwingConstants.TOP);

        // Right Pane Layout
        JPanel iconContainer = new JPanel(new BorderLayout());
        iconContainer.setOpaque(false);
        iconContainer.add(iconLabel, BorderLayout.LINE_END);

        rightPane.add(iconContainer, BorderLayout.NORTH);
        rightPane.add(viewReportButton, BorderLayout.SOUTH);

        holdingPane.add(leftPane, BorderLayout.CENTER);
        holdingPane.add(rightPane, BorderLayout.EAST);

        return holdingPane;
    }


    private static LinkButton getLinkButtons() {
        LinkButton viewReportButton = new LinkButton("<html><u>" + "View Report" + "</u></html>", null);
        viewReportButton.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 9));
        viewReportButton.setForeground(COLOR_FONT_GRAY);
        viewReportButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                viewReportButton.setText("<html><u>" + "View Report" + "</u></html>");
                viewReportButton.setForeground(COLOR_ORANGE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                viewReportButton.setText("<html><u>" + "View Report" + "</u></html>");
                viewReportButton.setForeground(COLOR_FONT_GRAY);
            }
        });
        return viewReportButton;
    }


    protected RoundedPanel createTopReportPanel() {
        RoundedPanel topDatePickerHoldingPanel = new RoundedPanel(10);
        topDatePickerHoldingPanel.setLayout(new BorderLayout());
        topDatePickerHoldingPanel.setPreferredSize(new Dimension(550, 60));
        topDatePickerHoldingPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 0));
        topDatePickerHoldingPanel.setOpaque(false);

        topDatePickerHoldingPanel.add(createDatePickerPanel(), BorderLayout.WEST);

        return topDatePickerHoldingPanel;
    }


    private static final long DEBOUNCE_DELAY = 300; // 300ms debounce delay
    private long lastEventTime = 0;
    private static DatePicker datePicker;

    public RoundedPanel createDatePickerPanel() {
        RoundedPanel datePickerPanel = new RoundedPanel(10);
        datePickerPanel.setPreferredSize(new Dimension(350, 0));
        datePickerPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        datePickerPanel.setBackground(COLOR_WHITE);

        datePicker = new DatePicker();
        datePicker.setDateSelectionMode(DatePicker.DateSelectionMode.BETWEEN_DATE_SELECTED);
        datePicker.setSeparator(" ― ");
        datePicker.setUsePanelOption(true);

        JFormattedTextField editor = new JFormattedTextField();
        editor.setBorder(null);
        datePicker.setEditor(editor);
        datePicker.setBackground(COLOR_WHITE);
        datePicker.setBorder(null);
        datePicker.setDateSelectionAble(localDate -> !localDate.isAfter(LocalDate.now()));

        // Handle date selection with debounce
        datePicker.addDateSelectionListener(e -> handleDateSelection(datePicker));

        // Use GridBagLayout to center the editor
        datePickerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 0, 0); // No padding
        gbc.anchor = GridBagConstraints.CENTER; // Center the editor

        datePickerPanel.add(editor, gbc);

        return datePickerPanel;
    }

    // Debounced date selection handler
    private void handleDateSelection(DatePicker datePicker) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastEventTime < DEBOUNCE_DELAY) {
            return;  // Ignore if the event is within the debounce delay
        }
        lastEventTime = currentTime;

        LocalDate[] dates = datePicker.getSelectedDateRange();
        if (dates != null) {
            LocalDate startDate = dates[0];
            LocalDate endDate = dates[1];
            Date fromDate = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date toDate = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());


            // Execute background task for updating charts and reports
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    updateMiniReports(fromDate, toDate);
                    return null;
                }

                @Override
                protected void done() {
                    createOrUpdateLineChartData();
                    updatePieDataset(pieChart1, pieChart2, pieChart3, fromDate, toDate);
                    pieChartsStartAnimation();
                }
            };
            worker.execute();  // Run background task
        }
    }


}
