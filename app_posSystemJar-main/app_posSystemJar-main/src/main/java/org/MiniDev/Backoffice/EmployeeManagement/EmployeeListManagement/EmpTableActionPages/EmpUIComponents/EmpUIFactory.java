package org.MiniDev.Backoffice.EmployeeManagement.EmployeeListManagement.EmpTableActionPages.EmpUIComponents;

import UI.*;
import Utils.NumericDocumentFilter;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.MiniDev.Backoffice.EmployeeManagement.EmployeeListManagement.EmpTableActionPages.EmpNewPage;
import org.MiniDev.Backoffice.EmployeeManagement.EmployeeManagementPage;
import org.MiniDev.Customer.CreateCustomerPanel;
import raven.datetime.component.date.DatePicker;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import static UI.IconCreator.createResizedIcon;
import static UI.UserFinalSettingsVar.*;

public class EmpUIFactory {

    public static JPanel getFieldPanel(String fieldLabel, RoundedTextFieldV2 field, String placeHolder, boolean isNumericField, boolean isEditOff) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 2, 2));
        panel.setOpaque(false);
        panel.setBackground(COLOR_WHITE);

        JLabel label = new JLabel(fieldLabel);
        panel.add(label);
        panel.add(getEmpTextField(field, placeHolder, isNumericField, isEditOff));

        return panel;
    }

    public static JPanel getPasswordField(String fieldLabel, RoundedPasswordField passwordField, String placeHolder, boolean isNumericField, boolean isEnabled) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 2, 2));
        panel.setOpaque(false);
        panel.setBackground(COLOR_WHITE);

        JLabel label = new JLabel(fieldLabel);
        panel.add(label);
        panel.add(getEmpPassField(passwordField, placeHolder, isNumericField, isEnabled));

        return panel;
    }

    public static JPanel getDateFieldPanel(String fieldLabel, DatePicker datePicker, JFormattedTextField editor, boolean isEditOff) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 2, 2));
        panel.setOpaque(false);
        panel.setBackground(COLOR_WHITE);

        JLabel label = new JLabel(fieldLabel);
        panel.add(label);
        panel.add(getDateChooserField(datePicker, editor, isEditOff));

        return panel;
    }

    public static JPanel getComboFieldPanel(String fieldLabel, RoundedComboBox<String> combo, List<String> optionString, boolean isEditOff) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 2, 2));
        panel.setOpaque(false);
        panel.setBackground(COLOR_WHITE);

        JLabel label = new JLabel(fieldLabel);
        panel.add(label);
        panel.add(getCombo(combo, optionString, isEditOff));

        return panel;
    }

    public static JPanel getNewAvatarPanel(EditableAvatar newAvatar) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBackground(COLOR_WHITE);

        newAvatar.setImage(CreateCustomerPanel.getProfilePictureDefault());

        panel.add(newAvatar, BorderLayout.CENTER);

        return panel;
    }

    public static RoundedTextFieldV2 getEmpTextField(RoundedTextFieldV2 field, String placeHolder, boolean isNumericField, boolean isEditOff) {
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeHolder);
        field.setHorizontalAlignment(SwingConstants.LEFT);
        field.setEnabled(!isEditOff);

        if (isNumericField) {
            addNumericKeyListener(field);
            ((AbstractDocument) field.getDocument()).setDocumentFilter(new NumericDocumentFilter());
        }
        return field;
    }

    public static RoundedPasswordField getEmpPassField(RoundedPasswordField passwordField, String placeHolder, boolean isNumericField, boolean isEnabled) {
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeHolder);
        passwordField.setHorizontalAlignment(SwingConstants.LEFT);
        passwordField.setEnabled(isEnabled);

        if (isNumericField) {
            addNumericKeyListenerForPasswordField(passwordField);
            ((AbstractDocument) passwordField.getDocument()).setDocumentFilter(new NumericDocumentFilter());
        }
        return passwordField;
    }

    public static RoundedComboBox<String> getCombo(RoundedComboBox<String> combo, List<String> optionString, boolean isEditOff) {
        combo.setEditable(true);
        combo.setEnabled(!isEditOff);
        initializeComboBox(combo, optionString);
        addKeyListenerToComboBox(combo, optionString);
        return combo;
    }

    private static void addKeyListenerToComboBox(RoundedComboBox<String> comboBox, List<String> optionString) {
        comboBox.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleComboBoxKeyPress(e, comboBox, optionString);
            }
        });
    }

    private static void handleComboBoxKeyPress(KeyEvent e, RoundedComboBox<String> comboBox, List<String> optionString) {
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            initializeComboBox(comboBox, optionString);
        }
    }

    private static void initializeComboBox(RoundedComboBox<String> comboBox, List<String> items) {
        String[] itemArray = items.toArray(new String[0]);
        comboBox.setModel(new DefaultComboBoxModel<>(itemArray));
        comboBox.setPlaceholder("- Select -");
        comboBox.setSelectedItem("- Select -");

        JTextField editorField = (JTextField) comboBox.getEditor().getEditorComponent();
        if (editorField != null) {
            editorField.setForeground(Color.GRAY); // Set initial placeholder color
        }
    }

    private static void addNumericKeyListener(RoundedTextFieldV2 textField) {
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

    private static void addNumericKeyListenerForPasswordField(RoundedPasswordField textField) {
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


    public static RoundedPanel getDateChooserField(DatePicker datePicker, JFormattedTextField editor, boolean isEditOff) {

        // Right panel to hold the DatePicker editor
        RoundedPanel right = new RoundedPanel(10);
        right.setOpaque(false);  // Ensure right panel is opaque
        right.setBackground(COLOR_WHITE);
        right.setBorderWidth(1);
        right.setBorderColor(COLOR_LINE_COLOR);
        right.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));


        // DatePicker setup
        datePicker.setDateSelectionMode(DatePicker.DateSelectionMode.SINGLE_DATE_SELECTED);
        datePicker.setUsePanelOption(false);
        datePicker.setEnabled(!isEditOff);

        // Set up the editor for the DatePicker
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


        return right;
    }


    public static JButton getCreateNewEmpButton(IconTextButton button, Color color, Runnable action) {
        button.setBackground(color);
        button.setForeground(COLOR_WHITE);
        button.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12));
        button.setPreferredSize(new Dimension(120, 40));

        button.addActionListener(e -> {
            action.run();
        });

        return button;
    }

    public static ImageIcon UPLOAD_BACKEND_IMG = createSmoothImageIcon(120);

    private static ImageIcon createSmoothImageIcon(int size) {
        SvgIcon svgIcon = new SvgIcon("/ProfileUploadIcon.svg", size);

        // Retrieve the image from SvgIcon
        Image image = svgIcon.getImageIcon().getImage();

        // Smooth the image for rendering
        Image smoothImage = image.getScaledInstance(size, size, Image.SCALE_SMOOTH);

        // Return an ImageIcon for the smooth image
        return new ImageIcon(smoothImage);
    }


    public static EditableFoodImage getAddNewImageAvatar(EditableFoodImage imageAvatar) {
        imageAvatar.setOpaque(false);
        imageAvatar.setBorderSize(0);
        imageAvatar.setArcSize(10);
        imageAvatar.setFillRect(true);
        imageAvatar.setAutoResizing(false); // Ensure the image is not resized automatically

        // Set explicit size for the image avatar
        Dimension preferredSize = new Dimension(96, 120);  // Desired size
        imageAvatar.setPreferredSize(preferredSize);
        imageAvatar.setMaximumSize(preferredSize);
        imageAvatar.setImage(UPLOAD_BACKEND_IMG);

        return imageAvatar;
    }


    public static RoundedIconButton getCloseThisWindowButt() {
        ImageIcon closeImgIcon = new SvgIcon("/CloseThisWindow.svg", 22).getImageIcon();
        RoundedIconButton button = new RoundedIconButton(closeImgIcon, 14, COLOR_ENTRY_GRAY, 0);
        button.setPreferredSize(new Dimension(38, 38));
        button.setBackground(COLOR_ENTRY_GRAY);
        button.addActionListener(e -> {
            EmployeeManagementPage.employeeMenuCardLayout.show(EmployeeManagementPage.menuEmployeeCardHoldingPanel, "EMP-MN");
            EmpNewPage.clearAllInputFields();
        });
        return button;
    }


    public static IconTextButton getExcelDownloadButton(String buttonName, String iconPath, int iconBaseLineSize,
                                                        Color buttonBaseColor, Color foreFontColor, int width, int height
    ) {
        ImageIcon icon = createResizedIcon(iconPath, iconBaseLineSize, null);
        IconTextButton button = new IconTextButton(buttonName, icon, 14, buttonBaseColor, 0);
        button.setBackground(buttonBaseColor);
        button.setForeground(foreFontColor);
        button.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, 11));
        button.setPreferredSize(new Dimension(width, height)); // Adjusted size for visibility

        // Set icon on the left side of the button text
        button.setIcon(icon);
        // Set the text and icon alignment
        button.setHorizontalTextPosition(SwingConstants.RIGHT); // Icon on the left
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);


        return button;
    }

}
