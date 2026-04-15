package UI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class RoundedJList extends JList<String> {
    private int radius;

    public RoundedJList(DefaultListModel<String> model, int radius) {
        super(model);
        this.radius = radius;
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Optional padding
        setOpaque(false); // Make it transparent to show rounded background
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Enable anti-aliasing for smooth corners
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set background color
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        super.paintComponent(g);
    }

    @Override
    public void paint(Graphics g) {
        // Set the clipping region to create rounded corners
        g.setClip(0, 0, getWidth(), getHeight());
        super.paint(g);
    }

    @Override
    public void setBorder(Border border) {
        // Override to prevent changing the border if necessary
    }
}
