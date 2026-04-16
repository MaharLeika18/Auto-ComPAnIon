//package org.MiniDev.Backoffice.EmployeeManagement.AttendanceHistory;
//
//import SqlLoadersAndUpdater.FetchAttendanceHistoryLists;
//import UI.*;
//import UI.CustomCellEditorTable.*;
//import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
//import org.MiniDev.OOP.AttendanceHistoryLists;
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
//import java.sql.Date;
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
//public class AtdHistoryMain extends Component {
//
//    protected DefaultTableModel modelForAtdTable;
//    protected JTable atdTable;
//    protected JScrollPane atdTableScrollPane;
//    protected JPanel atdInnerPanel;
//    protected RoundedPanel tableRoundedAtdPanel;
//    protected JTableHeader atdTableHeader;
//    protected List<AttendanceHistoryLists> atdArrayListsToUpdate;
//
//    public AtdHistoryMain() {
//    }
//
//    protected IconTextButton exportDownload;
//
//    public JPanel createAtdHistoryMainPage() {
//        atdInnerPanel = new JPanel(new BorderLayout());
//        atdInnerPanel.setOpaque(false);
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
//                String filePath = "AttendanceLists.xls";  // Define the file path for the exported file
//                exportAttendanceHistoryListsToExcel(filteredList, filePath);  // Export data to Excel
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
//        tableHoldingPanel.add(createTableWithActions());
//
//        atdInnerPanel.add(searchHoldingPanel, BorderLayout.NORTH);
//        atdInnerPanel.add(tableHoldingPanel, BorderLayout.CENTER);
//
//        return atdInnerPanel;
//    }
//
//    private final RoundedComboBox<String> comboBoxTodayThisMonth = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);
//    private final String[] todayThisMonthPreviousMonth = new String[]{"Today", "Yesterday", "Last 7 Days", "This Month", "Last Month", "This Year", "Last Year"};
//
//    private Component createComboBoxToFilter() {
//
//        comboBoxTodayThisMonth.setMaximumSize(new Dimension(150, 35));
//        // Add items to the combo box
//        for (String item : todayThisMonthPreviousMonth) {
//            comboBoxTodayThisMonth.addItem(item);
//        }
//
//        // Check if items were added before setting the selected index
//        if (comboBoxTodayThisMonth.getItemCount() > 0) {
//            comboBoxTodayThisMonth.setSelectedIndex(0);
//        }
//
//        // Add action listener to handle selection changes
//        comboBoxTodayThisMonth.addActionListener(e -> {
//            String selectedOption = (String) comboBoxTodayThisMonth.getSelectedItem();
//            filterAttendanceByTimeFrame(selectedOption);
//        });
//        return comboBoxTodayThisMonth; // Make sure to return the combo box
//    }
//
//
//    private void filterAttendanceByTimeFrame(String timeFrame) {
//        LocalDate startDate;
//        LocalDate endDate = LocalDate.now(); // Default end date is today
//
//        switch (timeFrame) {
//            case "Today":
//                startDate = LocalDate.now();
//                break;
//            case "Yesterday":
//                startDate = LocalDate.now().minusDays(1);
//                endDate = startDate; // End date is yesterday
//                break;
//            case "Last 7 Days":
//                startDate = LocalDate.now().minusDays(7);
//                break;
//            case "This Month":
//                startDate = LocalDate.now().withDayOfMonth(1);
//                break;
//            case "Last Month":
//                startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1);
//                endDate = LocalDate.now().minusMonths(1).withDayOfMonth(LocalDate.now().minusMonths(1).lengthOfMonth());
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
//        filterAttendanceByDateRange(startDate, endDate);
//    }
//
//
//    List<AttendanceHistoryLists> filteredList;
//
//    private void filterAttendanceByDateRange(LocalDate startDate, LocalDate endDate) {
//        filteredList = new ArrayList<>();
//
//        for (AttendanceHistoryLists attendance : atdArrayListsToUpdate) {
//            // Assuming getAttendanceDate() returns a java.sql.Date
//            java.sql.Date sqlDate = (Date) attendance.getAttendanceDate();
//            LocalDate attendanceDate = sqlDate.toLocalDate(); // Convert to LocalDate
//
//            // Check if the attendance date is within the specified range
//            if ((attendanceDate.isEqual(startDate) || attendanceDate.isAfter(startDate)) && (attendanceDate.isEqual(endDate) || attendanceDate.isBefore(endDate))) {
//                filteredList.add(attendance);
//            }
//        }
//
//        // Update the table model with the filtered list
//        modelForAtdTable = createCustomModel(filteredList);
//        atdTable.setModel(modelForAtdTable);
//        setupTableRenderersAndEditors();
//        atdTable.revalidate();
//        atdTable.repaint();
//    }
//
//
//    private void setupTableRenderersAndEditors() {
//        atdTable.getColumnModel().getColumn(8).setCellRenderer(new TableActionCellRender());
//        atdTable.getColumnModel().getColumn(8).setCellEditor(new TableActionCellEditor(event));
//
//        atdTable.getColumnModel().getColumn(0).setCellRenderer(createCenteredRenderer());
//        atdTable.getColumnModel().getColumn(1).setCellRenderer(createCenteredRenderer());
//        atdTable.getColumnModel().getColumn(4).setCellRenderer(createCenteredRenderer());
//        atdTable.getColumnModel().getColumn(5).setCellRenderer(createCenteredRenderer());
//        atdTable.getColumnModel().getColumn(6).setCellRenderer(createCenteredRenderer());
//        atdTable.getColumnModel().getColumn(7).setCellRenderer(createCenteredRenderer());
//
//        atdTable.getColumnModel().getColumn(0).setPreferredWidth(10);
//        atdTable.getColumnModel().getColumn(1).setPreferredWidth(10);
//        atdTable.getColumnModel().getColumn(2).setPreferredWidth(30);
//        atdTable.getColumnModel().getColumn(3).setPreferredWidth(15);
//        atdTable.getColumnModel().getColumn(4).setPreferredWidth(15);
//        atdTable.getColumnModel().getColumn(5).setPreferredWidth(15);
//        atdTable.getColumnModel().getColumn(6).setPreferredWidth(10);
//        atdTable.getColumnModel().getColumn(7).setPreferredWidth(15);
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
//    protected void filterAtd(String query) {
//        String lowerCaseQuery = query.toLowerCase();
//        List<AttendanceHistoryLists> filteredAtdList = new ArrayList<>();
//
//        for (AttendanceHistoryLists oneAtd : filteredList) {
//            String employeeName = oneAtd.getEmployeeName().toLowerCase();
//            String employeeID = String.valueOf(oneAtd.getEmployeeID());
//
//            if (employeeName.contains(lowerCaseQuery) || employeeID.contains(lowerCaseQuery)) {
//                filteredAtdList.add(oneAtd);
//            }
//        }
//
//        modelForAtdTable = createCustomModel(filteredAtdList);
//        atdTable.setModel(modelForAtdTable);
//        setupTableRenderersAndEditors();
//        atdTable.revalidate();
//        atdTable.repaint();
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
//                filterAtd(searchEmployeeField.getText());
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                filterAtd(searchEmployeeField.getText());
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//                filterAtd(searchEmployeeField.getText());
//            }
//        });
//
//        return searchTopPaneCreator;
//    }
//
//    private DefaultTableModel createCustomModel(List<AttendanceHistoryLists> atdArrayListsToUpdate) {
//        modelForAtdTable = new DefaultTableModel(new Object[][]{}, new String[]{"Active ?", "ID", "Name", "Date", "CheckIn", "CheckOut", "Hours", "Status", "Action"}) {
//            final boolean[] canEdit = new boolean[]{false, false, false, false, false, false, false, false, true};
//
//            @Override
//            public boolean isCellEditable(int rowIndex, int columnIndex) {
//                return canEdit[columnIndex];
//            }
//        };
//        for (AttendanceHistoryLists oneAttendance : atdArrayListsToUpdate) {
//
//            modelForAtdTable.addRow(new Object[]{oneAttendance.getActiveStatus(), oneAttendance.getEmployeeID(), oneAttendance.getEmployeeName(), oneAttendance.getAttendanceDate(), oneAttendance.getFormattedStartTime(), oneAttendance.getFormattedEndTime(), oneAttendance.getWorkingHours(), oneAttendance.getAbsenceStatus(), null});
//        }
//
//        return modelForAtdTable;
//    }
//
//    TableActionEvent event;
//
//    private RoundedPanel createTableWithActions() {
//        tableRoundedAtdPanel = new RoundedPanel(10);
//        tableRoundedAtdPanel.setLayout(new BorderLayout());
//        tableRoundedAtdPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
//        tableRoundedAtdPanel.setBackground(COLOR_PANEL_GRAY);
//
//        FetchAttendanceHistoryLists fetchAttendanceHistoryLists = new FetchAttendanceHistoryLists();
//        atdArrayListsToUpdate = fetchAttendanceHistoryLists.getAttendanceArrayLists();
//
//        modelForAtdTable = createCustomModel(atdArrayListsToUpdate);
//        atdTable = new JTable(modelForAtdTable) {
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
//        atdTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
//            @Override
//            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//                setBorder(BorderFactory.createEmptyBorder());
//                return this;
//            }
//        });
//
//        atdTable.setAutoCreateRowSorter(true);
//
//        atdTableHeader = atdTable.getTableHeader();
//        atdTableHeader.setBackground(COLOR_PANEL_GRAY);
//        atdTableHeader.setReorderingAllowed(true);
//        atdTableHeader.setPreferredSize(new Dimension(100, 40));
//        atdTable.setTableHeader(atdTableHeader);
//
//        atdTable.setOpaque(true);
//        atdTable.setBackground(COLOR_PANEL_GRAY);
//        atdTable.setRowHeight(55);
//        atdTable.setSelectionBackground(COLOR_SELECT_BLUE);
//        atdTable.setShowHorizontalLines(false);
//        atdTable.setShowVerticalLines(false);
//
//        event = new TableActionEvent() {
//            @Override
//            public void onEdit(int row) {
//
//            }
//
//            @Override
//            public void onDelete(int row) {
//                if (atdTable.isEditing()) {
//                    atdTable.getCellEditor().stopCellEditing();
//                }
//                modelForAtdTable.removeRow(row);
//            }
//
//            @Override
//            public void onView(int row) {
//
//            }
//        };
//        setupTableRenderersAndEditors();
//
//        atdTableScrollPane = new JScrollPane(atdTable);
//        atdTableScrollPane.setBackground(Color.WHITE);
//        atdTableScrollPane.setBorder(BorderFactory.createEmptyBorder());
//        atdTableScrollPane.getViewport().setBackground(Color.WHITE);
//        atdTableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//        atdTableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        atdTableScrollPane.getVerticalScrollBar().setUI(new MacOSScrollPane.MacOSScrollBarUI(COLOR_PANEL_GRAY));
//
//        tableRoundedAtdPanel.add(atdTableScrollPane, BorderLayout.CENTER);
//
//        return tableRoundedAtdPanel;
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
//    public void exportAttendanceHistoryListsToExcel(List<AttendanceHistoryLists> AttendanceHistoryLists, String filePath) {
//        WritableWorkbook workbook = null;
//        try {
//            workbook = Workbook.createWorkbook(new File(filePath));
//
//            WritableSheet sheet = workbook.createSheet("Attendance Data", 0);
//
//            String[] columnNames = {"Employee ID", "Employee Name", "Attendance Date", "CheckIn Time", "CheckOut Time", "Working Hours", "Absence Status"};
//            for (int col = 0; col < columnNames.length; col++) {
//                Label label = new Label(col, 0, columnNames[col]);
//                sheet.addCell(label);
//            }
//
//            for (int row = 0; row < AttendanceHistoryLists.size(); row++) {
//                AttendanceHistoryLists oneAttendance = AttendanceHistoryLists.get(row);
//                sheet.addCell(new Label(0, row + 1, String.valueOf(oneAttendance.getEmployeeID())));
//                sheet.addCell(new Label(1, row + 1, String.valueOf(oneAttendance.getEmployeeName())));
//                sheet.addCell(new Label(2, row + 1, String.valueOf(oneAttendance.getAttendanceDate())));
//                sheet.addCell(new Label(3, row + 1, oneAttendance.getFormattedStartTime()));
//                sheet.addCell(new Label(4, row + 1, oneAttendance.getFormattedEndTime()));
//                sheet.addCell(new Label(5, row + 1, String.valueOf(oneAttendance.getWorkingHours())));
//                sheet.addCell(new Label(6, row + 1, String.valueOf(oneAttendance.getAbsenceStatus())));
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
