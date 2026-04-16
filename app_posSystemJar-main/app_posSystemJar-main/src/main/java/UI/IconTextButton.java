package UI;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static UI.IconCreator.createResizedIcon;
import static UI.UserFinalSettingsVar.COLOR_ORANGE;
import static UI.UserFinalSettingsVar.COLOR_WHITE;

public class IconTextButton extends JButton {
    public IconTextButton(String text, Icon icon, int radius, Color borderColor, int thickness) {
        super(text, icon);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(true);
        setForeground(Color.WHITE);
        setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12));
        setHorizontalAlignment(SwingConstants.LEFT); // Align text and icon to the left
        setVerticalAlignment(SwingConstants.CENTER);
        setPreferredSize(new Dimension(150, 40)); // Adjust size as needed
        setUI(new IconTextButtonUI(radius, borderColor, thickness));
        setIconTextGap(5); // Adjust gap between icon and text
        setHorizontalTextPosition(SwingConstants.RIGHT); // Text on the right of the icon
        setVerticalTextPosition(SwingConstants.CENTER); // Center text vertically
    }

    static class IconTextButtonUI extends BasicButtonUI {
        private final Color borderColor;
        private final int radius;
        private final int thickness;

        public IconTextButtonUI(int radius, Color borderColor, int thickness) {
            this.radius = radius;
            this.borderColor = borderColor;
            this.thickness = thickness;
        }

        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            JButton button = (JButton) c;
            button.addMouseListener(new ButtonMouseListener(button));
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            AbstractButton button = (AbstractButton) c;

            // Paint the background
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color backgroundColor = button.getBackground();

            // Check the button state
            if (button.getModel().isPressed()) {
                // Darker background when pressed
                backgroundColor = backgroundColor.darker();
            } else if (button.getModel().isRollover() || button.getModel().isArmed()) {
                // Slightly darker background on rollover or armed (pressed)
                backgroundColor = backgroundColor.brighter();
            } else {
                // Default background color for normal state
                backgroundColor = button.getBackground();
            }

            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), radius, radius);

            // Paint the border
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, radius, radius);

            // Paint the icon and text
            Icon icon = button.getIcon();
            String text = button.getText();
            FontMetrics fm = g2.getFontMetrics();

            if (icon != null) {
                int iconX = (c.getWidth() - (icon.getIconWidth() + fm.stringWidth(text) + button.getIconTextGap())) / 2;
                int iconY = (c.getHeight() - icon.getIconHeight()) / 2;
                icon.paintIcon(c, g2, iconX, iconY);
            }

            if (text != null) {
                int textX = (c.getWidth() - fm.stringWidth(text) - (icon != null ? (icon.getIconWidth() + button.getIconTextGap()) : 0)) / 2;
                int textY = (c.getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.setColor(button.getForeground());
                g2.setFont(button.getFont());
                g2.drawString(text, textX + (icon != null ? (icon.getIconWidth() + button.getIconTextGap()) : 0), textY);
            }

            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            AbstractButton button = (AbstractButton) c;
            FontMetrics fm = button.getFontMetrics(button.getFont());
            int iconWidth = button.getIcon() != null ? button.getIcon().getIconWidth() : 0;
            int iconHeight = button.getIcon() != null ? button.getIcon().getIconHeight() : 0;
            int textWidth = fm.stringWidth(button.getText());
            int textHeight = fm.getHeight();

            int width = iconWidth + textWidth + button.getIconTextGap() + 20; // Add padding
            int height = Math.max(iconHeight, textHeight) + 20; // Add padding

            return new Dimension(width, height);
        }

        static class ButtonMouseListener implements MouseListener {
            private final JButton button;

            public ButtonMouseListener(JButton button) {
                this.button = button;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // No action needed here
            }
        }
    }

    public static IconTextButton createIconOrangeButton(String svgPath, int baseLineSizeSvg, Color pressedColor, String buttonText, int radius) {
        ImageIcon icon = createResizedIcon(svgPath, baseLineSizeSvg, pressedColor);
        IconTextButton iconTextButton = new IconTextButton(buttonText, icon, radius, pressedColor, 0);
        iconTextButton.setBackground(COLOR_ORANGE);
        iconTextButton.setForeground(COLOR_WHITE);
        iconTextButton.setFont(new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12));
        iconTextButton.setPreferredSize(new Dimension(130, 38)); // Adjusted size for visibility


        iconTextButton.setIcon(icon);
        iconTextButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        iconTextButton.setVerticalTextPosition(SwingConstants.CENTER);
        iconTextButton.setHorizontalAlignment(SwingConstants.CENTER);
        iconTextButton.setVerticalAlignment(SwingConstants.CENTER);

        return iconTextButton;
    }
}
