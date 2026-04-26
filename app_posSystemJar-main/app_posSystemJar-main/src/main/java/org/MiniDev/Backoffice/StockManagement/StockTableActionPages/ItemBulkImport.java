package org.MiniDev.Backoffice.StockManagement.StockTableActionPages;

import DBConnection.DBConnection;
import SqlLoadersAndUpdater.FetchCounterLists;
import SqlLoadersAndUpdater.FetchFoodCatLists;
import SqlLoadersAndUpdater.GetExistingItemSrNo;
import UI.*;

import javax.swing.*;
import java.awt.Color;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.MiniDev.Backoffice.StockManagement.StockInnerPage;
import org.MiniDev.Backoffice.StockManagement.StockManagementPage;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;
import java.util.*;
import java.util.List;

import static UI.UserFinalSettingsVar.*;


public class ItemBulkImport extends Component {

    protected static DefaultTableModel modelTemp;
    protected static JTable tableTemp;
    protected JScrollPane jScrollPane1;
    protected JPanel bulkPagePanel;
    protected RoundedPanel tableRoundedPanel;
    protected JTableHeader jTableHeader;
    protected RoundedTextFieldV2 totalCntRowDisplay = new RoundedTextFieldV2(10, 1, COLOR_FONT_GRAY);
    protected String tempTableRowCountInitText = "0";
    protected int tempTableRowCountInt = 0;
    protected int errorVerificationTemp = 0;
    protected int srCodeColumnErrorCnt = 0;
    protected int itemNameColumnErrorCnt = 0;
    protected int categoryAndCounterColumnsErrorCnt = 0;
    protected int numberColumnsErrorCnt = 0;
    protected boolean isVerifiedFinished = false;


    public ItemBulkImport() {
    }

    public JPanel createItemBulkPage() {
        bulkPagePanel = new JPanel(new BorderLayout());
        bulkPagePanel.setOpaque(false);

        JPanel tableHoldingPanel = new JPanel(new BorderLayout());
        tableHoldingPanel.setOpaque(false);
        tableHoldingPanel.add(createTable(), BorderLayout.CENTER);

        // Add panels to the main bulkPagePanel
        bulkPagePanel.add(getHeaderPanel(), BorderLayout.NORTH);
        bulkPagePanel.add(tableHoldingPanel, BorderLayout.CENTER);
        bulkPagePanel.add(getBottomPanel(), BorderLayout.SOUTH);

        return bulkPagePanel;
    }

    protected JPanel getHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        headerPanel.setPreferredSize(new Dimension(bulkPagePanel.getWidth(), 40));
        headerPanel.setBackground(COLOR_WHITE);

        JPanel mid = new JPanel();
        mid.setOpaque(false);


        JPanel left = new JPanel();
        left.setLayout(new BorderLayout());
        left.setPreferredSize(new Dimension(180, headerPanel.getHeight()));
        left.setOpaque(false);

