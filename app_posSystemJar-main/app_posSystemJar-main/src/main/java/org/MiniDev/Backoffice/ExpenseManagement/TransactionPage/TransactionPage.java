package org.MiniDev.Backoffice.ExpenseManagement.TransactionPage;

import SqlLoadersAndUpdater.FetchExpenseTransactionLists;
import UI.*;
import UI.CustomCellEditorTable.*;
import UI.CustomFloatingButton.floating.FloatingButtonUI;
import org.MiniDev.Backoffice.ExpenseManagement.TransactionPage.TrnhistTableActionPages.ExpTrnHistDeletePage;
import org.MiniDev.Backoffice.ExpenseManagement.TransactionPage.TrnhistTableActionPages.ExpTrnHistEditPage;
import org.MiniDev.Backoffice.ExpenseManagement.TransactionPage.TrnhistTableActionPages.ExpTrnHistViewPage;
import org.MiniDev.Home.MiniDevPOS;
import org.MiniDev.OOP.ExpenseTransactionLists;

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import raven.datetime.component.date.DatePicker;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import javax.swing.JOptionPane;

import static UI.IconCreator.createResizedIcon;
import static UI.UserFinalSettingsVar.*;

public class TransactionPage extends Component {

    protected static DefaultTableModel modelForExpTransaction;
    protected static JTable expTransactionTable;
    protected JScrollPane expTransactionTableScrollPane;
    protected JPanel expTransactionInnerPanel;
    protected RoundedPanel tableRoundedExpTransactionPanel;
    protected JTableHeader expTransactionTableHeader;
    protected static List<ExpenseTransactionLists> expTransactionArrayListsToUpdate; // all
    protected static List<ExpenseTransactionLists> filteredList; // temp instance
    protected static List<ExpenseTransactionLists> filteredThisMonthList; // this month

    public TransactionPage() {
    }

    /**
     * Creates the main StockInnerPage JPanel.
     */
    protected IconTextButton exportDownload;

