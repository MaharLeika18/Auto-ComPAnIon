package org.MiniDev.Backoffice.StockManagement.StockTableActionPages;

import SqlLoadersAndUpdater.FetchCounterLists;
import SqlLoadersAndUpdater.FetchFoodCatLists;
import SqlLoadersAndUpdater.FetchPromoCentLists;
import SqlLoadersAndUpdater.UpdateItemIntoDB;
import UI.*;
import Utils.NumericDocumentFilter;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.Backoffice.StockManagement.StockInnerPage;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Objects;

import static UI.RoundedImagePanel.DEFAULT_IMAGE_BYTES;
import static UI.UserFinalSettingsVar.*;
import static UI.UserFinalSettingsVar.COLOR_WHITE;
import static org.MiniDev.Cashier.CreateTodaySalePanel.GAP;

public class EditDialogPage {

    protected JDialog editDialog;
    protected RoundedTextFieldV2 editItemNameField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 editSellPriceField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 editBuyPriceField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 editStockCntField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 editItemSerialField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextArea editDescriptionField;
    protected RoundedComboBox<String> comboBoxCounterEdit = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);
    protected RoundedComboBox<String> comboBoxCategoryEdit = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);
    protected RoundedComboBox<String> comboBoxPromotionPercentEdit = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);
    protected static EditableFoodImage itemAvatar;

    public EditDialogPage() {
    }

    public void showEditPanel(byte[] foodImage, String barcodeText, String itemName, String categoryName,
                              String sellPriceText, String buyPriceText, String stockCnt, String counterName, String promoCentText, String textAreaText) {
        editDialog = new JDialog();
        editDialog.setUndecorated(true); // Remove window decorations for custom rounding
        editDialog.setBackground(new Color(0, 0, 0, 0));
        editDialog.setSize(500, 680);
        editDialog.setModal(true); // Make the dialog modal
        editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        RoundedOptionPane.setRoundedCorners(editDialog, 0);
        editDialog.setLayout(new BorderLayout());

        // Add the panel to the dialog
        editDialog.getContentPane().add(getEditHoldingPanel(foodImage, barcodeText, itemName, categoryName, sellPriceText, buyPriceText, stockCnt, counterName, promoCentText, textAreaText));
        editDialog.pack(); // Resize the dialog to fit the content
        editDialog.setLocationRelativeTo(null); // Center the dialog on the screen

        // Set the shape for rounded corners
        editDialog.setShape(new RoundRectangle2D.Double(0, 0, editDialog.getWidth(), editDialog.getHeight(), 0, 0));

        // Add fade-in animation
        DialogFadeIn.fadeIn(editDialog);

        editDialog.setVisible(true); // Show the dialog
    }


    protected RoundedPanel getEditHoldingPanel(byte[] foodImage, String barcodeText, String itemName, String categoryName, String sellPriceText, String buyPriceText, String stockCnt, String counterName, String promoCentText, String textAreaText) {
        // Create the rounded panel with a custom rounded border
        RoundedPanel editPanel = getEditHoldingPane();

        editPanel.add(getHeaderPanel(foodImage, barcodeText), BorderLayout.NORTH);
        editPanel.add(getContentPanel(itemName, categoryName, sellPriceText, buyPriceText, stockCnt, counterName, promoCentText, textAreaText), BorderLayout.CENTER);
        editPanel.add(getBottomPanel(), BorderLayout.SOUTH);

        return editPanel;
    }

    private RoundedPanel getEditHoldingPane() {
        RoundedPanel editPanel = new RoundedPanel(20);
        editPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the content
        editPanel.setPreferredSize(new Dimension(editDialog.getWidth(), editDialog.getHeight()));
        editPanel.setBackground(COLOR_ENTRY_GRAY);
        editPanel.setLayout(new BorderLayout());
        editPanel.setBorderWidth(1);
        editPanel.setBorderColor(COLOR_PANEL_BORDER_SILVER);
        return editPanel;
    }

    protected RoundedPanel getHeaderPanel(byte[] foodImage, String barcodeText) {
        RoundedPanel headerPanel = new RoundedPanel(10);
        headerPanel.setLayout(new GridLayout(1, 3, 0, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        headerPanel.setPreferredSize(new Dimension(editDialog.getWidth(), 75));
        headerPanel.setOpaque(false);
        headerPanel.setBackground(COLOR_GRAY);

        JPanel left = new JPanel();
        left.setLayout(new BorderLayout());
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(165, 75));

        JPanel mid = new JPanel();
        mid.setLayout(new BorderLayout());
        mid.setOpaque(false);

        JPanel right = new JPanel();
        right.setLayout(new BorderLayout());
        right.setOpaque(false);
        right.setBorder(BorderFactory.createEmptyBorder(GAP, 0, GAP, 0));
        right.setPreferredSize(new Dimension(165, 75));

        mid.add(initializeEditableFoodImage(foodImage), BorderLayout.CENTER);
        right.add(initializedItemSerialField(editItemSerialField, barcodeText), BorderLayout.EAST);

        headerPanel.add(left);
        headerPanel.add(mid);
        headerPanel.add(right);

        return headerPanel;
    }

    protected RoundedTextFieldV2 initializedItemSerialField(RoundedTextFieldV2 itemSerialField, String initializedSerialText) {
        itemSerialField.setText(initializedSerialText);
        itemSerialField.setHorizontalAlignment(SwingConstants.LEFT);
        itemSerialField.setEditable(true);
        itemSerialField.setBackground(COLOR_WHITE);

        // Set a DocumentFilter to limit input to 8 characters
        ((AbstractDocument) itemSerialField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string != null && (fb.getDocument().getLength() + string.length() <= 8)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text != null && (fb.getDocument().getLength() - length + text.length() <= 8)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                super.remove(fb, offset, length);
            }
        });

        return itemSerialField;
    }


    protected JPanel getContentPanel(String itemName, String categoryName, String sellPriceText, String buyPriceText, String stockCnt, String counterName, String promoCentText, String textAreaText) {
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout());

        JPanel upperGap = new JPanel();
        upperGap.setOpaque(false);
        upperGap.setPreferredSize(new Dimension(editDialog.getWidth(), 10));

        RoundedPanel mainCenterPanel = new RoundedPanel(10);
        mainCenterPanel.setLayout(new BorderLayout());
        mainCenterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 12, 10));
        mainCenterPanel.setOpaque(false);
        mainCenterPanel.setBackground(COLOR_GRAY);
        mainCenterPanel.add(mainInfoHoldingPanel(itemName, categoryName, sellPriceText, buyPriceText, stockCnt, counterName, promoCentText, textAreaText), BorderLayout.CENTER);

        JPanel bottomGap = new JPanel();
        bottomGap.setOpaque(false);
        bottomGap.setPreferredSize(new Dimension(editDialog.getWidth(), 10));

        contentPanel.add(upperGap, BorderLayout.NORTH);
        contentPanel.add(mainCenterPanel, BorderLayout.CENTER);
        contentPanel.add(bottomGap, BorderLayout.SOUTH);

        return contentPanel;
    }

    protected JPanel mainInfoHoldingPanel(String itemName, String categoryName, String sellPriceText, String buyPriceText, String stockCnt, String counterName, String promoCentText, String textAreaText) {
        JPanel mainInfoPanel = new JPanel();
        mainInfoPanel.setOpaque(false);
        mainInfoPanel.setLayout(new BorderLayout());
        mainInfoPanel.add(getUpperHoldingPanel(itemName, categoryName, sellPriceText, buyPriceText, stockCnt, counterName, promoCentText), BorderLayout.CENTER);
        mainInfoPanel.add(getLowerHoldingPanel(textAreaText), BorderLayout.SOUTH);

        return mainInfoPanel;
    }

    protected JPanel getUpperHoldingPanel(String itemName, String categoryName, String sellPriceText, String buyPriceText, String stockCnt, String counterName, String promoCentText) {
        JPanel upperHoldingPanel = new JPanel(new GridLayout(7, 1, GAP, GAP));
        upperHoldingPanel.setOpaque(false);

        initializeComboBoxes(categoryName, counterName, promoCentText);
        initializeTextValues(itemName, sellPriceText, buyPriceText, stockCnt);

        upperHoldingPanel.add(createRowTextFields("Item Name:", editItemNameField, false));
        upperHoldingPanel.add(createComboSelectionFields("Item Category:", comboBoxCategoryEdit));
        upperHoldingPanel.add(createRowTextFields("Sell Price:", editSellPriceField, true));
        upperHoldingPanel.add(createRowTextFields("Original Price:", editBuyPriceField, true));
        upperHoldingPanel.add(createRowTextFields("Stock Available:", editStockCntField, true));
        upperHoldingPanel.add(createComboSelectionFields("Counter Name:", comboBoxCounterEdit));
        upperHoldingPanel.add(createComboSelectionFields("Promotion Percentage:", comboBoxPromotionPercentEdit));

        return upperHoldingPanel;
    }

    private void initializeComboBoxes(String categoryName, String counterName, String promoCentText) {
        FetchCounterLists fetchCounterLists = new FetchCounterLists();
        java.util.List<String> counterNames = fetchCounterLists.getCounterLists();

        FetchFoodCatLists fetchFoodCatLists = new FetchFoodCatLists();
        java.util.List<String> foodCategories = fetchFoodCatLists.getFoodCatNames();

        FetchPromoCentLists fetchPromoCentLists = new FetchPromoCentLists();
        List<String> promoCentLists = fetchPromoCentLists.getPromoCentLists();

        // Make combo boxes editable
        comboBoxCategoryEdit.setEditable(true);
        comboBoxCounterEdit.setEditable(true);
        comboBoxPromotionPercentEdit.setEditable(true);

        // Initialize combo boxes
        initializeComboBox(comboBoxCategoryEdit, foodCategories, categoryName);
        initializeComboBox(comboBoxCounterEdit, counterNames, counterName);
        initializeComboBox(comboBoxPromotionPercentEdit, promoCentLists, promoCentText);
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

    protected JPanel createComboSelectionFields(String headerName, RoundedComboBox<String> comboBox) {
        JPanel comboPanel = new JPanel(new BorderLayout());
        comboPanel.setOpaque(false);

        JPanel innerLeft = new JPanel(new BorderLayout());
        JPanel innerRight = new JPanel(new BorderLayout());

        innerLeft.setOpaque(false);
        innerRight.setOpaque(false);

        innerLeft.setPreferredSize(new Dimension(140, comboPanel.getHeight()));

        JLabel headerLabel = new JLabel(headerName);
        headerLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13));

        innerLeft.add(headerLabel, BorderLayout.WEST);
        innerRight.add(comboBox, BorderLayout.CENTER);

        comboPanel.add(innerLeft, BorderLayout.WEST);
        comboPanel.add(innerRight, BorderLayout.CENTER);
        return comboPanel;
    }

    protected JPanel createRowTextFields(String headerName, RoundedTextFieldV2 textField, boolean isNumericField) {
        JPanel textFieldPanel = new JPanel(new BorderLayout());
        textFieldPanel.setOpaque(false);

        JPanel innerLeft = new JPanel(new BorderLayout());
        JPanel innerRight = new JPanel(new BorderLayout());

        innerLeft.setOpaque(false);
        innerRight.setOpaque(false);

        innerLeft.setPreferredSize(new Dimension(140, textFieldPanel.getHeight()));

        JLabel headerLabel = new JLabel(headerName);
        headerLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13));

        textField.setHorizontalAlignment(SwingConstants.LEFT);
        textField.setEditable(true);
        textField.setBackground(COLOR_WHITE);

        if (isNumericField) {
            addNumericKeyListener(textField);
            ((AbstractDocument) textField.getDocument()).setDocumentFilter(new NumericDocumentFilter());
        }

        innerLeft.add(headerLabel, BorderLayout.WEST);
        innerRight.add(textField, BorderLayout.CENTER);

        textFieldPanel.add(innerLeft, BorderLayout.WEST);
        textFieldPanel.add(innerRight, BorderLayout.CENTER);
        return textFieldPanel;
    }

    private void addNumericKeyListener(RoundedTextFieldV2 textField) {
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Allow digits and a decimal point (if needed)
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != '.') {
                    e.consume(); // Ignore the event
                }
            }
        });
    }


    protected JPanel getLowerHoldingPanel(String textAreaText) {
        JPanel lowerHoldingPanel = new JPanel(new BorderLayout());
        lowerHoldingPanel.setOpaque(false);
        lowerHoldingPanel.setBorder(BorderFactory.createEmptyBorder(GAP, 0, 0, 0));
        lowerHoldingPanel.setPreferredSize(new Dimension(editDialog.getWidth(), 120));

        JPanel leftLower = new JPanel(new BorderLayout());
        JPanel rightLower = new JPanel(new BorderLayout());

        leftLower.setOpaque(false);
        rightLower.setOpaque(false);

        leftLower.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        leftLower.setPreferredSize(new Dimension(140, lowerHoldingPanel.getHeight()));

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13));

        rightLower.add(createDescriptionField(), BorderLayout.CENTER);
        leftLower.add(descriptionLabel, BorderLayout.NORTH);

        lowerHoldingPanel.add(leftLower, BorderLayout.WEST);
        lowerHoldingPanel.add(rightLower, BorderLayout.CENTER);

        initializeTextAreaValues(textAreaText);

        return lowerHoldingPanel;
    }

    private void initializeTextValues(String itemName, String sellPriceText, String buyPriceText, String stockCntText) {
        editItemNameField.setText(itemName);
        editSellPriceField.setText(sellPriceText);
        editBuyPriceField.setText(buyPriceText);
        editStockCntField.setText(stockCntText);
    }

    private void initializeTextAreaValues(String textAreaText) {
        editDescriptionField.setText(textAreaText);
    }

    protected RoundedTextArea createDescriptionField() {
        int borderRadius = 10;

        editDescriptionField = new RoundedTextArea(borderRadius, COLOR_LINE_COLOR, 200);
        editDescriptionField.setBackground(COLOR_WHITE);
        editDescriptionField.setEditable(true);
        editDescriptionField.setRows(4);


        // Optionally set line wrap and word wrap
        editDescriptionField.setLineWrap(true);
        editDescriptionField.setWrapStyleWord(true);

        // Optionally set padding within the text area
        editDescriptionField.setMargin(new Insets(0, 0, 0, 0));

        return editDescriptionField;
    }

    private EditableFoodImage initializeEditableFoodImage(byte[] foodImage) {
        itemAvatar = new EditableFoodImage();
        itemAvatar.setBackground(COLOR_GRAY);
        itemAvatar.setOpaque(false);
        itemAvatar.setBorderSize(0);
        itemAvatar.setArcSize(10);
        itemAvatar.setFillRect(true);
        itemAvatar.setAutoResizing(false); // Ensure the image is not resized automatically

        ImageIcon foodImageIcon = new ImageIcon(foodImage);
        Image scaledImage = foodImageIcon.getImage().getScaledInstance(65, 65, Image.SCALE_SMOOTH);

        itemAvatar.setPreferredSize(new Dimension(65, 65));  // Desired size
        itemAvatar.setMaximumSize(new Dimension(65, 65));
        itemAvatar.setImage(new ImageIcon(scaledImage));

        return itemAvatar;
    }

    protected RoundedPanel getBottomPanel() {
        RoundedPanel bottomPanel = new RoundedPanel(10);
        bottomPanel.setPreferredSize(new Dimension(editDialog.getWidth(), 60));
        bottomPanel.setBackground(COLOR_GRAY);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, GAP));
        bottomPanel.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, GAP, 0));
        buttonPanel.setPreferredSize(new Dimension(215, 45));
        buttonPanel.setOpaque(false);
        buttonPanel.add(updateButtonEditPage());
        buttonPanel.add(closeButtonEditPage());

        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        return bottomPanel;
    }


    private RoundedBorderButton closeButtonEditPage() {
        RoundedBorderButton closeButton = UIAppButtonFactory.creatRoundedBorderButton("Close", "/CloseIcon.svg", COLOR_ORANGE,20);
        closeButton.addActionListener(e -> {
            editDialog.dispose();
        });
        return closeButton;
    }

    private IconTextButton updateButtonEditPage() {
        IconTextButton updateButton = UIAppButtonFactory.createIconTextButton("Update", "/UpdateIcon.svg", COLOR_GREEN);
        updateButton.addActionListener(e -> {
            if (processItemsIntoDatabase()) {
                new StockInnerPage().refreshTableAfterItemsUpdater();
                editDialog.dispose();
            }
        });
        return updateButton;
    }

    public int convertToInteger(String priceString) {
        if (priceString == null || priceString.isEmpty()) {
            throw new IllegalArgumentException("Input string cannot be null or empty");
        }

        // Remove the comma
        priceString = priceString.replace(",", "");

        try {
            // Convert to double
            return Integer.parseInt(priceString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid input for price: " + priceString, e);
        }
    }


    private boolean processItemsIntoDatabase() {
        try {
            String serialNumber = editItemSerialField.getText();
            String itemCat = Objects.requireNonNull(comboBoxCategoryEdit.getSelectedItem()).toString();
            String itemName = editItemNameField.getText();

            int sellPrice = convertToInteger(editSellPriceField.getText());
            int buyPrice = convertToInteger(editBuyPriceField.getText());
            int stockCnt = convertToInteger(editStockCntField.getText());

            String counterName = Objects.requireNonNull(comboBoxCounterEdit.getSelectedItem()).toString();
            String description = editDescriptionField.getText();

            String promoCetString = Objects.requireNonNull(comboBoxPromotionPercentEdit.getSelectedItem()).toString();
            double promoCet = (promoCetString != null && promoCetString.endsWith("%")) ?
                    Double.parseDouble(promoCetString.replace("%", "")) / 100.0 : 0.0;

            // Modify the validation logic to allow null values
            if (itemName.isEmpty() || Objects.equals(counterName, "Select Counter") || Objects.equals(itemCat, "Select Category")
                    || sellPrice < 0 || buyPrice < 0 || stockCnt < 0) { // Allowing zero values for prices and stock
                JOptionPane.showMessageDialog(null, "Please fill all the fields", "Error", JOptionPane.ERROR_MESSAGE);
                return false; // Indicate failure
            } else {
                UpdateItemIntoDB.itemUpdaterIntoDB(
                        itemAvatar.getImageBytes(),
                        serialNumber,
                        itemName,
                        itemCat,
                        sellPrice,
                        buyPrice,
                        stockCnt,
                        counterName,
                        promoCet,
                        description
                );
                return true; // Indicate success
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please fill all the fields", "Error", JOptionPane.ERROR_MESSAGE);
            return false; // Indicate failure
        } catch (Exception e) {
            e.printStackTrace(); // Handle other potential exceptions
            return false; // Indicate failure
        }
    }


}
