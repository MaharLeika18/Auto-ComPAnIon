package org.MiniDev.Backoffice.ExpenseManagement.CategoryPage;

import SqlLoadersAndUpdater.FetchExpenseCategoryLists;
import UI.*;
import UI.CustomCellEditorTable.TableActionCellEditor;
import UI.CustomCellEditorTable.TableActionCellRender;
import UI.CustomCellEditorTable.TableActionEvent;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.Backoffice.ExpenseManagement.CategoryPage.CategoryTableActionPages.CategoryEditPage;
import org.MiniDev.Backoffice.ExpenseManagement.CategoryPage.CategoryTableActionPages.CategoryViewPage;
import org.MiniDev.Backoffice.ExpenseManagement.CategoryPage.CategoryTableCellCustomRender.CategoryLabelStatus;
import org.MiniDev.Backoffice.ExpenseManagement.CategoryPage.CategoryTableCellCustomRender.TableCellProfile;
import org.MiniDev.OOP.ExpenseCategoryLists;

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
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

public class CategoryPage extends Component {

    protected static DefaultTableModel modelForCategory;
    protected static JTable categoryTable;
    protected JScrollPane categoryTableScrollPane;
    protected JPanel categoryInnerPanel;
    protected RoundedPanel tableRoundedCategoryPanel;
    protected JTableHeader categoryTableHeader;
    protected static List<ExpenseCategoryLists> categoryArrayListsToUpdate;

    public CategoryPage() {
    }


    /**
     * Creates the main StockInnerPage JPanel.
     */
    protected IconTextButton exportDownload;


