package org.MiniDev.Table;

import DBConnection.DBConnection;
import UI.*;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import net.miginfocom.swing.MigLayout;
import org.MiniDev.Home.MiniDevPOS;
import org.MiniDev.Payment.CreatePaymentPanel;
import org.MiniDev.Home.CreateHomePanel;
import org.MiniDev.OOP.Table;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static UI.UserFinalSettingsVar.*;
import static org.MiniDev.Home.CreateHomePanel.*;

public class CreateTablePanel extends JPanel {

    protected static String[] buttonNames = {"All Tables", "Vacant", "Occupied", "Disabled" };
    protected static RoundedBorderButton[] buttons; // Array to hold references to all buttons
    protected static List<Table> tablesLists = new ArrayList<>();
    protected static RoundedBorderButton lastClickedButtonOfTableCategory;
    protected static JPanel tableHoldingPanel;
    protected static JPanel bottomPanel;
    protected static JButton paymentButton;
    public static JPanel selectTableAndPaymentHoldingMainPane;
    public static CardLayout selectTableAndPaymentCard;
    protected static JPanel selectTableHoldingMainPanel;
    public static JLabel tableNameLabel = new JLabel("");
    public static JLabel orderCode = new JLabel("");
    protected static JPanel paymentPanel;
    public static String lastClickedOrderCode = "";
    protected static JPanel tableAndOrderCodePanel;
    public static JLabel lastClickedOrderLabel = new JLabel(); // or initialize it wherever needed
    public static CreatePaymentPanel thisPaymentFromTableCreation;

    public CreateTablePanel() {
        thisPaymentFromTableCreation = new CreatePaymentPanel();
    }


