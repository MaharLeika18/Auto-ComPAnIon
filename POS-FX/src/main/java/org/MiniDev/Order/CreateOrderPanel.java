package org.MiniDev.Order;

import DBConnection.DBConnection;
import SqlLoadersAndUpdater.FetchOrderDetails;
import SqlLoadersAndUpdater.FetchProductsToDisplay;
import UI.*;
import Utils.VoucherInfo;
import Utils.VoucherPrinter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.MiniDev.Backoffice.StockManagement.StockInnerPage;
import org.MiniDev.Cashier.CreateCashDrawerPanel;
import org.MiniDev.Customer.CreateCustomerPanel;
import org.MiniDev.Home.CreateHomePanel;
import org.MiniDev.Home.MiniDevPOS;
import org.MiniDev.OOP.OrderHistoryLists;
import org.MiniDev.OOP.OrderSummary;
import org.MiniDev.OOP.Product;
import org.MiniDev.Payment.CreatePaymentPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static UI.IconCreator.createResizedIcon;
import static UI.UserFinalSettingsVar.*;
import static UI.UserFinalSettingsVar.COLOR_PANEL_GRAY;


public class CreateOrderPanel extends CreateHomePanel {

    private static JPanel orderMainPanel;
    protected static JLabel subTotalNumberOrder = new JLabel("0 Ks");
    private static final JLabel taxTotalNumberOrder = new JLabel("0 Ks");
    protected static JLabel payableAmountNumberOrder = new JLabel("0 Ks");
    private static RoundedPanel mainOrderPane; // Define as a class member
    protected JPanel topOrderNavigationPanel;
    protected PanelRound searchingOrderPanel;
    protected static JButton lastClickedButton = null;
    private final String[] navigationButtonLabels = {"Order History", "Order On Hold", "Finished"};
    private static RoundedBorderButton[] navigationButtons;
    protected CardLayout cardLayout;
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    private static JLabel lastClickedLabel = null; // Store the reference to the last clicked label
    protected static JPanel orderRightCenterOrderPanel;
    static RoundedPanel orderHistMainInnerPanel = new RoundedPanel();
    protected static RoundedPanel panelForCustomerSelect;
    protected static boolean isVisibleCustomer = false;
    protected String initCustomerNameText = "";
    protected String initCustomerCID = "";
    protected static JLabel customerLabel; //Customer name that imported into database
    protected static JLabel customerCIDLabel;
    protected static RoundedPanel panelForTableSelected;
    protected static boolean isVisibleTable = false;
    protected String initTableNameText = "";
    protected String initTableID = "";
    protected static JLabel tableLabel; //Customer name that imported into database
    protected static JLabel tableIDLabel;
    protected static List<Product> products;
    public static NumberFormat currencyFormat = NumberFormat.getInstance();
    public static JLabel LAST_CLICKED_ORDER_CODE = null;

    public CreateOrderPanel() {
        updateProductsLists();
    }

    private static void updateProductsLists() {
        // Fetch products in the background to avoid blocking the UI
        SwingWorker<List<Product>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Product> doInBackground() {
                FetchProductsToDisplay productFactory = new FetchProductsToDisplay();
                return productFactory.fetchProductsFromDatabase();
            }

            @Override
            protected void done() {
                try {
                    // Handle the fetched products if necessary
                    products = get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    public JPanel createOrdersPanel() {
        orderMainPanel = new JPanel();
        orderMainPanel.setLayout(new BorderLayout());
        orderMainPanel.setBackground(Color.decode("#f5f5f5"));

        RoundedPanel orderLeftPanel = new RoundedPanel();
        orderLeftPanel.setLayout(new BoxLayout(orderLeftPanel, BoxLayout.Y_AXIS));
        orderLeftPanel.setPreferredSize(new Dimension(550, 550));
        orderLeftPanel.setBackground(Color.decode("#f5f5f5"));
        orderLeftPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 15));

        // Create top navigation panel
        topOrderNavigationPanel = createTopOrderNavigationButtons();

        topOrderNavigationPanel.setBackground(Color.WHITE); // For illustration

        JPanel holding = new JPanel(new BorderLayout());
        holding.setOpaque(false);
        holding.setBackground(COLOR_GRAY);
        holding.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        RoundedPanel orderHoldingPanel = new RoundedPanel(10);
        orderHoldingPanel.setLayout(new BorderLayout());
        orderHoldingPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        orderHoldingPanel.setBackground(Color.WHITE);
        orderHoldingPanel.add(createOrderListsPanel(), BorderLayout.CENTER);

        holding.add(orderHoldingPanel, BorderLayout.CENTER);

        // Create a rigid area for the gap
        Component gap = Box.createRigidArea(new Dimension(0, 15)); // Adjust height as needed

        // Add components to orderLeftPanel
        orderLeftPanel.add(topOrderNavigationPanel);
        orderLeftPanel.add(gap); // Add gap
        orderLeftPanel.add(holding);

        JPanel orderRightPanel = new JPanel(new BorderLayout());
        orderRightPanel.setBackground(Color.WHITE);
        orderRightPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.decode("#E8E8E8")));

        JPanel orderRightTopPanel = createOrderRightTopPanel();
        Border border = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#E8E8E8"));
        orderRightTopPanel.setBorder(border);
        orderRightPanel.add(orderRightTopPanel, BorderLayout.NORTH);

        orderRightCenterOrderPanel = new JPanel();
        orderRightCenterOrderPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 1, 5));
        orderRightCenterOrderPanel.setLayout(new BoxLayout(orderRightCenterOrderPanel, BoxLayout.Y_AXIS));
        orderRightCenterOrderPanel.setBackground(Color.WHITE);

//    orderRightCenterOrderPanel.add(createOrderPanelForOrderHist("Test",1,20000,20000));
        orderRightPanel.add(orderRightCenterOrderPanel, BorderLayout.CENTER);

        JScrollPane orderScrollPane = new JScrollPane(orderRightCenterOrderPanel);
        orderScrollPane.setBorder(BorderFactory.createEmptyBorder());
        orderScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        orderScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        orderScrollPane.getVerticalScrollBar().setUI(new MacOSScrollPane.MacOSScrollBarUI(Color.decode("#f5f5f5")));
        orderRightPanel.add(orderScrollPane, BorderLayout.CENTER);

        JPanel orderRightBottomOrderButtonsPanel = new JPanel(new GridBagLayout());
        orderRightBottomOrderButtonsPanel.setPreferredSize(new Dimension(300, 160));
        orderRightBottomOrderButtonsPanel.setBackground(Color.decode("#f5f5f5"));
        orderRightPanel.add(orderRightBottomOrderButtonsPanel, BorderLayout.SOUTH);

        GridBagConstraints gbcTop = new GridBagConstraints();
        gbcTop.gridx = 0;
        gbcTop.gridy = 0;
        gbcTop.weightx = 1.0;
        gbcTop.weighty = 0.2;
        gbcTop.fill = GridBagConstraints.BOTH;
        orderRightBottomOrderButtonsPanel.add(createOrderTopLayerPanel(), gbcTop);

        GridBagConstraints gbcCenter = new GridBagConstraints();
        gbcCenter.gridx = 0;
        gbcCenter.gridy = 1;
        gbcCenter.weightx = 1.0;
        gbcCenter.weighty = 0.1;
        gbcCenter.fill = GridBagConstraints.BOTH;
        orderRightBottomOrderButtonsPanel.add(createCenterLayerPanelOrder(), gbcCenter);

        GridBagConstraints gbcBottom = new GridBagConstraints();
        gbcBottom.gridx = 0;
        gbcBottom.gridy = 2;
        gbcBottom.weightx = 1.0;
        gbcBottom.weighty = 0.06;
        gbcBottom.fill = GridBagConstraints.BOTH;
        orderRightBottomOrderButtonsPanel.add(createOrderProceedPanelOrder(), gbcBottom);

        orderMainPanel.add(orderLeftPanel, BorderLayout.WEST);
        orderMainPanel.add(orderRightPanel, BorderLayout.CENTER);

        return orderMainPanel;
    }

    private static String getDateFormat(Timestamp timestamp) {
        return dateFormat.format(timestamp);
    }


    private static String getTimeFormat(Timestamp timestamp) {
        return timeFormat.format(timestamp);
    }

    private static String getMoneyFormat(BigDecimal amount) {
        return currencyFormat.format(amount);
    }

    private static RoundedPanel createOrderHistoryRowPanel(OrderSummary orderSummary, int rowIndex) {
        RoundedPanel orderItemPanel = getRoundedPanel(rowIndex);

        // Create inner panels
        RoundedPanel leftInnerPanelItems = new RoundedPanel(new BorderLayout());
        RoundedPanel midInnerPanelItems = new RoundedPanel(new BorderLayout());
        RoundedPanel timePanel = new RoundedPanel(new BorderLayout());
        RoundedPanel midRightInnerPanelItems = new RoundedPanel(new BorderLayout());
        RoundedPanel rightInnerPanelItems = new RoundedPanel(new BorderLayout());

        leftInnerPanelItems.setOpaque(false);
        midInnerPanelItems.setOpaque(false);
        timePanel.setOpaque(false);
        midRightInnerPanelItems.setOpaque(false);
        rightInnerPanelItems.setOpaque(false);

        leftInnerPanelItems.setBackground(orderItemPanel.getBackground());
        midInnerPanelItems.setBackground(orderItemPanel.getBackground());
        timePanel.setBackground(orderItemPanel.getBackground());
        midRightInnerPanelItems.setBackground(orderItemPanel.getBackground());
        rightInnerPanelItems.setBackground(orderItemPanel.getBackground());

        // Create labels
        JLabel leftInnerPanelLabelItems = new JLabel(orderSummary.getOrderCode(), JLabel.LEFT);
        JLabel midInnerPanelLabelItems = new JLabel(getDateFormat(orderSummary.getStartDate()), JLabel.CENTER);
        JLabel timeLabel = new JLabel(getTimeFormat(orderSummary.getStartDate()), JLabel.CENTER);
        JLabel midRightInnerPanelLabelItems = new JLabel(orderSummary.getOrderMode(), JLabel.CENTER);
        JLabel rightInnerPanelLabelItems = new JLabel(getMoneyFormat(orderSummary.getOrderSubTotal()), JLabel.RIGHT);

        // Default font
        Font defaultFont = new Font("Noto Sans Myanmar", Font.PLAIN, 12);
        leftInnerPanelLabelItems.setFont(defaultFont);
        midInnerPanelLabelItems.setFont(defaultFont);
        timeLabel.setFont(defaultFont);
        midRightInnerPanelLabelItems.setFont(defaultFont);
        rightInnerPanelLabelItems.setFont(defaultFont);

        // Add labels to their respective panels
        leftInnerPanelItems.add(leftInnerPanelLabelItems, BorderLayout.WEST);
        midInnerPanelItems.add(midInnerPanelLabelItems, BorderLayout.CENTER);
        timePanel.add(timeLabel, BorderLayout.CENTER);
        midRightInnerPanelItems.add(midRightInnerPanelLabelItems, BorderLayout.CENTER);
        rightInnerPanelItems.add(rightInnerPanelLabelItems, BorderLayout.EAST);

        // Add inner panels to the header panel
        orderItemPanel.add(leftInnerPanelItems);
        orderItemPanel.add(midInnerPanelItems);
        orderItemPanel.add(timePanel);
        orderItemPanel.add(midRightInnerPanelItems);
        orderItemPanel.add(rightInnerPanelItems);

        // Add a mouse listener to the panel
        orderItemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                orderItemPanelActionEvent(leftInnerPanelLabelItems, orderSummary);
                LAST_CLICKED_ORDER_CODE = leftInnerPanelLabelItems;
            }
        });

        return orderItemPanel;
    }

    private static void orderItemPanelActionEvent(JLabel leftInnerPanelLabelItems, OrderSummary orderSummary) {
        SwingUtilities.invokeLater(() -> {
            // Reset the font of the previously clicked label, if any
            if (lastClickedLabel != null) {
                resetFont(lastClickedLabel);
            }
            // Update the lastClickedLabel and set the font of the newly clicked label
            lastClickedLabel = leftInnerPanelLabelItems;
            actionPerformedSetFont(lastClickedLabel);
            updateProductsLists();
            resetRemainingLists(orderSummary.getOrderCode());
            // Retrieve and display order details with the refreshed data
            displayOrderDetails(orderSummary.getOrderCode());
        });
    }

    private static List<OrderHistoryLists> remainingLists = new ArrayList<>();

    private static void resetRemainingLists(String orderCode) {
        // Clear the existing list but keep the reference intact
        remainingLists.clear();

        // Add only the items that match the orderCode and create new copies of them
        remainingLists.addAll(orderHistoryLists.stream()
                .filter(f -> f.getOrderCode().equals(orderCode))
                .map(OrderHistoryLists::new) // Create new instances of OrderHistoryLists
                .toList());
    }


