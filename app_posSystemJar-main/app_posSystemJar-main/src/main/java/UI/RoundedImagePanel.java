package UI;

import Utils.DefaultProductPhoto;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;

public class RoundedImagePanel extends JPanel {
    private BufferedImage image;
    private final int radius;
    private final int width;
    private final int height;
    private static final Color PLACEHOLDER_COLOR = new Color(220, 220, 220);
    public static final byte[] DEFAULT_IMAGE_BYTES = DefaultProductPhoto.getDefaultPhotoBytes();

    public RoundedImagePanel(byte[] imageData, int radius, int width, int height) {
        this.radius = radius;
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(width, height));
        setOpaque(false); // Make the panel transparent to show rounded corners

        image = (imageData != null && imageData.length > 0)
                ? loadImageFromBytes(imageData)
                : loadDefaultImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Create rounded clipping area
        Shape clip = new RoundRectangle2D.Float(0, 0, width, height, radius, radius);
        g2d.setClip(clip);

        if (image != null) {
            // Draw the image smoothly within the clipping area
            g2d.drawImage(image, 0, 0, width, height, null);
        } else {
            // Draw placeholder if no image is available
            drawPlaceholder(g2d);
        }

        g2d.dispose();
    }

    private BufferedImage loadImageFromBytes(byte[] imageData) {
        try {
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageData));
            if (originalImage != null) {
                return scaleImage(originalImage);
            } else {
                System.err.println("Failed to read image from byte array.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loadDefaultImage();
    }

    private BufferedImage loadDefaultImage() {
        if (DEFAULT_IMAGE_BYTES != null) {
            try {
                BufferedImage defaultImage = ImageIO.read(new ByteArrayInputStream(DEFAULT_IMAGE_BYTES));
                return scaleImage(defaultImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return createPlaceholderImage();
    }

    private BufferedImage scaleImage(BufferedImage originalImage) {
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Scale the image smoothly
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.dispose();
        return scaledImage;
    }

    private BufferedImage createPlaceholderImage() {
        BufferedImage placeholder = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = placeholder.createGraphics();
        drawPlaceholder(g2d);
        g2d.dispose();
        return placeholder;
    }

    private void drawPlaceholder(Graphics2D g2d) {
        g2d.setColor(PLACEHOLDER_COLOR);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "No Image";
        int x = (width - fm.stringWidth(text)) / 2;
        int y = (height - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(text, x, y);
    }
}