    //Select Table
    public JPanel selectTablesPanel() {

        selectTableAndPaymentHoldingMainPane = new JPanel();
        selectTableAndPaymentCard = new CardLayout();
        selectTableAndPaymentHoldingMainPane.setLayout(selectTableAndPaymentCard);

        selectTableHoldingMainPanel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        centerPanel.setBackground(COLOR_GRAY);
        centerPanel.setPreferredSize(new Dimension(555, 50));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 15));

        RoundedPanel tableTopNavigationPanel = new RoundedPanel(10);
        tableTopNavigationPanel.setLayout(new BoxLayout(tableTopNavigationPanel, BoxLayout.X_AXIS));
        tableTopNavigationPanel.setBackground(Color.WHITE);
        tableTopNavigationPanel.setPreferredSize(new Dimension(520, 40));
        tableTopNavigationPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 170));
        tableTopNavigationPanel.setOpaque(false);

        buttons = new RoundedBorderButton[buttonNames.length];

        for (int i = 0; i < buttonNames.length; i++) {
            RoundedBorderButton navigateToTableCategory = getNaviButton(i);

            buttons[i] = navigateToTableCategory;
            tableTopNavigationPanel.add(navigateToTableCategory);
        }

        startAllTablesButtonAnimation();

        centerPanel.add(tableTopNavigationPanel, BorderLayout.NORTH);

        bottomPanel = createBottomLabelPanel();
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setPreferredSize(new Dimension(700, 100));

        tableHoldingPanel = new JPanel(new BorderLayout());
        tableHoldingPanel.setBorder(BorderFactory.createEmptyBorder(9, 8, 0, 8));
        tableHoldingPanel.setBackground(COLOR_GRAY);

        tableHoldingPanel.add(createTableLists(), BorderLayout.CENTER);
        selectTableHoldingMainPanel.add(centerPanel, BorderLayout.NORTH);
        selectTableHoldingMainPanel.add(tableHoldingPanel, BorderLayout.CENTER);
        selectTableHoldingMainPanel.add(bottomPanel, BorderLayout.SOUTH);


        paymentPanel = thisPaymentFromTableCreation.createPaymentPanel("SelectTablesPanel", lastClickedOrderLabel, customerInfo, tableInfo);

        selectTableAndPaymentHoldingMainPane.add(selectTableHoldingMainPanel, "SelectTablesPanel");
        selectTableAndPaymentHoldingMainPane.add(paymentPanel, "PaymentPanel");

        return selectTableAndPaymentHoldingMainPane;
    }

    private static RoundedBorderButton getNaviButton(int i) {
        String buttonName = buttonNames[i];
        RoundedBorderButton navigateToTableCategory = new RoundedBorderButton(buttonName, 80, 30, COLOR_WHITE);
        navigateToTableCategory.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 11));
        navigateToTableCategory.setRoundedArcSize(10);
        navigateToTableCategory.setHorizontalTextPosition(SwingConstants.CENTER);
        navigateToTableCategory.setVerticalTextPosition(SwingConstants.CENTER);
        navigateToTableCategory.setMargin(new Insets(7, 4, 7, 4));
        navigateToTableCategory.setPreferredSize(new Dimension(80, 30));
        navigateToTableCategory.setPreferredSize(new Dimension(80, 30)); // Adjust dimensions as needed
        navigateToTableCategory.setMaximumSize(new Dimension(80, 30)); // Adjust dimensions as needed
        navigateToTableCategory.setMinimumSize(new Dimension(80, 30)); // Adjust dimensions as needed
        navigateToTableCategory.setBorder(BorderFactory.createEmptyBorder());
        navigateToTableCategory.setFocusPainted(true);
        navigateToTableCategory.setContentAreaFilled(false);
        navigateToTableCategory.setRolloverEnabled(false);

        navigateToTableCategory.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            lastClickedButtonOfTableCategory = navigateToTableCategory;
            refreshTableLists();
            resetButtonColors();
            animateBorderColor(lastClickedButtonOfTableCategory);
        }));
        return navigateToTableCategory;
    }

    private static void resetButtonColors() {
        for (RoundedBorderButton button : buttons) {
            button.setBorderColor(COLOR_WHITE);
            button.setOverrideBackgroundColor(COLOR_WHITE);
            button.setForeground(COLOR_BLACK);
            button.setOpaque(false);
            button.setContentAreaFilled(false);
        }
    }


    public static void refreshTableLists() {
        // Show a loading indicator while the data is being fetched
        if (!(tableHoldingPanel == null)) {
            tableHoldingPanel.removeAll();
            JLabel loadingLabel = new JLabel("LOADING");
            loadingLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12)); // Optional: customize the font
            loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
            tableHoldingPanel.add(loadingLabel);
            tableHoldingPanel.revalidate();
            tableHoldingPanel.repaint();

            // Use SwingUtilities.invokeLater to ensure the loading label is rendered before starting background task
            SwingUtilities.invokeLater(() -> {
                // Use SwingWorker to fetch and update the UI
                SwingWorker<JScrollPane, Void> worker = new SwingWorker<>() {
                    @Override
                    protected JScrollPane doInBackground() {
                        // No database call here, data is already in memory
                        // Create a new table list panel
                        return createTableLists();
                    }

                    @Override
                    protected void done() {
                        try {
                            // Remove the loading indicator and add the new table panel
                            tableHoldingPanel.removeAll();
                            tableHoldingPanel.add(get());

                            // Refresh the UI
                            tableHoldingPanel.revalidate();
                            tableHoldingPanel.repaint();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                };

                worker.execute(); // Start the background task
            });
        } else {
            System.out.println("...TableHoldingPanel is null");
        }

    }


    protected static void retrieveTableInfoFromDatabase() {
        String sql = sql();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            tablesLists.clear(); // Clear existing data before fetching new data

            while (rs.next()) {
                String tableName = rs.getString("TableName");
                char tableStatus = rs.getString("TableCurrentStatus").charAt(0);
                Timestamp startTimestamp = rs.getTimestamp("StartTimeStamp");
                Timestamp endTimestamp = rs.getTimestamp("EndTime");
                char cancelYN = rs.getString("CancelYN").charAt(0);
                char tempCacheYN = rs.getString("TempCacheYN").charAt(0);
                String currentOrderCode = rs.getString("CurrentOrderCode");
                String tempCacheCode = rs.getString("TempCacheCode");
                Date startDate = (startTimestamp != null) ? new Date(startTimestamp.getTime()) : null;
                Date endDate = (endTimestamp != null) ? new Date(endTimestamp.getTime()) : null;
                int tableID = rs.getInt("TableID");

                Table table = new Table(tableName, tableStatus, startDate, endDate, cancelYN, tempCacheYN, currentOrderCode, tempCacheCode, tableID);
                tablesLists.add(table);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider using a logger instead
        }
    }


    //Author : Alien
    protected static JScrollPane createTableLists() {
        retrieveTableInfoFromDatabase();

        // Create the main panel with MigLayout
        JPanel panel = new JPanel(new MigLayout("wrap 5, fill",
                "[grow, fill][grow, fill][grow, fill][grow, fill][grow, fill]", // 5 columns
                "[]15[]15[]15[]15[]")); // No top gap, followed by rows with 15px gap
        panel.setBackground(Color.decode("#f5f5f5"));

        List<Table> filteredTables = filterTables();
        int tableCount = filteredTables.size();

        // Add buttons for each table
        for (Table filteredTable : filteredTables) {
            JButton button = createTableLists(filteredTable);
            panel.add(button, "grow, push"); // Allow buttons to grow and fill the cell
        }

        // Fill remaining cells with empty components to maintain layout (up to 15 total)
        for (int i = tableCount; i < 15; i++) {
            JPanel placeholder = new JPanel();
            placeholder.setPreferredSize(new Dimension(120, 120)); // Set a preferred size for empty panels to maintain button size
            panel.add(placeholder, "grow, push"); // Use empty panels to maintain the grid layout
        }

        // Create the JScrollPane
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUI(new MacOSScrollPane.MacOSScrollBarUI(Color.decode("#f5f5f5")));

        return scrollPane;
    }


    private static List<Table> filterTables() {
        if (lastClickedButtonOfTableCategory == null || lastClickedButtonOfTableCategory.getText().equals("All Tables")) {
            return new ArrayList<>(tablesLists); // Return all tables
        }

        char status = switch (lastClickedButtonOfTableCategory.getText()) {
            case "Vacant" -> 'V';
            case "Occupied" -> 'O';
            case "Disabled" -> 'D';
            default ->
                    throw new IllegalArgumentException("Unexpected value: " + lastClickedButtonOfTableCategory.getText());
        };

        return tablesLists.stream()
                .filter(table -> table.getTableStatus() == status)
                .collect(Collectors.toList());
    }

    protected static String sql() {
        if (lastClickedButtonOfTableCategory == null) {
            return "SELECT * FROM Table_Lists"; // Default query to select all tables
        }

        return switch (lastClickedButtonOfTableCategory.getText()) {
            case "Vacant" -> "SELECT * FROM Table_Lists WHERE TableCurrentStatus = 'V'";
            case "Occupied" -> "SELECT * FROM Table_Lists WHERE TableCurrentStatus = 'O'";
            case "Disabled" -> "SELECT * FROM Table_Lists WHERE TableCurrentStatus = 'D'";
            default -> "SELECT * FROM Table_Lists"; // Default to selecting all tables
        };
    }

    //Author : Alien
    protected static JButton createTableLists(Table table) {
        // Create the RoundButton (assuming it extends JButton)
        RoundButton button = new RoundButton("", 160, 140);
        button.setBackground(Color.WHITE);
        button.setOpaque(false);
        button.setBorderPainted(false); // No border

        // Load table icon (adjust size dynamically if needed)
        ImageIcon icon = getTableIcon();
        JLabel photoLabel = new JLabel(icon);
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoLabel.setVerticalAlignment(SwingConstants.CENTER);
        photoLabel.setAlignmentX(0.5f); // Ensure horizontal centering within the overlay
        photoLabel.setAlignmentY(0.5f); // Ensure vertical centering within the overlay

        // Create the overlay text (HTML for styling)
        String currentOrderCode = table.getCurrentOrderCode() == null ? "" : table.getCurrentOrderCode();
        JLabel overlayLabel = new JLabel(
                "<html><center><font size='5'><b>" +
                        CreateHomePanel.wrapText(table.getTableName(), 10) +
                        "</b></font><br><font size='4'><u>" +
                        CreateHomePanel.wrapText(currentOrderCode, 10) +
                        "</u></font></center></html>"
        );
        overlayLabel.setForeground(COLOR_BLACK); // White text for visibility over the photo
        overlayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        overlayLabel.setVerticalAlignment(SwingConstants.CENTER);
        overlayLabel.setAlignmentX(0.5f); // Ensure horizontal centering
        overlayLabel.setAlignmentY(0.5f); // Ensure vertical centering

        // Create the overlay panel using OverlayLayout
        JPanel overlayPanel = new JPanel();
        overlayPanel.setLayout(new OverlayLayout(overlayPanel));
        overlayPanel.setOpaque(false); // Transparent to show the photo underneath

        // Add the photo and text to the overlay panel
        overlayPanel.add(overlayLabel); // Overlay label on top
        overlayPanel.add(photoLabel);   // Photo in the background

        // Create the bottom panel to indicate table status
        RoundedPanel bottomPanel = new RoundedPanel(5);
        switch (table.getTableStatus()) {
            case 'V' -> bottomPanel.setBackground(Color.decode("#09AA29")); // Green for vacant
            case 'O' -> bottomPanel.setBackground(Color.decode("#4361ee")); // Blue for occupied
            case 'D' -> bottomPanel.setBackground(Color.decode("#ff4c4c")); // Red for dirty
            default -> bottomPanel.setBackground(Color.LIGHT_GRAY);
        }
        bottomPanel.setPreferredSize(new Dimension(button.getWidth(), 7)); // Adjust height as needed

        // Use BorderLayout on the button to position components
        button.setLayout(new BorderLayout());
        button.add(overlayPanel, BorderLayout.CENTER); // Overlay content at the center
        button.add(bottomPanel, BorderLayout.SOUTH);   // Status indicator at the bottom

        // Action listener for button click
        button.addActionListener(e -> updateTableNoAndOrderCode(table));

        return button;
    }

    protected static ImageIcon getTableIcon() {
        // Load the image using a class loader to ensure it's found correctly in the classpath
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(TableLists.class.getResource("/SelectTableListsIcon.png")));
        Image image = imageIcon.getImage();

        int squareSize = 150; // Adjust the size of the square as needed
        double scaleFactor = (double) squareSize / Math.max(image.getWidth(null), image.getHeight(null));
        int scaledWidth = (int) (image.getWidth(null) * scaleFactor);
        int scaledHeight = (int) (image.getHeight(null) * scaleFactor);

        // Scale the image using Image.SCALE_SMOOTH
        Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);

        return new ImageIcon(scaledImage);
    }


    protected JPanel createBottomLabelPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());

        Border border = BorderFactory.createMatteBorder(1, 0, 0, 0, Color.decode("#E8E8E8"));
        bottomPanel.setBorder(border);

        bottomPanel.add(createLeftLabel(), BorderLayout.WEST);
        bottomPanel.add(createRightLabel(), BorderLayout.EAST);

        return bottomPanel;
    }

    protected static JPanel createLeftLabel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));

        JLabel disabledLabel = getDisabledImgLabel();

        JLabel occupiedLabel = getOccupiedImgLabel();

        JLabel vacantLabel = getVacantImgLabel();

        leftPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        leftPanel.add(createBox(disabledLabel));
        leftPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        leftPanel.add(createBox(occupiedLabel));
        leftPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        leftPanel.add(createBox(vacantLabel));
        leftPanel.add(Box.createRigidArea(new Dimension(15, 0)));

        return leftPanel;
    }

    private static JLabel getVacantImgLabel() {
        ImageIcon vacantIcon = new SvgIcon("/VacantCircle.svg", 28).getImageIcon();
        JLabel vacantLabel = new JLabel("Vacant", vacantIcon, JLabel.LEFT);
        vacantLabel.setBackground(Color.WHITE);
        vacantLabel.setOpaque(true);  // Ensure background color is applied
        vacantLabel.setIconTextGap(5);
        vacantLabel.setHorizontalAlignment(SwingConstants.CENTER);
        vacantLabel.setVerticalAlignment(SwingConstants.CENTER);
        vacantLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12));
        return vacantLabel;
    }

    private static JLabel getOccupiedImgLabel() {
        ImageIcon occupiedIcon = new SvgIcon("/OccupiedCircle.svg", 28).getImageIcon();
        JLabel occupiedLabel = new JLabel("Order On hold", occupiedIcon, JLabel.LEFT);
        occupiedLabel.setBackground(Color.WHITE);
        occupiedLabel.setOpaque(true);  // Ensure background color is applied
        occupiedLabel.setIconTextGap(5);
        occupiedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        occupiedLabel.setVerticalAlignment(SwingConstants.CENTER);
        occupiedLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12));
        return occupiedLabel;
    }

    private static JLabel getDisabledImgLabel() {
        ImageIcon disabledIcon = new SvgIcon("/DisabledCircle.svg", 28).getImageIcon();
        JLabel disabledLabel = new JLabel("Occupied", disabledIcon, JLabel.LEFT);
        disabledLabel.setBackground(Color.WHITE);
        disabledLabel.setOpaque(true);  // Ensure background color is applied
        disabledLabel.setIconTextGap(5);
        disabledLabel.setHorizontalAlignment(SwingConstants.CENTER);
        disabledLabel.setVerticalAlignment(SwingConstants.CENTER);
        disabledLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12));
        return disabledLabel;
    }

    private static JPanel createBox(JLabel label) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.X_AXIS));
        box.add(label);
        return box;
    }

    static void startAllTablesButtonAnimation() {
        animateBorderColor(buttons[0]);
        buttons[0].setBackground(Color.decode("#fff2e8"));
        buttons[0].setOpaque(true);
        buttons[0].setContentAreaFilled(true);
    }


    private JPanel createRightLabel() {
        JPanel rightPanel = new JPanel(new BorderLayout()); // BorderLayout for full panel control
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(550, 100));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

        // Create the container panel
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new GridLayout(0, 3, 15, 15)); // Align components horizontally
        containerPanel.setBackground(Color.WHITE);
        containerPanel.setBorder(BorderFactory.createEmptyBorder(19, 0, 19, 0));


        RoundedBorderButton addProductsButton = getAddProductsButton();

        // Create and configure the selectPlaceOrderButton
        ImageIcon paymentIcon = IconCreator.createResizedIcon("/CreditCardIcon.svg", 19, null);
        paymentButton = new IconTextButton("Proceed to Payment", paymentIcon, 14, Color.decode("#09AA29"), 0);
        paymentButton.setBackground(Color.decode("#09AA29"));
        paymentButton.setForeground(Color.WHITE);
        paymentButton.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12));
        paymentButton.setPreferredSize(new Dimension(155, 41)); // Adjusted size for visibility
        paymentButton.setMaximumSize(new Dimension(155, 41));
        paymentButton.setMinimumSize(new Dimension(155, 41));

        // Set icon on the left side of the button text
        paymentButton.setIcon(paymentIcon);
        // Set the text and icon alignment
        paymentButton.setHorizontalTextPosition(SwingConstants.RIGHT); // Icon on the left
        paymentButton.setVerticalTextPosition(SwingConstants.CENTER);
        paymentButton.setHorizontalAlignment(SwingConstants.CENTER);
        paymentButton.setVerticalAlignment(SwingConstants.CENTER);

        paymentButton.addActionListener(e -> {
            if (!tableNameLabel.getText().isEmpty() && lastClickedOrderCode !=null) {
                // Start loading the payment details in the background
                thisPaymentFromTableCreation.displayOrderDetailsForPaymentProcess(lastClickedOrderCode, () -> {
                    // Callback after data processing is complete
                    // Switch to the payment panel
                    selectTableAndPaymentCard.show(selectTableAndPaymentHoldingMainPane, "PaymentPanel");
                    thisPaymentFromTableCreation.startAnimationDefaultPayment();
                });

            }
        });


        // Add the components to the containerPanel
        containerPanel.add(createTableAndOrderInfoPanel());
        containerPanel.add(addProductsButton);
        containerPanel.add(paymentButton);


        // Add the wrapperPanel to the rightPanel
        rightPanel.add(containerPanel, BorderLayout.EAST);

        return rightPanel;
    }

    private static RoundedBorderButton getAddProductsButton() {
        ImageIcon addProductsIcon = new SvgIcon("/PlusOrangeIcon.svg", 20).getImageIcon();
        RoundedBorderButton addProductsButton = new RoundedBorderButton("Add Products", 155, 41, COLOR_ORANGE);
        addProductsButton.setIcon(addProductsIcon);
//        addProductsButton.setOpaque(true);
        addProductsButton.setBackground(Color.WHITE);
        addProductsButton.setForeground(COLOR_ORANGE);
        addProductsButton.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12));
        addProductsButton.setPreferredSize(new Dimension(155, 41));
        addProductsButton.setMaximumSize(new Dimension(155, 41));
        addProductsButton.setMinimumSize(new Dimension(155, 41));

        // Align text and icon
        addProductsButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        addProductsButton.setVerticalTextPosition(SwingConstants.CENTER);
        addProductsButton.setHorizontalAlignment(SwingConstants.CENTER);
        addProductsButton.setVerticalAlignment(SwingConstants.CENTER);

        addProductsButton.addActionListener(e -> {
            addProductButtonEvent(lastClickedOrderCode);
        });


        return addProductsButton;
    }

    public static String getLastClickedOrderCode() {
        return lastClickedOrderCode;
    }

    private static void addProductButtonEvent(String lastClickedOrderCode) {
        if (lastClickedOrderCode == null) {
            JOptionPane.showMessageDialog(tableHoldingPanel, "Invalid Order Code");
        } else {
            int result = JOptionPane.showConfirmDialog(
                    tableHoldingPanel,
                    "Sure to add additional products to " + lastClickedOrderCode + "?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (result == JOptionPane.YES_OPTION) {
                CreateHomePanel.showOrderCode(lastClickedOrderCode);
                MiniDevPOS.showPanel("Home");
            } else if (result == JOptionPane.NO_OPTION) {
            }
        }
    }


    private static JPanel createTableAndOrderInfoPanel() {
        // Create a panel with BoxLayout to arrange components vertically
        tableAndOrderCodePanel = new JPanel();
        tableAndOrderCodePanel.setPreferredSize(new Dimension(100, 50));
        tableAndOrderCodePanel.setMaximumSize(new Dimension(100, 50));
        tableAndOrderCodePanel.setMinimumSize(new Dimension(100, 50));
        tableAndOrderCodePanel.setBackground(Color.WHITE);
        tableAndOrderCodePanel.setLayout(new BoxLayout(tableAndOrderCodePanel, BoxLayout.Y_AXIS));
        tableAndOrderCodePanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        // Create labels for the table name and order code
        tableNameLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 15));
        orderCode.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12));
        orderCode.setForeground(Color.decode("#9f9f9e"));

        // Add labels to the panel
        tableAndOrderCodePanel.add(tableNameLabel);
        tableAndOrderCodePanel.add(orderCode);

        return tableAndOrderCodePanel;
    }

    public static JLabel customerInfo = new JLabel();
    public static JLabel tableInfo = new JLabel();


    private static void updateTableNoAndOrderCode(Table lastCreatedTable) {
        tableNameLabel.setText("Table " + lastCreatedTable.getTableName());
        if (lastCreatedTable.getCurrentOrderCode() != null) {
            orderCode.setText("Order " + lastCreatedTable.getCurrentOrderCode());
        } else {
            orderCode.setText("Vacant");
        }

        lastClickedOrderCode = lastCreatedTable.getCurrentOrderCode();
        if (lastClickedOrderCode != null) {
            lastClickedOrderLabel.setText("Order ID " + lastClickedOrderCode);
        } else {
            lastClickedOrderLabel.setText("No Order ID");
        }

        customerInfo.setText("Default Customer");
        tableInfo.setText("Dine-In • " + lastCreatedTable.getTableName());
    }


}