package UI;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

import javax.swing.border.AbstractBorder;
import java.awt.*;

public class RoundedTitledBorder extends AbstractBorder {
    private final String title;
    private final int radius;
    private final Color borderColor;
    private final Color textColor;

    public RoundedTitledBorder(String title, int radius, Color borderColor, Color textColor) {
        this.title = title;
        this.radius = radius;
        this.borderColor = borderColor;
        this.textColor = textColor;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(borderColor);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw rounded rectangle
        g2.drawRoundRect(x, y + 10, width - 1, height - 20, radius, radius);

        // Draw title
        FontMetrics fm = g2.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        int titleHeight = fm.getHeight();

        // Erase background behind text
        g2.setColor(c.getBackground());
        g2.fillRect(x + 10, y, titleWidth + 10, titleHeight);

        // Draw text
        g2.setColor(textColor);
        g2.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 15));
        g2.drawString(title, x + 15, y + fm.getAscent());

        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(20, radius, radius, radius);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.top = 20;
        insets.left = radius;
        insets.right = radius;
        insets.bottom = radius;
        return insets;
    }
}
