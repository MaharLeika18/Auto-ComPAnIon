package org.MiniDev.Home.SubPopUpWindows.ProfileClickedUICom;

import UI.*;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

import javax.swing.*;
import java.awt.*;

import static UI.IconCreator.createResizedIcon;
import static UI.UserFinalSettingsVar.*;
import static org.MiniDev.Cashier.CreateTodaySalePanel.GAP;

public class ProfileClickedUICom {

    public static JPanel getPasswordResetField(String fieldLabel, RoundedPasswordField passwordField, String placeHolder) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        panel.setBackground(COLOR_WHITE);

        JLabel label = new JLabel(fieldLabel);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createHorizontalGlue()); // Add space after components
        panel.add(getPasswordField(passwordField, placeHolder));

        return panel;
    }

    public static RoundedPasswordField getPasswordField(RoundedPasswordField passwordField, String placeHolderName) {
        passwordField.setHorizontalAlignment(SwingConstants.LEFT);
        passwordField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true");
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeHolderName);
        passwordField.setPreferredSize(new Dimension(240, 38));
        passwordField.setMaximumSize(new Dimension(240, 38));
        passwordField.setMinimumSize(new Dimension(240, 38));
        return passwordField;
    }


    public static JPanel getProfileField(String fieldLabel, RoundedTextFieldV2 field, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        panel.setBackground(COLOR_WHITE);

        JLabel label = new JLabel(fieldLabel);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createHorizontalGlue()); // Add space after components
        panel.add(getProfileField(field, value));

        return panel;
    }


    public static RoundedTextFieldV2 getProfileField(RoundedTextFieldV2 field, String value) {
        field.setHorizontalAlignment(SwingConstants.LEFT);
        field.setText(value);
        field.setEditable(false);
        field.setEnabled(false);
        field.setPreferredSize(new Dimension(240, 38));
        field.setMaximumSize(new Dimension(240, 38));
        field.setMinimumSize(new Dimension(240, 38));
        return field;
    }


    public static ImageAvatar getCurrentImgAvatar(ImageAvatar imageAvatar, byte[] currentByteImg) {
        imageAvatar.setOpaque(false);
        imageAvatar.setBorderSize(0);
        imageAvatar.setArcSize(10);
        imageAvatar.setFillRect(true);
        imageAvatar.setAutoResizing(false); // Ensure the image is not resized automatically

        Dimension preferredSize = new Dimension(120, 150);  // Desired size
        imageAvatar.setPreferredSize(preferredSize);
        imageAvatar.setMaximumSize(preferredSize);
        imageAvatar.setImage(currentByteImg);

        return imageAvatar;
    }

    public static JPanel getCurrentUserImagePanel(String currentCustomerName, ImageAvatar imageAvatar, byte[] currentByte) {
        JLabel currentUserName = new JLabel(currentCustomerName);
        currentUserName.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 14));
        currentUserName.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the label

        // Create a panel to hold the image and label
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS)); // Vertical stacking
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        top.setPreferredSize(new Dimension(200, 180));

        // Add components to the top panel
        top.add(ProfileClickedUICom.getCurrentImgAvatar(imageAvatar, currentByte));
        top.add(Box.createVerticalStrut(GAP)); // Add some spacing between the image and the label
        top.add(currentUserName);

        return top;
    }

    public static RoundedIconButton getCloseThisWindowButt(JDialog thisDialog) {
        ImageIcon closeImgIcon = new SvgIcon("/CloseThisWindow.svg", 22).getImageIcon();
        RoundedIconButton button = new RoundedIconButton(closeImgIcon, 14, COLOR_GRAY, 0);
        button.setPreferredSize(new Dimension(38, 38));
        button.setBackground(COLOR_GRAY);

        button.addActionListener(e -> {
            thisDialog.setVisible(false);
            thisDialog.dispose();
        });

        return button;
    }

    public static IconTextButton getSubmitButton() {
        ImageIcon updateIcon = createResizedIcon("/UpdateIcon.svg", 20, null);
        IconTextButton updateButton = new IconTextButton("Submit", updateIcon, 14, (COLOR_BUTTON_BLUE), 0);
        updateButton.setBackground(COLOR_BUTTON_BLUE);
        updateButton.setForeground(COLOR_WHITE);
        updateButton.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));
        updateButton.setPreferredSize(new Dimension(100, 38));

        updateButton.setIcon(updateIcon);

        updateButton.setHorizontalTextPosition(SwingConstants.RIGHT); // Icon on the left
        updateButton.setVerticalTextPosition(SwingConstants.CENTER);
        updateButton.setHorizontalAlignment(SwingConstants.CENTER);
        updateButton.setVerticalAlignment(SwingConstants.CENTER);
        return updateButton;
    }

}
