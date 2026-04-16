package org.MiniDev.Backoffice.StockManagement;

import SqlLoadersAndUpdater.FetchUpdateSectionFoodLists;
import UI.*;
import UI.CustomCellEditorTable.TableActionCellEditor;
import UI.CustomCellEditorTable.TableActionCellRender;
import UI.CustomCellEditorTable.TableActionEvent;
import UI.CustomFloatingButton.floating.FloatingButtonUI;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

import org.MiniDev.OOP.UpdateSectionFoodLists;

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.MiniDev.Backoffice.StockManagement.StockTableActionPages.DeleteDialogPage;
import org.MiniDev.Backoffice.StockManagement.StockTableActionPages.EditDialogPage;
import org.MiniDev.Backoffice.StockManagement.StockTableActionPages.ViewDialogPage;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;

import static UI.IconCreator.createResizedIcon;
import static UI.UserFinalSettingsVar.*;

public class StockInnerPage extends Component {

    protected static DefaultTableModel modelForStock;
    protected static JTable tableStock;
    protected JScrollPane jScrollPane1;
    protected JPanel stockInnerPanel;
    protected RoundedPanel tableRoundedPanel;
    protected JTableHeader jTableHeader;
    protected static List<UpdateSectionFoodLists> foodArrayListsToUpdate = new ArrayList<>();

    public StockInnerPage() {
    }

    /**
     * Creates the main StockInnerPage JPanel.
     */
    protected IconTextButton exportDownload;
    protected IconTextButton bulkImport;

