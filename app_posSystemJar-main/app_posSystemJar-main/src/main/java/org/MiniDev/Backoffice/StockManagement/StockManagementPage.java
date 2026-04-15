package org.MiniDev.Backoffice.StockManagement;


import UI.LinkButton;
import UI.PanelRound;
import UI.RoundedPanel;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.Backoffice.CreateSettingsPanel;
import org.MiniDev.Backoffice.StockManagement.StockTableActionPages.ItemBulkImport;
import org.MiniDev.Backoffice.StockManagement.NewEntryPage.NewItemEntryPage;

import javax.swing.*;
import java.awt.*;

import static UI.UserFinalSettingsVar.*;

public class StockManagementPage extends CreateSettingsPanel {
    protected LinkButton[] stockMenuButtons = new LinkButton[2];
    protected PanelRound[] leftMenuHoldingPanels = new PanelRound[2];
    protected RoundedPanel stocksManagementPanel;
    private LinkButton lastClickedStockMenuButton;
    private PanelRound lastClickedMenuHoldingPanel;
    public static CardLayout stockMenuCardLayout;
    public static PanelRound mainMenuCardHoldingPanel;

    public StockManagementPage() {
    }

    public RoundedPanel stocksManagementPage() {
        stocksManagementPanel = new RoundedPanel(10);
        stocksManagementPanel.setLayout(new BorderLayout());
        stocksManagementPanel.setOpaque(false);

        mainMenuCardHoldingPanel = new PanelRound();
        mainMenuCardHoldingPanel.setOpaque(false);
        mainMenuCardHoldingPanel.setRoundBottomRight(10);
        mainMenuCardHoldingPanel.setRoundBottomLeft(10);
        mainMenuCardHoldingPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        stockMenuCardLayout = new CardLayout();
        mainMenuCardHoldingPanel.setLayout(stockMenuCardLayout);

        mainMenuCardHoldingPanel.add(new StockInnerPage().createStockInnerPage(), "StockInnerPage");
        mainMenuCardHoldingPanel.add(new CounterInnerPage().createCounterInnerPage(), "CounterInnerPage");
        mainMenuCardHoldingPanel.add(new NewItemEntryPage().createNewItemEntryPagePanel(), "NewItemEntryPage");
        mainMenuCardHoldingPanel.add(new ItemBulkImport().createItemBulkPage(), "StockExcelUpload");

        stocksManagementPanel.add(createHeaderMenuButtonsPanel(), BorderLayout.NORTH);
        stocksManagementPanel.add(mainMenuCardHoldingPanel, BorderLayout.CENTER);

        startAnimationDefaultMenu();

        return stocksManagementPanel;
    }


    protected PanelRound createHeaderMenuButtonsPanel() {
        PanelRound headerMenuButtonsPanel = getPanelRound();

        PanelRound leftMain = new PanelRound();
        PanelRound centerMain = new PanelRound();
        PanelRound rightMain = new PanelRound();

        leftMain.setLayout(new GridLayout(1, 3));

        leftMain.setOpaque(false);
        rightMain.setOpaque(false);

        leftMenuHoldingPanels[0] = new PanelRound();
        leftMenuHoldingPanels[1] = new PanelRound();

        leftMenuHoldingPanels[0].setRoundTopLeft(10);

        leftMenuHoldingPanels[0].setBackground(Color.WHITE);
        leftMenuHoldingPanels[1].setBackground(Color.WHITE);
        leftMenuHoldingPanels[0].setLayout(new BorderLayout());
        leftMenuHoldingPanels[1].setLayout(new BorderLayout());

        stockMenuButtons[0] = new LinkButton("Stock Management", null);
        stockMenuButtons[0].setFont(new Font("Noto Sans Myanmar", Font.BOLD, 11));
        stockMenuButtons[0].setForeground(Color.GRAY);
        stockMenuButtons[1] = new LinkButton("Counter Management", null);
        stockMenuButtons[1].setFont(new Font("Noto Sans Myanmar", Font.BOLD, 11));
        stockMenuButtons[1].setForeground(Color.GRAY);

        stockMenuButtons[0].addActionListener(e -> {
            handleButtonActionForStockNavigation(stockMenuButtons[0], leftMenuHoldingPanels[0]);
            stockMenuCardLayout.show(mainMenuCardHoldingPanel, "StockInnerPage");
        });
        stockMenuButtons[1].addActionListener(e -> {
            handleButtonActionForStockNavigation(stockMenuButtons[1], leftMenuHoldingPanels[1]);
            stockMenuCardLayout.show(mainMenuCardHoldingPanel, "CounterInnerPage");
        });

        leftMenuHoldingPanels[0].add(stockMenuButtons[0], BorderLayout.CENTER);
        leftMenuHoldingPanels[1].add(stockMenuButtons[1], BorderLayout.CENTER);

        leftMain.add(leftMenuHoldingPanels[0]);
        leftMain.add(leftMenuHoldingPanels[1]);

        headerMenuButtonsPanel.add(leftMain);
        headerMenuButtonsPanel.add(centerMain);
        headerMenuButtonsPanel.add(rightMain);

        return headerMenuButtonsPanel;
    }

    public void startAnimationDefaultMenu() {
        stockMenuButtons[0].setForeground(COLOR_ORANGE);
        leftMenuHoldingPanels[0].setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_ORANGE));
        stockMenuCardLayout.show(mainMenuCardHoldingPanel, "StockInnerPage");
    }

    private PanelRound getPanelRound() {
        PanelRound headerMenuButtonsPanel = new PanelRound();
        headerMenuButtonsPanel.setLayout(new GridLayout(1, 2, 0, 0));
        headerMenuButtonsPanel.setRoundTopLeft(10);
        headerMenuButtonsPanel.setRoundTopRight(10);
        headerMenuButtonsPanel.setBackground(COLOR_WHITE);
        headerMenuButtonsPanel.setPreferredSize(new Dimension(stocksManagementPanel.getMaximumSize().width, 40));
        headerMenuButtonsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 0, 0, 0),
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_LINE_COLOR)
        ));
        return headerMenuButtonsPanel;
    }


    protected void handleButtonActionForStockNavigation(LinkButton button, PanelRound panel) {
        resetColorsForMenuNavigationButtons();

        if (lastClickedStockMenuButton != null && lastClickedStockMenuButton != button) {
            resetColorsForMenuNavigationButtons();
        }

        if (lastClickedMenuHoldingPanel != null && lastClickedMenuHoldingPanel != panel) {
            resetColorsForMenuNavigationButtons();
        }

        lastClickedStockMenuButton = button;
        lastClickedMenuHoldingPanel = panel;

        button.setForeground(COLOR_ORANGE);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_ORANGE));
    }


    private void resetColorsForMenuNavigationButtons() {
        for (JButton button : stockMenuButtons) {
            button.setForeground(Color.gray);
        }
        for (PanelRound panel : leftMenuHoldingPanels) {
            panel.setBorder(BorderFactory.createEmptyBorder());
        }
    }

}
