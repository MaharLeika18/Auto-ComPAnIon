package org.MiniDev.Backoffice.StockManagement.NewEntryPage;

import SqlLoadersAndUpdater.*;
import UI.*;
import Utils.DrawTextOnBarcodeFactory;
import Utils.NumericDocumentFilter;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.MiniDev.Backoffice.StockManagement.StockInnerPage;
import org.MiniDev.Backoffice.StockManagement.StockManagementPage;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import static UI.UserFinalSettingsVar.*;
import static org.MiniDev.Cashier.CreateTodaySalePanel.GAP;

public class NewItemEntryPage {

    protected static JLabel barcodeLabel = new JLabel();
    protected static Set<String> generatedWords;
    protected static Random rand;
    protected static String text; //Barcode Text
    protected static BufferedImage barcodeImage;

    private final RoundedTextFieldV2 textFieldName = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    private final RoundedTextFieldV2 textFieldSellPrice = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    private final RoundedTextFieldV2 textFieldBuyPrice = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    private final RoundedTextFieldV2 textFieldStockCnt = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    private final RoundedTextFieldV2 textFieldDescription = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    private static final RoundedTextFieldV2 textFieldCustomBarCode = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    private final RoundedComboBox<String> comboBoxCounter = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);
    private final RoundedComboBox<String> comboBoxCategory = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);
    private final RoundedComboBox<String> comboBoxPromotionPercentage = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);

    RoundedPanel newItemEntryPanel;
    JLabel headerLabel;
    EditableFoodImage avatar = new EditableFoodImage();

    public NewItemEntryPage() {
        generatedWords = new HashSet<>();
        rand = new Random();
        GetExistingItemSrNo.fetchExistingSerialNumbers();
        generateUniqueWords();
        generateBarcode();
    }

    public RoundedPanel createNewItemEntryPagePanel() {
        newItemEntryPanel = new RoundedPanel(10);
        newItemEntryPanel.setBackground(COLOR_WHITE);
        newItemEntryPanel.setOpaque(false);
        newItemEntryPanel.setLayout(new BorderLayout());

        headerLabel = new JLabel("Item Register", SwingConstants.LEFT);
        headerLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 20));

        JPanel centerPanel = createCenterPanel();
        centerPanel.setOpaque(false);

        JPanel bottomPanel = createBottomPanel();
        bottomPanel.setOpaque(false);

        // Create a new panel for the center content
        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        centerContainer.setOpaque(false);

        // Create a gap panel
        JPanel gapPanel = new JPanel();
        gapPanel.setPreferredSize(new Dimension(0, 7)); // Set the gap height
        gapPanel.setOpaque(false);

        // Add components to the center container
        centerContainer.add(centerPanel);
        centerContainer.add(gapPanel);

        // Add to the main panel
        newItemEntryPanel.add(centerContainer, BorderLayout.CENTER);
        newItemEntryPanel.add(bottomPanel, BorderLayout.SOUTH);

        return newItemEntryPanel;
    }


    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(100, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, GAP, 0, GAP));
        headerPanel.setOpaque(false);
        avatar.setPreferredSize(new Dimension(70, 70));
        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(avatar, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());

        centerPanel.add(createPreviewGenerateBarcode(), BorderLayout.EAST);
        centerPanel.add(createTextFieldsHolding(), BorderLayout.CENTER);

        return centerPanel;
    }


    private void generateUniqueWords() {
        while (generatedWords.size() < 20) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                if (rand.nextBoolean()) {
                    sb.append((char) ('A' + rand.nextInt(26))); // Add a letter
                } else {
                    sb.append(rand.nextInt(10)); // Add a digit
                }
            }
            String generatedWord = sb.toString();

            // Check if the generated word is unique
            if (!GetExistingItemSrNo.existingSerialNumbers.contains(generatedWord) && !generatedWords.contains(generatedWord)) {
                generatedWords.add(generatedWord);
            }
        }
    }

    private void generateBarcode() {
        generatedWords = new HashSet<>();
        rand = new Random();
        GetExistingItemSrNo.fetchExistingSerialNumbers(); // Fetch existing serial numbers
        generateUniqueWords(); // Generate unique words in the constructor

        text = getRandomWord(); // Get a random word

        if (text != null) {
            try {
                // Generate barcode
                BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.CODE_128, 300, 120);
                barcodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

                // Draw text on barcode using high-resolution rendering
                BufferedImage finalBarcodeWithText = DrawTextOnBarcodeFactory.drawTextOnBarcodeStyle1(barcodeImage, text);
                liveChangesForBarcodeTextField();

                // Set barcode image with text to the label
                barcodeLabel.setIcon(new ImageIcon(finalBarcodeWithText));
                barcodeLabel.repaint();
            } catch (WriterException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error generating barcode: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "No words generated.");
        }
    }


    private String getRandomWord() {
        if (generatedWords.isEmpty()) return null;
        int index = rand.nextInt(generatedWords.size());
        return generatedWords.toArray(new String[0])[index]; // Get a random word
    }

    private JPanel createPreviewGenerateBarcode() {
        JPanel containerPanel = new JPanel();
        containerPanel.setOpaque(false);
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.X_AXIS));

        JPanel gapPanel = new JPanel();
        gapPanel.setOpaque(false);
        gapPanel.setPreferredSize(new Dimension(7, newItemEntryPanel.getHeight()));

        RoundedPanel centerRight = new RoundedPanel(7);
        centerRight.setLayout(new BorderLayout());
        centerRight.setPreferredSize(new Dimension(320, 100));

        centerRight.setBorder(BorderFactory.createEmptyBorder(40, 10, 0, 10));

        centerRight.setBackground(COLOR_WHITE);
        centerRight.setOpaque(false);
        JLabel previewLabel = new JLabel("Preview", SwingConstants.CENTER);
        previewLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 16));

        JPanel barcodePanel = new JPanel(new BorderLayout());
        barcodePanel.setPreferredSize(new Dimension(centerRight.getWidth(), 300));
        barcodePanel.setOpaque(false);

        JPanel barcodeInnerBottomPanel = new JPanel(new GridLayout(2, 1, GAP, GAP));
        barcodeInnerBottomPanel.setPreferredSize(new Dimension(centerRight.getWidth(), 100));
        barcodeInnerBottomPanel.setOpaque(false);

        JPanel innerTop = new JPanel(new BorderLayout());
        innerTop.setOpaque(false);
        innerTop.setPreferredSize(new Dimension(centerRight.getWidth(), 50));

        JPanel innerBot = new JPanel();
        innerBot.setOpaque(false);
        innerBot.setLayout(new GridLayout(1, 2, GAP, GAP));
        innerBot.setPreferredSize(new Dimension(centerRight.getWidth(), 50));

        barcodeLabel.setPreferredSize(new Dimension(300, 150)); // Set a preferred size for the barcode label

        textFieldCustomBarCode.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Custom BarCode No.");
        textFieldCustomBarCode.setHorizontalAlignment(SwingConstants.LEFT);

        innerTop.add(textFieldCustomBarCode, BorderLayout.CENTER);

        innerBot.add(barcodePrintButton());
        innerBot.add(regenerateBarcodeButton());

        barcodeInnerBottomPanel.add(innerTop);
        barcodeInnerBottomPanel.add(innerBot);

        barcodePanel.add(previewLabel, BorderLayout.NORTH);
        barcodePanel.add(barcodeLabel, BorderLayout.CENTER);
        barcodePanel.add(barcodeInnerBottomPanel, BorderLayout.SOUTH);

        centerRight.add(barcodePanel, BorderLayout.NORTH);

        containerPanel.add(gapPanel);
        containerPanel.add(centerRight);

        return containerPanel;
    }

    private RoundedPanel createBottomPanel() {
        RoundedPanel bottomPanel = new RoundedPanel(7);
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setPreferredSize(new Dimension(100, 65));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        bottomPanel.setBackground(COLOR_WHITE);
        bottomPanel.setOpaque(false);

        // Add save button or other components if needed
        JPanel leftInnerBottom = new JPanel(new GridLayout(1, 3, GAP, 0));
        leftInnerBottom.setPreferredSize(new Dimension(300, 45));
        leftInnerBottom.setOpaque(false);


        leftInnerBottom.add(saveItemButton());
        leftInnerBottom.add(closeItemEntryButton());

        bottomPanel.add(leftInnerBottom, BorderLayout.WEST);
        return bottomPanel;
    }

    private RoundedPanel createTextFieldsHolding() {
        RoundedPanel centerLeftMain = new RoundedPanel(7);
        centerLeftMain.setLayout(new BorderLayout());
        centerLeftMain.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        centerLeftMain.setBackground(COLOR_WHITE);
        centerLeftMain.setOpaque(false);

        JPanel centerLeft = new JPanel(new GridLayout(8, 1, GAP, GAP));
        centerLeft.setPreferredSize(new Dimension(400, newItemEntryPanel.getHeight()));
        centerLeft.setOpaque(false);
        initializeTextFields();
        initializeComboBoxes();

        centerLeft.add(textFieldName);
        centerLeft.add(comboBoxCategory);
        centerLeft.add(textFieldSellPrice);
        centerLeft.add(textFieldBuyPrice);
        centerLeft.add(textFieldStockCnt);
        centerLeft.add(comboBoxCounter);
        centerLeft.add(comboBoxPromotionPercentage);
        centerLeft.add(textFieldDescription);

        centerLeftMain.add(createHeaderPanel(), BorderLayout.NORTH);
        centerLeftMain.add(centerLeft, BorderLayout.CENTER);

        return centerLeftMain;
    }

    private void initializeTextFields() {
        textFieldName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Item Name");
        textFieldSellPrice.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Sell Price");
        textFieldBuyPrice.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Buy Price");
        textFieldStockCnt.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Stock Available");
        textFieldDescription.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Description");

        textFieldName.setHorizontalAlignment(SwingConstants.LEFT);
        textFieldSellPrice.setHorizontalAlignment(SwingConstants.LEFT);
        textFieldBuyPrice.setHorizontalAlignment(SwingConstants.LEFT);
        textFieldStockCnt.setHorizontalAlignment(SwingConstants.LEFT);
        textFieldDescription.setHorizontalAlignment(SwingConstants.LEFT);

        // Adding KeyListeners to restrict input to numbers
        addNumericKeyListener(textFieldSellPrice);
        addNumericKeyListener(textFieldBuyPrice);
        addNumericKeyListener(textFieldStockCnt);

        // Attach the DocumentFilter
        ((AbstractDocument) textFieldSellPrice.getDocument()).setDocumentFilter(new NumericDocumentFilter());
        ((AbstractDocument) textFieldBuyPrice.getDocument()).setDocumentFilter(new NumericDocumentFilter());
        ((AbstractDocument) textFieldStockCnt.getDocument()).setDocumentFilter(new NumericDocumentFilter());
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

    private void initializeComboBoxes() {
        FetchCounterLists fetchCounterLists = new FetchCounterLists();
        List<String> counterNames = fetchCounterLists.getCounterLists();

        FetchFoodCatLists fetchFoodCatLists = new FetchFoodCatLists();
        List<String> foodCategories = fetchFoodCatLists.getFoodCatNames();

        FetchPromoCentLists fetchPromoCentLists = new FetchPromoCentLists();
        List<String> promoCentLists = fetchPromoCentLists.getPromoCentLists();

        // Make combo boxes editable
        comboBoxCategory.setEditable(true);
        comboBoxCounter.setEditable(true);
        comboBoxPromotionPercentage.setEditable(true);

        // Initialize combo boxes
        initializeComboBox(comboBoxCategory, foodCategories, "Select Category");
        initializeComboBox(comboBoxCounter, counterNames, "Select Counter");
        initializeComboBox(comboBoxPromotionPercentage, promoCentLists, "Select Promotion Percentage");

        // Add key listeners for handling key events
        addKeyListenerToComboBox(comboBoxCounter);
        addKeyListenerToComboBox(comboBoxCategory);
        addKeyListenerToComboBox(comboBoxPromotionPercentage);
    }

    private void addKeyListenerToComboBox(RoundedComboBox<String> comboBox) {
        comboBox.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleComboBoxKeyPress(e, comboBox);
            }
        });
    }

    private void handleComboBoxKeyPress(KeyEvent e, RoundedComboBox<String> comboBox) {
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            initializeComboBox(comboBox, new FetchCounterLists().getCounterLists(),
                    comboBox == comboBoxCounter ? "Select Counter" :
                            comboBox == comboBoxCategory ? "Select Category" : "Select Promotion Percentage");
        }
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

    private void barCodeResetSection() {
        generateBarcode();
    }


    private IconTextButton saveItemButton() {
        IconTextButton saveButton = UIAppButtonFactory.createIconTextButton("Update", "/UpdateIcon.svg", COLOR_GREEN);
        saveButton.addActionListener(e -> {
            if (processItemsIntoDatabase()) {
                SwingUtilities.invokeLater(this::resetInputSection);
                new StockInnerPage().refreshTableAfterItemsUpdater();
            }
        });
        return saveButton;
    }

    private IconTextButton barcodePrintButton() {
        IconTextButton printButton = UIAppButtonFactory.createIconTextButton("Print", "/PrintIcon.svg", COLOR_BLUE);
        printButton.addActionListener(e -> {

        });
        return printButton;
    }


    private RoundedBorderButton closeItemEntryButton() {
        RoundedBorderButton closeButton = UIAppButtonFactory.creatRoundedBorderButton("Close", "/CloseIcon.svg", COLOR_ORANGE, 20);
        closeButton.addActionListener(e -> {
            StockManagementPage.stockMenuCardLayout.show(StockManagementPage.mainMenuCardHoldingPanel, "StockInnerPage");
            barCodeResetSection();
            resetInputSection();
        });
        return closeButton;
    }


    private RoundedBorderButton regenerateBarcodeButton() {
        RoundedBorderButton regenButton = UIAppButtonFactory.creatRoundedBorderButton("Regenerate", "/Regen.svg", COLOR_ORANGE, 20);
        regenButton.addActionListener(e -> {
            generateBarcode();
        });
        return regenButton;
    }


    private void resetInputSection() {
        SwingUtilities.invokeLater(() -> {
            clearAllTextFields();
            initializeTextFields();
            initializeComboBoxes();
            generateBarcode();
        });
    }

    private void clearAllTextFields() {
        SwingUtilities.invokeLater(() -> {
            textFieldName.setText("");
            textFieldSellPrice.setText("");
            textFieldBuyPrice.setText("");
            textFieldDescription.setText("");
            textFieldStockCnt.setText("");
            avatar.removeImage();
        });
    }

    private void liveChangesForBarcodeTextField() {
        textFieldCustomBarCode.setText(text);
    }


    private boolean processItemsIntoDatabase() {
        try {
            String serialNumber = text;
            String itemCat = (String) comboBoxCategory.getSelectedItem();
            String itemName = textFieldName.getText();

            int sellPrice = Integer.parseInt(textFieldSellPrice.getText().replace(",", ""));
            int buyPrice = Integer.parseInt(textFieldBuyPrice.getText().replace(",", ""));
            int stockCnt = Integer.parseInt(textFieldStockCnt.getText().replace(",", ""));
            String counterName = (String) comboBoxCounter.getSelectedItem();
            String description = textFieldDescription.getText();

            String promoCetString = (String) comboBoxPromotionPercentage.getSelectedItem();
            double promoCet = (promoCetString != null && promoCetString.endsWith("%")) ?
                    Double.parseDouble(promoCetString.replace("%", "")) / 100.0 : 0.0;

            if (itemName.isEmpty() || Objects.equals(counterName, "Select Counter") || Objects.equals(itemCat, "Select Category")
                    || sellPrice == 0 || buyPrice == 0 || stockCnt == 0) {
                JOptionPane.showMessageDialog(null, "Please fill all the fields", "Error", JOptionPane.ERROR_MESSAGE);
                return false; // Indicate failure
            } else {
                // Here you can log or check if the counter ID is valid
                UpdateItemIntoDB.itemUpdaterIntoDB(
                        avatar.getImageBytes(),
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
