package UI;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class MacOSScrollPane {

    // Custom UI for macOS-like scroll bar
    public static class MacOSScrollBarUI extends BasicScrollBarUI {

        protected static final int THUMB_WIDTH = 4; // Width of the scrollbar thumb
        protected static final Color THUMB_COLOR = Color.LIGHT_GRAY; // Scrollbar thumb color
        protected static final int THUMB_CORNER_RADIUS = 2; // Corner radius for rounded thumb
        private final Color trackColor;

        // Constructor to accept trackColor
        public MacOSScrollBarUI(Color trackColor) {
            this.trackColor = trackColor;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            g.setColor(trackColor);
            g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            try {
                int thumbHeight = thumbBounds.height; // Use the actual height of the thumb
                g2.setColor(THUMB_COLOR);

                // Enable anti-aliasing for smoother edges
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw rounded rectangle for the thumb
                int arc = THUMB_CORNER_RADIUS * 2;
                g2.fillRoundRect(thumbBounds.x, thumbBounds.y, THUMB_WIDTH, thumbHeight, arc, arc);
            } finally {
                g2.dispose();
            }
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void setThumbBounds(int x, int y, int width, int height) {
            super.setThumbBounds(x, y, THUMB_WIDTH, height);
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            return new Dimension(THUMB_WIDTH, Integer.MAX_VALUE);
        }
    }
}
