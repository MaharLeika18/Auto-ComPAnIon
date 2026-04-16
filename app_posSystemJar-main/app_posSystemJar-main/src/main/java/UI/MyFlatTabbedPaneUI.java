package UI;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.geom.Path2D;
import static UI.UserFinalSettingsVar.COLOR_BLACK;
public class MyFlatTabbedPaneUI extends FlatTabbedPaneUI {

    public static ComponentUI createUI(JComponent c) {
        return new MyFlatTabbedPaneUI();
    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                      int x, int y, int w, int h, boolean isSelected) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            float arc = UIScale.scale(19f); // Rounded corners
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Background color changes based on selection
            Color background = isSelected ? Color.decode("#bae6fd") : new Color(230, 230, 230);
            g2.setColor(background);

            // Draw rounded background
            g2.fillRoundRect(x, y, w, h, (int) arc, (int) arc);
        } finally {
            g2.dispose();
        }
    }


    @Override
    protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics,
                             int tabIndex, String title, Rectangle textRect, boolean isSelected) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            // Ensure smooth rendering with anti-aliasing
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Use the same font consistently, regardless of selection.
            g2.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, UIScale.scale(14)));

            // Set appropriate color based on selection state
            g2.setColor(isSelected ? COLOR_BLACK : Color.DARK_GRAY);

            // Draw the text
            FlatUIUtils.drawString(tabPane, g2, title, textRect.x, textRect.y + metrics.getAscent());
        } finally {
            g2.dispose();
        }
    }


    @Override
    protected void installDefaults() {
        super.installDefaults();
        // Set a high-quality font with appropriate scaling
        tabPane.setFont(new Font("Noto Sans Myanmar", Font.PLAIN, UIScale.scale(14)));
        UIManager.put("TabbedPane.font", tabPane.getFont()); // Enforce consistent font
    }

    @Override
    protected void paintCardTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                      int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            float lineWidth = UIScale.scale(1f); // Border width
            float arc = UIScale.scale(19f); // Corner arc radius

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Create the border path
            Path2D path = new Path2D.Float(Path2D.WIND_EVEN_ODD);
            path.append(FlatUIUtils.createRoundRectanglePath(x, y, w, h, arc, arc, 0, 0), false);
            path.append(FlatUIUtils.createRoundRectanglePath(x + lineWidth, y + lineWidth,
                    w - (lineWidth * 2), h - lineWidth, arc - lineWidth, arc - lineWidth, 0, 0), false);

            g2.fill(path);
        } finally {
            g2.dispose();
        }
    }

    @Override
    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
        // Do nothing to avoid default content border.
    }
}
