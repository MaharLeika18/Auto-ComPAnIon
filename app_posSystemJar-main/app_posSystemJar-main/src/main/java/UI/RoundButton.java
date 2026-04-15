package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundButton extends JButton {

    private final Color backgroundColor;
    private final Color hoverBackgroundColor;
    private final Color pressedBackgroundColor;
    protected int width;
    protected int height;

    public RoundButton(String text, int width, int height) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
        setBorderPainted(false); // Ensure the button does not paint its own border
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // Adjust padding
        setPreferredSize(new Dimension(width, height));

        // Set default colors for background and effects
        this.backgroundColor = Color.WHITE;
        this.hoverBackgroundColor = new Color(220, 220, 220);
        this.pressedBackgroundColor = new Color(200, 200, 200);
        this.width = width;
        this.height = height;
    }

    public RoundButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
        setBorderPainted(false); // Ensure the button does not paint its own border
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // Adjust padding

        // Set default colors for background and effects
        this.backgroundColor = Color.WHITE;
        this.hoverBackgroundColor = new Color(220, 220, 220);
        this.pressedBackgroundColor = new Color(200, 200, 200);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Paint background
        if (getModel().isPressed()) {
            g2.setColor(pressedBackgroundColor);
        } else if (getModel().isRollover()) {
            g2.setColor(hoverBackgroundColor);
        } else {
            g2.setColor(backgroundColor);
        }

        g2.fillRoundRect(0, 0, width - 1, height - 1, 13, 13); // Adjust corner radius as needed

        // Paint text
        super.paintComponent(g);

        g2.dispose();
    }

    @Override
    public Insets getInsets() {
        return new Insets(1, 1, 1, 1);
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Do not paint any border
    }

    @Override
    public boolean contains(int x, int y) {
        Shape shape = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10); // Adjust corner radius as needed
        return shape.contains(x, y);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(140, 190); // Set preferred size (width, height) as needed
    }
}