    public JPanel createExpTransactionPage() {
        expTransactionInnerPanel = new JPanel(new BorderLayout());
        expTransactionInnerPanel.setOpaque(false);

        JPanel searchHoldingPanel = new JPanel(new BorderLayout());
        JPanel tableHoldingPanel = new JPanel(new BorderLayout());

        searchHoldingPanel.setBackground(COLOR_WHITE);
        searchHoldingPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));
        tableHoldingPanel.setOpaque(false);

        JPanel rightContainersPanel = new JPanel();
        rightContainersPanel.setOpaque(false);
        rightContainersPanel.setLayout(new BoxLayout(rightContainersPanel, BoxLayout.X_AXIS));
        rightContainersPanel.setPreferredSize(new Dimension(700, 40));

        exportDownload = createIconButtonForCounter("Export", "/ExcelIcon.svg", 14, Color.decode("#65a30d"), COLOR_WHITE, 90, 35);

        exportDownload.addActionListener(e -> {

            String filePath = "ExpenseTransactions.xls";  // Define the file path for the exported file
            if (filteredList == null) {
                exportExpenseTransactionListsToExcel(filteredThisMonthList, filePath);  // Export data to Excel
            } else {
                exportExpenseTransactionListsToExcel(filteredList, filePath);  // Export data to Excel
            }
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

        });

        rightContainersPanel.add(createDatePickerToFilter());
        rightContainersPanel.add(Box.createHorizontalStrut(7));
        rightContainersPanel.add(exportDownload);
        rightContainersPanel.add(Box.createHorizontalStrut(7));
        rightContainersPanel.add(getTransactionTextField());

        searchHoldingPanel.add(rightContainersPanel, BorderLayout.EAST);

        tableHoldingPanel.add(new JLayer(createTableWithActions(), new FloatingButtonUI("EXP-NEW")));

        // Add panels to the main stockInnerPanel
        expTransactionInnerPanel.add(searchHoldingPanel, BorderLayout.NORTH);
        expTransactionInnerPanel.add(tableHoldingPanel, BorderLayout.CENTER);

        return expTransactionInnerPanel;
    }


    protected static DatePicker datePickerForExpRawTrn = new DatePicker();
    protected static JFormattedTextField expRawTrnTextField = new JFormattedTextField();

    private Component createDatePickerToFilter() {
        return getDateChooserField(datePickerForExpRawTrn, expRawTrnTextField, false);
    }

    public RoundedPanel getDateChooserField(DatePicker datePicker, JFormattedTextField editor, boolean isEditOff) {
        RoundedPanel right = new RoundedPanel(10);
        right.setOpaque(false);
        right.setBackground(COLOR_WHITE);
        right.setBorderWidth(1);
        right.setBorderColor(COLOR_LINE_COLOR);
        right.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        datePicker.setDateSelectionMode(DatePicker.DateSelectionMode.BETWEEN_DATE_SELECTED);
        datePicker.setUsePanelOption(false);
        datePicker.setEnabled(!isEditOff);

        editor.setPreferredSize(new Dimension(150, 30)); // Fixed width for the editor
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

        datePicker.addDateSelectionListener(e -> handleDateSelection(datePicker));

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


    private void handleDateSelection(DatePicker datePicker) {
        LocalDate[] dates = datePicker.getSelectedDateRange();
        if (dates != null) {
            LocalDate startDate = dates[0];
            LocalDate endDate = dates[1];
            // Filter the attendance records based on the date range
            filterExpenseByDateRange(startDate, endDate);
        }
    }


    private void filterExpenseByDateRange(LocalDate startDate, LocalDate endDate) {
        filteredList = new ArrayList<>();

        for (ExpenseTransactionLists expense : expTransactionArrayListsToUpdate) {
            // Assuming getAttendanceDate() returns a java.sql.Date
            Date sqlDate = expense.getTrnDate();
            LocalDate payDate = sqlDate.toLocalDate(); // Convert to LocalDate

            // Check if the attendance date is within the specified range
            if ((payDate.isEqual(startDate) || payDate.isAfter(startDate)) && (payDate.isEqual(endDate) || payDate.isBefore(endDate))) {
                filteredList.add(expense);
            }
        }

        // Update the table model with the filtered list
        modelForExpTransaction = createCustomModel(filteredList);
        expTransactionTable.setModel(modelForExpTransaction);
        setupTableRenderersAndEditors();
        expTransactionTable.revalidate();
        expTransactionTable.repaint();
    }


    private List<ExpenseTransactionLists> getArrayListsThisMonth(List<ExpenseTransactionLists> allLists) {
        filteredThisMonthList = new ArrayList<>();

        for (ExpenseTransactionLists expense : allLists) {
            // Assuming getAttendanceDate() returns a java.sql.Date
            Date sqlDate = expense.getTrnDate();
            LocalDate payDate = sqlDate.toLocalDate(); // Convert to LocalDate

            // Check if the attendance date is within the specified range
            if ((payDate.isEqual(LocalDate.now().withDayOfMonth(1)) || payDate.isAfter(LocalDate.now().withDayOfMonth(1))) &&
                    (payDate.isEqual(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())) || payDate.isBefore(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())))) {
                filteredThisMonthList.add(expense);
            }
        }

        return filteredThisMonthList;
    }

    /**
     * Sets up custom renderers and editors for the table columns.
     */
    private void setupTableRenderersAndEditors() {
        expTransactionTable.getColumnModel().getColumn(8).setCellRenderer(new TableActionCellRender());
        expTransactionTable.getColumnModel().getColumn(8).setCellEditor(new TableActionCellEditor(expEvent));

        expTransactionTable.getColumnModel().getColumn(0).setCellRenderer(createCenteredRenderer());
        expTransactionTable.getColumnModel().getColumn(1).setCellRenderer(createCenteredRenderer());
        expTransactionTable.getColumnModel().getColumn(2).setCellRenderer(createCenteredRenderer());
        expTransactionTable.getColumnModel().getColumn(7).setCellRenderer(createCenteredRenderer());

        expTransactionTable.getColumnModel().getColumn(5).setCellRenderer(createRightAlignedRenderer());
        expTransactionTable.getColumnModel().getColumn(6).setCellRenderer(createRightAlignedRenderer());

        expTransactionTable.getColumnModel().getColumn(0).setMaxWidth(50);
        expTransactionTable.getColumnModel().getColumn(1).setPreferredWidth(70);
        expTransactionTable.getColumnModel().getColumn(2).setPreferredWidth(45);
        expTransactionTable.getColumnModel().getColumn(3).setPreferredWidth(135);
        expTransactionTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        expTransactionTable.getColumnModel().getColumn(5).setPreferredWidth(30);
        expTransactionTable.getColumnModel().getColumn(6).setPreferredWidth(25);
        expTransactionTable.getColumnModel().getColumn(7).setPreferredWidth(30);
    }

    /**
     * Creates a TableCellRenderer that centers text.
     *
     * @return TableCellRenderer that centers text
     */
    private TableCellRenderer createCenteredRenderer() {
        DefaultTableCellRenderer centeredRenderer = new DefaultTableCellRenderer();
        centeredRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        return centeredRenderer;
    }

    private DefaultTableCellRenderer createRightAlignedRenderer() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.RIGHT);
        return renderer;
    }


    protected void filterExpenseTransactions(String query) {
        String lowerCaseQuery = query.toLowerCase();
        List<ExpenseTransactionLists> filteredExpenseList = new ArrayList<>();

        // Determine the appropriate source list based on the selected item in the combo box
        List<ExpenseTransactionLists> sourceList;
        if ((filteredList == null)) {
            Objects.requireNonNull(datePickerForExpRawTrn.getSelectedDateRange());
            sourceList = expTransactionArrayListsToUpdate;
        } else {
            sourceList = filteredList;
        }

        // Filter the source list based on the query
        for (ExpenseTransactionLists oneExpense : sourceList) {
            String expenseName = oneExpense.getExpName().toLowerCase();
            String expenseCode = String.valueOf(oneExpense.getExpCode());

            if (expenseName.contains(lowerCaseQuery) || expenseCode.contains(lowerCaseQuery)) {
                filteredExpenseList.add(oneExpense);
            }
        }

        // Update the table model
        modelForExpTransaction = createCustomModel(filteredExpenseList);
        expTransactionTable.setModel(modelForExpTransaction);
        setupTableRenderersAndEditors();
        expTransactionTable.revalidate();
        expTransactionTable.repaint();
    }


    /**
     * Creates the search text field for the stock search bar.
     */
    protected SearchTopPaneCreator getTransactionTextField() {
        SearchTopPaneCreator searchTopPaneCreator = new SearchTopPaneCreator("Search expense .....", "/SearchIcon.svg");
        searchTopPaneCreator.setPreferredSize(new Dimension(220, 40));

        JTextField searchExpenseField = searchTopPaneCreator.getSearchTextField();

        searchExpenseField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterExpenseTransactions(searchExpenseField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterExpenseTransactions(searchExpenseField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterExpenseTransactions(searchExpenseField.getText());
            }
        });

        return searchTopPaneCreator;
    }

    private DefaultTableModel createCustomModel(List<ExpenseTransactionLists> expTransactionArrayListsToUpdate) {
        modelForExpTransaction = new DefaultTableModel(
                new Object[][]{},
                new String[]{"#", "ID", "TrnDate", "Category", "Title", "Expense", "Refund", "AssignedTo", "Action"}
        ) {
            final boolean[] canEdit = new boolean[]{false, false, false, false, false, false, false, false, true};

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };

        int rowIndex = 1;
        DecimalFormat df = new DecimalFormat("#,###");

        for (ExpenseTransactionLists oneExpense : expTransactionArrayListsToUpdate) {

            modelForExpTransaction.addRow(new Object[]{
                    rowIndex++,
                    oneExpense.getExpUsedID(),
                    oneExpense.getTrnDate(),
                    oneExpense.getExpName(),
                    oneExpense.getExpRemark(),
                    df.format(oneExpense.getExpAmount()),
                    df.format(oneExpense.getRefundAmount()),
                    oneExpense.getAssignedToEmployee(),
                    null // This will be for the action buttons (Edit, Delete, View)
            });
        }

        return modelForExpTransaction;
    }

    protected static TableActionEvent expEvent;

    private RoundedPanel createTableWithActions() {
        tableRoundedExpTransactionPanel = new RoundedPanel(10);
        tableRoundedExpTransactionPanel.setLayout(new BorderLayout());
        tableRoundedExpTransactionPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        tableRoundedExpTransactionPanel.setBackground(COLOR_PANEL_GRAY);

        FetchExpenseTransactionLists fetchExpenseTransactionLists = new FetchExpenseTransactionLists();
        expTransactionArrayListsToUpdate = fetchExpenseTransactionLists.getExpenseTransactionLists();

        modelForExpTransaction = createCustomModel(getArrayListsThisMonth(expTransactionArrayListsToUpdate));
        expTransactionTable = new JTable(modelForExpTransaction) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    component.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_PANEL_GRAY);
                } else {
                    component.setBackground(COLOR_SELECT_BLUE);
                }
                return component;
            }
        };

        expTransactionTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder());
                return this;
            }
        });

        expTransactionTable.setAutoCreateRowSorter(true);

        expTransactionTableHeader = expTransactionTable.getTableHeader();
        expTransactionTableHeader.setBackground(COLOR_PANEL_GRAY);
        expTransactionTableHeader.setReorderingAllowed(true);
        expTransactionTableHeader.setPreferredSize(new Dimension(100, 40));
        expTransactionTable.setTableHeader(expTransactionTableHeader);

        expTransactionTable.setOpaque(true);
        expTransactionTable.setBackground(COLOR_PANEL_GRAY);
        expTransactionTable.setRowHeight(55);
        expTransactionTable.setSelectionBackground(COLOR_SELECT_BLUE);
        expTransactionTable.setShowHorizontalLines(false);
        expTransactionTable.setShowVerticalLines(false);

        expEvent = new TableActionEvent() {
            @Override
            public void onEdit(int row) {
                String cellValue = String.valueOf(expTransactionTable.getValueAt(row, 1));
                toEditExpTrnHistList(cellValue);
            }

            @Override
            public void onDelete(int row) {
                if (expTransactionTable.isEditing()) {
                    expTransactionTable.getCellEditor().stopCellEditing();
                }
                String cellValue = String.valueOf(expTransactionTable.getValueAt(row, 1));
                new ExpTrnHistDeletePage().showConfirmationToCancelExpTrnHist(cellValue, MiniDevPOS.contentPanel);
            }

            @Override
            public void onView(int row) {
                String cellValue = String.valueOf(expTransactionTable.getValueAt(row, 1));
                toViewExpTrnHistList(cellValue);
            }
        };


        setupTableRenderersAndEditors();

        // Initialize the scroll pane and add the table to it
        expTransactionTableScrollPane = new JScrollPane(expTransactionTable);
        expTransactionTableScrollPane.setBackground(Color.WHITE);
        expTransactionTableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        expTransactionTableScrollPane.getViewport().setBackground(Color.WHITE);
        expTransactionTableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        expTransactionTableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        expTransactionTableScrollPane.getVerticalScrollBar().setUI(new MacOSScrollPane.MacOSScrollBarUI(COLOR_PANEL_GRAY));

        // Add the scroll pane to the rounded panel
        tableRoundedExpTransactionPanel.add(expTransactionTableScrollPane, BorderLayout.CENTER);

        return tableRoundedExpTransactionPanel;
    }


    protected void toEditExpTrnHistList(String query) {
        if (query == null || query.isEmpty()) {
            return; // Early exit for invalid input
        }


        String lowerCaseQuery = query.toLowerCase();

        // Filter the transaction list using streams
        List<ExpenseTransactionLists> filteredExpTrnList = expTransactionArrayListsToUpdate.stream()
                .filter(expTrn -> expTrn.getExpUsedID().toLowerCase().contains(lowerCaseQuery))
                .toList();

        if (filteredExpTrnList.isEmpty()) {
            return; // No matches found
        }

        ExpenseTransactionLists selectedExpTrn = filteredExpTrnList.get(0); // Get the first matching transaction

        // Show the edit page with gathered data
        new ExpTrnHistEditPage().showExpHistPanel(selectedExpTrn);
    }


    protected void toViewExpTrnHistList(String query) {
        if (query == null || query.isEmpty()) {
            return; // Early exit for invalid input
        }


        String lowerCaseQuery = query.toLowerCase();

        // Filter the transaction list using streams
        List<ExpenseTransactionLists> filteredExpTrnList = expTransactionArrayListsToUpdate.stream()
                .filter(expTrn -> expTrn.getExpUsedID().toLowerCase().contains(lowerCaseQuery))
                .toList();

        if (filteredExpTrnList.isEmpty()) {
            return; // No matches found
        }

        ExpenseTransactionLists selectedExpTrn = filteredExpTrnList.get(0); // Get the first matching transaction

        // Show the edit page with gathered data
        new ExpTrnHistViewPage().showExpHistPanel(selectedExpTrn);
    }


    private IconTextButton createIconButtonForCounter(String buttonName, String iconPath, int iconBaseLineSize,
                                                      Color buttonBaseColor, Color foreFontColor, int width, int height
    ) {
        ImageIcon icon = createResizedIcon(iconPath, iconBaseLineSize, null);
        IconTextButton button = new IconTextButton(buttonName, icon, 14, buttonBaseColor, 0);
        button.setBackground(buttonBaseColor);
        button.setForeground(foreFontColor);
        button.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 11));
        button.setPreferredSize(new Dimension(width, height)); // Adjusted size for visibility

        // Set icon on the left side of the button text
        button.setIcon(icon);
        // Set the text and icon alignment
        button.setHorizontalTextPosition(SwingConstants.RIGHT); // Icon on the left
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);


        return button;
    }


    public void exportExpenseTransactionListsToExcel(List<ExpenseTransactionLists> ExpenseTransactionLists, String filePath) {
        WritableWorkbook workbook = null;
        try {
            // Create a writable workbook at the given path
            workbook = Workbook.createWorkbook(new File(filePath));

            // Create a new sheet in the workbook
            WritableSheet sheet = workbook.createSheet("Expense Transactions", 0);

            // Write column headers
            String[] columnNames = {"#", "TrnDate", "Code", "Category", "Title", "Exp Amount", "Refund", "RefundDate", "AssignedTo", "TrnUser", "LastUpdatedDate"};
            for (int col = 0; col < columnNames.length; col++) {
                Label label = new Label(col, 0, columnNames[col]);
                sheet.addCell(label);
            }

            int index = 1;
            // Write the data rows
            for (int row = 0; row < ExpenseTransactionLists.size(); row++) {
                ExpenseTransactionLists oneExpense = ExpenseTransactionLists.get(row);
                sheet.addCell(new Label(0, row + 1, String.valueOf(index++)));
                sheet.addCell(new Label(1, row + 1, String.valueOf(oneExpense.getTrnDate())));
                sheet.addCell(new Label(2, row + 1, oneExpense.getExpCode()));
                sheet.addCell(new Label(3, row + 1, oneExpense.getExpName()));
                sheet.addCell(new Label(4, row + 1, oneExpense.getExpRemark()));
                sheet.addCell(new Label(5, row + 1, String.valueOf(oneExpense.getExpAmount())));
                sheet.addCell(new Label(6, row + 1, String.valueOf(oneExpense.getRefundAmount())));
                sheet.addCell(new Label(7, row + 1, String.valueOf(oneExpense.getRefundDate())));
                sheet.addCell(new Label(8, row + 1, oneExpense.getAssignedToEmployee()));
                sheet.addCell(new Label(9, row + 1, oneExpense.getTrnUser()));
                sheet.addCell(new Label(10, row + 1, String.valueOf(oneExpense.getLastUpdatedDate())));
            }

            // Write the workbook to the file system
            workbook.write();
            System.out.println("Workbook written successfully!");

            JOptionPane.showMessageDialog(null, "Data successfully exported to Excel!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException | WriteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error exporting data to Excel: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Close the workbook
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException | WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void refreshExpenseTransactionTableData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                FetchExpenseTransactionLists fetchExpenseTransactionLists = new FetchExpenseTransactionLists();
                expTransactionArrayListsToUpdate = fetchExpenseTransactionLists.getExpenseTransactionLists();
                return null;
            }

            @Override
            protected void done() {
                modelForExpTransaction = createCustomModel(getArrayListsThisMonth(expTransactionArrayListsToUpdate));
                expTransactionTable.setModel(modelForExpTransaction);
                setupTableRenderersAndEditors();
                expTransactionTable.revalidate();
                expTransactionTable.repaint();
            }
        };
        worker.execute();
    }


}
