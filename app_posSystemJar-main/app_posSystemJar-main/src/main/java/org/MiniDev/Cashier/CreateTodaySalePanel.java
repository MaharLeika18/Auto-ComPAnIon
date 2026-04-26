package org.MiniDev.Cashier;

import SqlLoadersAndUpdater.FetchSales;
import UI.*;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.OOP.SalesFilter;
import org.MiniDev.OOP.TodaySalesLists;
import org.MiniDev.Order.CreateOrderPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static UI.UserFinalSettingsVar.*;
import static org.MiniDev.Cashier.CreateCashDrawerPanel.*;

/**
 * Creates the "Today Sale" panel for the sales application.
 */
public class CreateTodaySalePanel {

    public static final int TOP_LAYER_HEIGHT = 100;
    public static final int GAP = 15;

    protected static RoundedPanel detailsPanel;
    protected static JScrollPane todaySalesScrollPane;
    protected static RoundedPanel innerDetailsPanel;
    protected static JLabel openingDrawerAmountNumber;
    protected static JLabel cashPaymentAmountNumber;
    protected static JLabel otherPaymentAmountNumber;


    /**
     * Creates the main panel for today's sales.
     *
     * @return A JPanel containing the entire sales panel.
     */
    protected JPanel createTodaySalePanel() {
        JPanel saleMainPane = new JPanel(new BorderLayout());
        saleMainPane.setBackground(COLOR_GRAY);

        saleMainPane.add(createTopLayerSalePanel(), BorderLayout.NORTH);
        saleMainPane.add(createBottomLayerSalePanel(), BorderLayout.CENTER);

        return saleMainPane;
    }


