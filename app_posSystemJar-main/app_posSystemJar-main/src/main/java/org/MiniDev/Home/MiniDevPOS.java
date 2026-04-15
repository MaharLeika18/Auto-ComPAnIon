package org.MiniDev.Home;

import SqlLoadersAndUpdater.FetchEmployeeHistoryLists;
import UI.*;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import org.MiniDev.Cashier.CreateCashDrawerPanel;
import org.MiniDev.Cashier.CreateCashierPanel;
import org.MiniDev.Customer.CreateCustomerPanel;
import org.MiniDev.Home.ButtonHandler.ButtonHandler;
import org.MiniDev.Home.IconManager.IconManager;
import org.MiniDev.Home.SubPopUpWindows.ProfileClickerAction;
import org.MiniDev.Home.SubPopUpWindows.WhenProfileClicked;
import org.MiniDev.Login.Login;
import org.MiniDev.OOP.EmployeeHistoryLists;
import org.MiniDev.OOP.TellerLists;
import org.MiniDev.Order.CreateOrderPanel;
import org.MiniDev.Report.CreateReportsPanel;
import org.MiniDev.Backoffice.CreateSettingsPanel;
import org.MiniDev.Table.CreateTablePanel;
import org.MiniDev.Table.TableLists;
import raven.glasspanepopup.GlassPanePopup;
import raven.swing.AvatarIcon;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;

import static UI.DialogCollection.showCustomDialog;
import static UI.IconCreator.createResizedIcon;
import static UI.UserFinalSettingsVar.*;

public class MiniDevPOS extends JFrame {

