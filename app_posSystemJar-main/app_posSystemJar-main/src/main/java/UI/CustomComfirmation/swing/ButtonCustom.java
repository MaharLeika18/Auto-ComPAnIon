package UI.CustomComfirmation.swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import java.awt.geom.RoundRectangle2D;

public class ButtonCustom extends JButton {

    private Color background = new Color(69, 191, 71);
    private Color colorHover = new Color(76, 206, 78);
    private Color colorPressed = new Color(63, 175, 65);
    private boolean mouseOver = false;
    protected final int cornerRadius = 7; // Adjust corner radius as needed

    public ButtonCustom() {
        init();
    }

    private void init() {
        setContentAreaFilled(false); // Disable default button rendering
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setOpaque(false); // Make the button not opaque

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                mouseOver = true;
                repaint(); // Request repaint to reflect color change
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseOver = false;
                repaint(); // Request repaint to reflect color change
            }

            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(colorPressed);
                repaint(); // Request repaint to reflect color change
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (mouseOver) {
                    setBackground(colorHover);
                } else {
                    setBackground(background);
                }
                repaint(); // Request repaint to reflect color change
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set background color based on the button state
        if (mouseOver) {
            g2.setColor(colorHover);
        } else {
            g2.setColor(background);
        }

        // Draw the rounded rectangle
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

        super.paintComponent(g); // Call the superclass method to paint the text
        g2.dispose();
    }

    @Override
    public void setBackground(Color bg) {
        background = bg;
        super.setBackground(bg);
    }

    public Color getColorHover() {
        return colorHover;
    }

    public void setColorHover(Color colorHover) {
        this.colorHover = colorHover;
    }

    public Color getColorPressed() {
        return colorPressed;
    }

    public void setColorPressed(Color colorPressed) {
        this.colorPressed = colorPressed;
    }
}
