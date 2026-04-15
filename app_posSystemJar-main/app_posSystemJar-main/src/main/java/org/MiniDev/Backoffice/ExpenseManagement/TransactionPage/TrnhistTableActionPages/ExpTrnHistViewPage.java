
package org.MiniDev.Backoffice.ExpenseManagement.TransactionPage.TrnhistTableActionPages;

import UI.*;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.Home.MiniDevPOS;
import org.MiniDev.OOP.ExpenseTransactionLists;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;

import static UI.UserFinalSettingsVar.*;
import static org.MiniDev.Cashier.CreateTodaySalePanel.GAP;

public class ExpTrnHistViewPage {

    public static JDialog expViewDialog;
    protected RoundedTextFieldV2 expCatField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 expTitleField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 expRequestAmountField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 expRefundAmountField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 assignedToEmpField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);

    protected EditableAttachmentImage editableAttachmentImageForView;

    public ExpTrnHistViewPage() {
    }

    public void showExpHistPanel(ExpenseTransactionLists expTrnLists) {
        expViewDialog = new JDialog();
        expViewDialog.setUndecorated(true); // Remove window decorations for custom rounding
        expViewDialog.setBackground(new Color(0, 0, 0, 0));
        expViewDialog.setSize(500, 680);
        expViewDialog.setModal(true); // Make the dialog modal
        expViewDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        RoundedOptionPane.setRoundedCorners(expViewDialog, 0);
        expViewDialog.setLayout(new BorderLayout());

        // Add the panel to the dialog
        expViewDialog.getContentPane().add(getEditHoldingPanel(expTrnLists));
        expViewDialog.pack(); // Resize the dialog to fit the content
        expViewDialog.setLocationRelativeTo(MiniDevPOS.contentPanel); // Center the dialog on the screen

        // Set the shape for rounded corners
        expViewDialog.setShape(new RoundRectangle2D.Double(0, 0, expViewDialog.getWidth(), expViewDialog.getHeight(), 0, 0));

        // Add fade-in animation
        DialogFadeIn.fadeIn(expViewDialog);

        expViewDialog.setVisible(true); // Show the dialog
    }

    protected RoundedPanel getEditHoldingPanel(ExpenseTransactionLists expTrnLists) {
        // Create the rounded panel with a custom rounded border
        RoundedPanel editPanel = new RoundedPanel(20);
        editPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the content
        editPanel.setPreferredSize(new Dimension(expViewDialog.getWidth(), expViewDialog.getHeight()));
        editPanel.setBackground(COLOR_ENTRY_GRAY);
        editPanel.setLayout(new BorderLayout());

        editPanel.add(getHeaderPanel(expTrnLists.getExpUsedID(), expTrnLists.getExpName()), BorderLayout.NORTH);
        editPanel.add(getContentPanel(expTrnLists), BorderLayout.CENTER);
        editPanel.add(getBottomPanel(), BorderLayout.SOUTH);

        return editPanel;
    }

    protected RoundedPanel getHeaderPanel(String expUsedID, String expUsedCategory) {
        RoundedPanel headerPanel = new RoundedPanel(10);
        headerPanel.setLayout(new GridLayout(1, 2, 0, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        headerPanel.setPreferredSize(new Dimension(expViewDialog.getWidth(), 65));
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
        upperGap.setPreferredSize(new Dimension(expViewDialog.getWidth(), 10));

        RoundedPanel mainCenterPanel = new RoundedPanel(10);
        mainCenterPanel.setLayout(new BorderLayout());
        mainCenterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 12, 10));
        mainCenterPanel.setOpaque(false);
        mainCenterPanel.setBackground(COLOR_GRAY);
        mainCenterPanel.add(mainInfoHoldingPanel(expTrnLists), BorderLayout.CENTER);

        JPanel bottomGap = new JPanel();
        bottomGap.setOpaque(false);
        bottomGap.setPreferredSize(new Dimension(expViewDialog.getWidth(), 10));

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

        initializeTextValues(initializedExpTitle, initializedRequestAmountText, initializedRefundAmountText, initializedExpCategory, initializedAssignedToEmpName);

        upperHoldingPanel.add(createRowTextFields("Expense Category:", expCatField));
        upperHoldingPanel.add(createRowTextFields("Request Title:", expTitleField));
        upperHoldingPanel.add(createRowTextFields("Request Amount:", expRequestAmountField));
        upperHoldingPanel.add(createRowTextFields("Refund Amount:", expRefundAmountField));
        upperHoldingPanel.add(createRowTextFields("Assigned To:", assignedToEmpField));

        return upperHoldingPanel;
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
        textField.setBackground(COLOR_WHITE);
        textField.setFocusable(true);
        textField.setEditable(false);
        textField.setEnabled(false);

        innerLeft.add(headerLabel, BorderLayout.WEST);
        innerRight.add(textField, BorderLayout.CENTER);

        textFieldPanel.add(innerLeft, BorderLayout.WEST);
        textFieldPanel.add(innerRight, BorderLayout.CENTER);
        return textFieldPanel;
    }


    private void initializeTextValues(String initializedExpTitle, String initializedRequestAmountText,
                                      String initializedRefundAmountText, String initializedExpCategory,
                                      String initializedAssignedToEmpName) {
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
        expCatField.setText(initializedExpCategory);
        assignedToEmpField.setText(initializedAssignedToEmpName);
    }


    protected JPanel getLowerHoldingPanel(ExpenseTransactionLists expTrnList) {
        JPanel lowerHoldingPanel = new JPanel(new BorderLayout());
        lowerHoldingPanel.setOpaque(false);
        lowerHoldingPanel.setBorder(BorderFactory.createEmptyBorder(GAP, 0, 0, 0));
        lowerHoldingPanel.setPreferredSize(new Dimension(expViewDialog.getWidth(), 240));

        JPanel leftLower = new JPanel(new BorderLayout());
        JPanel rightLower = new JPanel(new BorderLayout());

        leftLower.setOpaque(false);
        rightLower.setOpaque(false);

        leftLower.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));

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

        editableAttachmentImageForView = new EditableAttachmentImage();
        editableAttachmentImageForView.setNewBytes(expTrnList.getAttachmentImage());

        fileUploadMainPanel.add(getRightCenterPanel(editableAttachmentImageForView), BorderLayout.CENTER);
        fileUploadMainPanel.add(getRightBottomPanel(editableAttachmentImageForView), BorderLayout.SOUTH);

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
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        bottomPanel.setOpaque(false);
        bottomPanel.setPreferredSize(new Dimension(600, 30));

        bottomPanel.add(getPreviewSelectedImage(editableAttachmentImageForEdit));
        bottomPanel.add(getDownloadFilesButton(editableAttachmentImageForEdit));
        return bottomPanel;
    }

    private RoundedBorderButton getPreviewSelectedImage(EditableAttachmentImage editableAttachmentImageForEdit) {
        RoundedBorderButton previewSelectedButton = UIAppButtonFactory.creatRoundedBorderButton(
                "View", "/ImageViewerIcon.svg", COLOR_FONT_GRAY, 20);
        previewSelectedButton.setHoverBackgroundColor(Color.LIGHT_GRAY);
        previewSelectedButton.setPressedBackgroundColor(COLOR_PANEL_GRAY);
        previewSelectedButton.setPreferredSize(new Dimension(60, 35));
        previewSelectedButton.addActionListener(e -> editableAttachmentImageForEdit.showFullView());
        return previewSelectedButton;
    }

    // Get the download button
    private RoundedBorderButton getDownloadFilesButton(EditableAttachmentImage editableAttachmentImageForEdit) {
        RoundedBorderButton downloadFilesButton = UIAppButtonFactory.creatRoundedBorderButton(
                "Download", "/DownloadFileIcon.svg", COLOR_FONT_GRAY, 20);
        downloadFilesButton.setHoverBackgroundColor(Color.LIGHT_GRAY);
        downloadFilesButton.setPressedBackgroundColor(COLOR_PANEL_GRAY);
        downloadFilesButton.setPreferredSize(new Dimension(60, 35));
        downloadFilesButton.addActionListener(e -> editableAttachmentImageForEdit.downloadImage());
        return downloadFilesButton;
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


    protected RoundedPanel getBottomPanel() {
        RoundedPanel bottomPanel = new RoundedPanel(10);
        bottomPanel.setPreferredSize(new Dimension(expViewDialog.getWidth(), 60));
        bottomPanel.setBackground(COLOR_GRAY);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, GAP));
        bottomPanel.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setPreferredSize(new Dimension(100, 45));
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButtonViewPage(), BorderLayout.CENTER);

        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        return bottomPanel;
    }

    private RoundedBorderButton closeButtonViewPage() {
        RoundedBorderButton closeButton = UIAppButtonFactory.creatRoundedBorderButton("Close", "/CloseIcon.svg", COLOR_ORANGE, 20);
        closeButton.addActionListener(e -> expViewDialog.dispose());
        return closeButton;
    }


}