    protected static CardLayout cardLayout; // CardLayout instance
    public static JFrame frame;
    public static JButton homeButton;
    public static JButton customersButton;
    public static JButton tablesButton;
    public static JButton cashierButton;
    public static JButton ordersButton;
    public static JButton reportsButton;
    public static JButton settingsButton;
    public static JButton currentButton; // Track the currently pressed button
    public static JButton selectTablesButton;
    public static JPanel contentPanel; // Panel to hold dynamic content
    public static JTextField searchField;
    public static JButton searchButton;
    public static RoundedPanel mainPane;
    // Panels for each section
    protected static JPanel homePanel;
    protected static JPanel customersPanel;
    protected static JPanel tablesPanel;
    protected static JPanel cashierPanel;
    protected static JPanel ordersPanel;
    protected static JPanel reportsPanel;
    protected static JPanel settingsPanel;
    protected static JPanel selectTablesPanel;
    public static String panelName = null;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            initializeUI();
            JFrame frame = new JFrame("Login");
            frame.setUndecorated(true);
            frame.setBackground(new Color(0, 0, 0, 0));
            // Pass the frame reference to the Login panel
            frame.add(new Login(frame));
            frame.pack();
            frame.setLocationRelativeTo(null); // Center the window
            frame.setVisible(true);
        });
    }

    public static void initializeUI() {
        try {
            // Set Look and Feel
            FlatRobotoFont.install();
            FlatLaf.registerCustomDefaultsSource("raven.themes");
            UIManager.setLookAndFeel(new FlatMacLightLaf());

            // Apply a font with explicit fallback support
            UIManager.put("defaultFont", new FontUIResource(getFallbackFont()));

        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a font that prioritizes FlatRoboto but falls back to Noto Sans Myanmar if needed.
     */
    private static Font getFallbackFont() {
        return new FontUIResource(new Font("FlatRobotoFont, Myanmar Text", Font.PLAIN, 13));
    }


    public static void createAndShowGUI(JFrame parentFrame) {
        SwingUtilities.invokeLater(() -> {
            // Dispose of the login frame
            if (parentFrame != null) {
                parentFrame.dispose();
            }

            frame = new JFrame("MiniDev POS");
            GlassPanePopup.install(frame);
            frame.setUndecorated(true);
            frame.setBackground(new Color(0, 0, 0, 0));

            // Get the screen dimensions
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            // Set the frame size to 80% width and height for adaptability
            int width = (int) (screenSize.width * 0.95);
            int height = (int) (screenSize.height * 0.89);

            //  frame.setSize(1280, 800);
            frame.setSize(width, height);

            frame.setLocationRelativeTo(null);
            setRoundedCorners(frame, 0);
            frame.setLayout(new BorderLayout());

            mainPane = new RoundedPanel(20);
            mainPane.setPreferredSize(new Dimension(frame.getWidth(), frame.getHeight()));
            mainPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            mainPane.setBackground(COLOR_WHITE);
            mainPane.setBorderWidth(1);
            mainPane.setBorderColor(COLOR_PANEL_GRAY);


            JPanel topPanel = createTopPanel();
            JPanel leftPanel = createLeftPanel();

            contentPanel = new JPanel(new CardLayout());
            contentPanel.setBackground(new Color(0, 0, 0, 0));
            cardLayout = (CardLayout) contentPanel.getLayout();

            homePanel = new CreateHomePanel().createHomePanel();
            customersPanel = CreateCustomerPanel.createCustomersPanel();
            tablesPanel = new CreateTablePanel().selectTablesPanel();
            cashierPanel = CreateCashierPanel.createCashierPanel();
            ordersPanel = new CreateOrderPanel().createOrdersPanel();
            reportsPanel = new CreateReportsPanel().createReportsPanel();
            settingsPanel = new CreateSettingsPanel().createSettingsPanel();
            selectTablesPanel = TableLists.selectTablesPanel();

            // Add initial panels only
            contentPanel.add(homePanel, "Home");
            contentPanel.add(customersPanel, "Customers");
            contentPanel.add(tablesPanel, "Tables");
            contentPanel.add(cashierPanel, "Cashier");
            contentPanel.add(ordersPanel, "Orders");
            contentPanel.add(reportsPanel, "Reports");
            contentPanel.add(settingsPanel, "Backoffice");
            contentPanel.add(selectTablesPanel, "SelectTables");

            Border border = BorderFactory.createMatteBorder(0, 1, 1, 1, COLOR_LINE_COLOR);
            contentPanel.setBorder(border);

            mainPane.setLayout(new BorderLayout());
            mainPane.add(topPanel, BorderLayout.NORTH);
            mainPane.add(leftPanel, BorderLayout.WEST);
            mainPane.add(contentPanel, BorderLayout.CENTER);

            // Start the home button animation
            startHomeButtonAnimation();
            TableLists.startAllTablesButtonAnimation();
            reportsButton.requestFocusInWindow();
            frame.setContentPane(mainPane);
            frame.pack();

            frame.setVisible(true);
        });
    }


    public static void showPanel(String panelName) {
        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
        cardLayout.show(contentPanel, panelName);
        contentPanel.revalidate();
        contentPanel.repaint();
    }


    public static void setRoundedCorners(JFrame frame, int arc) {
        // Set shape with rounded corners
        frame.setShape(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), arc, arc));

        // Use a component listener to set shape on resize
        frame.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                frame.setShape(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), arc, arc));
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
    }


    private static JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(COLOR_WHITE);
        leftPanel.setPreferredSize(new Dimension(70, frame.getHeight()));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));

        JPanel navigationButtonsPanel = createNavigationButtonsPanel();

        // Create profile icon label
        Component profileIconLabel = getCurrentProfileTellerAvatar(() -> {
            new WhenProfileClicked().showProfileClickerUI();
        });

        // Create a panel to hold profile icon and logout button with vertical gap
        JPanel profileLogoutPanel = new JPanel();
        profileLogoutPanel.setBackground(COLOR_WHITE);
        profileLogoutPanel.setLayout(new BoxLayout(profileLogoutPanel, BoxLayout.Y_AXIS));

        // Add vertical strut for spacing above the profile icon
        profileLogoutPanel.add(Box.createVerticalStrut(8)); // Adjust as needed for more spacing

        // Center align profile icon label within its container
        // Create a small rounded panel
        JPanel profileIconPanel = new JPanel(); // Smaller radius
        profileIconPanel.setBackground(COLOR_WHITE);
        profileIconPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        profileIconPanel.setPreferredSize(new Dimension(40, 60)); // Adjust size to be small
        profileIconPanel.setLayout(new GridBagLayout()); // Center the icon inside

        // Add the profile icon label
        profileIconPanel.add(profileIconLabel);
        profileLogoutPanel.add(profileIconPanel);


        // Add vertical gap between profile icon and logout button
        profileLogoutPanel.add(Box.createVerticalStrut(8)); // Adjust as needed for more spacing

        JButton logoutButton = createNavigationButton("Logout", "/LogoutIcon.svg");
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> DialogCollection.showCustomLogoutDialog(frame));

        profileLogoutPanel.add(logoutButton);

        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(navigationButtonsPanel, BorderLayout.NORTH);
        leftPanel.add(profileLogoutPanel, BorderLayout.SOUTH);

        return leftPanel;
    }

    public static ImageIcon getAvatarOfCurrentTellerID(int ID) {
        FetchEmployeeHistoryLists fetchEmployeeHistoryLists = new FetchEmployeeHistoryLists();
        List<EmployeeHistoryLists> empLists = fetchEmployeeHistoryLists.getEmployeeHistoryLists();

        return (ImageIcon) empLists.stream().filter(p -> p.getEmployeeId() == ID).findFirst().get().getEmpProfileAsIcon();
    }

    public static RoundedPanel getCurrentProfileTellerAvatar(ProfileClickerAction action) {
//        ImageIcon profileIcon = new SvgIcon("/AccountPhoto.svg", 35).getImageIcon();
        RoundedPanel panel = new RoundedPanel(10);
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(60, 60));
        panel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));

        ImageAvatar avatar = new ImageAvatar();
        avatar.setImage(getAvatarOfCurrentTellerID(CreateCashDrawerPanel.tellerID));
        avatar.setOpaque(false);
        avatar.setBorderSize(0);


        panel.add(avatar, BorderLayout.CENTER);

        JLabel profileIconLabel = new JLabel(getAvatarOfCurrentTellerID(CreateCashDrawerPanel.tellerID));
        profileIconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add a MouseListener to the JLabel
        avatar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.onClick(); // Trigger the provided action
            }
        });
        return panel;
    }


    private static JPanel createNavigationButtonsPanel() {
        JPanel navigationButtonsPanel = new JPanel();
        navigationButtonsPanel.setBackground(COLOR_WHITE);
        navigationButtonsPanel.setLayout(new BoxLayout(navigationButtonsPanel, BoxLayout.Y_AXIS));
        navigationButtonsPanel.add(Box.createVerticalStrut(10));


        // Create navigation buttons with icons and text
        homeButton = createNavigationButton("Home", "/HomeIcon.svg");
        customersButton = createNavigationButton("Customer", "/CustomersIcon.svg");
        tablesButton = createNavigationButton("Tables", "/GridTableIcon.svg");
        cashierButton = createNavigationButton("Cashier", "/CashierIcon.svg");
        ordersButton = createNavigationButton("Orders", "/OrderListsIcon.svg");
        reportsButton = createNavigationButton("Reports", "/ReportIcon.svg");
        settingsButton = createNavigationButton("Backoffice", "/SettingIcon.svg");


        // Set initial font color
        JButton[] buttons = {homeButton, customersButton, tablesButton, cashierButton, ordersButton, reportsButton, settingsButton};
        for (JButton button : buttons) {
            button.setForeground(COLOR_FONT_GRAY);
        }

        // Set currentButton if needed, for example, the first button by default
        currentButton = homeButton; // Or another logic to set the initial currentButton

        // Add buttons to the panel with glue for spacing
        addButtonWithGlue(navigationButtonsPanel, homeButton);
        addButtonWithGlue(navigationButtonsPanel, customersButton);
        addButtonWithGlue(navigationButtonsPanel, tablesButton);
        addButtonWithGlue(navigationButtonsPanel, cashierButton);
        addButtonWithGlue(navigationButtonsPanel, ordersButton);
        addButtonWithGlue(navigationButtonsPanel, reportsButton);
        addButtonWithGlue(navigationButtonsPanel, settingsButton);

        return navigationButtonsPanel;
    }


    // Add a button to the panel with vertical glue for spacing
    private static void addButtonWithGlue(JPanel panel, JButton button) {
        button.setOpaque(true);
        panel.add(button);
        panel.add(Box.createVerticalGlue());
    }


    private static JButton createNavigationButton(String text, String iconPath) {
        JButton button = new JButton();

        // Scale the icon to a fixed size (if needed)
        ImageIcon icon = new SvgIcon(iconPath, 20).getImageIcon();
        icon = new ImageIcon(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        button.setIcon(icon);

        button.setText("<html><center>" + text + "</center></html>");
        button.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12));

        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setMargin(new Insets(10, 10, 10, 10));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFocusPainted(false);
        button.setRolloverEnabled(false);
        button.setOpaque(true); // Initially make it transparent
        button.setContentAreaFilled(false); // Ensure it doesn't fill the background initially
        button.setPreferredSize(new Dimension(65, 65));  // Make sure this is large enough

        // Set font color to #9f9f9e
        button.setForeground(COLOR_FONT_GRAY);

        // Action listener to handle button clicks
        button.addActionListener(e -> {
            ButtonHandler.handleButtonAction(button);
        });

        return button;
    }


    public static void refreshTableAll() {
        TableLists.refreshTableLists();
        CreateTablePanel.refreshTableLists();
    }