    /**
     * Creates the top layer panel for today's sales.
     *
     * @return A JPanel with the top layer of the sales panel.
     */
    protected static JPanel createTopLayerSalePanel() {
        JPanel topLayerSalePanel = new JPanel(new GridLayout(1, 3, GAP, 0));
        topLayerSalePanel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, 0, GAP));
        topLayerSalePanel.setBackground(COLOR_GRAY);
        topLayerSalePanel.setPreferredSize(new Dimension(0, TOP_LAYER_HEIGHT));

        if (initializedOpeningAmountText.isBlank()) {
            initializedOpeningAmountText = "0";
        }

        openingDrawerAmountNumber = new JLabel(initializedOpeningAmountText + " Ks");
        cashPaymentAmountNumber = new JLabel(initializedCashPaymentSaleText + " Ks");
        otherPaymentAmountNumber = new JLabel(initializedOtherPaymentSaleText + " Ks");

        topLayerSalePanel.add(createRoundedPanel("Opening Drawer Amount", openingDrawerAmountNumber));
        topLayerSalePanel.add(createRoundedPanel("Cash Payment Sale", cashPaymentAmountNumber));
        topLayerSalePanel.add(createRoundedPanel("Other Payment Sale", otherPaymentAmountNumber));

        openingDrawerAmountNumber.setForeground(COLOR_ORANGE);
        cashPaymentAmountNumber.setForeground(COLOR_GREEN);
        otherPaymentAmountNumber.setForeground(COLOR_GREEN);

        return topLayerSalePanel;
    }


    protected static void updateOpeningAmount(double userInputOpeningAmount) {
        openingDrawerAmountNumber.setText(decimalFormat.format(userInputOpeningAmount) + " Ks");
    }

    /**
     * Creates a RoundedPanel with a title at the top-left and a label in the bottom-left.
     *
     * @param title The title for the panel.
     * @param label The label to display the amount.
     * @return A RoundedPanel with the specified title and label.
     */
    private static RoundedPanel createRoundedPanel(String title, JLabel label) {
        RoundedPanel panel = new RoundedPanel(10);
        panel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        panel.setLayout(new BorderLayout());
        panel.setBackground(COLOR_WHITE);

        // Title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 13));
        titleLabel.setForeground(Color.GRAY);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Amount label
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 20));
        panel.add(label, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the bottom layer panel for today's sales.
     *
     * @return A JPanel with the bottom layer of the sales panel.
     */
    protected JPanel createBottomLayerSalePanel() {
        JPanel bottomLayerSalePanel = new JPanel(new BorderLayout());
        bottomLayerSalePanel.setBorder(BorderFactory.createEmptyBorder(0, GAP, GAP, GAP));
        bottomLayerSalePanel.setBackground(COLOR_GRAY);

        bottomLayerSalePanel.add(createLabelAndFilterPanel(), BorderLayout.NORTH);
        bottomLayerSalePanel.add(createDetailsPanel(), BorderLayout.CENTER);

        return bottomLayerSalePanel;
    }

    public static void refreshDetailsPanel() {
        // Clear the existing panels
        innerDetailsPanel.removeAll();

        innerDetailsPanel.add(createHeaderTodaySalesHist());

        // Fetch the updated list of sales
        FetchSales fetchTodaySales = new FetchSales();
        List<TodaySalesLists> salesList = fetchTodaySales.getTodaySalesLists();

        // Apply filters
        List<TodaySalesLists> filteredSalesList = SalesFilter.filterSalesList(salesList);

        // Create panels for each filtered sale
        for (int i = 0; i < filteredSalesList.size(); i++) {
            TodaySalesLists singleList = filteredSalesList.get(i);
            RoundedPanel singleRowPanel = createTodaySalesRowLists(singleList, i);
            innerDetailsPanel.add(singleRowPanel);
        }

        // Revalidate and repaint to update the panel
        innerDetailsPanel.revalidate();
        innerDetailsPanel.repaint();
    }

    public static void refreshDetailsPanel(List<TodaySalesLists> filteredSalesList) {
        // Clear the existing panels
        innerDetailsPanel.removeAll();

        innerDetailsPanel.add(createHeaderTodaySalesHist());

        // Create panels for each filtered sale
        for (int i = 0; i < filteredSalesList.size(); i++) {
            TodaySalesLists singleList = filteredSalesList.get(i);
            RoundedPanel singleRowPanel = createTodaySalesRowLists(singleList, i);
            innerDetailsPanel.add(singleRowPanel);
        }

        // Revalidate and repaint to update the panel
        innerDetailsPanel.revalidate();
        innerDetailsPanel.repaint();
    }


    protected static RoundedPanel createDetailsPanel() {
        detailsPanel = new RoundedPanel(10);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        detailsPanel.setLayout(new BorderLayout());
        detailsPanel.setBackground(COLOR_WHITE);

        innerDetailsPanel = new RoundedPanel(10);
        innerDetailsPanel.setLayout(new BoxLayout(innerDetailsPanel, BoxLayout.Y_AXIS));
        innerDetailsPanel.setBackground(COLOR_WHITE);
        innerDetailsPanel.add(createHeaderTodaySalesHist());

        FetchSales fetchTodaySales = new FetchSales();
        List<TodaySalesLists> salesList = fetchTodaySales.getTodaySalesLists();

        //No Filtering
        List<TodaySalesLists> filteredSalesList = SalesFilter.filterSalesList(salesList);

        // Create panels for each filtered sale
        for (int i = 0; i < filteredSalesList.size(); i++) {
            TodaySalesLists singleList = filteredSalesList.get(i);
            RoundedPanel singleRowPanel = createTodaySalesRowLists(singleList, i);
            innerDetailsPanel.add(singleRowPanel);
        }

        todaySalesScrollPane = new JScrollPane(innerDetailsPanel);
        todaySalesScrollPane.setBackground(Color.WHITE);
        todaySalesScrollPane.setBorder(BorderFactory.createEmptyBorder());
        todaySalesScrollPane.getViewport().setBackground(Color.WHITE);
        todaySalesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        todaySalesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        todaySalesScrollPane.getVerticalScrollBar().setUI(new MacOSScrollPane.MacOSScrollBarUI(Color.WHITE));

        detailsPanel.add(todaySalesScrollPane, BorderLayout.CENTER);

        return detailsPanel;
    }


    private static RoundedPanel createHeaderTodaySalesHist() {
        RoundedPanel headerHoldingPane = new RoundedPanel(10);
        headerHoldingPane.setLayout(new GridLayout(1, 5, 0, 0));
        headerHoldingPane.setBackground(COLOR_PANEL_GRAY);
        headerHoldingPane.setBorder(BorderFactory.createEmptyBorder(0, GAP, 0, GAP));
        headerHoldingPane.setPreferredSize(new Dimension(1200, 40));
        headerHoldingPane.setMinimumSize(new Dimension(1200, 40));
        headerHoldingPane.setMaximumSize(new Dimension(1200, 40));

        JPanel cashierColumnPanel = new JPanel(new BorderLayout());
        JPanel firstColumnPanel = new JPanel(new BorderLayout());
        JPanel secondColumnPanel = new JPanel(new BorderLayout());
        JPanel thirdColumnPanel = new JPanel(new BorderLayout());
        JPanel fourthColumnPanel = new JPanel(new BorderLayout());

        cashierColumnPanel.setOpaque(false);
        firstColumnPanel.setOpaque(false);
        secondColumnPanel.setOpaque(false);
        thirdColumnPanel.setOpaque(false);
        fourthColumnPanel.setOpaque(false);

        JLabel cashierHeaderLabel = new JLabel("Claimed Teller", JLabel.LEFT);
        JLabel firstColumHeaderLabel = new JLabel("Order ID", JLabel.LEFT);
        JLabel secondColumnHeaderLabel = new JLabel("Time ", JLabel.LEFT);
        JLabel thirdColumnHeaderLabel = new JLabel("Order Total", JLabel.LEFT);
        JLabel fourthColumnHeaderLabel = new JLabel("Payment Mode", JLabel.LEFT);

        cashierHeaderLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 13));
        firstColumHeaderLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 13));
        secondColumnHeaderLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 13));
        thirdColumnHeaderLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 13));
        fourthColumnHeaderLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 13));

        cashierColumnPanel.add(cashierHeaderLabel, BorderLayout.CENTER);
        firstColumnPanel.add(firstColumHeaderLabel, BorderLayout.CENTER);
        secondColumnPanel.add(secondColumnHeaderLabel, BorderLayout.CENTER);
        thirdColumnPanel.add(thirdColumnHeaderLabel, BorderLayout.CENTER);
        fourthColumnPanel.add(fourthColumnHeaderLabel, BorderLayout.CENTER);

        headerHoldingPane.add(cashierColumnPanel);
        headerHoldingPane.add(firstColumnPanel);
        headerHoldingPane.add(secondColumnPanel);
        headerHoldingPane.add(thirdColumnPanel);
        headerHoldingPane.add(fourthColumnPanel);

        return headerHoldingPane;
    }


    private static RoundedPanel createTodaySalesRowLists(TodaySalesLists singleRowSale, double rowIndex) {
        RoundedPanel headerHoldingPane = getHeaderHoldingPane(rowIndex);

        JPanel cashierColumnPanel = new JPanel(new BorderLayout());
        JPanel firstColumnPanel = new JPanel(new BorderLayout());
        JPanel secondColumnPanel = new JPanel(new BorderLayout());
        JPanel thirdColumnPanel = new JPanel(new BorderLayout());
        JPanel fourthColumnPanel = new JPanel(new BorderLayout());

        cashierColumnPanel.setOpaque(false);
        firstColumnPanel.setOpaque(false);
        secondColumnPanel.setOpaque(false);
        thirdColumnPanel.setOpaque(false);
        fourthColumnPanel.setOpaque(false);

        String formattedDate = CreateOrderPanel.dateFormat.format(singleRowSale.getOrderEndDate());
        String formattedSubTotal = CreateOrderPanel.currencyFormat.format(singleRowSale.getOrderSubTotal());

        JLabel cashierColumnHeaderLabel = new JLabel(singleRowSale.getClosedCashierName(), JLabel.LEFT);
        JLabel firstColumHeaderLabel = new JLabel(singleRowSale.getOrderCode(), JLabel.LEFT);
        JLabel secondColumnHeaderLabel = new JLabel(formattedDate, JLabel.LEFT);
        JLabel thirdColumnHeaderLabel = new JLabel(formattedSubTotal, JLabel.LEFT);
        JLabel fourthColumnHeaderLabel = new JLabel(singleRowSale.getPaymentMethod(), JLabel.LEFT);

        cashierColumnHeaderLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13));
        firstColumHeaderLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13));
        secondColumnHeaderLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13));
        thirdColumnHeaderLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13));
        fourthColumnHeaderLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13));

        cashierColumnPanel.add(cashierColumnHeaderLabel, BorderLayout.CENTER);
        firstColumnPanel.add(firstColumHeaderLabel, BorderLayout.CENTER);
        secondColumnPanel.add(secondColumnHeaderLabel, BorderLayout.CENTER);
        thirdColumnPanel.add(thirdColumnHeaderLabel, BorderLayout.CENTER);
        fourthColumnPanel.add(fourthColumnHeaderLabel, BorderLayout.CENTER);

        headerHoldingPane.add(cashierColumnPanel);
        headerHoldingPane.add(firstColumnPanel);
        headerHoldingPane.add(secondColumnPanel);
        headerHoldingPane.add(thirdColumnPanel);
        headerHoldingPane.add(fourthColumnPanel);

        return headerHoldingPane;
    }

    private static RoundedPanel getHeaderHoldingPane(double rowIndex) {
        Color backgroundColor = (rowIndex % 2 == 0) ? COLOR_WHITE : COLOR_PANEL_GRAY;

        RoundedPanel headerHoldingPane = new RoundedPanel(10);
        headerHoldingPane.setLayout(new GridLayout(1, 5, 0, 0));
        headerHoldingPane.setBackground(backgroundColor);
        headerHoldingPane.setBorder(BorderFactory.createEmptyBorder(0, GAP, 0, GAP));
        headerHoldingPane.setPreferredSize(new Dimension(1200, 40));
        headerHoldingPane.setMinimumSize(new Dimension(1200, 40));
        headerHoldingPane.setMaximumSize(new Dimension(1200, 40));
        return headerHoldingPane;
    }


    protected RoundedBorderButton createFilterButtonUI() {
        ImageIcon filterIcon = new SvgIcon("/FilterIcon.svg", 20).getImageIcon();
        RoundedBorderButton filterButton = new RoundedBorderButton("Filters", 60, 40, COLOR_ORANGE);
        filterButton.setIcon(filterIcon);
        filterButton.setBackground(COLOR_WHITE);
        filterButton.setForeground(COLOR_ORANGE);
        filterButton.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 13));
        filterButton.setPreferredSize(new Dimension(60, 40));
        filterButton.setMaximumSize(new Dimension(60, 40));
        filterButton.setMinimumSize(new Dimension(60, 40));
        filterButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        filterButton.setVerticalTextPosition(SwingConstants.CENTER);
        filterButton.setHorizontalAlignment(SwingConstants.CENTER);
        filterButton.setVerticalAlignment(SwingConstants.CENTER);

        return filterButton;
    }

    /**
     * Creates the label for the sales history section.
     *
     * @return A JLabel with the sales history text.
     */
    public static JLabel createHeaderLabel(String text) {
        JLabel saleHistoryLabel = new JLabel(text);
        saleHistoryLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 16));
        saleHistoryLabel.setPreferredSize(new Dimension(500, 50)); // Adjust size as needed
        saleHistoryLabel.setHorizontalAlignment(SwingConstants.LEFT); // Adjust alignment
        return saleHistoryLabel;
    }


    /**
     * Creates the panel with label and filter button for sales history.
     *
     * @return A JPanel with the label and filter button.
     */
    protected JPanel createLabelAndFilterPanel() {
        JPanel labelAndFilterPanel = new JPanel(new BorderLayout());
        labelAndFilterPanel.setPreferredSize(new Dimension(0, 65));
        labelAndFilterPanel.setBorder(BorderFactory.createEmptyBorder(GAP, 0, GAP, 0));
        labelAndFilterPanel.setBackground(COLOR_GRAY);

        RoundedBorderButton filterButton = createFilterButtonUI();
        filterButton.addActionListener(e -> {
            DialogCollection dialogCollection = new DialogCollection();
            dialogCollection.showFilterPopUp("Filter");
        });

        labelAndFilterPanel.add(createHeaderLabel("Sales History"), BorderLayout.WEST);
        labelAndFilterPanel.add(filterButton, BorderLayout.EAST);

        return labelAndFilterPanel;
    }

}
