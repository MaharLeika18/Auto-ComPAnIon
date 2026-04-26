package org.MiniDev.Backoffice.ExpenseManagement;

import UI.LinkButton;
import UI.PanelRound;
import UI.RoundedPanel;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.Backoffice.CreateSettingsPanel;
import org.MiniDev.Backoffice.ExpenseManagement.CategoryPage.CategoryPage;
import org.MiniDev.Backoffice.ExpenseManagement.SummaryPage.SummaryPage;
import org.MiniDev.Backoffice.ExpenseManagement.TransactionPage.NewExpTransactionPage;
import org.MiniDev.Backoffice.ExpenseManagement.TransactionPage.TransactionPage;


import javax.swing.*;
import java.awt.*;

import static UI.UserFinalSettingsVar.*;

public class ExpenseManagementPage extends CreateSettingsPanel {

    protected LinkButton[] expenseMenuButtons = new LinkButton[3];
    protected PanelRound[] leftMenuHoldingPanels = new PanelRound[3];
    private LinkButton lastClickedExpenseMenuButton;
    private PanelRound lastClickedMenuHoldingPanel;
    protected RoundedPanel expenseManagementPanel;
    public static CardLayout expenseMenuCardLayout;
    public static PanelRound menuExpenseCardHoldingPanel;

    public ExpenseManagementPage() {
    }

    public RoundedPanel createEmployeeManagementPage() {
        expenseManagementPanel = new RoundedPanel(10);
        expenseManagementPanel.setLayout(new BorderLayout());
        expenseManagementPanel.setOpaque(false);

        menuExpenseCardHoldingPanel = new PanelRound();
        menuExpenseCardHoldingPanel.setOpaque(false);
        menuExpenseCardHoldingPanel.setRoundBottomRight(10);
        menuExpenseCardHoldingPanel.setRoundBottomLeft(10);
        menuExpenseCardHoldingPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        expenseMenuCardLayout = new CardLayout();
        menuExpenseCardHoldingPanel.setLayout(expenseMenuCardLayout);

        menuExpenseCardHoldingPanel.add(new SummaryPage().createExpenseSummaryPage(), "EXP-SUM");
        menuExpenseCardHoldingPanel.add(new TransactionPage().createExpTransactionPage(), "EXP-TRN");
        menuExpenseCardHoldingPanel.add(new CategoryPage().createCategoryMainPage(), "EXP-CAT");
        menuExpenseCardHoldingPanel.add(new NewExpTransactionPage().createNewExpEntryPanel(), "EXP-NEW");

        expenseManagementPanel.add(createHeaderMenuButtonsPanel(), BorderLayout.NORTH);
        expenseManagementPanel.add(menuExpenseCardHoldingPanel, BorderLayout.CENTER);

        startAnimationDefaultMenu();

        return expenseManagementPanel;
    }

