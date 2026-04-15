package org.MiniDev.Backoffice.EmployeeManagement.EmployeeListManagement;

import SqlLoadersAndUpdater.FetchEmployeeHistoryLists;
import UI.*;
import UI.CustomCellEditorTable.TableActionCellEditor;
import UI.CustomCellEditorTable.TableActionCellRender;
import UI.CustomCellEditorTable.TableActionEvent;
import UI.CustomFloatingButton.floating.FloatingButtonUI;
import org.MiniDev.Backoffice.EmployeeManagement.EmployeeListManagement.EmpTableActionPages.EmpEditPage;
import org.MiniDev.Backoffice.EmployeeManagement.EmployeeListManagement.EmpTableActionPages.EmpViewPage;
import org.MiniDev.Backoffice.EmployeeManagement.EmployeeManagementPage;
import org.MiniDev.OOP.EmployeeHistoryLists;

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;

import static UI.IconCreator.createResizedIcon;
import static UI.UserFinalSettingsVar.*;

public class EmpMainPage extends Component {

    protected static DefaultTableModel modelForEmp;
    protected static JTable empTable;
    protected static JScrollPane empTableScrollPane;
    protected static JPanel empInnerPanel;
    protected static RoundedPanel tableRoundedEmpPanel;
    protected static JTableHeader empTableHeader;
    public static List<EmployeeHistoryLists> empArrayListsToUpdate;


    public EmpMainPage() {

    }


    /**
     * Creates the main StockInnerPage JPanel.
     */
    protected IconTextButton exportDownload;

