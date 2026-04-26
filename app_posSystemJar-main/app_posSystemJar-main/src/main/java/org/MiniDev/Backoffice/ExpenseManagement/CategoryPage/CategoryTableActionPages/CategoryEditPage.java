package org.MiniDev.Backoffice.ExpenseManagement.CategoryPage.CategoryTableActionPages;

import SqlLoadersAndUpdater.ExpenseBatchFactory;
import UI.*;
import Utils.NumericDocumentFilter;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.Backoffice.ExpenseManagement.CategoryPage.CategoryPage;
import org.MiniDev.Backoffice.ExpenseManagement.TransactionPage.NewExpTransactionPage;
import org.MiniDev.Cashier.CreateCashDrawerPanel;
import org.MiniDev.OOP.ExpenseCategoryLists;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static UI.UserFinalSettingsVar.*;
import static org.MiniDev.Cashier.CreateTodaySalePanel.GAP;

public class CategoryEditPage {

    protected JDialog categoryEditDialog;
    protected static EditableAvatar avatarForExp;
    protected RoundedTextFieldV2 expCategoryCode = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 expCategoryName = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedComboBox<String> comboBoxExpUsedYN = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 expCurrentYearAllocationField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);

    public CategoryEditPage() {
    }

    public void showCounterEditPanel(ExpenseCategoryLists expenseCategoryLists) {
        categoryEditDialog = new JDialog();
        categoryEditDialog.setUndecorated(true); // Remove window decorations for custom rounding
        categoryEditDialog.setBackground(new Color(0, 0, 0, 0));
        categoryEditDialog.setSize(500, 400);
        categoryEditDialog.setModal(true); // Make the dialog modal
        categoryEditDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        RoundedOptionPane.setRoundedCorners(categoryEditDialog, 0);
        categoryEditDialog.setLayout(new BorderLayout());

        // Add the panel to the dialog
        categoryEditDialog.getContentPane().add(getEditHoldingPanel(expenseCategoryLists));
        categoryEditDialog.pack(); // Resize the dialog to fit the content
        categoryEditDialog.setLocationRelativeTo(null); // Center the dialog on the screen

        // Set the shape for rounded corners
        categoryEditDialog.setShape(new RoundRectangle2D.Double(0, 0, categoryEditDialog.getWidth(), categoryEditDialog.getHeight(), 0, 0));

        // Add fade-in animation
        DialogFadeIn.fadeIn(categoryEditDialog);

        categoryEditDialog.setVisible(true); // Show the dialog
    }

    protected RoundedPanel getEditHoldingPanel(ExpenseCategoryLists expenseCategoryLists) {
        // Create the rounded panel with a custom rounded border
        RoundedPanel editPanel = new RoundedPanel(20);
        editPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the content
        editPanel.setPreferredSize(new Dimension(categoryEditDialog.getWidth(), categoryEditDialog.getHeight()));
        editPanel.setBackground(COLOR_ENTRY_GRAY);
        editPanel.setLayout(new BorderLayout());

        editPanel.add(getHeaderPanel(expenseCategoryLists), BorderLayout.NORTH);
        editPanel.add(getContentPanel(expenseCategoryLists), BorderLayout.CENTER);
        editPanel.add(getBottomPanel(expenseCategoryLists), BorderLayout.SOUTH);

        return editPanel;
    }

    protected RoundedPanel getHeaderPanel(ExpenseCategoryLists expenseCategoryLists) {
        RoundedPanel headerPanel = new RoundedPanel(10);
        headerPanel.setLayout(new GridLayout(1, 3, 0, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        headerPanel.setPreferredSize(new Dimension(categoryEditDialog.getWidth(), 75));
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

        mid.add(initializeEditableFoodImage(expenseCategoryLists), BorderLayout.CENTER);

        headerPanel.add(left);
        headerPanel.add(mid);
        headerPanel.add(right);

        return headerPanel;
    }

    protected JPanel getContentPanel(ExpenseCategoryLists expenseCategoryLists) {
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout());

        JPanel upperGap = new JPanel();
        upperGap.setOpaque(false);
        upperGap.setPreferredSize(new Dimension(categoryEditDialog.getWidth(), 10));

        RoundedPanel mainCenterPanel = new RoundedPanel(10);
        mainCenterPanel.setLayout(new BorderLayout());
        mainCenterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 12, 10));
        mainCenterPanel.setOpaque(false);
        mainCenterPanel.setBackground(COLOR_GRAY);
        mainCenterPanel.add(mainInfoHoldingPanel(expenseCategoryLists), BorderLayout.CENTER);

        JPanel bottomGap = new JPanel();
        bottomGap.setOpaque(false);
        bottomGap.setPreferredSize(new Dimension(categoryEditDialog.getWidth(), 10));

        contentPanel.add(upperGap, BorderLayout.NORTH);
        contentPanel.add(mainCenterPanel, BorderLayout.CENTER);
        contentPanel.add(bottomGap, BorderLayout.SOUTH);

        return contentPanel;
    }

    protected JPanel mainInfoHoldingPanel(ExpenseCategoryLists expenseCategoryLists) {
        JPanel infoHoldingPanel = new JPanel(new GridLayout(4, 1, GAP, GAP));
        infoHoldingPanel.setOpaque(false);

        initializeComboBoxes(expenseCategoryLists);
        initializeTextValues(expenseCategoryLists);

        infoHoldingPanel.add(createRowTextFields("Expense Code:", expCategoryCode, false, false));
        infoHoldingPanel.add(createRowTextFields("Expense Name:", expCategoryName, false, true));
        infoHoldingPanel.add(createRowTextFields("Allocation (ThisYear):", expCurrentYearAllocationField, true, true));
        infoHoldingPanel.add(createComboSelectionFields("Active/Inactive:", comboBoxExpUsedYN));

        return infoHoldingPanel;
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

    private void initializeTextValues(ExpenseCategoryLists expenseCategoryLists) {
        expCategoryCode.setText(expenseCategoryLists.getExpenseCode());
        expCategoryName.setText(expenseCategoryLists.getExpenseCodeName());
        expCurrentYearAllocationField.setText(expenseCategoryLists.getCurrentYearBudgetAmountString());
    }

    protected JPanel createRowTextFields(String headerName, RoundedTextFieldV2 textField, boolean isNumericField, boolean canEdit) {
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

        textField.setEditable(canEdit);
        textField.setEnabled(canEdit);
        textField.setFocusable(canEdit);

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

    private EditableAvatar initializeEditableFoodImage(ExpenseCategoryLists expenseCategoryLists) {
        avatarForExp = new EditableAvatar();
        avatarForExp.setBackground(COLOR_GRAY);

        ImageIcon foodImageIcon = new ImageIcon(expenseCategoryLists.getIconExpense());
        Image scaledImage = foodImageIcon.getImage().getScaledInstance(65, 65, Image.SCALE_SMOOTH);

        avatarForExp.setPreferredSize(new Dimension(65, 65));  // Desired size
        avatarForExp.setMaximumSize(new Dimension(65, 65));
        avatarForExp.setImage(new ImageIcon(scaledImage));

        return avatarForExp;
    }


    private void initializeComboBoxes(ExpenseCategoryLists expenseCategoryLists) {
        java.util.List<String> activeYNChars = new ArrayList<>(Arrays.asList("Active", "Inactive"));
        comboBoxExpUsedYN.setEditable(true);
        initializeComboBox(comboBoxExpUsedYN, activeYNChars, expenseCategoryLists.getExpUsedActiveInactive());

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


    protected RoundedPanel getBottomPanel(ExpenseCategoryLists expenseCategoryLists) {
        RoundedPanel bottomPanel = new RoundedPanel(10);
        bottomPanel.setPreferredSize(new Dimension(categoryEditDialog.getWidth(), 60));
        bottomPanel.setBackground(COLOR_GRAY);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, GAP));
        bottomPanel.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, GAP, 0));
        buttonPanel.setPreferredSize(new Dimension(215, 45));
        buttonPanel.setOpaque(false);
        buttonPanel.add(updateButtonEditPage(expenseCategoryLists));
        buttonPanel.add(closeButtonEditPage());

        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        return bottomPanel;
    }

    private RoundedBorderButton closeButtonEditPage() {
        RoundedBorderButton closeButton = UIAppButtonFactory.creatRoundedBorderButton("Close", "/CloseIcon.svg", COLOR_ORANGE, 20);
        closeButton.addActionListener(e -> categoryEditDialog.dispose());
        return closeButton;
    }

    private IconTextButton updateButtonEditPage(ExpenseCategoryLists expenseCategoryLists) {
        IconTextButton updateButton = UIAppButtonFactory.createIconTextButton("Update", "/UpdateIcon.svg", COLOR_GREEN);
        updateButton.addActionListener(e -> {
            if (processExpCategoryIntoDatabase(expenseCategoryLists)) {
                NewExpTransactionPage.refreshExpSummaryAndHistTables();
                new CategoryPage().refreshExpCategoryTable();
                categoryEditDialog.dispose();
            }
        });
        return updateButton;
    }

    private boolean processExpCategoryIntoDatabase(ExpenseCategoryLists expenseCategoryLists) {
        try {
            int allocationAmtInt = expCurrentYearAllocationField.getText().trim().isEmpty()
                    ? 0
                    : Integer.parseInt(expCurrentYearAllocationField.getText().replace(",", "").trim());

            String expenseCatName = expCategoryName.getText();
            char expActiveYN = Objects.equals(comboBoxExpUsedYN.getSelectedItem(), "Inactive") ? 'N' : 'Y';


            // Validate form fields
            if (expenseCategoryLists.getExpenseCode().isEmpty() || expenseCatName.isEmpty() || allocationAmtInt < 0) {
                JOptionPane.showMessageDialog(categoryEditDialog, "You must fill all fields correctly", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return false; // Indicate failure
            }

            ExpenseBatchFactory.updateExpenseCategoryIntoDB(
                    CreateCashDrawerPanel.tellerID,
                    avatarForExp.getImageBytes(),
                    expenseCategoryLists.getExpenseCode(),
                    expenseCatName,
                    allocationAmtInt,
                    expActiveYN
            );

            return true; // Indicate success
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(categoryEditDialog, "Please fill all fields correctly: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false; // Indicate failure
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return false; // Indicate failure
        }
    }
}
