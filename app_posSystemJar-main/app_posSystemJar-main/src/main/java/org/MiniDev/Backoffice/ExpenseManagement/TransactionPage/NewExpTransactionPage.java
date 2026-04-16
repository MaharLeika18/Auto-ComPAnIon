package org.MiniDev.Backoffice.ExpenseManagement.TransactionPage;

import SqlLoadersAndUpdater.*;
import UI.*;
import Utils.NumericDocumentFilter;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.Backoffice.ExpenseManagement.ExpenseManagementPage;
import org.MiniDev.Backoffice.ExpenseManagement.SummaryPage.SummaryPage;
import org.MiniDev.Cashier.CreateCashDrawerPanel;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import static UI.UserFinalSettingsVar.*;
import static org.MiniDev.Cashier.CreateTodaySalePanel.GAP;

public class NewExpTransactionPage {

    protected RoundedComboBox<String> comboBoxExpCategory = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 expTitleField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 expRequestAmountField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 expRefundAmountField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedComboBox<String> comboBoxAssignedTo = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);

    protected SignaturePanel signaturePanel;
    protected static RoundedPanel uploadFilePanel;
    protected static EditableAttachmentImage editableAttachmentImage;


    private RoundedPanel newExpEntryPanel;
    private JLabel headerLabel;


    public NewExpTransactionPage() {
    }

    public RoundedPanel createNewExpEntryPanel() {
        newExpEntryPanel = new RoundedPanel(10);
        newExpEntryPanel.setBackground(COLOR_WHITE);
        newExpEntryPanel.setOpaque(false);
        newExpEntryPanel.setLayout(new BorderLayout());

        headerLabel = new JLabel("New Expense Request", SwingConstants.LEFT);
        headerLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 20));

        JPanel centerPanel = createTextFieldsHolding();
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
        newExpEntryPanel.add(centerContainer, BorderLayout.CENTER);
        newExpEntryPanel.add(bottomPanel, BorderLayout.SOUTH);
        newExpEntryPanel.add(getRightInnerCenter(), BorderLayout.EAST);

        return newExpEntryPanel;
    }


    private JPanel getRightInnerCenter() {
        JPanel centerRightContainer = new JPanel();
        centerRightContainer.setOpaque(false);
        centerRightContainer.setLayout(new BorderLayout());
        centerRightContainer.setPreferredSize(new Dimension(327, 100));

        JPanel leftGap = new JPanel();
        JPanel botGap = new JPanel();

        leftGap.setOpaque(false);
        botGap.setOpaque(false);

        botGap.setPreferredSize(new Dimension(centerRightContainer.getWidth(), 7));
        leftGap.setPreferredSize(new Dimension(7, centerRightContainer.getHeight()));

        centerRightContainer.add(leftGap, BorderLayout.WEST);
        centerRightContainer.add(botGap, BorderLayout.SOUTH);
        centerRightContainer.add(getInnerRightPanel(), BorderLayout.CENTER);

        return centerRightContainer;
    }

    // Method to create the right panel containing the uploader
    private JPanel getInnerRightPanel() {
        RoundedPanel innerRightPanel = new RoundedPanel(7);
        innerRightPanel.setOpaque(false);
        innerRightPanel.setBackground(COLOR_WHITE);

        innerRightPanel.setLayout(new BorderLayout());
        innerRightPanel.add(getAttachmentPanel(), BorderLayout.CENTER);

        return innerRightPanel;
    }

    private JPanel getAttachmentPanel() {
        JPanel fileUploadMainPanel = new JPanel(new BorderLayout());
        fileUploadMainPanel.setOpaque(false);
        fileUploadMainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        fileUploadMainPanel.setPreferredSize(new Dimension(600, 180));

        editableAttachmentImage = new EditableAttachmentImage();
        fileUploadMainPanel.add(getRightCenterPanel(editableAttachmentImage), BorderLayout.CENTER);
        fileUploadMainPanel.add(getRightBottomPanel(editableAttachmentImage), BorderLayout.SOUTH);

        return fileUploadMainPanel;
    }

    protected JPanel attachmentImageHoldingPanel;

    private RoundedPanel getRightCenterPanel(EditableAttachmentImage editableAttachmentImage) {
        uploadFilePanel = new RoundedPanel(10);
        uploadFilePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        uploadFilePanel.setLayout(new BorderLayout());
        uploadFilePanel.setBackground(COLOR_WHITE);
        uploadFilePanel.setOpaque(false);


        attachmentImageHoldingPanel = new JPanel(new BorderLayout());
        attachmentImageHoldingPanel.setOpaque(false);
        attachmentImageHoldingPanel.add(editableAttachmentImage, BorderLayout.CENTER);

        uploadFilePanel.add(attachmentImageHoldingPanel, BorderLayout.CENTER);
        return uploadFilePanel;
    }


    private JPanel getRightBottomPanel(EditableAttachmentImage editableAttachmentImage) {
        JPanel bottomMainHoldingPane = new JPanel(new BorderLayout());
        bottomMainHoldingPane.setPreferredSize(new Dimension(600, 45));
        bottomMainHoldingPane.setOpaque(false);

        JPanel upperGapPanel = new JPanel();
        upperGapPanel.setOpaque(false);
        upperGapPanel.setPreferredSize(new Dimension(bottomMainHoldingPane.getWidth(), 10));

        JPanel bottomPanel = getBottomPanelOfCenterRight(editableAttachmentImage);

        bottomMainHoldingPane.add(upperGapPanel, BorderLayout.NORTH);
        bottomMainHoldingPane.add(bottomPanel, BorderLayout.CENTER);

        return bottomMainHoldingPane;
    }

    private JPanel getBottomPanelOfCenterRight(EditableAttachmentImage editableAttachmentImage) {
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        bottomPanel.setOpaque(false);
        bottomPanel.setPreferredSize(new Dimension(600, 45));

        bottomPanel.add(getRemoveSelectedButton(editableAttachmentImage));
        bottomPanel.add(getBrowseFilesButton(editableAttachmentImage));
        return bottomPanel;
    }


    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(100, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, GAP, 0, GAP));
        headerPanel.setOpaque(false);
        headerPanel.add(headerLabel, BorderLayout.WEST);
        return headerPanel;
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


        leftInnerBottom.add(saveExpTrnButton());
        leftInnerBottom.add(closeExpEntryButton());

        bottomPanel.add(leftInnerBottom, BorderLayout.WEST);
        return bottomPanel;
    }

    private RoundedPanel createTextFieldsHolding() {
        RoundedPanel centerMain = new RoundedPanel(7);
        centerMain.setLayout(new BorderLayout());
        centerMain.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        centerMain.setBackground(COLOR_WHITE);
        centerMain.setOpaque(false);

        JPanel centerInner = new JPanel(new GridLayout(5, 1, GAP, GAP));
        centerInner.setPreferredSize(new Dimension(400, newExpEntryPanel.getHeight()));
        centerInner.setOpaque(false);
        initializeTextFields();
        initializeComboBoxes();

        centerInner.add(comboBoxExpCategory);
        centerInner.add(expTitleField);
        centerInner.add(expRequestAmountField);
        centerInner.add(expRefundAmountField);
        centerInner.add(comboBoxAssignedTo);

        centerMain.add(createHeaderPanel(), BorderLayout.NORTH);
        centerMain.add(centerInner, BorderLayout.CENTER);
        centerMain.add(getSignaturePanel(), BorderLayout.SOUTH);

        return centerMain;
    }

    private JPanel getSignaturePanel() {
        JPanel bottomHoldingMain = new JPanel(new BorderLayout());
        bottomHoldingMain.setPreferredSize(new Dimension(600, 180));
        bottomHoldingMain.setOpaque(false);

        JPanel upperSignaturePanel = getUpperSignaturePanel(bottomHoldingMain);
        bottomHoldingMain.add(upperSignaturePanel, BorderLayout.NORTH);
        bottomHoldingMain.add(getSignatureHoldingPanel(), BorderLayout.CENTER);

        return bottomHoldingMain;
    }

    private JPanel getUpperSignaturePanel(JPanel bottomHoldingMain) {
        JPanel upperSignaturePanel = new JPanel(new BorderLayout());
        upperSignaturePanel.setPreferredSize(new Dimension(bottomHoldingMain.getWidth(), 30));
        upperSignaturePanel.setOpaque(false);
        upperSignaturePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        JLabel signatureLabel = new JLabel("Assigned Employee's Signature");
        signatureLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 13));
        signatureLabel.setOpaque(false);
        signatureLabel.setForeground(COLOR_FONT_GRAY);
        upperSignaturePanel.add(signatureLabel, BorderLayout.WEST);
        return upperSignaturePanel;
    }

    private RoundedPanel getSignatureHoldingPanel() {
        // Create the signature holding panel with rounded corners
        RoundedPanel signatureHolding = new RoundedPanel(10);
        signatureHolding.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        signatureHolding.setBackground(COLOR_WHITE);
        signatureHolding.setBorderColor(COLOR_LINE_COLOR);
        signatureHolding.setBorderWidth(1);
        signatureHolding.setOpaque(false);

        // Use fixed size for the SignaturePanel
        signaturePanel = new SignaturePanel(900, 180);
        signatureHolding.setLayout(new BorderLayout());

        JPanel buttonHoldingPanel = new JPanel(new BorderLayout());
        buttonHoldingPanel.setPreferredSize(new Dimension(buttonHoldingPanel.getWidth(), 40));
        buttonHoldingPanel.setOpaque(false);

        JPanel gapPanel = new JPanel(new BorderLayout());
        gapPanel.setPreferredSize(new Dimension(buttonHoldingPanel.getWidth(), 5));
        gapPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_LINE_COLOR));
        gapPanel.setOpaque(false);

        JPanel buttonInnerHolding = new JPanel(new BorderLayout());
        buttonInnerHolding.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        buttonInnerHolding.setOpaque(false);

        JPanel buttonRightInnerHolding = new JPanel(new BorderLayout());
        buttonRightInnerHolding.setOpaque(false);
        buttonRightInnerHolding.setPreferredSize(new Dimension(75, 35));

        buttonRightInnerHolding.add(getClearSignatureButton(signaturePanel), BorderLayout.CENTER);

        buttonInnerHolding.add(buttonRightInnerHolding, BorderLayout.EAST);


        buttonHoldingPanel.add(gapPanel, BorderLayout.NORTH);
        buttonHoldingPanel.add(buttonInnerHolding, BorderLayout.CENTER);

        signatureHolding.add(signaturePanel, BorderLayout.CENTER);
        signatureHolding.add(buttonHoldingPanel, BorderLayout.SOUTH);

        return signatureHolding;
    }


    private RoundedBorderButton getClearSignatureButton(SignaturePanel signaturePanel) {
        RoundedBorderButton clearButton = UIAppButtonFactory.creatRoundedBorderButton(
                "Clear", "/SignatureIcon.svg", COLOR_FONT_GRAY, 14);
        clearButton.setHoverBackgroundColor(Color.LIGHT_GRAY);
        clearButton.setPressedBackgroundColor(COLOR_PANEL_GRAY);
        clearButton.setPreferredSize(new Dimension(60, 35));

        clearButton.addActionListener(e -> signaturePanel.confirmClearSignature());

        return clearButton;
    }

    private RoundedBorderButton getBrowseFilesButton(EditableAttachmentImage attachmentImage) {
        RoundedBorderButton browseFileButton = UIAppButtonFactory.creatRoundedBorderButton(
                "Browse", "/FileUploadIconBlue.svg", COLOR_FONT_GRAY, 20);
        browseFileButton.setHoverBackgroundColor(Color.LIGHT_GRAY);
        browseFileButton.setPressedBackgroundColor(COLOR_PANEL_GRAY);
        browseFileButton.setPreferredSize(new Dimension(60, 35));

        // Connect the button to the attachmentImage's chooseNewImage method
        browseFileButton.addActionListener(e -> attachmentImage.chooseNewImage());
        return browseFileButton;
    }

    private RoundedBorderButton getRemoveSelectedButton(EditableAttachmentImage attachmentImage) {
        RoundedBorderButton removeSelectedButton = UIAppButtonFactory.creatRoundedBorderButton(
                "Remove", "/delete.svg", COLOR_FONT_GRAY, 20);
        removeSelectedButton.setHoverBackgroundColor(Color.LIGHT_GRAY);
        removeSelectedButton.setPressedBackgroundColor(COLOR_PANEL_GRAY);
        removeSelectedButton.setPreferredSize(new Dimension(60, 35));

        // Connect the button to the attachmentImage's removeImage method
        removeSelectedButton.addActionListener(e -> attachmentImage.removeImage());
        return removeSelectedButton;
    }


    private void initializeTextFields() {
        expTitleField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Expense Request Title");
        expRequestAmountField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Request Amount");
        expRefundAmountField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Refund Amount");

        expTitleField.setHorizontalAlignment(SwingConstants.LEFT);
        expRequestAmountField.setHorizontalAlignment(SwingConstants.LEFT);
        expRefundAmountField.setHorizontalAlignment(SwingConstants.LEFT);

        expRefundAmountField.setEditable(false);
        expRefundAmountField.setFocusable(false);

        // Adding KeyListeners to restrict input to numbers
        addNumericKeyListener(expRequestAmountField);
        // Attach the DocumentFilter
        ((AbstractDocument) expRequestAmountField.getDocument()).setDocumentFilter(new NumericDocumentFilter());
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
        FetchExpenseCategoryLists fetchExpenseCategoryLists = new FetchExpenseCategoryLists();
        List<String> expCatNames = fetchExpenseCategoryLists.getExpCatNames();

        FetchEmployeeHistoryLists fetchEmployeeHistoryLists = new FetchEmployeeHistoryLists();
        List<String> employeeNames = fetchEmployeeHistoryLists.getEmpNames();

        // Make combo boxes editable
        comboBoxExpCategory.setEditable(true);
        comboBoxAssignedTo.setEditable(true);

        // Initialize combo boxes
        initializeComboBox(comboBoxExpCategory, expCatNames, "Select Expense Category");
        initializeComboBox(comboBoxAssignedTo, employeeNames, "Select Assigned To");

        // Add key listeners for handling key events
        addKeyListenerToComboBox(comboBoxExpCategory);
        addKeyListenerToComboBox(comboBoxAssignedTo);
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
                    comboBox == comboBoxExpCategory ? "Select Expense Category" :
                            comboBox == comboBoxAssignedTo ? "Select Assigned To" : "");
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


    private IconTextButton saveExpTrnButton() {
        IconTextButton saveButton = UIAppButtonFactory.createIconTextButton("Update", "/UpdateIcon.svg", COLOR_GREEN);
        saveButton.addActionListener(e -> {
            if (isCompleteForCollectDataAndImport()) {
                SwingUtilities.invokeLater(this::resetInputSection);
                refreshExpSummaryAndHistTables();
            }
        });
        return saveButton;
    }

    public static void refreshExpSummaryAndHistTables() {
        new TransactionPage().refreshExpenseTransactionTableData();
        new SummaryPage().refreshSummaryExpenseTable();
    }


    private RoundedBorderButton closeExpEntryButton() {
        RoundedBorderButton closeButton = UIAppButtonFactory.creatRoundedBorderButton("Close", "/CloseIcon.svg", COLOR_ORANGE, 20);
        closeButton.addActionListener(e -> {
            ExpenseManagementPage.expenseMenuCardLayout.show(ExpenseManagementPage.menuExpenseCardHoldingPanel, "EXP-TRN");
            resetInputSection();
        });
        return closeButton;
    }


    private void resetInputSection() {
        SwingUtilities.invokeLater(() -> {
            clearSignatureArea();
            clearAllAttachments();
            clearAllTextFields();
            initializeTextFields();
            initializeComboBoxes();
        });
    }

    private void clearAllTextFields() {
        SwingUtilities.invokeLater(() -> {
            expTitleField.setText("");
            expRequestAmountField.setText("");
        });
    }

    private void clearSignatureArea() {
        signaturePanel.clearSignature();
    }

    private void clearAllAttachments() {
        // Clear the uploaded files from the list model
        editableAttachmentImage.removeImage();
    }

    private boolean isCompleteForCollectDataAndImport() {
        try {
            // Generate a new expense code ID
            String generateNewExpCodeID = GenerateExpenseCodeFactory.generateNewExpCode();
            // Get selected expense category
            String expCategory = String.valueOf(comboBoxExpCategory.getSelectedItem());
            // Get expense title and request amount
            String expTitle = expTitleField.getText().trim();
            String expRequestAmountText = expRequestAmountField.getText().replace(",", "").trim();

            int batchUserID = CreateCashDrawerPanel.tellerID;

            // Validate request amount
            if (expRequestAmountText.isEmpty()) {
                throw new NumberFormatException("Request amount is empty");
            }

            int requestExpAmount = Integer.parseInt(expRequestAmountText);
            String assignedToEmpName = "";
            if (comboBoxAssignedTo.getSelectedItem() == null) {
                assignedToEmpName = String.valueOf(comboBoxAssignedTo.getSelectedItem());
            }
            byte[] signatureAsBytes = signaturePanel.getSignatureAsBytes();

            // Validate form fields
            if (expCategory.equals("Select Expense Category") || expTitle.isEmpty() || requestExpAmount <= 0) {
                JOptionPane.showMessageDialog(newExpEntryPanel, "You must fill all fields correctly", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return false; // Indicate failure
            }

            byte[] importAttachment = null;
            if (editableAttachmentImage.getImageBytes() != null) {
                importAttachment = editableAttachmentImage.getImageBytes();
            }

            // Call the method to create a new expense in the database
            ExpenseBatchFactory.createNewExpenseIntoDB(
                    batchUserID,
                    generateNewExpCodeID,
                    expCategory,
                    expTitle,
                    requestExpAmount,
                    assignedToEmpName,
                    signatureAsBytes,
                    importAttachment
            );

            return true; // Indicate success
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please fill all fields correctly: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false; // Indicate failure
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return false; // Indicate failure
        }
    }


}