//    protected static void handleButtonAction(JButton button) {
//        // Reset the previous button's background and border
//        refreshTableAll();
//
//        if (currentButton != null) {
//            resetButtonBorder(currentButton);
//        }
//
//        // Set the current button as the active button
//        currentButton = button;
//
//        // Determine which panel to show based on the button clicked
//        panelName = null;
//
//        if (button == homeButton) {
//            panelName = "Home";
//        } else if (button == customersButton) {
//            panelName = "Customers";
//        } else if (button == tablesButton) {
//            panelName = "Tables";
//        } else if (button == cashierButton) {
//            panelName = "Cashier";
//        } else if (button == ordersButton) {
//            panelName = "Orders";
//            CreateOrderPanel.updateOrderHistPanel(true);
//            CreateOrderPanel.resetNavigationButtonAnimation();
//        } else if (button == reportsButton) {
//            panelName = "Reports";
//            CreateReportsPanel.pieChartsStartAnimation();
//        } else if (button == settingsButton) {
//            panelName = "Backoffice";
//        } else if (button == selectTablesButton) {
//            panelName = "SelectTables";
//        }
//
//        if (panelName != null) {
//            showPanel(panelName);
//
//            // Apply border animation only if the selected panel is not 'SelectTables'
//            if (!panelName.equals("SelectTables")) {
//                animateBorderColor(button, COLOR_ORANGE);
//            }
//
//            // Fill the button with color to indicate it is active
//            button.setBackground(Color.decode("#fff2e8")); // Set your desired fill color here
//            button.setOpaque(true); // Make it opaque
//            button.setContentAreaFilled(true); // Ensure it fills the background
//        }
//
//    }


    public static String getCurrentParentPanelName() {
        return Objects.requireNonNullElse(panelName, "Home");
    }


    public static void animateBorderColor(JButton button, Color color) {
        Border border;
        if (color == null) {
            border = BorderFactory.createEmptyBorder();
            button.setBackground(null);
        } else {
            border = new RoundedCornerBorder(5, color, 1);
            button.setBackground(Color.decode("#FFF5EE"));
            button.setIcon(IconManager.getActiveIcon(button));
        }
        button.setBorder(border);
        button.setForeground(COLOR_ORANGE);
        resetButtonBorder(button, homeButton, customersButton, tablesButton, cashierButton, ordersButton, reportsButton, settingsButton);
        frame.pack();
    }


    public static void resetButtonBorder(JButton currentButton, JButton... buttons) {
        for (JButton button : buttons) {
            if (button != null && button != currentButton) {
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setForeground(COLOR_FONT_GRAY);
                button.setBackground(COLOR_WHITE);
                button.setIcon(IconManager.getDefaultIcon(button)); // Ensure default icon is correct
            }
        }
        frame.pack();
    }


    // Start animation on the Home button
    public static void startHomeButtonAnimation() {
        // Simulate button press by changing border color for a short period
        animateBorderColor(homeButton, COLOR_ORANGE);
        homeButton.setIcon(IconManager.getActiveIcon(homeButton));
        // Fill the button with color to indicate it is active
        homeButton.setBackground(Color.decode("#fff2e8")); // Set your desired fill color here
        homeButton.setOpaque(true); // Make it opaque
        homeButton.setContentAreaFilled(true); // Ensure it fills the background

        resetButtonBorder(homeButton, customersButton, tablesButton, cashierButton, ordersButton, reportsButton, settingsButton);
        frame.pack();
    }


