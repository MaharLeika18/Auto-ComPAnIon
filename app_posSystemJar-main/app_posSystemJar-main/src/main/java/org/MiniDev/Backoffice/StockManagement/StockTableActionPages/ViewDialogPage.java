package org.MiniDev.Backoffice.StockManagement.StockTableActionPages;

import UI.*;
import Utils.DrawTextOnBarcodeFactory;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import static UI.RoundedImagePanel.DEFAULT_IMAGE_BYTES;
import static UI.UserFinalSettingsVar.*;
import static org.MiniDev.Cashier.CreateTodaySalePanel.GAP;

public class ViewDialogPage {
    protected JDialog dialog;
    protected RoundedTextFieldV2 itemNameField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 categoryField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 sellPriceField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 buyPriceField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 stockCntField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 counterField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 promoCentField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextArea descriptionField;
    protected BufferedImage barcodeImageView;
    protected JLabel barcodeLabelView = new JLabel();

    public ViewDialogPage() {
    }

    public void showViewPanel(byte[] foodImage, String barcodeText, String itemName, String categoryName,
                              String sellPriceText, String buyPriceText, String stockCnt, String counterName, String promoCentText, String textAreaText) {
        dialog = new JDialog();
        dialog.setUndecorated(true); // Remove window decorations for custom rounding
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.setSize(500, 680);
        dialog.setModal(true); // Make the dialog modal
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        RoundedOptionPane.setRoundedCorners(dialog, 0);
        dialog.setLayout(new BorderLayout());

        // Add the panel to the dialog
        dialog.getContentPane().add(getViewingHoldingPanel(foodImage, barcodeText, itemName, categoryName, sellPriceText, buyPriceText, stockCnt, counterName, promoCentText, textAreaText));
        dialog.pack(); // Resize the dialog to fit the content
        dialog.setLocationRelativeTo(null); // Center the dialog on the screen

        // Set the shape for rounded corners
        dialog.setShape(new RoundRectangle2D.Double(0, 0, dialog.getWidth(), dialog.getHeight(), 0, 0));

        // Add fade-in animation
        DialogFadeIn.fadeIn(dialog);

        dialog.setVisible(true); // Show the dialog
    }

    protected RoundedPanel getViewingHoldingPanel(byte[] foodImage, String barcodeText, String itemName, String categoryName, String sellPriceText, String buyPriceText, String stockCnt, String counterName, String promoCentText, String textAreaText) {
        // Create the rounded panel with a custom rounded border
        RoundedPanel viewPanel = getViewPanel();

        viewPanel.add(getHeaderPanel(foodImage, barcodeText), BorderLayout.NORTH);
        viewPanel.add(getContentPanel(itemName, categoryName, sellPriceText, buyPriceText, stockCnt, counterName, promoCentText, textAreaText), BorderLayout.CENTER);
        viewPanel.add(getBottomPanel(), BorderLayout.SOUTH);


        return viewPanel;
    }

    private RoundedPanel getViewPanel() {
        RoundedPanel viewPanel = new RoundedPanel(20);
        viewPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the content
        viewPanel.setPreferredSize(new Dimension(dialog.getWidth(), dialog.getHeight()));
        viewPanel.setBackground(COLOR_ENTRY_GRAY);
        viewPanel.setLayout(new BorderLayout());
        viewPanel.setBorderWidth(1);
        viewPanel.setBorderColor(COLOR_PANEL_BORDER_SILVER);
        return viewPanel;
    }

    protected JLabel barcodePanelViewLabel(String barcodeText) throws WriterException {
        if (barcodeText != null || !barcodeText.isEmpty()) {
            barcodeLabelView.removeAll();
            barcodeLabelView.repaint();
        }
        BitMatrix bitMatrix = new MultiFormatWriter().encode(barcodeText, BarcodeFormat.CODE_128, 150, 60);
        barcodeImageView = MatrixToImageWriter.toBufferedImage(bitMatrix);

        BufferedImage finalBarcodeWithText = DrawTextOnBarcodeFactory.drawTextOnBarcodeStyle1(barcodeImageView, barcodeText);

        barcodeLabelView.setIcon(new ImageIcon(finalBarcodeWithText));
        barcodeLabelView.repaint();

        return barcodeLabelView;
    }