    protected JPanel createStockInnerPage() {
        stockInnerPanel = new JPanel(new BorderLayout());
        stockInnerPanel.setOpaque(false);

        JPanel searchHoldingPanel = new JPanel(new BorderLayout());
        JPanel tableHoldingPanel = new JPanel(new BorderLayout());

        searchHoldingPanel.setBackground(COLOR_WHITE);
        searchHoldingPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));
        tableHoldingPanel.setOpaque(false);

        JPanel rightContainersPanel = new JPanel();
        rightContainersPanel.setOpaque(false);
        rightContainersPanel.setLayout(new BoxLayout(rightContainersPanel, BoxLayout.X_AXIS));
        rightContainersPanel.setPreferredSize(new Dimension(400, 40));

        exportDownload = createIconButtonForStock("Export", "/ExcelIcon.svg", 14, Color.decode("#65a30d"), COLOR_WHITE, 90, 35);
        bulkImport = createIconButtonForStock("Import", "/BulkUploadIcon.svg", 14, COLOR_BLUE, COLOR_WHITE, 90, 35);

        exportDownload.addActionListener(e -> {
            String filePath = "StockData.xls";  // Define the file path for the exported file
            exportFoodListToExcel(foodArrayListsToUpdate, filePath);  // Export data to Excel

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

        bulkImport.addActionListener(e -> StockManagementPage.stockMenuCardLayout.show(StockManagementPage.mainMenuCardHoldingPanel, "StockExcelUpload"));


        rightContainersPanel.add(bulkImport);
        rightContainersPanel.add(Box.createHorizontalStrut(7));
        rightContainersPanel.add(exportDownload);
        rightContainersPanel.add(Box.createHorizontalStrut(7));
        rightContainersPanel.add(getStockTextField());

        searchHoldingPanel.add(rightContainersPanel, BorderLayout.EAST);


        tableHoldingPanel.add(new JLayer(createTableWithActions(), new FloatingButtonUI("NewItemEntryPage")));

        // Add panels to the main stockInnerPanel
        stockInnerPanel.add(searchHoldingPanel, BorderLayout.NORTH);
        stockInnerPanel.add(tableHoldingPanel, BorderLayout.CENTER);

        return stockInnerPanel;
    }


    /**
     * Sets up custom renderers and editors for the table columns.
     */
    private void setupTableRenderersAndEditors() {
        // Set up custom renderers and editors for specific columns
        tableStock.getColumnModel().getColumn(8).setCellRenderer(new TableActionCellRender());
        tableStock.getColumnModel().getColumn(8).setCellEditor(new TableActionCellEditor(eventStock));
        tableStock.getColumnModel().getColumn(1).setCellRenderer(createImageAvatarRenderer());

        tableStock.getColumnModel().getColumn(0).setCellRenderer(createCenteredRenderer());
        tableStock.getColumnModel().getColumn(5).setCellRenderer(createCenteredRenderer());
        tableStock.getColumnModel().getColumn(6).setCellRenderer(createCenteredRenderer());
        tableStock.getColumnModel().getColumn(7).setCellRenderer(createCenteredRenderer());

        // Set preferred widths for columns
        tableStock.getColumnModel().getColumn(0).setMaxWidth(50);
        tableStock.getColumnModel().getColumn(1).setPreferredWidth(30);
        tableStock.getColumnModel().getColumn(2).setPreferredWidth(30);
        tableStock.getColumnModel().getColumn(3).setPreferredWidth(100);
        tableStock.getColumnModel().getColumn(4).setPreferredWidth(45);
        tableStock.getColumnModel().getColumn(5).setPreferredWidth(50);
        tableStock.getColumnModel().getColumn(6).setPreferredWidth(30);
        tableStock.getColumnModel().getColumn(7).setPreferredWidth(30);
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
    private TableCellRenderer createImageAvatarRenderer() {
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


    protected void filterItems(String query) {
        // Convert query to lowercase once
        String lowerCaseQuery = query.toLowerCase();
        List<UpdateSectionFoodLists> filteredFoodList = new ArrayList<>();

        // Iterate through the list and filter by FoodName or FoodSerialNumber
        for (UpdateSectionFoodLists food : foodArrayListsToUpdate) {
            String foodNameLower = food.getFoodName().toLowerCase(); // Convert once
            String foodSerialLower = food.getFoodSerialNumber().toLowerCase(); // Convert once

            if (foodNameLower.contains(lowerCaseQuery) || foodSerialLower.contains(lowerCaseQuery)) {
                filteredFoodList.add(food);
            }
        }

        // Update the model with the filtered list
        modelForStock = createCustomModelForStock(filteredFoodList);
        tableStock.setModel(modelForStock);
        setupTableRenderersAndEditors();
        tableStock.revalidate();
        tableStock.repaint();
    }

    public void refreshTableAfterItemsUpdater() {
        foodArrayListsToUpdate.clear();

        FetchUpdateSectionFoodLists fetchFoodListsToUpdate = new FetchUpdateSectionFoodLists();
        foodArrayListsToUpdate = fetchFoodListsToUpdate.getUpdateSectionFoodLists();

        modelForStock = createCustomModelForStock(foodArrayListsToUpdate);
        tableStock.setModel(modelForStock);
        setupTableRenderersAndEditors();
        tableStock.revalidate();
        tableStock.repaint();
    }

    /**
     * Creates the search text field for the stock search bar.
     */
    protected SearchTopPaneCreator getStockTextField() {
        SearchTopPaneCreator searchTopPaneCreator = new SearchTopPaneCreator("Search the product .....", "/SearchIcon.svg");
        searchTopPaneCreator.setPreferredSize(new Dimension(220, 40));

        JTextField searchItemsField = searchTopPaneCreator.getSearchTextField();

        searchItemsField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterItems(searchItemsField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterItems(searchItemsField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterItems(searchItemsField.getText());
            }
        });

        return searchTopPaneCreator;
    }

    protected static ImageIcon defaultIconForStock;
    protected static ImageIcon foodPhotoIcon;

    private static DefaultTableModel createCustomModelForStock(List<UpdateSectionFoodLists> foodArrayListsToUpdate) {
        modelForStock = new DefaultTableModel(
                new Object[][]{},
                new String[]{"#", "Photo", "Directory", "Name", "Category", "Price", "Stocks", "Item Code", "Action"}
        ) {
            final boolean[] canEdit = new boolean[]{false, false, false, false, false, false, false, false, true};

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };

        defaultIconForStock = new ImageIcon("/DefaultFoodIcon.png");
        int rowIndex = 1;
        for (UpdateSectionFoodLists foodItem : foodArrayListsToUpdate) {
            byte[] foodPhotoBytes = foodItem.getFoodPhoto();
            foodPhotoIcon = (foodPhotoBytes != null) ? new ImageIcon(foodPhotoBytes) : defaultIconForStock;

            DecimalFormat df = new DecimalFormat("#,###");
            String foodPrice = df.format(foodItem.getFoodPrice()) + " MMK";

            modelForStock.addRow(new Object[]{
                    rowIndex++,
                    foodPhotoIcon,
                    foodItem.getCounterName(),
                    foodItem.getFoodName(),
                    foodItem.getFoodCategory(),
                    foodPrice,
                    foodItem.getStockAvailableCnt(),
                    foodItem.getFoodSerialNumber(),
                    null // This will be for the action buttons (Edit, Delete, View)
            });
        }

        return modelForStock;
    }

    protected static TableActionEvent eventStock;

    private RoundedPanel createTableWithActions() {
        tableRoundedPanel = new RoundedPanel(10);
        tableRoundedPanel.setLayout(new BorderLayout());
        tableRoundedPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        tableRoundedPanel.setBackground(COLOR_PANEL_GRAY);

        FetchUpdateSectionFoodLists fetchFoodListsToUpdate = new FetchUpdateSectionFoodLists();
        foodArrayListsToUpdate = fetchFoodListsToUpdate.getUpdateSectionFoodLists();

        modelForStock = createCustomModelForStock(foodArrayListsToUpdate);
        tableStock = new JTable(modelForStock) {
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

        tableStock.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder());
                return this;
            }
        });

        tableStock.setAutoCreateRowSorter(true);

        jTableHeader = tableStock.getTableHeader();
        jTableHeader.setBackground(COLOR_PANEL_GRAY);
        jTableHeader.setReorderingAllowed(true);
        jTableHeader.setPreferredSize(new Dimension(100, 40));
        tableStock.setTableHeader(jTableHeader);

        tableStock.setOpaque(true);
        tableStock.setBackground(COLOR_PANEL_GRAY);
        tableStock.setRowHeight(55);
        tableStock.setSelectionBackground(COLOR_SELECT_BLUE);
        tableStock.setShowHorizontalLines(false);
        tableStock.setShowVerticalLines(false);

        eventStock = new TableActionEvent() {
            @Override
            public void onEdit(int row) {
                String cellValue = String.valueOf(tableStock.getValueAt(row, 7));
                toEditFoodList(cellValue);
            }

            @Override
            public void onDelete(int row) {
                if (tableStock.isEditing()) {
                    tableStock.getCellEditor().stopCellEditing();
                }
                String cellValue = String.valueOf(tableStock.getValueAt(row, 7));
                new DeleteDialogPage().showConfirmationDialog(cellValue, stockInnerPanel);
            }


            @Override
            public void onView(int row) {
                String cellValue = String.valueOf(tableStock.getValueAt(row, 7));
                toViewFoodList(cellValue);
            }

        };


        setupTableRenderersAndEditors();

        // Initialize the scroll pane and add the table to it
        jScrollPane1 = new JScrollPane(tableStock);
        jScrollPane1.setBackground(Color.WHITE);
        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getViewport().setBackground(Color.WHITE);
        jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.getVerticalScrollBar().setUI(new MacOSScrollPane.MacOSScrollBarUI(COLOR_PANEL_GRAY));

        // Add the scroll pane to the rounded panel
        tableRoundedPanel.add(jScrollPane1, BorderLayout.CENTER);

        return tableRoundedPanel;
    }

    protected void toEditFoodList(String foodCode) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        DecimalFormat percentFormat = new DecimalFormat("#");

        String lowerCaseQuery = foodCode.toLowerCase();
        List<UpdateSectionFoodLists> viewFood = new ArrayList<>();
        for (UpdateSectionFoodLists food : foodArrayListsToUpdate) {
            String foodSerialLower = food.getFoodSerialNumber().toLowerCase();
            if (foodSerialLower.contains(lowerCaseQuery)) {
                viewFood.add(food);
            }
        }

        if (viewFood.isEmpty()) {

            return;
        }

        UpdateSectionFoodLists selectedFood = viewFood.get(0); // Use get(0) instead of getFirst()

        byte[] viewImage = selectedFood.getFoodPhoto();
        String barcodeText = selectedFood.getFoodSerialNumber();
        String itemName = selectedFood.getFoodName();
        String itemCategory = selectedFood.getFoodCategory();

        // Format prices and stock count with null checks
        String formattedSellPrice = formatNumber(selectedFood.getFoodPrice(), decimalFormat);
        String formattedBuyPrice = formatNumber(selectedFood.getFoodOriginalPrice(), decimalFormat);
        String formattedStockCnt = formatNumber(selectedFood.getStockAvailableCnt(), decimalFormat);

        String counterName = selectedFood.getCounterName();

        // Format promotion percentage with a check
        double promotionPercentage = selectedFood.getPromotionPercentage();
        String formattedPromotionPercent = percentFormat.format(promotionPercentage * 100) + "%";

        String textAreaText = selectedFood.getFoodDesc();
        new EditDialogPage().showEditPanel(viewImage, barcodeText, itemName, itemCategory, formattedSellPrice, formattedBuyPrice, formattedStockCnt, counterName, formattedPromotionPercent, textAreaText);
    }

    protected void toViewFoodList(String foodCode) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        DecimalFormat percentFormat = new DecimalFormat("#");

        String lowerCaseQuery = foodCode.toLowerCase();
        List<UpdateSectionFoodLists> viewFood = new ArrayList<>();
        for (UpdateSectionFoodLists food : foodArrayListsToUpdate) {
            String foodSerialLower = food.getFoodSerialNumber().toLowerCase();
            if (foodSerialLower.contains(lowerCaseQuery)) {
                viewFood.add(food);
            }
        }

        if (viewFood.isEmpty()) {

            return;
        }

        UpdateSectionFoodLists selectedFood = viewFood.get(0); // Use get(0) instead of getFirst()

        byte[] viewImage = selectedFood.getFoodPhoto();
        String barcodeText = selectedFood.getFoodSerialNumber();
        String itemName = selectedFood.getFoodName();
        String itemCategory = selectedFood.getFoodCategory();

        // Format prices and stock count with null checks
        String formattedSellPrice = formatNumber(selectedFood.getFoodPrice(), decimalFormat);
        String formattedBuyPrice = formatNumber(selectedFood.getFoodOriginalPrice(), decimalFormat);
        String formattedStockCnt = formatNumber(selectedFood.getStockAvailableCnt(), decimalFormat);

        String counterName = selectedFood.getCounterName();

        // Format promotion percentage with a check
        double promotionPercentage = selectedFood.getPromotionPercentage();
        String formattedPromotionPercent = percentFormat.format(promotionPercentage * 100) + " %";

        String textAreaText = selectedFood.getFoodDesc();
        new ViewDialogPage().showViewPanel(viewImage, barcodeText, itemName, itemCategory, formattedSellPrice, formattedBuyPrice, formattedStockCnt, counterName, formattedPromotionPercent, textAreaText);
    }

    private String formatNumber(Number number, DecimalFormat decimalFormat) {
        if (number != null) {
            return decimalFormat.format(number);
        }
        return "0"; // or handle as you prefer
    }


    private IconTextButton createIconButtonForStock(String buttonName, String iconPath, int iconBaseLineSize,
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


    public void exportFoodListToExcel(List<UpdateSectionFoodLists> foodList, String filePath) {
        WritableWorkbook workbook = null;
        try {
            // Create a writable workbook at the given path
            workbook = Workbook.createWorkbook(new File(filePath));

            // Create a new sheet in the workbook
            WritableSheet sheet = workbook.createSheet("Stock Data", 0);

            // Write column headers
            String[] columnNames = {"Stock_Main_Counter_ID", "Counter_Name", "Stock_Category", "Stock_Serial_Number",
                    "Stock_Name", "Stock_Price", "Stock_Original_Price", "Tax_Percentage", "Promotion_Percentage", "Stock_Desc",
                    "Stock_Count_YN", "Stock_Available_Cnt", "MainPrinterPortName", "MainPrinterPortAddress", "CreatedDate"};
            for (int col = 0; col < columnNames.length; col++) {
                Label label = new Label(col, 0, columnNames[col]);
                sheet.addCell(label);
            }
            // Write the data rows
            for (int row = 0; row < foodList.size(); row++) {
                UpdateSectionFoodLists foodItem = foodList.get(row);
                sheet.addCell(new Label(0, row + 1, String.valueOf(foodItem.getFoodMainCounterID())));
                sheet.addCell(new Label(1, row + 1, foodItem.getCounterName()));
                sheet.addCell(new Label(2, row + 1, foodItem.getFoodCategory()));
                sheet.addCell(new Label(3, row + 1, foodItem.getFoodSerialNumber()));
                sheet.addCell(new Label(4, row + 1, foodItem.getFoodName()));
                sheet.addCell(new Label(5, row + 1, String.valueOf(foodItem.getFoodPrice())));
                sheet.addCell(new Label(6, row + 1, String.valueOf(foodItem.getFoodOriginalPrice())));
                sheet.addCell(new Label(7, row + 1, String.valueOf(foodItem.getTaxPercentage())));
                sheet.addCell(new Label(8, row + 1, String.valueOf(foodItem.getPromotionPercentage())));
                sheet.addCell(new Label(9, row + 1, foodItem.getFoodDesc()));
                sheet.addCell(new Label(10, row + 1, String.valueOf(foodItem.getStockCountYN())));
                sheet.addCell(new Label(11, row + 1, String.valueOf(foodItem.getStockAvailableCnt())));
                sheet.addCell(new Label(12, row + 1, foodItem.getMainPrinterPortName()));
                sheet.addCell(new Label(13, row + 1, foodItem.getMainPrinterPortAddress()));
                sheet.addCell(new Label(14, row + 1, String.valueOf(foodItem.getCreatedDate())));
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
