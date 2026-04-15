package org.MiniDev.Home;

import DBConnection.DBConnection;
import Security.BatchUserDrawerChecking;
import SqlLoadersAndUpdater.FetchCounterLists;
import SqlLoadersAndUpdater.FetchProductsToDisplay;
import SqlLoadersAndUpdater.GenerateOrderCodeFactory;
import UI.*;
import Utils.DefaultProductPhoto;
import Utils.PrintUtil;
import Utils.VoucherInfo;
import Utils.VoucherPrinter;
import com.formdev.flatlaf.FlatClientProperties;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import UI.MacOSScrollPane.*;
import net.miginfocom.swing.MigLayout;
import org.MiniDev.Backoffice.StockManagement.StockInnerPage;
import org.MiniDev.Cashier.CreateCashDrawerPanel;
import org.MiniDev.Customer.CreateCustomerPanel;
import org.MiniDev.OOP.CounterLists;
import org.MiniDev.OOP.Product;
import org.MiniDev.Order.CreateOrderPanel;
import org.MiniDev.Payment.CreatePaymentPanel;
import org.MiniDev.Payment.MiniPaymentPanel;
import org.MiniDev.Table.CreateTablePanel;
import org.MiniDev.Table.TableLists;
import org.apache.batik.swing.JSVGCanvas;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static UI.IconCreator.createResizedIcon;
import static UI.UserFinalSettingsVar.*;
import static UI.UserFinalSettingsVar.CURRENT_PROCESSING_ORDER_CODE;
import static org.MiniDev.Cashier.CreateTodaySalePanel.GAP;


public class CreateHomePanel {
    protected static JPanel productBoxCardPanel;
    protected static String productName;
    protected static int displaySubtotal = 0;
    protected static int displayQuantity = 0;
    protected static int panelCount = 0;
    protected static JPanel productHoldingPanel;
    protected static JPanel gapPanel;
    public static List<RoundedPanel> orderListsPanelList = new ArrayList<>();
    protected static JPanel homeRightPanel;
    protected static JPanel homeRightTopPanel;
    protected static JPanel homeRightCenterOrderPanel;
    protected static JPanel homeRightBottomOrderButtonsPanel;
    protected static final Map<String, RoundedBorderButton> buttonCache = new HashMap<>();
    private static RoundedBorderButton allButton;
    protected static List<JButton> categoryButtons;
    protected static JScrollPane scrollPane;
    protected static JPanel orderButtonTopLayerPanel;
    protected static JPanel subtotalOfOrderListsPanel;
    protected static JPanel taxOfOrderListsPanel;
    protected static JPanel orderButtonCenterLayerPanel;
    protected static JPanel orderButtonBottomLayerPanel;
    protected static IconTextButton orderListPrintButton;
    protected static JLabel subTotalNumber = new JLabel("0 Ks");
    protected static JLabel taxTotalNumber = new JLabel("0 Ks");
    protected static JLabel payableAmountNumber = new JLabel("0 Ks");
    protected static JLabel tableNameLabel;
    protected static Map<String, JLabel> notesMap = new HashMap<>();
    protected static RoundedBorderButton lastClickedButton = null;
    protected static JPanel topNavigationPanel;
    protected static RoundedPanel topLeftNavigationButtonPanel;

    protected static JButton buttonDineIn;
    protected static JButton buttonToGo;
    protected static JButton buttonDelivery;
    protected static Color backgroundColor;
    protected static JButton activeButton = null; // Track the currently active button

    public static JPanel mainAllHoldingPanel;
    public static CardLayout mainCard;
    protected static JPanel deliToGoPaymentPane;
    protected MiniPaymentPanel miniPaymentPanel = new MiniPaymentPanel(CreatePaymentPanel.changeNumberLabel, CreatePaymentPanel.creditNumberLabel);
    protected static CardLayout productDisplayCard = new CardLayout();
    protected JSVGCanvas svgCanvasForNoSelected;

    //Author : Alien
    protected static List<Product> allDisplayProductLists = new ArrayList<>();
    protected List<Product> filteredProductOfIndex1 = new ArrayList<>();
    protected List<Product> filteredProductOfIndex2 = new ArrayList<>();
    protected List<Product> filteredProductOfIndex3 = new ArrayList<>();
    protected List<Product> filteredProductOfIndex4 = new ArrayList<>();
    protected List<Product> filteredProductOfIndex5 = new ArrayList<>();
    protected List<Product> filteredProductOfSearching = new ArrayList<>();

    protected JComponent indexSearchPanel = new JPanel();
    protected JPanel noProductFoundInDisplayBoxPanel;

    protected JSVGCanvas svgCanvasForNoData;
    protected static JPanel cartPanel;
    protected static JScrollPane orderScrollPane;
    protected static CardLayout cartDisplayCard;
    protected static JPanel cartDrawingPanel;

    public CreateHomePanel() {
        new BatchUserDrawerChecking();
    }

    protected JPanel createHomePanel() {
        mainAllHoldingPanel = new JPanel();
        mainCard = new CardLayout();
        mainAllHoldingPanel.setLayout(mainCard);

        JPanel homeMainPanel = new JPanel();
        homeMainPanel.setLayout(new BorderLayout());
        homeMainPanel.setBackground(COLOR_GRAY);
        homeMainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel homeLeftPanel = new JPanel(new BorderLayout());
        homeLeftPanel.setPreferredSize(new Dimension(550, 0));
        homeLeftPanel.setBackground(COLOR_GRAY);
        homeLeftPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Create top navigation panel
        JPanel topNaviHoldingLeft = new JPanel(new BorderLayout());
        topNaviHoldingLeft.setOpaque(false);
        topNaviHoldingLeft.setPreferredSize(new Dimension(550, 40));
        topNaviHoldingLeft.setBorder(BorderFactory.createEmptyBorder(0, GAP, 0, GAP));
        topNavigationPanel = createTopLeftNavigationButtonPanel();
        topNaviHoldingLeft.add(topNavigationPanel, BorderLayout.CENTER);

        productHoldingPanel = new JPanel(new BorderLayout());
        productHoldingPanel.setBackground(COLOR_GRAY);
        productHoldingPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 5));

        // Create gap panel
        gapPanel = new JPanel();
        gapPanel.setOpaque(false);
        gapPanel.setBackground(COLOR_GRAY);
        gapPanel.setPreferredSize(new Dimension(550, 5));


        // Add top navigation panel to homeLeftPanel
        homeLeftPanel.add(topNaviHoldingLeft, BorderLayout.NORTH);
        productHoldingPanel.add(gapPanel, BorderLayout.NORTH);

        productHoldingPanel.add(initProductBoxPanelDisplay(), BorderLayout.CENTER);
        homeLeftPanel.add(productHoldingPanel, BorderLayout.CENTER);

        animateBorderColor(allButton);

        homeRightPanel = new JPanel(new BorderLayout()); // Set BorderLayout for homeRightPanel
        homeRightPanel.setBackground(COLOR_WHITE);
        homeRightPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, COLOR_LINE_COLOR));

        homeRightTopPanel = createHomeRightTopPanel();
        // Creating a MatteBorder with 1 pixel thickness, colored light gray, on the top of topPanel
        Border border = BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_LINE_COLOR);
        homeRightTopPanel.setBorder(border);
        homeRightPanel.add(homeRightTopPanel, BorderLayout.NORTH); // Add homeRightTopPanel to CENTER of homeRightPanel

        homeRightPanel.add(initCartDisplayPanel(), BorderLayout.CENTER);
        homeRightPanel.add(cartPanel, BorderLayout.CENTER);

        homeRightBottomOrderButtonsPanel = new JPanel(new GridBagLayout());
        homeRightBottomOrderButtonsPanel.setPreferredSize(new Dimension(300, 160));
        homeRightBottomOrderButtonsPanel.setBackground(COLOR_GRAY);
        homeRightPanel.add(homeRightBottomOrderButtonsPanel, BorderLayout.SOUTH);

// Constraints for the top panel
        GridBagConstraints gbcTop = new GridBagConstraints();
        gbcTop.gridx = 0;
        gbcTop.gridy = 0;
        gbcTop.weightx = 1.0; // Occupy full horizontal space
        gbcTop.weighty = 0.2; // Allocate more vertical space
        gbcTop.fill = GridBagConstraints.BOTH; // Expand both horizontally and vertically
        homeRightBottomOrderButtonsPanel.add(createTopLayerPanel(), gbcTop);

// Constraints for the center panel
        GridBagConstraints gbcCenter = new GridBagConstraints();
        gbcCenter.gridx = 0;
        gbcCenter.gridy = 1;
        gbcCenter.weightx = 1.0; // Occupy full horizontal space
        gbcCenter.weighty = 0.1; // Less vertical space compared to top and bottom
        gbcCenter.fill = GridBagConstraints.BOTH; // Expand both horizontally and vertically
        homeRightBottomOrderButtonsPanel.add(createCenterLayerPanel(), gbcCenter);