    private ImageAvatar getViewImage(byte[] foodImage) {
        ImageAvatar imageAvatar = new ImageAvatar();
        imageAvatar.setBackground(COLOR_GRAY);
        imageAvatar.setOpaque(false);
        imageAvatar.setBorderSize(0);
        imageAvatar.setArcSize(10);
        imageAvatar.setFillRect(true);
        imageAvatar.setAutoResizing(false); // Ensure the image is not resized automatically

        if (foodImage == null || foodImage.length == 0) {
            foodImage = DEFAULT_IMAGE_BYTES; // Ensure this is valid image data
        }

        ImageIcon foodImageIcon = new ImageIcon(foodImage);
        Image scaledImage = foodImageIcon.getImage().getScaledInstance(65, 65, Image.SCALE_SMOOTH);

        imageAvatar.setPreferredSize(new Dimension(65, 65));  // Desired size
        imageAvatar.setMaximumSize(new Dimension(65, 65));
        imageAvatar.setImage(new ImageIcon(scaledImage));

        return imageAvatar;
    }


    protected RoundedPanel getHeaderPanel(byte[] foodImage, String barcodeText) {
        RoundedPanel headerPanel = new RoundedPanel(10);
        headerPanel.setLayout(new GridLayout(1, 3, 0, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        headerPanel.setPreferredSize(new Dimension(dialog.getWidth(), 75));
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
        right.setPreferredSize(new Dimension(165, 75));

        mid.add(getViewImage(foodImage), BorderLayout.CENTER);
        try {
            left.add(barcodePanelViewLabel(barcodeText), BorderLayout.WEST);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }

        headerPanel.add(left);
        headerPanel.add(mid);
        headerPanel.add(right);


        return headerPanel;
    }


    protected JPanel getContentPanel(String itemName, String categoryName, String sellPriceText, String buyPriceText, String stockCnt, String counterName, String promoCentText, String textAreaText) {
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout());

        JPanel upperGap = new JPanel();
        upperGap.setOpaque(false);
        upperGap.setPreferredSize(new Dimension(dialog.getWidth(), 10));

        RoundedPanel mainCenterPanel = new RoundedPanel(10);
        mainCenterPanel.setLayout(new BorderLayout());
        mainCenterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 12, 10));
        mainCenterPanel.setOpaque(false);
        mainCenterPanel.setBackground(COLOR_GRAY);
        mainCenterPanel.add(mainInfoHoldingPanel(itemName, categoryName, sellPriceText, buyPriceText, stockCnt, counterName, promoCentText, textAreaText), BorderLayout.CENTER);

        JPanel bottomGap = new JPanel();
        bottomGap.setOpaque(false);
        bottomGap.setPreferredSize(new Dimension(dialog.getWidth(), 10));

        contentPanel.add(upperGap, BorderLayout.NORTH);
        contentPanel.add(mainCenterPanel, BorderLayout.CENTER);
        contentPanel.add(bottomGap, BorderLayout.SOUTH);