//    private static ImageIcon getDefaultIcon(JButton button) {
//        // Return default icon based on button
//        if (button == homeButton) {
//            return new SvgIcon("/HomeIcon.svg", 20).getImageIcon();
//        } else if (button == customersButton) {
//            return new SvgIcon("/CustomersIcon.svg", 20).getImageIcon();
//        } else if (button == tablesButton) {
//            return new SvgIcon("/GridTableIcon.svg", 20).getImageIcon();
//        } else if (button == cashierButton) {
//            return new SvgIcon("/CashierIcon.svg", 20).getImageIcon();
//        } else if (button == ordersButton) {
//            return new SvgIcon("/OrderListsIcon.svg", 20).getImageIcon();
//        } else if (button == reportsButton) {
//            return new SvgIcon("/ReportIcon.svg", 20).getImageIcon();
//        } else if (button == settingsButton) {
//            return new SvgIcon("/SettingIcon.svg", 20).getImageIcon();
//        }
//        return null;
//    }
//
//    private static ImageIcon getActiveIcon(JButton button) {
//        // Return active icon based on button
//        if (button == homeButton) {
//            return new SvgIcon("/HomeIconActive.svg", 20).getImageIcon(); // Example active icon
//        } else if (button == customersButton) {
//            return new SvgIcon("/CustomersIconActive.svg", 20).getImageIcon();
//        } else if (button == tablesButton) {
//            return new SvgIcon("/GridTableIconActive.svg", 20).getImageIcon();
//        } else if (button == cashierButton) {
//            return new SvgIcon("/CashierIconActive.svg", 20).getImageIcon();
//        } else if (button == ordersButton) {
//            return new SvgIcon("/OrderListsIconActive.svg", 20).getImageIcon();
//        } else if (button == reportsButton) {
//            return new SvgIcon("/ReportIconActive.svg", 20).getImageIcon();
//        } else if (button == settingsButton) {
//            return new SvgIcon("/SettingIconActive.svg", 20).getImageIcon();
//        }
//        return null;
//    }

    protected static JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(frame.getWidth(), 55));
        topPanel.setBackground(COLOR_WHITE);

        // Creating a MatteBorder with 1 pixel thickness, colored light gray, on the top of topPanel
        Border border = BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_LINE_COLOR);
        topPanel.setBorder(border);

        JPanel topLeftMainPanel = demonstrateTopLeftMainPanel();
        JPanel topRightMainPanel = demonstrateTopRightMainPanel();

        topPanel.add(topLeftMainPanel, BorderLayout.WEST);
        topPanel.add(topRightMainPanel, BorderLayout.EAST);

        return topPanel;
    }

    protected static JPanel demonstrateTopLeftMainPanel() {
        JPanel topLeftMainPanel = new JPanel();
        topLeftMainPanel.setBackground(COLOR_WHITE);

        // Using GridBagLayout for precise component positioning
        GridBagLayout gridBagLayout = new GridBagLayout();
        topLeftMainPanel.setLayout(gridBagLayout);

        // GridBagConstraints for companyLabel
        JLabel companyLabel = new JLabel("  MiniDev POS");
        companyLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 16));
        GridBagConstraints gbcCompanyLabel = new GridBagConstraints();
        gbcCompanyLabel.gridx = 0;
        gbcCompanyLabel.gridy = 0;
        gbcCompanyLabel.insets = new Insets(0, 5, 5, 10); // Padding
        gbcCompanyLabel.anchor = GridBagConstraints.WEST; // Align left
        topLeftMainPanel.add(companyLabel, gbcCompanyLabel);

        // Create searchField with placeholder text and ActionListener for Enter key
        searchField = new JTextField("", 20) {
            private boolean firstClick = true; // Flag to manage placeholder

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (!isOpaque()) {
                    g2.setColor(getBackground());
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 15, 15));
                }

                // Set placeholder text properties
                if (getText().isEmpty() && !firstClick) {
                    g2.setColor(Color.LIGHT_GRAY); // Set placeholder text color to gray
                    g2.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 12));
                    FontMetrics fm = g2.getFontMetrics();
                    int x = getInsets().left; // Start from the left edge
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent(); // Center vertically
                    g2.drawString("Search the products......", x, y);
                }

                super.paintComponent(g);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                // Do not paint border
            }

            @Override
            protected void processFocusEvent(FocusEvent e) {
                super.processFocusEvent(e);
                firstClick = false; // Disable placeholder on first click
                repaint();
            }
        };

        // ActionListener for Enter key press
        searchField.addActionListener(e -> {
            new CreateHomePanel().makeFilteredProductsForSearching(searchField.getText().toLowerCase()); // Call performSearch with searchField as argument
        });

        searchField.setOpaque(false); // Make text field transparent
        searchField.setBackground(COLOR_GRAY);
        searchField.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 13));
        searchField.setBorder(new RoundedCornerBorder(5, COLOR_GRAY, 1)); // Custom border for rounded corners
        searchField.setPreferredSize(new Dimension(220, 40));
        searchField.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // ActionListener for clearButton

        // Panel to hold the searchField and clearButton
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setPreferredSize(new Dimension(300, 40));
        searchPanel.add(searchField, BorderLayout.CENTER);


        GridBagConstraints gbcSearchPanel = new GridBagConstraints();
        gbcSearchPanel.gridx = 1;
        gbcSearchPanel.gridy = 0;
        gbcSearchPanel.insets = new Insets(0, 0, 5, 10); // Padding
        gbcSearchPanel.anchor = GridBagConstraints.WEST; // Align left
        topLeftMainPanel.add(searchPanel, gbcSearchPanel);

        ImageIcon searchIcon = new SvgIcon("/SearchIcon.svg", 22).getImageIcon();
        // GridBagConstraints for searchButton (unchanged)
        searchButton = new RoundedIconButton(searchIcon, 14, (COLOR_GRAY), 2);
        searchButton.setPreferredSize(new Dimension(38, 38));
        searchButton.setBackground(COLOR_GRAY); // Set background color for label

        // ActionListener for searchButton (unchanged)
        searchButton.addActionListener(e -> {
            String searchText = searchField.getText().trim().toLowerCase();

            if (searchText.isEmpty()) {
                showCustomDialog(new JFrame(), "Input something to search", "Invalid Search");
            } else {
                new CreateHomePanel().makeFilteredProductsForSearching(searchText);
            }
        });


        GridBagConstraints gbcSearchButton = new GridBagConstraints();
        gbcSearchButton.gridx = 2;
        gbcSearchButton.gridy = 0;
        gbcSearchButton.insets = new Insets(0, 0, 5, 5); // Padding
        gbcSearchButton.anchor = GridBagConstraints.WEST; // Align left
        topLeftMainPanel.add(searchButton, gbcSearchButton);

        return topLeftMainPanel;
    }


    protected static JPanel demonstrateTopRightMainPanel() {
        JPanel topRightMainPanel = new JPanel();
        topRightMainPanel.setBackground(COLOR_WHITE);

        // Using GridBagLayout for precise component positioning
        GridBagLayout gridBagLayout = new GridBagLayout();
        topRightMainPanel.setLayout(gridBagLayout);

        JButton refreshButton = getjButton();

        GridBagConstraints gbcRefreshButton = new GridBagConstraints();
        gbcRefreshButton.gridx = 0; // Leftmost column
        gbcRefreshButton.gridy = 0; // Top row
        gbcRefreshButton.insets = new Insets(0, 10, 5, 4); // Padding
        gbcRefreshButton.anchor = GridBagConstraints.WEST; // Align left
        topRightMainPanel.add(refreshButton, gbcRefreshButton);

        // Load Connected icon
        ImageIcon connectionIcon = new SvgIcon("/ConnectedIcon.svg", 22).getImageIcon();
        // GridBagConstraints for searchButton (unchanged)
        JButton connectionButton = new RoundedIconButton(connectionIcon, 14, (COLOR_GRAY), 2);
        connectionButton.setPreferredSize(new Dimension(38, 38));
        connectionButton.setBackground(COLOR_GRAY); // Set background color for label
        GridBagConstraints gbcConnectionLabel = new GridBagConstraints();
        gbcConnectionLabel.gridx = 1; // Center column
        gbcConnectionLabel.gridy = 0; // Top row
        gbcConnectionLabel.insets = new Insets(0, 10, 5, 4); // Padding
        gbcConnectionLabel.anchor = GridBagConstraints.CENTER; // Align center horizontally
        topRightMainPanel.add(connectionButton, gbcConnectionLabel);

        // Load Select Table icon (adjust path as needed)


        ImageIcon selectTableIcon = createResizedIcon("/SelectTableIcon.svg", 19, null);
        selectTablesButton = new IconTextButton("Seating Map", selectTableIcon, 14, (COLOR_ORANGE), 0);
        selectTablesButton.setBackground(COLOR_ORANGE);
        selectTablesButton.setForeground(COLOR_WHITE);
        selectTablesButton.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12));
        selectTablesButton.setPreferredSize(new Dimension(130, 38)); // Adjusted size for visibility

        // Set icon on the left side of the button text
        selectTablesButton.setIcon(selectTableIcon);
        // Set the text and icon alignment
        selectTablesButton.setHorizontalTextPosition(SwingConstants.RIGHT); // Icon on the left
        selectTablesButton.setVerticalTextPosition(SwingConstants.CENTER);
        selectTablesButton.setHorizontalAlignment(SwingConstants.CENTER);
        selectTablesButton.setVerticalAlignment(SwingConstants.CENTER);

        selectTablesButton.addActionListener((ActionEvent e) -> showPanel("SelectTables"));
        // GridBagConstraints for selectTableButton
        GridBagConstraints gbcSelectTableButton = new GridBagConstraints();
        gbcSelectTableButton.gridx = 2; // Rightmost column
        gbcSelectTableButton.gridy = 0; // Top row
        gbcSelectTableButton.insets = new Insets(0, 10, 5, 10); // Padding
        gbcSelectTableButton.anchor = GridBagConstraints.EAST; // Align right

        // Add button to the panel
        topRightMainPanel.add(selectTablesButton, gbcSelectTableButton);

        return topRightMainPanel;
    }

    private static JButton getjButton() {
        ImageIcon refreshIcon = new SvgIcon("/RefreshIcon.svg", 22).getImageIcon();
        // GridBagConstraints for refreshButton (with icon and border)
        JButton refreshButton = new RoundedIconButton(refreshIcon, 14, (COLOR_GRAY), 2);
        refreshButton.setPreferredSize(new Dimension(38, 38)); // Adjusted size for icon visibility
        refreshButton.setBackground(COLOR_GRAY); // Set background color for button
        // ActionListener for refreshButton
        refreshButton.addActionListener(e -> {
            // Disable the refresh button to prevent multiple clicks
            refreshButton.setEnabled(false);

            // Reset search field and button icon
            searchField.setText("");

            // Use SwingWorker to perform product updates in the background
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    new CreateHomePanel().updateDisplayProductDataInBackground();
                    return null;
                }

                @Override
                protected void done() {
                    SwingUtilities.invokeLater(() -> {
                        resetHomeNavigationAnimation();
                        // Show the "Home" panel and refresh the layout
                        showPanel("Home");
                        contentPanel.revalidate();
                        contentPanel.repaint();

                        // Scroll to the top smoothly
                        SwingUtilities.invokeLater(() -> scrollToTop(contentPanel));

                        // Re-enable the refresh button after the process is done
                        refreshButton.setEnabled(true);
                    });
                }
            }.execute();
        });

        return refreshButton;
    }

    private static JButton getCurrentButton() {
        return currentButton;
    }

    public static void resetHomeNavigationAnimation() {
        if (getCurrentButton() != null) {
            resetButtonBorder(getCurrentButton());
        }
        startHomeButtonAnimation();
    }

    // Helper method to scroll the content panel to the top smoothly
    private static void scrollToTop(JComponent panel) {
        JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, panel);
        if (scrollPane != null) {
            JViewport viewport = scrollPane.getViewport();
            viewport.setViewPosition(new Point(0, 0));
        }
    }


}
