package UI;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {
    private int radius;
    private Color shadowColor = Color.BLACK;
    private int shadowSize = 5;
    private boolean drawShadow = false;
    private Color borderColor = Color.BLACK; // Border color
    private int borderWidth = 0; // Border width

    public RoundedPanel() {
        this(20); // Default radius
    }

    public RoundedPanel(int radius) {
        this.radius = radius;
        setOpaque(false);
    }

    public RoundedPanel(LayoutManager layout, int radius) {
        super(layout);
        this.radius = radius;
        setOpaque(false);
    }

    public RoundedPanel(LayoutManager layout) {
        this(layout, 10); // Default radius
    }

    public void setShadowColor(Color shadowColor) {
        this.shadowColor = shadowColor;
    }

    public void setShadowSize(int shadowSize) {
        this.shadowSize = shadowSize;
        repaint(); // Repaint the panel to apply new shadow size
    }

    public void setDrawShadow(boolean drawShadow) {
        this.drawShadow = drawShadow;
        repaint(); // Repaint when shadow drawing state changes
    }

    // Setter for border color
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        repaint(); // Repaint to apply border color change
    }

    // Getter for border color
    public Color getBorderColor() {
        return this.borderColor; // Return the current border color
    }

    // Setter for border width
    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        repaint(); // Repaint to apply border width change
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g.create();

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int width = getWidth();
        int height = getHeight();
        int shadowOffset = drawShadow ? shadowSize : 0;

        // Draw shadow if enabled
        if (drawShadow) {
            graphics.setColor(shadowColor);
            graphics.fillRoundRect(shadowOffset, shadowOffset, width - shadowOffset - 1, height - shadowOffset - 1, radius, radius);
        }

        // Draw rounded panel with background color
        graphics.setColor(getBackground());
        graphics.fillRoundRect(0, 0, width - shadowOffset - 1, height - shadowOffset - 1, radius, radius);

        // Draw border
        if (borderWidth > 0) {
            graphics.setColor(borderColor);
            graphics.setStroke(new BasicStroke(borderWidth));
            graphics.drawRoundRect(borderWidth / 2, borderWidth / 2, width - borderWidth - 1, height - borderWidth - 1, radius, radius);
        }

        graphics.dispose();
    }
}
