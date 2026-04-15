package org.MiniDev.Backoffice.ExpenseManagement.CategoryPage.CategoryTableCellCustomRender;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

public class CategoryLabelStatus extends JLabel {

    protected final int cornerRadius = 15; // Adjust corner radius as needed

    public CategoryLabelStatus(String status) {
        setForeground(Color.WHITE);
        setBorder(new EmptyBorder(5, 15, 5, 15)); // Adjust padding to fit rounded design
        setText(status);
        setHorizontalAlignment(JLabel.CENTER);
        setOpaque(false); // Ensure background is not painted by default JLabel logic
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set color based on status
        if (getText().equalsIgnoreCase("Active")) {
            g2.setColor(Color.decode("#7dd3fc")); // Use predefined green for "Active"
        } else {
            g2.setColor(Color.decode("#fef9c3")); // Red for "Inactive"
        }

        // Draw rounded background
        int width = getWidth();
        int height = getHeight();
        RoundRectangle2D roundedRect = new RoundRectangle2D.Float(0, 0, width, height, cornerRadius, cornerRadius);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
        g2.fill(roundedRect); // Fill with background color
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        super.paintComponent(g); // Paint the text and other components
    }
}