        return contentPanel;
    }


    protected JPanel mainInfoHoldingPanel(String itemName, String categoryName, String sellPriceText, String buyPriceText, String stockCnt, String counterName, String promoCentText, String textAreaText) {
        JPanel mainInfoPanel = new JPanel();
        mainInfoPanel.setOpaque(false);
        mainInfoPanel.setLayout(new BorderLayout());
        mainInfoPanel.add(getUpperHoldingPanel(itemName, categoryName, sellPriceText, buyPriceText, stockCnt, counterName, promoCentText), BorderLayout.CENTER);
        mainInfoPanel.add(getLowerHoldingPanel(textAreaText), BorderLayout.SOUTH);

        return mainInfoPanel;
    }


    protected JPanel getUpperHoldingPanel(String itemName, String categoryName, String sellPriceText, String buyPriceText, String stockCnt, String counterName, String promoCentText) {
        JPanel upperHoldingPanel = new JPanel(new GridLayout(7, 1, GAP, GAP));
        upperHoldingPanel.setOpaque(false);
        initializeShowingFields();
        initializeTextValues(itemName, categoryName, sellPriceText, buyPriceText, stockCnt, counterName, promoCentText);

        upperHoldingPanel.add(createRowTextFields("Item Name:", itemNameField));
        upperHoldingPanel.add(createRowTextFields("Item Category:", categoryField));
        upperHoldingPanel.add(createRowTextFields("Sell Price:", sellPriceField));
        upperHoldingPanel.add(createRowTextFields("Original Price:", buyPriceField));
        upperHoldingPanel.add(createRowTextFields("Stock Available:", stockCntField));
        upperHoldingPanel.add(createRowTextFields("Counter Name:", counterField));
        upperHoldingPanel.add(createRowTextFields("Promotion Percentage:", promoCentField));


        return upperHoldingPanel;
    }


    protected RoundedTextArea createDescriptionField() {
        int borderRadius = 10;

        descriptionField = new RoundedTextArea(borderRadius, COLOR_LINE_COLOR,200);
        descriptionField.setBackground(COLOR_WHITE);
        descriptionField.setEditable(false);
        descriptionField.setFocusable(false);
        descriptionField.setRows(4);

        // Optionally set line wrap and word wrap
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);

        // Optionally set padding within the text area
        descriptionField.setMargin(new Insets(0, 0, 0, 0));

        return descriptionField;
    }

    protected JPanel getLowerHoldingPanel(String textAreaText) {
        JPanel lowerHoldingPanel = new JPanel(new BorderLayout());
        lowerHoldingPanel.setOpaque(false);
        lowerHoldingPanel.setBorder(BorderFactory.createEmptyBorder(GAP, 0, 0, 0));
        lowerHoldingPanel.setPreferredSize(new Dimension(dialog.getWidth(), 120));

        JPanel leftLower = new JPanel(new BorderLayout());
        JPanel rightLower = new JPanel(new BorderLayout());

        leftLower.setOpaque(false);
        rightLower.setOpaque(false);

        leftLower.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        leftLower.setPreferredSize(new Dimension(140, lowerHoldingPanel.getHeight()));

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13));

        rightLower.add(createDescriptionField(), BorderLayout.CENTER);
        leftLower.add(descriptionLabel, BorderLayout.NORTH);

        lowerHoldingPanel.add(leftLower, BorderLayout.WEST);
        lowerHoldingPanel.add(rightLower, BorderLayout.CENTER);

        initializeTextAreaValues(textAreaText);

        return lowerHoldingPanel;
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

        innerLeft.add(headerLabel, BorderLayout.WEST);
        innerRight.add(textField, BorderLayout.CENTER);

        textFieldPanel.add(innerLeft, BorderLayout.WEST);
        textFieldPanel.add(innerRight, BorderLayout.CENTER);
        return textFieldPanel;
    }

    private void initializeTextValues(String itemName, String categoryName, String sellPriceText, String buyPriceText, String stockCntText, String counterText, String promoCentText) {
        itemNameField.setText(itemName);
        categoryField.setText(categoryName);
        sellPriceField.setText(sellPriceText);
        buyPriceField.setText(buyPriceText);
        stockCntField.setText(stockCntText);
        counterField.setText(counterText);
        promoCentField.setText(promoCentText);
    }

    private void initializeTextAreaValues(String textAreaText) {
        descriptionField.setText(textAreaText);
    }


    private void initializeShowingFields() {
        itemNameField.setHorizontalAlignment(SwingConstants.LEFT);
        categoryField.setHorizontalAlignment(SwingConstants.LEFT);
        sellPriceField.setHorizontalAlignment(SwingConstants.LEFT);
        buyPriceField.setHorizontalAlignment(SwingConstants.LEFT);
        stockCntField.setHorizontalAlignment(SwingConstants.LEFT);
        counterField.setHorizontalAlignment(SwingConstants.LEFT);
        promoCentField.setHorizontalAlignment(SwingConstants.LEFT);

        // Set editable to false and make non-focusable
        itemNameField.setEditable(false);
        categoryField.setEditable(false);
        sellPriceField.setEditable(false);
        buyPriceField.setEditable(false);
        stockCntField.setEditable(false);
        counterField.setEditable(false);
        promoCentField.setEditable(false);

        // Prevent focus and cursor from appearing
        itemNameField.setFocusable(false);
        categoryField.setFocusable(false);
        sellPriceField.setFocusable(false);
        buyPriceField.setFocusable(false);
        stockCntField.setFocusable(false);
        counterField.setFocusable(false);
        promoCentField.setFocusable(false);

        // Set background colors
        itemNameField.setBackground(COLOR_WHITE);
        categoryField.setBackground(COLOR_WHITE);
        sellPriceField.setBackground(COLOR_WHITE);
        buyPriceField.setBackground(COLOR_WHITE);
        stockCntField.setBackground(COLOR_WHITE);
        counterField.setBackground(COLOR_WHITE);
        promoCentField.setBackground(COLOR_WHITE);
    }


    protected RoundedPanel getBottomPanel() {
        RoundedPanel bottomPanel = new RoundedPanel(10);
        bottomPanel.setPreferredSize(new Dimension(dialog.getWidth(), 60));
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
        RoundedBorderButton closeButton = UIAppButtonFactory.creatRoundedBorderButton("Close", "/CloseIcon.svg",COLOR_ORANGE,20);
        closeButton.addActionListener(e -> {
            dialog.dispose();
        });
        return closeButton;
    }


}
