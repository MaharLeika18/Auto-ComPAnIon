package UI;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

import static UI.UserFinalSettingsVar.*;
import static UI.UserFinalSettingsVar.COLOR_WHITE;
import static org.MiniDev.Cashier.CreateTodaySalePanel.GAP;

public class CustomPopUpFactory {

    public static void showVerifyBeforeItemUpload(JPanel parentPanel, boolean noError, int srCodeErrorCnt,
                                                  int itemNameErrorCnt, int categoryCounterErrorCnt, int numberColumnsErrorCnt) {
        // Create a dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parentPanel), true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.setSize(330, 280);
        dialog.setLocationRelativeTo(null);

        RoundedPanel contentPane = new RoundedPanel(20);
        contentPane.setOpaque(false);
        contentPane.setBackground(Color.decode("#082f49"));
        contentPane.setLayout(new BorderLayout());
        contentPane.setShadowSize(10);
        contentPane.setShadowColor(COLOR_BLACK);
        contentPane.setPreferredSize(new Dimension(dialog.getWidth(), dialog.getHeight()));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBackground(COLOR_ENTRY_GRAY); // Set header panel background to white
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 0, COLOR_LINE_COLOR),
                BorderFactory.createEmptyBorder(GAP, GAP * 2, GAP, GAP * 2)
        ));

        JPanel firstLayer = new JPanel();
        firstLayer.setLayout(new GridLayout(2, 1, 0, 0));
        firstLayer.setPreferredSize(new Dimension(dialog.getWidth(), 90));
        firstLayer.setOpaque(false);

        firstLayer.add(getSuccessFailureIcon(noError));
        firstLayer.add(getSuccessFailureLabel(noError));

        RoundedPanel secondLayer = new RoundedPanel(20);
        secondLayer.setLayout(new GridLayout(4, 1, 1, 1));
        secondLayer.setOpaque(false);
        secondLayer.setBackground(COLOR_ENTRY_GRAY);
        secondLayer.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        secondLayer.add(createRowPanelForEachRows("SerialCode Column", srCodeErrorCnt));
        secondLayer.add(createRowPanelForEachRows("ItemName Column", itemNameErrorCnt));
        secondLayer.add(createRowPanelForEachRows("Category-Counter Columns", categoryCounterErrorCnt));
        secondLayer.add(createRowPanelForEachRows("Number Columns", numberColumnsErrorCnt));

        centerPanel.add(firstLayer, BorderLayout.NORTH);
        centerPanel.add(secondLayer, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setPreferredSize(new Dimension(centerPanel.getWidth(), 50));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 50, GAP, 50));
        bottomPanel.add(getPopUpControlButton(noError, dialog), BorderLayout.CENTER);

        contentPane.add(centerPanel, BorderLayout.CENTER);
        contentPane.add(bottomPanel, BorderLayout.SOUTH);

        // Add content pane to the dialog
        dialog.setContentPane(contentPane);

        // Pack the dialog
        dialog.pack();

        // Use SwingUtilities to set the shape after dialog is packed and made visible
        SwingUtilities.invokeLater(() -> {
            dialog.setShape(new RoundRectangle2D.Double(0, 0, dialog.getWidth(), dialog.getHeight(), 5, 5));
            dialog.setVisible(true); // Show the dialog
        });
    }


    private static JPanel createRowPanelForEachRows(String columnNameText, int columnErrorCnt) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel iconPanel = new JPanel(new BorderLayout());
        JPanel labelPanel = new JPanel(new BorderLayout());

        iconPanel.setOpaque(false);
        labelPanel.setOpaque(false);

        iconPanel.setPreferredSize(new Dimension(15, panel.getHeight()));

        JLabel iconLabel = new JLabel();
        JLabel columnLabel = new JLabel(columnNameText + " " + "[" + columnErrorCnt + "]" + "error");
        columnLabel.setFont(new Font("Noto Sans Myanmar",Font.BOLD,14));

        String iconPathNoError = "/ActiveStatusIcon.svg";
        String iconPathWithError = "/WarningSignIcon.svg";


        if (columnErrorCnt > 0) {
            iconLabel.setIcon(new SvgIcon(iconPathWithError, 12).getImageIcon());

        } else {
            iconLabel.setIcon(new SvgIcon(iconPathNoError, 12).getImageIcon());
        }

        iconPanel.add(iconLabel, BorderLayout.CENTER);
        labelPanel.add(columnLabel, BorderLayout.CENTER);

        panel.add(iconPanel, BorderLayout.WEST);
        panel.add(labelPanel, BorderLayout.CENTER);

        return panel;
    }

    private static JPanel getLicenseWarning() {
        Color fontColor;
        String title;


        title = "WARNING!";
        fontColor = COLOR_ERROR_RED;


        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 16));
        titleLabel.setForeground(fontColor);

        // Create a JPanel with FlowLayout to center the label
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false); // Make the panel transparent
        panel.add(titleLabel); // Add the label to the panel

        return panel;
    }

    private static JPanel getSuccessFailureLabel(boolean noError) {
        Color fontColor;
        String title;
        if (noError) {
            title = "VERIFIED";
            fontColor = COLOR_GREEN;
        } else {
            title = "ERROR!";
            fontColor = COLOR_ERROR_RED;
        }

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 16));
        titleLabel.setForeground(fontColor);

        // Create a JPanel with FlowLayout to center the label
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false); // Make the panel transparent
        panel.add(titleLabel); // Add the label to the panel

        return panel;
    }


    private static JLabel getSuccessFailureIcon(boolean noError) {
        String iconPath;
        if (noError) {
            iconPath = "/SuccessIcon.svg";
        } else {
            iconPath = "/ErrorIcon.svg";
        }

        ImageIcon icon = new SvgIcon(iconPath, 40).getImageIcon();
        JLabel iconLabel = new JLabel(icon);
        return iconLabel;
    }


    private static IconTextButton getPopUpControlButton(boolean noError, JDialog parentDialog) {
        String buttonText;
        Color baseColor;
        if (noError) {
            buttonText = "Continue";
            baseColor = COLOR_GREEN;
        } else {
            buttonText = "Try again";
            baseColor = COLOR_ERROR_RED;
        }
        IconTextButton dialogControlButton = new IconTextButton(buttonText, null, 10, baseColor, 0);
        dialogControlButton.setBackground(baseColor);
        dialogControlButton.setForeground(COLOR_WHITE);
        dialogControlButton.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 14));
        dialogControlButton.setPreferredSize(new Dimension(250, 40));

        dialogControlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentDialog.dispose();
            }
        });

        return dialogControlButton;
    }

    public static void showLicenseInvalidPopUp(JPanel parentPanel) {
        // Create a dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parentPanel), true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.setSize(330, 280);
        dialog.setLocationRelativeTo(null);

        RoundedPanel contentPane = new RoundedPanel(20);
        contentPane.setOpaque(false);
        contentPane.setBackground(Color.decode("#082f49"));
        contentPane.setLayout(new BorderLayout());
        contentPane.setShadowSize(10);
        contentPane.setShadowColor(COLOR_BLACK);
        contentPane.setPreferredSize(new Dimension(dialog.getWidth(), dialog.getHeight()));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBackground(COLOR_ENTRY_GRAY); // Set header panel background to white
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 0, COLOR_LINE_COLOR),
                BorderFactory.createEmptyBorder(GAP, GAP * 2, GAP, GAP * 2)
        ));

        JPanel firstLayer = new JPanel();
        firstLayer.setLayout(new GridLayout(2, 1, 0, 0));
        firstLayer.setPreferredSize(new Dimension(dialog.getWidth(), 90));
        firstLayer.setOpaque(false);

        firstLayer.add(getSuccessFailureIcon(false));
        firstLayer.add(getLicenseWarning());

        RoundedPanel secondLayer = new RoundedPanel(20);
        secondLayer.setLayout(new BoxLayout(secondLayer,BoxLayout.Y_AXIS));
        secondLayer.setOpaque(false);
        secondLayer.setBackground(COLOR_ENTRY_GRAY);
        secondLayer.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

        JLabel label1stLine = new JLabel("Your license code is expired or invalid.");
        JLabel label2ndLine = new JLabel("Contact your application provider.");

        label1stLine.setFont(new Font("Noto Sans Myanmar",Font.BOLD,14));
        label2ndLine.setFont(new Font("Noto Sans Myanmar",Font.BOLD,14));

        secondLayer.add(Box.createVerticalStrut(5));
        secondLayer.add(label1stLine);
        secondLayer.add(Box.createVerticalStrut(5)); // Adds spacing between the two panels
        secondLayer.add(label2ndLine);

        centerPanel.add(firstLayer, BorderLayout.NORTH);
        centerPanel.add(secondLayer, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setPreferredSize(new Dimension(centerPanel.getWidth(), 50));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 50, GAP, 50));
        bottomPanel.add(getContinueButton(dialog), BorderLayout.CENTER);

        contentPane.add(centerPanel, BorderLayout.CENTER);
        contentPane.add(bottomPanel, BorderLayout.SOUTH);

        // Add content pane to the dialog
        dialog.setContentPane(contentPane);

        // Pack the dialog
        dialog.pack();

        // Use SwingUtilities to set the shape after dialog is packed and made visible
        SwingUtilities.invokeLater(() -> {
            dialog.setShape(new RoundRectangle2D.Double(0, 0, dialog.getWidth(), dialog.getHeight(), 5, 5));
            dialog.setVisible(true); // Show the dialog
        });
    }

    private static IconTextButton getContinueButton(JDialog parentDialog) {
        String buttonText;
        Color baseColor;

        buttonText = "Try again";
        baseColor = COLOR_ERROR_RED;

        IconTextButton dialogControlButton = new IconTextButton(buttonText, null, 10, baseColor, 0);
        dialogControlButton.setBackground(baseColor);
        dialogControlButton.setForeground(COLOR_WHITE);
        dialogControlButton.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 14));
        dialogControlButton.setPreferredSize(new Dimension(250, 40));

        dialogControlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentDialog.dispose();
            }
        });

        return dialogControlButton;
    }




}
