package org.MiniDev.Backoffice.ExpenseManagement.CategoryPage.CategoryTableActionPages;

import UI.*;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.OOP.ExpenseCategoryLists;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import static UI.UserFinalSettingsVar.*;
import static org.MiniDev.Cashier.CreateTodaySalePanel.GAP;

public class CategoryViewPage {

    protected JDialog categoryViewDialog;
    protected static ImageAvatar avatarForExp;
    protected RoundedTextFieldV2 expCategoryCode = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 expCategoryName = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 expUsedActiveInactive = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 expCurrentYearAllocationField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);

    public CategoryViewPage() {
    }

    public void showCounterViewPanel(ExpenseCategoryLists expenseCategoryLists) {
        categoryViewDialog = new JDialog();
        categoryViewDialog.setUndecorated(true); // Remove window decorations for custom rounding
        categoryViewDialog.setBackground(new Color(0, 0, 0, 0));
        categoryViewDialog.setSize(500, 400);
        categoryViewDialog.setModal(true); // Make the dialog modal
        categoryViewDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        RoundedOptionPane.setRoundedCorners(categoryViewDialog, 0);
        categoryViewDialog.setLayout(new BorderLayout());

        // Add the panel to the dialog
        categoryViewDialog.getContentPane().add(getEditHoldingPanel(expenseCategoryLists));
        categoryViewDialog.pack(); // Resize the dialog to fit the content
        categoryViewDialog.setLocationRelativeTo(null); // Center the dialog on the screen

        // Set the shape for rounded corners
        categoryViewDialog.setShape(new RoundRectangle2D.Double(0, 0, categoryViewDialog.getWidth(), categoryViewDialog.getHeight(), 0, 0));

        // Add fade-in animation
        DialogFadeIn.fadeIn(categoryViewDialog);

        categoryViewDialog.setVisible(true); // Show the dialog
    }

    protected RoundedPanel getEditHoldingPanel(ExpenseCategoryLists expenseCategoryLists) {
        // Create the rounded panel with a custom rounded border
        RoundedPanel editPanel = new RoundedPanel(20);
        editPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the content
        editPanel.setPreferredSize(new Dimension(categoryViewDialog.getWidth(), categoryViewDialog.getHeight()));
        editPanel.setBackground(COLOR_ENTRY_GRAY);
        editPanel.setLayout(new BorderLayout());

        editPanel.add(getHeaderPanel(expenseCategoryLists), BorderLayout.NORTH);
        editPanel.add(getContentPanel(expenseCategoryLists), BorderLayout.CENTER);
        editPanel.add(getBottomPanel(), BorderLayout.SOUTH);

        return editPanel;
    }

    protected RoundedPanel getHeaderPanel(ExpenseCategoryLists expenseCategoryLists) {
        RoundedPanel headerPanel = new RoundedPanel(10);
        headerPanel.setLayout(new GridLayout(1, 3, 0, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        headerPanel.setPreferredSize(new Dimension(categoryViewDialog.getWidth(), 75));
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
        upperGap.setPreferredSize(new Dimension(categoryViewDialog.getWidth(), 10));

        RoundedPanel mainCenterPanel = new RoundedPanel(10);
        mainCenterPanel.setLayout(new BorderLayout());
        mainCenterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 12, 10));
        mainCenterPanel.setOpaque(false);
        mainCenterPanel.setBackground(COLOR_GRAY);
        mainCenterPanel.add(mainInfoHoldingPanel(expenseCategoryLists), BorderLayout.CENTER);

        JPanel bottomGap = new JPanel();
        bottomGap.setOpaque(false);
        bottomGap.setPreferredSize(new Dimension(categoryViewDialog.getWidth(), 10));

        contentPanel.add(upperGap, BorderLayout.NORTH);
        contentPanel.add(mainCenterPanel, BorderLayout.CENTER);
        contentPanel.add(bottomGap, BorderLayout.SOUTH);

        return contentPanel;
    }

    protected JPanel mainInfoHoldingPanel(ExpenseCategoryLists expenseCategoryLists) {
        JPanel infoHoldingPanel = new JPanel(new GridLayout(4, 1, GAP, GAP));
        infoHoldingPanel.setOpaque(false);

        initializeTextValues(expenseCategoryLists);

        infoHoldingPanel.add(createRowTextFields("Expense Code:", expCategoryCode));
        infoHoldingPanel.add(createRowTextFields("Expense Name:", expCategoryName));
        infoHoldingPanel.add(createRowTextFields("Allocation (ThisYear):", expCurrentYearAllocationField));
        infoHoldingPanel.add(createRowTextFields("Active Status:", expUsedActiveInactive));

        return infoHoldingPanel;
    }


    private void initializeTextValues(ExpenseCategoryLists expenseCategoryLists) {
        expCategoryCode.setText(expenseCategoryLists.getExpenseCode());
        expCategoryName.setText(expenseCategoryLists.getExpenseCodeName());
        expUsedActiveInactive.setText(expenseCategoryLists.getExpUsedActiveInactive());
        expCurrentYearAllocationField.setText(expenseCategoryLists.getCurrentYearBudgetAmountString());
    }

    protected JPanel createRowTextFields(String headerName, RoundedTextFieldV2 textField) {
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
        textField.setEnabled(false);
        textField.setBackground(COLOR_WHITE);

        innerLeft.add(headerLabel, BorderLayout.WEST);
        innerRight.add(textField, BorderLayout.CENTER);

        textFieldPanel.add(innerLeft, BorderLayout.WEST);
        textFieldPanel.add(innerRight, BorderLayout.CENTER);
        return textFieldPanel;
    }


    private ImageAvatar initializeEditableFoodImage(ExpenseCategoryLists expenseCategoryLists) {
        avatarForExp = new ImageAvatar();
        avatarForExp.setBackground(COLOR_GRAY);

        ImageIcon foodImageIcon = new ImageIcon(expenseCategoryLists.getIconExpense());
        Image scaledImage = foodImageIcon.getImage().getScaledInstance(65, 65, Image.SCALE_SMOOTH);

        avatarForExp.setPreferredSize(new Dimension(65, 65));  // Desired size
        avatarForExp.setMaximumSize(new Dimension(65, 65));
        avatarForExp.setImage(new ImageIcon(scaledImage));

        return avatarForExp;
    }


    protected RoundedPanel getBottomPanel() {
        RoundedPanel bottomPanel = new RoundedPanel(10);
        bottomPanel.setPreferredSize(new Dimension(categoryViewDialog.getWidth(), 60));
        bottomPanel.setBackground(COLOR_GRAY);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, GAP));
        bottomPanel.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setPreferredSize(new Dimension(100, 45));
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButtonEditPage(), BorderLayout.CENTER);

        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        return bottomPanel;
    }

    private RoundedBorderButton closeButtonEditPage() {
        RoundedBorderButton closeButton = UIAppButtonFactory.creatRoundedBorderButton("Close", "/CloseIcon.svg", COLOR_ORANGE, 20);
        closeButton.addActionListener(e -> categoryViewDialog.dispose());
        return closeButton;
    }

}