//                    LocalDateTime now = LocalDateTime.now(); // Get current time here
//
//                    if (orderHistoryList.getFirst().getEndDate() == null) {
//                        showVoucher("MiniDev", orderCode, subTotal, orderHistoryList.getFirst().getTableName(), orderHistoryList, orderHistoryList.getFirst().getStartDate(), now, orderHistoryList.getFirst().getOrderFinish(), orderHistoryList.getFirst().getDineInToGoDelivery());
//                    } else {
//                        showVoucher("MiniDev", orderCode, subTotal, orderHistoryList.getFirst().getTableName(), orderHistoryList, orderHistoryList.getFirst().getStartDate(), orderHistoryList.getFirst().getEndDate(), orderHistoryList.getFirst().getOrderFinish(), orderHistoryList.getFirst().getDineInToGoDelivery());
//                    }

    private static void displayOrderDetails(String orderCode) {
        SwingWorker<List<OrderHistoryLists>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<OrderHistoryLists> doInBackground() {
                // Efficiently filter and return the relevant order history
                return orderHistoryLists.stream()
                        .filter(orderRow -> orderRow.getOrderCode().equals(orderCode))
                        .collect(Collectors.toList());
            }

            @Override
            protected void done() {
                try {
                    List<OrderHistoryLists> orderHistoryList = get();
                    updateOrderDetailsPanel(orderHistoryList);
                    updateOrderCost(dynamicSub, dynamicTax, dynamicPayable);
                    generateCustomerInfoOnOrderPage(orderHistoryList);
                    generateTableChangeOnOrderPage(orderHistoryList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }


    private static RoundedPanel getRoundedPanel(int rowIndex) {
        RoundedPanel orderItemPanel = new RoundedPanel(12);
        orderItemPanel.setOpaque(false);
        orderItemPanel.setLayout(new GridLayout(1, 4, 0, 0));
        orderItemPanel.setPreferredSize(new Dimension(470, 40));
        orderItemPanel.setMaximumSize(new Dimension(470, 40));
        orderItemPanel.setMinimumSize(new Dimension(470, 40));
        orderItemPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        Color backgroundColor = (rowIndex % 2 == 0) ? COLOR_WHITE : COLOR_PANEL_GRAY;

        orderItemPanel.setBackground(backgroundColor);
        return orderItemPanel;
    }

    static DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss a");


    public static void generateCustomerInfoOnOrderPage(List<OrderHistoryLists> orderHistoryLists) {
        if (orderHistoryLists.getFirst().getCustomerID() == null) {
            setCustomerPanelVisibilityOnOrderPage(false, "", "");
        } else {
            setCustomerPanelVisibilityOnOrderPage(true, orderHistoryLists.getFirst().getCustomerName(), orderHistoryLists.getFirst().getCustomerID());
        }
    }

    public static void generateTableChangeOnOrderPage(List<OrderHistoryLists> orderHistoryLists) {
        if (orderHistoryLists.getFirst().getTableName() == null) {
            setTableChangePanelVisibilityOnOrderPage(false, "", "");
        } else {
            setTableChangePanelVisibilityOnOrderPage(true, orderHistoryLists.getFirst().getTableName(), String.valueOf(orderHistoryLists.getFirst().getTableID()));
        }
    }


    public static ImageIcon createImageIcon(byte[] imageData) {
        if (imageData != null && imageData.length > 0) {
            try {
                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageData));
                return new ImageIcon(bufferedImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private static RoundedPanel createOrderPanelForOrderHist(OrderHistoryLists oneOrder, int rowIndex) {
        Color backgroundColor = (rowIndex % 2 == 0) ? COLOR_WHITE : COLOR_PANEL_GRAY;

        // Create the main panel with rounded corners and GridBagLayout
        RoundedPanel orderPanel = getOrderRowMainPanel(backgroundColor);
        orderPanel.putClientProperty("order", oneOrder);  // Attach the order object
        // PictureBox for the product photo
        ImageAvatar picsBox = new ImageAvatar();
        ImageIcon icon = createImageIcon(oneOrder.getFoodPhoto());
        if (icon != null) {
            picsBox.setImage(icon);
        }
        picsBox.setOpaque(false);
        picsBox.setBorderSize(0);
        picsBox.setArcSize(8);
        picsBox.setBackground(backgroundColor);
        picsBox.setPreferredSize(new Dimension(65, 65));
        picsBox.setMinimumSize(new Dimension(65, 65));
        picsBox.setMaximumSize(new Dimension(65, 65));

        // Create the right inner panel
        RoundedPanel middleInner = new RoundedPanel(new GridLayout(2, 1, 0, 0));
        middleInner.setOpaque(false);
        middleInner.setBackground(backgroundColor);

        // Item name label (fixed width)
        int maxLength = 60;
        JLabel nameLabel = getjLabel(maxLength, oneOrder);

        JLabel quantityLabel = new JLabel("x" + oneOrder.getOrderQty());
        quantityLabel.setName("quantityLabel");
        orderPanel.putClientProperty("quantityLabel", quantityLabel);


        quantityLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 12));
        quantityLabel.setForeground(Color.GRAY);
        quantityLabel.setHorizontalAlignment(SwingConstants.RIGHT);


        JLabel subtotalLabel = new JLabel(priceFormat.format(oneOrder.getOrderSubTotal()) + " Ks");
        subtotalLabel.setName("subTotalLabel");
        subtotalLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));
        subtotalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        orderPanel.putClientProperty("subTotalLabel", subtotalLabel);

        // Add labels to rightUpperLayer
        RoundedPanel rightUpperLayer = new RoundedPanel(new BorderLayout());
        rightUpperLayer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        rightUpperLayer.setOpaque(false);
        rightUpperLayer.setBackground(backgroundColor);

        RoundedPanel rightLowerLayer = new RoundedPanel(new BorderLayout());
        rightLowerLayer.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        rightLowerLayer.setOpaque(false);
        rightLowerLayer.setBackground(backgroundColor);

        //Alien Version
        JPanel buttonUpper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonUpper.setOpaque(false);
        buttonUpper.setBackground(COLOR_ORANGE);
        buttonUpper.add(getQtyAddButton(orderPanel));

        JPanel buttonLower = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonLower.setOpaque(false);
        buttonLower.add(getQtyMinusButton(orderPanel));
        rightUpperLayer.add(nameLabel, BorderLayout.WEST);


        // Item name label (fixed width)
        RoundedPanel middlePlaceHolder = getRoundedPanel(oneOrder, maxLength);
        middlePlaceHolder.setOpaque(false);
        middlePlaceHolder.setBackground(backgroundColor);

        rightLowerLayer.add(middlePlaceHolder, BorderLayout.WEST);

        middleInner.add(rightUpperLayer);
        middleInner.add(rightLowerLayer);


        ImageIcon deleteButtonIcon = new SvgIcon("/DeleteIcon.svg", 18).getImageIcon();
        JButton deleteButton = createIconButton(deleteButtonIcon, e -> {
            deleteOrderAndUpdateDisplay(oneOrder);
        });
        deleteButton.setBackground(backgroundColor);
        deleteButton.setBorderPainted(false);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setFocusPainted(false);
        deleteButton.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));


        PanelRound rightHolding = getButtonHoldingPanel(backgroundColor);
        rightHolding.setOpaque(true);
        rightHolding.setPreferredSize(new Dimension(140, orderPanel.getHeight()));
        rightHolding.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 15));

        // Create a single panel for both quantity buttons with GridLayout for vertical alignment
        JPanel quantityButtonsPanel = new JPanel(new GridLayout(2, 1, 0, 5));  // 5px gap for spacing
        quantityButtonsPanel.setOpaque(false);

        // Add the add and minus buttons to the panel
        quantityButtonsPanel.add(getQtyAddButton(orderPanel));
        quantityButtonsPanel.add(getQtyMinusButton(orderPanel));

        JLabel qty1PriceLabel = new JLabel(String.valueOf(oneOrder.getOrderPrice1Qty()));
        qty1PriceLabel.setName("qty1PriceLabel");
        orderPanel.putClientProperty("qty1PriceLabel", qty1PriceLabel);

        JLabel foodSerialCodeLabel = new JLabel(oneOrder.getFoodSerialCode());
        foodSerialCodeLabel.setName("foodSerialCodeLabel");
        orderPanel.putClientProperty("foodSerialCodeLabel", foodSerialCodeLabel);

        JLabel thisOrder = new JLabel(oneOrder.getOrderCode());
        thisOrder.setName("thisOrder");
        orderPanel.putClientProperty("thisOrder", thisOrder);


        JPanel itemInfoPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        itemInfoPanel.setOpaque(false);
        itemInfoPanel.setName("infoPanel");  // Set the name to ensure it can be found
        itemInfoPanel.add(quantityLabel);
        itemInfoPanel.add(subtotalLabel);


        rightHolding.add(quantityButtonsPanel, BorderLayout.WEST);
        rightHolding.add(itemInfoPanel, BorderLayout.CENTER);
        rightHolding.add(deleteButton, BorderLayout.EAST);

        orderPanel.add(picsBox, BorderLayout.WEST);
        orderPanel.add(middleInner, BorderLayout.CENTER);
        orderPanel.add(rightHolding, BorderLayout.EAST);

        return orderPanel;
    }

    private static double extractNumber(String text) {
        // Remove non-numeric characters (except for the decimal point)
        text = text.replaceAll("[^0-9.]", "");

        // Parse the cleaned string to a double
        try {
            return Double.parseDouble(text); // Convert the string to a double
        } catch (NumberFormatException e) {
            // Handle cases where the string is not a valid number (e.g., empty string)
            System.err.println("Error parsing number: " + text);
            return 0.0; // Default to 0.0 in case of error
        }
    }

    private static JButton getQtyAddButton(RoundedPanel thisOrderPanel) {
        ImageIcon addItemIcon = new SvgIcon("/AddItemIcon.svg", 22).getImageIcon();
        return createIconButton(addItemIcon, e -> doQtyAddButtonEvent(thisOrderPanel));
    }

    private static JButton getQtyMinusButton(RoundedPanel thisOrderPanel) {
        // Create and configure buttons
        ImageIcon removeItemIcon = new SvgIcon("/RemoveItemIcon.svg", 22).getImageIcon();

        return createIconButton(removeItemIcon, e -> doQtyMinusButtonEvent(thisOrderPanel));
    }

    private static void doQtyAddButtonEvent(RoundedPanel thisOrderPanel) {
        frontendUpdateAddButtonEvent(thisOrderPanel);
    }

    private static void doQtyMinusButtonEvent(RoundedPanel thisOrderPanel) {
        frontendUpdateMinusButtonEvent(thisOrderPanel);
    }

    private static void frontendUpdateMinusButtonEvent(RoundedPanel orderPanel) {
        JLabel qtyLabel = (JLabel) orderPanel.getClientProperty("quantityLabel");
        JLabel qty1PriceLabel = (JLabel) orderPanel.getClientProperty("qty1PriceLabel");
        JLabel foodSerialCodeLabel = (JLabel) orderPanel.getClientProperty("foodSerialCodeLabel");
        JLabel thisOrderCode = (JLabel) orderPanel.getClientProperty("thisOrder");
        JLabel subTotalLabel = (JLabel) orderPanel.getClientProperty("subTotalLabel");

        // Extract numbers from text, using the extractNumber method for proper handling
        int updateQty = (int) extractNumber(qtyLabel.getText()) - 1;
        double qty1Price = extractNumber(qty1PriceLabel.getText());  // Use extractNumber for consistency
        double subTotal = extractNumber(subTotalLabel.getText()) - qty1Price;

        // Set new values
        if (updateQty >= 1 && subTotal >= qty1Price) {
            qtyLabel.setText("x" + updateQty);
            subTotalLabel.setText(getMoneyFormat(BigDecimal.valueOf(subTotal)) + " Ks");
            dynamicSub -= qty1Price;

            for (Product product : products) {
                if (product.getSerialCode().equals(foodSerialCodeLabel.getText())) {
                    product.setStockAvailableNumber(product.getStockAvailableNumber() + 1);
                }
            }

        } else {
            JOptionPane.showMessageDialog(orderMainPanel, "You cannot reduce stocks lower than 1");
        }


        dynamicTax = dynamicSub * TAX_PERCENTAGE;
        dynamicPayable = dynamicSub + dynamicTax;

        // Refresh the UI panel and update cost display
        orderRightCenterOrderPanel.revalidate();
        orderRightCenterOrderPanel.repaint();
        updateOrderCost(dynamicSub, dynamicTax, dynamicPayable);

        for (OrderHistoryLists orderHistoryLists : remainingLists) {
            if (orderHistoryLists.getFoodSerialCode().equals(foodSerialCodeLabel.getText())) {
                orderHistoryLists.setOrderQty(updateQty);
                orderHistoryLists.setOrderSubTotal(subTotal);
                orderHistoryLists.setOrderPrice1Qty(qty1Price);
            }
            orderHistoryLists.setTotalOrderSubTotal(dynamicSub);
        }
    }


    private static void frontendUpdateAddButtonEvent(RoundedPanel orderPanel) {
        // Retrieve components and properties from the order panel
        JLabel qtyLabel = (JLabel) orderPanel.getClientProperty("quantityLabel");
        JLabel qty1PriceLabel = (JLabel) orderPanel.getClientProperty("qty1PriceLabel");
        JLabel foodSerialCodeLabel = (JLabel) orderPanel.getClientProperty("foodSerialCodeLabel");
        JLabel thisOrderCode = (JLabel) orderPanel.getClientProperty("thisOrder");
        JLabel subTotalLabel = (JLabel) orderPanel.getClientProperty("subTotalLabel");


        // Find matching product and ensure stock is available
        boolean stockAvailable = false;
        for (Product product : products) {
            if (product.getSerialCode().equals(foodSerialCodeLabel.getText()) && product.getStockAvailableNumber() > 0) {
                // Reduce stock by 1 for each button click
                product.setStockAvailableNumber(product.getStockAvailableNumber() - 1);
                stockAvailable = true;
                break;
            }
        }

        // Proceed only if stock is available
        if (stockAvailable) {
            // Update quantity and subtotal based on the retrieved prices
            int updateQty = (int) extractNumber(qtyLabel.getText()) + 1;
            double qty1Price = extractNumber(qty1PriceLabel.getText());
            double subTotal = extractNumber(subTotalLabel.getText()) + qty1Price;

            // Update the display labels
            qtyLabel.setText("x" + updateQty);
            subTotalLabel.setText(getMoneyFormat(BigDecimal.valueOf(subTotal)) + " Ks");

            // Update dynamic totals
            dynamicSub += qty1Price;
            dynamicTax = dynamicSub * TAX_PERCENTAGE;
            dynamicPayable = dynamicSub + dynamicTax;

            // Refresh UI panel and update cost display
            orderRightCenterOrderPanel.revalidate();
            orderRightCenterOrderPanel.repaint();
            updateOrderCost(dynamicSub, dynamicTax, dynamicPayable);

            // Update the corresponding `OrderHistoryLists` entry
            for (OrderHistoryLists orderHistoryLists : remainingLists) {
                if (orderHistoryLists.getFoodSerialCode().equals(foodSerialCodeLabel.getText())) {
                    orderHistoryLists.setOrderQty(updateQty);
                    orderHistoryLists.setOrderSubTotal(subTotal);
                    orderHistoryLists.setOrderPrice1Qty(qty1Price);
                }
                orderHistoryLists.setTotalOrderSubTotal(dynamicSub);
            }
        } else {
            // Handle case when no stock is available
            JOptionPane.showMessageDialog(null, "No stock available for this product.", "Stock Alert", JOptionPane.WARNING_MESSAGE);
        }
    }


    private static PanelRound getButtonHoldingPanel(Color backgroundColor) {
        PanelRound buttonsHoldingPanel = new PanelRound();
        buttonsHoldingPanel.setRoundBottomRight(10);
        buttonsHoldingPanel.setRoundTopRight(10);
        buttonsHoldingPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Adjust padding
        buttonsHoldingPanel.setPreferredSize(new Dimension(35, 35)); // Smaller size
        buttonsHoldingPanel.setLayout(new BorderLayout());
        buttonsHoldingPanel.setOpaque(true);
        buttonsHoldingPanel.setBackground(backgroundColor);
        return buttonsHoldingPanel;
    }

    private static RoundedPanel getOrderRowMainPanel(Color backgroundColor) {
        RoundedPanel orderPanel = new RoundedPanel(10);
        orderPanel.setLayout(new BorderLayout());
        orderPanel.setOpaque(false);
        orderPanel.setBackground(backgroundColor);
        orderPanel.setPreferredSize(new Dimension(130, 70));
        orderPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 70));
        orderPanel.setBorder(BorderFactory.createCompoundBorder(
                (BorderFactory.createEmptyBorder(1, 2, 1, 5)),
                (BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_LINE_COLOR))
        ));
        return orderPanel;
    }


    private static void deleteOrderAndUpdateDisplay(OrderHistoryLists orderToDelete) {
        // Loop through components and find the panel matching the order to delete
        for (Component component : orderRightCenterOrderPanel.getComponents()) {
            if (component instanceof RoundedPanel orderPanel) {
                // Get the order from the client property of the panel
                OrderHistoryLists panelOrder = (OrderHistoryLists) orderPanel.getClientProperty("order");

                // If the panel order matches the order to delete
                if (panelOrder != null && panelOrder.equals(orderToDelete)) {
                    // Only proceed if there's more than one panel left after removal
                    if (orderRightCenterOrderPanel.getComponentCount() > 1) {
                        // Remove the specific panel from the order display
                        orderRightCenterOrderPanel.remove(orderPanel);

                        // Calculate the total value of all matching items in remainingLists
                        int totalToSubtract = 0;
                        for (OrderHistoryLists item : remainingLists) {
                            if (item.getFoodSerialCode().equals(orderToDelete.getFoodSerialCode())) {
                                totalToSubtract += (int) (item.getOrderQty() * item.getOrderPrice1Qty());
                            }
                        }
                        // Subtract the total value of matching items from dynamicSub
                        dynamicSub -= totalToSubtract;

                        // Remove all matching items from remainingLists based on foodSerialCode
                        remainingLists.removeIf(item -> item.getFoodSerialCode().equals(orderToDelete.getFoodSerialCode()));
                    } else {
                        JOptionPane.showMessageDialog(orderMainPanel, "You must have at least one order...");
                    }
                    break; // Exit loop once the order is found and removed
                }
            }
        }

        // Recalculate tax and payable after deletion
        dynamicTax = dynamicSub * TAX_PERCENTAGE;
        dynamicPayable = dynamicSub + dynamicTax;

        // Refresh the UI panel and update cost display
        SwingUtilities.invokeLater(() -> { // Ensure updates happen on the EDT
            orderRightCenterOrderPanel.revalidate();
            orderRightCenterOrderPanel.repaint();
            updateOrderCost(dynamicSub, dynamicTax, dynamicPayable);
        });
    }

    private static JLabel getjLabel(int maxLength, OrderHistoryLists oneOrder) {
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

    private static RoundedPanel getRoundedPanel(OrderHistoryLists oneOrder, int maxLength) {
        int maxLengthForNote = 60;
        String displayNote = oneOrder.getOrderNote().length() > maxLengthForNote ? oneOrder.getOrderNote().substring(0, maxLength) + "..." : oneOrder.getOrderNote();
        JLabel orderLabel = getjLabel(oneOrder, displayNote);

        RoundedPanel middlePlaceHolder = new RoundedPanel(new BorderLayout());
        middlePlaceHolder.setPreferredSize(new Dimension(200, 100));
        middlePlaceHolder.setMaximumSize(new Dimension(200, 100));
        middlePlaceHolder.add(orderLabel, BorderLayout.WEST);
        middlePlaceHolder.setOpaque(false);
        return middlePlaceHolder;
    }

    private static JLabel getjLabel(OrderHistoryLists oneOrder, String displayNote) {
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


    protected static void updateOrderCost(double resultSub, double resultTax, double resultPayable) {
        subTotalNumberOrder.setText(priceFormat.format(resultSub) + " Ks");
        taxTotalNumberOrder.setText(priceFormat.format(resultTax) + " Ks");
        payableAmountNumberOrder.setText(priceFormat.format(resultPayable) + " Ks");
    }

    protected void clearNumbersOfOrder() {
        subTotalNumberOrder.setText("0 Ks");
        taxTotalNumberOrder.setText("0 Ks");
        payableAmountNumberOrder.setText("0 Ks");
    }


    public static void showVoucher(String cashierName, String orderCode, double displaySubtotal, String tableName, List<OrderHistoryLists> orderHistoryLists, LocalDateTime startDate, LocalDateTime endDate, char finishYN, String dineInToGoDeliveryStatus) {
        VoucherInfo voucherInfo = new VoucherInfo();
        JFrame popupFrame = new JFrame("Order Summary");
        popupFrame.setSize(500, 680);
        popupFrame.setLocationRelativeTo(null);
        popupFrame.setUndecorated(true);
        popupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 10, 20, 10));
        contentPanel.setBackground(Color.WHITE);

        JPanel voucherPanel = new JPanel(new BorderLayout());
        voucherPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        voucherPanel.setBackground(Color.WHITE);

        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        textPane.setBackground(Color.WHITE);
        textPane.setBorder(null);

        DecimalFormat decimalFormat = new DecimalFormat("#,###");


        // Calculate the duration between the two dates
        Duration duration = Duration.between(startDate, endDate);

        // Extract hours, minutes, and seconds
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        // Format the result as HH:MM:SS
        String formattedTableDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        String voucherStatus = null;


        if (finishYN == 'Y') {
            voucherStatus = "Paid";
        } else {
            voucherStatus = "Pending";
        }


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
        summary.append("<td style='padding: 0; text-align:left; margin: 0;'>Cashier: ").append(cashierName).append("</td>");
        summary.append("<td style='padding: 0; text-align:right; margin: 0;'>Order No: ").append(orderCode).append("</td>");
        summary.append("</tr>");

        summary.append("<tr>");

        if (tableName == null) {
            summary.append("<td style='padding: 0; text-align:left; margin: 0;'>Table: ").append(" ").append("</td>");
        } else {
            summary.append("<td style='padding: 0; text-align:left; margin: 0;'>Table: ").append(tableName).append("</td>");
        }

        if (tableName == null) {
            summary.append("<td style='padding: 0; text-align:right; margin: 0;'>").append(dineInToGoDeliveryStatus).append("</td>");
        } else {
            summary.append("<td style='padding: 0; text-align:right; margin: 0;'>Duration: ").append(formattedTableDuration).append("</td>");
        }
        summary.append("</tr>");

        summary.append("<tr>");
        summary.append("<td style='padding: 0; text-align:left; margin: 0;'>Status: ").append(voucherStatus).append("</td>");
        summary.append("<td style='padding: 0; text-align:right; margin: 0;'>").append(date.format(startDate)).append("</td>");
        summary.append("</tr>");

        summary.append("</table>");
        summary.append("<div style='border-top: 1px dashed black; width: 100%; margin: 0; padding: 0;'></div>");
        summary.append("</div>");

        summary.append("<h3 style='margin: 3px 0;'>Order Summary</h3>");
        summary.append("<table style='width:100%; border-collapse:collapse;'>");
        summary.append("<thead>");
        summary.append("<tr style='border-bottom: none;'>");
        summary.append("<th style='padding: 5px; text-align:right; width:10%; border-bottom: none;'>No.</th>");
        summary.append("<th style='padding: 5px; text-align:left; width:40%; border-bottom: none;'>Description</th>");
        summary.append("<th style='padding: 5px; text-align:right; width:15%; border-bottom: none;'>UnitPrice</th>");
        summary.append("<th style='padding: 5px; text-align:right; width:10%; border-bottom: none;'>Qty</th>");
        summary.append("<th style='padding: 5px; text-align:right; width:25%; border-bottom: none;'>SubTotal</th>");
        summary.append("</tr>");
        summary.append("</thead>");
        summary.append("<tbody>");

        int panelCounter = 0;
        for (OrderHistoryLists item : orderHistoryLists) {
            panelCounter++;

            double subtotalPrice = item.getOrderSubTotal();
            int quantity = item.getOrderQty();
            double unitPrice = item.getOrderPrice1Qty();

            summary.append("<tr style='font-family: \"Noto Sans Myanmar\", Roboto;'>");
            summary.append("<td style='padding: 5px; text-align:right;'>").append(panelCounter).append("</td>");
            summary.append("<td style='padding: 5px; text-align:left;'>").append(item.getOrderFood()).append("</td>");
            summary.append("<td style='padding: 5px; text-align:right;'>").append(decimalFormat.format(unitPrice)).append("</td>");
            summary.append("<td style='padding: 5px; text-align:right;'>").append(quantity).append("</td>");
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
        scrollPaneV.getVerticalScrollBar().setUI(new MacOSScrollPane.MacOSScrollBarUI(Color.decode("#f5f5f5")));

        // Ensure the scrollPane starts at the top
        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalScrollBar = scrollPaneV.getVerticalScrollBar();
            verticalScrollBar.setValue(verticalScrollBar.getMinimum());
        });

        JButton printButton = new JButton("Print");
        printButton.setPreferredSize(new Dimension(80, 30)); // Adjust button size
        printButton.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13));
        printButton.setBackground(new Color(240, 240, 240));
        printButton.setFocusPainted(true);
        printButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        printButton.addActionListener(e -> VoucherPrinter.printVoucher(textPane));

        JButton closeButton = new JButton("Close");
        closeButton.setPreferredSize(new Dimension(80, 30)); // Adjust button size
        closeButton.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13));
        closeButton.setBackground(new Color(240, 240, 240));
        closeButton.setFocusPainted(true);
        closeButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        closeButton.addActionListener(e -> popupFrame.dispose());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(printButton);
        bottomPanel.add(closeButton);

        voucherPanel.add(scrollPaneV, BorderLayout.CENTER);
        voucherPanel.add(bottomPanel, BorderLayout.SOUTH);

        contentPanel.add(voucherPanel, BorderLayout.CENTER);

        popupFrame.add(contentPanel);
        popupFrame.setVisible(true);
    }


    protected RoundedPanel createOrderListsPanel() {
        RoundedPanel orderPanel = new RoundedPanel(new BorderLayout());
        orderPanel.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
        orderPanel.setBackground(Color.WHITE);

        searchingOrderPanel = createOrderPageTopSearchPanel();

        // Initialize mainOrderPane with CardLayout
        cardLayout = new CardLayout();

        mainOrderPane = new RoundedPanel(cardLayout);
        mainOrderPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        mainOrderPane.setBackground(Color.WHITE);
        mainOrderPane.add(createOrderHistPanel());

        // Add the searchingOrderPanel and mainOrderPane to the orderPanel
        orderPanel.add(searchingOrderPanel, BorderLayout.NORTH);
        orderPanel.add(mainOrderPane, BorderLayout.CENTER);

        return orderPanel;
    }

    private static RoundedPanel createHeaderPanel() {
        Color color = Color.decode("#f5f5f5");
        RoundedPanel headerPanel = new RoundedPanel(12);
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new GridLayout(1, 4, 0, 0));
        headerPanel.setBackground(color);
        headerPanel.setPreferredSize(new Dimension(470, 40));
        headerPanel.setMaximumSize(new Dimension(470, 40));
        headerPanel.setMinimumSize(new Dimension(470, 40));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(1, 10, 1, 10));

        // Create inner panels
        RoundedPanel leftInnerPanel = new RoundedPanel(new BorderLayout());
        RoundedPanel midInnerPanel = new RoundedPanel(new BorderLayout());
        RoundedPanel timePanel = new RoundedPanel(new BorderLayout());
        RoundedPanel midRightInnerPanel = new RoundedPanel(new BorderLayout());
        RoundedPanel rightInnerPanel = new RoundedPanel(new BorderLayout());

        leftInnerPanel.setOpaque(false);
        midInnerPanel.setOpaque(false);
        timePanel.setOpaque(false);
        midRightInnerPanel.setOpaque(false);
        rightInnerPanel.setOpaque(false);

        leftInnerPanel.setBackground(color);
        midInnerPanel.setBackground(color);
        timePanel.setBackground(color);
        midRightInnerPanel.setBackground(color);
        rightInnerPanel.setBackground(color);

        // Create labels
        JLabel leftInnerPanelLabel = new JLabel("Order ID", JLabel.LEFT);
        JLabel midInnerPanelLabel = new JLabel("Date", JLabel.CENTER);
        JLabel timePanelLabel = new JLabel("Time", JLabel.CENTER);
        JLabel midRightInnerPanelLabel = new JLabel("Mode", JLabel.CENTER);
        JLabel rightInnerPanelLabel = new JLabel("Total Sales", JLabel.RIGHT);

        leftInnerPanelLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));
        midInnerPanelLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));
        timePanelLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));
        midRightInnerPanelLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));
        rightInnerPanelLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));

        // Add labels to their respective panels
        leftInnerPanel.add(leftInnerPanelLabel, BorderLayout.WEST);
        midInnerPanel.add(midInnerPanelLabel, BorderLayout.CENTER);
        timePanel.add(timePanelLabel, BorderLayout.CENTER);
        midRightInnerPanel.add(midRightInnerPanelLabel, BorderLayout.CENTER);
        rightInnerPanel.add(rightInnerPanelLabel, BorderLayout.EAST);

        // Add inner panels to the header panel
        headerPanel.add(leftInnerPanel);
        headerPanel.add(midInnerPanel);
        headerPanel.add(timePanel);
        headerPanel.add(midRightInnerPanel);
        headerPanel.add(rightInnerPanel);

        return headerPanel;
    }


    private static void actionPerformedSetFont(JLabel label) {
        // Use a larger and bold font directly
        label.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 15));
    }

    private static void resetFont(JLabel label) {
        // Reset the font to the original state
        try {
            label.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 12));
        } catch (NullPointerException e) {
            System.out.println("label is null");
        }

    }


    static void startOrderHistoryButtonAnimation() {
        animateBorderColor(navigationButtons[0]);
    }

    protected JPanel createTopOrderNavigationButtons() {
        JPanel holding = new JPanel(new FlowLayout(FlowLayout.LEFT));
        holding.setOpaque(false);
        holding.setBackground(COLOR_GRAY);
        holding.setPreferredSize(new Dimension(555, 50));

        RoundedPanel topOrderNavigationPanel = getTopOrderNavigationPanel();

        navigationButtons = new RoundedBorderButton[navigationButtonLabels.length];
        for (int i = 0; i < navigationButtonLabels.length; i++) {
            navigationButtons[i] = createTopNavigationButton(navigationButtonLabels[i]);
            int index = i; // capture the index for the lambda
            navigationButtons[i].addActionListener(e -> SwingUtilities.invokeLater(() -> {
                resetButtonColorsForNavigationButtons();
                lastClickedButton = navigationButtons[index];
                handleButtonActionForOrderNavigation((RoundedBorderButton) e.getSource());
                updateOrderHistPanel(false);
            }));
            topOrderNavigationPanel.add(navigationButtons[i]);
        }

        startOrderHistoryButtonAnimation();

        holding.add(topOrderNavigationPanel);

        return holding;
    }

    private static RoundedPanel getTopOrderNavigationPanel() {
        RoundedPanel topOrderNavigationPanel = new RoundedPanel(10);
        topOrderNavigationPanel.setLayout(new BoxLayout(topOrderNavigationPanel, BoxLayout.X_AXIS));
        topOrderNavigationPanel.setBackground(Color.WHITE);
        topOrderNavigationPanel.setPreferredSize(new Dimension(520, 40));
        topOrderNavigationPanel.setMinimumSize(new Dimension(520, 40));
        topOrderNavigationPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        topOrderNavigationPanel.setOpaque(false);
        return topOrderNavigationPanel;
    }

    public static void resetNavigationButtonAnimation() {
        resetButtonColorsForNavigationButtons();
        startOrderHistoryButtonAnimation();
    }

    protected RoundedBorderButton createTopNavigationButton(String buttonName) {
        return getNaviButUI(buttonName, 85, 30);
    }

    public static RoundedBorderButton getNaviButUI(String buttonName, int width, int height) {
        RoundedBorderButton button = new RoundedBorderButton(buttonName, width, height, COLOR_WHITE);
        button.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 11));
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setRoundedArcSize(10);
        button.setMargin(new Insets(7, 4, 7, 4));