    public void startAnimationDefaultMenu() {
        expenseMenuButtons[0].setForeground(COLOR_ORANGE);
        leftMenuHoldingPanels[0].setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_ORANGE));
        expenseMenuCardLayout.show(menuExpenseCardHoldingPanel, "EXP-SUM");
    }

    private PanelRound getPanelRound() {
        PanelRound headerMenuButtonsPanel = new PanelRound();
        headerMenuButtonsPanel.setLayout(new GridLayout(1, 2, 0, 0));
        headerMenuButtonsPanel.setRoundTopLeft(10);
        headerMenuButtonsPanel.setRoundTopRight(10);
        headerMenuButtonsPanel.setBackground(COLOR_WHITE);
        headerMenuButtonsPanel.setPreferredSize(new Dimension(expenseManagementPanel.getMaximumSize().width, 40));
        headerMenuButtonsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 0, 0, 0),
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_LINE_COLOR)
        ));
        return headerMenuButtonsPanel;
    }


    protected PanelRound createHeaderMenuButtonsPanel() {
        PanelRound headerMenuButtonsPanel = getPanelRound();

        PanelRound leftMain = new PanelRound();
        PanelRound rightMain = new PanelRound();

        leftMain.setLayout(new GridLayout(1, 3));

        leftMain.setOpaque(false);
        rightMain.setOpaque(false);

        leftMenuHoldingPanels[0] = new PanelRound();
        leftMenuHoldingPanels[1] = new PanelRound();
        leftMenuHoldingPanels[2] = new PanelRound();

        leftMenuHoldingPanels[0].setRoundTopLeft(10);

        leftMenuHoldingPanels[0].setBackground(Color.WHITE);
        leftMenuHoldingPanels[1].setBackground(Color.WHITE);
        leftMenuHoldingPanels[2].setBackground(Color.WHITE);

        leftMenuHoldingPanels[0].setLayout(new BorderLayout());
        leftMenuHoldingPanels[1].setLayout(new BorderLayout());
        leftMenuHoldingPanels[2].setLayout(new BorderLayout());

        expenseMenuButtons[0] = new LinkButton("Summary", null);
        expenseMenuButtons[0].setFont(new Font("Noto Sans Myanmar", Font.BOLD, 11));
        expenseMenuButtons[0].setForeground(Color.GRAY);

        expenseMenuButtons[1] = new LinkButton("Transaction", null);
        expenseMenuButtons[1].setFont(new Font("Noto Sans Myanmar", Font.BOLD, 11));
        expenseMenuButtons[1].setForeground(Color.GRAY);

        expenseMenuButtons[2] = new LinkButton("Category", null);
        expenseMenuButtons[2].setFont(new Font("Noto Sans Myanmar", Font.BOLD, 11));
        expenseMenuButtons[2].setForeground(Color.GRAY);


        expenseMenuButtons[0].addActionListener(e -> {
            handleButtonActionForExpenseNavigation(expenseMenuButtons[0], leftMenuHoldingPanels[0]);
            expenseMenuCardLayout.show(menuExpenseCardHoldingPanel, "EXP-SUM");
        });

        expenseMenuButtons[1].addActionListener(e -> {
            handleButtonActionForExpenseNavigation(expenseMenuButtons[1], leftMenuHoldingPanels[1]);
            expenseMenuCardLayout.show(menuExpenseCardHoldingPanel, "EXP-TRN");
        });

        expenseMenuButtons[2].addActionListener(e -> {
            handleButtonActionForExpenseNavigation(expenseMenuButtons[2], leftMenuHoldingPanels[2]);
            expenseMenuCardLayout.show(menuExpenseCardHoldingPanel, "EXP-CAT");
        });


        leftMenuHoldingPanels[0].add(expenseMenuButtons[0], BorderLayout.CENTER);
        leftMenuHoldingPanels[1].add(expenseMenuButtons[1], BorderLayout.CENTER);
        leftMenuHoldingPanels[2].add(expenseMenuButtons[2], BorderLayout.CENTER);

        leftMain.add(leftMenuHoldingPanels[0]);
        leftMain.add(leftMenuHoldingPanels[1]);
        leftMain.add(leftMenuHoldingPanels[2]);

        headerMenuButtonsPanel.add(leftMain);
        headerMenuButtonsPanel.add(rightMain);

        return headerMenuButtonsPanel;
    }


    protected void handleButtonActionForExpenseNavigation(LinkButton button, PanelRound panel) {
        resetColorsForMenuNavigationButtons();

        if (lastClickedExpenseMenuButton != null && lastClickedExpenseMenuButton != button) {
            resetColorsForMenuNavigationButtons();
        }

        if (lastClickedMenuHoldingPanel != null && lastClickedMenuHoldingPanel != panel) {
            resetColorsForMenuNavigationButtons();
        }

        lastClickedExpenseMenuButton = button;
        lastClickedMenuHoldingPanel = panel;

        button.setForeground(COLOR_ORANGE);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_ORANGE));
    }


    private void resetColorsForMenuNavigationButtons() {
        for (JButton button : expenseMenuButtons) {
            button.setForeground(Color.gray);
        }
        for (PanelRound panel : leftMenuHoldingPanels) {
            panel.setBorder(BorderFactory.createEmptyBorder());
        }
    }
}