        JLabel pageLabel = new JLabel("<html><u>Items Multi-Import Register</u></html>");
        pageLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));
        pageLabel.setForeground(COLOR_FONT_GRAY);
        left.add(pageLabel, BorderLayout.WEST);


        JPanel right = new JPanel();
        right.setPreferredSize(new Dimension(280, headerPanel.getHeight()));
        right.setLayout(new GridLayout(1, 3, 10, 10));
        right.setOpaque(false);

        right.add(downloadFormatButton());
        right.add(loadSheetButton());
        right.add(verifySheetButton());

        headerPanel.add(left, BorderLayout.WEST);
        headerPanel.add(mid, BorderLayout.CENTER);
        headerPanel.add(right, BorderLayout.EAST);

        return headerPanel;
    }


    private void setupTableRenderersAndEditors() {
        tableTemp.getColumnModel().getColumn(0).setCellRenderer(createCenteredRenderer());
        tableTemp.getColumnModel().getColumn(3).setCellRenderer(createCenteredRenderer());
        tableTemp.getColumnModel().getColumn(4).setCellRenderer(createCenteredRenderer());
        tableTemp.getColumnModel().getColumn(5).setCellRenderer(createCenteredRenderer());
        tableTemp.getColumnModel().getColumn(7).setCellRenderer(createCenteredRenderer());

        tableTemp.getColumnModel().getColumn(0).setPreferredWidth(100);
        tableTemp.getColumnModel().getColumn(1).setPreferredWidth(135);
        tableTemp.getColumnModel().getColumn(2).setPreferredWidth(90);
        tableTemp.getColumnModel().getColumn(3).setPreferredWidth(85);
        tableTemp.getColumnModel().getColumn(4).setPreferredWidth(85);
        tableTemp.getColumnModel().getColumn(5).setPreferredWidth(75);
        tableTemp.getColumnModel().getColumn(6).setPreferredWidth(90);
        tableTemp.getColumnModel().getColumn(7).setPreferredWidth(70);
        tableTemp.getColumnModel().getColumn(8).setPreferredWidth(150);

        tableTemp.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
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


    private DefaultTableModel createCustomModelTemp() {
        modelTemp = new DefaultTableModel(
                new Object[][]{},
                new String[]{"SerialCode", "Name", "Category", "SellPrice", "BuyPrice", "StocksCnt", "Counter", "Promotion", "Desc"}
        ) {
            final boolean[] canEdit = new boolean[]{false, false, false, false, false, false, false, false, false};

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };

        return modelTemp;
    }


    private RoundedPanel createTable() {
        tableRoundedPanel = new RoundedPanel(10);
        tableRoundedPanel.setLayout(new BorderLayout());
        tableRoundedPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        tableRoundedPanel.setBackground(COLOR_PANEL_GRAY);

        modelTemp = createCustomModelTemp();
        tableTemp = new JTable(modelTemp) {
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

        // Set up the table properties
        tableTemp.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder());
                return this;
            }
        });

        tableTemp.setAutoCreateRowSorter(true);
        setupTableRenderersAndEditors(); // Call to set fixed widths

        jTableHeader = tableTemp.getTableHeader();
        jTableHeader.setBackground(COLOR_PANEL_GRAY);
        jTableHeader.setReorderingAllowed(true);
        jTableHeader.setPreferredSize(new Dimension(100, 40));

        tableTemp.setOpaque(true);
        tableTemp.setBackground(COLOR_PANEL_GRAY);
        tableTemp.setRowHeight(55);
        tableTemp.setSelectionBackground(COLOR_SELECT_BLUE);
        tableTemp.setShowHorizontalLines(false);
        tableTemp.setShowVerticalLines(false);

        jScrollPane1 = new JScrollPane(tableTemp);
        jScrollPane1.setBackground(Color.WHITE);
        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(Color.WHITE);
        jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.getVerticalScrollBar().setUI(new MacOSScrollPane.MacOSScrollBarUI(COLOR_PANEL_GRAY));

        tableRoundedPanel.add(jScrollPane1, BorderLayout.CENTER);

        return tableRoundedPanel;
    }

    protected JPanel getBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setPreferredSize(new Dimension(bulkPagePanel.getWidth(), 43));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        bottomPanel.setOpaque(false);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.setPreferredSize(new Dimension(280, 43));
        buttonPanel.setOpaque(false);
        buttonPanel.add(importButtonImportPage());
        buttonPanel.add(clearCurrentTableButton());
        buttonPanel.add(closeButtonImportPage());

        JPanel leftDisplayPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        leftDisplayPanel.setOpaque(false);
        leftDisplayPanel.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));
        leftDisplayPanel.setPreferredSize(new Dimension(150, 40));

        // Create the label
        JLabel totalCountLabel = new JLabel("Total Count: ");
        totalCountLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));
        totalCountLabel.setForeground(COLOR_FONT_GRAY); // Set text color if needed

        leftDisplayPanel.add(totalCountLabel);
        leftDisplayPanel.add(initializedDisplayField(totalCntRowDisplay, tempTableRowCountInitText));

        bottomPanel.add(leftDisplayPanel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        return bottomPanel;
    }

    protected RoundedTextFieldV2 initializedDisplayField(RoundedTextFieldV2 textFieldV2, String initializedText) {
        textFieldV2.setText(initializedText);
        textFieldV2.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));
        textFieldV2.setForeground(COLOR_FONT_GRAY);
        textFieldV2.setHorizontalAlignment(SwingConstants.LEFT);
        textFieldV2.setEditable(false);
        textFieldV2.setFocusable(false);
        textFieldV2.setBackground(COLOR_WHITE);
        return textFieldV2;
    }


    private void readAndPutSheetData(jxl.Sheet sheet) {
        modelTemp.setRowCount(0);
        for (int rowIndex = 1; rowIndex < sheet.getRows(); rowIndex++) {
            Object[] rowData = new Object[sheet.getColumns()];
            for (int colIndex = 0; colIndex < sheet.getColumns(); colIndex++) {
                jxl.Cell cell = sheet.getCell(colIndex, rowIndex);
                rowData[colIndex] = cell.getContents(); // Store cell content
            }
            modelTemp.addRow(rowData); // Add row data to the model
        }
        collectRowCount(modelTemp.getRowCount());
    }

    private List<String> getExistingCounterNames() {
        FetchCounterLists fetchCounterLists = new FetchCounterLists();
        List<String> counterNames = fetchCounterLists.getCounterLists();
        return counterNames;
    }

    private List<String> getExistingFoodCat() {
        FetchFoodCatLists fetchFoodCatLists = new FetchFoodCatLists();
        List<String> foodCat = fetchFoodCatLists.getFoodCatNames();
        return foodCat;
    }

    private List<String> getExistingSrCodes() {
        GetExistingItemSrNo getExistingItemSrNo = new GetExistingItemSrNo();
        List<String> srCodes = getExistingItemSrNo.fetchExistingSrCodes();
        return srCodes;
    }

    private void verifyTempTable() {
        // Reset error counts
        tempTableRowCountInt = 0;
        errorVerificationTemp = 0;
        srCodeColumnErrorCnt = 0;
        itemNameColumnErrorCnt = 0;
        categoryAndCounterColumnsErrorCnt = 0;
        numberColumnsErrorCnt = 0;

        isVerifiedFinished = true;

        List<String> existingSrNoCollector = new ArrayList<>();
        List<String> existingNamesCollector = new ArrayList<>();
        List<String> existingFoodCatCollector = new ArrayList<>();
        List<String> existingCounterCollector = new ArrayList<>();
        List<String> existingSellPriceCollector = new ArrayList<>();
        List<String> existingBuyPriceCollector = new ArrayList<>();
        List<String> existingStockCntCollector = new ArrayList<>();
        List<String> existingPromotionCollector = new ArrayList<>();

        // Fetch existing values once for faster lookup
        Set<String> existingCounterNamesSet = new HashSet<>(getExistingCounterNames());
        Set<String> existingSrCodesSet = new HashSet<>(getExistingSrCodes());
        Set<String> srNoSet = new HashSet<>(); // To track duplicates

        // Iterate through each row in modelTemp
        for (int rowIndex = 0; rowIndex < modelTemp.getRowCount(); rowIndex++) {
            // Get values from the respective columns
            String srNoString = String.valueOf(modelTemp.getValueAt(rowIndex, 0));
            String nameTempValue = String.valueOf(modelTemp.getValueAt(rowIndex, 1));
            String foodCatTempValue = String.valueOf(modelTemp.getValueAt(rowIndex, 2));
            String sellValue = String.valueOf(modelTemp.getValueAt(rowIndex, 3));
            String buyValue = String.valueOf(modelTemp.getValueAt(rowIndex, 4));
            String stockCntValue = String.valueOf(modelTemp.getValueAt(rowIndex, 5));
            String counterTempValue = String.valueOf(modelTemp.getValueAt(rowIndex, 6));
            String promoValue = String.valueOf(modelTemp.getValueAt(rowIndex, 7));

            // Check for duplicates
            boolean isDuplicateInColumn = !srNoSet.add(srNoString); // add() returns false if it already existed
            if (isDuplicateInColumn || existingSrCodesSet.contains(srNoString) || srNoString.isBlank()) {
                existingSrNoCollector.add(srNoString);
                srCodeColumnErrorCnt++;
                errorVerificationTemp++;
            }

            if (nameTempValue.isBlank()) {
                existingNamesCollector.add(nameTempValue);
                itemNameColumnErrorCnt++;
                errorVerificationTemp++;
            }

            if (foodCatTempValue.isBlank()) {
                existingFoodCatCollector.add(foodCatTempValue);
                categoryAndCounterColumnsErrorCnt++;
                errorVerificationTemp++;
            }

            if (!existingCounterNamesSet.contains(counterTempValue)) {
                existingCounterCollector.add(counterTempValue);
                categoryAndCounterColumnsErrorCnt++;
                errorVerificationTemp++;
            }

            if (isInvalidNumber(sellValue)) {
                existingSellPriceCollector.add(sellValue);
                numberColumnsErrorCnt++;
                errorVerificationTemp++;
            }

            if (isInvalidNumber(buyValue)) {
                existingBuyPriceCollector.add(buyValue);
                numberColumnsErrorCnt++;
                errorVerificationTemp++;
            }

            if (isInvalidNumber(stockCntValue) || stockCntValue.isBlank()) {
                existingStockCntCollector.add(stockCntValue);
                numberColumnsErrorCnt++;
                errorVerificationTemp++;
            }

            if (containsOnlyAlphabets(promoValue.trim())) {
                existingPromotionCollector.add(promoValue);
                numberColumnsErrorCnt++;
                errorVerificationTemp++;
            }
        }
    }

    // Helper method to check if a string is a valid number
    private boolean isInvalidNumber(String value) {
        return containsOnlyAlphabets(value.trim()) || value.isBlank() || value.equals("0");
    }

    // Helper method to check if a string contains only alphabets
    private boolean containsOnlyAlphabets(String str) {
        return str != null && str.matches("[a-zA-Z]+");
    }


    private void collectRowCount(int rowCnt) {
        tempTableRowCountInt = rowCnt;
    }

    private void prepareDisplayRowCount() {
        DecimalFormat df = new DecimalFormat("#,###");
        totalCntRowDisplay.setText(df.format(tempTableRowCountInt));
    }

    private void resetDisplayRowCount() {
        totalCntRowDisplay.setText(tempTableRowCountInitText);
        errorVerificationTemp = 0;
        srCodeColumnErrorCnt = 0;
        itemNameColumnErrorCnt = 0;
        categoryAndCounterColumnsErrorCnt = 0;
        numberColumnsErrorCnt = 0;
    }

    private void readXLSFile(File file) {
        try {
            Workbook workbook = Workbook.getWorkbook(file);
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                jxl.Sheet sheet = workbook.getSheet(i);
                readAndPutSheetData(sheet); // This will populate modelTemp
            }
            workbook.close();
            // Refresh the table to reflect changes
            tableTemp.setModel(modelTemp); // This line may not be necessary since modelTemp is already set in JTable
            prepareDisplayRowCount();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(bulkPagePanel, "Error reading the Excel file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void exportFormatExcel(String filePath) {
        WritableWorkbook workbook = null;
        try {
            workbook = Workbook.createWorkbook(new File(filePath));
            WritableSheet sheet = workbook.createSheet("Item Entry Format", 0);

            String[] columnNames = {"Serial Code", "Item Name", "Item Category", "Sell Price",
                    "Original Price", "Stock_Available_Cnt", "Item Counter Name", "Promotion Percentage", "Description"};
            for (int col = 0; col < columnNames.length; col++) {
                Label label = new Label(col, 0, columnNames[col]);
                sheet.addCell(label);
            }
            workbook.write();
            JOptionPane.showMessageDialog(bulkPagePanel, "Data successfully exported to Excel!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException | WriteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(bulkPagePanel, "Error exporting data to Excel: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException | WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private RoundedBorderButton verifySheetButton() {
        RoundedBorderButton verifyButton = UIAppButtonFactory.creatRoundedBorderButton("Verify", "/ActiveStatusIcon.svg", COLOR_FONT_GRAY, 14);
        verifyButton.setHoverBackgroundColor(Color.LIGHT_GRAY);
        verifyButton.setPressedBackgroundColor(COLOR_PANEL_GRAY);
        verifyButton.setPreferredSize(new Dimension(90, 35));

        verifyButton.addActionListener(e -> {
            checkTempTableBeforeVerify();
        });

        return verifyButton;
    }


    private void checkTempTableBeforeVerify() {
        if (modelTemp.getRowCount() == 0) {
            JOptionPane.showMessageDialog(bulkPagePanel, "Make sure correct data of excel cells", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            verifyTempTable();
            doingVerifyButtonBackend(errorVerificationTemp);
        }
    }

    private void doingVerifyButtonBackend(int errorCnt) {
        if (errorCnt > 0) {
            CustomPopUpFactory.showVerifyBeforeItemUpload(bulkPagePanel, false, srCodeColumnErrorCnt, itemNameColumnErrorCnt, categoryAndCounterColumnsErrorCnt, numberColumnsErrorCnt);
        } else {
            CustomPopUpFactory.showVerifyBeforeItemUpload(bulkPagePanel, true, srCodeColumnErrorCnt, itemNameColumnErrorCnt, categoryAndCounterColumnsErrorCnt, numberColumnsErrorCnt);
        }
    }


    private RoundedBorderButton loadSheetButton() {
        RoundedBorderButton loadButton = UIAppButtonFactory.creatRoundedBorderButton("Load", "/ExcelGreenIcon.svg", COLOR_FONT_GRAY, 14);
        loadButton.setHoverBackgroundColor(Color.LIGHT_GRAY);
        loadButton.setPressedBackgroundColor(COLOR_PANEL_GRAY);
        loadButton.setPreferredSize(new Dimension(90, 35));

        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Choose an Excel file");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files", "xls"));

            int userSelection = fileChooser.showOpenDialog(bulkPagePanel);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToLoad = fileChooser.getSelectedFile();
                if (fileToLoad.getName().endsWith(".xls")) {
                    readXLSFile(fileToLoad);
                } else {
                    JOptionPane.showMessageDialog(bulkPagePanel, "Invalid file format. Please select an .xls file.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return loadButton;
    }


    private RoundedBorderButton downloadFormatButton() {
        RoundedBorderButton downloadButton = UIAppButtonFactory.creatRoundedBorderButton("Download", "/ExcelGreenIcon.svg", COLOR_FONT_GRAY, 14);
        downloadButton.setHoverBackgroundColor(Color.LIGHT_GRAY);
        downloadButton.setPressedBackgroundColor(COLOR_PANEL_GRAY);
        downloadButton.setPreferredSize(new Dimension(90, 35));
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = "Format.xls";
                exportFormatExcel(filePath);

                File excelFile = new File(filePath);
                if (excelFile.exists()) {
                    try {
                        Desktop.getDesktop().open(excelFile);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(bulkPagePanel, "Unable to open the exported Excel file.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(bulkPagePanel, "The exported file was not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return downloadButton;
    }


    private void afterImportingBackground(boolean isVerifiedFinished) {
        if (isVerifiedFinished) {
            if (errorVerificationTemp > 0 || modelTemp.getRowCount() == 0) {
                JOptionPane.showMessageDialog(bulkPagePanel, "Make sure correct data of excel cells", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                uploadNewItemsTableToDatabase();
                JOptionPane.showMessageDialog(bulkPagePanel, "Import Done");
                refreshTempTable();
                new StockInnerPage().refreshTableAfterItemsUpdater();
            }
        } else {
            JOptionPane.showMessageDialog(bulkPagePanel, "Make sure verify first", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }


    private IconTextButton importButtonImportPage() {
        IconTextButton updateButton = UIAppButtonFactory.createIconTextButton("Import", "/UpdateIcon.svg", COLOR_GREEN);
        updateButton.addActionListener(e -> {
            afterImportingBackground(isVerifiedFinished);
        });
        return updateButton;
    }

    private RoundedBorderButton closeButtonImportPage() {
        RoundedBorderButton closeButton = UIAppButtonFactory.creatRoundedBorderButton("Close", "/CloseIcon.svg", COLOR_ORANGE, 20);
        closeButton.addActionListener(e -> {
            StockManagementPage.stockMenuCardLayout.show(StockManagementPage.mainMenuCardHoldingPanel, "StockInnerPage");
            refreshTempTable();
        });
        return closeButton;
    }

    private RoundedBorderButton clearCurrentTableButton() {
        RoundedBorderButton closeButton = UIAppButtonFactory.creatRoundedBorderButton("Clear", "/ClearIcon.svg", COLOR_ORANGE, 20);
        closeButton.addActionListener(e -> {
            refreshTempTable();
        });
        return closeButton;
    }

    public void refreshTempTable() {
        resetDisplayRowCount();
        modelTemp = createCustomModelTemp();
        tableTemp.setModel(modelTemp);
        setupTableRenderersAndEditors();
        tableTemp.revalidate();
        tableTemp.repaint();
    }


    private void uploadNewItemsTableToDatabase() {
        EditableFoodImage foodImg = new EditableFoodImage();
        Connection connection = null;

        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false); // Disable auto-commit

            try (CallableStatement stmt = connection.prepareCall("{CALL sp_UpdateFood(? ,?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {
                int batchSize = 100;
                int count = 0;

                for (int rowIndex = 0; rowIndex < modelTemp.getRowCount(); rowIndex++) {
                    byte[] defaultImage = foodImg.getImageBytes();
                    String srNoString = String.valueOf(modelTemp.getValueAt(rowIndex, 0));
                    String nameTempValue = String.valueOf(modelTemp.getValueAt(rowIndex, 1));
                    String foodCatTempValue = String.valueOf(modelTemp.getValueAt(rowIndex, 2));
                    int sellValue = getIntFromString(String.valueOf(modelTemp.getValueAt(rowIndex, 3)));
                    int buyValue = getIntFromString(String.valueOf(modelTemp.getValueAt(rowIndex, 4)));
                    int stockCntValue = getIntFromString(String.valueOf(modelTemp.getValueAt(rowIndex, 5)));
                    String counterTempValue = String.valueOf(modelTemp.getValueAt(rowIndex, 6));
                    double promoValue = getPromoPerFromString(String.valueOf(modelTemp.getValueAt(rowIndex, 7)));
                    String description = String.valueOf(modelTemp.getValueAt(rowIndex, 8));

                    // Set the parameters for the stored procedure
                    stmt.setBytes(1, defaultImage);
                    stmt.setString(2, srNoString);
                    stmt.setString(3, nameTempValue);
                    stmt.setString(4, foodCatTempValue);
                    stmt.setInt(5, sellValue);
                    stmt.setInt(6, buyValue);
                    stmt.setInt(7, stockCntValue);
                    stmt.setString(8, counterTempValue);
                    stmt.setDouble(9, promoValue);
                    stmt.setString(10, description);
                    stmt.setDouble(11, TAX_PERCENTAGE);

                    // Add the statement to the batch
                    stmt.addBatch();
                    count++;

                    // Execute batch if reached batch size
                    if (count % batchSize == 0) {
                        stmt.executeBatch();
                    }
                }

                // Execute any remaining statements in the batch
                if (count % batchSize != 0) {
                    stmt.executeBatch();
                }

                // Commit the transaction
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
                if (connection != null) {
                    connection.rollback(); // Rollback on error
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle connection errors
        } finally {
            // Ensure the connection is closed
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }




    private double getPromoPerFromString(String numberString) {
        if (numberString.isBlank()) {
            return 0.0;
        } else {
            return Double.parseDouble(numberString) / 100.0;
        }
    }

    private int getIntFromString(String numberString) {
        if (numberString.isBlank()) {
            return 0;
        } else {
            return Integer.parseInt(numberString);
        }
    }

}
