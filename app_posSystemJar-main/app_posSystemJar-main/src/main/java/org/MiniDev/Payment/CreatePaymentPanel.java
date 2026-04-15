package org.MiniDev.Payment;

import DBConnection.DBConnection;
import Security.BatchUserDrawerChecking;
import SqlLoadersAndUpdater.FetchPaymentTypes;
import UI.*;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.Cashier.CreateCashDrawerPanel;
import org.MiniDev.Home.CreateHomePanel;
import org.MiniDev.Home.MiniDevPOS;
import org.MiniDev.OOP.OrderHistoryLists;
import org.MiniDev.Order.CreateOrderPanel;
import org.MiniDev.Table.CreateTablePanel;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.Border;

import static UI.UserFinalSettingsVar.*;
import static org.MiniDev.Table.CreateTablePanel.*;

public class CreatePaymentPanel {

    protected JPanel paymentPanel;
    private JPanel leftCenterMainPane;
    public static JLabel creditNumberLabel;
    public static JLabel changeNumberLabel;
    protected JLabel subTotalNumberLabel = new JLabel("0 Ks"); //SubtotalAmt
    protected JLabel taxNumberLabel = new JLabel("0 Ks"); //TaxAmt
    protected JLabel discountNumberLabel = new JLabel("0 Ks");
    protected JLabel payableNumberLabel = new JLabel("0 Ks"); //SubtotalAmt + TaxAmt
    protected JLabel grandTotalNumberLabel = new JLabel("0 Ks"); //SubtotalAmt + TaxAmt
    protected LinkButton[] paymentNavigationButtons = new LinkButton[2];
    protected  RoundedPanel[] leftRightNavigationPaymentChoosePanel = new RoundedPanel[2];
    private LinkButton lastClickedPayment;
    private RoundedPanel lastClickedPanel;
    protected CardLayout cardLayout = new CardLayout();
    protected RoundedPanel cardPanel = new RoundedPanel(10);
    protected static double currentOrderPayableAmount = 0.0;
    protected static double userInputPaymentNumber = 0.0;
    private JTextField displayBox;
    // Assuming tableNameLabel and orderCode are JLabels declared somewhere in the class

    public CreatePaymentPanel() {
    }

    public JPanel createPaymentPanel(String parentBackPanelName, JLabel lastClickedOrderLabel, JLabel customerInfo, JLabel tableInfo) {
        paymentPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        paymentPanel.setOpaque(false);

        changeNumberLabel = new JLabel("0 Ks");
        creditNumberLabel = new JLabel("0 Ks");

        JPanel paymentLeftPanel = createPaymentLeftPanel(parentBackPanelName, lastClickedOrderLabel, customerInfo, tableInfo, parentBackPanelName, changeNumberLabel, creditNumberLabel);
        JPanel paymentRightPanel = createPaymentRightPanel(changeNumberLabel, creditNumberLabel);

        paymentPanel.add(paymentLeftPanel);
        paymentPanel.add(paymentRightPanel);

        return paymentPanel;
    }

    private JPanel createPaymentLeftPanel(String parentBackPanelName, JLabel lastClickedOrderLabel, JLabel customerInfo, JLabel tableInfo, String parentPanelName,
                                          JLabel changeNumberLabel, JLabel creditNumberLabel
    ) {
        JPanel paymentLeftPanel = new JPanel(new BorderLayout());

        JPanel leftTopPanel = createLeftTopPanel(parentBackPanelName);
        JPanel leftCenterMainPanel = createLeftCenterMainPanel(lastClickedOrderLabel, customerInfo, tableInfo, parentPanelName, changeNumberLabel, creditNumberLabel);

        paymentLeftPanel.add(leftTopPanel, BorderLayout.NORTH);
        paymentLeftPanel.add(leftCenterMainPanel, BorderLayout.CENTER);

        return paymentLeftPanel;
    }

    //Complete Back Button to MainPane
    private static JPanel createLeftTopPanel(String parentBackPanelName) {
        JPanel leftTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftTopPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        leftTopPanel.setPreferredSize(new Dimension(0, 55));
        leftTopPanel.setBackground(COLOR_GRAY);

        LinkButton backButton = getLinkButton(parentBackPanelName); // Assume getLinkButton() is defined elsewhere
        leftTopPanel.add(backButton);

        return leftTopPanel;
    }

