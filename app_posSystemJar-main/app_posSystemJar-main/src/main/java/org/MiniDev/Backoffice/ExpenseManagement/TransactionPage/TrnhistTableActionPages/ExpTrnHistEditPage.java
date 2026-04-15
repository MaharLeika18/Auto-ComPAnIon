
package org.MiniDev.Backoffice.ExpenseManagement.TransactionPage.TrnhistTableActionPages;

import SqlLoadersAndUpdater.*;
import UI.*;
import Utils.NumericDocumentFilter;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.Backoffice.ExpenseManagement.TransactionPage.NewExpTransactionPage;
import org.MiniDev.Cashier.CreateCashDrawerPanel;
import org.MiniDev.Home.MiniDevPOS;
import org.MiniDev.OOP.ExpenseTransactionLists;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.util.List;
import static UI.UserFinalSettingsVar.*;
import static org.MiniDev.Cashier.CreateTodaySalePanel.GAP;

public class ExpTrnHistEditPage {

    protected static JDialog expEditDialog;
    protected RoundedComboBox<String> comboBoxExpCategory = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 expTitleField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 expRequestAmountField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 expRefundAmountField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedComboBox<String> comboBoxAssignedTo = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);

    protected EditableAttachmentImage editableAttachmentImageForEdit;

    public ExpTrnHistEditPage() {
    }

    public void showExpHistPanel(ExpenseTransactionLists expTrnLists) {
        expEditDialog = new JDialog();
        expEditDialog.setUndecorated(true); // Remove window decorations for custom rounding
        expEditDialog.setBackground(new Color(0, 0, 0, 0));
        expEditDialog.setSize(500, 680);
        expEditDialog.setModal(true); // Make the dialog modal
        expEditDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        RoundedOptionPane.setRoundedCorners(expEditDialog, 0);
        expEditDialog.setLayout(new BorderLayout());

        // Add the panel to the dialog
        expEditDialog.getContentPane().add(getEditHoldingPanel(expTrnLists));
        expEditDialog.pack(); // Resize the dialog to fit the content
        expEditDialog.setLocationRelativeTo(MiniDevPOS.contentPanel); // Center the dialog on the screen

        // Set the shape for rounded corners
        expEditDialog.setShape(new RoundRectangle2D.Double(0, 0, expEditDialog.getWidth(), expEditDialog.getHeight(), 0, 0));

        // Add fade-in animation
        DialogFadeIn.fadeIn(expEditDialog);

        expEditDialog.setVisible(true); // Show the dialog
    }

    protected RoundedPanel getEditHoldingPanel(ExpenseTransactionLists expTrnLists) {
        // Create the rounded panel with a custom rounded border
        RoundedPanel editPanel = new RoundedPanel(20);
        editPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the content
        editPanel.setPreferredSize(new Dimension(expEditDialog.getWidth(), expEditDialog.getHeight()));
        editPanel.setBackground(COLOR_ENTRY_GRAY);
        editPanel.setLayout(new BorderLayout());

        editPanel.add(getHeaderPanel(expTrnLists.getExpUsedID(), expTrnLists.getExpName()), BorderLayout.NORTH);
        editPanel.add(getContentPanel(expTrnLists), BorderLayout.CENTER);
        editPanel.add(getBottomPanel(expTrnLists), BorderLayout.SOUTH);

        return editPanel;
    }

    protected RoundedPanel getHeaderPanel(String expUsedID, String expUsedCategory) {
        RoundedPanel headerPanel = new RoundedPanel(10);
        headerPanel.setLayout(new GridLayout(1, 2, 0, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        headerPanel.setPreferredSize(new Dimension(expEditDialog.getWidth(), 65));
        headerPanel.setOpaque(false);
        headerPanel.setBackground(COLOR_GRAY);

        JPanel left = new JPanel();
        left.setLayout(new BorderLayout());
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(200, 65));

        JPanel right = new JPanel();
        right.setLayout(new BorderLayout());
        right.setOpaque(false);
        right.setBorder(BorderFactory.createEmptyBorder(GAP, 0, GAP, 0));
        right.setPreferredSize(new Dimension(200, 65));

        left.add(createCustomHeaderLabel(expUsedCategory, 14), BorderLayout.WEST);
        right.add(createCustomHeaderLabel(expUsedID, 11), BorderLayout.EAST);

        headerPanel.add(left);
        headerPanel.add(right);

        return headerPanel;
    }


    protected JPanel getContentPanel(ExpenseTransactionLists expTrnLists) {
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout());

        JPanel upperGap = new JPanel();
        upperGap.setOpaque(false);
        upperGap.setPreferredSize(new Dimension(expEditDialog.getWidth(), 10));

        RoundedPanel mainCenterPanel = new RoundedPanel(10);
        mainCenterPanel.setLayout(new BorderLayout());
        mainCenterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 12, 10));
        mainCenterPanel.setOpaque(false);
        mainCenterPanel.setBackground(COLOR_GRAY);
        mainCenterPanel.add(mainInfoHoldingPanel(expTrnLists), BorderLayout.CENTER);

        JPanel bottomGap = new JPanel();
        bottomGap.setOpaque(false);
        bottomGap.setPreferredSize(new Dimension(expEditDialog.getWidth(), 10));

        contentPanel.add(upperGap, BorderLayout.NORTH);
        contentPanel.add(mainCenterPanel, BorderLayout.CENTER);
        contentPanel.add(bottomGap, BorderLayout.SOUTH);

        return contentPanel;
    }

    protected JPanel mainInfoHoldingPanel(ExpenseTransactionLists expTrnLists) {
        JPanel mainInfoPanel = new JPanel();
        mainInfoPanel.setOpaque(false);
        mainInfoPanel.setLayout(new BorderLayout());
        mainInfoPanel.add(getUpperHoldingPanel(expTrnLists.getExpName(), expTrnLists.getAssignedToEmployee(),
                expTrnLists.getExpRemark(), String.valueOf(expTrnLists.getExpAmount()), String.valueOf(expTrnLists.getRefundAmount())), BorderLayout.CENTER);
        mainInfoPanel.add(getLowerHoldingPanel(expTrnLists), BorderLayout.SOUTH);

        return mainInfoPanel;
    }

    protected JPanel getUpperHoldingPanel(String initializedExpCategory, String initializedAssignedToEmpName,
                                          String initializedExpTitle, String initializedRequestAmountText, String initializedRefundAmountText) {
        JPanel upperHoldingPanel = new JPanel(new GridLayout(5, 1, GAP, GAP));
        upperHoldingPanel.setOpaque(false);

        initializeComboBoxes(initializedExpCategory, initializedAssignedToEmpName);
        initializeTextValues(initializedExpTitle, initializedRequestAmountText, initializedRefundAmountText);

        upperHoldingPanel.add(createComboSelectionFields("Expense Category:", comboBoxExpCategory));
        upperHoldingPanel.add(createRowTextFields("Request Title:", expTitleField, false, true));
        upperHoldingPanel.add(createRowTextFields("Request Amount:", expRequestAmountField, true, false));
        upperHoldingPanel.add(createRowTextFields("Refund Amount:", expRefundAmountField, true, true));
        upperHoldingPanel.add(createComboSelectionFields("Assigned To:", comboBoxAssignedTo));

        return upperHoldingPanel;
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

    protected JPanel createRowTextFields(String headerName, RoundedTextFieldV2 textField, boolean isNumericField, boolean isEnabled) {
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

        if (isEnabled) {
            textField.setEditable(true);
            textField.setFocusable(true);
        } else {
            textField.setFocusable(false);
            textField.setEditable(false);
            textField.setEnabled(false);
        }

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

    private void initializeTextValues(String initializedExpTitle, String initializedRequestAmountText, String initializedRefundAmountText) {
        expTitleField.setText(initializedExpTitle);

        // Create a DecimalFormat instance
        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        // Parse and format the request amount
        try {
            int requestAmount = Integer.parseInt(initializedRequestAmountText);
            expRequestAmountField.setText(decimalFormat.format(requestAmount));
        } catch (NumberFormatException e) {
            expRequestAmountField.setText(initializedRequestAmountText); // or handle the error appropriately
        }

        expRefundAmountField.setText(initializedRefundAmountText);
    }

    private void initializeComboBoxes(String initializedExpCategory, String initializedAssignedToEmpName) {
        FetchExpenseCategoryLists fetchExpenseCategoryLists = new FetchExpenseCategoryLists();
        List<String> expCatNames = fetchExpenseCategoryLists.getExpCatNames();

        FetchEmployeeHistoryLists fetchEmployeeHistoryLists = new FetchEmployeeHistoryLists();
        List<String> employeeNames = fetchEmployeeHistoryLists.getEmpNames();

        // Make combo boxes editable
        comboBoxExpCategory.setEditable(true);
        comboBoxAssignedTo.setEditable(true);

        // Initialize combo boxes
        initializeComboBox(comboBoxExpCategory, expCatNames, initializedExpCategory);
        initializeComboBox(comboBoxAssignedTo, employeeNames, initializedAssignedToEmpName);
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


    protected JPanel getLowerHoldingPanel(ExpenseTransactionLists expTrnList) {
        JPanel lowerHoldingPanel = new JPanel(new BorderLayout());
        lowerHoldingPanel.setOpaque(false);
        lowerHoldingPanel.setBorder(BorderFactory.createEmptyBorder(GAP, 0, 0, 0));
        lowerHoldingPanel.setPreferredSize(new Dimension(expEditDialog.getWidth(), 240));

        JPanel leftLower = new JPanel(new BorderLayout());
        JPanel rightLower = new JPanel(new BorderLayout());

        leftLower.setOpaque(false);
        rightLower.setOpaque(false);

        leftLower.setBorder(BorderFactory.createEmptyBorder(3,0,0,0));

        leftLower.setPreferredSize(new Dimension(140, lowerHoldingPanel.getHeight()));

        JLabel descriptionLabel = new JLabel("File Attachment:");
        descriptionLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13));

        leftLower.add(descriptionLabel, BorderLayout.NORTH);
        rightLower.add(getAttachmentPanel(expTrnList), BorderLayout.CENTER);


        lowerHoldingPanel.add(leftLower, BorderLayout.WEST);
        lowerHoldingPanel.add(rightLower, BorderLayout.CENTER);

        return lowerHoldingPanel;
    }


    private JPanel getAttachmentPanel(ExpenseTransactionLists expTrnList) {
        JPanel fileUploadMainPanel = new JPanel(new BorderLayout());
        fileUploadMainPanel.setOpaque(false);
        fileUploadMainPanel.setPreferredSize(new Dimension(600, 180));

        editableAttachmentImageForEdit = new EditableAttachmentImage();
        editableAttachmentImageForEdit.setNewBytes(expTrnList.getAttachmentImage());

        fileUploadMainPanel.add(getRightCenterPanel(editableAttachmentImageForEdit), BorderLayout.CENTER);
        fileUploadMainPanel.add(getRightBottomPanel(editableAttachmentImageForEdit), BorderLayout.SOUTH);

        return fileUploadMainPanel;
    }

    private JPanel getRightBottomPanel(EditableAttachmentImage editableAttachmentImageForEdit) {
        JPanel bottomMainHoldingPane = new JPanel(new BorderLayout());
        bottomMainHoldingPane.setPreferredSize(new Dimension(600, 45));
        bottomMainHoldingPane.setOpaque(false);

        JPanel upperGapPanel = new JPanel();
        upperGapPanel.setOpaque(false);
        upperGapPanel.setPreferredSize(new Dimension(bottomMainHoldingPane.getWidth(), 10));

        JPanel bottomPanel = getBottomPanelOfCenterRight(editableAttachmentImageForEdit);

        bottomMainHoldingPane.add(upperGapPanel, BorderLayout.NORTH);
        bottomMainHoldingPane.add(bottomPanel, BorderLayout.CENTER);

        return bottomMainHoldingPane;
    }

    private JPanel getBottomPanelOfCenterRight(EditableAttachmentImage editableAttachmentImageForEdit) {
        JPanel bottomPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        bottomPanel.setOpaque(false);
        bottomPanel.setPreferredSize(new Dimension(600, 30));

        bottomPanel.add(getRemoveSelectedButton(editableAttachmentImageForEdit));
        bottomPanel.add(getDownloadFilesButton(editableAttachmentImageForEdit));
        bottomPanel.add(getBrowseFilesButton(editableAttachmentImageForEdit));
        return bottomPanel;
    }

    private RoundedBorderButton getRemoveSelectedButton(EditableAttachmentImage editableAttachmentImageForEdit) {
        RoundedBorderButton removeSelectedButton = UIAppButtonFactory.creatRoundedBorderButton(
                "Remove", "/delete.svg", COLOR_FONT_GRAY, 20);
        removeSelectedButton.setHoverBackgroundColor(Color.LIGHT_GRAY);
        removeSelectedButton.setPressedBackgroundColor(COLOR_PANEL_GRAY);
        removeSelectedButton.setPreferredSize(new Dimension(60, 35));
        removeSelectedButton.addActionListener(e -> editableAttachmentImageForEdit.removeImage());
        return removeSelectedButton;
    }

    // Get the download button
    private RoundedBorderButton getDownloadFilesButton(EditableAttachmentImage editableAttachmentImageForEdit) {
        RoundedBorderButton downloadFilesButton = UIAppButtonFactory.creatRoundedBorderButton(
                "Download", "/DownloadFileIcon.svg", COLOR_FONT_GRAY, 20);
        downloadFilesButton.setHoverBackgroundColor(Color.LIGHT_GRAY);
        downloadFilesButton.setPressedBackgroundColor(COLOR_PANEL_GRAY);
        downloadFilesButton.setPreferredSize(new Dimension(60, 35));
        downloadFilesButton.addActionListener(e -> {editableAttachmentImageForEdit.downloadImage();});
        return downloadFilesButton;
    }


    private RoundedBorderButton getBrowseFilesButton(EditableAttachmentImage editableAttachmentImageForEdit) {
        RoundedBorderButton browseFileButton = UIAppButtonFactory.creatRoundedBorderButton(
                "Browse", "/FileUploadIconBlue.svg", COLOR_FONT_GRAY, 20);
        browseFileButton.setHoverBackgroundColor(Color.LIGHT_GRAY);
        browseFileButton.setPressedBackgroundColor(COLOR_PANEL_GRAY);
        browseFileButton.setPreferredSize(new Dimension(60, 35));
        browseFileButton.addActionListener(e -> editableAttachmentImageForEdit.chooseNewImage());
        return browseFileButton;
    }


    protected RoundedPanel uploadFilePanelForExp;
    protected JPanel attachmentImageHoldingPanel;

    private RoundedPanel getRightCenterPanel(EditableAttachmentImage editableAttachmentImageForEdit) {
        uploadFilePanelForExp = new RoundedPanel(10);
        uploadFilePanelForExp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        uploadFilePanelForExp.setLayout(new BorderLayout());
        uploadFilePanelForExp.setBackground(COLOR_WHITE);


        attachmentImageHoldingPanel = new JPanel(new BorderLayout());
        attachmentImageHoldingPanel.setOpaque(false);
        attachmentImageHoldingPanel.add(editableAttachmentImageForEdit, BorderLayout.CENTER);

        uploadFilePanelForExp.add(attachmentImageHoldingPanel, BorderLayout.CENTER);

        return uploadFilePanelForExp;
    }



    protected JLabel createCustomHeaderLabel(String labelText, int fontBaseSize) {
        JLabel label = new JLabel(labelText);
        label.setForeground(COLOR_FONT_GRAY);
        label.setFont(new Font("Noto Sans Myanmar", Font.BOLD, fontBaseSize));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }


    protected RoundedPanel getBottomPanel(ExpenseTransactionLists selectedExpTrnList) {
        RoundedPanel bottomPanel = new RoundedPanel(10);
        bottomPanel.setPreferredSize(new Dimension(expEditDialog.getWidth(), 60));
        bottomPanel.setBackground(COLOR_GRAY);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, GAP));
        bottomPanel.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, GAP, 0));
        buttonPanel.setPreferredSize(new Dimension(215, 45));
        buttonPanel.setOpaque(false);
        buttonPanel.add(updateButtonExpEditPage(selectedExpTrnList));
        buttonPanel.add(closeButtonExpEditPage());

        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        return bottomPanel;
    }

    private RoundedBorderButton closeButtonExpEditPage() {
        RoundedBorderButton closeButton = UIAppButtonFactory.creatRoundedBorderButton("Close", "/CloseIcon.svg", COLOR_ORANGE, 20);
        closeButton.addActionListener(e -> {
            expEditDialog.dispose();
        });
        return closeButton;
    }


    private IconTextButton updateButtonExpEditPage(ExpenseTransactionLists selectedExpTrnList) {
        IconTextButton updateButton = UIAppButtonFactory.createIconTextButton("Update", "/UpdateIcon.svg", COLOR_GREEN);
        updateButton.addActionListener(e -> {
            if (processExpenseUpdateIntoDatabase(selectedExpTrnList)) {
                NewExpTransactionPage.refreshExpSummaryAndHistTables();
                expEditDialog.dispose();
            }
        });
        return updateButton;
    }

    private boolean processExpenseUpdateIntoDatabase(ExpenseTransactionLists selectedExpTrnList) {
            try {
                // Generate a new expense code ID
                String usedExpID = selectedExpTrnList.getExpUsedID();
                // Get selected expense category
                String expCategory = String.valueOf(comboBoxExpCategory.getSelectedItem());
                // Get expense title and request amount
                String expTitle = expTitleField.getText().trim();
                String expRefundAmountText = expRefundAmountField.getText().replace(",", "").trim();

                int batchUserID = CreateCashDrawerPanel.tellerID;

                int refundExpAmount = Integer.parseInt(expRefundAmountText);
                String assignedToEmpName = String.valueOf(comboBoxAssignedTo.getSelectedItem());

                // Validate form fields
                if (expCategory.equals("Select Expense Category") || expTitle.isEmpty() || refundExpAmount < 0) {
                    JOptionPane.showMessageDialog(expEditDialog, "You must fill all fields correctly", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return false; // Indicate failure
                }

                byte[] importAttachment = null;
                if (editableAttachmentImageForEdit.getImageBytes() != null) {
                    importAttachment = editableAttachmentImageForEdit.getImageBytes();
                }

                // Call the method to create a new expense in the database
                ExpenseBatchFactory.updateExpenseTrnIntoDB(
                        batchUserID,
                        usedExpID,
                        expCategory,
                        expTitle,
                        refundExpAmount,
                        assignedToEmpName,
                        importAttachment
                );

                return true; // Indicate success
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(expEditDialog, "Please fill all fields correctly: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return false; // Indicate failure
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
                e.printStackTrace();
                return false; // Indicate failure
            }
        }


    }
