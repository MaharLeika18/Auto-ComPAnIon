package org.MiniDev.Table;

import DBConnection.DBConnection;
import UI.*;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import net.miginfocom.swing.MigLayout;
import org.MiniDev.Home.CreateHomePanel;
import org.MiniDev.Home.MiniDevPOS;
import org.MiniDev.OOP.Table;
import org.MiniDev.Order.CreateOrderPanel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static UI.UserFinalSettingsVar.*;
import static org.MiniDev.Home.CreateHomePanel.*;

//Seating Map
public class TableLists {

    static String[] buttonNames = {"All Tables", "Vacant", "Occupied", "Disabled"};
    static RoundedBorderButton[] buttons; // Array to hold references to all buttons
    protected static List<Table> tablesLists = new ArrayList<>();
    static RoundedBorderButton lastClickedButtonOfTableCategory;
    static JPanel tableHoldingPanel;
    static JScrollPane scrollPane;
    static JPanel bottomPanel;
    static JLabel tableNameLabel;
    static JLabel orderCode;
    private static JButton lastClickedButton = null;
    private static Table lastClickedTable = null;
    protected static JPanel panel;


    public static JPanel selectTablesPanel() {
        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        centerPanel.setBackground(COLOR_GRAY);
        centerPanel.setPreferredSize(new Dimension(555, 50));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 15));

        RoundedPanel tableTopNavigationPanel = new RoundedPanel(10);
        tableTopNavigationPanel.setLayout(new BoxLayout(tableTopNavigationPanel,BoxLayout.X_AXIS));
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

        centerPanel.add(tableTopNavigationPanel);

        bottomPanel = createBottomLabelPanel();
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setPreferredSize(new Dimension(550, 100));

        tableHoldingPanel = new JPanel(new BorderLayout());
        tableHoldingPanel.setBorder(BorderFactory.createEmptyBorder(9, 8, 0, 8));
        tableHoldingPanel.setBackground(COLOR_GRAY);

        tableHoldingPanel.add(createTableLists(), BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.NORTH);
        panel.add(tableHoldingPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private static RoundedBorderButton getNaviButton(int i) {
        String buttonName = buttonNames[i];
        RoundedBorderButton navigateToTableCategory = new RoundedBorderButton(buttonName,80,30,COLOR_WHITE);
        navigateToTableCategory.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 11));
        navigateToTableCategory.setRoundedArcSize(10);
        navigateToTableCategory.setHorizontalTextPosition(SwingConstants.CENTER);
        navigateToTableCategory.setVerticalTextPosition(SwingConstants.CENTER);
        navigateToTableCategory.setMargin(new Insets(7, 4, 7, 4));
        navigateToTableCategory.setPreferredSize(new Dimension(85, 30));
        navigateToTableCategory.setMaximumSize(new Dimension(85, 30)); // Adjust dimensions as needed
        navigateToTableCategory.setMinimumSize(new Dimension(85, 30)); // Adjust dimensions as needed
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
                        JScrollPane result = get(); // This might throw an exception if the doInBackground() fails
                        if (result != null) {
                            tableHoldingPanel.removeAll();
                            tableHoldingPanel.add(result);
                            tableHoldingPanel.revalidate();
                            tableHoldingPanel.repaint();
                        } else {
                            System.out.println("Result from createTableLists() is null");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            worker.execute(); // Start the background task
        });
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
        scrollPane = new JScrollPane(panel);
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
        RoundButton button = new RoundButton("", 120, 120);
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
        button.addActionListener(e -> {
            handleAfterClickTables(table, button, bottomPanel, MiniDevPOS.getCurrentParentPanelName());
            updateTableNoAndOrderCode(table);
        });

        return button;
    }


    private static void handleAfterClickTables(Table table, JButton button, RoundedPanel bottomPanel, String parentPanel) {
        if (parentPanel.equals("Orders")) {
            handleTableSelectionForTableChanges(table, button, bottomPanel);
        } else {
            handleTableSelection(table, button, bottomPanel);
        }
    }

    private static void handleTableSelectionForTableChanges(Table table, JButton button, RoundedPanel bottomPanel) {
        if (table.getTableStatus() == 'V') {
            if (lastClickedButton != null && lastClickedTable != null
                    && lastClickedTable.getTableStatus() == 'V') {
                RoundedPanel lastBottomPanel = (RoundedPanel) lastClickedButton.getComponent(1);
                lastBottomPanel.setBackground(Color.decode("#09AA29"));
            }
            lastClickedButton = button;
            lastClickedTable = table;
            bottomPanel.setBackground(Color.decode("#4361ee"));
        } else if (table.getTableStatus() == 'O' || table.getTableStatus() == 'D') {
            JOptionPane.showMessageDialog(tableHoldingPanel, "The table you selected has already been occupied with " + table.getCurrentOrderCode() + ".");
        }
    }

    private static void refreshSeatingMap() {
        refreshTableLists();
        resetButtonColors();
        startAllTablesButtonAnimation();
    }

    private static void reservationRemovePopupEvent(String reserveThisTableName) {
        int response = JOptionPane.showConfirmDialog(
                tableHoldingPanel,
                "Remove the reservation for " + reserveThisTableName + "?",
                "Confirm remove Reservation",
                JOptionPane.YES_NO_OPTION
        );

        if (response == JOptionPane.YES_OPTION) {
            reserveRemoveBackendDB(reserveThisTableName);
            refreshSeatingMap();
        } else if (response == JOptionPane.NO_OPTION) {

        }
    }

    private static void reserveRemoveBackendDB(String reserveThisTableName) {
        try (Connection connection = DBConnection.getConnection(); CallableStatement stmt = connection.prepareCall("{CALL sp_RemoveReservationTable(?)}")) {
            // Set the parameters and execute the stored procedure
            stmt.setString(1, reserveThisTableName);

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Handle table selection logic in a separate method
    private static void handleTableSelection(Table table, JButton button, RoundedPanel bottomPanel) {
        if (table.getTableStatus() == 'V') {
            if (lastClickedButton != null && lastClickedTable != null
                    && lastClickedTable.getTableStatus() == 'V') {
                RoundedPanel lastBottomPanel = (RoundedPanel) lastClickedButton.getComponent(1);
                lastBottomPanel.setBackground(Color.decode("#09AA29"));
            }
            lastClickedButton = button;
            lastClickedTable = table;
            bottomPanel.setBackground(Color.decode("#4361ee"));
        } else if (table.getTableStatus() == 'O') {
            DialogCollection.showCustomDialog(tableHoldingPanel, "Cannot select this table.", "Selection Error");
        } else if (table.getTableStatus() == 'D') {
            lastClickedTable = table;
            reservationRemovePopupEvent(lastClickedTable.getTableName());
        }
    }

    //        if (checkOrderListsActive()) {
    //        } else {
//            DialogCollection.showCustomDialog(tableHoldingPanel, "Please select the products first", "Selection Error");
//            HomePageFrame.showPanel("Home");
//        }


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


    protected static JPanel createBottomLabelPanel() {
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 0, 0));

        Border border = BorderFactory.createMatteBorder(1, 0, 0, 0, Color.decode("#E8E8E8"));
        bottomPanel.setBorder(border);

        bottomPanel.add(createLeftLabel());
        bottomPanel.add(createRightLabel());

        return bottomPanel;
    }

    protected static JPanel createLeftLabel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));

        JLabel disabledLabel = getjLabel();

        JLabel occupiedLabel = getOccupiedLabel();

        JLabel vacantLabel = getLabel("/VacantCircle.svg", "Vacant");

        leftPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        leftPanel.add(createBox(disabledLabel));
        leftPanel.add(Box.createRigidArea(new Dimension(15, 0)));


        leftPanel.add(createBox(occupiedLabel));
        leftPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        leftPanel.add(createBox(vacantLabel));
        leftPanel.add(Box.createRigidArea(new Dimension(15, 0)));

        return leftPanel;
    }

    private static JLabel getLabel(String svgPath, String Vacant) {
        ImageIcon vacantIcon = new SvgIcon(svgPath, 28).getImageIcon();
        JLabel vacantLabel = new JLabel(Vacant, vacantIcon, JLabel.LEFT);
        vacantLabel.setBackground(Color.WHITE);
        vacantLabel.setOpaque(true);  // Ensure background color is applied
        vacantLabel.setIconTextGap(5);
        vacantLabel.setHorizontalAlignment(SwingConstants.CENTER);
        vacantLabel.setVerticalAlignment(SwingConstants.CENTER);
        vacantLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12));
        return vacantLabel;
    }

    private static JLabel getOccupiedLabel() {
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

    private static JLabel getjLabel() {
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


    public static void startAllTablesButtonAnimation() {
        if (!(buttons == null)) {
            animateBorderColor(buttons[0]);
        } else {
            System.out.println("Buttons are null");
        }
    }


    private static JPanel createRightLabel() {
        JPanel rightPanel = new JPanel(new BorderLayout()); // BorderLayout for full panel control
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

        // Create the container panel
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.X_AXIS)); // Align components horizontally
        containerPanel.setBackground(Color.WHITE);

        // Add the components to the containerPanel
        containerPanel.add(createTableAndOrderInfoPanel());
        containerPanel.add(Box.createHorizontalStrut(10));
        containerPanel.add(getReservationButton());
        containerPanel.add(Box.createHorizontalStrut(10));
        containerPanel.add(getSelectAndPlaceOrderButton());

        // Create a wrapper panel to center the containerPanel
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(Color.WHITE);
        wrapperPanel.add(Box.createHorizontalGlue(), BorderLayout.WEST); // Add glue to push content to the center
        wrapperPanel.add(containerPanel, BorderLayout.CENTER); // Center the containerPanel
        wrapperPanel.add(Box.createHorizontalGlue(), BorderLayout.EAST); // Add glue to push content to the center

        // Add the wrapperPanel to the rightPanel
        rightPanel.add(wrapperPanel, BorderLayout.EAST);

        return rightPanel;
    }

    private static IconTextButton getSelectAndPlaceOrderButton() {
        // Create and configure the selectPlaceOrderButton
        ImageIcon orderIconBag = IconCreator.createResizedIcon("/OrderBag.svg", 19, null);
        IconTextButton selectPlaceOrderButton = new IconTextButton("Select & Place Order", orderIconBag, 14, Color.decode("#FC8019"), 0);
        selectPlaceOrderButton.setBackground(Color.decode("#FC8019"));
        selectPlaceOrderButton.setForeground(Color.WHITE);
        selectPlaceOrderButton.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12));
        selectPlaceOrderButton.setPreferredSize(new Dimension(155, 41)); // Adjusted size for visibility
        selectPlaceOrderButton.setMaximumSize(new Dimension(155, 41)); // Adjusted size for visibility
        selectPlaceOrderButton.setMinimumSize(new Dimension(155, 41));
        // Set icon on the left side of the button text
        selectPlaceOrderButton.setIcon(orderIconBag);
        // Set the text and icon alignment
        selectPlaceOrderButton.setHorizontalTextPosition(SwingConstants.RIGHT); // Icon on the left
        selectPlaceOrderButton.setVerticalTextPosition(SwingConstants.CENTER);
        selectPlaceOrderButton.setHorizontalAlignment(SwingConstants.CENTER);
        selectPlaceOrderButton.setVerticalAlignment(SwingConstants.CENTER);

        selectPlaceOrderButton.addActionListener((ActionEvent e) -> {
            selectedButtonActionPerformed(MiniDevPOS.getCurrentParentPanelName());
        });

        return selectPlaceOrderButton;
    }

    private static IconTextButton getReservationButton() {
        // Create and configure the selectPlaceOrderButton
        ImageIcon reservationIcon = IconCreator.createResizedIcon("/ClockIcon.svg", 19, null);
        IconTextButton confirmReservationButton = new IconTextButton("Reserve", reservationIcon, 14, Color.decode("#082f49"), 0);
        confirmReservationButton.setBackground(Color.decode("#082f49"));
        confirmReservationButton.setForeground(Color.WHITE);
        confirmReservationButton.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12));
        confirmReservationButton.setPreferredSize(new Dimension(140, 41)); // Adjusted size for visibility
        confirmReservationButton.setMaximumSize(new Dimension(140, 41)); // Adjusted size for visibility
        confirmReservationButton.setMinimumSize(new Dimension(140, 41));
        // Set icon on the left side of the button text
        confirmReservationButton.setIcon(reservationIcon);
        // Set the text and icon alignment
        confirmReservationButton.setHorizontalTextPosition(SwingConstants.RIGHT); // Icon on the left
        confirmReservationButton.setVerticalTextPosition(SwingConstants.CENTER);
        confirmReservationButton.setHorizontalAlignment(SwingConstants.CENTER);
        confirmReservationButton.setVerticalAlignment(SwingConstants.CENTER);

        confirmReservationButton.addActionListener((ActionEvent e) -> {
            if (lastClickedTable != null) {
                reserveButtonEvent(lastClickedTable.getTableName());
            } else {
                JOptionPane.showMessageDialog(tableHoldingPanel, "Make sure to select the table..");
            }
        });

        return confirmReservationButton;
    }

    private static void reserveButtonEvent(String reserveThisTableName) {
        int response = JOptionPane.showConfirmDialog(
                tableHoldingPanel,
                "Confirm the reservation for " + reserveThisTableName + "?",
                "Confirm Reservation",
                JOptionPane.YES_NO_OPTION
        );

        if (response == JOptionPane.YES_OPTION) {
            reserveActionBackendDB(reserveThisTableName);
            refreshSeatingMap();
        } else if (response == JOptionPane.NO_OPTION) {

        }
    }


    private static void reserveActionBackendDB(String reserveThisTableName) {
        try (Connection connection = DBConnection.getConnection(); CallableStatement stmt = connection.prepareCall("{CALL sp_ReserveTable(?)}")) {
            // Set the parameters and execute the stored procedure
            stmt.setString(1, reserveThisTableName);

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void selectedButtonActionPerformed(String parentPanelName) {
        if (parentPanelName.equals("Orders")) {
            CreateOrderPanel.setTableChangePanelVisibilityOnOrderPage(true, lastClickedTable.getTableName(), String.valueOf(lastClickedTable.getTableID()));
            MiniDevPOS.showPanel("Orders");
        } else {
            if (checkOrderListsActive()) {
                if (!(CreateHomePanel.orderListsPanelList.isEmpty())) {
                    // Check if a table has been selected
                    if (lastClickedTable == null) {
                        showTableSelectionError();
                    } else {
                        // Place the order asynchronously
                        placeOrderAndUpdateUI();
                    }
                } else {
                    SwingUtilities.invokeLater(() -> {
                        showProductNoSelectedError();
                        resetButtonColors();
                        startAllTablesButtonAnimation();
                    });
                }
            } else {
                DialogCollection.showCustomDialog(tableHoldingPanel, "Please select the products first", "Selection Error");
                MiniDevPOS.showPanel("Home");
            }
        }
    }


    // Method to show an error dialog when no table is selected
    private static void showTableSelectionError() {
        SwingUtilities.invokeLater(() -> DialogCollection.showCustomDialog(panel, "You have selected no table!", "Select Table"));
    }


    // Method to show an error dialog when no table is selected
    private static void showProductNoSelectedError() {
        SwingUtilities.invokeLater(() -> DialogCollection.showCustomDialog(panel, "You have selected no product!", "Select Products"));
    }

    // Method to handle the order placement and UI updates
    public static void placeOrderAndUpdateUI() {
        SwingUtilities.invokeLater(() -> {
            try {
                createOrderToDatabase(lastClickedTable.getTableName());
                showOrderCompletedDialog();
                updateUIComponents();
            } catch (Exception ex) {
                handleOrderPlacementError(ex);
            }
        });
    }

    // Method to show a dialog indicating that the order was completed
    public static void showOrderCompletedDialog() {
        DialogCollection.showCustomDialog(panel, "You have completed the order!", "Order Completed");
    }

    // Method to update UI components after placing the order
    public static void updateUIComponents() {
        deleteOrderListsPanel();
        new CreateHomePanel().updateDisplayProductDataInBackground();
        MiniDevPOS.showPanel("Home"); // Use the panel's name as defined in CardLayout
        MiniDevPOS.contentPanel.revalidate();
        MiniDevPOS.contentPanel.repaint();
        MiniDevPOS.startHomeButtonAnimation();
        resetButtonColors();
        startAllTablesButtonAnimation();
    }

    // Method to handle errors during order placement
    private static void handleOrderPlacementError(Exception ex) {
        // Log the error and show an appropriate message to the user
        ex.printStackTrace();
        SwingUtilities.invokeLater(() -> DialogCollection.showCustomDialog(panel, "An error occurred while placing the order. Please try again.", "Order Error"));
    }

    private static JPanel createTableAndOrderInfoPanel() {
        // Create a panel with BoxLayout to arrange components vertically
        JPanel tableAndOrderCodePanel = getjPanel();

        // Create labels for the table name and order code
        tableNameLabel = new JLabel(" ");
        tableNameLabel.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 15));

        orderCode = new JLabel(" ");
        orderCode.setFont(new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12));
        orderCode.setForeground(Color.decode("#9f9f9e"));

        // Add labels to the panel
        tableAndOrderCodePanel.add(tableNameLabel);
        tableAndOrderCodePanel.add(orderCode);

        return tableAndOrderCodePanel;
    }


    private static void updateTableNoAndOrderCode(Table lastClickedTable) {
        if (lastClickedTable.getTableStatus() != 'O') {
            tableNameLabel.setText("Table " + lastClickedTable.getTableName());
            if (lastClickedTable.getCurrentOrderCode() != null) {
                orderCode.setText("Order " + lastClickedTable.getCurrentOrderCode());
            } else {
                orderCode.setText("Vacant");
            }
        }
    }


    private static JPanel getjPanel() {
        JPanel tableAndOrderCodePanel = new JPanel();
        tableAndOrderCodePanel.setPreferredSize(new Dimension(115, 50));
        tableAndOrderCodePanel.setMaximumSize(new Dimension(115, 50));
        tableAndOrderCodePanel.setMinimumSize(new Dimension(115, 50));
        tableAndOrderCodePanel.setBackground(Color.WHITE);
        tableAndOrderCodePanel.setLayout(new BoxLayout(tableAndOrderCodePanel, BoxLayout.Y_AXIS));
        tableAndOrderCodePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 5));
        return tableAndOrderCodePanel;
    }


}
