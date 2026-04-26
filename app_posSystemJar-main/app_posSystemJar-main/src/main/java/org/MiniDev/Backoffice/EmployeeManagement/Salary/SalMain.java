//package org.MiniDev.Backoffice.EmployeeManagement.Salary;
//
//import SqlLoadersAndUpdater.FetchSalaryHistoryLists;
//import UI.*;
//import UI.CustomCellEditorTable.*;
//import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
//import org.MiniDev.OOP.SalaryHistoryLists;
//
//import javax.swing.*;
//import java.awt.Color;
//import java.awt.Font;
//import javax.swing.event.DocumentEvent;
//import javax.swing.event.DocumentListener;
//import javax.swing.table.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.text.DecimalFormat;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import javax.swing.table.DefaultTableModel;
//
//import jxl.Workbook;
//import jxl.write.Label;
//import jxl.write.WritableSheet;
//import jxl.write.WritableWorkbook;
//import jxl.write.WriteException;
//
//import java.io.File;
//import java.io.IOException;
//import javax.swing.JOptionPane;
//
//import static UI.IconCreator.createResizedIcon;
//import static UI.UserFinalSettingsVar.*;
//
//public class SalMain extends Component {
//
//    protected DefaultTableModel modelForSalTable;
//    protected JTable salTable;
//    protected JScrollPane salTableScrollPane;
//    protected JPanel salInnerPanel;
//    protected RoundedPanel tableRoundedSalPanel;
//    protected JTableHeader salTableHeader;
//    protected List<SalaryHistoryLists> salArrayListsToUpdate;
//
//    public SalMain() {
//    }
//
//    protected IconTextButton exportDownload;
//
//    public JPanel createSalMainPage() {
//        salInnerPanel = new JPanel(new BorderLayout());
//        salInnerPanel.setOpaque(false);
//
//        JPanel searchHoldingPanel = new JPanel(new BorderLayout());
//        JPanel tableHoldingPanel = new JPanel(new BorderLayout());
//
//        searchHoldingPanel.setBackground(COLOR_WHITE);
//        searchHoldingPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));
//        tableHoldingPanel.setOpaque(false);
//
//        JPanel rightContainersPanel = new JPanel();
//        rightContainersPanel.setOpaque(false);
//        rightContainersPanel.setLayout(new BoxLayout(rightContainersPanel, BoxLayout.X_AXIS));
//        rightContainersPanel.setPreferredSize(new Dimension(500, 40));
//
//        exportDownload = createIconButtonForAtd("Export", "/ExcelIcon.svg", 14, Color.decode("#65a30d"), COLOR_WHITE, 90, 35);
//
//        exportDownload.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//                String filePath = "SalaryLists.xls";  // Define the file path for the exported file
//                exportSalaryHistoryListsToExcel(filteredList, filePath);  // Export data to Excel
//                // Open the exported Excel file after creation
//                File excelFile = new File(filePath);
//                if (excelFile.exists()) {
//                    try {
//                        // Open the file using the default application associated with Excel files
//                        Desktop.getDesktop().open(excelFile);
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                        JOptionPane.showMessageDialog(null, "Unable to open the exported Excel file.", "Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                } else {
//                    JOptionPane.showMessageDialog(null, "The exported file was not found.", "Error", JOptionPane.ERROR_MESSAGE);
//                }
//
//            }
//        });
//
//
//        rightContainersPanel.add(createComboBoxToFilter());
//        rightContainersPanel.add(Box.createHorizontalStrut(7));
//        rightContainersPanel.add(exportDownload);
//        rightContainersPanel.add(Box.createHorizontalStrut(7));
//        rightContainersPanel.add(getEmployeeTextField());
//
//        searchHoldingPanel.add(rightContainersPanel, BorderLayout.EAST);
//
//
//        tableHoldingPanel.add(createTableWithActions());
//
//        salInnerPanel.add(searchHoldingPanel, BorderLayout.NORTH);
//        salInnerPanel.add(tableHoldingPanel, BorderLayout.CENTER);
//
//        return salInnerPanel;
//    }
//
//    private final RoundedComboBox<String> comboBoxTimeSelection = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);
//    private final String[] timeFilterStrings = new String[]{"This Month", "Last 1 Month", "Last 2 Month", "Last 3 Month", "Last 7 Month", "This Year", "Last Year"};
//
//    private Component createComboBoxToFilter() {
//
//        comboBoxTimeSelection.setMaximumSize(new Dimension(150, 35));
//        // Add items to the combo box
//        for (String item : timeFilterStrings) {
//            comboBoxTimeSelection.addItem(item);
//        }
//
//        // Check if items were added before setting the selected index
//        if (comboBoxTimeSelection.getItemCount() > 0) {
//            comboBoxTimeSelection.setSelectedIndex(0);
//        }
//
//        // Add action listener to handle selection changes
//        comboBoxTimeSelection.addActionListener(e -> {
//            String selectedOption = (String) comboBoxTimeSelection.getSelectedItem();
//            filterSalaryByTimeFrame(selectedOption);
//        });
//
//        return comboBoxTimeSelection; // Make sure to return the combo box
//    }
//
//
//    private void filterSalaryByTimeFrame(String timeFrame) {
//        LocalDate startDate;
//        LocalDate endDate = LocalDate.now(); // Default end date is today
//
//        switch (timeFrame) {
//            case "This Month":
//                startDate = LocalDate.now().withDayOfMonth(1);
//                endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()); // Set end date to the last day of the month
//                break;
//            case "Last 1 Month":
//                startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1);
//                endDate = LocalDate.now().minusMonths(1).withDayOfMonth(LocalDate.now().minusMonths(1).lengthOfMonth());
//                break;
//            case "Last 2 Months":
//                startDate = LocalDate.now().minusMonths(2).withDayOfMonth(1);
//                endDate = LocalDate.now().minusMonths(1).withDayOfMonth(1).minusDays(1); // End date is last month's end
//                break;
//            case "Last 3 Months":
//                startDate = LocalDate.now().minusMonths(3).withDayOfMonth(1);
//                endDate = LocalDate.now().minusMonths(1).withDayOfMonth(1).minusDays(1); // End date is last month's end
//                break;
//            case "Last 7 Months":
//                startDate = LocalDate.now().minusMonths(7).withDayOfMonth(1);
//                endDate = LocalDate.now().minusMonths(1).withDayOfMonth(1).minusDays(1); // End date is last month's end
//                break;
//            case "This Year":
//                startDate = LocalDate.now().withDayOfYear(1);
//                break;
//            case "Last Year":
//                startDate = LocalDate.now().minusYears(1).withDayOfYear(1);
//                endDate = LocalDate.now().minusYears(1).withDayOfYear(LocalDate.now().minusYears(1).lengthOfYear());
//                break;
//            default:
//                return; // If no valid option is selected, do nothing
//        }
//
//        // Filter the attendance records based on the date range
//        filterSalaryByDateRange(startDate, endDate);
//    }
//
//
//    List<SalaryHistoryLists> filteredList;
//
//    private void filterSalaryByDateRange(LocalDate startDate, LocalDate endDate) {
//        filteredList = new ArrayList<>();
//
//        for (SalaryHistoryLists salary : salArrayListsToUpdate) {
//            // Assuming getAttendanceDate() returns a java.sql.Date
//            java.sql.Date sqlDate = salary.getPayDate();
//            LocalDate payDate = sqlDate.toLocalDate(); // Convert to LocalDate
//
//            // Check if the attendance date is within the specified range
//            if ((payDate.isEqual(startDate) || payDate.isAfter(startDate)) && (payDate.isEqual(endDate) || payDate.isBefore(endDate))) {
//                filteredList.add(salary);
//            }
//        }
//
//        // Update the table model with the filtered list
//        modelForSalTable = createCustomModel(filteredList);
//        salTable.setModel(modelForSalTable);
//        setupTableRenderersAndEditors();
//        salTable.revalidate();
//        salTable.repaint();
//    }
//
//
//    private void setupTableRenderersAndEditors() {
//        salTable.getColumnModel().getColumn(9).setCellRenderer(new TableActionCellRender());
//        salTable.getColumnModel().getColumn(9).setCellEditor(new TableActionCellEditor(event));
//
//        salTable.getColumnModel().getColumn(0).setCellRenderer(createCenteredRenderer());
//        salTable.getColumnModel().getColumn(2).setCellRenderer(createCenteredRenderer());
//        salTable.getColumnModel().getColumn(8).setCellRenderer(createCenteredRenderer());
//        salTable.getColumnModel().getColumn(7).setCellRenderer(createCenteredRenderer());
//        salTable.getColumnModel().getColumn(6).setCellRenderer(createCenteredRenderer());
//        salTable.getColumnModel().getColumn(5).setCellRenderer(createCenteredRenderer());
//
//        salTable.getColumnModel().getColumn(0).setPreferredWidth(15);
//        salTable.getColumnModel().getColumn(1).setPreferredWidth(20);
//        salTable.getColumnModel().getColumn(2).setPreferredWidth(5);
//        salTable.getColumnModel().getColumn(3).setPreferredWidth(15);
//        salTable.getColumnModel().getColumn(4).setPreferredWidth(15);
//        salTable.getColumnModel().getColumn(5).setPreferredWidth(15);
//        salTable.getColumnModel().getColumn(6).setPreferredWidth(10);
//        salTable.getColumnModel().getColumn(7).setPreferredWidth(10);
//        salTable.getColumnModel().getColumn(8).setPreferredWidth(15);
//
//    }
//
//
//    private TableCellRenderer createCenteredRenderer() {
//        DefaultTableCellRenderer centeredRenderer = new DefaultTableCellRenderer();
//        centeredRenderer.setHorizontalAlignment(SwingConstants.CENTER);
//        return centeredRenderer;
//    }
//
//
//    protected void filterSal(String query) {
//        String lowerCaseQuery = query.toLowerCase();
//        List<SalaryHistoryLists> filteredSalList = new ArrayList<>();
//
//        for (SalaryHistoryLists oneSal : filteredList) {
//            String employeeName = oneSal.getEmployeeName().toLowerCase();
//            String employeeID = String.valueOf(oneSal.getEmployeeId());
//
//            if (employeeName.contains(lowerCaseQuery) || employeeID.contains(lowerCaseQuery)) {
//                filteredSalList.add(oneSal);
//            }
//        }
//
//        modelForSalTable = createCustomModel(filteredSalList);
//        salTable.setModel(modelForSalTable);
//        setupTableRenderersAndEditors();
//        salTable.revalidate();
//        salTable.repaint();
//    }
//
//
//    protected SearchTopPaneCreator getEmployeeTextField() {
//        SearchTopPaneCreator searchTopPaneCreator = new SearchTopPaneCreator("Search employee .....", "/SearchIcon.svg");
//        searchTopPaneCreator.setPreferredSize(new Dimension(220, 40));
//
//        JTextField searchEmployeeField = searchTopPaneCreator.getSearchTextField();
//
//        searchEmployeeField.getDocument().addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                filterSal(searchEmployeeField.getText());
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                filterSal(searchEmployeeField.getText());
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//                filterSal(searchEmployeeField.getText());
//            }
//        });
//
//        return searchTopPaneCreator;
//    }
//
//    private DefaultTableModel createCustomModel(List<SalaryHistoryLists> salArrayListsToUpdate) {
//        modelForSalTable = new DefaultTableModel(new Object[][]{}, new String[]{"PayDate", "Name", "ID", "NRC", "Dprt", "Fixed", "Bonus/Incentive", "PayCut", "Final", "Action"}) {
//            final boolean[] canEdit = new boolean[]{false, false, false, false, false, false, false, false, false, true};
//
//            @Override
//            public boolean isCellEditable(int rowIndex, int columnIndex) {
//                return canEdit[columnIndex];
//            }
//        };
//        for (SalaryHistoryLists oneSalary : salArrayListsToUpdate) {
//
//            DecimalFormat df = new DecimalFormat("#,###");
//            String fixed = df.format(oneSalary.getFixedSalaryAmount());
//            String bonusIncentive = df.format(oneSalary.getBonusIncentive());
//            String payCut = df.format(oneSalary.getReducedAmount());
//            String finalSalary = df.format(oneSalary.getFinalCalculatedAmount());
//
//            modelForSalTable.addRow(new Object[]{
//                    oneSalary.getPayDate(),
//                    oneSalary.getEmployeeName(),
//                    oneSalary.getEmployeeId(),
//                    oneSalary.getNrc(),
//                    oneSalary.getDepartmentName(),
//                    fixed,
//                    bonusIncentive,
//                    payCut,
//                    finalSalary,
//                    null});
//        }
//
//        return modelForSalTable;
//    }
//
//    TableActionEvent event;
//
//    private RoundedPanel createTableWithActions() {
//        tableRoundedSalPanel = new RoundedPanel(10);
//        tableRoundedSalPanel.setLayout(new BorderLayout());
//        tableRoundedSalPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
//        tableRoundedSalPanel.setBackground(COLOR_PANEL_GRAY);
//
//        FetchSalaryHistoryLists fetchSalaryHistoryLists = new FetchSalaryHistoryLists();
//        salArrayListsToUpdate = fetchSalaryHistoryLists.getSalaryArrayLists();
//
//        modelForSalTable = createCustomModel(salArrayListsToUpdate);
//        salTable = new JTable(modelForSalTable) {
//            @Override
//            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
//                Component component = super.prepareRenderer(renderer, row, column);
//                if (!isRowSelected(row)) {
//                    component.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_PANEL_GRAY);
//                } else {
//                    component.setBackground(COLOR_SELECT_BLUE);
//                }
//                return component;
//            }
//        };
//
//        salTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
//            @Override
//            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//                setBorder(BorderFactory.createEmptyBorder());
//                return this;
//            }
//        });
//
//        salTable.setAutoCreateRowSorter(true);
//
//        salTableHeader = salTable.getTableHeader();
//        salTableHeader.setBackground(COLOR_PANEL_GRAY);
//        salTableHeader.setReorderingAllowed(true);
//        salTableHeader.setPreferredSize(new Dimension(100, 40));
//        salTable.setTableHeader(salTableHeader);
//
//        salTable.setOpaque(true);
//        salTable.setBackground(COLOR_PANEL_GRAY);
//        salTable.setRowHeight(55);
//        salTable.setSelectionBackground(COLOR_SELECT_BLUE);
//        salTable.setShowHorizontalLines(false);
//        salTable.setShowVerticalLines(false);
//
//        event = new TableActionEvent() {
//            @Override
//            public void onEdit(int row) {
//
//            }
//
//            @Override
//            public void onDelete(int row) {
//                if (salTable.isEditing()) {
//                    salTable.getCellEditor().stopCellEditing();
//                }
//                modelForSalTable.removeRow(row);
//            }
//
//            @Override
//            public void onView(int row) {
//
//            }
//        };
//        setupTableRenderersAndEditors();
//
//        salTableScrollPane = new JScrollPane(salTable);
//        salTableScrollPane.setBackground(Color.WHITE);
//        salTableScrollPane.setBorder(BorderFactory.createEmptyBorder());
//        salTableScrollPane.getViewport().setBackground(Color.WHITE);
//        salTableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//        salTableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        salTableScrollPane.getVerticalScrollBar().setUI(new MacOSScrollPane.MacOSScrollBarUI(COLOR_PANEL_GRAY));
//
//        tableRoundedSalPanel.add(salTableScrollPane, BorderLayout.CENTER);
//
//        return tableRoundedSalPanel;
//    }
//
//
//    private IconTextButton createIconButtonForAtd(String buttonName, String iconPath, int iconBaseLineSize, Color buttonBaseColor, Color foreFontColor, int width, int height) {
//        ImageIcon icon = createResizedIcon(iconPath, iconBaseLineSize, null);
//        IconTextButton button = new IconTextButton(buttonName, icon, 14, buttonBaseColor, 0);
//        button.setBackground(buttonBaseColor);
//        button.setForeground(foreFontColor);
//        button.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 11));
//        button.setPreferredSize(new Dimension(width, height)); // Adjusted size for visibility
//
//        button.setIcon(icon);
//
//        button.setHorizontalTextPosition(SwingConstants.RIGHT); // Icon on the left
//        button.setVerticalTextPosition(SwingConstants.CENTER);
//        button.setHorizontalAlignment(SwingConstants.CENTER);
//        button.setVerticalAlignment(SwingConstants.CENTER);
//
//
//        return button;
//    }
//
//
//    public void exportSalaryHistoryListsToExcel(List<SalaryHistoryLists> SalaryHistoryLists, String filePath) {
//        WritableWorkbook workbook = null;
//        try {
//            workbook = Workbook.createWorkbook(new File(filePath));
//
//            WritableSheet sheet = workbook.createSheet("Salary Data", 0);
//
//            String[] columnNames = {"PayDate", "Name", "ID", "Department", "NRC", "FixedAmount", "BonusAmount", "IncentiveAmount", "PayCutAmount", "FinalCalculatedAmount"};
//            for (int col = 0; col < columnNames.length; col++) {
//                Label label = new Label(col, 0, columnNames[col]);
//                sheet.addCell(label);
//            }
//
//            for (int row = 0; row < SalaryHistoryLists.size(); row++) {
//                SalaryHistoryLists oneSalary = SalaryHistoryLists.get(row);
//                sheet.addCell(new Label(0, row + 1, String.valueOf(oneSalary.getPayDate())));
//                sheet.addCell(new Label(1, row + 1, oneSalary.getEmployeeName()));
//                sheet.addCell(new Label(2, row + 1, String.valueOf(oneSalary.getEmployeeId())));
//                sheet.addCell(new Label(3, row + 1, oneSalary.getDepartmentName()));
//                sheet.addCell(new Label(4, row + 1, oneSalary.getNrc()));
//                sheet.addCell(new Label(5, row + 1, String.valueOf(oneSalary.getFixedSalaryAmount())));
//                sheet.addCell(new Label(6, row + 1, String.valueOf(oneSalary.getBonusAmount())));
//                sheet.addCell(new Label(7, row + 1, String.valueOf(oneSalary.getIncentiveAmount())));
//                sheet.addCell(new Label(8, row + 1, String.valueOf(oneSalary.getReducedAmount())));
//                sheet.addCell(new Label(9, row + 1, String.valueOf(oneSalary.getFinalCalculatedAmount())));
//            }
//
//            workbook.write();
//            System.out.println("Workbook written successfully!");
//
//            JOptionPane.showMessageDialog(null, "Data successfully exported to Excel!", "Success", JOptionPane.INFORMATION_MESSAGE);
//
//        } catch (IOException | WriteException ex) {
//            ex.printStackTrace();
//            JOptionPane.showMessageDialog(null, "Error exporting data to Excel: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//        } finally {
//            // Close the workbook
//            if (workbook != null) {
//                try {
//                    workbook.close();
//                } catch (IOException | WriteException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//
//}