// Constraints for the proceed panel
        GridBagConstraints gbcBottom = new GridBagConstraints();
        gbcBottom.gridx = 0;
        gbcBottom.gridy = 2;
        gbcBottom.weightx = 1.0; // Occupy full horizontal space
        gbcBottom.weighty = 0.06; // Allocate more vertical space
        gbcBottom.fill = GridBagConstraints.BOTH; // Expand both horizontally and vertically
        homeRightBottomOrderButtonsPanel.add(createOrderProceedPanel(), gbcBottom);

        homeMainPanel.add(homeLeftPanel, BorderLayout.WEST);
        homeMainPanel.add(homeRightPanel, BorderLayout.CENTER);


        deliToGoPaymentPane = miniPaymentPanel.createPaymentPanel("HomeMainPane", lastClickedOrderLabel, customerInfo, tableInfo);

        mainAllHoldingPanel.add(homeMainPanel, "HomeMainPane");
        mainAllHoldingPanel.add(deliToGoPaymentPane, "PaymentPanel");

        return mainAllHoldingPanel;
    }

    protected static JLabel lastClickedOrderLabel = new JLabel();
    protected static JLabel customerInfo = new JLabel();
    protected static JLabel tableInfo = new JLabel();


    private static void updateTableNoAndOrderCode() {
        customerInfo.setText("Default Customer");
        tableInfo.setText(generateDineInToGoDeliveryStatus());
        lastClickedOrderLabel.setText(CURRENT_PROCESSING_ORDER_CODE);
    }


    private static void showVoucher() {
        VoucherInfo voucherInfo = new VoucherInfo();
        JFrame popupFrame = new JFrame("Order Summary");
        popupFrame.setSize(500, 600);
        popupFrame.setLocationRelativeTo(null);
        popupFrame.setUndecorated(true); // Remove window borders for a cleaner look
        popupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 10, 20, 10));
        contentPanel.setBackground(COLOR_WHITE);

        // Box panel for the voucher
        JPanel voucherPanel = new JPanel();
        voucherPanel.setLayout(new BorderLayout());
        voucherPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        voucherPanel.setBackground(COLOR_WHITE);

        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        textPane.setBackground(COLOR_WHITE);
        textPane.setBorder(null);

        displaySubtotal = 0;
        displayQuantity = 0;
        panelCount = 0;
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss a");
        LocalDateTime now = LocalDateTime.now();

        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        StringBuilder summary = new StringBuilder();
        summary.append("<html><body style='text-align:center; margin: 0; font-family: Roboto;'>");

        summary.append("<h2 style='margin: 5px;'>").append(voucherInfo.getCompanyName()).append("</h2>");
        summary.append("<p style='margin: 2px;'>").append(voucherInfo.getAddressLine1()).append("</p>");
        summary.append("<p style='margin: 2px;'>").append(voucherInfo.getAddressLine2()).append("</p>");
        summary.append("<p style='margin: 2px;'>").append(voucherInfo.getPhoneNumber()).append("</p>");
        summary.append("<p style='margin: 2px;'>").append(voucherInfo.getEmailAddress()).append("</p>");

        summary.append("<div style='border-bottom: 1px dashed black; width: 100%; margin: 0; padding: 0;'></div>");
        summary.append("<div style='margin: 0; padding: 1px;'>");
        summary.append("<table style='width:100%; border-collapse:collapse; margin: 0; padding: 4px;'>");

        summary.append("<tr>");
        summary.append("<td style='padding: 0; text-align:left; margin: 0;'>Receipt No: 000001</td>");
        summary.append("<td style='padding: 0; text-align:right; margin: 0;'>Order No: #00001</td>");
        summary.append("</tr>");

        summary.append("<tr>");
        summary.append("<td style='padding: 0; text-align:left; margin: 0;'>No: ").append(tableNameLabel.getText()).append("</td>");
        summary.append("<td style='padding: 0; text-align:right; margin: 0;'>").append(date.format(now)).append("</td>");
        summary.append("</tr>");

        summary.append("</table>");
        summary.append("<div style='border-top: 1px dashed black; width: 100%; margin: 0; padding: 0;'></div>");
        summary.append("</div>");

        summary.append("<h3 style='margin: 3px 0;'>Order Summary</h3>");
        summary.append("<table style='width:100%; border-collapse:collapse;'>");
        summary.append("<thead>");
        summary.append("<tr style='border-bottom: none;'>"); // Remove the line at the bottom of the header
        summary.append("<th style='padding: 5px; text-align:right; width:10%; border-bottom: none;'>No.</th>");
        summary.append("<th style='padding: 5px; text-align:left; width:40%; border-bottom: none;'>Description</th>");
        summary.append("<th style='padding: 5px; text-align:right; width:15%; border-bottom: none;'>UnitPrice</th>");
        summary.append("<th style='padding: 5px; text-align:right; width:10%; border-bottom: none;'>Qty</th>");
        summary.append("<th style='padding: 5px; text-align:right; width:25%; border-bottom: none;'>SubTotal</th>");
        summary.append("</tr>");
        summary.append("</thead>");
        summary.append("<tbody>");

        for (RoundedPanel panel : orderListsPanelList) {
            JLabel nameLabel = (JLabel) panel.getComponent(0);
            String productName = (nameLabel != null ? nameLabel.getText() : "Name label not found");

            JPanel buttonsPanel = findButtonsPanel(panel);
            assert buttonsPanel != null;
            JLabel subtotalLabel = findComponentByType(buttonsPanel, JLabel.class, "subtotalLabel");
            JLabel quantityLabel = findComponentByType(buttonsPanel, JLabel.class, "quantityLabel");

            String subtotalText = (subtotalLabel != null ? subtotalLabel.getText() : "0 Ks").replace(" Ks", "").replace(",", "");
            String quantityText = (quantityLabel != null ? quantityLabel.getText() : "0");

            int subtotalPrice = Integer.parseInt(subtotalText);
            int quantity = Integer.parseInt(quantityText);

            panelCount++;
            displaySubtotal += subtotalPrice;
            displayQuantity += quantity;

            summary.append("<tr>");
            summary.append("<td style='padding: 5px; text-align:right;'>").append(String.format("%d", panelCount)).append("</td>");
            summary.append("<td style='padding: 5px; text-align:left;'>").append(productName).append("</td>");
            summary.append("<td style='padding: 5px; text-align:right;'>").append(decimalFormat.format(subtotalPrice / quantity)).append("</td>");
            summary.append("<td style='padding: 5px; text-align:right;'>").append(String.format("%d", quantity)).append("</td>");
            summary.append("<td style='padding: 5px; text-align:right;'>").append(decimalFormat.format(subtotalPrice)).append(" Ks</td>");
            summary.append("</tr>");
        }

        summary.append("</tbody>");
        summary.append("</table>");
        summary.append("<div style='border-bottom: 1px dashed black; width: 100%; margin: 0; padding: 0;'></div>");

        double tax = displaySubtotal * TAX_PERCENTAGE; // Assuming a 5% tax rate
        double grandTotal = displaySubtotal + tax;

        summary.append("<table style='width:100%; border-collapse:collapse;'>");
        summary.append("<tr>");
        summary.append("<td style='padding: 5px; text-align:left; width:70%;'>Tax (5%)</td>");
        summary.append("<td style='padding: 5px; text-align:right; width:30%;'>").append(decimalFormat.format(tax)).append(" Ks</td>");
        summary.append("</tr>");
        summary.append("<tr>");
        summary.append("<td style='padding: 5px; text-align:left; width:70%;'><b>Grand Total:</b></td>");
        summary.append("<td style='padding: 5px; text-align:right; width:30%;'><b>").append(decimalFormat.format(grandTotal)).append(" Ks</b></td>");
        summary.append("</tr>");
        summary.append("</table>");

        summary.append("<div style='border-top: 1px dashed black; width: 100%; margin: 0; padding: 0;'></div>");
        summary.append("<p style='margin: 4px 0;'>").append(voucherInfo.getAfterSaleLabelForVoucher()).append("</p>");
        summary.append("<p style='padding: 4px; margin: 0; font-size: 7px; text-align:right;'>").append(voucherInfo.getDevelopmentCompanyNameForVoucher()).append("</p>");
        summary.append("</body></html>");

        textPane.setText(summary.toString());

        JScrollPane scrollPaneV = new JScrollPane(textPane);
        scrollPaneV.setBorder(BorderFactory.createEmptyBorder());
        scrollPaneV.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneV.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPaneV.getVerticalScrollBar().setUI(new MacOSScrollBarUI(COLOR_GRAY));

        // Ensure the scrollPane starts at the top
        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalScrollBar = scrollPaneV.getVerticalScrollBar();
            verticalScrollBar.setValue(verticalScrollBar.getMinimum());
        });

        JButton printButton = new JButton("Print");
        printButton.setPreferredSize(new Dimension(80, 30)); // Adjust button size
        printButton.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        printButton.setBackground(new Color(240, 240, 240));
        printButton.setFocusPainted(true);
        printButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        printButton.addActionListener(e -> VoucherPrinter.printVoucher(textPane));

        JButton closeButton = new JButton("Close");
        closeButton.setPreferredSize(new Dimension(80, 30)); // Adjust button size
        closeButton.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        closeButton.setBackground(new Color(240, 240, 240));
        closeButton.setFocusPainted(true);
        closeButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        closeButton.addActionListener(e -> popupFrame.dispose());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(COLOR_WHITE);
        bottomPanel.add(printButton);
        bottomPanel.add(closeButton);

        voucherPanel.add(scrollPaneV, BorderLayout.CENTER);
        voucherPanel.add(bottomPanel, BorderLayout.SOUTH);

        contentPanel.add(voucherPanel, BorderLayout.CENTER);

        popupFrame.add(contentPanel);
        popupFrame.setVisible(true);
    }

    protected JPanel createOrderProceedPanel() {
        orderButtonBottomLayerPanel = new JPanel();
        orderButtonBottomLayerPanel.setLayout(new GridLayout(1, 2, 6, 2));
        orderButtonBottomLayerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        ImageIcon printIcon = createResizedIcon("/PrintIcon.svg", 14, null);
        orderListPrintButton = new IconTextButton("Print", printIcon, 14, (COLOR_BLUE), 0);
        orderListPrintButton.setBackground(COLOR_BLUE);
        orderListPrintButton.setForeground(COLOR_WHITE);
        orderListPrintButton.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12));
        orderListPrintButton.setPreferredSize(new Dimension(130, 38)); // Adjusted size for visibility

        // Set icon on the left side of the button text
        orderListPrintButton.setIcon(printIcon);
        // Set the text and icon alignment
        orderListPrintButton.setHorizontalTextPosition(SwingConstants.RIGHT); // Icon on the left
        orderListPrintButton.setVerticalTextPosition(SwingConstants.CENTER);
        orderListPrintButton.setHorizontalAlignment(SwingConstants.CENTER);
        orderListPrintButton.setVerticalAlignment(SwingConstants.CENTER);

        ImageIcon orderIcon = createResizedIcon("/OrderIcon.svg", 14, null);
        JButton orderChartButton = getOrderButton(orderIcon);


//        ImageIcon proceedIcon = createResizedIcon("/ProceedIcon.svg", 14, null);
//        JButton proceedButton = getjButton(orderIcon, proceedIcon);

        orderListPrintButton.addActionListener(e -> showVoucherPopup());

        orderChartButton.addActionListener(e -> {
            if (!BatchUserDrawerChecking.isOpened()) {
                JOptionPane.showMessageDialog(mainAllHoldingPanel, "Your drawer was closed.");
                return;
            }

            // Disable the orderChartButton to prevent multiple clicks
            orderChartButton.setEnabled(false);

            // Perform the button event action
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    orderChartButtonEvent();
                    return null;
                }

                @Override
                protected void done() {
                    SwingUtilities.invokeLater(() -> {
                        // Re-enable the button after the process is complete
                        orderChartButton.setEnabled(true);
                    });
                }
            }.execute();
        });


//        proceedButton.addActionListener(e -> showVoucher());

        orderButtonBottomLayerPanel.add(orderListPrintButton);
        orderButtonBottomLayerPanel.add(orderChartButton);
//        orderButtonBottomLayerPanel.add(proceedButton);

        return orderButtonBottomLayerPanel;
    }


    private void orderChartButtonEvent() {
        String lastOrderCode = CreateTablePanel.getLastClickedOrderCode();
        if (!orderCodeLabel.getText().isEmpty()) {
            createAdditionalOrderEvent(lastOrderCode);
        } else {
            createNewOrderEvent();
        }
    }


    private void createNewOrderEvent() {
        String dineInToGoDeliveryStatus = generateDineInToGoDeliveryStatus();
        if (dineInToGoDeliveryStatus.equals("Dine In")) {
            MiniDevPOS.showPanel("SelectTables");
        } else if (dineInToGoDeliveryStatus.equals("Delivery") || dineInToGoDeliveryStatus.equals("To Go")) {
            SwingUtilities.invokeLater(() -> {
                createOrderToDatabase(null);
                updateTableNoAndOrderCode();
                mainCard.show(mainAllHoldingPanel, "PaymentPanel");

                // Start loading the payment details in the background
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        // Perform the time-consuming data fetching here
                        miniPaymentPanel.displayOrderDetailsForPaymentProcess(CURRENT_PROCESSING_ORDER_CODE, null);
                        return null;
                    }

                    @Override
                    protected void done() {
                        miniPaymentPanel.startAnimationDefaultPayment();
                    }
                };
                worker.execute();
            });
        } else {
            SwingUtilities.invokeLater(() -> {
                createOrderToDatabase(null);
                TableLists.showOrderCompletedDialog();
                TableLists.updateUIComponents();
            });
        }
    }

    private void createAdditionalOrderEvent(String lastClickedOrderCode) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                createAdditionalOrderIntoDB(lastClickedOrderCode); // Move DB operation to background
                return null;
            }

            @Override
            protected void done() {
                deleteOrderListsPanel(); // Ensure efficient cleanup
                TableLists.showOrderCompletedDialog();
                new CreateHomePanel().updateDisplayProductDataInBackground();
                MiniDevPOS.resetHomeNavigationAnimation();
            }
        };
        worker.execute();
    }


