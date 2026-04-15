package UI;


import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedBorderButton extends JButton {

    private Color backgroundColor;
    private Color hoverBackgroundColor;
    private Color pressedBackgroundColor;
    private Color borderColor;
    private int borderWidth;
    private int arcSize;

    public RoundedBorderButton(String text, int width, int height, Color initializedColor) {
        super(text);
        this.arcSize = 14; // Corner radius for rounding
        this.borderWidth = 1; // Thickness of the border
        this.borderColor = initializedColor;

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
        setBorderPainted(false); // Ensure the button does not paint its own border
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // Adjust padding
        setPreferredSize(new Dimension(width, height));

        // Set default colors for background and effects
        this.backgroundColor = Color.WHITE;
        this.hoverBackgroundColor = Color.decode("#f5f5f5");
        this.pressedBackgroundColor = Color.decode("#FFF2E8");
    }

    // Setter method for hoverBackgroundColor
    public void setHoverBackgroundColor(Color color) {
        this.hoverBackgroundColor = color;
    }

    // Setter method for hoverBackgroundColor
    public void setPressedBackgroundColor(Color color) {
        this.pressedBackgroundColor = color;
    }

    // Setter method for hoverBackgroundColor
    public void setRoundedArcSize(int arcSize) {
        this.arcSize = arcSize;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Create a rounded rectangle clipping area
        Shape clip = new RoundRectangle2D.Double(0, 0, width, height, arcSize, arcSize);
        g2.setClip(clip); // Apply the clipping

        // Paint background with smooth transitions for hover and pressed states
        if (getModel().isPressed()) {
            g2.setColor(pressedBackgroundColor);
        } else if (getModel().isRollover()) {
            g2.setColor(hoverBackgroundColor);
        } else {
            g2.setColor(backgroundColor);
        }
        g2.fillRect(0, 0, width, height); // Fill the clipped area with background color

        // Paint text and icon
        super.paintComponent(g);

        g2.dispose();
    }


    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Subtle border color that contrasts with the background
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(borderWidth)); // Thin border for sleek look
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arcSize, arcSize); // Draw rounded border

        g2.dispose();
    }

    @Override
    public Insets getInsets() {
        return new Insets(borderWidth, borderWidth, borderWidth, borderWidth);
    }

    @Override
    public boolean contains(int x, int y) {
        Shape shape = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arcSize, arcSize);
        return shape.contains(x, y);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(100, 45); // Set preferred size (width, height) as needed
    }


    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint(); // Repaint to apply the new border color immediately
    }

    public void setOverrideBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint(); // Repaint to apply the new border color immediately
    }
}