    //Create leftMain Holding Pane
    private JPanel createLeftCenterMainPanel(JLabel lastClickedOrderLabel, JLabel customerInfo, JLabel tableInfo, String parentPanelName, JLabel changeNumberLabel, JLabel creditNumberLabel) {
        JPanel leftCenterMainPanel = new JPanel(new BorderLayout());
        leftCenterMainPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 10));
        leftCenterMainPanel.setBackground(COLOR_GRAY);

        JPanel leftCenterInnerMainHoldingPane = createLeftCenterInnerMainHoldingPane(lastClickedOrderLabel, customerInfo, tableInfo, parentPanelName, changeNumberLabel, creditNumberLabel);

        leftCenterMainPanel.add(leftCenterInnerMainHoldingPane, BorderLayout.CENTER);

        return leftCenterMainPanel;
    }

    private JPanel createLeftCenterInnerMainHoldingPane(JLabel lastClickedOrderLabel, JLabel customerInfo, JLabel tableInfo, String parentPanelName, JLabel changeNumberLabel, JLabel creditNumberLabel) {
        JPanel leftCenterInnerMainHoldingPane = new JPanel(new BorderLayout());
        leftCenterInnerMainHoldingPane.setBackground(Color.WHITE);

        JPanel leftCenterMainPaneTop = createLeftCenterMainPaneTop(lastClickedOrderLabel, customerInfo, tableInfo);

        leftCenterMainPane = new JPanel();
        leftCenterMainPane.setLayout(new BoxLayout(leftCenterMainPane, BoxLayout.Y_AXIS));
        leftCenterMainPane.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        leftCenterMainPane.setBackground(COLOR_GRAY);

        JScrollPane paymentScrollPane = new JScrollPane(leftCenterMainPane);
        paymentScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));
        paymentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        paymentScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        paymentScrollPane.getVerticalScrollBar().setUI(new MacOSScrollPane.MacOSScrollBarUI(Color.WHITE));

        JPanel leftCenterMainPaneBottom = createLeftCenterMainPaneBottom(parentPanelName, changeNumberLabel, creditNumberLabel);

        leftCenterInnerMainHoldingPane.add(leftCenterMainPaneTop, BorderLayout.NORTH);
        leftCenterInnerMainHoldingPane.add(paymentScrollPane, BorderLayout.CENTER);
        leftCenterInnerMainHoldingPane.add(leftCenterMainPaneBottom, BorderLayout.SOUTH);

        return leftCenterInnerMainHoldingPane;
    }


    private static JPanel createLeftCenterMainPaneTop(JLabel lastClickedOrderLabel, JLabel customerInfo, JLabel tableInfo) {
        JPanel leftCenterMainPaneTop = new JPanel(new GridLayout(2, 1, 0, 0));
        leftCenterMainPaneTop.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#E8E8E8")));
        leftCenterMainPaneTop.setPreferredSize(new Dimension(0, 80));
        leftCenterMainPaneTop.setBackground(Color.WHITE);

        JPanel orderIDPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        orderIDPanel.setBorder(BorderFactory.createEmptyBorder(15, 5, 0, 0));
        orderIDPanel.setBackground(Color.WHITE);

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 15, 10));
        infoPanel.setBackground(Color.WHITE);

        lastClickedOrderLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 17));

        customerInfo.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 12));
        customerInfo.setForeground(Color.gray);
        tableInfo.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12));
        tableInfo.setForeground(Color.gray);

        infoPanel.add(customerInfo, BorderLayout.WEST);
        infoPanel.add(tableInfo, BorderLayout.EAST);

        orderIDPanel.add(lastClickedOrderLabel);

        leftCenterMainPaneTop.add(orderIDPanel);
        leftCenterMainPaneTop.add(infoPanel);

        return leftCenterMainPaneTop;
    }

    private JPanel createLeftCenterMainPaneBottom(String parentPanelName, JLabel changeNumberLabel, JLabel creditNumberLabel) {
        JPanel leftCenterMainPaneBottom = new JPanel();
        leftCenterMainPaneBottom.setPreferredSize(new Dimension(0, 280));
        leftCenterMainPaneBottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 2, 10));
        leftCenterMainPaneBottom.setBackground(Color.WHITE);
        leftCenterMainPaneBottom.setLayout(new BoxLayout(leftCenterMainPaneBottom, BoxLayout.Y_AXIS));

        JPanel firstLayerOfBottomPane = createFirstLayerOfBottomPane();
        JPanel middleLayerOfBottomPane = createMiddleLayerOfBottomPane(changeNumberLabel, creditNumberLabel);
        JPanel bottomLayerOfBottomPane = createBottomLayerOfBottomPane(parentPanelName);

        // Set preferred heights to achieve the percentage distribution
        int height = 280; // Total height of the parent panel
        int firstLayerHeight = (int) (height * 0.45);
        int middleLayerHeight = (int) (height * 0.25);
        int bottomLayerHeight = (int) (height * 0.30);

        firstLayerOfBottomPane.setPreferredSize(new Dimension(leftCenterMainPaneBottom.getWidth(), firstLayerHeight));
        middleLayerOfBottomPane.setPreferredSize(new Dimension(leftCenterMainPaneBottom.getWidth(), middleLayerHeight));
        bottomLayerOfBottomPane.setPreferredSize(new Dimension(leftCenterMainPaneBottom.getWidth(), bottomLayerHeight));

        leftCenterMainPaneBottom.add(firstLayerOfBottomPane);
        leftCenterMainPaneBottom.add(middleLayerOfBottomPane);
        leftCenterMainPaneBottom.add(bottomLayerOfBottomPane);

        return leftCenterMainPaneBottom;
    }

    private JPanel createFirstLayerOfBottomPane() {
        JPanel firstLayerOfBottomPane = new JPanel(new BorderLayout());
        firstLayerOfBottomPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        firstLayerOfBottomPane.setBackground(Color.WHITE);

        JPanel subTotalTaxDiscountPanel = new JPanel();
        subTotalTaxDiscountPanel.setLayout(new BoxLayout(subTotalTaxDiscountPanel, BoxLayout.Y_AXIS));
        subTotalTaxDiscountPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.decode("#E8E8E8")));
        subTotalTaxDiscountPanel.setBackground(Color.WHITE);

        JPanel grandTotalPanel = new JPanel(new BorderLayout());
        grandTotalPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        grandTotalPanel.setBackground(Color.WHITE);
        grandTotalPanel.setPreferredSize(new Dimension(0, 50));

        JPanel subTotalPanel = new JPanel(new BorderLayout());
        subTotalPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        JPanel taxPanel = new JPanel(new BorderLayout());
        taxPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        JPanel discountPanel = new JPanel(new BorderLayout());
        discountPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        subTotalPanel.setBackground(Color.WHITE);
        taxPanel.setBackground(Color.WHITE);
        discountPanel.setBackground(Color.WHITE);

        subTotalTaxDiscountPanel.add(subTotalPanel);
        subTotalTaxDiscountPanel.add(taxPanel);
        subTotalTaxDiscountPanel.add(discountPanel);

        JLabel subTotalForPayment = new JLabel("Subtotal");
        subTotalForPayment.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12));
        JLabel taxForPayment = new JLabel("Tax");
        taxForPayment.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12));
        JLabel discountForPayment = new JLabel("Discount");
        discountForPayment.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12));

        subTotalPanel.add(subTotalForPayment, BorderLayout.WEST);
        taxPanel.add(taxForPayment, BorderLayout.WEST);
        discountPanel.add(discountForPayment, BorderLayout.WEST);

        subTotalPanel.add(subTotalNumberLabel, BorderLayout.EAST);
        subTotalNumberLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12));
        taxPanel.add(taxNumberLabel, BorderLayout.EAST);
        taxNumberLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12));
        discountPanel.add(discountNumberLabel, BorderLayout.EAST);
        discountNumberLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12));

        JLabel grandTotalForPayment = new JLabel("Grand Total");


        grandTotalPanel.add(grandTotalForPayment, BorderLayout.WEST);
        grandTotalForPayment.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 14));
        grandTotalPanel.add(grandTotalNumberLabel, BorderLayout.EAST);
        grandTotalNumberLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 14));

        firstLayerOfBottomPane.add(subTotalTaxDiscountPanel, BorderLayout.CENTER);
        firstLayerOfBottomPane.add(grandTotalPanel, BorderLayout.SOUTH);

        return firstLayerOfBottomPane;
    }


    public static void showVoucherForOrder(String orderCode) {
        SwingWorker<java.util.List<OrderHistoryLists>, Void> worker = new SwingWorker<>() {
            @Override
            protected java.util.List<OrderHistoryLists> doInBackground() {

                return CreateOrderPanel.orderHistoryLists.stream()
                        .filter(orderRow -> orderRow.getOrderCode().contains(orderCode))
                        .toList();
            }

            @Override
            protected void done() {
                try {
                    List<OrderHistoryLists> orderHistoryList = (get());
                    if (orderHistoryList.isEmpty()) return;

                    double subTotal = 0;
                    for (OrderHistoryLists item : orderHistoryList) {
                        subTotal += item.getOrderSubTotal();
                    }

                    LocalDateTime now = LocalDateTime.now();
                    OrderHistoryLists firstItem = orderHistoryList.get(0);

                    // Show the voucher
                    if (firstItem.getEndDate() == null) {
                        CreateOrderPanel.showVoucher(
                                String.valueOf(CreateCashDrawerPanel.tellerID),
                                orderCode,
                                subTotal,
                                firstItem.getTableName(),
                                orderHistoryList,
                                firstItem.getStartDate(),
                                now,
                                firstItem.getOrderFinish(),
                                firstItem.getDineInToGoDelivery()
                        );
                    } else {
                        CreateOrderPanel.showVoucher(
                                String.valueOf(CreateCashDrawerPanel.tellerID),
                                orderCode,
                                subTotal,
                                firstItem.getTableName(),
                                orderHistoryList,
                                firstItem.getStartDate(),
                                firstItem.getEndDate(),
                                firstItem.getOrderFinish(),
                                firstItem.getDineInToGoDelivery()
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }


    private JPanel createMiddleLayerOfBottomPane(JLabel changeNumberLabel, JLabel creditNumberLabel) {
        JPanel middleLayerOfBottomPane = new RoundedPanel(10);
        middleLayerOfBottomPane.setLayout(new GridLayout(2, 1, 0, 0));
        middleLayerOfBottomPane.setBackground(Color.decode("#f5f5f5"));

        RoundedPanel creditPanel = new RoundedPanel(10);
        creditPanel.setLayout(new BorderLayout());
        creditPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 15));
        RoundedPanel changePanel = new RoundedPanel(10);
        changePanel.setLayout(new BorderLayout());
        changePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 15));

        JLabel creditLabel = new JLabel("Credit");
        creditLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 14));
        JLabel changeLabel = new JLabel("Change");
        changeLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 14));

        creditPanel.add(creditLabel, BorderLayout.WEST);
        changePanel.add(changeLabel, BorderLayout.WEST);

        creditPanel.add(creditNumberLabel, BorderLayout.EAST);
        creditNumberLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 14));


        changePanel.add(changeNumberLabel, BorderLayout.EAST);
        changeNumberLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 14));

        middleLayerOfBottomPane.add(creditPanel);
        middleLayerOfBottomPane.add(changePanel);

        return middleLayerOfBottomPane;
    }

    private JPanel createBottomLayerOfBottomPane(String parentPanelName) {
        JPanel bottomLayerOfBottomPane = new JPanel(new BorderLayout());
        bottomLayerOfBottomPane.setBackground(Color.WHITE);
        bottomLayerOfBottomPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Create and configure the selectPlaceOrderButton
        ImageIcon confirmPaymentIcon = IconCreator.createResizedIcon("/CreditCardIcon.svg", 19, null);
        JButton confirmPaymentButton = new IconTextButton("Confirm Payment", confirmPaymentIcon, 10, Color.decode("#09AA29"), 0);
        confirmPaymentButton.setBackground(Color.decode("#09AA29"));
        confirmPaymentButton.setForeground(Color.WHITE);
        confirmPaymentButton.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12));
        confirmPaymentButton.setPreferredSize(new Dimension(160, 45)); // Adjusted size for visibility

        // Set icon on the left side of the button text
        confirmPaymentButton.setIcon(confirmPaymentIcon);
        // Set the text and icon alignment
        confirmPaymentButton.setHorizontalTextPosition(SwingConstants.RIGHT); // Icon on the left
        confirmPaymentButton.setVerticalTextPosition(SwingConstants.CENTER);
        confirmPaymentButton.setHorizontalAlignment(SwingConstants.CENTER);
        confirmPaymentButton.setVerticalAlignment(SwingConstants.CENTER);

        confirmPaymentButton.addActionListener(e -> {

            if (BatchUserDrawerChecking.isOpened()) {
                confirmPaymentButtonEvent(parentPanelName);
            } else {
                JOptionPane.showMessageDialog(paymentPanel, "Your drawer was closed.");
            }

        });


        bottomLayerOfBottomPane.add(confirmPaymentButton);

        return bottomLayerOfBottomPane;
    }

    private void confirmPaymentButtonEvent(String parentPanelName) {
        if (userInputPaymentNumber == 0.0) {
            DialogCollection.showCustomDialog(paymentPanel, "No credit amount to payment.", "No credit amount to payment");
        } else if (userInputPaymentNumber < (currentOrderPayableAmount - (currentOrderPayableAmount * TAX_PERCENTAGE))) {
            DialogCollection.showCustomDialog(paymentPanel, "Insufficient credit amount.", "Insufficient credit amount");
        } else {
            // Show the dialog immediately
            showOrderClaimedDialog();

            // Process the payment and show voucher asynchronously
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    // Process payment in the database
                    if (parentPanelName.equals("HomeMainPane")) {
                        lastClickedOrderCode = CURRENT_PROCESSING_ORDER_CODE;
                        batchInputPaymentForToGoDeliveryIntoDatabase(CURRENT_PROCESSING_ORDER_CODE, userInputPaymentNumber, TAX_PERCENTAGE);
                        CreateHomePanel.deleteOrderListsPanel();
                    } else {
                        batchInputPaymentForDineInIntoDatabase(lastClickedOrderCode, userInputPaymentNumber, TAX_PERCENTAGE);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get(); // Ensures any exception thrown in doInBackground is rethrown here
                        // Show voucher after payment processing is complete
                        new CreateHomePanel().updateDisplayProductDataInBackground();
                        CreateCashDrawerPanel.refreshDrawerData();
                        CreateOrderPanel.updateOrderHistoryArray();
                        CreateOrderPanel.updateOrderSummaryArray();
                        showVoucherForOrder(lastClickedOrderCode);


                    } catch (Exception ex) {
                        ex.printStackTrace();
                        DialogCollection.showCustomDialog(new JFrame(), "An error occurred while processing the payment.", "Payment Error");
                    } finally {
                        // Reset all payment panels and show the home page
                        resetAllPaymentPanels(parentPanelName);
                        MiniDevPOS.showPanel("Home");
                    }
                }
            };
            worker.execute();
        }
    }

    // Method to show a dialog indicating that the order was completed
    static void showOrderClaimedDialog() {
        DialogCollection.showCustomDialog(new JFrame(), "You have completed the payment!", "Payment Completed");
    }


    private void resetAllPaymentPanels(String parentBackPanelName) {
        // Directly update UI components to avoid unnecessary repainting
        leftCenterMainPane.removeAll();
        lastClickedOrderLabel.setText("");
        customerInfo.setText("");
        tableInfo.setText("");

        creditNumberLabel.setText("0 Ks");
        changeNumberLabel.setText("0 Ks");
        subTotalNumberLabel.setText("0 Ks"); //SubtotalAmt
        taxNumberLabel.setText("0 Ks"); //TaxAmt
        discountNumberLabel.setText("0 Ks");
        payableNumberLabel.setText("0 Ks"); //SubtotalAmt + TaxAmt
        grandTotalNumberLabel.setText("0 Ks"); //SubtotalAmt + TaxAmt

        displayBox.setText("");

        tableNameLabel.setText("");
        orderCode.setText("");

        // Use SwingUtilities.invokeLater to ensure UI updates are handled properly
        SwingUtilities.invokeLater(() -> {
            leftCenterMainPane.revalidate();
            leftCenterMainPane.repaint();

            MiniDevPOS.refreshTableAll();

            CreateTablePanel.selectTableAndPaymentCard.show(CreateTablePanel.selectTableAndPaymentHoldingMainPane, parentBackPanelName);
            CreateHomePanel.mainCard.show(CreateHomePanel.mainAllHoldingPanel, parentBackPanelName);
            MiniDevPOS.showPanel("Home");
            MiniDevPOS.startHomeButtonAnimation();
        });
    }


    private static void batchInputPaymentForDineInIntoDatabase(String orderCode, double userInputPaymentNumber, double TAX_PERCENTAGE) {
        String sql = "{CALL sp_ProcessPayment(?, ?, ?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            // Set the parameters
            stmt.setString(1, orderCode);
            stmt.setBigDecimal(2, BigDecimal.valueOf(userInputPaymentNumber));
            stmt.setBigDecimal(3, BigDecimal.valueOf(TAX_PERCENTAGE));
            stmt.setInt(4, CreateCashDrawerPanel.tellerID);

            // Execute the stored procedure
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void batchInputPaymentForToGoDeliveryIntoDatabase(String orderCode, double userInputPaymentNumber, double TAX_PERCENTAGE) {
        String sql = "{CALL sp_ProcessPaymentToGoDelivery(?, ?, ?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            // Set the parameters
            stmt.setString(1, orderCode);
            stmt.setBigDecimal(2, BigDecimal.valueOf(userInputPaymentNumber));
            stmt.setBigDecimal(3, BigDecimal.valueOf(TAX_PERCENTAGE));
            stmt.setInt(4, CreateCashDrawerPanel.tellerID);

            // Execute the stored procedure
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private JPanel createPaymentRightPanel(JLabel changeNumberLabel, JLabel creditNumberLabel) {
        JPanel paymentRightPanel = new JPanel(new BorderLayout());
        paymentRightPanel.setOpaque(false);
        paymentRightPanel.setBackground(Color.decode("#f5f5f5"));
        paymentRightPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 12));

        paymentRightPanel.add(rightTopPayableCustomerInfoPanel(), BorderLayout.NORTH);
        paymentRightPanel.add(rightCenterHoldingPanel(changeNumberLabel, creditNumberLabel), BorderLayout.CENTER);

        return paymentRightPanel;
    }


    private JPanel rightTopPayableCustomerInfoPanel() {
        JPanel rightTopPayableCustomerInfoPanel = new RoundedPanel(10);
        rightTopPayableCustomerInfoPanel.setLayout(new GridLayout(1, 2, 0, 0));
        rightTopPayableCustomerInfoPanel.setPreferredSize(new Dimension(0, 90));
        rightTopPayableCustomerInfoPanel.setBackground(Color.WHITE);

        rightTopPayableCustomerInfoPanel.add(createRightTopInnerPayableInfoPanel());
        rightTopPayableCustomerInfoPanel.add(createRightTopInnerCustomerInfoPanel());

        return rightTopPayableCustomerInfoPanel;
    }

    private RoundedPanel createRightTopInnerPayableInfoPanel() {
        RoundedPanel rightTopInnerPayableInfoPanel = new RoundedPanel(10);
        rightTopInnerPayableInfoPanel.setLayout(new BorderLayout());
        rightTopInnerPayableInfoPanel.setBackground(Color.WHITE);

        RoundedPanel labelPanel = new RoundedPanel(10);
        RoundedPanel numberPanel = new RoundedPanel(10);

        labelPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 15));
        numberPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 5, 15));

        labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        numberPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        labelPanel.setBackground(Color.WHITE);
        numberPanel.setBackground(Color.WHITE);
        labelPanel.setPreferredSize(new Dimension(100, 35));

        JLabel payableAmountLabel = new JLabel("Payable Amount");
        payableAmountLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 14));

        payableNumberLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 26));
        payableNumberLabel.setForeground(Color.decode("#09AA29"));

        labelPanel.add(payableAmountLabel);
        numberPanel.add(payableNumberLabel);

        rightTopInnerPayableInfoPanel.add(labelPanel, BorderLayout.NORTH);
        rightTopInnerPayableInfoPanel.add(numberPanel, BorderLayout.CENTER);

        return rightTopInnerPayableInfoPanel;

    }

    private static RoundedPanel createRightTopInnerCustomerInfoPanel() {
        RoundedPanel rightTopInnerCustomerInfoPanel = new RoundedPanel(10);
        rightTopInnerCustomerInfoPanel.setBackground(Color.WHITE);

        return rightTopInnerCustomerInfoPanel;
    }


    private JPanel rightCenterHoldingPanel(JLabel changeNumberLabel, JLabel creditNumberLabel) {
        JPanel rightCenterHoldingPanel = new JPanel(new BorderLayout());
        rightCenterHoldingPanel.setOpaque(false);
        rightCenterHoldingPanel.setBackground(Color.decode("#f5f5f5"));

        JPanel gapPanel = new RoundedPanel(10);
        gapPanel.setBackground(Color.decode("#f5f5f5"));
        gapPanel.setPreferredSize(new Dimension(0, 25));

        rightCenterHoldingPanel.add(gapPanel, BorderLayout.NORTH);
        rightCenterHoldingPanel.add(createCenterCalculatorHoldingPanel(changeNumberLabel, creditNumberLabel), BorderLayout.CENTER);

        return rightCenterHoldingPanel;
    }


    private RoundedPanel createCenterCalculatorHoldingPanel(JLabel changeNumberLabel, JLabel creditNumberLabel) {
        RoundedPanel centerCalculatorHoldingPanel = new RoundedPanel(10);
        centerCalculatorHoldingPanel.setLayout(new BorderLayout());
        centerCalculatorHoldingPanel.setOpaque(false);
        centerCalculatorHoldingPanel.setBackground(Color.WHITE);

        // Set GridLayout with no gaps
        RoundedPanel navigationPaymentChoosePanel = new RoundedPanel(10);
        navigationPaymentChoosePanel.setLayout(new GridLayout(1, 2, 0, 0));
        navigationPaymentChoosePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#E8E8E8")));
        navigationPaymentChoosePanel.setPreferredSize(new Dimension(0, 55));
        navigationPaymentChoosePanel.setBackground(Color.WHITE);

        // Create inner panels with no borders
        leftRightNavigationPaymentChoosePanel[0] = new RoundedPanel(10);
        leftRightNavigationPaymentChoosePanel[1] = new RoundedPanel(10);
        leftRightNavigationPaymentChoosePanel[0].setBackground(Color.WHITE);
        leftRightNavigationPaymentChoosePanel[1].setBackground(Color.WHITE);

        leftRightNavigationPaymentChoosePanel[0].setLayout(new BorderLayout());
        leftRightNavigationPaymentChoosePanel[1].setLayout(new BorderLayout());

        // Create buttons with no borders
        paymentNavigationButtons[0] = new LinkButton("Cash", null);
        paymentNavigationButtons[0].setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 15));
        paymentNavigationButtons[0].setForeground(Color.GRAY);
        paymentNavigationButtons[1] = new LinkButton("Other Modes", null);
        paymentNavigationButtons[1].setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 15));
        paymentNavigationButtons[1].setForeground(Color.GRAY);

        // Add buttons to panels
        leftRightNavigationPaymentChoosePanel[0].add(paymentNavigationButtons[0], BorderLayout.CENTER);
        leftRightNavigationPaymentChoosePanel[1].add(paymentNavigationButtons[1], BorderLayout.CENTER);

        // Add inner panels to main navigation panel
        navigationPaymentChoosePanel.add(leftRightNavigationPaymentChoosePanel[0]);
        navigationPaymentChoosePanel.add(leftRightNavigationPaymentChoosePanel[1]);

        // Add navigation panel to the main holding panel
        centerCalculatorHoldingPanel.add(navigationPaymentChoosePanel, BorderLayout.NORTH);

        // Initialize CardLayout for the center panel
        cardPanel.setLayout(cardLayout);
        cardPanel.setOpaque(false);
        cardPanel.setBackground(Color.WHITE);

        // Add the panels to cardPanel
        cardPanel.add(createCashPanel(changeNumberLabel, creditNumberLabel), "CashPanel");
        cardPanel.add(createOtherModesPanel(changeNumberLabel, creditNumberLabel), "OtherModesPanel");

        // Add cardPanel to the center of centerCalculatorHoldingPanel
        centerCalculatorHoldingPanel.add(cardPanel, BorderLayout.CENTER);

        // Add action listeners to buttons
        paymentNavigationButtons[0].addActionListener(e -> {
            handleButtonActionForPaymentNavigation(paymentNavigationButtons[0], leftRightNavigationPaymentChoosePanel[0]);
            cardLayout.show(cardPanel, "CashPanel");
        });
        paymentNavigationButtons[1].addActionListener(e -> {
            handleButtonActionForPaymentNavigation(paymentNavigationButtons[1], leftRightNavigationPaymentChoosePanel[1]);
            cardLayout.show(cardPanel, "OtherModesPanel");
        });

        return centerCalculatorHoldingPanel;
    }


    private RoundedPanel createCashPanel(JLabel changeNumberLabel, JLabel creditNumberLabel) {
        RoundedPanel cashPanel = new RoundedPanel(10);
        cashPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        cashPanel.setLayout(new BorderLayout());
        cashPanel.setBackground(Color.WHITE);

        RoundedPanel displayBoxHolding = new RoundedPanel(10);
        displayBoxHolding.setPreferredSize(new Dimension(0, 70));
        displayBoxHolding.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        displayBoxHolding.setLayout(new BorderLayout());
        displayBoxHolding.setBackground(Color.WHITE);

        // Display Box
        displayBox = new JTextField();
        displayBox.setEditable(false);
        displayBox.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 26));
        displayBox.setHorizontalAlignment(JTextField.RIGHT);
        displayBox.setPreferredSize(new Dimension(0, 65)); // Fit well with layout

        // Padding and Border
        int padding = 10;
        Border paddingBorder = BorderFactory.createEmptyBorder(0, padding, 0, padding);
        Border lineBorder = BorderFactory.createLineBorder(Color.decode("#E0E0E0"), 2);
        Border compoundBorder = BorderFactory.createCompoundBorder(lineBorder, paddingBorder);

        displayBox.setBorder(compoundBorder);
        displayBox.setBackground(Color.WHITE); // Ensure background matches the panel
        displayBoxHolding.add(displayBox, BorderLayout.CENTER);

        // Button Grid Panel
        JPanel buttonGrid = new JPanel();
        buttonGrid.setLayout(new GridBagLayout()); // Use GridBagLayout
        buttonGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        buttonGrid.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; // Allow grid to stretch horizontally
        gbc.weighty = 1.0; // Allow grid to stretch vertically
        gbc.insets = new Insets(5, 5, 5, 5);

        // Create buttons
        String[] buttonLabels = {
                "1", "2", "3",
                "4", "5", "6",
                "7", "8", "9",
                "00", "0", "\u232B", // Backspace symbol
                ".", "Cancel"
        };

        int index = 0;
        for (String label : buttonLabels) {
            gbc.gridx = index % 3;
            gbc.gridy = index / 3;

            if (label.equals("Cancel")) {
                gbc.gridwidth = 2; // Span 2 columns for Cancel button
            } else {
                gbc.gridwidth = 1; // Default span of 1 column
            }

            JButton button = new JButton(label);
            if (label.equals("\u232B")) {  // Backspace symbol case
                button.setFont(new Font("Segoe UI Symbol", Font.BOLD, 19));
            } else {
                button.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 19));
            }
            button.setFocusPainted(false);
            button.setBackground(Color.decode("#f5f5f5"));
            button.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0)); // Padding
            button.setPreferredSize(new Dimension(120, 70)); // Size of buttons
            buttonGrid.add(button, gbc);

            // Add action listeners to buttons
            button.addActionListener(e -> {
                String command = e.getActionCommand();
                if (command.equals("\u232B")) {
                    String currentText = displayBox.getText().replace(",", "");
                    if (!currentText.isEmpty()) {
                        displayBox.setText(formatNumber(currentText.substring(0, currentText.length() - 1)));
                    }
                } else if (command.equals("Cancel")) {
                    displayBox.setText("");
                } else {
                    String currentText = displayBox.getText().replace(",", "");
                    displayBox.setText(formatNumber(currentText + command));
                }

                // Update the user input payment number and update labels
                getLiveUserInputNumber(displayBox.getText().replace(",", ""));
                liveUpdateForCreditAndChangeLabels(userInputPaymentNumber, changeNumberLabel, creditNumberLabel);
            });

            index++;
        }

        cashPanel.add(displayBoxHolding, BorderLayout.NORTH);
        cashPanel.add(buttonGrid, BorderLayout.CENTER);

        return cashPanel;
    }

    private String formatNumber(String input) {
        try {
            long number = Long.parseLong(input);
            return NumberFormat.getNumberInstance().format(number);
        } catch (NumberFormatException e) {
            return input;
        }
    }

    private void getLiveUserInputNumber(String inputText) {
        try {
            userInputPaymentNumber = Double.parseDouble(inputText);
        } catch (NumberFormatException e) {
            userInputPaymentNumber = 0.0;
        }
    }

    private static void getCurrentPayableAmount(double payableAmount) {
        currentOrderPayableAmount = payableAmount;
    }

    private void liveUpdateForCreditAndChangeLabels(double userInputPaymentNumber, JLabel changeNumberLabel, JLabel creditNumberLabel) {
        creditNumberLabel.setText(CreateHomePanel.priceFormat.format(userInputPaymentNumber) + " Ks");

        if (userInputPaymentNumber == 0.0) {
            changeNumberLabel.setText("0 Ks");
        } else {
            changeNumberLabel.setText(CreateHomePanel.priceFormat.format(-(userInputPaymentNumber - currentOrderPayableAmount)) + " Ks");
        }
    }


    private void resetColorsForPaymentNavigationButtons() {
        for (JButton button : paymentNavigationButtons) {
            button.setForeground(Color.gray);
        }
        for (RoundedPanel panel : leftRightNavigationPaymentChoosePanel) {
            panel.setBorder(BorderFactory.createEmptyBorder());
        }
    }

    public  void startAnimationDefaultPayment() {
        if (paymentNavigationButtons[0] != null || leftRightNavigationPaymentChoosePanel[0] != null) {
            assert paymentNavigationButtons[0] != null;
            paymentNavigationButtons[0].setForeground(Color.decode("#FC8019"));
            leftRightNavigationPaymentChoosePanel[0].setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.decode("#FC8019")));
        }
    }

    protected void handleButtonActionForPaymentNavigation(LinkButton button, RoundedPanel panel) {
        resetColorsForPaymentNavigationButtons();
        // Reset the previously clicked button's color and border
        if (lastClickedPayment != null && lastClickedPayment != button) {
            resetColorsForPaymentNavigationButtons();
        }

        if (lastClickedPanel != null && lastClickedPanel != panel) {
            resetColorsForPaymentNavigationButtons();
        }

        // Set the current button as the last clicked button
        lastClickedPayment = button;
        lastClickedPanel = panel;

        // Apply active state to the new button
        button.setForeground(Color.decode("#FC8019"));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.decode("#FC8019")));
    }


    private static LinkButton getLinkButton(String parentBackPanelName) {
        ImageIcon backButtonIcon = new SvgIcon("/LeftArrowIcon.svg", 14).getImageIcon();
        LinkButton backButton = new LinkButton("Back", backButtonIcon);
        // Set the icon separately
        backButton.setIcon(backButtonIcon);

        // Configure text and icon alignment
        backButton.setHorizontalTextPosition(SwingConstants.RIGHT); // Text to the right of the icon
        backButton.setVerticalTextPosition(SwingConstants.CENTER);
        backButton.setHorizontalAlignment(SwingConstants.CENTER);
        backButton.setVerticalAlignment(SwingConstants.CENTER);


        // Add action listener for button click
        backButton.addActionListener(e -> {
            CreateTablePanel.selectTableAndPaymentCard.show(CreateTablePanel.selectTableAndPaymentHoldingMainPane, parentBackPanelName);
            CreateHomePanel.mainCard.show(CreateHomePanel.mainAllHoldingPanel, parentBackPanelName);
        });
        return backButton;
    }

    public void displayOrderDetailsForPaymentProcess(String orderCode, Runnable onComplete) {
        SwingWorker<List<OrderHistoryLists>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<OrderHistoryLists> doInBackground() {

                return CreateOrderPanel.orderHistoryLists.stream()
                        .filter(orderRow -> orderRow.getOrderCode().contains(orderCode))
                        .toList();
            }

            @Override
            protected void done() {
                try {
                    List<OrderHistoryLists> orderHistoryList = new ArrayList<>(get());
                    updateOrderDetailsPaymentPanel(orderHistoryList);

                    // Calculate subTotal and payableAmount
                    double subTotal = orderHistoryList.stream()
                            .mapToDouble(OrderHistoryLists::getOrderSubTotal)
                            .sum();
                    double taxPercentage = TAX_PERCENTAGE; // 5% tax rate
                    double payableAmount = subTotal + (subTotal * taxPercentage);

                    // Update UI with calculated values
                    getConsolidateInfoForPayment(subTotal, payableAmount);
                    getCurrentPayableAmount(payableAmount);

                    // Call the onComplete callback after all processing is done
                    if (onComplete != null) {
                        SwingUtilities.invokeLater(onComplete);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }


    protected  void getConsolidateInfoForPayment(double subTotal, double payableAmount) {
        subTotalNumberLabel.setText(CreateHomePanel.priceFormat.format(subTotal) + " Ks"); //SubtotalAmt
        taxNumberLabel.setText(CreateHomePanel.priceFormat.format(payableAmount - subTotal) + " Ks"); // TaxAmt
        grandTotalNumberLabel.setText(CreateHomePanel.priceFormat.format(payableAmount) + " Ks"); //SubtotalAmt + TaxAmt
        payableNumberLabel.setText(grandTotalNumberLabel.getText());
    }


    private  void updateOrderDetailsPaymentPanel(List<OrderHistoryLists> orderHistoryListForPayment) {
        // Use SwingUtilities.invokeLater to ensure the updates are on the EDT
        SwingUtilities.invokeLater(() -> {
            // Remove all components and add them incrementally
            if (leftCenterMainPane != null) {
                leftCenterMainPane.removeAll();
            }

            // Create and add the panels in a batch to avoid intermediate repaints
            JPanel batchPanel = new JPanel();
            batchPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
            batchPanel.setLayout(new BoxLayout(batchPanel, BoxLayout.Y_AXIS));

            // Reset the alternating background flag at the start
            boolean alternateBackgroundForPayment = false;

            for (OrderHistoryLists item : orderHistoryListForPayment) {
                // Create order panel with alternating background colors
                RoundedPanel orderPanel = createOrderPanelForOrderHist(item, alternateBackgroundForPayment);

                // Toggle the flag for the next iteration
                alternateBackgroundForPayment = !alternateBackgroundForPayment;

                // Add the panel to the batch
                batchPanel.add(orderPanel);
                batchPanel.add(Box.createVerticalStrut(3));
            }

            if (leftCenterMainPane!=null) {
                // Add all panels to the main pane in one go
                leftCenterMainPane.add(batchPanel);

                // Update the UI components in a single batch to avoid excessive revalidation/repaint
                leftCenterMainPane.revalidate();
                leftCenterMainPane.repaint();
            }
        });
    }


    private static RoundedPanel createOrderPanelForOrderHist(OrderHistoryLists oneOrder, boolean useAlternateBackground) {
        // Create the main panel with rounded corners and GridBagLayout
        RoundedPanel orderPanel = new RoundedPanel(10);
        orderPanel.setLayout(new BorderLayout());
        orderPanel.setOpaque(false);
        orderPanel.setBorder(BorderFactory.createCompoundBorder(
                (BorderFactory.createEmptyBorder(1, 2, 1, 10)),
                (BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_LINE_COLOR))
        ));

        // Set background color based on the alternating flag
        Color backgroundColor = useAlternateBackground ? Color.decode("#f6f6f6") : Color.WHITE;
        orderPanel.setBackground(backgroundColor);

        orderPanel.setPreferredSize(new Dimension(130, 70));
        orderPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 70));

        // PictureBox for the product photo
        ImageAvatar picsBox = new ImageAvatar();
        ImageIcon icon = CreateOrderPanel.createImageIcon(oneOrder.getFoodPhoto());
        if (icon != null) {
            picsBox.setImage(icon);
        }
        picsBox.setOpaque(false);
        picsBox.setBorderSize(0);
        picsBox.setArcSize(8);
        picsBox.setBackground(orderPanel.getBackground());
        picsBox.setPreferredSize(new Dimension(65, 65));
        picsBox.setMinimumSize(new Dimension(65, 65));
        picsBox.setMaximumSize(new Dimension(65, 65));

        // Create the right inner panel
        RoundedPanel rightInner = new RoundedPanel(10);
        rightInner.setOpaque(false);
        rightInner.setLayout(new GridLayout(2, 1, 0, 0));
        rightInner.setBackground(orderPanel.getBackground());

        // Item name label (fixed width)
        int maxLength = 60;
        JLabel nameLabel = getjLabel(oneOrder, maxLength);

        // Create the labels for price, quantity, and subtotal
        JLabel unitPriceLabel = new JLabel(CreateHomePanel.priceFormat.format(oneOrder.getOrderPrice1Qty()) + " Ks");
        unitPriceLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12));
        unitPriceLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel quantityLabel = new JLabel("x" + oneOrder.getOrderQty());
        quantityLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12));
        quantityLabel.setForeground(Color.GRAY);
        quantityLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel subtotalLabel = new JLabel(CreateHomePanel.priceFormat.format(oneOrder.getOrderSubTotal()) + " Ks");
        subtotalLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12));
        subtotalLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        // Add labels to rightUpperLayer
        RoundedPanel rightUpperLayer = new RoundedPanel(10);
        rightUpperLayer.setLayout(new BorderLayout());
        rightUpperLayer.setOpaque(false);
        rightUpperLayer.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
        rightUpperLayer.setBackground(orderPanel.getBackground());
        RoundedPanel rightLowerLayer = new RoundedPanel(10);
        rightLowerLayer.setLayout(new BorderLayout());
        rightLowerLayer.setOpaque(false);
        rightLowerLayer.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 0));
        rightLowerLayer.setBackground(orderPanel.getBackground());
        rightUpperLayer.add(nameLabel, BorderLayout.WEST);
        rightUpperLayer.add(quantityLabel, BorderLayout.EAST);


        // Item name label (fixed width)
        RoundedPanel middlePlaceHolder = getRoundedPanel(oneOrder, maxLength, orderPanel);

        rightLowerLayer.add(middlePlaceHolder, BorderLayout.WEST);
        rightLowerLayer.add(subtotalLabel, BorderLayout.CENTER);

        rightInner.add(rightUpperLayer);
        rightInner.add(rightLowerLayer);


        orderPanel.add(picsBox, BorderLayout.WEST);
        orderPanel.add(rightInner, BorderLayout.CENTER);

        return orderPanel;
    }

    private static RoundedPanel getRoundedPanel(OrderHistoryLists oneOrder, int maxLength, RoundedPanel orderPanel) {
        JLabel orderLabel = getLabel(oneOrder, maxLength);

        RoundedPanel middlePlaceHolder = new RoundedPanel(10);
        middlePlaceHolder.setLayout(new BorderLayout());
        middlePlaceHolder.setOpaque(false);
        middlePlaceHolder.setBackground(orderPanel.getBackground());
        middlePlaceHolder.setPreferredSize(new Dimension(200, 100));
        middlePlaceHolder.setMaximumSize(new Dimension(200, 100));
        middlePlaceHolder.add(orderLabel, BorderLayout.WEST);
        return middlePlaceHolder;
    }

    private static JLabel getLabel(OrderHistoryLists oneOrder, int maxLength) {
        int maxLengthForNote = 60;
        String displayNote = oneOrder.getOrderNote().length() > maxLengthForNote ? oneOrder.getOrderNote().substring(0, maxLength) + "..." : oneOrder.getOrderNote();
        JLabel orderLabel;
        if (displayNote.isEmpty()) {
            orderLabel = new JLabel(displayNote);
        } else {
            orderLabel = new JLabel("Note: " + displayNote);
        }
        orderLabel.setToolTipText(oneOrder.getOrderNote());
        orderLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 11));
        orderLabel.setForeground(Color.GRAY);
        orderLabel.setHorizontalAlignment(SwingConstants.LEFT);
        orderLabel.setVerticalAlignment(SwingConstants.CENTER);
        return orderLabel;
    }

    private static JLabel getjLabel(OrderHistoryLists oneOrder, int maxLength) {
        String displayName = oneOrder.getOrderFood().length() > maxLength ? oneOrder.getOrderFood().substring(0, maxLength) + "..." : oneOrder.getOrderFood();
        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setToolTipText(oneOrder.getOrderFood());
        nameLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        nameLabel.setPreferredSize(new Dimension(200, 45)); // Set a fixed width
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        nameLabel.setVerticalAlignment(SwingConstants.CENTER);
        return nameLabel;
    }

    //Alien Version
    private RoundedPanel createOtherModesPanel(JLabel changeNumberLabel, JLabel creditNumberLabel) {
        RoundedPanel otherModesPanel = new RoundedPanel(10);
        otherModesPanel.setLayout(new BorderLayout());
        otherModesPanel.setOpaque(false);
        otherModesPanel.setBackground(COLOR_WHITE);
        otherModesPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        otherModesPanel.add(getUpperLayerOtherModes(otherModesPanel), BorderLayout.NORTH);
        otherModesPanel.add(getCenterMiddleOtherModes(changeNumberLabel, creditNumberLabel), BorderLayout.CENTER);

        return otherModesPanel;
    }

    private String[] paymentNames;
    private RoundedBorderButton[] paymentButtons; // Array to hold references to all buttons
    private RoundedBorderButton lastClickedPaymentButton;

    private JPanel getUpperLayerOtherModes(RoundedPanel parentPanel) {
        JPanel upperLayerOtherModesPanel = new JPanel(new GridLayout(1, 6, 5, 5));
        upperLayerOtherModesPanel.setOpaque(false);
        upperLayerOtherModesPanel.setBackground(COLOR_WHITE);
        upperLayerOtherModesPanel.setPreferredSize(new Dimension(parentPanel.getWidth(), 55));
        upperLayerOtherModesPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        FetchPaymentTypes fetchPaymentTypes = new FetchPaymentTypes();
        List<String> paymentNamesList = fetchPaymentTypes.getShortNamesOfPaymentTypes();
        paymentNames = paymentNamesList.toArray(new String[0]); // Convert List to String array

        paymentButtons = new RoundedBorderButton[paymentNames.length];

        for (int i = 0; i < paymentNames.length; i++) {
            RoundedBorderButton paymentArrayButton = createRoundedBorderButton(paymentNames[i]);
            paymentButtons[i] = paymentArrayButton;
            upperLayerOtherModesPanel.add(paymentArrayButton);
        }

        startIndex0PaymentButtonAnimation();

        return upperLayerOtherModesPanel;
    }

    private RoundedBorderButton createRoundedBorderButton(String buttonName) {
        RoundedBorderButton button = new RoundedBorderButton(buttonName, 80, 50, COLOR_LINE_COLOR);
        button.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12));
        button.setForeground(COLOR_BLACK);
        button.setOverrideBackgroundColor(COLOR_PANEL_GRAY);
        button.setOpaque(false);
        button.setPressedBackgroundColor(Color.decode("#bfdbfe"));
        button.addActionListener(e -> {
            lastClickedPaymentButton = button;
            resetPaymentButtonAnimation();
            animateThisPaymentButton(lastClickedPaymentButton);
        });
        return button;
    }

    public static void animateThisPaymentButton(RoundedBorderButton button) {
        button.setBorderColor(Color.decode("#1e40af"));
        button.setOverrideBackgroundColor(Color.decode("#dbeafe"));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
    }

    protected void resetPaymentButtonAnimation() {
        for (RoundedBorderButton button : paymentButtons) {
            button.setBorderColor(COLOR_LINE_COLOR);
            button.setOverrideBackgroundColor(COLOR_PANEL_GRAY);
            button.setOpaque(false);
            button.setContentAreaFilled(false);
        }
    }

    public void startIndex0PaymentButtonAnimation() {
        if (!(paymentButtons == null)) {
            paymentButtons[0].setBorderColor(Color.decode("#1e40af"));
            paymentButtons[0].setOverrideBackgroundColor(Color.decode("#dbeafe"));
            paymentButtons[0].setContentAreaFilled(false);
            paymentButtons[0].setOpaque(false);
        } else {
            System.out.println("Buttons are null");
        }
    }


    private JPanel getCenterMiddleOtherModes(JLabel changeNumberLabel, JLabel creditNumberLabel) {
        JPanel centerMiddlePanel = new JPanel(new BorderLayout());
        centerMiddlePanel.setOpaque(false);
        centerMiddlePanel.setBackground(COLOR_WHITE);
        centerMiddlePanel.add(createPaymentCalculatorDisplay(changeNumberLabel, creditNumberLabel), BorderLayout.CENTER);

        return centerMiddlePanel;
    }

    private RoundedPanel createPaymentCalculatorDisplay(JLabel changeNumberLabel, JLabel creditNumberLabel) {
        RoundedPanel cashPanel = new RoundedPanel(10);
        cashPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        cashPanel.setLayout(new BorderLayout());
        cashPanel.setBackground(Color.WHITE);

        RoundedPanel displayBoxHolding = new RoundedPanel(10);
        displayBoxHolding.setPreferredSize(new Dimension(0, 70));
        displayBoxHolding.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        displayBoxHolding.setLayout(new BorderLayout());
        displayBoxHolding.setBackground(Color.WHITE);

        // Display Box
        JTextField displayBox = new JTextField();
        displayBox.setEditable(false);
        displayBox.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 26));
        displayBox.setHorizontalAlignment(JTextField.RIGHT);
        displayBox.setPreferredSize(new Dimension(0, 65)); // Fit well with layout

        // Padding and Border
        int padding = 10;
        Border paddingBorder = BorderFactory.createEmptyBorder(0, padding, 0, padding);
        Border lineBorder = BorderFactory.createLineBorder(Color.decode("#E0E0E0"), 2);
        Border compoundBorder = BorderFactory.createCompoundBorder(lineBorder, paddingBorder);

        displayBox.setBorder(compoundBorder);
        displayBox.setBackground(Color.WHITE); // Ensure background matches the panel
        displayBoxHolding.add(displayBox, BorderLayout.CENTER);

        // Button Grid Panel
        JPanel buttonGrid = new JPanel();
        buttonGrid.setLayout(new GridBagLayout()); // Use GridBagLayout
        buttonGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        buttonGrid.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; // Allow grid to stretch horizontally
        gbc.weighty = 1.0; // Allow grid to stretch vertically
        gbc.insets = new Insets(5, 5, 5, 5);

        // Create buttons
        String[] buttonLabels = {
                "1", "2", "3",
                "4", "5", "6",
                "7", "8", "9",
                "00", "0", "\u232B", // Backspace symbol
                ".", "Cancel"
        };

        int index = 0;
        for (String label : buttonLabels) {
            gbc.gridx = index % 3;
            gbc.gridy = index / 3;

            if (label.equals("Cancel")) {
                gbc.gridwidth = 2; // Span 2 columns for Cancel button
            } else {
                gbc.gridwidth = 1; // Default span of 1 column
            }

            JButton button = new JButton(label);
            if (label.equals("\u232B")) {  // Backspace symbol case
                button.setFont(new Font("Segoe UI Symbol", Font.BOLD, 19));
            } else {
                button.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 19));
            }
            button.setFocusPainted(false);
            button.setBackground(Color.decode("#f5f5f5"));
            button.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0)); // Padding
            button.setPreferredSize(new Dimension(120, 70)); // Size of buttons
            buttonGrid.add(button, gbc);

            // Add action listeners to buttons
            button.addActionListener(e -> {
                String command = e.getActionCommand();
                if (command.equals("\u232B")) {
                    String currentText = displayBox.getText().replace(",", "");
                    if (!currentText.isEmpty()) {
                        displayBox.setText(formatNumber(currentText.substring(0, currentText.length() - 1)));
                    }
                } else if (command.equals("Cancel")) {
                    displayBox.setText("");
                } else {
                    String currentText = displayBox.getText().replace(",", "");
                    displayBox.setText(formatNumber(currentText + command));
                }

                // Update the user input payment number and update labels
                getLiveUserInputNumber(displayBox.getText().replace(",", ""));
                liveUpdateForCreditAndChangeLabels(userInputPaymentNumber, changeNumberLabel, creditNumberLabel);
            });

            index++;
        }

        cashPanel.add(displayBoxHolding, BorderLayout.NORTH);
        cashPanel.add(buttonGrid, BorderLayout.CENTER);

        return cashPanel;
    }

}
