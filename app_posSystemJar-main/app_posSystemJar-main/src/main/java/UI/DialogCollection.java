package UI;

import SqlLoadersAndUpdater.FetchSales;
import SqlLoadersAndUpdater.FetchTellerLists;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.Cashier.CreateSalesHistoryPanel;
import org.MiniDev.Cashier.CreateTodaySalePanel;
import org.MiniDev.OOP.DrawerSalesLists;
import org.MiniDev.OOP.SalesFilter;
import org.MiniDev.OOP.TellerLists;
import org.MiniDev.OOP.TodaySalesLists;
import org.MiniDev.combo_suggestion.ComboBoxSuggestion;
import raven.datetime.component.date.DateEvent;
import raven.datetime.component.date.DatePicker;
import raven.datetime.component.date.DateSelectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static UI.UserFinalSettingsVar.*;
import static org.MiniDev.Cashier.CreateTodaySalePanel.GAP;

public class DialogCollection {

    static {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    // Helper method to convert java.sql.Date to LocalDate
    private LocalDate convertToLocalDate(java.sql.Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Date(date.getTime()).toLocalDate(); // Create a new java.sql.Date and convert
    }

    public static void showCustomLogoutDialog(JFrame frame) {
        JDialog dialog = new JDialog(frame, "Logout Confirmation", true);
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

        JLabel messageLabel = new JLabel("Are you sure you want to logout?");
        messageLabel.setFont(UIManager.getFont("Label.font"));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton yesButton = new JButton("Yes");
        yesButton.setPreferredSize(new Dimension(80, 30));
        JButton noButton = new JButton("No");
        noButton.setPreferredSize(new Dimension(80, 30));

        yesButton.addActionListener((ActionEvent e) -> System.exit(0));
        noButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(UIManager.getColor("Panel.background"));
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        contentPane.add(messageLabel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        dialog.getContentPane().add(contentPane);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(frame);

        // Set rounded corners for the dialog
        dialog.setShape(new RoundRectangle2D.Double(0, 0, dialog.getWidth(), dialog.getHeight(), 20, 20));

        dialog.setVisible(true);
    }


    public static void showCustomDialog(JFrame parentFrame, String message, String title) {
        JDialog dialog = new JDialog(parentFrame, title, true);
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

        // Create the warning sign icon
        SvgIcon warningIcon = new SvgIcon("/WarningSignIcon.svg", 30); // Ensure this path is correct
        JLabel iconLabel = new JLabel(warningIcon.getImageIcon());

        JLabel messageLabel = new JLabel("<html><body style='width: 100%;'>" + message + "</body></html>");
        messageLabel.setFont(UIManager.getFont("Label.font"));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel messagePanel = new JPanel(new BorderLayout(10, 10));
        messagePanel.setBackground(UIManager.getColor("Panel.background"));
        messagePanel.add(iconLabel, BorderLayout.WEST);
        messagePanel.add(messageLabel, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(80, 30));
        okButton.addActionListener((ActionEvent e) -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(UIManager.getColor("Panel.background"));
        buttonPanel.add(okButton);

        contentPane.add(messagePanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        dialog.getContentPane().add(contentPane);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(parentFrame);

        // Set rounded corners for the dialog
        dialog.setShape(new RoundRectangle2D.Double(0, 0, dialog.getWidth(), dialog.getHeight(), 20, 20));

        dialog.setVisible(true);
    }

    public static void showCustomDialog(JPanel panel, String message, String title) {
        // Create a dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(panel), title, true);
        dialog.setUndecorated(true);

        // Panel for the dialog's content
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

        // Create the warning sign icon
        SvgIcon warningIcon = new SvgIcon("/WarningSignIcon.svg", 30); // Ensure this path is correct
        JLabel iconLabel = new JLabel(warningIcon.getImageIcon());

        // Create message label
        JLabel messageLabel = new JLabel("<html><body style='width: 100%;'>" + message + "</body></html>");
        messageLabel.setFont(UIManager.getFont("Label.font"));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Create and set up the message panel
        JPanel messagePanel = new JPanel(new BorderLayout(10, 10));
        messagePanel.setBackground(UIManager.getColor("Panel.background"));
        messagePanel.add(iconLabel, BorderLayout.WEST);
        messagePanel.add(messageLabel, BorderLayout.CENTER);

        // Create and set up the OK button
        JButton okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(80, 30));
        okButton.addActionListener((ActionEvent e) -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(UIManager.getColor("Panel.background"));
        buttonPanel.add(okButton);

        // Add components to the content pane
        contentPane.add(messagePanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        // Add content pane to the dialog
        dialog.getContentPane().add(contentPane);
        dialog.pack();

        // Position the dialog relative to the given panel
        dialog.setLocationRelativeTo(panel);

        // Set rounded corners for the dialog
        dialog.setShape(new RoundRectangle2D.Double(0, 0, dialog.getWidth(), dialog.getHeight(), 20, 20));

        // Display the dialog
        dialog.setVisible(true);
    }

    private RoundedComboBox<String> comboBoxSuggestion1 = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);
    private RoundedComboBox<String> comboBoxSuggestion2 = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);
    private RoundedComboBox<String> comboBoxSuggestion3 = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);
    private RoundedComboBox<String> comboBoxSuggestion4 = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);

    private final RoundedComboBox<String> comboBoxSuggestionHist1 = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);
    private final RoundedComboBox<String> comboBoxSuggestionHist2 = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);
    private final RoundedComboBox<String> comboBoxSuggestionHist3 = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);


    public void showFilterPopUp(String title) {
        // Create a dialog
        RoundedPanel contentPane = new RoundedPanel(20);
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(contentPane), title, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.setSize(550, 350);
        dialog.setLocationRelativeTo(null);
        dialog.setShape(new RoundRectangle2D.Double(0, 0, dialog.getWidth(), dialog.getHeight(), 5, 5));


        contentPane.setBackground(COLOR_GRAY);
        contentPane.setLayout(new BorderLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        contentPane.setBorderWidth(1);
        contentPane.setBorderColor(COLOR_LINE_COLOR);
        contentPane.setPreferredSize(new Dimension(dialog.getWidth(), dialog.getHeight()));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(4, 2, GAP, GAP));
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
                (BorderFactory.createMatteBorder(1, 0, 1, 0, COLOR_LINE_COLOR)),
                (BorderFactory.createEmptyBorder(GAP, GAP * 2, GAP, GAP * 2))
        ));

        centerPanel.setBackground(COLOR_GRAY);

        PanelRound headerPanel = new PanelRound();
        headerPanel.setBackground(COLOR_WHITE);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(centerPanel.getWidth(), 50));
        headerPanel.setRoundTopRight(16);
        headerPanel.setRoundTopLeft(16);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, GAP, 0, GAP));

        PanelRound bottomPanel = new PanelRound();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBackground(COLOR_WHITE);
        bottomPanel.setPreferredSize(new Dimension(centerPanel.getWidth(), 60));
        bottomPanel.setRoundBottomRight(16);
        bottomPanel.setRoundBottomLeft(16);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, GAP));

        JPanel buttonInnerPanel = new JPanel(new GridLayout(1, 2, GAP, 5));
        buttonInnerPanel.setBackground(COLOR_WHITE);
        buttonInnerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        buttonInnerPanel.setPreferredSize(new Dimension(250, 60));


        JLabel titleLabel = new JLabel("All Filters");
        titleLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 14));


        headerPanel.add(titleLabel, BorderLayout.WEST);

        buttonInnerPanel.add(getClearAllFilterButton());
        buttonInnerPanel.add(getApplyFilterButton(dialog));
        bottomPanel.add(buttonInnerPanel, BorderLayout.EAST);

        List<String> orderConditions = new ArrayList<>(List.of("All Orders", "Order Finished", "Order Holding"));
        initializeComboBox(comboBoxSuggestion1, orderConditions, "All Orders");

        List<String> orderTypes = new ArrayList<>(List.of("All Types", "Dine In", "To Go", "Delivery"));
        initializeComboBox(comboBoxSuggestion2, orderTypes, "All Types");

        List<String> paymentTypes = new ArrayList<>(List.of("All Payment Methods", "Cash", "Other Payment"));
        initializeComboBox(comboBoxSuggestion3, paymentTypes, "All Payment Methods");


        // Fetch the teller list
        FetchTellerLists fetchTellerLists = new FetchTellerLists();
        List<TellerLists> tellerLists = fetchTellerLists.getTellerLists();

        // Extract the teller names into a String array
        String[] tellerNames = tellerLists.stream()
                .map(TellerLists::getTellerName) // Assuming `getTellerName()` returns the teller's name
                .toArray(String[]::new);

        // Create a new array with "All Cashier" as the first element
        String[] comboBoxItems = new String[tellerNames.length + 1];
        comboBoxItems[0] = "All Cashiers";
        System.arraycopy(tellerNames, 0, comboBoxItems, 1, tellerNames.length);

        // Set the model of the combo box with the combined array
        initializeComboBox(comboBoxSuggestion4, comboBoxItems, "All Cashiers");


        centerPanel.add(comboBoxSuggestion1);
        centerPanel.add(comboBoxSuggestion2);
        centerPanel.add(comboBoxSuggestion3);
        centerPanel.add(comboBoxSuggestion4);

        contentPane.add(headerPanel, BorderLayout.NORTH);
        contentPane.add(centerPanel, BorderLayout.CENTER);
        contentPane.add(bottomPanel, BorderLayout.SOUTH);

        // Add content pane to the dialog
        dialog.setContentPane(contentPane);
        dialog.pack();

        // Position the dialog relative to the given panel
        dialog.setLocationRelativeTo(contentPane);
        dialog.setVisible(true);
    }


    private IconTextButton getApplyFilterButton(JDialog dialog) {
        IconTextButton applyButton = new IconTextButton("Apply Filter", null, 14, COLOR_ORANGE, 0);
        applyButton.setBackground(COLOR_ORANGE);
        applyButton.setForeground(Color.WHITE);
        applyButton.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));
        applyButton.setPreferredSize(new Dimension(100, 40));

        applyButton.addActionListener(e -> {
            dialog.dispose();

            // Capture the selected items from the combo boxes
            int selectedOption1 = comboBoxSuggestion1.getSelectedIndex();
            int selectedOption2 = comboBoxSuggestion2.getSelectedIndex();
            int selectedOption3 = comboBoxSuggestion3.getSelectedIndex();
            String selectedOption4 = (String) comboBoxSuggestion4.getSelectedItem();

            // Fetch the updated list of sales
            FetchSales fetchTodaySales = new FetchSales();
            List<TodaySalesLists> salesList = fetchTodaySales.getTodaySalesLists();

            // Define predicates based on the selected options
            Predicate<TodaySalesLists> filterByOrderFinishYN = sale -> {
                switch (selectedOption1) {
                    case 1 -> {
                        return sale.getOrderFinishYN() == 'Y';
                    }
                    case 2 -> {
                        return sale.getOrderFinishYN() == 'N';
                    }
                    default -> {
                        return true;
                    }
                }
            };

            Predicate<TodaySalesLists> filterByDineInToGoDelivery = sale -> {
                switch (selectedOption2) {
                    case 1 -> {
                        return Objects.equals(sale.getDineInToGoDelivery(), "Dine In");
                    }
                    case 2 -> {
                        return Objects.equals(sale.getDineInToGoDelivery(), "To Go");
                    }
                    case 3 -> {
                        return Objects.equals(sale.getDineInToGoDelivery(), "Delivery");
                    }
                    default -> {
                        return true;
                    }
                }
            };


            Predicate<TodaySalesLists> filterByPaymentMethod = sale -> {
                switch (selectedOption3) {
                    case 1 -> {
                        return Objects.equals(sale.getPaymentMethod(), "Cash");
                    }
                    case 2 -> {
                        return Objects.equals(sale.getPaymentMethod(), "Other Payment");
                    }
                    default -> {
                        return true;
                    }
                }
            };


            Predicate<TodaySalesLists> filterByCashier = sale -> {
                assert selectedOption4 != null;
                return selectedOption4.equals("All Cashiers") || selectedOption4.equals(sale.getClosedCashierName());
            };

            // Combine predicates
            List<TodaySalesLists> filteredSalesList = SalesFilter.filterSalesList(salesList,
                    filterByOrderFinishYN.and(filterByPaymentMethod).and(filterByDineInToGoDelivery).and(filterByCashier));

            // Refresh the panel with the filtered data
            CreateTodaySalePanel.refreshDetailsPanel(filteredSalesList);
        });

        return applyButton;
    }


    protected IconTextButton getApplyFilterButtonForSaleHistory(JDialog dialog) {
        IconTextButton applyButton = new IconTextButton("Apply Filter", null, 14, COLOR_ORANGE, 0);
        applyButton.setBackground(COLOR_ORANGE);
        applyButton.setForeground(Color.WHITE);
        applyButton.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));
        applyButton.setPreferredSize(new Dimension(100, 40));


        applyButton.addActionListener(e -> {
            dialog.dispose();

            // Get selected options from combo boxes
            int selectedOption1 = comboBoxSuggestionHist1.getSelectedIndex();
            int selectedOption2 = comboBoxSuggestionHist2.getSelectedIndex();
            String selectedOption3 = (String) comboBoxSuggestionHist3.getSelectedItem();

            // Fetch the updated list of sales
            FetchSales fetchSales = new FetchSales();
            List<DrawerSalesLists> drawerSalesLists = fetchSales.getSaleHistoryAll();

            // Get the date range
            LocalDate[] datesForSaleHist = datePicker.getSelectedDateRange();
            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            if (datesForSaleHist != null && datesForSaleHist[0] != null && datesForSaleHist[1] != null) {
                System.out.println("Start Date: " + df.format(datesForSaleHist[0]));
                System.out.println("End Date: " + df.format(datesForSaleHist[1]));
            } else {
                System.out.println("Date range is not selected or invalid.");
            }

            // Define predicates based on the selected options
            Predicate<DrawerSalesLists> filterByClosedOpened = sale -> switch (selectedOption1) {
                case 1 -> sale.getClosedStatus() == 'Y';
                case 2 -> sale.getClosedStatus() == 'N';
                default -> true;
            };

            Predicate<DrawerSalesLists> filterByPaymentMethod = sale -> switch (selectedOption2) {
                case 1 -> sale.getPaymentMethods().equals("CASH");
                case 2 -> sale.getPaymentMethods().equals("OPM");
                default -> true;
            };

            Predicate<DrawerSalesLists> filterByDate = sale -> {
                if (datesForSaleHist == null || datesForSaleHist[0] == null || datesForSaleHist[1] == null) {
                    return true;  // No date range specified
                }

                LocalDate drawerDate = sale.getDrawerDate();

                // Make sure drawerDate is not null and falls within the range, including boundary dates
                return drawerDate != null
                        && !drawerDate.isBefore(datesForSaleHist[0])
                        && !drawerDate.isAfter(datesForSaleHist[1]);
            };


            Predicate<DrawerSalesLists> filterByCashier = sale ->
                    selectedOption3.equals("All Cashiers") || selectedOption3.equals(sale.getTellerName());

            // Combine the filters and apply them
            List<DrawerSalesLists> filteredDrawerSalesList = SalesFilter.filterDrawerSalesList(
                    drawerSalesLists, filterByClosedOpened.and(filterByPaymentMethod).and(filterByDate).and(filterByCashier)
            );

            // Refresh the panel with the filtered data
            CreateSalesHistoryPanel.refreshDetailsSalesHistPanel(filteredDrawerSalesList);
        });

        return applyButton;
    }


    public RoundedBorderButton getClearAllFilterButton() {
        RoundedBorderButton clearButton = new RoundedBorderButton("Clear All Filter", 100, 40, COLOR_ORANGE);
        clearButton.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));
        clearButton.setForeground(COLOR_ORANGE);
        clearButton.setBackground(COLOR_BUTTON_ORANGE);

        clearButton.addActionListener(e -> {

            clearComboSuggestionsFields(comboBoxSuggestion1);
            clearComboSuggestionsFields(comboBoxSuggestion2);
            clearComboSuggestionsFields(comboBoxSuggestion3);
            clearComboSuggestionsFields(comboBoxSuggestion4);

            CreateTodaySalePanel.refreshDetailsPanel();
        });

        return clearButton;
    }

    private void clearComboSuggestionsFields(RoundedComboBox<String> comboBox) {
        comboBox.setSelectedIndex(0);
        JTextField editorField = (JTextField) comboBox.getEditor().getEditorComponent();
        if (editorField != null) {
            editorField.setForeground(Color.GRAY); // Set initial placeholder color
        }
    }

    public RoundedBorderButton getClearAllFilterButtonForSaleHistory() {
        RoundedBorderButton clearButton = new RoundedBorderButton("Clear All Filter", 100, 40, COLOR_ORANGE);
        clearButton.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));
        clearButton.setForeground(COLOR_ORANGE);
        clearButton.setBackground(COLOR_BUTTON_ORANGE);

        clearButton.addActionListener(e -> {
            clearComboSuggestionsFields(comboBoxSuggestionHist1);
            clearComboSuggestionsFields(comboBoxSuggestionHist2);
            clearComboSuggestionsFields(comboBoxSuggestionHist2);
            if (datePicker.getSelectedDateRange() != null) {
                datePicker.clearSelectedDate();
            }
            CreateSalesHistoryPanel.refreshDetailsSalesHistPanel();
        });

        return clearButton;
    }


    public void showFilterPopUpForSaleHist(String title) {
        RoundedPanel contentPane = new RoundedPanel(20);
        // Create a dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(contentPane), title, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.setSize(550, 350);
        dialog.setLocationRelativeTo(null);
        dialog.setShape(new RoundRectangle2D.Double(0, 0, dialog.getWidth(), dialog.getHeight(), 5, 5));

        contentPane.setBackground(COLOR_GRAY);
        contentPane.setLayout(new BorderLayout());

        contentPane.setBorderWidth(1);
        contentPane.setBorderColor(COLOR_LINE_COLOR);

        contentPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        contentPane.setPreferredSize(new Dimension(dialog.getWidth(), dialog.getHeight()));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(4, 2, GAP, GAP));
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
                (BorderFactory.createMatteBorder(1, 0, 1, 0, COLOR_LINE_COLOR)),
                (BorderFactory.createEmptyBorder(GAP, GAP * 2, GAP, GAP * 2))
        ));

        centerPanel.setBackground(COLOR_GRAY);

        PanelRound headerPanel = new PanelRound();
        headerPanel.setBackground(COLOR_WHITE);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(centerPanel.getWidth(), 50));
        headerPanel.setRoundTopRight(16);
        headerPanel.setRoundTopLeft(16);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, GAP, 0, GAP));

        PanelRound bottomPanel = new PanelRound();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBackground(COLOR_WHITE);
        bottomPanel.setPreferredSize(new Dimension(centerPanel.getWidth(), 60));
        bottomPanel.setRoundBottomRight(16);
        bottomPanel.setRoundBottomLeft(16);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, GAP));

        JPanel buttonInnerPanel = new JPanel(new GridLayout(1, 2, GAP, 5));
        buttonInnerPanel.setBackground(COLOR_WHITE);
        buttonInnerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        buttonInnerPanel.setPreferredSize(new Dimension(250, 60));


        JLabel titleLabel = new JLabel("All Filters");
        titleLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 14));


        headerPanel.add(titleLabel, BorderLayout.WEST);


        buttonInnerPanel.add(getClearAllFilterButtonForSaleHistory());
        buttonInnerPanel.add(getApplyFilterButtonForSaleHistory(dialog));
        bottomPanel.add(buttonInnerPanel, BorderLayout.EAST);

        RoundedPanel datePickerPanel = createDatePickerPanelForSaleHist(datePicker, editor, false);

        List<String> conditionsOptions = new ArrayList<>(List.of("All Conditions", "Drawer Closed", "Drawer Opening"));
        initializeComboBox(comboBoxSuggestionHist1, conditionsOptions, "All Conditions");


        List<String> paymentOptions = new ArrayList<>(List.of("All Payment Methods", "Cash", "Other Payment"));
        initializeComboBox(comboBoxSuggestionHist2, paymentOptions, "All Payment Methods");


        // Fetch the teller list
        FetchTellerLists fetchTellerLists = new FetchTellerLists();
        List<TellerLists> tellerLists = fetchTellerLists.getTellerLists();

        if (tellerLists != null) {
            // Extract the teller names into a String array
            String[] tellerNames = tellerLists.stream()
                    .map(TellerLists::getTellerName) // Assuming `getTellerName()` returns the teller's name
                    .toArray(String[]::new);

            // Create a new array with "All Cashier" as the first element
            String[] comboBoxItems = new String[tellerNames.length + 1];
            comboBoxItems[0] = "All Cashiers";
            System.arraycopy(tellerNames, 0, comboBoxItems, 1, tellerNames.length);

            // Initialize the combo box
            initializeComboBox(comboBoxSuggestionHist3, comboBoxItems, "All Cashiers");
        } else {
            // Handle the case where tellerLists is null (e.g., no data fetched)
            initializeComboBox(comboBoxSuggestionHist3, new String[]{"All Cashiers"}, "All Cashiers");
        }

        centerPanel.add(datePickerPanel);
        centerPanel.add(comboBoxSuggestionHist1);
        centerPanel.add(comboBoxSuggestionHist2);
        centerPanel.add(comboBoxSuggestionHist3);


        contentPane.add(headerPanel, BorderLayout.NORTH);
        contentPane.add(centerPanel, BorderLayout.CENTER);
        contentPane.add(bottomPanel, BorderLayout.SOUTH);
        // Add content pane to the dialog
        dialog.setContentPane(contentPane);
        dialog.pack();

        // Position the dialog relative to the given panel
        dialog.setLocationRelativeTo(contentPane);
        dialog.setVisible(true);
    }

    private void initializeComboBox(RoundedComboBox<String> comboBox, List<String> items, String defaultSelection) {
        String[] itemArray = items.toArray(new String[0]);
        comboBox.setModel(new DefaultComboBoxModel<>(itemArray));
        comboBox.setPlaceholder(defaultSelection);
        comboBox.setSelectedItem(defaultSelection);

        JTextField editorField = (JTextField) comboBox.getEditor().getEditorComponent();
        if (editorField != null) {
            editorField.setForeground(Color.GRAY); // Set initial placeholder color
        }
    }

    private void initializeComboBox(RoundedComboBox<String> comboBox, String[] itemArray, String defaultSelection) {
        comboBox.setModel(new DefaultComboBoxModel<>(itemArray));
        comboBox.setPlaceholder(defaultSelection);
        comboBox.setSelectedItem(defaultSelection);

        JTextField editorField = (JTextField) comboBox.getEditor().getEditorComponent();
        if (editorField != null) {
            editorField.setForeground(Color.GRAY); // Set initial placeholder color
        }
    }

    protected LocalDate[] datesForSaleHist;
    protected DatePicker datePicker = new DatePicker();
    protected JFormattedTextField editor = new JFormattedTextField();

    private RoundedPanel createDatePickerPanelForSaleHist(DatePicker datePicker, JFormattedTextField editor, boolean isEditOff) {
        RoundedPanel right = new RoundedPanel(10);
        right.setOpaque(false);
        right.setBackground(COLOR_WHITE);
        right.setBorderWidth(1);
        right.setBorderColor(COLOR_LINE_COLOR);
        right.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        datePicker.setDateSelectionMode(DatePicker.DateSelectionMode.BETWEEN_DATE_SELECTED);
        datePicker.setUsePanelOption(false);
        datePicker.setEnabled(!isEditOff);

        editor.setPreferredSize(new Dimension(120, 30)); // Fixed width for the editor
        editor.setBorder(null);
        datePicker.setEditor(editor);
        datePicker.setBackground(COLOR_WHITE);
        datePicker.setBorder(null);

        right.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        datePicker.addDateSelectionListener(dateEvent -> {
            datesForSaleHist = datePicker.getSelectedDateRange();
        });

        FocusListener focusListener = new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                right.setBorderColor(COLOR_SELECT_BLUE);
            }

            @Override
            public void focusLost(FocusEvent e) {
                right.setBorderColor(COLOR_LINE_COLOR);
            }
        };

        editor.addFocusListener(focusListener);
        datePicker.addFocusListener(focusListener);

        right.setPreferredSize(new Dimension(150, 30)); // Fixed size for the RoundedPanel
        right.add(editor, gbc);

        return right;
    }


}
