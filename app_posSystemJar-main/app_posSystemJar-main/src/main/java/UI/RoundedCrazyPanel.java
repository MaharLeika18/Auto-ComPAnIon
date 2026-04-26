package UI;

import raven.crazypanel.CrazyPanel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedCrazyPanel extends CrazyPanel {
    private final int borderRadius;

    public RoundedCrazyPanel(int borderRadius) {
        this.borderRadius = borderRadius;
        setOpaque(false); // Make it non-opaque to handle custom background
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Paint the rounded rectangle background
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), borderRadius, borderRadius));

        g2.dispose();
    }
}