//        button.setPreferredSize(new Dimension(90, 30));
        button.setPreferredSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setMinimumSize(new Dimension(width, height));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFocusPainted(true);
        button.setContentAreaFilled(false);
        button.setRolloverEnabled(false);
        return button;
    }


    protected static double dynamicSub;
    protected static double dynamicTax;
    protected static double dynamicPayable;


    private static void updateOrderDetailsPanel(List<OrderHistoryLists> orderHistoryList) {
        SwingWorker<Void, RoundedPanel> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                dynamicSub = 0;
                dynamicTax = 0;
                dynamicPayable = 0;
                List<RoundedPanel> panels = new ArrayList<>();

                // Optimized processing: Use for-each loop with rowIndex tracking
                int rowIndex = 0;
                for (OrderHistoryLists item : orderHistoryList) {
                    // Pass the item and the rowIndex to the method
                    RoundedPanel orderPanel = createOrderPanelForOrderHist(item, rowIndex);
                    panels.add(orderPanel);

                    // Calculate dynamic subtotal
                    dynamicSub += (item.getOrderQty() * item.getOrderPrice1Qty());

                    // Increment rowIndex for the next item
                    rowIndex++;
                }

                // Calculate tax and total only once after processing all items
                dynamicTax = dynamicSub * TAX_PERCENTAGE;
                dynamicPayable = dynamicTax + dynamicSub;

                // Publish the panels for UI update
                publish(panels.toArray(new RoundedPanel[0]));
                return null;
            }

            @Override
            protected void process(List<RoundedPanel> chunks) {
                // Add all panels in bulk
                orderRightCenterOrderPanel.removeAll();
                for (RoundedPanel panel : chunks) {
                    orderRightCenterOrderPanel.add(panel);
                }
            }

            @Override
            protected void done() {
                // Update the panel layout only once after all panels are added
                orderRightCenterOrderPanel.revalidate();
                orderRightCenterOrderPanel.repaint();
                updateOrderCost(dynamicSub, dynamicTax, dynamicPayable);
            }
        };
        worker.execute();
    }


    protected void handleButtonActionForOrderNavigation(RoundedBorderButton button) {
        // Reset the previously clicked button's color and border
        if (lastClickedButton != null && lastClickedButton != button) {
            resetButtonColorsForNavigationButtons(); // Reset all buttons' colors
        }

        // Set the current button as the last clicked button
        lastClickedButton = button;

        // Apply active state to the new button
        animateBorderColor(button);

        // Update the order history panel based on the selected button
        updateOrderHistPanel(false); // Call method to refresh the panel content

        clearOrderDisplayPane();
    }


    private static List<OrderSummary> getOrderSummary() {
        String query = "{CALL vw_OrderSummary}";

        try (Connection conn = DBConnection.getConnection(); CallableStatement stmt = conn.prepareCall(query)) {

            try (ResultSet rs = stmt.executeQuery()) {
                List<OrderSummary> results = new ArrayList<>();
                while (rs.next()) {
                    OrderSummary summary = new OrderSummary(
                            rs.getString("OrderCode"),
                            rs.getTimestamp("StartDate"),
                            rs.getTimestamp("EndDate"),
                            rs.getString("OrderFinish"),
                            rs.getBigDecimal("OrderSubTotal"),
                            rs.getString("DineInToGoDelivery"));
                    results.add(summary);
                }
                return results;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    // Utility to create a configured JScrollPane
    private static JScrollPane createConfiguredScrollPane(JPanel contentPanel) {
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUI(new MacOSScrollPane.MacOSScrollBarUI(Color.WHITE));
        return scrollPane;
    }

    // Utility to create the main order list panel
    private static RoundedPanel createMainOrderListPanel(List<OrderSummary> summaries) {
        RoundedPanel listPanel = new RoundedPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        listPanel.setBackground(Color.WHITE);
        listPanel.add(createHeaderPanel());

        for (int i = 0; i < summaries.size(); i++) {
            OrderSummary summary = summaries.get(i);
            RoundedPanel orderRow = createOrderHistoryRowPanel(summary, i);
            listPanel.add(orderRow);
        }
        return listPanel;
    }


    protected static List<OrderSummary> orderSummaries;
    public static List<OrderHistoryLists> orderHistoryLists;

    public static void updateOrderHistoryArray() {
        FetchOrderDetails orderHistoryList = new FetchOrderDetails();
        orderHistoryLists = orderHistoryList.getLists();
    }

    public static void updateOrderSummaryArray() {
        orderSummaries = getOrderSummary();
    }


    protected JPanel createOrderHistPanel() {
        RoundedPanel orderHistMainPanel = new RoundedPanel(new BorderLayout());
        orderHistMainPanel.setPreferredSize(new Dimension(500, 680));
        orderHistMainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        orderHistMainPanel.setBackground(Color.WHITE);

        updateOrderHistoryArray();
        updateOrderSummaryArray();


        RoundedPanel mainOrderList = createMainOrderListPanel(orderSummaries);

        orderHistMainInnerPanel = new RoundedPanel(new BorderLayout());
        orderHistMainInnerPanel.setOpaque(false);
        orderHistMainInnerPanel.setBackground(Color.WHITE);
        orderHistMainInnerPanel.add(mainOrderList, BorderLayout.CENTER);

        JScrollPane scrollPane = createConfiguredScrollPane(orderHistMainInnerPanel);
        JPanel scrollPaneWrapper = new JPanel(new BorderLayout());
        scrollPaneWrapper.setOpaque(false);
        scrollPaneWrapper.setBackground(Color.WHITE);
        scrollPaneWrapper.add(scrollPane, BorderLayout.CENTER);

        orderHistMainPanel.add(scrollPaneWrapper, BorderLayout.CENTER);
        return orderHistMainPanel;
    }


    public void clearOrderDisplayPane() {
        SwingUtilities.invokeLater(() -> {
            orderRightCenterOrderPanel.removeAll();
            clearNumbersOfOrder();
            resetFont(lastClickedLabel);
            setCustomerPanelVisibilityOnOrderPage(false, "", "");
            setTableChangePanelVisibilityOnOrderPage(false, "", "");
            orderRightCenterOrderPanel.revalidate();
            orderRightCenterOrderPanel.repaint();
        });
    }

    private static JPanel createLoadingPanel() {
        JPanel loadingPanel = new JPanel(new GridBagLayout());
        loadingPanel.setBackground(Color.WHITE);

        JLabel loadingLabel = new JLabel("LOADING");
        loadingLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 12));
        loadingPanel.add(loadingLabel);

        return loadingPanel;
    }


    protected static String getVarOfOrderNavigationButton() {
        if (lastClickedButton == null) {
            return null; // Return null if no button is clicked
        }

        return switch (lastClickedButton.getText()) {
            case "Order History" -> null; // null indicates to select all orders
            case "Order On Hold" -> "N";  // "N" indicates the orders that are on hold
            case "Finished" -> "Y";       // "Y" indicates the orders that are finished
            default -> null; // Default to null if the button text doesn't match
        };
    }


    public static void updateOrderHistPanel(boolean isInit) {
        JPanel loadingPanel = createLoadingPanel();

        mainOrderPane.removeAll();
        mainOrderPane.add(loadingPanel);
        mainOrderPane.revalidate();
        mainOrderPane.repaint();

        SwingWorker<List<OrderSummary>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<OrderSummary> doInBackground() {

                if (isInit || getVarOfOrderNavigationButton() == null) {
                    return orderSummaries;
                } else {
                    String filterCondition = Objects.requireNonNull(getVarOfOrderNavigationButton());
                    return orderSummaries.stream()
                            .filter(orderSummary -> orderSummary.getOrderFinish().contains(filterCondition))
                            .toList();
                }
            }

            @Override
            protected void done() {
                try {
                    List<OrderSummary> summaries = new ArrayList<>(get());
                    RoundedPanel mainOrderList = createMainOrderListPanel(summaries);

                    orderHistMainInnerPanel = new RoundedPanel(new BorderLayout());
                    orderHistMainInnerPanel.setBackground(Color.WHITE);
                    orderHistMainInnerPanel.add(mainOrderList, BorderLayout.CENTER);

                    JScrollPane scrollPane = createConfiguredScrollPane(orderHistMainInnerPanel);
                    JPanel scrollPaneWrapper = new JPanel(new BorderLayout());
                    scrollPaneWrapper.setBackground(Color.WHITE);
                    scrollPaneWrapper.add(scrollPane, BorderLayout.CENTER);


                    mainOrderPane.removeAll();
                    mainOrderPane.add(scrollPaneWrapper);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mainOrderPane.revalidate();
                mainOrderPane.repaint();
            }
        };
        worker.execute();
    }


    private static void resetButtonColorsForNavigationButtons() {
        for (RoundedBorderButton button : navigationButtons) {
            button.setBorderColor(COLOR_WHITE);
            button.setForeground(COLOR_BLACK);
            button.setOverrideBackgroundColor(COLOR_WHITE);
            button.setOpaque(false);
            button.setContentAreaFilled(false);
        }
    }

    // Method to create the search panel with an icon and text field
    protected PanelRound createOrderPageTopSearchPanel() {
        SearchTopPaneCreator topSearchPanel = new SearchTopPaneCreator("Search Order ID.", "/SearchIcon.svg");
        JTextField searchField = topSearchPanel.getSearchTextField();

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterOrder(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterOrder(searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterOrder(searchField.getText());
            }
        });

        return topSearchPanel;
    }

    protected void filterOrder(String query) {
        JPanel loadingPanel = createLoadingPanel();
        mainOrderPane.removeAll();
        mainOrderPane.add(loadingPanel);
        mainOrderPane.revalidate();
        mainOrderPane.repaint();

        SwingUtilities.invokeLater(() -> {
            List<OrderSummary> filteredSummaries = orderSummaries.stream()
                    .filter(summary ->
                            (summary.getOrderCode() != null && summary.getOrderCode().toLowerCase().contains(query.toLowerCase())) ||
                                    (summary.getOrderMode() != null && summary.getOrderMode().toLowerCase().contains(query.toLowerCase()))
                    )
                    .toList();

            RoundedPanel mainOrderList = createMainOrderListPanel(filteredSummaries);

            orderHistMainInnerPanel = new RoundedPanel(new BorderLayout());
            orderHistMainInnerPanel.setBackground(Color.WHITE);
            orderHistMainInnerPanel.add(mainOrderList, BorderLayout.CENTER);

            JScrollPane scrollPane = createConfiguredScrollPane(orderHistMainInnerPanel);
            JPanel scrollPaneWrapper = new JPanel(new BorderLayout());
            scrollPaneWrapper.setBackground(Color.WHITE);
            scrollPaneWrapper.add(scrollPane, BorderLayout.CENTER);

            mainOrderPane.removeAll();
            mainOrderPane.add(scrollPaneWrapper);
            mainOrderPane.revalidate();
            mainOrderPane.repaint();
        });
    }


    protected JPanel createOrderRightTopPanel() {
        JPanel orderRightTopPanel = new JPanel(new BorderLayout()); // Set horizontal and vertical gaps
        orderRightTopPanel.setPreferredSize(new Dimension(300, 60));
        orderRightTopPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        orderRightTopPanel.setOpaque(false);

        JPanel orderTopRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); // Set horizontal and vertical gaps
        orderTopRight.setOpaque(false);
        orderTopRight.setPreferredSize(new Dimension(155, 60));
        orderTopRight.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        ImageIcon reloadButtonIcon = new SvgIcon("/ReloadIcon.svg", 22).getImageIcon();
        RoundedIconButton reloadButton = new RoundedIconButton(reloadButtonIcon, 14, COLOR_GRAY, 0);
        reloadButton.setPreferredSize(new Dimension(38, 38));
        reloadButton.setBackground(COLOR_GRAY);
        reloadButton.addActionListener(e -> clearOrderDisplayPane());

        orderTopRight.add(reloadButton);

        JPanel homeTopLeft = new JPanel(new BorderLayout());
        homeTopLeft.setBackground(COLOR_WHITE);
        homeTopLeft.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 0));
        homeTopLeft.setPreferredSize(new Dimension(135, 60));

        homeTopLeft.add(createInitTableInfoOnOrderPage(initTableNameText, initTableID), BorderLayout.WEST);

        JPanel homeMiddleTop = new JPanel(new BorderLayout());
        homeMiddleTop.setOpaque(false);
        homeMiddleTop.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 5));
        homeMiddleTop.add(createInitCustomerInfoOnOrderPage(initCustomerNameText, initCustomerCID), BorderLayout.CENTER);

        orderRightTopPanel.add(homeTopLeft, BorderLayout.WEST);
        orderRightTopPanel.add(homeMiddleTop, BorderLayout.CENTER);
        orderRightTopPanel.add(orderTopRight, BorderLayout.EAST);

        return orderRightTopPanel;
    }

    public static void setCustomerPanelVisibilityOnOrderPage(boolean visible, String lastClickedCustomerNameUpdated, String lastClickedCustomerCID) {
        isVisibleCustomer = visible;
        if (panelForCustomerSelect != null) {
            customerLabel.setText(lastClickedCustomerNameUpdated);
            customerCIDLabel.setText(lastClickedCustomerCID);
            panelForCustomerSelect.setVisible(isVisibleCustomer);
            panelForCustomerSelect.revalidate();
            panelForCustomerSelect.repaint();
        }
    }

    public static void setTableChangePanelVisibilityOnOrderPage(boolean visible, String lastClickedTable, String lastClickedTableID) {
        isVisibleTable = visible;
        if (panelForTableSelected != null) {
            tableLabel.setText(lastClickedTable);
            tableIDLabel.setText(lastClickedTableID);
            panelForTableSelected.setVisible(isVisibleTable);
            panelForTableSelected.revalidate();
            panelForTableSelected.repaint();
        }
    }

    private RoundedPanel createInitCustomerInfoOnOrderPage(String lastClickedCustomerName, String cid) {
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
        buttonPanel.add(getChangeButtonCustomerInfo(), BorderLayout.CENTER);

        // Add components to the main panel
        panelForCustomerSelect.add(customerPanel, BorderLayout.CENTER);
        panelForCustomerSelect.add(buttonPanel, BorderLayout.EAST);

        panelForCustomerSelect.setVisible(isVisibleCustomer);

        return panelForCustomerSelect;
    }

    private RoundedPanel createInitTableInfoOnOrderPage(String lastClickedTable, String tableID) {
        panelForTableSelected = new RoundedPanel(20);
        panelForTableSelected.setLayout(new BorderLayout());
        panelForTableSelected.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        panelForTableSelected.setOpaque(false);
        panelForTableSelected.setBackground(COLOR_PANEL_GRAY);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
        tablePanel.setOpaque(false);
        tablePanel.setPreferredSize(new Dimension(80, 60));

        tableIDLabel = new JLabel(tableID);
        tableLabel = new JLabel(lastClickedTable);
        tableLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 13));

        tablePanel.add(tableLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(2, 1, 3, 3));
        buttonPanel.setPreferredSize(new Dimension(40, 38));
        buttonPanel.add(getChangeButtonTableInfo(), BorderLayout.CENTER);

        // Add components to the main panel
        panelForTableSelected.add(tablePanel, BorderLayout.CENTER);
        panelForTableSelected.add(buttonPanel, BorderLayout.EAST);

        panelForTableSelected.setVisible(isVisibleTable);

        return panelForTableSelected;
    }

    private JButton getChangeButtonTableInfo() {
        JButton changeButton = new JButton(createResizedIcon("/ChangeIcon.svg", 22, COLOR_WHITE));
        CreateHomePanel.configureButtonStyle(changeButton, backgroundColor);

        changeButton.addActionListener(e -> {
            MiniDevPOS.showPanel("SelectTables");
        });

        return changeButton;
    }

    private JButton getChangeButtonCustomerInfo() {
        JButton changeButton = new JButton(createResizedIcon("/ChangeIcon.svg", 22, COLOR_WHITE));
        CreateHomePanel.configureButtonStyle(changeButton, backgroundColor);

        changeButton.addActionListener(e -> {
            MiniDevPOS.showPanel("Customers");
        });

        return changeButton;
    }


    protected JPanel createOrderTopLayerPanel() {
        JPanel orderButtonTopLayerPanelOrder = new JPanel();
        orderButtonTopLayerPanelOrder.setLayout(new GridLayout(2, 1, 0, 0));
        Border topLayerTopBorderOrder = BorderFactory.createMatteBorder(1, 0, 0, 0, Color.decode("#E8E8E8"));
        orderButtonTopLayerPanelOrder.setBorder(topLayerTopBorderOrder);

        JPanel subtotalOfOrderListsPanelOrder = new JPanel(new GridLayout(1, 2, 0, 0));
        subtotalOfOrderListsPanelOrder.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        JPanel taxOfOrderListsPanelOrder = new JPanel();

        // Creating left label panel with GridBagLayout for precise alignment control
        JPanel leftLabelPanelOrder = new JPanel(new GridBagLayout());

        JLabel subtotalLabelOrder = new JLabel("SubTotal");
        subtotalLabelOrder.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13));
        subtotalLabelOrder.setVerticalTextPosition(SwingConstants.CENTER);

        // Setting GridBagConstraints for left-center alignment
        GridBagConstraints leftGbcOrder = new GridBagConstraints();
        leftGbcOrder.anchor = GridBagConstraints.WEST; // Align left
        leftGbcOrder.fill = GridBagConstraints.VERTICAL; // Allow vertical centering
        leftGbcOrder.weightx = 1; // Take all horizontal space
        subtotalOfOrderListsPanelOrder.add(subtotalLabelOrder, leftGbcOrder);

        // Creating left label panel with GridBagLayout for precise alignment control
        JPanel rightNumberPanelOrder = new JPanel(new GridBagLayout());

        subTotalNumberOrder.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 13));
        subTotalNumberOrder.setVerticalTextPosition(SwingConstants.CENTER);

        // Setting GridBagConstraints for right-center alignment
        GridBagConstraints rightGbcOrder = new GridBagConstraints();
        rightGbcOrder.anchor = GridBagConstraints.EAST; // Align right
        rightGbcOrder.fill = GridBagConstraints.VERTICAL; // Allow vertical centering
        rightGbcOrder.weightx = 1; // Take all horizontal space
        rightGbcOrder.gridx = 1; // Place the label in the second column
        rightGbcOrder.insets = new Insets(0, 0, 0, 10); // Add some padding to the right

        rightNumberPanelOrder.add(subTotalNumberOrder, rightGbcOrder);

        subtotalOfOrderListsPanelOrder.add(leftLabelPanelOrder);
        subtotalOfOrderListsPanelOrder.add(rightNumberPanelOrder);

        orderButtonTopLayerPanelOrder.add(subtotalOfOrderListsPanelOrder);

        subtotalOfOrderListsPanelOrder = new JPanel(new GridLayout(1, 2, 0, 0));
        subtotalOfOrderListsPanelOrder.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        taxOfOrderListsPanelOrder = new JPanel(new GridLayout(1, 2, 0, 0));

        JPanel leftTaxLabelPanelOrder = new JPanel(new GridBagLayout());
        leftTaxLabelPanelOrder.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        JLabel leftTaxLabelOrder = new JLabel("Tax");
        leftTaxLabelOrder.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13));
        leftTaxLabelOrder.setVerticalTextPosition(SwingConstants.CENTER);

        leftTaxLabelPanelOrder.add(leftTaxLabelOrder, leftGbcOrder);


        JPanel rightTaxLabelPanelOrder = new JPanel(new GridBagLayout());
        rightTaxLabelPanelOrder.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        taxTotalNumberOrder.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 13));
        taxTotalNumberOrder.setVerticalTextPosition(SwingConstants.CENTER);

        rightTaxLabelPanelOrder.add(taxTotalNumberOrder, rightGbcOrder);

        taxOfOrderListsPanelOrder.add(leftTaxLabelPanelOrder);
        taxOfOrderListsPanelOrder.add(rightTaxLabelPanelOrder);

        orderButtonTopLayerPanelOrder.add(taxOfOrderListsPanelOrder);

        return orderButtonTopLayerPanelOrder;
    }

    protected JPanel createCenterLayerPanelOrder() {
        JPanel orderButtonCenterLayerPanelOrder = new JPanel(new GridLayout(1, 2, 2, 2));

        // Creating left label panel with GridBagLayout for precise alignment control
        JPanel leftLabelPanelOrder = new JPanel(new GridBagLayout());
        leftLabelPanelOrder.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        JLabel payableAmountLabelOrder = new JLabel("Payable Amount");
        payableAmountLabelOrder.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 15));
        payableAmountLabelOrder.setVerticalTextPosition(SwingConstants.CENTER);

        // Setting GridBagConstraints for left-center alignment
        GridBagConstraints leftGbcOrder = new GridBagConstraints();
        leftGbcOrder.anchor = GridBagConstraints.WEST; // Align left
        leftGbcOrder.fill = GridBagConstraints.VERTICAL; // Allow vertical centering
        leftGbcOrder.weightx = 1; // Take all horizontal space
        leftLabelPanelOrder.add(payableAmountLabelOrder, leftGbcOrder);

        // Creating right label panel with GridBagLayout for precise alignment control
        JPanel rightSubtotalLabelPanelOrder = new JPanel(new GridBagLayout());
        rightSubtotalLabelPanelOrder.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));


        payableAmountNumberOrder.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 15));
        payableAmountNumberOrder.setVerticalTextPosition(SwingConstants.CENTER);

        // Setting GridBagConstraints for right-center alignment
        GridBagConstraints rightGbcOrder = new GridBagConstraints();
        rightGbcOrder.anchor = GridBagConstraints.EAST; // Align right
        rightGbcOrder.fill = GridBagConstraints.VERTICAL; // Allow vertical centering
        rightGbcOrder.weightx = 1; // Take all horizontal space
        rightGbcOrder.gridx = 1; // Place the label in the second column
        rightGbcOrder.insets = new Insets(0, 0, 0, 10); // Add some padding to the right

        rightSubtotalLabelPanelOrder.add(payableAmountNumberOrder, rightGbcOrder);

        orderButtonCenterLayerPanelOrder.add(leftLabelPanelOrder);
        orderButtonCenterLayerPanelOrder.add(rightSubtotalLabelPanelOrder);

        return orderButtonCenterLayerPanelOrder;
    }


    protected JPanel createOrderProceedPanelOrder() {
        JPanel orderButtonBottomLayerPanelOrder = new JPanel();
        orderButtonBottomLayerPanelOrder.setLayout(new GridLayout(1, 3, 6, 2));
        orderButtonBottomLayerPanelOrder.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        ImageIcon printIconOrder = IconCreator.createResizedIcon("/PrintIcon.svg", 14, null);
        JButton orderListPrintButtonOrder = new IconTextButton("Print", printIconOrder, 14, (Color.decode("#4361ee")), 0);
        orderListPrintButtonOrder.setBackground(Color.decode("#4361ee"));
        orderListPrintButtonOrder.setForeground(Color.WHITE);
        orderListPrintButtonOrder.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));
        orderListPrintButtonOrder.setPreferredSize(new Dimension(130, 38)); // Adjusted size for visibility

        // Set icon on the left side of the button text
        orderListPrintButtonOrder.setIcon(printIconOrder);
        // Set the text and icon alignment
        orderListPrintButtonOrder.setHorizontalTextPosition(SwingConstants.RIGHT); // Icon on the left
        orderListPrintButtonOrder.setVerticalTextPosition(SwingConstants.CENTER);
        orderListPrintButtonOrder.setHorizontalAlignment(SwingConstants.CENTER);
        orderListPrintButtonOrder.setVerticalAlignment(SwingConstants.CENTER);

        ImageIcon cancelTrnIcon = IconCreator.createResizedIcon("/CancelTransactionIcon.svg", 20, null);
        JButton cancelTrnButton = new IconTextButton("Cancel Order", cancelTrnIcon, 14, (Color.decode("#e11d48")), 0);
        cancelTrnButton.setBackground(Color.decode("#e11d48"));
        cancelTrnButton.setForeground(Color.WHITE);
        cancelTrnButton.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));
        cancelTrnButton.setPreferredSize(new Dimension(130, 38)); // Adjusted size for visibility

        // Set icon on the left side of the button text
        cancelTrnButton.setIcon(cancelTrnIcon);
        // Set the text and icon alignment
        cancelTrnButton.setHorizontalTextPosition(SwingConstants.RIGHT); // Icon on the left
        cancelTrnButton.setVerticalTextPosition(SwingConstants.CENTER);
        cancelTrnButton.setHorizontalAlignment(SwingConstants.CENTER);
        cancelTrnButton.setVerticalAlignment(SwingConstants.CENTER);


        ImageIcon dbUpdateIcon = IconCreator.createResizedIcon("/DBUpdateIcon.svg", 14, null);
        JButton updateButton = new IconTextButton("Update Order", dbUpdateIcon, 14, (Color.decode("#09AA29")), 0);
        updateButton.setBackground(Color.decode("#09AA29"));
        updateButton.setForeground(Color.WHITE);
        updateButton.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));
        updateButton.setPreferredSize(new Dimension(130, 38)); // Adjusted size for visibility

        // Set icon on the left side of the button text
        updateButton.setIcon(dbUpdateIcon);
        // Set the text and icon alignment
        updateButton.setHorizontalTextPosition(SwingConstants.RIGHT); // Icon on the left
        updateButton.setVerticalTextPosition(SwingConstants.CENTER);
        updateButton.setHorizontalAlignment(SwingConstants.CENTER);
        updateButton.setVerticalAlignment(SwingConstants.CENTER);


        orderListPrintButtonOrder.addActionListener(e -> {
            if (payableAmountNumberOrder.getText().equals("0 Ks")) {
                DialogCollection.showCustomDialog(mainAllHoldingPanel, "No vouchers available to display.", "No Vouchers");
            } else {
                CreatePaymentPanel.showVoucherForOrder(getOrderCodeToUpdate());
            }
        });

        cancelTrnButton.addActionListener(e -> {
            if (payableAmountNumberOrder.getText().equals("0 Ks")) {
                DialogCollection.showCustomDialog(mainAllHoldingPanel, "No order available to cancel.", "Order ID Invalid");
            } else {
                cancelTrnButtonEvent(getCancelOrderCode());
            }

        });

        updateButton.addActionListener(e -> {
            if (payableAmountNumberOrder.getText().equals("0 Ks")) {
                DialogCollection.showCustomDialog(mainAllHoldingPanel, "No order available to update.", "Order ID Invalid");
            } else {
                updateButtonEvent(getOrderCodeToUpdate());
            }
        });

        orderButtonBottomLayerPanelOrder.add(orderListPrintButtonOrder);
        orderButtonBottomLayerPanelOrder.add(cancelTrnButton);
        orderButtonBottomLayerPanelOrder.add(updateButton);

        return orderButtonBottomLayerPanelOrder;
    }

    private void updateButtonEvent(String lastClickedOrderCode) {
        if (isFinishedOrder(lastClickedOrderCode)) {
            doUpdateForFinishedOrder();
        } else {
            doUpdateForActiveOrder(lastClickedOrderCode);
            updateProductsLists();
        }

        clearOrderDisplayPane();
        updateOrderHistoryArray();
        updateOrderSummaryArray();
        CreateCustomerPanel.refreshCustomerInfoLists();
    }

    private boolean isFinishedOrder(String lastClickedOrderCode) {
        OrderSummary orderSummary = orderSummaries.stream()
                .filter(code -> code.getOrderCode().equals(lastClickedOrderCode))
                .findFirst().get();
        return orderSummary.getOrderFinish().equals("Y");
    }

    private void doUpdateForFinishedOrder() {
        JOptionPane.showMessageDialog(orderMainPanel, "This order is already finished...");
    }

    private void doUpdateForActiveOrder(String lastClickedOrderCode) {
        SwingWorker<List<OrderHistoryLists>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<OrderHistoryLists> doInBackground() {
                // Filter and collect as a list (immutable by default)
                return remainingLists;
            }

            @Override
            protected void done() {
                try {
                    // Fetch result from doInBackground and prepare data for update
                    List<OrderHistoryLists> updatedOrderList = get();
                    String tableNameToUpdate = tableLabel.getText();
                    String customerNameToUpdate = customerLabel.getText();

                    // Update active order in the database
                    batchUpdateActiveOrderIntoDB(lastClickedOrderCode, customerNameToUpdate, tableNameToUpdate, updatedOrderList);

                    // Refresh UI components that rely on updated data
                    refreshUIComponents();

                    // Notify user of successful update
                    JOptionPane.showMessageDialog(orderMainPanel, "Order code " + lastClickedOrderCode + " has been successfully updated.");
                } catch (Exception e) {
                    // Log any errors that occur during the update process
                    logError(e);
                }
            }
        };
        worker.execute();
    }

    private void refreshUIComponents() {
        // Combines multiple UI updates into one for efficiency
        updateProductsLists();
        new StockInnerPage().refreshTableAfterItemsUpdater();
        CreateCustomerPanel.refreshCustomerInfoLists();
        allClearCustomerInfoOrderCodeVisibility();
        updateOrderHistoryArray();
        updateOrderSummaryArray();
    }

    private void logError(Exception e) {
        // Uses a logger to handle errors professionally
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error updating active order", e);
    }


    private static void batchUpdateActiveOrderIntoDB(String orderCode, String newCustomerName, String newTableName, List<OrderHistoryLists> updatedOrderList) {
        String orderItemsJson = getUpdatedItemsJson(updatedOrderList);

        // Connect to MySQL and call the stored procedure
        try (Connection connection = DBConnection.getConnection(); CallableStatement stmt = connection.prepareCall("{CALL sp_UpdateActiveOrder(?, ?, ?, ?,?)}")) {

            // Set the parameters and execute the stored procedure
            stmt.setString(1, orderCode);
            stmt.setString(2, newCustomerName);
            stmt.setString(3, newTableName);
            stmt.setString(4, orderItemsJson);
            stmt.setBigDecimal(5, BigDecimal.valueOf(thisOrderTotalNumber));

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected static int thisOrderTotalNumber;

    private static String getUpdatedItemsJson(List<OrderHistoryLists> updatedOrderList) {
        Gson gson = new Gson();
        JsonArray updateOrderItems = new JsonArray();
        thisOrderTotalNumber = 0;
        for (OrderHistoryLists orderHistoryLists : updatedOrderList) {

            String productSerialCode = orderHistoryLists.getFoodSerialCode();
            String orderFoodName = orderHistoryLists.getOrderFood();
            int quantity = orderHistoryLists.getOrderQty();
            int subTotalPrice = (int) (orderHistoryLists.getOrderSubTotal());
            String orderNotes = orderHistoryLists.getOrderNote();

            thisOrderTotalNumber += subTotalPrice;

            JsonObject oneItem = new JsonObject();
            oneItem.addProperty("SerialCode", productSerialCode);
            oneItem.addProperty("OrderFood", orderFoodName);
            oneItem.addProperty("OrderQty", quantity);
            oneItem.addProperty("OrderPrice1Qty", subTotalPrice / quantity);
            oneItem.addProperty("OrderNote", orderNotes);
            oneItem.addProperty("OrderSubTotal", subTotalPrice);

            updateOrderItems.add(oneItem);
        }

        return gson.toJson(updateOrderItems);
    }


    private void cancelTrnButtonEvent(String lastClickedOrderCode) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                OrderHistoryLists orderToCancel = orderHistoryLists.stream()
                        .filter(orderRow -> orderRow.getOrderCode().contains(lastClickedOrderCode))
                        .findFirst()
                        .orElse(null);

                if (orderToCancel != null) {
                    // Ask for confirmation before proceeding
                    int confirmation = JOptionPane.showConfirmDialog(orderMainPanel,
                            "Are you sure you want to cancel the order code " + lastClickedOrderCode + "?",
                            "Confirm Cancellation",
                            JOptionPane.YES_NO_OPTION);

                    if (confirmation == JOptionPane.YES_OPTION) {
                        processOrderCancellation(lastClickedOrderCode, orderToCancel);
                    }
                }

                updateOrderHistoryArray();
                updateOrderSummaryArray();
                CreateCustomerPanel.refreshCustomerInfoLists();

                return null; // Return type needs to be Void
            }

            @Override
            protected void done() {
                try {
                    // Optionally, handle UI updates here if needed.
                    clearOrderDisplayPane();
                    JOptionPane.showMessageDialog(orderMainPanel,
                            "The order code " + lastClickedOrderCode + " has been cancelled.");
                } catch (Exception e) {
                    handleException(e);
                }
            }
        };
        worker.execute();
    }

    private void processOrderCancellation(String orderCode, OrderHistoryLists order) {
        String tableName = order.getTableName();
        int batchUser = CreateCashDrawerPanel.tellerID;
        batchUpdateCancelOrderIntoDB(orderCode, batchUser, tableName, TAX_PERCENTAGE);
        updateProductsLists();
    }

    private static void batchUpdateCancelOrderIntoDB(String orderCode, int batchUser, String tableName, double taxPercentage) {
        String sql = "{CALL sp_CancelOrder(?, ?, ?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, orderCode);
            stmt.setInt(2, batchUser);
            stmt.setString(3, tableName);
            stmt.setDouble(4, taxPercentage);

            stmt.execute();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    private static void handleException(Exception e) {
        // Implement your logging framework here
        e.printStackTrace();
        // Optionally show a user-friendly message
        JOptionPane.showMessageDialog(orderMainPanel, "An error occurred: " + e.getMessage());
    }


    private String getCancelOrderCode() {
        return LAST_CLICKED_ORDER_CODE.getText();
    }

    private String getOrderCodeToUpdate() {
        return LAST_CLICKED_ORDER_CODE.getText();
    }

}
