package UI;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class RoundedIconButton extends JButton {

    public RoundedIconButton(Icon icon, int radius, Color color, int thickness) {
        super(icon);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(true);
        setPreferredSize(new Dimension(100, 100));
        setUI(new IconOnlyButtonUI(radius, color, thickness));
    }

    static class IconOnlyButtonUI extends BasicButtonUI {
        private final Color borderColor;
        private final int radius;
        private final int thickness;

        public IconOnlyButtonUI(int radius, Color borderColor, int thickness) {
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

            if (button.getModel().isPressed()) {
                backgroundColor = backgroundColor.darker(); // Darker background when pressed
            } else if (button.getModel().isRollover()) {
                backgroundColor = backgroundColor.darker();
            }

            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), radius, radius);

            // Paint the border
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, radius, radius);

            // Calculate the icon position
            Icon icon = button.getIcon();

            if (icon != null) {
                int iconX = (c.getWidth() - icon.getIconWidth()) / 2;
                int iconY = (c.getHeight() - icon.getIconHeight()) / 2;
                icon.paintIcon(c, g2, iconX, iconY);
            }

            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            AbstractButton button = (AbstractButton) c;
            int iconWidth = button.getIcon() != null ? button.getIcon().getIconWidth() : 0;
            int iconHeight = button.getIcon() != null ? button.getIcon().getIconHeight() : 0;

            int width = iconWidth + 20; // Add padding
            int height = iconHeight + 20; // Add padding

            return new Dimension(width, height);
        }

        private static class ButtonMouseListener implements MouseListener {
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
}
