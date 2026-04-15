package UI;

import javax.swing.JTextField;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedTextField extends JTextField {
    private final int borderRadius;
    private final int borderThickness; // Border thickness
    private Color borderColor; // Border color

    public RoundedTextField(int borderRadius, int borderThickness, Color borderColor) {
        this.borderRadius = borderRadius;
        this.borderThickness = borderThickness;
        this.borderColor = borderColor;
        setOpaque(false); // Make the JTextField non-opaque to paint custom background
        setBorder(null); // Remove default border
        setFocusable(true); // Ensure the text field can receive focus
        setHorizontalAlignment(JTextField.RIGHT); // Align the text to the right
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Paint the rounded rectangle background
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), borderRadius, borderRadius));

        // Call the superclass method to paint the text and other components
        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the rounded border
        g2.setColor(borderColor); // Use the border color set in the constructor
        g2.setStroke(new BasicStroke(borderThickness)); // Set the border thickness
        g2.draw(new RoundRectangle2D.Float(
                borderThickness / 2f,
                borderThickness / 2f,
                getWidth() - borderThickness,
                getHeight() - borderThickness,
                borderRadius,
                borderRadius
        ));

        g2.dispose();
    }

    @Override
    public Insets getInsets() {
        // Adjust these values to ensure proper padding
        return new Insets(borderThickness + 10, 10, borderThickness + 10, 10); // Padding values, can be adjusted as needed
    }

    @Override
    public void setBorder(Border border) {
        // Remove border to prevent default border rendering
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        // Ensure background color changes are handled correctly
    }

    // Method to set the border color
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        repaint(); // Repaint to apply the new border color
    }
}
