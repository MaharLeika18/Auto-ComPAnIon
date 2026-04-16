package UI;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LinkButton extends JButton {

    private String originalText;

    public LinkButton(String text, Icon icon) {
        super(text);
        originalText = text;
        setIcon(icon);  // Set the icon
        initialize();
    }

    private void initialize() {
        // Customize the appearance
        setForeground(Color.decode("#FC8019"));  // Set link color
        setBorderPainted(false);                // Remove border
        setFocusPainted(false);                 // Remove focus border
        setContentAreaFilled(false);            // Make background transparent
        setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12)); // Set font
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Hand cursor

        // Adding hover effect (underline on hover)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setText("<html><u>" + originalText + "</u></html>"); // Add underline
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setText(originalText); // Remove underline
            }
        });
    }
}