    public JPanel createEmpMainPage() {
        empInnerPanel = new JPanel(new BorderLayout());
        empInnerPanel.setOpaque(false);

        JPanel searchHoldingPanel = new JPanel(new BorderLayout());
        JPanel tableHoldingPanel = new JPanel(new BorderLayout());

        searchHoldingPanel.setBackground(COLOR_WHITE);
        searchHoldingPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));
        tableHoldingPanel.setOpaque(false);

        JPanel rightContainersPanel = new JPanel();
        rightContainersPanel.setOpaque(false);
        rightContainersPanel.setLayout(new BoxLayout(rightContainersPanel, BoxLayout.X_AXIS));
        rightContainersPanel.setPreferredSize(new Dimension(305, 40));

        exportDownload = createIconButtonForEmployee("Export", "/ExcelIcon.svg", 14, Color.decode("#65a30d"), COLOR_WHITE, 90, 35);

        exportDownload.addActionListener(e -> {
            String filePath = "EmployeeLists.xls";  // Define the file path for the exported file
            exportEmployeeListsToExcel(empArrayListsToUpdate, filePath);  // Export data to Excel
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

        rightContainersPanel.add(exportDownload);
        rightContainersPanel.add(Box.createHorizontalStrut(7));
        rightContainersPanel.add(getEmployeeTextField());

        searchHoldingPanel.add(rightContainersPanel, BorderLayout.EAST);


        tableHoldingPanel.add(new JLayer(createTableWithActions(), new FloatingButtonUI("EMP-NEW")));

        // Add panels to the main stockInnerPanel
        empInnerPanel.add(searchHoldingPanel, BorderLayout.NORTH);
        empInnerPanel.add(tableHoldingPanel, BorderLayout.CENTER);

        return empInnerPanel;
    }

    /**
     * Sets up custom renderers and editors for the table columns.
     */
    private static void setupTableRenderersAndEditors() {
        empTable.getColumnModel().getColumn(0).setCellRenderer(createImageAvatarRenderer());
        empTable.getColumnModel().getColumn(8).setCellRenderer(new TableActionCellRender());
        empTable.getColumnModel().getColumn(8).setCellEditor(new TableActionCellEditor(event));

        empTable.getColumnModel().getColumn(5).setCellRenderer(createCenteredRenderer());
        empTable.getColumnModel().getColumn(6).setCellRenderer(createCenteredRenderer());
        empTable.getColumnModel().getColumn(7).setCellRenderer(createCenteredRenderer());

        // Set preferred widths
        empTable.getColumnModel().getColumn(0).setPreferredWidth(50); // Adjust width as needed
        empTable.getColumnModel().getColumn(1).setPreferredWidth(10);
        empTable.getColumnModel().getColumn(2).setPreferredWidth(30);
        empTable.getColumnModel().getColumn(3).setPreferredWidth(15);
        empTable.getColumnModel().getColumn(4).setPreferredWidth(15);
        empTable.getColumnModel().getColumn(5).setPreferredWidth(15);
        empTable.getColumnModel().getColumn(6).setPreferredWidth(5);
        empTable.getColumnModel().getColumn(7).setPreferredWidth(15);
    }

    /**
     * Creates a TableCellRenderer that centers text.
     *
     * @return TableCellRenderer that centers text
     */
    private static TableCellRenderer createCenteredRenderer() {
        DefaultTableCellRenderer centeredRenderer = new DefaultTableCellRenderer();
        centeredRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        return centeredRenderer;
    }


    /**
     * Creates an image avatar renderer for the table.
     *
     * @return TableCellRenderer for image avatar
     */
    private static TableCellRenderer createImageAvatarRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                // Create a panel to wrap the ImageAvatar and apply padding
                JPanel cellPanel = new JPanel(new GridBagLayout()); // Use GridBagLayout to center the image
                cellPanel.setOpaque(true); // Ensure the background color is painted

                // Add padding around the image
                cellPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // 5px padding on all sides

                if (value instanceof ImageIcon) {
                    ImageAvatar imageAvatar = getImageAvatar((ImageIcon) value);

                    // Add the ImageAvatar to the panel
                    cellPanel.add(imageAvatar);
                }

                // Set the background color for alternating rows
                if (isSelected) {
                    cellPanel.setBackground(COLOR_SELECT_BLUE); // Selected row color
                } else {
                    cellPanel.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_PANEL_GRAY); // Alternating row colors
                }

                return cellPanel;
            }
        };
    }

    private static ImageAvatar getImageAvatar(ImageIcon value) {
        ImageAvatar imageAvatar = new ImageAvatar();
        imageAvatar.setOpaque(false);
        imageAvatar.setBorderSize(0);
        imageAvatar.setArcSize(10);
        imageAvatar.setFillRect(true);
        imageAvatar.setAutoResizing(false); // Ensure the image is not resized automatically

        // Set explicit size for the image avatar
        Dimension preferredSize = new Dimension(45, 45);  // Desired size
        imageAvatar.setPreferredSize(preferredSize);
        imageAvatar.setMaximumSize(preferredSize);

        imageAvatar.setImage(value);
        return imageAvatar;
    }


    private SwingWorker<Void, Void> currentWorker;

    protected void filterEmployee(String query) {
        // Cancel any running background tasks
        if (currentWorker != null && !currentWorker.isDone()) {
            currentWorker.cancel(true);
        }

        String lowerCaseQuery = query.toLowerCase();

        currentWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                List<EmployeeHistoryLists> filteredEmpList = new ArrayList<>();

                // Perform filtering in the background
                for (EmployeeHistoryLists oneEmployee : empArrayListsToUpdate) {
                    if (isCancelled()) break; // Stop if cancelled

                    String empNameLower = oneEmployee.getEmployeeName().toLowerCase();
                    String empID = String.valueOf(oneEmployee.getEmployeeId());

                    if (empNameLower.contains(lowerCaseQuery) || empID.contains(lowerCaseQuery)) {
                        filteredEmpList.add(oneEmployee);
                    }
                }

                // Update the model on the EDT
                SwingUtilities.invokeLater(() -> {
                    if (!isCancelled()) {
                        modelForEmp = createCustomModel(filteredEmpList);
                        empTable.setModel(modelForEmp);
                        setupTableRenderersAndEditors();
                        empTable.revalidate();
                        empTable.repaint();
                    }
                });

                return null;
            }
        };

        currentWorker.execute();
    }

    private Timer debounceTimer;

    /**
     * Creates the search text field for the stock search bar.
     */
    protected SearchTopPaneCreator getEmployeeTextField() {
        SearchTopPaneCreator searchTopPaneCreator = new SearchTopPaneCreator("Search the product .....", "/SearchIcon.svg");
        searchTopPaneCreator.setPreferredSize(new Dimension(220, 40));

        JTextField searchEmpField = searchTopPaneCreator.getSearchTextField();

        // Debounce logic
        debounceTimer = new Timer(300, e -> {
            filterEmployee(searchEmpField.getText());
        });
        debounceTimer.setRepeats(false);

        searchEmpField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                debounceTimer.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                debounceTimer.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                debounceTimer.restart();
            }
        });

        return searchTopPaneCreator;
    }

    public static ImageIcon defaultIcon;
    public static ImageIcon PhotoIcon;

    private static DefaultTableModel createCustomModel(List<EmployeeHistoryLists> empArrayListsToUpdate) {
        modelForEmp = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Photo", "ID", "Name", "Tel No", "NRC", "Address", "Age", "HiredDate", "Action"}
        ) {
            final boolean[] canEdit = new boolean[]{false, false, false, false, false, false, false, false, true};

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };

        defaultIcon = new ImageIcon("/ProfileIcon.png");

        for (EmployeeHistoryLists oneEmployee : empArrayListsToUpdate) {

            Icon empPhotoBytes = oneEmployee.getEmpProfileAsIcon();

            modelForEmp.addRow(new Object[]{
                    empPhotoBytes,
                    oneEmployee.getEmployeeId(),
                    oneEmployee.getEmployeeName(),
                    oneEmployee.getEmployeeTelNo(),
                    oneEmployee.getNrc(),
                    oneEmployee.getHomeAddress(),
                    oneEmployee.getAge(),
                    oneEmployee.getHiredDate(),
                    null // This will be for the action buttons (Edit, Delete, View)
            });
        }
        return modelForEmp;
    }

    public static TableActionEvent event;

    private RoundedPanel createTableWithActions() {
        tableRoundedEmpPanel = new RoundedPanel(10);
        tableRoundedEmpPanel.setLayout(new BorderLayout());
        tableRoundedEmpPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        tableRoundedEmpPanel.setBackground(COLOR_PANEL_GRAY);

        FetchEmployeeHistoryLists fetchEmployeeHistoryLists = new FetchEmployeeHistoryLists();
        empArrayListsToUpdate = fetchEmployeeHistoryLists.getEmployeeHistoryLists();

        modelForEmp = createCustomModel(empArrayListsToUpdate);
        empTable = new JTable(modelForEmp) {
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

        empTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder());
                return this;
            }
        });

        empTable.setAutoCreateRowSorter(true);

        empTableHeader = empTable.getTableHeader();
        empTableHeader.setBackground(COLOR_PANEL_GRAY);
        empTableHeader.setReorderingAllowed(true);
        empTableHeader.setPreferredSize(new Dimension(100, 40));
        empTable.setTableHeader(empTableHeader);

        empTable.setOpaque(true);
        empTable.setBackground(COLOR_PANEL_GRAY);
        empTable.setRowHeight(55);
        empTable.setSelectionBackground(COLOR_SELECT_BLUE);
        empTable.setShowHorizontalLines(false);
        empTable.setShowVerticalLines(false);

        event = new TableActionEvent() {
            @Override
            public void onEdit(int row) {
                int cellValue = (int) empTable.getValueAt(row, 1);
                EmployeeManagementPage.employeeMenuCardLayout.show(EmployeeManagementPage.menuEmployeeCardHoldingPanel, "EMP-EDT");
                toEditEmployee(cellValue);
            }

            @Override
            public void onDelete(int row) {
                if (empTable.isEditing()) {
                    empTable.getCellEditor().stopCellEditing();
                }
                modelForEmp.removeRow(row);
            }

            @Override
            public void onView(int row) {
                int cellValue = (int) empTable.getValueAt(row, 1);
                EmployeeManagementPage.employeeMenuCardLayout.show(EmployeeManagementPage.menuEmployeeCardHoldingPanel, "EMP-VIW");
                toViewEmployee(cellValue);
            }
        };


        setupTableRenderersAndEditors();

        // Initialize the scroll pane and add the table to it
        empTableScrollPane = new JScrollPane(empTable);
        empTableScrollPane.setBackground(Color.WHITE);
        empTableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        empTableScrollPane.getViewport().setBackground(Color.WHITE);
        empTableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        empTableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        empTableScrollPane.getVerticalScrollBar().setUI(new MacOSScrollPane.MacOSScrollBarUI(COLOR_PANEL_GRAY));

        // Add the scroll pane to the rounded panel
        tableRoundedEmpPanel.add(empTableScrollPane, BorderLayout.CENTER);

        return tableRoundedEmpPanel;
    }

    protected void toEditEmployee(int empID) {
        List<EmployeeHistoryLists> empLists = new ArrayList<>();
        for (EmployeeHistoryLists emp : empArrayListsToUpdate) {
            if (empID == emp.getEmployeeId()) {
                empLists.add(emp);
            }
        }
        if (empLists.isEmpty()) {
            return;
        }

        EmployeeHistoryLists selectedEmp = empLists.get(0); // Use get(0) instead of getFirst()

        Icon employeePhoto = selectedEmp.getEmpProfileAsIcon();
        String employeeName = selectedEmp.getEmployeeName();
        String rawPassword = selectedEmp.getRawPassword();
        String departmentName = selectedEmp.getDepartmentName();
        String telNo = selectedEmp.getTelNo();
        LocalDate hireDate = selectedEmp.getHireDate().toLocalDate();
        String nrc = selectedEmp.getNrc();
        LocalDate birthDate = selectedEmp.getBirthDate().toLocalDate();
        String homeAddress = selectedEmp.getHomeAddress();
        String fatherName = selectedEmp.getFatherName();
        int currentFixedSalaryAmount = selectedEmp.getCurrentFixedSalaryAmount();
        char genderMW = selectedEmp.getGenderMW();
        String maritalValue = selectedEmp.getMaritalStatus();

        EmpEditPage.setDataInFields(empID, employeePhoto, employeeName, rawPassword, fatherName, telNo,
                homeAddress, nrc, currentFixedSalaryAmount, birthDate,
                hireDate, departmentName, getGenderFullFormat(genderMW), maritalValue);
    }


    protected void toViewEmployee(int empID) {
        List<EmployeeHistoryLists> empLists = new ArrayList<>();
        for (EmployeeHistoryLists emp : empArrayListsToUpdate) {
            if (empID == emp.getEmployeeId()) {
                empLists.add(emp);
            }
        }
        if (empLists.isEmpty()) {
            return;
        }

        EmployeeHistoryLists selectedEmp = empLists.get(0); // Use get(0) instead of getFirst()

        Icon employeePhoto = selectedEmp.getEmpProfileAsIcon();
        String employeeName = selectedEmp.getEmployeeName();
        String rawPassword = selectedEmp.getRawPassword();
        String departmentName = selectedEmp.getDepartmentName();
        String telNo = selectedEmp.getTelNo();
        LocalDate hireDate = selectedEmp.getHireDate().toLocalDate();
        String nrc = selectedEmp.getNrc();
        LocalDate birthDate = selectedEmp.getBirthDate().toLocalDate();
        String homeAddress = selectedEmp.getHomeAddress();
        String fatherName = selectedEmp.getFatherName();
        int currentFixedSalaryAmount = selectedEmp.getCurrentFixedSalaryAmount();
        char genderMW = selectedEmp.getGenderMW();
        String maritalValue = selectedEmp.getMaritalStatus();

        EmpViewPage.setDataInFields(employeePhoto, employeeName, empID, fatherName, telNo,
                homeAddress, nrc, currentFixedSalaryAmount, birthDate,
                hireDate, departmentName, getGenderFullFormat(genderMW), maritalValue);


    }

    private String getGenderFullFormat(char value) {
        if (value == 'M') {
            return "Male";
        } else if (value == 'W') {
            return "Female";
        } else {
            return "Others";
        }
    }


    private IconTextButton createIconButtonForEmployee(String buttonName, String iconPath, int iconBaseLineSize,
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


    public void exportEmployeeListsToExcel(List<EmployeeHistoryLists> empLists, String filePath) {
        WritableWorkbook workbook = null;
        try {
            // Create a writable workbook at the given path
            workbook = Workbook.createWorkbook(new File(filePath));

            // Create a new sheet in the workbook
            WritableSheet sheet = workbook.createSheet("Food Data", 0);

            // Write column headers
            String[] columnNames = {"ID", "Name", "Department", "Telephone",
                    "HiredDate", "RetiredDate", "NRC", "BirthDate", "HomeAddress", "FatherName",
                    "FixedSalaryAmount", "ActiveYN", "Gender"};
            for (int col = 0; col < columnNames.length; col++) {
                Label label = new Label(col, 0, columnNames[col]);
                sheet.addCell(label);
            }

            // Write the data rows
            for (int row = 0; row < empLists.size(); row++) {
                EmployeeHistoryLists oneEmp = empLists.get(row);
                sheet.addCell(new Label(0, row + 1, String.valueOf(oneEmp.getEmployeeId())));
                sheet.addCell(new Label(1, row + 1, oneEmp.getEmployeeName()));
                sheet.addCell(new Label(2, row + 1, oneEmp.getDepartmentName()));
                sheet.addCell(new Label(3, row + 1, oneEmp.getEmployeeTelNo()));
                sheet.addCell(new Label(4, row + 1, String.valueOf(oneEmp.getHiredDate())));
                sheet.addCell(new Label(5, row + 1, String.valueOf(oneEmp.getRetiredDate())));
                sheet.addCell(new Label(6, row + 1, oneEmp.getNrc()));
                sheet.addCell(new Label(7, row + 1, String.valueOf(oneEmp.getBirthDate())));
                sheet.addCell(new Label(8, row + 1, oneEmp.getHomeAddress()));
                sheet.addCell(new Label(9, row + 1, oneEmp.getFatherName()));
                sheet.addCell(new Label(10, row + 1, String.valueOf(oneEmp.getCurrentFixedSalaryAmount())));
                sheet.addCell(new Label(11, row + 1, String.valueOf(oneEmp.getActiveYN())));
                sheet.addCell(new Label(12, row + 1, String.valueOf(oneEmp.getGenderMW())));
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


//    public static void refreshTableAfterEmpUpdater() {
//        empArrayListsToUpdate.clear();
//
//        FetchEmployeeHistoryLists fetchEmployeeHistoryLists = new FetchEmployeeHistoryLists();
//        empArrayListsToUpdate = fetchEmployeeHistoryLists.getEmployeeHistoryLists();
//
//        modelForEmp = createCustomModel(empArrayListsToUpdate);
//        empTable.setModel(modelForEmp);
//        setupTableRenderersAndEditors();
//        empTable.revalidate();
//        empTable.repaint();
//    }


    public static void refreshTableAfterEmpUpdater() {
        new SwingWorker<List<EmployeeHistoryLists>, Void>() {
            @Override
            protected List<EmployeeHistoryLists> doInBackground() {
                FetchEmployeeHistoryLists fetchEmployeeHistoryLists = new FetchEmployeeHistoryLists();
                return fetchEmployeeHistoryLists.getEmployeeHistoryLists(); // Fetch in background
            }

            @Override
            protected void done() {
                try {
                    empArrayListsToUpdate.clear();
                    empArrayListsToUpdate.addAll(get()); // Efficiently update the list

                    modelForEmp = createCustomModel(empArrayListsToUpdate);
                    empTable.setModel(modelForEmp);
                    setupTableRenderersAndEditors();

                    empTable.revalidate();
                    empTable.repaint();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }


}
