package UI.CustomComfirmation.swing;

import java.awt.*;
import javax.swing.JComponent;

public class Glass extends JComponent {

    private float alpha = 0f;
    private int arcWidth = 18;  // Width of the rounded corner arc
    private int arcHeight = 18; // Height of the rounded corner arc

    // Constructor
    public Glass() {
        setOpaque(false); // Ensure the background remains transparent
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        repaint(); // Repaint the component when the alpha value changes
    }

    public int getArcWidth() {
        return arcWidth;
    }

    public void setArcWidth(int arcWidth) {
        this.arcWidth = arcWidth;
        repaint();
    }

    public int getArcHeight() {
        return arcHeight;
    }

    public void setArcHeight(int arcHeight) {
        this.arcHeight = arcHeight;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Create a Graphics2D object with advanced rendering capabilities
        Graphics2D g2 = (Graphics2D) g.create();

        // Enable anti-aliasing for smooth curves
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Use high-quality rendering for better visuals
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Set alpha composite for smooth transparency effect
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // Set a semi-transparent color (gray)
        g2.setColor(new Color(100, 100, 100, 150));

        // Draw a high-quality rounded rectangle
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcWidth, arcHeight);

        // Dispose of the Graphics2D object to free resources
        g2.dispose();

        // Call the superclass's paintComponent method
        super.paintComponent(g);
    }
}
