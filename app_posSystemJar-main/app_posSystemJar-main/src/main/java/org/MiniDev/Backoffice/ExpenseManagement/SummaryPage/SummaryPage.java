package org.MiniDev.Backoffice.ExpenseManagement.SummaryPage;

import SqlLoadersAndUpdater.FetchExpenseReportLists;
import UI.*;
import UI.CustomCellEditorTable.*;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.OOP.ExpenseReportLists;

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
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
import java.util.Map;
import javax.swing.JOptionPane;

import static UI.IconCreator.createResizedIcon;
import static UI.UserFinalSettingsVar.*;

public class SummaryPage extends Component {

    protected static DefaultTableModel modelForSalExpense;
    protected static JTable expenseTable;
    protected JScrollPane expenseTableScrollPane;
    protected JPanel expenseInnerPanel;
    protected RoundedPanel tableRoundedExpensePanel;
    protected JTableHeader expenseTableHeader;
    protected static List<ExpenseReportLists> expenseArrayListsToUpdate;

    public SummaryPage() {
    }

    protected IconTextButton exportDownload;

    public JPanel createExpenseSummaryPage() {
        expenseInnerPanel = new JPanel(new BorderLayout());
        expenseInnerPanel.setOpaque(false);

        JPanel searchHoldingPanel = new JPanel(new BorderLayout());
        JPanel tableHoldingPanel = new JPanel(new BorderLayout());

        searchHoldingPanel.setBackground(COLOR_WHITE);
        searchHoldingPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));
        tableHoldingPanel.setOpaque(false);

        JPanel rightContainersPanel = new JPanel();
        rightContainersPanel.setOpaque(false);
        rightContainersPanel.setLayout(new BoxLayout(rightContainersPanel, BoxLayout.X_AXIS));
        rightContainersPanel.setPreferredSize(new Dimension(700, 40));

        exportDownload = createIconButtonForExpense("Export", "/ExcelIcon.svg", 14, Color.decode("#65a30d"), COLOR_WHITE, 90, 35);

        exportDownload.addActionListener(e -> {

            String filePath = "ExpenseLists.xls";  // Define the file path for the exported file
            if (filteredList == null) {
                exportExpenseReportListsToExcel(filteredThisMonthList, filePath);  // Export data to Excel
            } else {
                exportExpenseReportListsToExcel(filteredList, filePath);  // Export data to Excel
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
        rightContainersPanel.add(getExpenseTextField());

        searchHoldingPanel.add(rightContainersPanel, BorderLayout.EAST);


        tableHoldingPanel.add(createTableWithoutActions());

        expenseInnerPanel.add(searchHoldingPanel, BorderLayout.NORTH);
        expenseInnerPanel.add(tableHoldingPanel, BorderLayout.CENTER);

        return expenseInnerPanel;
    }

    protected static DatePicker datePickerForExpSummary = new DatePicker();
    protected static JFormattedTextField expSummaryTextField = new JFormattedTextField();

    private Component createDatePickerToFilter() {
        return getDateChooserField(datePickerForExpSummary, expSummaryTextField, false);
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

    protected static List<ExpenseReportLists> filteredList;
    protected static List<ExpenseReportLists> filteredThisMonthList;

    private void filterExpenseByDateRange(LocalDate startDate, LocalDate endDate) {
        filteredList = new ArrayList<>();

        for (ExpenseReportLists expense : expenseArrayListsToUpdate) {
            // Assuming getAttendanceDate() returns a java.sql.Date
            Date sqlDate = expense.getExpDate();
            LocalDate payDate = sqlDate.toLocalDate(); // Convert to LocalDate

            // Check if the attendance date is within the specified range
            if ((payDate.isEqual(startDate) || payDate.isAfter(startDate)) && (payDate.isEqual(endDate) || payDate.isBefore(endDate))) {
                filteredList.add(expense);
            }
        }

        // Update the table model with the filtered list
        modelForSalExpense = createPivotModel(filteredList);
        expenseTable.setModel(modelForSalExpense);
        setupTableRenderersAndEditors();
        expenseTable.revalidate();
        expenseTable.repaint();
    }


    private List<ExpenseReportLists> getArrayListsThisMonth(List<ExpenseReportLists> allLists) {
        filteredThisMonthList = new ArrayList<>();

        for (ExpenseReportLists expense : allLists) {
            // Assuming getAttendanceDate() returns a java.sql.Date
            Date sqlDate = expense.getExpDate();
            LocalDate payDate = sqlDate.toLocalDate(); // Convert to LocalDate

            // Check if the attendance date is within the specified range
            if ((payDate.isEqual(LocalDate.now().withDayOfMonth(1)) || payDate.isAfter(LocalDate.now().withDayOfMonth(1))) &&
                    (payDate.isEqual(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())) || payDate.isBefore(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())))) {
                filteredThisMonthList.add(expense);
            }
        }

        return filteredThisMonthList;
    }


    private void setupTableRenderersAndEditors() {
        // Center align for the first column (row number)
        expenseTable.getColumnModel().getColumn(0).setCellRenderer(createCenteredRenderer());
        expenseTable.getColumnModel().getColumn(1).setCellRenderer(createCenteredRenderer());

        // Right align for columns 2, 3, and 4
        expenseTable.getColumnModel().getColumn(3).setCellRenderer(createRightAlignedRenderer());
        expenseTable.getColumnModel().getColumn(4).setCellRenderer(createRightAlignedRenderer());
        expenseTable.getColumnModel().getColumn(5).setCellRenderer(createRightAlignedRenderer());

        // Set preferred widths
        expenseTable.getColumnModel().getColumn(0).setMaxWidth(50);
        expenseTable.getColumnModel().getColumn(1).setPreferredWidth(8);

        expenseTable.getColumnModel().getColumn(3).setPreferredWidth(20);
        expenseTable.getColumnModel().getColumn(4).setPreferredWidth(20);
        expenseTable.getColumnModel().getColumn(5).setPreferredWidth(20);
    }


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


    protected SearchTopPaneCreator getExpenseTextField() {
        SearchTopPaneCreator searchTopPaneCreator = new SearchTopPaneCreator("Search expense .....", "/SearchIcon.svg");
        searchTopPaneCreator.setPreferredSize(new Dimension(220, 40));

        JTextField searchExpenseField = searchTopPaneCreator.getSearchTextField();
        Timer debounceTimer = new Timer(300, null); // 300ms debounce timer
        debounceTimer.setRepeats(false); // Run only once per pause

        searchExpenseField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                triggerSearchWithDebounce();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                triggerSearchWithDebounce();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                triggerSearchWithDebounce();
            }

            private void triggerSearchWithDebounce() {
                // Restart timer on each input
                debounceTimer.restart();
                debounceTimer.addActionListener(evt -> {
                    String query = searchExpenseField.getText().trim();
                    new FilterExpenseWorker(query).execute(); // Run filtering in background
                });
            }
        });

        return searchTopPaneCreator;
    }

    private class FilterExpenseWorker extends SwingWorker<List<ExpenseReportLists>, Void> {
        private final String query;

        public FilterExpenseWorker(String query) {
            this.query = query.toLowerCase();
        }

        @Override
        protected List<ExpenseReportLists> doInBackground() {
            List<ExpenseReportLists> sourceList = (filteredList == null)
                    ? (datePickerForExpSummary.getSelectedDateRange()==null ? filteredThisMonthList : expenseArrayListsToUpdate)
                    : filteredList;

            List<ExpenseReportLists> filteredExpenseList = new ArrayList<>();
            for (ExpenseReportLists expense : sourceList) {
                String expenseName = expense.getExpName().toLowerCase();
                String expenseCode = String.valueOf(expense.getExpCode());

                if (expenseName.contains(query) || expenseCode.contains(query)) {
                    filteredExpenseList.add(expense);
                }
            }
            return filteredExpenseList;
        }

        @Override
        protected void done() {
            try {
                List<ExpenseReportLists> filteredExpenseList = get();
                updateExpenseTable(filteredExpenseList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void updateExpenseTable(List<ExpenseReportLists> filteredExpenseList) {
        // Only update the table if the content has changed
        if (!modelForSalExpense.getDataVector().equals(createPivotModel(filteredExpenseList).getDataVector())) {
            modelForSalExpense = createPivotModel(filteredExpenseList);
            expenseTable.setModel(modelForSalExpense);
            setupTableRenderersAndEditors();
            expenseTable.revalidate();
            expenseTable.repaint();
        }
    }

    private DefaultTableModel createPivotModel(List<ExpenseReportLists> expenseArrayListsToUpdate) {
        // Map to store aggregated data
        Map<String, Map<String, double[]>> pivotData = new HashMap<>();

        for (ExpenseReportLists oneExpense : expenseArrayListsToUpdate) {
            String category = oneExpense.getExpName();
            String code = String.valueOf(oneExpense.getExpCode());

            // Aggregate values
            pivotData.putIfAbsent(category, new HashMap<>());
            pivotData.get(category).putIfAbsent(code, new double[3]); // {Expense Amount, Refund Amount, Executed Amount}

            pivotData.get(category).get(code)[0] += oneExpense.getExpAmount();
            pivotData.get(category).get(code)[1] += oneExpense.getRefundAmount();
            pivotData.get(category).get(code)[2] += oneExpense.getExecutedAmount();
        }

        // Create the table model
        modelForSalExpense = new DefaultTableModel(new Object[][]{},
                new String[]{"#", "Code", "Category", "Expense Amount", "Refund Amount", "Executed Amount"}) {
            final boolean[] canEdit = new boolean[]{false, false, false, false, false, false};

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };

        // Populate the model with aggregated data and row numbers
        DecimalFormat df = new DecimalFormat("#,###");
        int index = 1; // Row number starts at 1
        for (Map.Entry<String, Map<String, double[]>> categoryEntry : pivotData.entrySet()) {
            String category = categoryEntry.getKey();
            Map<String, double[]> codeMap = categoryEntry.getValue();

            for (Map.Entry<String, double[]> codeEntry : codeMap.entrySet()) {
                String code = codeEntry.getKey();
                double[] amounts = codeEntry.getValue();

                modelForSalExpense.addRow(new Object[]{
                        index++, // Row number
                        code,
                        category,
                        df.format(amounts[0]), // Total Expense Amount
                        df.format(amounts[1]), // Total Refund Amount
                        df.format(amounts[2]), // Total Executed Amount
                });
            }
        }

        return modelForSalExpense;
    }


    private RoundedPanel createTableWithoutActions() {
        tableRoundedExpensePanel = new RoundedPanel(10);
        tableRoundedExpensePanel.setLayout(new BorderLayout());
        tableRoundedExpensePanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        tableRoundedExpensePanel.setBackground(COLOR_PANEL_GRAY);

        FetchExpenseReportLists fetchExpenseReportLists = new FetchExpenseReportLists();
        expenseArrayListsToUpdate = fetchExpenseReportLists.getExpenseReportLists();

        modelForSalExpense = createPivotModel(getArrayListsThisMonth(expenseArrayListsToUpdate));
        expenseTable = new JTable(modelForSalExpense) {
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

        expenseTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder());
                return this;
            }
        });

        expenseTable.setAutoCreateRowSorter(true);

        expenseTableHeader = expenseTable.getTableHeader();
        expenseTableHeader.setBackground(COLOR_PANEL_GRAY);
        expenseTableHeader.setReorderingAllowed(true);
        expenseTableHeader.setPreferredSize(new Dimension(100, 40));
        expenseTable.setTableHeader(expenseTableHeader);

        expenseTable.setOpaque(true);
        expenseTable.setBackground(COLOR_PANEL_GRAY);
        expenseTable.setRowHeight(55);
        expenseTable.setSelectionBackground(COLOR_SELECT_BLUE);
        expenseTable.setShowHorizontalLines(false);
        expenseTable.setShowVerticalLines(false);

        setupTableRenderersAndEditors();

        expenseTableScrollPane = new JScrollPane(expenseTable);
        expenseTableScrollPane.setBackground(Color.WHITE);
        expenseTableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        expenseTableScrollPane.getViewport().setBackground(Color.WHITE);
        expenseTableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        expenseTableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        expenseTableScrollPane.getVerticalScrollBar().setUI(new MacOSScrollPane.MacOSScrollBarUI(COLOR_PANEL_GRAY));

        tableRoundedExpensePanel.add(expenseTableScrollPane, BorderLayout.CENTER);

        return tableRoundedExpensePanel;
    }


    private IconTextButton createIconButtonForExpense(String buttonName, String iconPath, int iconBaseLineSize, Color buttonBaseColor, Color foreFontColor, int width, int height) {
        ImageIcon icon = createResizedIcon(iconPath, iconBaseLineSize, null);
        IconTextButton button = new IconTextButton(buttonName, icon, 14, buttonBaseColor, 0);
        button.setBackground(buttonBaseColor);
        button.setForeground(foreFontColor);
        button.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 11));
        button.setPreferredSize(new Dimension(width, height)); // Adjusted size for visibility

        button.setIcon(icon);

        button.setHorizontalTextPosition(SwingConstants.RIGHT); // Icon on the left
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);


        return button;
    }


    public void exportExpenseReportListsToExcel(List<ExpenseReportLists> expenseReportLists, String filePath) {
        WritableWorkbook workbook = null;
        try {
            workbook = Workbook.createWorkbook(new File(filePath));
            WritableSheet sheet = workbook.createSheet("Expense Data", 0);

            String[] columnNames = {"#", "Code", "Category", "Expense Amount", "Refund Amount", "Executed Amount"};
            for (int col = 0; col < columnNames.length; col++) {
                Label label = new Label(col, 0, columnNames[col]);
                sheet.addCell(label);
            }

            // Map to store aggregated data grouped by category and code
            Map<String, Map<String, double[]>> pivotData = new HashMap<>();

            for (ExpenseReportLists oneExpense : expenseReportLists) {
                String category = oneExpense.getExpName();
                String code = String.valueOf(oneExpense.getExpCode());

                pivotData.putIfAbsent(category, new HashMap<>());
                pivotData.get(category).putIfAbsent(code, new double[3]); // {Expense Amount, Refund Amount, Executed Amount}

                pivotData.get(category).get(code)[0] += oneExpense.getExpAmount();
                pivotData.get(category).get(code)[1] += oneExpense.getRefundAmount();
                pivotData.get(category).get(code)[2] += oneExpense.getExecutedAmount();
            }

            int indexRow = 1;
            for (Map.Entry<String, Map<String, double[]>> categoryEntry : pivotData.entrySet()) {
                String category = categoryEntry.getKey();
                Map<String, double[]> codeMap = categoryEntry.getValue();

                for (Map.Entry<String, double[]> codeEntry : codeMap.entrySet()) {
                    String code = codeEntry.getKey();
                    double[] amounts = codeEntry.getValue();

                    sheet.addCell(new Label(0, indexRow, String.valueOf(indexRow))); // Row number
                    sheet.addCell(new Label(1, indexRow, code)); // Code
                    sheet.addCell(new Label(2, indexRow, category)); // Category
                    sheet.addCell(new Label(3, indexRow, String.valueOf(amounts[0]))); // Total Expense Amount
                    sheet.addCell(new Label(4, indexRow, String.valueOf(amounts[1]))); // Total Refund Amount
                    sheet.addCell(new Label(5, indexRow, String.valueOf(amounts[2]))); // Total Executed Amount

                    indexRow++;
                }
            }

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


    public void refreshSummaryExpenseTable() {
        FetchExpenseReportLists fetchExpenseReportLists = new FetchExpenseReportLists();
        expenseArrayListsToUpdate = fetchExpenseReportLists.getExpenseReportLists();

        modelForSalExpense = createPivotModel(getArrayListsThisMonth(expenseArrayListsToUpdate));
        expenseTable.setModel(modelForSalExpense);
        setupTableRenderersAndEditors();
        expenseTable.revalidate();
        expenseTable.repaint();
    }


}
