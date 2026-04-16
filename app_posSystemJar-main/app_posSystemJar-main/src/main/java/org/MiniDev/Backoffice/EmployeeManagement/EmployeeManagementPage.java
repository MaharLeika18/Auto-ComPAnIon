package org.MiniDev.Backoffice.EmployeeManagement;

import UI.LinkButton;
import UI.PanelRound;
import UI.RoundedPanel;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.Backoffice.CreateSettingsPanel;
import org.MiniDev.Backoffice.EmployeeManagement.EmployeeListManagement.EmpTableActionPages.EmpEditPage;
import org.MiniDev.Backoffice.EmployeeManagement.EmployeeListManagement.EmpMainPage;
import org.MiniDev.Backoffice.EmployeeManagement.EmployeeListManagement.EmpTableActionPages.EmpNewPage;
import org.MiniDev.Backoffice.EmployeeManagement.EmployeeListManagement.EmpTableActionPages.EmpViewPage;

import javax.swing.*;
import java.awt.*;

import static UI.UserFinalSettingsVar.*;

public class EmployeeManagementPage extends CreateSettingsPanel {

    protected LinkButton[] employeeMenuButtons = new LinkButton[1];
    protected PanelRound[] leftMenuHoldingPanels = new PanelRound[1];
    private LinkButton lastClickedStockMenuButton;
    private PanelRound lastClickedMenuHoldingPanel;
    protected RoundedPanel employeeManagementPanel;
    public static CardLayout employeeMenuCardLayout;
    public static PanelRound menuEmployeeCardHoldingPanel;

    public EmployeeManagementPage() {
    }

    public RoundedPanel employeeManagementPage() {
        employeeManagementPanel = new RoundedPanel(10);
        employeeManagementPanel.setLayout(new BorderLayout());
        employeeManagementPanel.setOpaque(false);

        menuEmployeeCardHoldingPanel = new PanelRound();
        menuEmployeeCardHoldingPanel.setOpaque(false);
        menuEmployeeCardHoldingPanel.setRoundBottomRight(10);
        menuEmployeeCardHoldingPanel.setRoundBottomLeft(10);
        menuEmployeeCardHoldingPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        employeeMenuCardLayout = new CardLayout();
        menuEmployeeCardHoldingPanel.setLayout(employeeMenuCardLayout);

        menuEmployeeCardHoldingPanel.add(new EmpMainPage().createEmpMainPage(), "EMP-MN");
        menuEmployeeCardHoldingPanel.add(new EmpNewPage().createEmpNewPage(), "EMP-NEW");
        menuEmployeeCardHoldingPanel.add(new EmpEditPage().createEmpEditPage(), "EMP-EDT");
        menuEmployeeCardHoldingPanel.add(new EmpViewPage().createEmpViewPage(), "EMP-VIW");

        employeeManagementPanel.add(createHeaderMenuButtonsPanel(), BorderLayout.NORTH);
        employeeManagementPanel.add(menuEmployeeCardHoldingPanel, BorderLayout.CENTER);

        startAnimationDefaultMenu();

        return employeeManagementPanel;
    }

    public void startAnimationDefaultMenu() {
        employeeMenuButtons[0].setForeground(COLOR_ORANGE);
        leftMenuHoldingPanels[0].setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_ORANGE));
        employeeMenuCardLayout.show(menuEmployeeCardHoldingPanel, "EMP-MN");
    }

    private PanelRound getPanelRound() {
        PanelRound headerMenuButtonsPanel = new PanelRound();
        headerMenuButtonsPanel.setLayout(new GridLayout(1, 2, 0, 0));
        headerMenuButtonsPanel.setRoundTopLeft(10);
        headerMenuButtonsPanel.setRoundTopRight(10);
        headerMenuButtonsPanel.setBackground(COLOR_WHITE);
        headerMenuButtonsPanel.setPreferredSize(new Dimension(employeeManagementPanel.getMaximumSize().width, 40));
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
        PanelRound panelRound1 = new PanelRound();
        PanelRound panelRound2 = new PanelRound();

        leftMenuHoldingPanels[0].setRoundTopLeft(10);

        leftMenuHoldingPanels[0].setBackground(Color.WHITE);
//        leftMenuHoldingPanels[1].setBackground(Color.WHITE);
//        leftMenuHoldingPanels[2].setBackground(Color.WHITE);

        leftMenuHoldingPanels[0].setLayout(new BorderLayout());
//        leftMenuHoldingPanels[1].setLayout(new BorderLayout());
//        leftMenuHoldingPanels[2].setLayout(new BorderLayout());

        employeeMenuButtons[0] = new LinkButton("Employee", null);
        employeeMenuButtons[0].setFont(new Font("Noto Sans Myanmar", Font.BOLD, 11));
        employeeMenuButtons[0].setForeground(Color.GRAY);

//        employeeMenuButtons[1] = new LinkButton("Employee", null);
//        employeeMenuButtons[1].setFont(new Font("Noto Sans Myanmar", Font.BOLD, 11));
//        employeeMenuButtons[1].setForeground(Color.GRAY);
//
//        employeeMenuButtons[2] = new LinkButton("Salary", null);
//        employeeMenuButtons[2].setFont(new Font("Noto Sans Myanmar", Font.BOLD, 11));
//        employeeMenuButtons[2].setForeground(Color.GRAY);


        employeeMenuButtons[0].addActionListener(e -> {
            handleButtonActionForEmployeeNavigation(employeeMenuButtons[0], leftMenuHoldingPanels[0]);
            employeeMenuCardLayout.show(menuEmployeeCardHoldingPanel, "EMP-MN");
        });
//
//        employeeMenuButtons[1].addActionListener(e -> {
//            handleButtonActionForEmployeeNavigation(employeeMenuButtons[1], leftMenuHoldingPanels[1]);
//            employeeMenuCardLayout.show(menuEmployeeCardHoldingPanel, "EMP-MN");
//        });
//
//        employeeMenuButtons[2].addActionListener(e -> {
//            handleButtonActionForEmployeeNavigation(employeeMenuButtons[2], leftMenuHoldingPanels[2]);
//            employeeMenuCardLayout.show(menuEmployeeCardHoldingPanel, "SAL-MN");
//        });


        leftMenuHoldingPanels[0].add(employeeMenuButtons[0], BorderLayout.CENTER);
//        leftMenuHoldingPanels[1].add(employeeMenuButtons[1], BorderLayout.CENTER);
//        leftMenuHoldingPanels[2].add(employeeMenuButtons[2], BorderLayout.CENTER);


        leftMain.add(leftMenuHoldingPanels[0]);
        leftMain.add(panelRound1);
        leftMain.add(panelRound2);


        headerMenuButtonsPanel.add(leftMain);
        headerMenuButtonsPanel.add(rightMain);

        return headerMenuButtonsPanel;
    }


    protected void handleButtonActionForEmployeeNavigation(LinkButton button, PanelRound panel) {
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
        for (JButton button : employeeMenuButtons) {
            button.setForeground(Color.gray);
        }
        for (PanelRound panel : leftMenuHoldingPanels) {
            panel.setBorder(BorderFactory.createEmptyBorder());
        }
    }
}
