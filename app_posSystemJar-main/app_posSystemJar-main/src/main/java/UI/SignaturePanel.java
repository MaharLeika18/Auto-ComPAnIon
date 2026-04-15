package UI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SignaturePanel extends RoundedPanel {
    private BufferedImage signatureImage;
    private Graphics2D g2d;
    private boolean drawing;
    private int lastX, lastY;

    public SignaturePanel(int parentWidth, int parentHeight) {
        setOpaque(false);
        setBackground(new Color(255, 255, 255, 200));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // Initialize signature image
        signatureImage = new BufferedImage(parentWidth, parentHeight, BufferedImage.TYPE_INT_ARGB);
        g2d = signatureImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, signatureImage.getWidth(), signatureImage.getHeight());
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));

        // Add mouse listeners for drawing
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                drawing = true;
                lastX = e.getX();
                lastY = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                drawing = false;
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (drawing) {
                    drawSmoothLine(lastX, lastY, e.getX(), e.getY());
                    lastX = e.getX();
                    lastY = e.getY();
                }
            }
        });
        setLayout(new BorderLayout());

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(signatureImage, 0, 0, null); // Draw the signature image
    }

    private void drawSmoothLine(int x1, int y1, int x2, int y2) {
        // Use quadratic curve for smoother drawing
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(x1, y1, x2, y2);
        repaint();
    }

    public void confirmClearSignature() {
        int option = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear the signature?",
                "Clear Signature",
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            clearSignature();
        }
    }

    public void clearSignature() {
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, signatureImage.getWidth(), signatureImage.getHeight());
        repaint();
    }

    public byte[] getSignatureAsBytes() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(signatureImage, "jpg", baos); // Choose your preferred format
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
