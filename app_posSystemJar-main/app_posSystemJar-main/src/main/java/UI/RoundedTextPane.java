package UI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import static UI.UserFinalSettingsVar.COLOR_LINE_COLOR;

public class RoundedTextPane extends JTextPane {

    private int cornerRadius;

    public RoundedTextPane(int radius) {
        this.cornerRadius = radius;
        setOpaque(false);  // Ensure transparency around the corners
        setBorder(new EmptyBorder(10, 10, 10, 10));  // Add padding
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // Enable anti-aliasing for smooth edges
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set background color and draw the rounded rectangle
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        // Clip the content to the rounded shape
        g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

        // Call the superclass's paintComponent to draw the text
        super.paintComponent(g2);

        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // Enable anti-aliasing for smooth border edges
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set the border color and thickness
        g2.setColor(COLOR_LINE_COLOR);
        g2.setStroke(new BasicStroke(0));  // Thicker border for better visibility

        // Draw the rounded border
        g2.drawRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        g2.dispose();
    }
}
