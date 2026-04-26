package org.MiniDev.Backoffice.Label;

import UI.*;
import Utils.DrawTextOnBarcodeFactory;
import Utils.NumericDocumentFilter;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.MiniDev.Backoffice.CreateSettingsPanel;
import raven.datetime.component.date.DatePicker;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static UI.UserFinalSettingsVar.*;
import static org.MiniDev.Cashier.CreateTodaySalePanel.GAP;

public class GenerateBarcodeSettings extends CreateSettingsPanel {

    protected static JLabel barcodeLabel1 = new JLabel();
    protected static JLabel barcodeLabel2 = new JLabel();
    protected static JLabel barcodeLabel3 = new JLabel();
    protected static BufferedImage barcodeImage;

    protected String lastClickedPanelName;

    protected RoundedTextFieldV2 currencyField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 widthField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 heightField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 prefixField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 itemNameField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);
    protected RoundedTextFieldV2 priceField = new RoundedTextFieldV2(10, 1, COLOR_LINE_COLOR);


    protected List<String> dateFormatOptions = new ArrayList<>(Arrays.asList("MM/DD/YYYY", "DD. MM. YYYY", "YYYY-MM-DD", "DD/MM/YYYY", "MMM/YYYY"));
    protected RoundedComboBox<String> barcodeTypeCombo = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);
    protected RoundedComboBox<String> dateChooseCombo = new RoundedComboBox<>(10, 1, COLOR_LINE_COLOR);
    protected DatePicker datePicker = new DatePicker();

    public GenerateBarcodeSettings() {

    }

    public JPanel createGenerateBarcodePage() {
        JPanel generateBarcodePanel = new JPanel(new BorderLayout());
        generateBarcodePanel.setOpaque(false);

        // RoundedPanel class (Assumed custom class)
        RoundedPanel mainHoldingPanel = new RoundedPanel(10);
        mainHoldingPanel.setOpaque(false);
        mainHoldingPanel.setBackground(Color.WHITE);
        mainHoldingPanel.setLayout(new BorderLayout());
        mainHoldingPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Left and Right Panels
        JPanel leftCenterPanel = getLeftCenterPanel();
        JPanel rightCenterPanel = getRightPanel(mainHoldingPanel);

        // Create a gap panel
        JPanel gapPanel = new JPanel();
        gapPanel.setOpaque(false);
        gapPanel.setBackground(COLOR_WHITE);
        gapPanel.setPreferredSize(new Dimension(5, 0)); // Set gap width (10px, for example)

        // Add components to the main holding panel
        mainHoldingPanel.add(leftCenterPanel, BorderLayout.CENTER);

        // Create a new container panel for the gap
        JPanel rightPanelContainer = new JPanel();
        rightPanelContainer.setOpaque(false);
        rightPanelContainer.setLayout(new BorderLayout());

        // Add the gap panel in the center and right panel in the east
        rightPanelContainer.add(gapPanel, BorderLayout.WEST);
        rightPanelContainer.add(rightCenterPanel, BorderLayout.EAST);

        // Add the new rightPanelContainer to the main holding panel
        mainHoldingPanel.add(rightPanelContainer, BorderLayout.EAST);

        generateBarcodePanel.add(mainHoldingPanel, BorderLayout.CENTER);
        return generateBarcodePanel;
    }

    public JPanel getLeftCenterPanel() {
        JPanel leftCenterPanel = new JPanel();
        leftCenterPanel.setOpaque(false);
        leftCenterPanel.setBackground(COLOR_WHITE);
        leftCenterPanel.setLayout(new BoxLayout(leftCenterPanel, BoxLayout.Y_AXIS));  // Vertical layout

        // Add the main panel
        leftCenterPanel.add(getLeftMain());

        // Add a vertical gap (use an invisible component with preferred size)
        leftCenterPanel.add(Box.createVerticalStrut(4));

        // Add the bottom panel
        leftCenterPanel.add(getLeftBot());

        return leftCenterPanel;
    }


    // LeftPart
    public JPanel getLeftMain() {
        JPanel leftMain = new JPanel(new GridLayout(2, 1, GAP, GAP));
        leftMain.setOpaque(false);  // Ensure the panel is opaque
        leftMain.add(upperLeft());
        leftMain.add(bottomLeft());
        return leftMain;
    }

    public RoundedPanel upperLeft() {
        RoundedPanel upperLeft = new RoundedPanel(10);
        upperLeft.setOpaque(false);  // Set opaque to true to ensure visibility
        upperLeft.setLayout(new BorderLayout()); // Explicitly set BorderLayout
        upperLeft.setBackground(COLOR_WHITE);
        upperLeft.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        upperLeft.add(getHeaderLabel("Generate Barcode"), BorderLayout.NORTH);
        upperLeft.add(upperHoldingPane(), BorderLayout.CENTER);

        return upperLeft;
    }


    public JPanel upperHoldingPane() {
        JPanel upperHoldingPane = new JPanel(new GridLayout(4, 1, 5, 5));
        upperHoldingPane.setOpaque(false);
        upperHoldingPane.setBorder(BorderFactory.createEmptyBorder(GAP, 5, GAP, 5));

        JPanel prefixPanel = rowTextFieldPanel(false, false, prefixField, "Prefix Code No: ", "eg. 123456789012");
        JPanel namePanel = rowTextFieldPanel(false, false, itemNameField, "Item Name: ", "eg. Coffee");
        JPanel pricePanel = rowTextFieldPanel(false, true, priceField, "Price: ", "eg. 100000");
        JPanel expiredDatePanel = rowDateChooserPanel("Expired Date: ", datePicker);

        upperHoldingPane.add(prefixPanel);
        upperHoldingPane.add(namePanel);
        upperHoldingPane.add(pricePanel);
        upperHoldingPane.add(expiredDatePanel);

        return upperHoldingPane;
    }

    public JPanel rowDateChooserPanel(String textFieldLabels, DatePicker datePicker) {
        // Main panel that holds everything
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setOpaque(false);  // Ensure the panel is opaque
        rowPanel.setBackground(COLOR_WHITE);

        // Left panel to hold the label
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.setPreferredSize(new Dimension(180, 70));
        left.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        left.setOpaque(false);  // Ensure left panel is opaque
        left.setBackground(COLOR_WHITE);

        // Right panel to hold the DatePicker editor
        RoundedPanel right = new RoundedPanel(10);
        right.setOpaque(false);  // Ensure right panel is opaque
        right.setBackground(COLOR_WHITE);
        right.setBorderWidth(1);
        right.setBorderColor(COLOR_LINE_COLOR);
        right.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        // Label for the date picker field
        JLabel textFieldLabel = new JLabel(textFieldLabels);
        textFieldLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 12));

        left.add(textFieldLabel);  // Add label to the left panel

        // DatePicker setup
        datePicker.setDateSelectionMode(DatePicker.DateSelectionMode.SINGLE_DATE_SELECTED);
        datePicker.setUsePanelOption(false);

        // Set up the editor for the DatePicker
        JFormattedTextField editor = new JFormattedTextField();
        editor.setBorder(null);
        datePicker.setEditor(editor);
        datePicker.setBackground(COLOR_WHITE);
        datePicker.setBorder(null);

        // Use GridBagLayout to center the editor
        right.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 0, 0); // No padding
        gbc.anchor = GridBagConstraints.CENTER; // Center the editor


        // Focus listener to change border color on focus
        FocusListener focusListener = new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                right.setBorderColor(COLOR_SELECT_BLUE); // Focused border color
            }

            @Override
            public void focusLost(FocusEvent e) {
                right.setBorderColor(COLOR_LINE_COLOR); // Default border color
            }
        };

        // Add focus listener to the editor and the DatePicker
        editor.addFocusListener(focusListener);
        datePicker.addFocusListener(focusListener);

        right.add(editor, gbc);

        // Add both left and right panels to the main row panel
        rowPanel.add(left, BorderLayout.WEST);
        rowPanel.add(right, BorderLayout.CENTER);

        return rowPanel;
    }


    public JPanel rowComboBoxPanel(RoundedComboBox<String> comboBox, String textFieldLabels, List<String> options, String initString) {
        // Outer panel to hold everything
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setOpaque(false);  // Make sure the background is painted
        rowPanel.setBackground(COLOR_WHITE);

        JPanel left = getInputLeftLabelPanel(textFieldLabels);

        comboBox.setEditable(true);
        comboBox.setEditable(true);

        initializeComboBox(comboBox, options, initString);

        addKeyListenerToComboBox(comboBox, options);

        // Add left and right panels to the rowPanel
        rowPanel.add(left, BorderLayout.WEST);
        rowPanel.add(comboBox, BorderLayout.CENTER);

        return rowPanel;
    }

    private void addKeyListenerToComboBox(RoundedComboBox<String> comboBox, List<String> options) {
        comboBox.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleComboBoxKeyPress(e, comboBox, options);
            }
        });
    }

    private void handleComboBoxKeyPress(KeyEvent e, RoundedComboBox<String> comboBox, List<String> options) {
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            initializeComboBox(comboBox, options,
                    comboBox == dateChooseCombo ? "Default" :
                            comboBox == barcodeTypeCombo ? "Automatically select" : "");
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


    public JPanel rowTextFieldPanel(boolean isFixed, boolean isNumericField, RoundedTextFieldV2 v2TextField, String textFieldLabels, String textFieldPalaceHolder) {
        // Outer panel to hold everything
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setOpaque(false);  // Make sure the background is painted
        rowPanel.setBackground(COLOR_WHITE);

        JPanel left = getInputLeftLabelPanel(textFieldLabels);

        // TextField setup
        if (isNumericField) {
            addNumericKeyListener(v2TextField);
            ((AbstractDocument) v2TextField.getDocument()).setDocumentFilter(new NumericDocumentFilter());
        }
        v2TextField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, textFieldPalaceHolder);
        v2TextField.setHorizontalAlignment(SwingConstants.LEFT);
        v2TextField.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13));
        if (isFixed) {
            v2TextField.setText("Fixed");
            v2TextField.setEditable(false);
            v2TextField.setEnabled(false);
        }

        // Add left and right panels to the rowPanel
        rowPanel.add(left, BorderLayout.WEST);
        rowPanel.add(v2TextField, BorderLayout.CENTER);

        return rowPanel;
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


    private static JPanel getInputLeftLabelPanel(String textFieldLabels) {
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        left.setOpaque(false);  // Make sure it is opaque to show the background color
        left.setPreferredSize(new Dimension(180, 70));

        // Label setup
        JLabel textFieldLabel = new JLabel(textFieldLabels);
        textFieldLabel.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 13));

        left.add(textFieldLabel);  // Add label to the left panel
        return left;
    }


    public RoundedPanel bottomLeft() {
        RoundedPanel bottomLeft = new RoundedPanel(10);
        bottomLeft.setOpaque(false);  // Set opaque to true to ensure visibility
        bottomLeft.setLayout(new BorderLayout()); // Explicitly set BorderLayout
        bottomLeft.setBackground(COLOR_WHITE);
        bottomLeft.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        bottomLeft.add(getHeaderLabel("Enter your label size (in mm)"), BorderLayout.NORTH);
        bottomLeft.add(bottomCenterLeft(), BorderLayout.CENTER);

        return bottomLeft;
    }


    public JPanel bottomCenterLeft() {
        JPanel bottomCenterLeft = new JPanel(new GridLayout(4, 1, 5, 5));
        bottomCenterLeft.setBorder(BorderFactory.createEmptyBorder(GAP, 5, GAP, 5));
        bottomCenterLeft.setOpaque(false);

        JPanel currencySymbolPanel = rowTextFieldPanel(false, false, currencyField, "Currency Symbol: ", "eg. Ks");
        JPanel dateFormatOptionPanel = rowComboBoxPanel(dateChooseCombo, "Date Format: ", dateFormatOptions, "Default");
        JPanel widthPanel = rowTextFieldPanel(true, true, widthField, "Width: ", "Fixed");
        JPanel heightPanel = rowTextFieldPanel(true, true, heightField, "Height: ", "Fixed");

        bottomCenterLeft.add(currencySymbolPanel);
        bottomCenterLeft.add(dateFormatOptionPanel);
        bottomCenterLeft.add(widthPanel);
        bottomCenterLeft.add(heightPanel);

        return bottomCenterLeft;
    }


    public JLabel getHeaderLabel(String headerString) {
        JLabel headerLabel = new JLabel(headerString);
        headerLabel.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 14));
        headerLabel.setHorizontalAlignment(SwingConstants.LEFT); // Left align the text
        return headerLabel;
    }


    public RoundedPanel getLeftBot() {
        RoundedPanel bottomPanel = new RoundedPanel(7);
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setPreferredSize(new Dimension(500, 85));
//        bottomPanel.setBorder(BorderFactory.createEmptyBorder(8, 5, 9, 5));
        bottomPanel.setOpaque(false);
        bottomPanel.setBackground(COLOR_WHITE);

        // Add save button or other components if needed
        JPanel leftInnerBottom = new JPanel(new GridLayout(1, 3, GAP, 0));
        leftInnerBottom.setPreferredSize(new Dimension(300, 85));
        leftInnerBottom.setOpaque(false);


        leftInnerBottom.add(barcodePrintButton());
        leftInnerBottom.add(regenerateBarcodeButton());


        bottomPanel.add(leftInnerBottom, BorderLayout.WEST);
        return bottomPanel;
    }


    //RightPart
    public RoundedPanel getRightPanel(RoundedPanel parentPanel) {
        RoundedPanel rightPanel = new RoundedPanel(10);
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setBackground(COLOR_WHITE);
        rightPanel.setPreferredSize(new Dimension(480, parentPanel.getHeight()));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        rightPanel.add(getHeaderLabel("Choose Your Label Style"), BorderLayout.NORTH);
        rightPanel.add(getRightCenter(), BorderLayout.CENTER);

        return rightPanel;
    }


    public JPanel getRightCenter() {
        JPanel rightCenter = new JPanel(new GridLayout(3, 1, GAP, GAP));
        rightCenter.setOpaque(false);
        rightCenter.setBackground(COLOR_WHITE);
        rightCenter.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        RoundedPanel first = createOrdinaryPanel(20, 2, 2, 2);
        RoundedPanel second = createOrdinaryPanel(15, 2, 2, 2);
        RoundedPanel third = createOrdinaryPanel(15, 2, 2, 2);

        first.setName("First");
        second.setName("Second");
        third.setName("Third");

        first.add(barcodeLabel1);
        second.add(barcodeLabel2);
        third.add(barcodeLabel3);

        rightCenter.add(first);
        rightCenter.add(second);
        rightCenter.add(third);

        // Add mouse click event listeners to panels
        addPanelClickListener(first, second, third);
        addPanelClickListener(second, first, third);
        addPanelClickListener(third, first, second);


        // Start the animation on first panel (optional, based on your logic)
        startAnimationBorder(first);
        return rightCenter;
    }

    // This method will handle adding mouse listeners and optimizing the repainting
    private void addPanelClickListener(RoundedPanel clickedPanel, RoundedPanel... others) {
        clickedPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                panelClickEvent(clickedPanel, others);
            }
        });
    }

    public RoundedPanel createOrdinaryPanel(int top, int left, int bot, int right) {
        RoundedPanel panel = new RoundedPanel(15);
        panel.setOpaque(false);
        panel.setBackground(COLOR_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(top, left, bot, right));
        panel.setBorderWidth(1);
        panel.setBorderColor(COLOR_LINE_COLOR);
        return panel;
    }


    public void panelClickEvent(RoundedPanel thisPanel, RoundedPanel... others) {
        // Avoid unnecessary repaints on others if no changes are made
        for (RoundedPanel panel : others) {
            if (panel.getBorderColor() != COLOR_LINE_COLOR) {  // Check if the border color is not default
                panel.setBorderWidth(1);  // Reset border width to default
                panel.setBorderColor(COLOR_LINE_COLOR);  // Reset border color to default
            }
        }

        // Start animation on the clicked panel
        startAnimationBorder(thisPanel);
    }

    public void startAnimationBorder(RoundedPanel startPanel) {
        startPanel.setBorderWidth(1);  // Increase border width for animation effect
        startPanel.setBorderColor(COLOR_GREEN);  // Set border color to green (active state)
        startPanel.repaint();  // Repaint only the clicked panel to avoid full re-layout

        lastClickedPanelName = startPanel.getName();
    }

    private RoundedBorderButton regenerateBarcodeButton() {
        RoundedBorderButton regenButton = UIAppButtonFactory.creatRoundedBorderButton("Regenerate", "/Regen.svg", COLOR_ORANGE, 20);
        regenButton.addActionListener(e -> {
            generateBarcode(prefixField.getText());
        });
        return regenButton;
    }

    private IconTextButton barcodePrintButton() {
        IconTextButton printButton = UIAppButtonFactory.createIconTextButton("Print", "/PrintIcon.svg", COLOR_BLUE);
        printButton.addActionListener(e -> {
            printSelectedBarcode(lastClickedPanelName);
        });
        return printButton;
    }

    private void printSelectedBarcode(String whichPanel) {
        // Determine which label to print based on the selected panel
        JLabel selectedLabel = null;

        if (whichPanel.equals("First")) {
            selectedLabel = barcodeLabel1;
        } else if (whichPanel.equals("Second")) {
            selectedLabel = barcodeLabel2;
        } else if (whichPanel.equals("Third")) {
            selectedLabel = barcodeLabel3;
        }

        if (selectedLabel != null) {
            // Create a PrinterJob
            PrinterJob printerJob = getBarcodePrinterJOB(selectedLabel);

            // Show print dialog for selecting a printer
            boolean printAccepted = printerJob.printDialog();
            if (printAccepted) {
                try {
                    printerJob.print();
                } catch (PrinterException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "No barcode label selected to print.");
        }
    }

    private static PrinterJob getBarcodePrinterJOB(JLabel selectedLabel) {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setPrintable((g, pageFormat, pageIndex) -> {
            if (pageIndex >= 1) {
                return Printable.NO_SUCH_PAGE;
            }

            // Graphics object to print the barcode image
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            // Get the ImageIcon from the selected label
            Icon icon = selectedLabel.getIcon();
            if (icon != null) {
                Image image = ((ImageIcon) icon).getImage();
                g2d.drawImage(image, 0, 0, null);
            }

            return Printable.PAGE_EXISTS;
        });
        return printerJob;
    }


    private void generateBarcode(String randomWord) {
        if (randomWord != null) {
            try {
                // Generate barcode
                BitMatrix bitMatrix = new MultiFormatWriter().encode(randomWord, BarcodeFormat.CODE_128, 300, 120);
                barcodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

                LocalDate selectedDate = datePicker.getSelectedDate();
                LocalDate issuedDate = LocalDate.now();
                String selectedFormat = dateChooseCombo.getSelectedItem().toString();
                String expiredDateString;
                String issuedDateString = "";

                // Check if selectedDate is null
                if (selectedDate == null) {
                    expiredDateString = ""; // Fallback for null dates
                    issuedDateString = issuedDate.toString();
                } else {
                    DateTimeFormatter formatter;
                    switch (selectedFormat) {
                        case "Default":
                            expiredDateString = datePicker.getSelectedDateAsString(); // Assuming this returns a default string
                            issuedDateString = issuedDate.toString(); // Default format
                            break;
                        case "MM/DD/YYYY":
                            formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                            expiredDateString = selectedDate.format(formatter);
                            issuedDateString = issuedDate.format(formatter);
                            break;
                        case "DD. MM. YYYY":
                            formatter = DateTimeFormatter.ofPattern("dd. MM. yyyy");
                            expiredDateString = selectedDate.format(formatter);
                            issuedDateString = issuedDate.format(formatter);
                            break;
                        case "YYYY-MM-DD":
                            formatter = DateTimeFormatter.ISO_LOCAL_DATE;
                            expiredDateString = selectedDate.format(formatter);
                            issuedDateString = issuedDate.format(formatter);
                            break;
                        case "DD/MM/YYYY":
                            formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                            expiredDateString = selectedDate.format(formatter);
                            issuedDateString = issuedDate.format(formatter);
                            break;
                        case "MMM/YYYY":
                            formatter = DateTimeFormatter.ofPattern("MMM/yyyy");
                            expiredDateString = selectedDate.format(formatter);
                            issuedDateString = issuedDate.format(formatter);
                            break;
                        default:
                            expiredDateString = "";
                            issuedDateString = issuedDate.toString(); // Default to ISO format for issued date
                            break;
                    }
                }


                // Draw text on barcode using high-resolution rendering
                BufferedImage finalBarcodeWithText1 = DrawTextOnBarcodeFactory.drawTextOnBarcodeStyle1(barcodeImage, randomWord);
                BufferedImage finalBarcodeWithText2 = DrawTextOnBarcodeFactory.drawTextOnBarcodeStyle2(currencyField.getText(), barcodeImage, itemNameField.getText(), priceField.getText(), issuedDateString, expiredDateString);
                BufferedImage finalBarcodeWithText3 = DrawTextOnBarcodeFactory.drawTextOnBarcodeStyle3(barcodeImage, itemNameField.getText(), randomWord);


                liveChangesForBarcodeTextField(randomWord);

                // Set barcode image with text to the label
                barcodeLabel1.setIcon(new ImageIcon(finalBarcodeWithText1));
                barcodeLabel1.repaint();

                barcodeLabel2.setIcon(new ImageIcon(finalBarcodeWithText2));
                barcodeLabel2.repaint();

                barcodeLabel3.setIcon(new ImageIcon(finalBarcodeWithText3));
                barcodeLabel3.repaint();


            } catch (WriterException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error generating barcode: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "No words generated.");
        }
    }

    private void liveChangesForBarcodeTextField(String randomWord) {
        prefixField.setText(randomWord);
        prefixField.repaint();
    }


}