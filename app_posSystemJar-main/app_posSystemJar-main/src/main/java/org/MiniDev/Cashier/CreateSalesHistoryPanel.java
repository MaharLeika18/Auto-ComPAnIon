package org.MiniDev.Cashier;

import SqlLoadersAndUpdater.FetchSales;
import UI.DialogCollection;
import UI.MacOSScrollPane;
import UI.RoundedBorderButton;
import UI.RoundedPanel;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.OOP.DrawerSalesLists;
import org.MiniDev.OOP.SalesFilter;
import org.MiniDev.Order.CreateOrderPanel;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.List;


import static UI.UserFinalSettingsVar.*;
import static org.MiniDev.Cashier.CreateTodaySalePanel.GAP;

/**
 * Creates the "Sales History" panel for the sales application.
 */
public class CreateSalesHistoryPanel {

    /**
     * Creates the main panel for sales history.
     *
     * @return A JPanel containing the entire sales history panel.
     */
    protected JPanel createSaleHistoryPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, GAP, GAP, GAP));
        mainPanel.setBackground(COLOR_GRAY);

        mainPanel.add(createLabelAndFilterPanel(), BorderLayout.NORTH);
        mainPanel.add(createSaleHistoryDetailsPanel(), BorderLayout.CENTER);

        return mainPanel;
    }

    RoundedPanel detailsPanelSaleHistory;
    static RoundedPanel innerDetailsPanelSaleHistory;
    JScrollPane histScrollPane;

    public static void refreshDetailsSalesHistPanel() {
        // Clear the existing panels
        innerDetailsPanelSaleHistory.removeAll();

        innerDetailsPanelSaleHistory.add(createHeaderSalesHist());

        // Fetch the updated list of sales
        FetchSales fetchTodaySales = new FetchSales();
        List<DrawerSalesLists> drawerSalesLists = fetchTodaySales.getSaleHistoryAll();

        // Apply filters
        List<DrawerSalesLists> filteredDrawerSalesLists = SalesFilter.filterDrawerSalesList(drawerSalesLists);

        // Create panels for each filtered sale
        for (int i = 0; i < filteredDrawerSalesLists.size(); i++) {
            DrawerSalesLists singleList = filteredDrawerSalesLists.get(i);
            RoundedPanel singleRowPanel = createRowsSalesHist(singleList, i);
            innerDetailsPanelSaleHistory.add(singleRowPanel);
        }

        // Revalidate and repaint to update the panel
        innerDetailsPanelSaleHistory.revalidate();
        innerDetailsPanelSaleHistory.repaint();
    }

    public static void refreshDetailsSalesHistPanel(List<DrawerSalesLists> filteredDrawerSalesList) {
        // Clear the existing panels
        innerDetailsPanelSaleHistory.removeAll();

        innerDetailsPanelSaleHistory.add(createHeaderSalesHist());

        // Create panels for each filtered sale
        for (int i = 0; i < filteredDrawerSalesList.size(); i++) {
            DrawerSalesLists singleList = filteredDrawerSalesList.get(i);
            RoundedPanel singleRowPanel = createRowsSalesHist(singleList, i);
            innerDetailsPanelSaleHistory.add(singleRowPanel);
        }

        // Revalidate and repaint to update the panel
        innerDetailsPanelSaleHistory.revalidate();
        innerDetailsPanelSaleHistory.repaint();
    }


    /**
     * Creates the panel containing sale history details.
     *
     * @return A JPanel with the sale history details.
     */

    protected RoundedPanel createSaleHistoryDetailsPanel() {
        detailsPanelSaleHistory = new RoundedPanel(10);
        detailsPanelSaleHistory.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        detailsPanelSaleHistory.setLayout(new BorderLayout());
        detailsPanelSaleHistory.setBackground(COLOR_WHITE);

        innerDetailsPanelSaleHistory = new RoundedPanel(10);
        innerDetailsPanelSaleHistory.setLayout(new BoxLayout(innerDetailsPanelSaleHistory, BoxLayout.Y_AXIS));
        innerDetailsPanelSaleHistory.setBackground(COLOR_WHITE);
        innerDetailsPanelSaleHistory.add(createHeaderSalesHist());

        FetchSales fetchDrawerSale = new FetchSales();
        List<DrawerSalesLists> drawerSalesLists = fetchDrawerSale.getSaleHistoryAll();

        //No Filtering
        List<DrawerSalesLists> filteredDrawerSalesList = SalesFilter.filterDrawerSalesList(drawerSalesLists);

        // Create panels for each filtered sale
        for (int i = 0; i < filteredDrawerSalesList.size(); i++) {
            DrawerSalesLists singleList = filteredDrawerSalesList.get(i);
            RoundedPanel singleRowPanel = createRowsSalesHist(singleList, i);
            innerDetailsPanelSaleHistory.add(singleRowPanel);
        }

        histScrollPane = new JScrollPane(innerDetailsPanelSaleHistory);
        histScrollPane.setBackground(Color.WHITE);
        histScrollPane.setBorder(BorderFactory.createEmptyBorder());
        histScrollPane.getViewport().setBackground(Color.WHITE);
        histScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        histScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        histScrollPane.getVerticalScrollBar().setUI(new MacOSScrollPane.MacOSScrollBarUI(Color.WHITE));

        detailsPanelSaleHistory.add(histScrollPane, BorderLayout.CENTER);

        return detailsPanelSaleHistory;
    }


    CreateTodaySalePanel createTodaySalePanel = new CreateTodaySalePanel();
    DialogCollection dialogCollection = new DialogCollection();

    /**
     * Creates the panel with label and filter button for sales history.
     *
     * @return A JPanel with the label and filter button.
     */
    protected JPanel createLabelAndFilterPanel() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(0, 65));
        panel.setBorder(BorderFactory.createEmptyBorder(GAP, 0, GAP, 0));
        panel.setBackground(COLOR_GRAY);

        RoundedBorderButton filterButtonSaleHist = createTodaySalePanel.createFilterButtonUI();
        filterButtonSaleHist.addActionListener(e -> {
            dialogCollection.showFilterPopUpForSaleHist("Filter");
        });

        panel.add(CreateTodaySalePanel.createHeaderLabel("Sales History"), BorderLayout.WEST);
        panel.add(filterButtonSaleHist, BorderLayout.EAST);

        return panel;
    }


    private static RoundedPanel createHeaderSalesHist() {
        RoundedPanel headerHoldingPane = new RoundedPanel(10);
        headerHoldingPane.setLayout(new BorderLayout());
        headerHoldingPane.setBackground(COLOR_PANEL_GRAY);
        headerHoldingPane.setBorder(BorderFactory.createEmptyBorder(0, GAP, 0, GAP));
        headerHoldingPane.setPreferredSize(new Dimension(1200, 40));
        headerHoldingPane.setMinimumSize(new Dimension(1200, 40));
        headerHoldingPane.setMaximumSize(new Dimension(1200, 40));

        JPanel centerPart = new JPanel();
        JPanel cashierHeaderPanel = new JPanel(new BorderLayout());
        JPanel firstColumnPanel = new JPanel(new BorderLayout());
        JPanel secondColumnPanel = new JPanel(new BorderLayout());
        JPanel thirdColumnPanel = new JPanel(new BorderLayout());
        JPanel fourthColumnPanel = new JPanel(new BorderLayout());
        JPanel fifthColumnPanel = new JPanel();

        centerPart.setOpaque(false);

        cashierHeaderPanel.setOpaque(false);
        firstColumnPanel.setOpaque(false);
        secondColumnPanel.setOpaque(false);
        thirdColumnPanel.setOpaque(false);
        fourthColumnPanel.setOpaque(false);
        fifthColumnPanel.setOpaque(false);

        centerPart.setMinimumSize(new Dimension(350, 40));

        fifthColumnPanel.setMinimumSize(new Dimension(50, 40));
        fifthColumnPanel.setPreferredSize(new Dimension(260, 40));

        centerPart.setLayout(new GridLayout(1, 5, 0, 0));
        fifthColumnPanel.setLayout(new BorderLayout());

        JLabel cashierHeaderLabel = new JLabel("Teller", JLabel.LEFT);
        JLabel firstColumHeaderLabel = new JLabel("Date", JLabel.LEFT);
        JLabel secondColumnHeaderLabel = new JLabel("Cash Payments", JLabel.LEFT);
        JLabel thirdColumnHeaderLabel = new JLabel("Other Payments", JLabel.LEFT);
        JLabel fourthColumnHeaderLabel = new JLabel("Total Sales", JLabel.LEFT);

        cashierHeaderLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 13));
        firstColumHeaderLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 13));
        secondColumnHeaderLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 13));
        thirdColumnHeaderLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 13));
        fourthColumnHeaderLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 13));

        cashierHeaderPanel.add(cashierHeaderLabel, BorderLayout.CENTER);
        firstColumnPanel.add(firstColumHeaderLabel, BorderLayout.CENTER);
        secondColumnPanel.add(secondColumnHeaderLabel, BorderLayout.CENTER);
        thirdColumnPanel.add(thirdColumnHeaderLabel, BorderLayout.CENTER);
        fourthColumnPanel.add(fourthColumnHeaderLabel, BorderLayout.CENTER);

        centerPart.add(cashierHeaderPanel);
        centerPart.add(firstColumnPanel);
        centerPart.add(secondColumnPanel);
        centerPart.add(thirdColumnPanel);
        centerPart.add(fourthColumnPanel);

        JLabel fifthColumnHeaderLabel = new JLabel("Drawer Notes", JLabel.LEFT);
        fifthColumnHeaderLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 13));
        fifthColumnPanel.add(fifthColumnHeaderLabel, BorderLayout.CENTER);

        headerHoldingPane.add(fifthColumnPanel, BorderLayout.EAST);
        headerHoldingPane.add(centerPart, BorderLayout.CENTER);

        return headerHoldingPane;
    }


    private static RoundedPanel createRowsSalesHist(DrawerSalesLists singleRowSale, double rowIndex) {
        RoundedPanel headerHoldingPane = getHeaderHoldingPane(rowIndex);

        JPanel centerPart = new JPanel();

        JPanel cashierColumnPanel = new JPanel(new BorderLayout());
        JPanel firstColumnPanel = new JPanel(new BorderLayout());
        JPanel secondColumnPanel = new JPanel(new BorderLayout());
        JPanel thirdColumnPanel = new JPanel(new BorderLayout());
        JPanel fourthColumnPanel = new JPanel(new BorderLayout());
        JPanel fifthColumnPanel = new JPanel();

        centerPart.setOpaque(false);

        cashierColumnPanel.setOpaque(false);
        firstColumnPanel.setOpaque(false);
        secondColumnPanel.setOpaque(false);
        thirdColumnPanel.setOpaque(false);
        fourthColumnPanel.setOpaque(false);
        fifthColumnPanel.setOpaque(false);

        centerPart.setMinimumSize(new Dimension(350, 40));

        fifthColumnPanel.setMinimumSize(new Dimension(50, 40));
        fifthColumnPanel.setPreferredSize(new Dimension(260, 40));

        centerPart.setLayout(new GridLayout(1, 5, 0, 0));
        fifthColumnPanel.setLayout(new BorderLayout());


        // Ensure the date is not null and convert if necessary
        LocalDate drawerDate = singleRowSale.getDrawerDate(); // Assuming it's LocalDate, adapt if necessary
        String formattedDate;
        if (drawerDate != null) {
            // Convert LocalDate to Date (if using LocalDate) or directly format LocalDate
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
            formattedDate = drawerDate.format(formatter);
        } else {
            formattedDate = "N/A";  // Handle the case where the date is null
        }


        String formattedSubTotal = CreateOrderPanel.currencyFormat.format(singleRowSale.getTotalSales());
        String cashPaymentTotal = CreateOrderPanel.currencyFormat.format(singleRowSale.getCashSales());
        String otherPaymentTotal = CreateOrderPanel.currencyFormat.format(singleRowSale.getOtherPaymentSales());
        String cashierName = singleRowSale.getTellerName();

        JLabel cashierNameColumnHeaderLabel = new JLabel(cashierName, JLabel.LEFT);
        JLabel firstColumHeaderLabel = new JLabel(formattedDate, JLabel.LEFT);
        JLabel secondColumnHeaderLabel = new JLabel(cashPaymentTotal, JLabel.LEFT);
        JLabel thirdColumnHeaderLabel = new JLabel(otherPaymentTotal, JLabel.LEFT);
        JLabel fourthColumnHeaderLabel = new JLabel(formattedSubTotal, JLabel.LEFT);

        cashierNameColumnHeaderLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13));
        firstColumHeaderLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13));
        secondColumnHeaderLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13));
        thirdColumnHeaderLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13));
        fourthColumnHeaderLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13));

        cashierColumnPanel.add(cashierNameColumnHeaderLabel, BorderLayout.CENTER);
        firstColumnPanel.add(firstColumHeaderLabel, BorderLayout.CENTER);
        secondColumnPanel.add(secondColumnHeaderLabel, BorderLayout.CENTER);
        thirdColumnPanel.add(thirdColumnHeaderLabel, BorderLayout.CENTER);
        fourthColumnPanel.add(fourthColumnHeaderLabel, BorderLayout.CENTER);

        centerPart.add(cashierColumnPanel);
        centerPart.add(firstColumnPanel);
        centerPart.add(secondColumnPanel);
        centerPart.add(thirdColumnPanel);
        centerPart.add(fourthColumnPanel);

        JLabel fifthColumnHeaderLabel = new JLabel(singleRowSale.getDrawerNotes(), JLabel.LEFT);
        fifthColumnHeaderLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13));
        fifthColumnPanel.add(fifthColumnHeaderLabel, BorderLayout.CENTER);

        headerHoldingPane.add(fifthColumnPanel, BorderLayout.EAST);
        headerHoldingPane.add(centerPart, BorderLayout.CENTER);

        return headerHoldingPane;
    }

    private static RoundedPanel getHeaderHoldingPane(double rowIndex) {
        Color backgroundColor = (rowIndex % 2 == 0) ? COLOR_WHITE : COLOR_PANEL_GRAY;

        RoundedPanel headerHoldingPane = new RoundedPanel(10);
        headerHoldingPane.setLayout(new BorderLayout());
        headerHoldingPane.setBackground(backgroundColor);
        headerHoldingPane.setBorder(BorderFactory.createEmptyBorder(0, GAP, 0, GAP));
        headerHoldingPane.setPreferredSize(new Dimension(1200, 40));
        headerHoldingPane.setMinimumSize(new Dimension(1200, 40));
        headerHoldingPane.setMaximumSize(new Dimension(1200, 40));
        return headerHoldingPane;
    }


}