    public JPanel createCategoryMainPage() {
        categoryInnerPanel = new JPanel(new BorderLayout());
        categoryInnerPanel.setOpaque(false);

        JPanel searchHoldingPanel = new JPanel(new BorderLayout());
        JPanel tableHoldingPanel = new JPanel(new BorderLayout());

        searchHoldingPanel.setBackground(COLOR_WHITE);
        searchHoldingPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));
        tableHoldingPanel.setOpaque(false);

        JPanel rightContainersPanel = new JPanel();
        rightContainersPanel.setOpaque(false);
        rightContainersPanel.setLayout(new BoxLayout(rightContainersPanel, BoxLayout.X_AXIS));
        rightContainersPanel.setPreferredSize(new Dimension(333, 40));

        exportDownload = createIconButtonForEmployee("Export", "/ExcelIcon.svg", 14, Color.decode("#65a30d"), COLOR_WHITE, 90, 35);


        exportDownload.addActionListener(e -> {

            String filePath = "CategoryLists.xls";  // Define the file path for the exported file
            exportEmployeeListsToExcel(categoryArrayListsToUpdate, filePath);  // Export data to Excel
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
        rightContainersPanel.add(getCategoryTextField());

        searchHoldingPanel.add(rightContainersPanel, BorderLayout.EAST);


        tableHoldingPanel.add(createTableWithActions(), BorderLayout.CENTER);

        // Add panels to the main stockInnerPanel
        categoryInnerPanel.add(searchHoldingPanel, BorderLayout.NORTH);
        categoryInnerPanel.add(tableHoldingPanel, BorderLayout.CENTER);

        return categoryInnerPanel;
    }

    /**
     * Sets up custom renderers and editors for the table columns.
     */
    private void setupTableRenderersAndEditors() {
        categoryTable.getColumnModel().getColumn(0).setCellRenderer(createProfileCellRenderer());
        categoryTable.getColumnModel().getColumn(6).setCellRenderer(new TableActionCellRender());
        categoryTable.getColumnModel().getColumn(6).setCellEditor(new TableActionCellEditor(eventForCategoryTable));

        categoryTable.getColumnModel().getColumn(1).setCellRenderer(createCenteredRenderer());
        categoryTable.getColumnModel().getColumn(3).setCellRenderer(createCenteredRenderer());
        categoryTable.getColumnModel().getColumn(4).setCellRenderer(createCenteredRenderer());
        categoryTable.getColumnModel().getColumn(5).setCellRenderer(createRightAlignedRenderer());

        // Set preferred widths
        categoryTable.getColumnModel().getColumn(0).setMaxWidth(200); // Adjust width as needed
        categoryTable.getColumnModel().getColumn(1).setPreferredWidth(15);
        categoryTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        categoryTable.getColumnModel().getColumn(3).setPreferredWidth(45);
        categoryTable.getColumnModel().getColumn(4).setPreferredWidth(35);
        categoryTable.getColumnModel().getColumn(5).setPreferredWidth(70);

        categoryTable.getColumnModel().getColumn(4).setCellRenderer(createStatusCellRenderer());
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


    private TableCellRenderer createProfileCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                JPanel cellPanel = new JPanel(new GridBagLayout()); // Center alignment
                cellPanel.setOpaque(true);
                cellPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1)); // Padding around the profile

                if (value instanceof TableCellProfile profilePanel) {

                    // Handle selection color changes
                    if (isSelected) {
                        profilePanel.setBackground(COLOR_SELECT_BLUE);
                        profilePanel.setForeground(Color.WHITE);
                    } else {
                        profilePanel.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_PANEL_GRAY);
                        profilePanel.setForeground(Color.BLACK);
                    }

                    // Ensure panel matches the profile’s background and add it to the cell panel
                    cellPanel.setBackground(profilePanel.getBackground());
                    cellPanel.add(profilePanel);
                }

                return cellPanel;
            }
        };
    }


    private TableCellRenderer createStatusCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                JPanel cellPanel = new JPanel(new GridBagLayout()); // Center alignment
                cellPanel.setOpaque(true);
                cellPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Padding around the status

                if (value instanceof CategoryLabelStatus statusLabel) {

                    if (isSelected) {
                        statusLabel.setBackground(COLOR_SELECT_BLUE);
                        statusLabel.setForeground(Color.WHITE);
                    } else {
                        statusLabel.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_PANEL_GRAY);
                        statusLabel.setForeground(Color.BLACK);
                    }

                    cellPanel.setBackground(statusLabel.getBackground()); // Ensure panel matches the label’s background
                    cellPanel.add(statusLabel); // Add the status label to the panel
                }

                return cellPanel;
            }
        };
    }


    protected void filterCategory(String query) {
        if (query.isEmpty()) {
            // Reset table to original data when the query is empty
            updateCategoryTable(categoryArrayListsToUpdate);
            return;
        }

        // Execute filtering in the background to avoid UI lag
        SwingWorker<List<ExpenseCategoryLists>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ExpenseCategoryLists> doInBackground() {
                List<ExpenseCategoryLists> filteredList = new ArrayList<>();

                // Convert query to lowercase once for case-insensitive search
                String lowerCaseQuery = query.toLowerCase();

                for (ExpenseCategoryLists category : categoryArrayListsToUpdate) {
                    String catNameLower = category.getExpenseCodeName().toLowerCase();
                    String catIDLower = category.getExpenseCode().toLowerCase();

                    // Check if the query matches either the name or the ID
                    if (catNameLower.contains(lowerCaseQuery) || catIDLower.contains(lowerCaseQuery)) {
                        filteredList.add(category);
                    }
                }

                return filteredList;
            }

            @Override
            protected void done() {
                try {
                    // Update the table with the filtered data
                    updateCategoryTable(get());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        worker.execute();  // Start the background task
    }


    public void refreshExpCategoryTable() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                categoryArrayListsToUpdate.clear();
                FetchExpenseCategoryLists fetchExpenseCategoryLists = new FetchExpenseCategoryLists();
                categoryArrayListsToUpdate = fetchExpenseCategoryLists.getExpenseCategoryLists();
                return null;
            }

            @Override
            protected void done() {
                modelForCategory = createCustomModel(categoryArrayListsToUpdate);
                categoryTable.setModel(modelForCategory);
                setupTableRenderersAndEditors();
                categoryTable.revalidate();
                categoryTable.repaint();
            }
        };
        worker.execute();
    }


    private void updateCategoryTable(List<ExpenseCategoryLists> filteredList) {
        // Create a new table model with the filtered data
        DefaultTableModel newModel = createCustomModel(filteredList);
        categoryTable.setModel(newModel);

        // Reapply custom renderers and editors after setting the new model
        setupTableRenderersAndEditors();

        // Refresh the table UI
        categoryTable.revalidate();
        categoryTable.repaint();
    }


    protected SearchTopPaneCreator getCategoryTextField() {
        SearchTopPaneCreator searchTopPaneCreator =
                new SearchTopPaneCreator("Search Category .....", "/SearchIcon.svg");
        searchTopPaneCreator.setPreferredSize(new Dimension(220, 40));

        JTextField searchField = searchTopPaneCreator.getSearchTextField();

        // Debouncing search calls with Swing Timer
        Timer debounceTimer = new Timer(300, e -> filterCategory(searchField.getText().trim()));
        debounceTimer.setRepeats(false);  // Ensure the timer triggers only once per event

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                debounceTimer.restart();  // Restart timer on text change
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                debounceTimer.restart();  // Restart timer on text change
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                debounceTimer.restart();  // Restart timer on text change
            }
        });

        return searchTopPaneCreator;
    }


    private DefaultTableModel createCustomModel(List<ExpenseCategoryLists> categoryArrayListsToUpdate) {
        modelForCategory = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Type", "Code", "Category", "LastUpdatedDate", "CurrentUsed", "Allocation(ThisYear)", "Action"}
        ) {
            final boolean[] canEdit = new boolean[]{false, false, false, false, false, false, true};

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };
        for (ExpenseCategoryLists oneCategory : categoryArrayListsToUpdate) {

            String statusText = (oneCategory.getCurrentUseYN() == 'Y') ? "Active" : "Inactive";

            CategoryLabelStatus statusCell = new CategoryLabelStatus(statusText);
            TableCellProfile tableCellProfile = new TableCellProfile(oneCategory);

            modelForCategory.addRow(new Object[]{
                    tableCellProfile,
                    oneCategory.getExpenseCode(),
                    oneCategory.getExpenseCodeName(),
                    oneCategory.getLastUpdatedDate(),
                    statusCell,
                    oneCategory.getCurrentYearBudgetAmountString(),
                    null // This will be for the action buttons (Edit, Delete, View)
            });
        }
        return modelForCategory;
    }

    protected static TableActionEvent eventForCategoryTable;

    private RoundedPanel createTableWithActions() {
        tableRoundedCategoryPanel = new RoundedPanel(10);
        tableRoundedCategoryPanel.setLayout(new BorderLayout());
        tableRoundedCategoryPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        tableRoundedCategoryPanel.setBackground(COLOR_PANEL_GRAY);

        FetchExpenseCategoryLists fetchExpenseCategoryLists = new FetchExpenseCategoryLists();
        categoryArrayListsToUpdate = fetchExpenseCategoryLists.getExpenseCategoryLists();

        modelForCategory = createCustomModel(categoryArrayListsToUpdate);
        categoryTable = new JTable(modelForCategory) {
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

        categoryTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder());
                return this;
            }
        });

        categoryTable.setAutoCreateRowSorter(true);

        categoryTableHeader = categoryTable.getTableHeader();
        categoryTableHeader.setBackground(COLOR_PANEL_GRAY);
        categoryTableHeader.setReorderingAllowed(true);
        categoryTableHeader.setPreferredSize(new Dimension(100, 40));
        categoryTable.setTableHeader(categoryTableHeader);

        categoryTable.setOpaque(true);
        categoryTable.setBackground(COLOR_PANEL_GRAY);
        categoryTable.setRowHeight(55);
        categoryTable.setSelectionBackground(COLOR_SELECT_BLUE);
        categoryTable.setShowHorizontalLines(false);
        categoryTable.setShowVerticalLines(false);

        eventForCategoryTable = new TableActionEvent() {
            @Override
            public void onEdit(int row) {
                String cellValue = String.valueOf(categoryTable.getValueAt(row, 1));
                toEditExpenseCategory(cellValue);
            }

            @Override
            public void onDelete(int row) {
                if (categoryTable.isEditing()) {
                    categoryTable.getCellEditor().stopCellEditing();
                }
                JOptionPane.showMessageDialog(categoryInnerPanel, "Not Allowed to Delete Expense Category Table.");
            }

            @Override
            public void onView(int row) {
                String cellValue = String.valueOf(categoryTable.getValueAt(row, 1));
                toViewExpenseCategory(cellValue);
            }
        };


        setupTableRenderersAndEditors();

        // Initialize the scroll pane and add the table to it
        categoryTableScrollPane = new JScrollPane(categoryTable);
        categoryTableScrollPane.setBackground(Color.WHITE);
        categoryTableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        categoryTableScrollPane.getViewport().setBackground(Color.WHITE);
        categoryTableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        categoryTableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        categoryTableScrollPane.getVerticalScrollBar().setUI(new MacOSScrollPane.MacOSScrollBarUI(COLOR_PANEL_GRAY));

        // Add the scroll pane to the rounded panel
        tableRoundedCategoryPanel.add(categoryTableScrollPane, BorderLayout.CENTER);

        return tableRoundedCategoryPanel;
    }

    protected void toViewExpenseCategory(String query) {
        if (query == null || query.isEmpty()) {
            return;
        }
        String lowerCaseQuery = query.toLowerCase();

        List<ExpenseCategoryLists> filteredExpCat = categoryArrayListsToUpdate.stream()
                .filter(expenseCategoryLists -> expenseCategoryLists.getExpenseCode().toLowerCase().contains(lowerCaseQuery))
                .toList();

        if (filteredExpCat.isEmpty()) return;

        ExpenseCategoryLists selectedExpCat = filteredExpCat.get(0);

        new CategoryViewPage().showCounterViewPanel(selectedExpCat);
    }


    protected void toEditExpenseCategory(String query) {
        if (query == null || query.isEmpty()) {
            return;
        }
        String lowerCaseQuery = query.toLowerCase();

        List<ExpenseCategoryLists> filteredExpCat = categoryArrayListsToUpdate.stream()
                .filter(expenseCategoryLists -> expenseCategoryLists.getExpenseCode().toLowerCase().contains(lowerCaseQuery))
                .toList();

        if (filteredExpCat.isEmpty()) return;

        ExpenseCategoryLists selectedExpCat = filteredExpCat.get(0);

        new CategoryEditPage().showCounterEditPanel(selectedExpCat);
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


    public void exportEmployeeListsToExcel(List<ExpenseCategoryLists> empLists, String filePath) {
        WritableWorkbook workbook = null;
        try {
            // Create a writable workbook at the given path
            workbook = Workbook.createWorkbook(new File(filePath));

            // Create a new sheet in the workbook
            WritableSheet sheet = workbook.createSheet("Category Data", 0);

            // Write column headers
            String[] columnNames = {"Code", "Category", "LastUpdatedDate", "CurrentUsed"};
            for (int col = 0; col < columnNames.length; col++) {
                Label label = new Label(col, 0, columnNames[col]);
                sheet.addCell(label);
            }

            // Write the data rows
            for (int row = 0; row < empLists.size(); row++) {
                ExpenseCategoryLists oneCat = empLists.get(row);
                sheet.addCell(new Label(0, row + 1, oneCat.getExpenseCode()));
                sheet.addCell(new Label(1, row + 1, oneCat.getExpenseCodeName()));
                sheet.addCell(new Label(2, row + 1, String.valueOf(oneCat.getLastUpdatedDate())));
                sheet.addCell(new Label(3, row + 1, String.valueOf(oneCat.getCurrentUseYN())));
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


}