//    private static JButton getjButton(ImageIcon orderIcon, ImageIcon proceedIcon) {
//        JButton proceedButton = new IconTextButton("Proceed", orderIcon, 14, (COLOR_GREEN), 0);
//        proceedButton.setBackground(COLOR_GREEN);
//        proceedButton.setForeground(COLOR_WHITE);
//        proceedButton.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));
//        proceedButton.setPreferredSize(new Dimension(130, 38)); // Adjusted size for visibility
//
//        // Set icon on the left side of the button text
//        proceedButton.setIcon(proceedIcon);
//        // Set the text and icon alignment
//        proceedButton.setHorizontalTextPosition(SwingConstants.RIGHT); // Icon on the left
//        proceedButton.setVerticalTextPosition(SwingConstants.CENTER);
//        proceedButton.setHorizontalAlignment(SwingConstants.CENTER);
//        proceedButton.setVerticalAlignment(SwingConstants.CENTER);
//        return proceedButton;
//    }

    private static JButton getOrderButton(ImageIcon orderIcon) {
        JButton orderChartButton = new IconTextButton("Order", orderIcon, 14, (COLOR_ORANGE), 0);
        orderChartButton.setBackground(COLOR_ORANGE);
        orderChartButton.setForeground(COLOR_WHITE);
        orderChartButton.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12));
        orderChartButton.setPreferredSize(new Dimension(130, 38)); // Adjusted size for visibility

        // Set icon on the left side of the button text
        orderChartButton.setIcon(orderIcon);
        // Set the text and icon alignment
        orderChartButton.setHorizontalTextPosition(SwingConstants.RIGHT); // Icon on the left
        orderChartButton.setVerticalTextPosition(SwingConstants.CENTER);
        orderChartButton.setHorizontalAlignment(SwingConstants.CENTER);
        orderChartButton.setVerticalAlignment(SwingConstants.CENTER);
        return orderChartButton;
    }


    protected static JPanel createCenterLayerPanel() {
        orderButtonCenterLayerPanel = new JPanel(new GridLayout(1, 2, 2, 2));

        // Creating left label panel with GridBagLayout for precise alignment control
        JPanel leftLabelPanel = new JPanel(new GridBagLayout());
        leftLabelPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        JLabel payableAmountLabel = new JLabel("Payable Amount");
        payableAmountLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 17));
        payableAmountLabel.setVerticalTextPosition(SwingConstants.CENTER);

        // Setting GridBagConstraints for left-center alignment
        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.anchor = GridBagConstraints.WEST; // Align left
        leftGbc.fill = GridBagConstraints.VERTICAL; // Allow vertical centering
        leftGbc.weightx = 1; // Take all horizontal space
        leftLabelPanel.add(payableAmountLabel, leftGbc);

        // Creating right label panel with GridBagLayout for precise alignment control
        JPanel rightSubtotalLabelPanel = new JPanel(new GridBagLayout());
        rightSubtotalLabelPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        payableAmountNumber.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 17));
        payableAmountNumber.setVerticalTextPosition(SwingConstants.CENTER);

        // Setting GridBagConstraints for right-center alignment
        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.anchor = GridBagConstraints.EAST; // Align right
        rightGbc.fill = GridBagConstraints.VERTICAL; // Allow vertical centering
        rightGbc.weightx = 1; // Take all horizontal space
        rightGbc.gridx = 1; // Place the label in the second column
        rightGbc.insets = new Insets(0, 0, 0, 10); // Add some padding to the right

        rightSubtotalLabelPanel.add(payableAmountNumber, rightGbc);

        orderButtonCenterLayerPanel.add(leftLabelPanel);
        orderButtonCenterLayerPanel.add(rightSubtotalLabelPanel);

        return orderButtonCenterLayerPanel;
    }

    protected static JPanel createTopLayerPanel() {
        orderButtonTopLayerPanel = new JPanel();
        orderButtonTopLayerPanel.setLayout(new GridLayout(2, 1, 0, 0));
        Border topLayerTopBorder = BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_LINE_COLOR);
        orderButtonTopLayerPanel.setBorder(topLayerTopBorder);

        subtotalOfOrderListsPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        subtotalOfOrderListsPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        taxOfOrderListsPanel = new JPanel();

        // Creating left label panel with GridBagLayout for precise alignment control
        JPanel leftLabelPanel = new JPanel(new GridBagLayout());

        JLabel subtotalLabel = new JLabel("SubTotal");
        subtotalLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 15));
        subtotalLabel.setVerticalTextPosition(SwingConstants.CENTER);

        // Setting GridBagConstraints for left-center alignment
        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.anchor = GridBagConstraints.WEST; // Align left
        leftGbc.fill = GridBagConstraints.VERTICAL; // Allow vertical centering
        leftGbc.weightx = 1; // Take all horizontal space
        subtotalOfOrderListsPanel.add(subtotalLabel, leftGbc);

        // Creating left label panel with GridBagLayout for precise alignment control
        JPanel rightNumberPanel = new JPanel(new GridBagLayout());

        subTotalNumber.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 15));
        subTotalNumber.setVerticalTextPosition(SwingConstants.CENTER);

        // Setting GridBagConstraints for right-center alignment
        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.anchor = GridBagConstraints.EAST; // Align right
        rightGbc.fill = GridBagConstraints.VERTICAL; // Allow vertical centering
        rightGbc.weightx = 1; // Take all horizontal space
        rightGbc.gridx = 1; // Place the label in the second column
        rightGbc.insets = new Insets(0, 0, 0, 10); // Add some padding to the right

        rightNumberPanel.add(subTotalNumber, rightGbc);

        subtotalOfOrderListsPanel.add(leftLabelPanel);
        subtotalOfOrderListsPanel.add(rightNumberPanel);

        orderButtonTopLayerPanel.add(subtotalOfOrderListsPanel);

        subtotalOfOrderListsPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        subtotalOfOrderListsPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        taxOfOrderListsPanel = new JPanel(new GridLayout(1, 2, 0, 0));

        JPanel leftTaxLabelPanel = new JPanel(new GridBagLayout());
        leftTaxLabelPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        JLabel leftTaxLabel = new JLabel("Tax");
        leftTaxLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 15));
        leftTaxLabel.setVerticalTextPosition(SwingConstants.CENTER);

        leftTaxLabelPanel.add(leftTaxLabel, leftGbc);


        JPanel rightTaxLabelPanel = new JPanel(new GridBagLayout());
        rightTaxLabelPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        taxTotalNumber = new JLabel("0 Ks"); //-------------------------Government Tax Amount
        taxTotalNumber.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 15));
        taxTotalNumber.setVerticalTextPosition(SwingConstants.CENTER);

        rightTaxLabelPanel.add(taxTotalNumber, rightGbc);

        taxOfOrderListsPanel.add(leftTaxLabelPanel);
        taxOfOrderListsPanel.add(rightTaxLabelPanel);

        orderButtonTopLayerPanel.add(taxOfOrderListsPanel);

        return orderButtonTopLayerPanel;
    }

    static CardLayout cardLayoutDynamics = new CardLayout();


    protected static JPanel createHomeRightTopPanel() {
        JPanel homeRightTopPanel = new JPanel(new BorderLayout()); // Set horizontal and vertical gaps
        homeRightTopPanel.setPreferredSize(new Dimension(300, 60));
        homeRightTopPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        homeRightTopPanel.setOpaque(false);

        JPanel homeTopRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); // Set horizontal and vertical gaps
        homeTopRight.setOpaque(false);
        homeTopRight.setPreferredSize(new Dimension(155, 60));
        homeTopRight.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        ImageIcon plusButtonIcon = new SvgIcon("/AddIcon.svg", 22).getImageIcon();
        RoundedIconButton plusButton = new RoundedIconButton(plusButtonIcon, 14, COLOR_GRAY, 0);
        plusButton.setPreferredSize(new Dimension(38, 38));
        plusButton.setBackground(COLOR_GRAY);

        plusButton.addActionListener(e -> MiniDevPOS.showPanel("Customers"));

        RoundedIconButton scannerButton = getRoundedIconButton();

        // Key listener for capturing scanned barcodes
        mainAllHoldingPanel.addKeyListener(new KeyAdapter() {
            private StringBuilder scannedCodeBuffer = new StringBuilder();
            private static final int BARCODE_LENGTH = 8; // Adjust based on your barcode length

            @Override
            public void keyPressed(KeyEvent e) {
                char c = e.getKeyChar();
                if (Character.isDigit(c) || Character.isLetter(c)) { // Validate characters
                    scannedCodeBuffer.append(c);

                    // Process the barcode once it reaches the expected length
                    if (scannedCodeBuffer.length() >= BARCODE_LENGTH) {
                        String scannedCode = scannedCodeBuffer.toString();
                        Product matchedProduct = findProductBySerialCode(scannedCode);

                        if (matchedProduct != null) {
                            makeToConfirmSelectTable(matchedProduct);
                        } else {
                            DialogCollection.showCustomDialog(mainAllHoldingPanel, "No product found for the scanned code: " + scannedCode, "Product Not Found");
                        }

                        // Clear the buffer for the next scan
                        scannedCodeBuffer.setLength(0);
                    }
                }
            }
        });

        ImageIcon reloadButtonIcon = new SvgIcon("/ReloadIcon.svg", 22).getImageIcon();
        RoundedIconButton reloadButton = new RoundedIconButton(reloadButtonIcon, 14, COLOR_GRAY, 0);
        reloadButton.setPreferredSize(new Dimension(38, 38));
        reloadButton.setBackground(COLOR_GRAY);

        reloadButton.addActionListener(e -> deleteOrderListsPanel());

        homeTopRight.add(plusButton);
        homeTopRight.add(scannerButton);
        homeTopRight.add(reloadButton);

        JPanel homeTopLeft = new JPanel(new BorderLayout());
        homeTopLeft.setBackground(COLOR_WHITE);
        homeTopLeft.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 0));
        homeTopLeft.setPreferredSize(new Dimension(135, 60));

        homeTopLeft.add(createHoldingDineInToGoDeliCategory(), BorderLayout.WEST);

        JPanel homeMiddleTop = new JPanel(new BorderLayout());
        homeMiddleTop.setOpaque(false);
        homeMiddleTop.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 5));

        RoundedPanel blankPanel = new RoundedPanel(20);
        blankPanel.setOpaque(true);
        blankPanel.setBackground(COLOR_WHITE);

        // Initialize CardLayout container

        stackedPanel = new RoundedPanel(20);
        stackedPanel.setOpaque(false);
        stackedPanel.setLayout(cardLayoutDynamics);
        stackedPanel.add(createHoldingCustomerInfo(initCustomerNameText, initCustomerCID), "CustomerInfo");
        stackedPanel.add(createOrderCodeInfoOnOrderDisplay(initOrderCode), "OrderCodeInfo");
        stackedPanel.add(blankPanel, "BlankPanel");  // Adding a blank panel

        cardLayoutDynamics.show(stackedPanel, "BlankPanel");

        homeMiddleTop.add(stackedPanel, BorderLayout.CENTER);

        homeRightTopPanel.add(homeTopLeft, BorderLayout.WEST);
        homeRightTopPanel.add(homeMiddleTop, BorderLayout.CENTER);
        homeRightTopPanel.add(homeTopRight, BorderLayout.EAST);

        return homeRightTopPanel;
    }


    private static RoundedPanel stackedPanel; // Container with CardLayout
    protected static RoundedPanel panelForCustomerSelect;
    protected static String initCustomerNameText = "";
    protected static String initCustomerCID = "";
    protected static JLabel customerLabel; //Customer name that imported into database
    protected static JLabel customerCIDLabel;

    protected static RoundedPanel panelForOrderSelect;
    protected static JLabel orderCodeLabel;
    protected static String initOrderCode = "";


    // Methods to toggle between views
    public static void showCustomerInfoPanel() {
        CardLayout layout = (CardLayout) stackedPanel.getLayout();
        layout.show(stackedPanel, "CustomerInfo");
    }

    public static void showOrderCodeInfoPanel() {
        CardLayout layout = (CardLayout) stackedPanel.getLayout();
        layout.show(stackedPanel, "OrderCodeInfo");
    }

    public static void showNothing() {
        CardLayout layout = (CardLayout) stackedPanel.getLayout();
        layout.show(stackedPanel, "BlankPanel");
    }


    public static void setCustomerInfoStringVisibility(String lastClickedCustomerNameUpdated, String lastClickedCustomerCID) {
        customerLabel.setText(lastClickedCustomerNameUpdated);
        customerCIDLabel.setText(lastClickedCustomerCID);
    }

    public static void setOrderCodeStringVisibility(String lastClickedOrderCodeUpdated) {
        orderCodeLabel.setText(lastClickedOrderCodeUpdated);
    }

    public static void showCustomerInfo(String lastClickedCustomerNameUpdated, String lastClickedCustomerCID) {
        setCustomerInfoStringVisibility(lastClickedCustomerNameUpdated, lastClickedCustomerCID);
        showCustomerInfoPanel();
    }

    public static void showOrderCode(String lastClickedOrderCodeUpdated) {
        setOrderCodeStringVisibility(lastClickedOrderCodeUpdated);
        showOrderCodeInfoPanel();
    }

    public static void clearCustomerInfoVisibility() {
        showNothing();
        setCustomerInfoStringVisibility(initCustomerNameText, initCustomerCID);
    }

    public static void clearOrderCodeVisibilty() {
        showNothing();
        setOrderCodeStringVisibility(initOrderCode);
    }

    public static void allClearCustomerInfoOrderCodeVisibility() {
        showNothing();
        setCustomerInfoStringVisibility(initCustomerNameText, initCustomerCID);
        setOrderCodeStringVisibility(initOrderCode);
    }

    private static RoundedPanel createOrderCodeInfoOnOrderDisplay(String lastClickedOrderCode) {
        panelForOrderSelect = new RoundedPanel(20);
        panelForOrderSelect.setLayout(new GridBagLayout()); // Use GridBagLayout to center components
        panelForOrderSelect.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        panelForOrderSelect.setOpaque(false);
        panelForOrderSelect.setBackground(COLOR_PANEL_GRAY);

        orderCodeLabel = new JLabel(lastClickedOrderCode);
        orderCodeLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 13));

        // Center the label in the panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        panelForOrderSelect.add(orderCodeLabel, gbc);


        return panelForOrderSelect;
    }


    private static RoundedPanel createHoldingCustomerInfo(String lastClickedCustomerName, String cid) {
        panelForCustomerSelect = new RoundedPanel(20);
        panelForCustomerSelect.setLayout(new BorderLayout());
        panelForCustomerSelect.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        panelForCustomerSelect.setOpaque(false);
        panelForCustomerSelect.setBackground(COLOR_PANEL_GRAY);
        // Customer Panel
        JPanel customerPanel = new JPanel(new BorderLayout());
        customerPanel.setOpaque(false);

        customerCIDLabel = new JLabel(cid);

        customerLabel = new JLabel(lastClickedCustomerName);
        customerLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 13));
        customerPanel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
        customerPanel.add(customerLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(2, 1, 3, 3));
        buttonPanel.setPreferredSize(new Dimension(40, 38));
        buttonPanel.add(getDeleteButtonCustomerInfo(), BorderLayout.CENTER);
        // Add components to the main panel
        panelForCustomerSelect.add(customerPanel, BorderLayout.CENTER);
        panelForCustomerSelect.add(buttonPanel, BorderLayout.EAST);


        return panelForCustomerSelect;
    }


    private static JButton getDeleteButtonCustomerInfo() {
        JButton deleteButton = new JButton(createResizedIcon("/delete.svg", 22, COLOR_WHITE));
        configureButtonStyle(deleteButton, backgroundColor);

        deleteButton.addActionListener(e -> allClearCustomerInfoOrderCodeVisibility());

        return deleteButton;
    }

    private static RoundedIconButton getRoundedIconButton() {
        ImageIcon scannerButtonIcon = new SvgIcon("/BarcodeScannerIcon.svg", 22).getImageIcon();
        RoundedIconButton scannerButton = new RoundedIconButton(scannerButtonIcon, 14, COLOR_GRAY, 0);
        scannerButton.setPreferredSize(new Dimension(38, 38));
        scannerButton.setBackground(COLOR_GRAY);

        // Add action listener for the scanner button
        scannerButton.addActionListener(e -> {
            // Set focus on the main panel to capture keyboard input for scanning
            mainAllHoldingPanel.requestFocusInWindow();
        });
        return scannerButton;
    }


    // Method to find a product by its serial code
    private static Product findProductBySerialCode(String serialCode) {
        // Assuming you have a method to get all products
        for (Product product : allDisplayProductLists) { // Replace with your actual product list
            if (product.getSerialCode().equals(serialCode)) {
                return product; // Return the matched product
            }
        }
        return null; // No product found
    }


    private static JPanel createHoldingDineInToGoDeliCategory() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 5", // Layout constraints for the panel itself
                "[center][center][center]", // Column constraints: Center-aligned columns
                "[center]" // Row constraints: Center-aligned row
        ));
        panel.setOpaque(true);
        panel.setBackground(COLOR_PANEL_GRAY);
        panel.putClientProperty(FlatClientProperties.STYLE, "arc:20");

        backgroundColor = panel.getBackground(); // Get the default background color

        // Create the buttons for Dine In, To Go, and Delivery
        buttonDineIn = new JButton(createResizedIcon("/DineInIcon.svg", 25, COLOR_WHITE));
        buttonToGo = new JButton(createResizedIcon("/ToGoIcon.svg", 25, COLOR_WHITE));
        buttonDelivery = new JButton(createResizedIcon("/DeliveryIcon.svg", 25, COLOR_WHITE));

        // Configure styles for the buttons
        configureButtonStyle(buttonDineIn, backgroundColor);
        configureButtonStyle(buttonToGo, backgroundColor);
        configureButtonStyle(buttonDelivery, backgroundColor);

        // Add action listeners for buttons
        buttonDineIn.addActionListener(e -> handleButtonClick(buttonDineIn));
        buttonToGo.addActionListener(e -> handleButtonClick(buttonToGo));
        buttonDelivery.addActionListener(e -> handleButtonClick(buttonDelivery));

        // Start with the Dine In button active
        startAnimationFromDineIn();

        // Add buttons to the panel with reduced gaps
        panel.add(buttonToGo, "cell 0 0, align center");
        panel.add(buttonDineIn, "cell 1 0, align center");
        panel.add(buttonDelivery, "cell 2 0, align center");

        return panel;
    }

    // Method to configure button style
    protected static void configureButtonStyle(JButton button, Color backgroundColor) {
        button.putClientProperty(FlatClientProperties.STYLE, "" + "arc:10;" + "background:" + toHexString(backgroundColor) + ";" + "borderWidth:0;" + "focusWidth:0;" + "innerFocusWidth:0;" + "margin:3,5,3,5;");
    }

    private static String currentStatus = "Dine In"; // Default status

    // Method to generate the status based on the last clicked button
    private static String handleConditionDineInToGoDelivery() {
        if (activeButton == buttonDineIn) {
            return "Dine In";
        } else if (activeButton == buttonToGo) {
            return "To Go";
        } else if (activeButton == buttonDelivery) {
            return "Delivery";
        }
        return "Dine In"; // Default status
    }

    private static void handleButtonClick(JButton clickedButton) {
        // If there's an active button, reset its icon and background color
        if (activeButton != null) {
            activeButton.setIcon(createDefaultIcon(activeButton));
            activeButton.setBackground(backgroundColor); // Reset to panel's background color
        }

        // Set the clicked button to active state
        clickedButton.setIcon(createActiveIcon(clickedButton));
        clickedButton.setBackground(backgroundColor);

        // Update the active button reference
        activeButton = clickedButton;

        // Generate and print the current status
        currentStatus = handleConditionDineInToGoDelivery();

    }

    public static String generateDineInToGoDeliveryStatus() {
        return currentStatus;
    }


    // Method to convert Color to hexadecimal string
    protected static String toHexString(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    // Method to create the default icon based on the button
    private static ImageIcon createDefaultIcon(JButton button) {
        if (button == buttonDineIn) {
            return createResizedIcon("/DineInIcon.svg", 25, COLOR_WHITE);
        } else if (button == buttonToGo) {
            return createResizedIcon("/ToGoIcon.svg", 25, COLOR_WHITE);
        } else if (button == buttonDelivery) {
            return createResizedIcon("/DeliveryIcon.svg", 25, COLOR_WHITE);
        }
        return null;
    }

    // Method to create the active icon based on the button
    private static ImageIcon createActiveIcon(JButton button) {
        if (button == buttonDineIn) {
            return createResizedIcon("/DineInIconActive.svg", 25, COLOR_WHITE);
        } else if (button == buttonToGo) {
            return createResizedIcon("/ToGoIconActive.svg", 25, COLOR_WHITE);
        } else if (button == buttonDelivery) {
            return createResizedIcon("/DeliveryIconActive.svg", 25, COLOR_WHITE);
        }
        return null;
    }

    // Start animation for the Dine In button
    protected static void startAnimationFromDineIn() {
        buttonDineIn.setIcon(createActiveIcon(buttonDineIn));
        buttonDineIn.setBackground(backgroundColor);
        activeButton = buttonDineIn; // Set the initial active button
    }


    protected RoundedPanel createTopLeftNavigationButtonPanel() {
        topLeftNavigationButtonPanel = new RoundedPanel(10);
        topLeftNavigationButtonPanel.setBackground(COLOR_WHITE);
        topLeftNavigationButtonPanel.setPreferredSize(new Dimension(550, 40));
        topLeftNavigationButtonPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        topLeftNavigationButtonPanel.setOpaque(false);

        // Use a FlowLayout for alignment and spacing
        topLeftNavigationButtonPanel.setLayout(new BoxLayout(topLeftNavigationButtonPanel, BoxLayout.X_AXIS));

        // Create list to store dynamically created buttons
        categoryButtons = new ArrayList<>();

        // Create default "All" button and add it to the panel
        allButton = createOrGetCachedButton("All", 0);
        categoryButtons.add(allButton);
        topLeftNavigationButtonPanel.add(allButton);


        SwingUtilities.invokeLater(() -> fetchAndCreateButtons(topLeftNavigationButtonPanel));

        return topLeftNavigationButtonPanel;
    }


    protected void refreshNavigateButtons() {
        SwingUtilities.invokeLater(() -> {
            fetchAndCreateButtons(topLeftNavigationButtonPanel); // Re-fetch and recreate the buttons\
            topLeftNavigationButtonPanel.revalidate(); // Revalidate the panel to update the layout
            topLeftNavigationButtonPanel.repaint();    // Repaint the panel to ensure the changes are reflected visually
        });
    }

    List<CounterLists> counterLists = new ArrayList<>();


    private void fetchAndCreateButtons(RoundedPanel panel) {
        // Create buttons dynamically based on query results
        List<RoundedBorderButton> buttons = new ArrayList<>();

        FetchCounterLists fetchCounterLists = new FetchCounterLists();
        counterLists = fetchCounterLists.getCounterArrayLists();
        // Add allButton first
        buttons.add(allButton);

        // Iterate over counterLists to create buttons
        for (CounterLists counter : counterLists) {
            String text = counter.getCounterName(); // Get the counter name
            int counterID = counter.getCounterID(); // Get the counter ID
            RoundedBorderButton button = createOrGetCachedButton(text, counterID); // Create or retrieve from cache
            buttons.add(button);
        }


        // Update UI with buttons in one go
        SwingUtilities.invokeLater(() -> {
            categoryButtons.add(allButton); // Add allButton first to categoryButtons
            panel.add(allButton); // Add allButton first to the panel

            for (JButton button : buttons) {
                if (button != allButton) { // Avoid re-adding allButton
                    categoryButtons.add(button);
                    button.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 11));
                    panel.add(button);
                }
            }
            panel.revalidate(); // Revalidate panel to ensure layout updates
            panel.repaint();    // Repaint panel to ensure visual updates
        });
    }


    private RoundedBorderButton createOrGetCachedButton(String buttonText, int index) {
        RoundedBorderButton cachedButton = buttonCache.get(buttonText);
        if (cachedButton == null) {
            cachedButton = createTopNavigationButton(buttonText, index);
            buttonCache.put(buttonText, cachedButton);
        }
        return cachedButton;
    }

    private RoundedBorderButton createTopNavigationButton(String text, int index) {
        RoundedBorderButton button = new RoundedBorderButton(null, 80, 30, COLOR_WHITE); // Use the provided text as the button's label
        button.setFont(new Font("Noto Sans Myanmar Bold", Font.PLAIN, 11));
        button.setText("<html><center><b>" + text + "</b></center></html>");
        button.setRoundedArcSize(10);
        button.setHorizontalTextPosition(SwingConstants.CENTER); // Center text horizontally
        button.setVerticalTextPosition(SwingConstants.CENTER); // Center text vertically
        button.setMargin(new Insets(7, 4, 7, 4));
        button.setFocusPainted(true);
        button.setContentAreaFilled(false);
        button.setRolloverEnabled(false);
        button.setPreferredSize(new Dimension(80, 30)); // Adjust dimensions as needed
        button.setMaximumSize(new Dimension(80, 30)); // Adjust dimensions as needed
        button.setMinimumSize(new Dimension(80, 30)); // Adjust dimensions as needed
        button.setBorder(BorderFactory.createEmptyBorder());

        button.addActionListener(e -> {
            animateBorderColor(button);
            actionPerformedWhenPressNavigationButtons(index, button);
        });

        return button;
    }

    protected JComponent initCartDisplayPanel() {
        cartDisplayCard = new CardLayout();
        cartPanel = new JPanel(cartDisplayCard);
        cartPanel.setOpaque(false);

        // Initialize the panels
        homeRightCenterOrderPanel = initHomeRightCenterOrderPanel();
        cartDrawingPanel = getNoProductInCartPanel();

        // Wrap homeRightCenterOrderPanel in a JScrollPane
        orderScrollPane = new JScrollPane(homeRightCenterOrderPanel);
        orderScrollPane.setBorder(BorderFactory.createEmptyBorder());
        orderScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        orderScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        orderScrollPane.getVerticalScrollBar().setUI(new MacOSScrollBarUI(COLOR_GRAY));

        // Add the JScrollPane to the cartPanel
        cartPanel.add(orderScrollPane, "ACTIVE");
        cartPanel.add(cartDrawingPanel, "INACTIVE");

        cartDisplayCard.show(cartPanel, "INACTIVE");

        return cartPanel;
    }


    private JPanel initHomeRightCenterOrderPanel() {
        homeRightCenterOrderPanel = new JPanel();
        homeRightCenterOrderPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        homeRightCenterOrderPanel.setLayout(new BoxLayout(homeRightCenterOrderPanel, BoxLayout.Y_AXIS));
        homeRightCenterOrderPanel.setBackground(COLOR_WHITE);
        homeRightCenterOrderPanel.setOpaque(false);

        return homeRightCenterOrderPanel;
    }

    protected JPanel initProductBoxPanelDisplay() {
        // Create product panel
        productBoxCardPanel = new JPanel(productDisplayCard);
        productBoxCardPanel.setOpaque(false);

        FetchProductsToDisplay fetchProductsToDisplay = new FetchProductsToDisplay();
        allDisplayProductLists = fetchProductsToDisplay.fetchProductsFromDatabase();
        makeFilteredListsForRespectiveIndex();

        // Initialize all index card holdings using a loop
        for (int i = 0; i <= 5; i++) {
            JComponent cardHoldingPanel = initCardHolding(i, getProductsForIndex(i));
            productBoxCardPanel.add(cardHoldingPanel, String.valueOf(i));
        }

        productBoxCardPanel.add(indexSearchPanel, "Search");

        return productBoxCardPanel;
    }

    private List<Product> getProductsForIndex(int index) {
        return switch (index) {
            case 0 -> allDisplayProductLists;
            case 1 -> filteredProductOfIndex1;
            case 2 -> filteredProductOfIndex2;
            case 3 -> filteredProductOfIndex3;
            case 4 -> filteredProductOfIndex4;
            case 5 -> filteredProductOfIndex5;
            default -> throw new IllegalArgumentException("Invalid index: " + index);
        };
    }

    protected JComponent initCardHolding(int index, List<Product> products) {
        CardLayout cardLayout = new CardLayout();
        JPanel cardHoldingPanel = new JPanel(cardLayout);

        JComponent productPanel;
        switch (index) {
            case 0:
                productPanel = initDefaultProductDisplay(products);
                break;
            case 1:
                productPanel = initDisplayProductOfIndex1(products);
                break;
            case 2:
                productPanel = initDisplayProductOfIndex2(products);
                break;
            case 3:
                productPanel = initDisplayProductOfIndex3(products);
                break;
            case 4:
                productPanel = initDisplayProductOfIndex4(products);
                break;
            case 5:
                productPanel = initDisplayProductOfIndex5(products);
                break;
            default:
                throw new IllegalArgumentException("Invalid index: " + index);
        }

        JPanel noProductPanel = getNoProductFoundPanel();
        cardHoldingPanel.add(productPanel, "WithProduct");
        cardHoldingPanel.add(noProductPanel, "WithoutProduct");

        initCheckingCardShowList(products, cardLayout, cardHoldingPanel);
        return cardHoldingPanel;
    }

    protected void initCheckingCardShowList(List<Product> products, CardLayout mainCard, JComponent childComponent) {
        mainCard.show(childComponent, products.isEmpty() ? "WithoutProduct" : "WithProduct");
    }

    protected void makeFilteredListsForRespectiveIndex() {
        filteredProductOfIndex1 = allDisplayProductLists.stream().filter(product -> product.getCounterId() == 1).collect(Collectors.toList());

        filteredProductOfIndex2 = allDisplayProductLists.stream().filter(product -> product.getCounterId() == 2).collect(Collectors.toList());

        filteredProductOfIndex3 = allDisplayProductLists.stream().filter(product -> product.getCounterId() == 3).collect(Collectors.toList());

        filteredProductOfIndex4 = allDisplayProductLists.stream().filter(product -> product.getCounterId() == 4).collect(Collectors.toList());

        filteredProductOfIndex5 = allDisplayProductLists.stream().filter(product -> product.getCounterId() == 5).collect(Collectors.toList());
    }

    protected void makeFilteredProductsForSearching(String userInputSearchingText) {
        indexSearchPanel.removeAll();

        // Fetch latest product list
        FetchProductsToDisplay fetchProductsToDisplay = new FetchProductsToDisplay();
        allDisplayProductLists = fetchProductsToDisplay.fetchProductsFromDatabase();

        // Filter products based on user input
        filteredProductOfSearching = allDisplayProductLists.stream().filter(product -> product.getName().toLowerCase().contains(userInputSearchingText.toLowerCase().trim()) || product.getSerialCode().toLowerCase().contains(userInputSearchingText.toLowerCase().trim())).collect(Collectors.toList());

        // Create a new product display panel
        JComponent newSearchPanel = createProductDisplayPanel(filteredProductOfSearching);

        // Replace the existing indexSearchPanel
        productBoxCardPanel.remove(indexSearchPanel); // Remove old panel
        indexSearchPanel = newSearchPanel; // Update the reference
        productBoxCardPanel.add(indexSearchPanel, "Search"); // Add new panel

        // Show the updated search panel
        productDisplayCard.show(productBoxCardPanel, "Search");

        doRefreshTopDynamicNavigation();
        // Refresh layout
        productBoxCardPanel.revalidate();
        productBoxCardPanel.repaint();
    }

    private JPanel getNoProductFoundPanel() {
        noProductFoundInDisplayBoxPanel = new JPanel(new GridBagLayout()); // Center content
        noProductFoundInDisplayBoxPanel.setOpaque(false); // Transparent background

        // Constraints for layout positioning
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE; // Place components one below the other
        gbc.insets = new Insets(10, 0, 10, 0); // Add vertical spacing
        gbc.anchor = GridBagConstraints.CENTER; // Center components

        // Initialize the SVG canvas
        svgCanvasForNoData = new JSVGCanvas();
        svgCanvasForNoData.setOpaque(false);
        svgCanvasForNoData.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        svgCanvasForNoData.setPreferredSize(new Dimension(250, 250)); // Fixed size
        svgCanvasForNoData.setMaximumSize(new Dimension(250, 250));
        svgCanvasForNoData.setMinimumSize(new Dimension(250, 250));
        svgCanvasForNoData.setEnableZoomInteractor(false); // Disable zoom
        svgCanvasForNoData.setEnablePanInteractor(false); // Disable pan

        // Load the default SVG image
        loadDefaultNoProductFoundImage();

        // Create the "No items found" label
        JLabel noItemsFoundLabel = new JLabel("No items found");
        noItemsFoundLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 16));
        noItemsFoundLabel.setForeground(Color.GRAY); // Gray color for subtle appearance
        noItemsFoundLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Add the SVG canvas and label to the panel
        noProductFoundInDisplayBoxPanel.add(svgCanvasForNoData, gbc); // Add canvas
        noProductFoundInDisplayBoxPanel.add(noItemsFoundLabel, gbc);  // Add label below canvas

        return noProductFoundInDisplayBoxPanel;
    }

    private void loadDefaultNoProductFoundImage() {
        try {
            URL defaultImageUrl = getClass().getResource("/NoDataIllustration.svg");
            if (defaultImageUrl != null) {
                svgCanvasForNoData.setURI(defaultImageUrl.toString());
                svgCanvasForNoData.setPreferredSize(new Dimension(250, 250)); // Set preferred size
                svgCanvasForNoData.revalidate(); // Revalidate after setting the URI
                svgCanvasForNoData.repaint(); // Repaint to reflect changes
            } else {
                System.out.println("SVG file not found: /NoDataIllustration.svg");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private JPanel getNoProductInCartPanel() {
        JPanel noSelectedProductPanel = new JPanel(new GridBagLayout());
        noSelectedProductPanel.setOpaque(false); // Transparent background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        // Wrap canvas in a fixed-size panel
        JPanel canvasWrapper = new JPanel();
        canvasWrapper.setPreferredSize(new Dimension(250, 250));
        canvasWrapper.setOpaque(false);
        canvasWrapper.setLayout(new BorderLayout());

        svgCanvasForNoSelected = new JSVGCanvas();
        svgCanvasForNoSelected.setOpaque(false);
        svgCanvasForNoSelected.setBackground(new Color(0, 0, 0, 0));
        svgCanvasForNoSelected.setEnableZoomInteractor(false);
        svgCanvasForNoSelected.setEnablePanInteractor(false);

        canvasWrapper.add(svgCanvasForNoSelected, BorderLayout.CENTER);

        // Preload SVG image in the background
        preloadSVGImage();

        JLabel noSelectedFoundLabel = new JLabel("No items in Cart");
        noSelectedFoundLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 16));
        noSelectedFoundLabel.setForeground(Color.GRAY);
        noSelectedFoundLabel.setHorizontalAlignment(SwingConstants.CENTER);

        noSelectedProductPanel.add(canvasWrapper, gbc);
        noSelectedProductPanel.add(noSelectedFoundLabel, gbc);

        return noSelectedProductPanel;
    }

    private void preloadSVGImage() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    URL defaultImageUrl = getClass().getResource("/CartIllustration.svg");
                    if (defaultImageUrl != null) {
                        svgCanvasForNoSelected.setURI(defaultImageUrl.toString());
                    } else {
                        System.out.println("SVG file not found: /CartIllustration.svg");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }


    private JComponent createProductDisplayPanel(List<Product> products) {
        // Create product panel with MigLayout
        JPanel productDisplayPanel = new JPanel(new MigLayout("wrap 3, insets 10, gap 10",  // 3 columns with spacing
                "[grow, fill]",               // Columns grow and fill available space
                "[top]10[]10[]"               // Rows grow and fill available space, with vertical gaps
        ));
        productDisplayPanel.setBackground(COLOR_GRAY);

        // Assume you know the container size
        Dimension containerSize = new Dimension(530, 650); // Example size, replace with actual size

        // Subtract insets and gaps
        int insetWidth = 10 * 2; // Assuming 10 pixels inset on each side
        int gapWidth = 10 * (3 - 1); // Gaps between 3 columns
        int gapHeight = 10 * (3 - 1); // Gaps between 3 rows

        // Calculate available width and height
        int availableWidth = containerSize.width - insetWidth - gapWidth;
        int availableHeight = containerSize.height - 10 - gapHeight; // Use a fixed height for insets instead of insetHeight

        // Calculate maximum width and height for each button
        int maxWidth = availableWidth / 3; // 3 columns
        int maxHeight = availableHeight / 3; // 3 rows

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            JButton button = createProductButton(product);

            // Logic for positioning buttons based on the number of products
            if (products.size() == 2) {
                // If there are exactly two products, position them in the first row
                productDisplayPanel.add(button, "cell " + i + " 0, width " + maxWidth + "!, height " + maxHeight + "!");
            } else if (products.size() <= 3) {
                // If there are 1 to 3 products, position them in the first row
                productDisplayPanel.add(button, "cell " + i + " 0, width " + maxWidth + "!, height " + maxHeight + "!");
            } else if (products.size() <= 6) {
                // If there are 4 to 6 products, position them in the first two rows
                productDisplayPanel.add(button, "cell " + (i % 3) + " " + (i / 3) + ", width " + maxWidth + "!, height " + maxHeight + "!");
            } else {
                // For more than 6 products, continue to fill normally
                productDisplayPanel.add(button, "cell " + (i % 3) + " " + (i / 3) + ", width " + maxWidth + "!, height " + maxHeight + "!");
            }
        }

        // Fill remaining cells with empty components if needed
        int totalCells = Math.min(products.size(), 9); // Limit to a 3x3 grid
        for (int i = totalCells; i < 9; i++) {
            productDisplayPanel.add(new JPanel(), "cell " + (i % 3) + " " + (i / 3) + ", width " + maxWidth + "!, height " + maxHeight + "!");
        }

        // Ensure all components are added starting from the top left corner
        productDisplayPanel.revalidate(); // Refresh layout
        productDisplayPanel.repaint(); // Repaint panel to show changes

        // Create the scroll pane
        JScrollPane scrollPane = new JScrollPane(productDisplayPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUI(new MacOSScrollBarUI(COLOR_GRAY));
        return scrollPane;

    }

    public void updateDisplayProductDataInBackground() {
        // Use SwingWorker to fetch data and update UI in the background
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                // Fetch the latest product list from the database in the background
                FetchProductsToDisplay fetchProductsToDisplay = new FetchProductsToDisplay();
                allDisplayProductLists = fetchProductsToDisplay.fetchProductsFromDatabase();

                // Update filtered lists based on the latest product data
                makeFilteredListsForRespectiveIndex();

                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // Handle any exceptions that occurred during background execution

                    // Initialize panels with the fetched data using the new method
                    for (int i = 0; i <= 5; i++) {
                        JComponent cardHoldingPanel = initCardHolding(i, getProductsForIndex(i));
                        productBoxCardPanel.add(cardHoldingPanel, String.valueOf(i));
                    }

                    // Add search panel
                    productBoxCardPanel.add(indexSearchPanel, "Search");

                    // Show the first panel or modify this to show a specific index
                    productDisplayCard.show(productBoxCardPanel, "0");

                    // Refresh navigation buttons and top dynamic navigation
                    doRefreshTopDynamicNavigation();
                    refreshNavigateButtons();

                    // Refresh layout to ensure new components are displayed
                    productBoxCardPanel.revalidate();
                    productBoxCardPanel.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle errors gracefully, such as showing a message dialog
                    JOptionPane.showMessageDialog(null, "Error updating product data.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }


    void doRefreshTopDynamicNavigation() {
        // Reset and animate button borders
        if (lastClickedButton != null) {
            resetButtonBorder(lastClickedButton);
        }
        if (allButton != null) {
            animateBorderColor(allButton);
        }
    }


    private JComponent initDefaultProductDisplay(List<Product> allProducts) {
        return createProductDisplayPanel(allProducts);
    }

    private JComponent initDisplayProductOfIndex1(List<Product> filteredIndex1Products) {
        return createProductDisplayPanel(filteredIndex1Products);
    }

    private JComponent initDisplayProductOfIndex2(List<Product> filteredIndex2Products) {
        return createProductDisplayPanel(filteredIndex2Products);
    }

    private JComponent initDisplayProductOfIndex3(List<Product> filteredIndex3Products) {
        return createProductDisplayPanel(filteredIndex3Products);
    }

    private JComponent initDisplayProductOfIndex4(List<Product> filteredIndex4Products) {
        return createProductDisplayPanel(filteredIndex4Products);
    }

    private JComponent initDisplayProductOfIndex5(List<Product> filteredIndex5Products) {
        return createProductDisplayPanel(filteredIndex5Products);
    }


    private void actionPerformedWhenPressNavigationButtons(int buttonIndex, RoundedBorderButton button) {
        // Set border animation and update lastClickedButton
        if (lastClickedButton != null && lastClickedButton != allButton) {
            resetButtonBorder(lastClickedButton);
        }
        resetButtonBorder(allButton);
        animateBorderColor(button);
        lastClickedButton = button;

        productDisplayCard.show(productBoxCardPanel, String.valueOf(buttonIndex)); // Show the panel corresponding to the index
    }


    public static void animateBorderColor(RoundedBorderButton button) {
        button.setBorderColor(COLOR_ORANGE);
        button.setOverrideBackgroundColor(Color.decode("#FFF5EE"));
        button.setForeground(COLOR_ORANGE);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
    }

    protected static void resetButtonBorder(RoundedBorderButton button) {
        button.setBorderColor(COLOR_WHITE);
        button.setForeground(COLOR_BLACK);
        button.setOverrideBackgroundColor(COLOR_WHITE);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
    }


    public static final DecimalFormat priceFormat = new DecimalFormat("#,###");

    public static RoundButton createProductButton(Product product) {
        RoundButton button = new RoundButton(""); // Use custom RoundButton instead of JButton
        button.setBackground(COLOR_WHITE);
        button.setOpaque(false);
        button.setBorderPainted(false); // Remove border

        // Font for the labels
        Font labelFont = new Font("Noto Sans Myanmar", Font.PLAIN, 14); // Adjust size and style as needed

        // Photo label with centered alignment
        ImageIcon icon = getProductPhotoIcon(product.getPhoto()); // Adjust dimensions as needed

        if (product.getPhoto() == null) {
            icon = getProductPhotoIcon(DefaultProductPhoto.getDefaultPhotoBytes());
        }

        JLabel photoLabel = new JLabel(icon);
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoLabel.setVerticalAlignment(SwingConstants.CENTER);
        photoLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); // Adjust padding (top, right, bottom, left)

        // Label for the product name with centered text and consistent styling
        JLabel nameLabel = new JLabel("<html><center><font size='3'>" + wrapText(product.getName(), 7) + "</font><br><font size='3'><b>" + priceFormat.format(product.getPrice()) + "</b></font></center></html>");
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setVerticalAlignment(SwingConstants.CENTER);
        nameLabel.setForeground(Color.BLACK);
        nameLabel.setFont(labelFont);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // Adjust padding (top, right, bottom, left)


        // Panel for stock information
        JPanel stockPanel = getPanel(product, labelFont);

        // Set layout and add components to the button
        button.setLayout(new BorderLayout());
        button.add(photoLabel, BorderLayout.NORTH);
        button.add(photoLabel, BorderLayout.NORTH);
        button.add(nameLabel, BorderLayout.CENTER);
        button.add(stockPanel, BorderLayout.SOUTH); // Add stockPanel to the SOUTH position

        // Tooltip text with more details
        button.setToolTipText(String.format("<html><b>%s</b><br>Price: %s<br>Description: %s</html>", product.getName(), priceFormat.format(product.getPrice()), product.getDescription()));

        // Action listener for button click event
        button.addActionListener(e -> {
            // Handle button click event
            makeToConfirmSelectTable(product);

        });
        return button;
    }

    private static JPanel getPanel(Product product, Font labelFont) {
        JPanel stockPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        stockPanel.setOpaque(false); // Ensure it's transparent
        stockPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // Adjust margins as needed

        // Example stock information label
        JLabel stockLabel = new JLabel("Stock: " + product.getStockAvailableNumber()); // Assuming getStock() returns stock information
        stockLabel.setFont(labelFont);
        stockLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 11));
        stockLabel.setForeground(Color.GRAY);

        // Add stockLabel to stockPanel
        stockPanel.add(stockLabel);
        return stockPanel;
    }

    protected static void makeToConfirmSelectTable(Product product) {
        // Proceed with creating order lists and printing grand total
        cartDisplayCard.show(cartPanel, "ACTIVE");
        processOrder(product);
    }


    // Method to process order
    private static void processOrder(Product product) {
        // Create order lists
        createOrderLists(product);
        // Print the grand total
        printGrandTotalTest();
    }


    protected static void createOrderLists(Product product) {
        boolean productFound = false;
        // Check if the product is already in the order list
        for (RoundedPanel orderPanel : orderListsPanelList) {
            JLabel nameLabel = (JLabel) orderPanel.getComponent(0);
            if (nameLabel.getText().equals(product.getName())) {
                // Product already exists, increase quantity and update UI
                JPanel buttonsPanel = findButtonsPanel(orderPanel);
                if (buttonsPanel == null) {
                    System.err.println("Buttons panel not found in orderPanel.");
                    return;
                }

                JLabel quantityLabel = findComponentByType(buttonsPanel, JLabel.class, "quantityLabel");
                if (quantityLabel == null) {
                    System.err.println("Quantity label not found in buttonsPanel.");
                    return;
                }

                int quantity = Integer.parseInt(quantityLabel.getText().trim());

                // Check if quantity exceeds available stock
                if (quantity >= product.getStockAvailableNumber()) {
                    DialogCollection.showCustomDialog(mainAllHoldingPanel, "Cannot add more of " + product.getName() + " to the order. Only " + product.getStockAvailableNumber() + " available.", "Quantity Limit Exceeded");
                    return; // Exit method if exceeded
                }
                quantity++; // Increment quantity
                quantityLabel.setText("" + quantity);

                // Update subtotal label with new quantity
                updateSubtotalLabel(orderPanel, product.getPrice(), quantity);
                calculateTaxAmountDisplay(displaySubtotal);
                displayPayableAmount(displaySubtotal);

                productFound = true;
                break; // Exit loop as product was found and updated
            }
        }

        if (!productFound) {
            // If product does not exist in the list, create a new order panel
            // Check if the empty illustration panel is currently being displayed

            RoundedPanel newOrderPanel = createOrderPanel(product);
            orderListsPanelList.add(newOrderPanel);
            homeRightCenterOrderPanel.add(newOrderPanel);
        }
        // Update UI
        homeRightCenterOrderPanel.revalidate();
        homeRightCenterOrderPanel.repaint();
    }


    protected static boolean alternateBackground = false; // Flag to alternate background colors

    private static RoundedPanel createOrderPanel(Product product) {
        // Create order panel
        RoundedPanel orderPanel = new RoundedPanel(20);
        orderPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcForOrderPanel = new GridBagConstraints();
        orderPanel.setBackground(alternateBackground ? COLOR_WHITE : COLOR_GRAY); // Alternate background color
        orderPanel.setPreferredSize(new Dimension(130, 75));
        orderPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 75));
        orderPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_LINE_COLOR));

        // Toggle background for the next panel creation
        alternateBackground = !alternateBackground;

        // Create item name label
        JLabel nameLabel = getProductNameLabel(product);
        addNoteMouseListener(nameLabel);
        // Configure nameLabel (40% of width)
        gbcForOrderPanel.gridx = 0; // Column index
        gbcForOrderPanel.gridy = 0; // Row index
        gbcForOrderPanel.weightx = 0.32; // 40% of width
        gbcForOrderPanel.weighty = 1.0; // Fill the height
        gbcForOrderPanel.fill = GridBagConstraints.BOTH; // Fill both horizontally and vertically
        orderPanel.add(nameLabel, gbcForOrderPanel);

        // Create buttons panel as a RoundedPanel with GridBagLayout
        RoundedPanel buttonsPanel = new RoundedPanel(20);
        buttonsPanel.setLayout(new GridBagLayout());
        buttonsPanel.setBackground(alternateBackground ? COLOR_GRAY : COLOR_WHITE); // Alternate background color
        buttonsPanel.setName("buttonsPanel"); // Ensure the name is set

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5); // Uniform horizontal padding
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0; // Do not expand in the x-direction

        // Create unit price label
        JLabel unitPriceLabel = new JLabel(priceFormat.format(product.getPrice()) + " Ks");
        unitPriceLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 14));
        unitPriceLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Create and configure buttons
        ImageIcon removeItemIcon = new SvgIcon("/RemoveItemIcon.svg", 24).getImageIcon();
        JButton minusButton = createIconButton(removeItemIcon, e -> updateQuantity(orderPanel, product, -1));

        ImageIcon addItemIcon = new SvgIcon("/AddItemIcon.svg", 24).getImageIcon();
        JButton addItemButton = createIconButton(addItemIcon, e -> updateQuantity(orderPanel, product, 1));

        // Create quantity label
        JLabel quantityLabel = new JLabel("1");
        quantityLabel.setName("quantityLabel");
        quantityLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 14));
        quantityLabel.setForeground(COLOR_BLACK);
        quantityLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center alignment
        quantityLabel.setPreferredSize(new Dimension(30, 25)); // Fixed size for quantity label

        // Create subtotal label
        JLabel subtotalLabel = new JLabel(priceFormat.format(product.getPrice()) + " Ks");
        subtotalLabel.setName("subtotalLabel");
        subtotalLabel.setForeground(COLOR_BLACK);
        subtotalLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 15));
        subtotalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        subtotalLabel.setPreferredSize(new Dimension(130, 25)); // Fixed width for alignment
        subtotalLabel.setMaximumSize(new Dimension(130, 25));   // Ensuring fixed size

        ImageIcon deleteButtonIcon = new SvgIcon("/DeleteIcon.svg", 18).getImageIcon();
        JButton deleteButton = createIconButton(deleteButtonIcon, e -> deleteOrderPanel(orderPanel));

        // Set fixed sizes for buttons
        Dimension buttonSize = new Dimension(removeItemIcon.getIconWidth(), removeItemIcon.getIconHeight());
        minusButton.setPreferredSize(buttonSize);
        minusButton.setMinimumSize(buttonSize);
        minusButton.setMaximumSize(buttonSize);

        addItemButton.setPreferredSize(buttonSize);
        addItemButton.setMinimumSize(buttonSize);
        addItemButton.setMaximumSize(buttonSize);

        // Configure GridBagConstraints for buttonsPanel
        gbc.gridx = 0;
        gbc.weightx = 0.0; // Fixed width
        buttonsPanel.add(minusButton, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.0; // Fixed width
        buttonsPanel.add(quantityLabel, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0; // Fixed width
        buttonsPanel.add(addItemButton, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1.0; // Expand to fill remaining space
        buttonsPanel.add(Box.createHorizontalGlue(), gbc);

        gbc.gridx = 4;
        gbc.weightx = 0.0; // Fixed width
        buttonsPanel.add(subtotalLabel, gbc);

        gbc.gridx = 5;
        gbc.weightx = 0.0; // Fixed width
        buttonsPanel.add(deleteButton, gbc);

        // Configure buttonsPanel (60% of width)
        gbcForOrderPanel.gridx = 1; // Column index
        gbcForOrderPanel.weightx = 0.68; // 60% of width
        orderPanel.add(buttonsPanel, gbcForOrderPanel);

        return orderPanel;
    }

    private static JLabel getProductNameLabel(Product product) {
        String itemName = product.getName();
        int maxLength = 40; // Further increased length for more space
        String displayName = itemName.length() > maxLength ? itemName.substring(0, maxLength) + "..." : itemName;
        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setToolTipText(itemName);
        nameLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 14));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0)); // Margin on the left
        nameLabel.setForeground(COLOR_BLACK);
        nameLabel.setPreferredSize(new Dimension(300, 45)); // Increased size for more space
        nameLabel.setMaximumSize(new Dimension(300, 45));
        return nameLabel;
    }

    // Helper method to create an icon button with press-and-hold support
    protected static JButton createIconButton(ImageIcon icon, ActionListener actionListener) {
        JButton button = getItemStockDynamicButton(icon);

        // Add press-and-hold functionality
        Timer pressAndHoldTimer = new Timer(150, actionListener);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Trigger the action immediately when the button is pressed
                actionListener.actionPerformed(null);
                // Start the timer for press-and-hold
                pressAndHoldTimer.start();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Stop the timer when the button is released
                pressAndHoldTimer.stop();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // If the mouse exits the button, stop the timer to prevent unintended behavior
                pressAndHoldTimer.stop();
            }
        });

        return button;
    }


    private static JButton getItemStockDynamicButton(ImageIcon icon) {
        JButton button = new JButton();
        button.setIcon(icon);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
        button.setMinimumSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
        button.setMaximumSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
        button.setSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
        return button;
    }


    protected static void addNoteMouseListener(JLabel nameLabel) {
        nameLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Show note popup when panel is clicked
                showNotePopup(nameLabel.getText());
            }
        });
    }

    protected static void showNotePopup(String productName) {
        JDialog dialog = new JDialog();
        dialog.setUndecorated(true);

        JPanel contentPane = new JPanel(new BorderLayout(20, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPane.setBackground(UIManager.getColor("Panel.background"));

        JLabel messageLabel = new JLabel("Enter a note for this item:");
        messageLabel.setFont(UIManager.getFont("Label.font"));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JTextArea noteArea = new JTextArea(5, 20);
        noteArea.setFont(UIManager.getFont("TextArea.font"));
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        noteArea.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // Pre-fill the text area if there is an existing note
        JLabel existingNoteLabel = notesMap.get(productName);
        if (existingNoteLabel != null) {
            noteArea.setText(existingNoteLabel.getText().replaceAll("<html><i>Note: ", "").replace("</i></html>", ""));
        }

        RoundedBorderButton saveButton = getjButton(productName, noteArea, dialog);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(UIManager.getColor("Panel.background"));
        buttonPanel.add(saveButton);

        contentPane.add(messageLabel, BorderLayout.NORTH);

        noteArea.setCaretPosition(0); // Ensure the caret is at the start

        scrollPane = new JScrollPane(noteArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUI(new MacOSScrollBarUI(COLOR_GRAY));


        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        dialog.getContentPane().add(contentPane);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(null);

        // Set rounded corners for the dialog
        dialog.setShape(new RoundRectangle2D.Double(0, 0, dialog.getWidth(), dialog.getHeight(), 20, 20));

        dialog.setVisible(true);
    }

    private static RoundedBorderButton getjButton(String productName, JTextArea noteArea, JDialog dialog) {
        RoundedBorderButton saveButton = new RoundedBorderButton("Save", 80, 30, COLOR_LINE_COLOR);
        saveButton.setPreferredSize(new Dimension(80, 30));
        saveButton.addActionListener((ActionEvent e) -> {
            String note = noteArea.getText().trim();
            if (!note.isEmpty()) {
                // Save the note in notesMap
                JLabel noteLabel = new JLabel("<html><i>Note: " + note + "</i></html>");
                notesMap.put(productName, noteLabel);
            } else {
                // Remove the note if the text area is empty
                notesMap.remove(productName);
            }
            dialog.dispose();
        });
        return saveButton;
    }


    private static void updateQuantity(RoundedPanel orderPanel, Product product, int change) {
        JPanel buttonsPanel = findButtonsPanel(orderPanel);
        if (buttonsPanel == null) {
            System.err.println("Buttons panel not found in orderPanel.");
            return;
        }

        JLabel quantityLabel = findComponentByType(buttonsPanel, JLabel.class, "quantityLabel");
        JLabel subtotalLabel = findComponentByType(buttonsPanel, JLabel.class, "subtotalLabel");

        if (quantityLabel == null || subtotalLabel == null) {
            System.err.println("Quantity or subtotal label not found in buttonsPanel.");
            return;
        }

        int currentQuantity = Integer.parseInt(quantityLabel.getText().trim());
        int newQuantity = currentQuantity + change;

        if (newQuantity < 1) {
            // Optionally handle minimum quantity
            return;
        } else if (newQuantity > product.getStockAvailableNumber()) {
            DialogCollection.showCustomDialog(orderPanel, "Quantity limit exceeded.", "Error");
            return;
        }

        quantityLabel.setText("" + newQuantity);
        double subtotal = product.getPrice() * newQuantity;
        subtotalLabel.setText(priceFormat.format(subtotal) + " Ks");

        // Update displayQuantity
        displayQuantity += change;

        // Log and update the subtotal label
        logOrderPanelState();
    }

    private static JPanel findButtonsPanel(RoundedPanel orderPanel) {
        for (Component component : orderPanel.getComponents()) {
            if (component instanceof JPanel && component.getName() != null && component.getName().equals("buttonsPanel")) {
                return (JPanel) component;
            }
        }
        return null;
    }

    private static <T extends Component> T findComponentByType(Container container, Class<T> type, String name) {
        for (Component component : container.getComponents()) {
            if (type.isAssignableFrom(component.getClass()) && component.getName() != null && component.getName().equals(name)) {
                return type.cast(component);
            }
        }
        return null;
    }


    private static void updateSubtotalLabel(RoundedPanel orderPanel, double currentPrice, int quantity) {
        JPanel buttonsPanel = findButtonsPanel(orderPanel);
        if (buttonsPanel == null) {
            System.err.println("Buttons panel not found in orderPanel.");
            return;
        }

        JLabel subtotalLabel = findComponentByType(buttonsPanel, JLabel.class, "subtotalLabel");

        if (subtotalLabel == null) {
            System.err.println("Subtotal label not found in buttonsPanel.");
            return;
        }

        double subtotal = currentPrice * quantity;
        subtotalLabel.setText(priceFormat.format(subtotal) + " Ks");

    }


    /**
     * Helper method to wrap text to a specified line length.
     *
     * @param text       The text to wrap.
     * @param lineLength The maximum length of each line.
     * @return The wrapped text with <br> tags inserted.
     */
    public static String wrapText(String text, int lineLength) {
        StringBuilder sb = new StringBuilder(text);
        int i = 0;
        while (i + lineLength < sb.length() && (i = sb.lastIndexOf(" ", i + lineLength)) != -1) {
            sb.replace(i, i + 1, "<br>");
        }
        return sb.toString();
    }


    public static ImageIcon getProductPhotoIcon(byte[] photoBlob) {
        if (photoBlob == null || photoBlob.length == 0) {
            return new ImageIcon("/DefaultFoodIcon.png");
        }

        Image image = new ImageIcon(photoBlob).getImage();
        int squareSize = 100;
        double scaleFactor = (double) squareSize / Math.max(image.getWidth(null), image.getHeight(null));
        Image scaledImage = image.getScaledInstance((int) (image.getWidth(null) * scaleFactor), (int) (image.getHeight(null) * scaleFactor), Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }


    private static void printGrandTotalTest() {
        logOrderPanelState();
    }


    private static void updateSubTotalLabel() {
        subTotalNumber.setText(priceFormat.format(displaySubtotal) + " Ks");
    }


    private static void calculateTaxAmountDisplay(int displaySubtotal) {
        taxTotalNumber.setText(priceFormat.format(displaySubtotal * TAX_PERCENTAGE) + " Ks");
    }


    private static void displayPayableAmount(int displaySubtotal) {
        payableAmountNumber.setText(priceFormat.format((displaySubtotal) + (displaySubtotal * TAX_PERCENTAGE)) + " Ks");
    }


    private static void logOrderPanelState() {

        displaySubtotal = 0; // Reset the subtotal before logging
        displayQuantity = 0;
        panelCount = 0;

        for (RoundedPanel panel : orderListsPanelList) {
            JLabel nameLabel = (JLabel) panel.getComponent(0);
            productName = (nameLabel != null ? nameLabel.getText() : "Name label not found");

            // Find the buttonsPanel within the orderPanel
            JPanel buttonsPanel = findButtonsPanel(panel);

            // Find the subtotalLabel within the buttonsPanel
            assert buttonsPanel != null;
            JLabel subtotalLabel = findComponentByType(buttonsPanel, JLabel.class, "subtotalLabel");
            JLabel quantityLabel = findComponentByType(buttonsPanel, JLabel.class, "quantityLabel");

            String subtotalText = (subtotalLabel != null ? subtotalLabel.getText() : "Subtotal label not found");
            String quantityText = (quantityLabel != null ? quantityLabel.getText() : "Quantity label not found");
            int subtotalPrice = parsePrice(subtotalText);
            int quantity = Integer.parseInt(quantityText);
            displaySubtotal += subtotalPrice;
            displayQuantity += quantity;
            panelCount += orderListsPanelList.contains(panel) ? 1 : 0;
        }
        // Update the subTotalNumber label
        updateSubTotalLabel();
        calculateTaxAmountDisplay(displaySubtotal);
        displayPayableAmount(displaySubtotal);
    }

    // Print Button Cart's Action
    public static void showVoucherPopup() {
        List<RoundedTextPane> voucherTextPanes = makeBackendVouchers();

//        if (voucherTextPanes.isEmpty()) {
//            DialogCollection.showCustomDialog(mainAllHoldingPanel, "No vouchers available to display.", "No Vouchers");
//            return;
//        }

        Frame parentFrame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, mainAllHoldingPanel);

        // Create the JDialog with the parent window as the owner
        JDialog dialog = new JDialog(parentFrame, "Voucher", true);
        dialog.setUndecorated(true); // Remove window decorations for custom rounding
        dialog.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        dialog.setSize(680, 680);
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Create a rounded holding panel
        RoundedPanel holdingPanel = new RoundedPanel(20);
        holdingPanel.setLayout(new BorderLayout());
        holdingPanel.setOpaque(false);
        holdingPanel.setPreferredSize(new Dimension(680, 680));
        holdingPanel.setBackground(COLOR_ENTRY_GRAY);

        dialog.getContentPane().add(holdingPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setShape(new RoundRectangle2D.Double(0, 0, 680, 680, 20, 20));

        // Create the custom JTabbedPane with MyFlatTabbedPaneUI
        JTabbedPane tabbedPane = getTabbedPane();
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        // Populate the tabbed pane with vouchers
        for (int i = 0; i < voucherTextPanes.size(); i++) {
            RoundedTextPane textPane = voucherTextPanes.get(i);
            JScrollPane scrollPane = new JScrollPane(textPane);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); // No visible borders
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.getVerticalScrollBar().setUI(new MacOSScrollBarUI(tabbedPane.getBackground())); // Custom scrollbar

            tabbedPane.addTab("Voucher " + (i + 1), scrollPane); // Add each voucher as a tab
        }

        // Create print panel with buttons for each counter
        JPanel printPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 4));
        printPanel.setOpaque(false);
        for (String counterName : getUniqueCounterNames()) {
            String printerAddress = Product.getMainPrinterPortAddressByCounterName(counterName);
            RoundedBorderButton printButton = new RoundedBorderButton(counterName, 30, 28, COLOR_PANEL_GRAY);
            printButton.addActionListener(e -> {
                for (JTextPane textPane : voucherTextPanes) {
                    PrintUtil.printTextPane(textPane, printerAddress);
                }
                MiniDevPOS.showPanel("Home");

            });
            printButton.setFont(new Font("Noto Sans Myanmar",Font.BOLD,11));

            printPanel.add(printButton);
        }

        // Create close button
        RoundedBorderButton closeButton = new RoundedBorderButton("Close", 30, 28, COLOR_SELECT_BLUE);
        closeButton.setFont(new Font(FlatRobotoFont.FAMILY,Font.BOLD,13));
        closeButton.addActionListener(e -> {
            dialog.dispose();
            MiniDevPOS.showPanel("Home");
        });

        // Create button panel and add print/close buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
        buttonPanel.setPreferredSize(new Dimension(dialog.getWidth(), 60));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        buttonPanel.setOpaque(false);
        buttonPanel.add(printPanel);
        buttonPanel.add(closeButton);

        // Add components to the holding panel
        holdingPanel.add(tabbedPane, BorderLayout.CENTER);
        holdingPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private static JTabbedPane getTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane() {
            @Override
            public void updateUI() {
                setUI(new MyFlatTabbedPaneUI()); // Use the custom UI for rounded tabs
            }
        };
        tabbedPane.setOpaque(false); // Make the tabbed pane transparent to blend with the rounded panel
        tabbedPane.setBackground(COLOR_ENTRY_GRAY);
        return tabbedPane;
    }


    private static List<String> getUniqueCounterNames() {
        Set<String> counterNamesSet = new HashSet<>();

        // Extract product names from the panels
        for (RoundedPanel panel : orderListsPanelList) {
            JLabel nameLabel = (JLabel) panel.getComponent(0);
            String productName = (nameLabel != null ? nameLabel.getText() : "Name label not found");

            // Get the counter names for the product
            List<String> counterNames = Product.getCounterNameByProductName(productName);

            // Add counter names to the set to ensure uniqueness
            if (counterNames != null) {
                counterNamesSet.addAll(counterNames);
            }
        }

        // Convert set to list
        return new ArrayList<>(counterNamesSet);
    }


    private static List<RoundedTextPane> makeBackendVouchers() {
        displayQuantity = 0;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss a");
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        java.time.DayOfWeek dayOfWeek = today.getDayOfWeek();

        // Map to group products by counter name
        Map<String, Map<String, Integer>> counterToProductsMap = new HashMap<>();

        for (RoundedPanel panel : orderListsPanelList) {
            JLabel nameLabel = (JLabel) panel.getComponent(0);
            String productName = (nameLabel != null ? nameLabel.getText() : "Name label not found");

            // Find the buttonsPanel within the orderPanel
            JPanel buttonsPanel = findButtonsPanel(panel);
            assert buttonsPanel != null;
            JLabel quantityLabel = findComponentByType(buttonsPanel, JLabel.class, "quantityLabel");
            String quantityText = (quantityLabel != null ? quantityLabel.getText() : "Quantity label not found");
            int quantity = Integer.parseInt(quantityText);

            // Assuming Product.getCounterNameByProductName(productName) returns a list of counter names
            List<String> counterNames = Product.getCounterNameByProductName(productName);

            assert counterNames != null;
            for (String counterName : counterNames) {
                counterToProductsMap.computeIfAbsent(counterName, k -> new HashMap<>()).merge(productName, quantity, Integer::sum);
            }

            displayQuantity += quantity;
        }

        // Create a JTextPane for each counter name
        List<RoundedTextPane> textPanes = new ArrayList<>();
        for (Map.Entry<String, Map<String, Integer>> entry : counterToProductsMap.entrySet()) {
            String counterName = entry.getKey();
            Map<String, Integer> productsWithQuantities = entry.getValue();

            RoundedTextPane textPane = new RoundedTextPane(20);
            textPane.setContentType("text/html"); // Allow for HTML formatting
            StringBuilder voucherContent = new StringBuilder();
            voucherContent.append("<html><body>");
            voucherContent.append("<p>Order Mode: ").append(generateDineInToGoDeliveryStatus()).append("</p>");
            voucherContent.append("<p>Date: ").append(dateFormatter.format(now)).append(" - ").append(dayOfWeek).append("</p>");
            voucherContent.append("<h3>Counter: ").append(counterName).append("</h3>");
            voucherContent.append("<ul>");
            for (Map.Entry<String, Integer> productEntry : productsWithQuantities.entrySet()) {
                String productName = productEntry.getKey();
                int quantity = productEntry.getValue();
                voucherContent.append("<li>").append(" Qty: ").append("( ").append(quantity) // Quantity
                        .append(" ) ").append(productName);
                String note = getNotesOfProduct(productName);
                if (!note.isEmpty()) {
                    voucherContent.append("<ul>").append(" Note : ").append(note).append("</ul>");
                }
                voucherContent.append("</li>");
            }
            voucherContent.append("</ul>");
            voucherContent.append("</body></html>");

            // Set the formatted content to the JTextPane
            textPane.setText(voucherContent.toString());

            // Add the JTextPane to the list
            textPanes.add(textPane);
        }

        return textPanes;
    }

    private static int parsePrice(String priceText) {
        // Remove currency symbol and commas
        String cleanedText = priceText.replaceAll("[^0-9]", "");
        return Integer.parseInt(cleanedText);
    }

    private static void deleteOrderPanel(RoundedPanel orderPanel) {
        if (orderListsPanelList.contains(orderPanel)) {
            orderListsPanelList.remove(orderPanel);
        } else {
        }

        JPanel buttonsPanel = findButtonsPanel(orderPanel);
        JLabel quantityLabel = findComponentByType(buttonsPanel, JLabel.class, "quantityLabel");
        assert quantityLabel != null;
        int quantity = Integer.parseInt(quantityLabel.getText().trim());

        displayQuantity -= quantity;

        if (homeRightCenterOrderPanel.getComponents().length > 0) {
            homeRightCenterOrderPanel.remove(orderPanel);
        } else {
        }

        homeRightCenterOrderPanel.revalidate();
        homeRightCenterOrderPanel.repaint();

        if (orderListsPanelList.isEmpty()) {
            cartDisplayCard.show(cartPanel, "INACTIVE");
            displaySubtotal = 0;
            displayQuantity = 0; // Reset displayQuantity
        } else {
            logOrderPanelState(); // Log and recalculate displaySubtotal after deletion
        }
        // Update the subtotal label after deletion
        updateSubTotalLabel();
        calculateTaxAmountDisplay(displaySubtotal);
        displayPayableAmount(displaySubtotal);

        if (productName != null) {
            notesMap.remove(productName);
        }
    }

    public static void deleteOrderListsPanel() {
        // Initialize a variable to hold the total quantity to be deducted
        int totalQuantityToRemove = 0;

        // Iterate through all components in the homeRightCenterOrderPanel
        for (Component component : homeRightCenterOrderPanel.getComponents()) {
            if (component instanceof RoundedPanel) {
                RoundedPanel orderPanel = (RoundedPanel) component;

                // Find the buttons panel and quantity label
                JPanel buttonsPanel = findButtonsPanel(orderPanel);
                if (buttonsPanel != null) {
                    JLabel quantityLabel = findComponentByType(buttonsPanel, JLabel.class, "quantityLabel");
                    if (quantityLabel != null) {
                        int quantity = Integer.parseInt(quantityLabel.getText().trim());
                        totalQuantityToRemove += quantity;
                    }
                }
            }
        }

        // Remove all components from the panel
        homeRightCenterOrderPanel.removeAll();

        // Update display quantities and subtotals
        displayQuantity -= totalQuantityToRemove; // Adjust total quantity
        displaySubtotal = 0; // Reset subtotal
        cartDisplayCard.show(cartPanel, "INACTIVE");

        // Revalidate and repaint the panel to reflect changes
        homeRightCenterOrderPanel.revalidate();
        homeRightCenterOrderPanel.repaint();

        // Clear the orderListsPanelList as well if needed
        orderListsPanelList.clear();

        // Update UI components or labels related to the subtotal and quantities
        updateSubTotalLabel();
        calculateTaxAmountDisplay(displaySubtotal);
        displayPayableAmount(displaySubtotal);
        // Optionally clear notesMap if it's related to the order panel
        notesMap.clear();

        allClearCustomerInfoOrderCodeVisibility();
    }


    private static String getNotesOfProduct(String productName) {
        String noteText = "";
        if (notesMap.containsKey(productName)) {
            JLabel noteLabel = notesMap.get(productName);
            noteText = noteLabel.getText().replaceAll("<html><i>Note: ", "").replace("</i></html>", "");
        } else {
        }
        return noteText;
    }

    public static void createAdditionalOrderIntoDB(String additionalOrderToOrderCode) {
        displaySubtotal = 0;
        displayQuantity = 0;
        panelCount = 0;

        // Initialize Gson for JSON processing
        Gson gson = new Gson();

        // Create a JSON array to hold the order items
        JsonArray orderItems = new JsonArray();
        for (RoundedPanel panel : orderListsPanelList) {
            JLabel nameLabel = (JLabel) panel.getComponent(0);
            String productName = (nameLabel != null ? nameLabel.getText() : "Name label not found");

            JPanel buttonsPanel = findButtonsPanel(panel);
            assert buttonsPanel != null;
            JLabel subtotalLabel = findComponentByType(buttonsPanel, JLabel.class, "subtotalLabel");
            JLabel quantityLabel = findComponentByType(buttonsPanel, JLabel.class, "quantityLabel");

            String subtotalText = (subtotalLabel != null ? subtotalLabel.getText() : "0 Ks").replace(" Ks", "").replace(",", "");
            String quantityText = (quantityLabel != null ? quantityLabel.getText() : "0");

            int subtotalPrice = Integer.parseInt(subtotalText);
            int quantity = Integer.parseInt(quantityText);

            panelCount++;
            displaySubtotal += subtotalPrice;
            displayQuantity += quantity;

            // Retrieve note for the product
            String noteText = getNotesOfProduct(productName);

            // Create JSON object for each item
            JsonObject orderItem = new JsonObject();
            orderItem.addProperty("OrderFood", productName);
            orderItem.addProperty("OrderQty", quantity);
            orderItem.addProperty("OrderSubTotal", subtotalPrice);
            orderItem.addProperty("OrderPrice1Qty", (subtotalPrice / quantity));
            orderItem.addProperty("OrderNote", noteText); // Add the OrderNote field

            orderItems.add(orderItem);
        }


        String orderItemsJson = gson.toJson(orderItems); // Convert JsonArray to JSON string

        // Connect to MySQL and call the stored procedure
        try (Connection connection = DBConnection.getConnection(); CallableStatement stmt = connection.prepareCall("{CALL sp_CreateAdditionalOrder(?, ?, ?)}")) {

            // Set the parameters and execute the stored procedure
            stmt.setString(1, additionalOrderToOrderCode);
            stmt.setString(2, orderItemsJson);
            stmt.setString(3, String.valueOf(CreateCashDrawerPanel.tellerID));

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        new StockInnerPage().refreshTableAfterItemsUpdater();
        CreateCustomerPanel.refreshCustomerInfoLists();
        allClearCustomerInfoOrderCodeVisibility();
        CreateOrderPanel.updateOrderHistoryArray();
        CreateOrderPanel.updateOrderSummaryArray();
    }


    public static void createOrderToDatabase(String lastClickedTableName) {
        displaySubtotal = 0;
        displayQuantity = 0;
        panelCount = 0;

        // Format the date for SQLite with 12-hour clock format and AM/PM
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(dateFormatter);
        String cid = (customerCIDLabel != null && customerCIDLabel.getText() != null) ? customerCIDLabel.getText() : "";


        // Initialize Gson for JSON processing
        Gson gson = new Gson();

        // Create a JSON array to hold the order items
        JsonArray orderItems = new JsonArray();
        for (RoundedPanel panel : orderListsPanelList) {
            JLabel nameLabel = (JLabel) panel.getComponent(0);
            String productName = (nameLabel != null ? nameLabel.getText() : "Name label not found");

            JPanel buttonsPanel = findButtonsPanel(panel);
            assert buttonsPanel != null;
            JLabel subtotalLabel = findComponentByType(buttonsPanel, JLabel.class, "subtotalLabel");
            JLabel quantityLabel = findComponentByType(buttonsPanel, JLabel.class, "quantityLabel");

            String subtotalText = (subtotalLabel != null ? subtotalLabel.getText() : "0 Ks").replace(" Ks", "").replace(",", "");
            String quantityText = (quantityLabel != null ? quantityLabel.getText() : "0");

            int subtotalPrice = Integer.parseInt(subtotalText);
            int quantity = Integer.parseInt(quantityText);

            panelCount++;
            displaySubtotal += subtotalPrice;
            displayQuantity += quantity;

            // Retrieve note for the product
            String noteText = getNotesOfProduct(productName);

            // Create JSON object for each item
            JsonObject orderItem = new JsonObject();
            orderItem.addProperty("OrderFood", productName);
            orderItem.addProperty("OrderQty", quantity);
            orderItem.addProperty("OrderSubTotal", subtotalPrice);
            orderItem.addProperty("OrderPrice1Qty", (subtotalPrice / quantity));
            orderItem.addProperty("OrderNote", noteText); // Add the OrderNote field

            orderItems.add(orderItem);
        }

        String newOrderCode = GenerateOrderCodeFactory.generateNewOrderCode();
        CURRENT_PROCESSING_ORDER_CODE = newOrderCode;

        String orderItemsJson = gson.toJson(orderItems); // Convert JsonArray to JSON string

        // Connect to MySQL and call the stored procedure
        try (Connection connection = DBConnection.getConnection(); CallableStatement stmt = connection.prepareCall("{CALL sp_CreateOrder(?, ?, ?, ?, ?,?)}")) {

            // Set the parameters and execute the stored procedure
            stmt.setString(1, lastClickedTableName);
            stmt.setString(2, newOrderCode);
            stmt.setString(3, orderItemsJson); // Pass JSON string to stored procedure
            stmt.setString(4, generateDineInToGoDeliveryStatus());
            stmt.setString(5, String.valueOf(CreateCashDrawerPanel.tellerID));
            stmt.setString(6, cid);

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        new StockInnerPage().refreshTableAfterItemsUpdater();
        CreateCustomerPanel.refreshCustomerInfoLists();
        allClearCustomerInfoOrderCodeVisibility();
        CreateOrderPanel.updateOrderHistoryArray();
        CreateOrderPanel.updateOrderSummaryArray();
    }


    public static boolean checkOrderListsActive() {
        return !orderListsPanelList.isEmpty();
    }


}
