package org.MiniDev.Backoffice.StockManagement;

import SqlLoadersAndUpdater.FetchCounterLists;
import UI.*;
import UI.CustomCellEditorTable.*;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.Backoffice.StockManagement.CounterTableActionPages.CounterEditDialogPage;
import org.MiniDev.Backoffice.StockManagement.CounterTableActionPages.CounterViewDialogPage;
import org.MiniDev.OOP.CounterLists;
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

public class CounterInnerPage extends Component {

    protected static DefaultTableModel modelForTable;
    protected static JTable counterTable;
    protected JScrollPane counterTableScrollPane;
    protected JPanel counterInnerPanel;
    protected RoundedPanel tableRoundedCounterPanel;
    protected JTableHeader counterTableHeader;
    protected List<CounterLists> counterArrayListsToUpdate;

    public CounterInnerPage() {
    }

    /**
     * Creates the main StockInnerPage JPanel.
     */
    protected IconTextButton exportDownload;


    protected JPanel createCounterInnerPage() {
        counterInnerPanel = new JPanel(new BorderLayout());
        counterInnerPanel.setOpaque(false);

        JPanel searchHoldingPanel = new JPanel(new BorderLayout());
        JPanel tableHoldingPanel = new JPanel(new BorderLayout());

        searchHoldingPanel.setBackground(COLOR_WHITE);
        searchHoldingPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));
        tableHoldingPanel.setOpaque(false);

        JPanel rightContainersPanel = new JPanel();
        rightContainersPanel.setOpaque(false);
        rightContainersPanel.setLayout(new BoxLayout(rightContainersPanel, BoxLayout.X_AXIS));
        rightContainersPanel.setPreferredSize(new Dimension(305, 40));

        exportDownload = createIconButtonForCounter("Export", "/ExcelIcon.svg", 14, Color.decode("#65a30d"), COLOR_WHITE, 90, 35);
        exportDownload.addActionListener(e -> {

            String filePath = "CounterLists.xls";  // Define the file path for the exported file
            exportCounterListsToExcel(counterArrayListsToUpdate, filePath);  // Export data to Excel
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
        rightContainersPanel.add(getCounterTextField());

        searchHoldingPanel.add(rightContainersPanel, BorderLayout.EAST);


        tableHoldingPanel.add(createTableWithActions(), BorderLayout.CENTER);

        // Add panels to the main stockInnerPanel
        counterInnerPanel.add(searchHoldingPanel, BorderLayout.NORTH);
        counterInnerPanel.add(tableHoldingPanel, BorderLayout.CENTER);

        return counterInnerPanel;
    }

    /**
     * Sets up custom renderers and editors for the table columns.
     */
    private void setupTableRenderersAndEditors() {
        counterTable.getColumnModel().getColumn(7).setCellRenderer(new TableActionCellRender());
        counterTable.getColumnModel().getColumn(7).setCellEditor(new TableActionCellEditor(eventCounter));

        counterTable.getColumnModel().getColumn(0).setCellRenderer(createCenteredRenderer());
        counterTable.getColumnModel().getColumn(1).setCellRenderer(createCenteredRenderer());
        counterTable.getColumnModel().getColumn(3).setCellRenderer(createCenteredRenderer());
        counterTable.getColumnModel().getColumn(4).setCellRenderer(createCenteredRenderer());


        counterTable.getColumnModel().getColumn(0).setMaxWidth(50);
        counterTable.getColumnModel().getColumn(1).setPreferredWidth(10);
        counterTable.getColumnModel().getColumn(2).setPreferredWidth(30);
        counterTable.getColumnModel().getColumn(3).setPreferredWidth(10);
        counterTable.getColumnModel().getColumn(4).setPreferredWidth(10);
        counterTable.getColumnModel().getColumn(5).setPreferredWidth(35);
        counterTable.getColumnModel().getColumn(1).setPreferredWidth(35);
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


    protected void filterCounters(String query) {
        // Convert query to lowercase once
        String lowerCaseQuery = query.toLowerCase();
        List<CounterLists> filteredCounterList = new ArrayList<>();

        // Iterate through the list and filter by FoodName or FoodSerialNumber
        for (CounterLists oneCounter : counterArrayListsToUpdate) {
            String counterNameLower = oneCounter.getCounterName().toLowerCase(); // Convert once
            String counterID = String.valueOf(oneCounter.getCounterID()); // Convert once

            if (counterNameLower.contains(lowerCaseQuery) || counterID.contains(lowerCaseQuery)) {
                filteredCounterList.add(oneCounter);
            }
        }

        // Update the model with the filtered list
        modelForTable = createCustomModel(filteredCounterList);
        counterTable.setModel(modelForTable);
        setupTableRenderersAndEditors();
        counterTable.revalidate();
        counterTable.repaint();
    }


    /**
     * Creates the search text field for the stock search bar.
     */
    protected SearchTopPaneCreator getCounterTextField() {
        SearchTopPaneCreator searchTopPaneCreator = new SearchTopPaneCreator("Search the product .....", "/SearchIcon.svg");
        searchTopPaneCreator.setPreferredSize(new Dimension(220, 40));

        JTextField searchCounterField = searchTopPaneCreator.getSearchTextField();

        searchCounterField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterCounters(searchCounterField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterCounters(searchCounterField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterCounters(searchCounterField.getText());
            }
        });

        return searchTopPaneCreator;
    }

    private DefaultTableModel createCustomModel(List<CounterLists> counterArrayListsToUpdate) {
        modelForTable = new DefaultTableModel(
                new Object[][]{},
                new String[]{"#", "CounterID", "Counter Name", "Counter Level", "Active", "Printer Name", "Printer Address", "Action"}
        ) {
            final boolean[] canEdit = new boolean[]{false, false, false, false, false, false, false, true};

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };

        int rowIndex = 1;
        for (CounterLists oneCounter : counterArrayListsToUpdate) {

            modelForTable.addRow(new Object[]{
                    rowIndex++,
                    oneCounter.getCounterID(),
                    oneCounter.getCounterName(),
                    oneCounter.getCounterLevel(),
                    oneCounter.getActiveYN(),
                    oneCounter.getMainPrinterPortName(),
                    oneCounter.getMainPrinterPortAddress(),
                    null // This will be for the action buttons (Edit, Delete, View)
            });
        }

        return modelForTable;
    }

    protected static TableActionEvent eventCounter;

    private RoundedPanel createTableWithActions() {
        tableRoundedCounterPanel = new RoundedPanel(10);
        tableRoundedCounterPanel.setLayout(new BorderLayout());
        tableRoundedCounterPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        tableRoundedCounterPanel.setBackground(COLOR_PANEL_GRAY);

        FetchCounterLists fetchCounterLists = new FetchCounterLists();
        counterArrayListsToUpdate = fetchCounterLists.getCounterArrayLists();

        modelForTable = createCustomModel(counterArrayListsToUpdate);
        counterTable = new JTable(modelForTable) {
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

        counterTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder());
                return this;
            }
        });

        counterTable.setAutoCreateRowSorter(true);
        counterTable.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13));

        counterTableHeader = counterTable.getTableHeader();
        counterTableHeader.setBackground(COLOR_PANEL_GRAY);
        counterTableHeader.setReorderingAllowed(true);
        counterTableHeader.setPreferredSize(new Dimension(100, 40));
        counterTable.setTableHeader(counterTableHeader);

        counterTable.setOpaque(true);
        counterTable.setBackground(COLOR_PANEL_GRAY);
        counterTable.setRowHeight(55);
        counterTable.setSelectionBackground(COLOR_SELECT_BLUE);
        counterTable.setShowHorizontalLines(false);
        counterTable.setShowVerticalLines(false);

        eventCounter = new TableActionEvent() {
            @Override
            public void onEdit(int row) {
                int cellValue = (int) counterTable.getValueAt(row, 1);
                toEditCounterList(cellValue);
            }

            @Override
            public void onDelete(int row) {
                if (counterTable.isEditing()) {
                    counterTable.getCellEditor().stopCellEditing();
                }
                JOptionPane.showMessageDialog(counterInnerPanel, "Not Allowed to Delete Counter Table.");
            }

            @Override
            public void onView(int row) {
                int cellValue = (int) counterTable.getValueAt(row, 1);
                toViewCounterList(cellValue);
            }
        };


        setupTableRenderersAndEditors();

        // Initialize the scroll pane and add the table to it
        counterTableScrollPane = new JScrollPane(counterTable);
        counterTableScrollPane.setBackground(Color.WHITE);
        counterTableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        counterTableScrollPane.getViewport().setBackground(Color.WHITE);
        counterTableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        counterTableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        counterTableScrollPane.getVerticalScrollBar().setUI(new MacOSScrollPane.MacOSScrollBarUI(COLOR_PANEL_GRAY));

        // Add the scroll pane to the rounded panel
        tableRoundedCounterPanel.add(counterTableScrollPane, BorderLayout.CENTER);

        return tableRoundedCounterPanel;
    }

    protected void toViewCounterList(int counterID) {
        List<CounterLists> editCounter = new ArrayList<>();
        for (CounterLists oneCounter : counterArrayListsToUpdate) {
            int counterIDFind1 = oneCounter.getCounterID();
            // Check if the counterID matches
            if (counterIDFind1 == counterID) {
                editCounter.add(oneCounter); // Add the found counter to the editCounter list
            }
        }

        if (editCounter.isEmpty()) {

            return;
        }

        CounterLists selectCounter = editCounter.get(0); // Use get(0) instead of getFirst()
        String counterName = selectCounter.getCounterName();
        int counterLevel = selectCounter.getCounterLevel();
        char counterActiveYN = selectCounter.getActiveYN();
        String counterMainPrinterPortName = selectCounter.getMainPrinterPortName();

        new CounterViewDialogPage().showCounterViewPanel(counterID, counterName, counterLevel, counterActiveYN, counterMainPrinterPortName);
    }


    protected void toEditCounterList(int counterID) {
        List<CounterLists> editCounter = new ArrayList<>();
        for (CounterLists oneCounter : counterArrayListsToUpdate) {
            int counterIDFind1 = oneCounter.getCounterID();
            // Check if the counterID matches
            if (counterIDFind1 == counterID) {
                editCounter.add(oneCounter); // Add the found counter to the editCounter list
            }
        }

        if (editCounter.isEmpty()) {

            return;
        }

        CounterLists selectCounter = editCounter.get(0); // Use get(0) instead of getFirst()
        String counterName = selectCounter.getCounterName();
        int counterLevel = selectCounter.getCounterLevel();
        char counterActiveYN = selectCounter.getActiveYN();
        String counterMainPrinterPortName = selectCounter.getMainPrinterPortName();

        new CounterEditDialogPage().showCounterEditPanel(counterID, counterName, counterLevel, counterActiveYN, counterMainPrinterPortName);
    }

    public void refreshTableAfterCounterUpdater() {
        FetchCounterLists fetchCounterLists = new FetchCounterLists();
        counterArrayListsToUpdate = fetchCounterLists.getCounterArrayLists();

        modelForTable = createCustomModel(counterArrayListsToUpdate);
        counterTable.setModel(modelForTable);

        // Set up renderers and editors again
        setupTableRenderersAndEditors();

        // Revalidate and repaint the table
        counterTable.revalidate();
        counterTable.repaint();
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


    public void exportCounterListsToExcel(List<CounterLists> counterLists, String filePath) {
        WritableWorkbook workbook = null;
        try {
            // Create a writable workbook at the given path
            workbook = Workbook.createWorkbook(new File(filePath));

            // Create a new sheet in the workbook
            WritableSheet sheet = workbook.createSheet("Counter Data", 0);

            // Write column headers
            String[] columnNames = {"Counter_Name", "Counter_ID", "Food_Level", "ActiveYN", "MainPrinterPortName", "MainPrinterPortAddress"};
            for (int col = 0; col < columnNames.length; col++) {
                Label label = new Label(col, 0, columnNames[col]);
                sheet.addCell(label);
            }

            // Write the data rows
            for (int row = 0; row < counterLists.size(); row++) {
                CounterLists oneCounter = counterLists.get(row);
                sheet.addCell(new Label(0, row + 1, oneCounter.getCounterName()));
                sheet.addCell(new Label(1, row + 1, String.valueOf(oneCounter.getCounterID())));
                sheet.addCell(new Label(2, row + 1, String.valueOf(oneCounter.getCounterLevel())));
                sheet.addCell(new Label(3, row + 1, String.valueOf(oneCounter.getActiveYN())));
                sheet.addCell(new Label(4, row + 1, oneCounter.getMainPrinterPortName()));
                sheet.addCell(new Label(5, row + 1, oneCounter.getMainPrinterPortAddress()));
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
