package UI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;

import static UI.UserFinalSettingsVar.*;

public class RoundedPasswordField extends JPasswordField {
    private final int borderRadius;
    private final int borderThickness; // Border thickness
    private Color borderColor; // Default border color
    private Color focusBorderColor = COLOR_SELECT_BLUE; // Border color when focused
    private Color placeholderColor = Color.GRAY; // Placeholder color
    private String placeholder = ""; // Placeholder text

    public RoundedPasswordField(int borderRadius, int borderThickness, Color borderColor) {
        this.borderRadius = borderRadius;
        this.borderThickness = borderThickness;
        this.borderColor = borderColor;
        setOpaque(false); // Make the JPasswordField non-opaque to paint custom background
        setBorder(null); // Remove default border
        setFocusable(true); // Ensure the password field can receive focus
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

        // Draw placeholder text if the password field is empty and not focused
        if (getPassword().length == 0 && !isFocusOwner() && placeholder != null) {
            g2.setColor(placeholderColor); // Set placeholder text color
            g2.setFont(getFont().deriveFont(Font.PLAIN)); // Use plain font for placeholder
            g2.drawString(placeholder, getInsets().left, getHeight() / 2 + getFont().getSize() / 2 - 2); // Draw placeholder
        }

        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Determine border color based on focus state
        Color currentBorderColor = isFocusOwner() ? focusBorderColor : borderColor;

        // Draw the rounded border
        g2.setColor(currentBorderColor);
        g2.setStroke(new BasicStroke(borderThickness));
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
        return new Insets(borderThickness + 10, 10, borderThickness + 10, 10);
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

    // Method to set the default border color
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        repaint(); // Repaint to apply the new border color
    }

    // Method to set the focus border color
    public void setFocusBorderColor(Color focusBorderColor) {
        this.focusBorderColor = focusBorderColor;
        repaint(); // Repaint to apply the new focus border color
    }

    // Method to set the placeholder text
    public void setPlaceholder(String placeholder) {
        this.placeholder = (placeholder == null) ? "" : placeholder; // Set to empty string if null
        repaint(); // Repaint to apply the placeholder
    }

    // Method to set the placeholder color
    public void setPlaceholderColor(Color placeholderColor) {
        this.placeholderColor = placeholderColor;
        repaint(); // Repaint to apply the new placeholder color
    }

    // Override processKeyEvent to handle special key events
    @Override
    protected void processKeyEvent(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && e.isControlDown()) {
            // Handle Backspace + Ctrl combination
            setText(""); // Clear the text field
            e.consume(); // Prevent default action
        } else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            setText(""); // Clear the text field
            e.consume(); // Prevent default action
        } else {
            super.processKeyEvent(e); // Handle other keys as usual
        }
    }
}
