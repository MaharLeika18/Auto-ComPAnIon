package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.geom.Area;

import static org.MiniDev.Customer.CreateCustomerPanel.getProfilePictureDefault;

public class ImageAvatar extends JComponent {

    private Icon image;
    private int borderSize = 2;
    private int borderSpace = 1;
    private Color gradientColor1 = new Color(255, 90, 90);
    private Color gradientColor2 = new Color(42, 199, 80);
    private boolean fillRect = true; // Controls whether the component fills with a rectangle
    private boolean autoResizing = true; // New property to control auto-resizing
    private int arcSize = 0; // New property for rounded corner arc size

    public ImageAvatar() {
    }

    public Icon getImage() {
        return image;
    }

    public void removeImage() {
        this.image = null;
        repaint();
    }

    public void setImage(Icon image) {
        this.image = image;
        repaint();
    }

    public void setImage(byte[] image) {
        this.image = byteArrayToIcon(image);
        repaint();
    }

    private Icon byteArrayToIcon(byte[] imageBytes) {
        if (imageBytes == null) {
            return new ImageIcon(getProfilePictureDefault()); // Handle null case
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
            BufferedImage bufferedImage = ImageIO.read(bais);
            return new ImageIcon(bufferedImage);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getBorderSize() {
        return borderSize;
    }

    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
        repaint();
    }

    public int getBorderSpace() {
        return borderSpace;
    }

    public void setBorderSpace(int borderSpace) {
        this.borderSpace = borderSpace;
        repaint();
    }

    public Color getGradientColor1() {
        return gradientColor1;
    }

    public void setGradientColor1(Color gradientColor1) {
        this.gradientColor1 = gradientColor1;
        repaint();
    }

    public Color getGradientColor2() {
        return gradientColor2;
    }

    public void setGradientColor2(Color gradientColor2) {
        this.gradientColor2 = gradientColor2;
        repaint();
    }

    public boolean isFillRect() {
        return fillRect;
    }

    public void setFillRect(boolean fillRect) {
        this.fillRect = fillRect;
        repaint();
    }

    public boolean isAutoResizing() {
        return autoResizing;
    }

    public void setAutoResizing(boolean autoResizing) {
        this.autoResizing = autoResizing;
        repaint();
    }

    public int getArcSize() {
        return arcSize;
    }

    public void setArcSize(int arcSize) {
        this.arcSize = arcSize;
        repaint(); // Repaint when arc size changes
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (fillRect) {
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        createBorder(g2);
        if (image != null) {
            int width = getWidth();
            int height = getHeight();
            int diameter = Math.min(width, height) - (borderSize * 2 + borderSpace * 2);
            int x = (width - diameter) / 2;
            int y = (height - diameter) / 2;

            // Adjust size based on autoResizing
            Rectangle size;
            if (autoResizing) {
                size = getAutoSize(image, diameter);
            } else {
                size = new Rectangle(0, 0, diameter, diameter);
            }

            BufferedImage img = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2_img = img.createGraphics();
            g2_img.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2_img.fillRoundRect(0, 0, diameter, diameter, arcSize, arcSize); // Use round rect
            Composite composite = g2_img.getComposite();
            g2_img.setComposite(AlphaComposite.SrcIn);
            g2_img.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2_img.drawImage(toImage(image), size.x, size.y, size.width, size.height, null);
            g2_img.setComposite(composite);
            g2_img.dispose();
            g2.drawImage(img, x, y, null);
        }
        super.paintComponent(grphcs);
    }

    private void createBorder(Graphics2D g2) {
        int width = getWidth();
        int height = getHeight();
        int diameter = Math.min(width, height);
        int x = (width - diameter) / 2;
        int y = (height - diameter) / 2;
        if (isOpaque()) {
            g2.setColor(getBackground());
            g2.fillRoundRect(x, y, diameter, diameter, arcSize, arcSize); // Use round rect
        }
        Area area = new Area(new Ellipse2D.Double(x, y, diameter, diameter));
        int s = diameter -= (borderSize * 2);
        area.subtract(new Area(new Ellipse2D.Double(x + borderSize, y + borderSize, s, s)));
        g2.setPaint(new GradientPaint(0, 0, gradientColor1, width, height, gradientColor2));
        g2.fill(area);
    }

    private Rectangle getAutoSize(Icon image, int size) {
        int w = size;
        int h = size;
        int iw = image.getIconWidth();
        int ih = image.getIconHeight();
        double xScale = (double) w / iw;
        double yScale = (double) h / ih;
        double scale = Math.max(xScale, yScale);
        int width = (int) (scale * iw);
        int height = (int) (scale * ih);
        if (width < 1) {
            width = 1;
        }
        if (height < 1) {
            height = 1;
        }
        int cw = size;
        int ch = size;
        int x = (cw - width) / 2;
        int y = (ch - height) / 2;
        return new Rectangle(new Point(x, y), new Dimension(width, height));
    }

    private Image toImage(Icon icon) {
        return ((ImageIcon) icon).getImage();
    }

    public byte[] getImageBytes() {
        if (image != null && image instanceof ImageIcon) {
            Image img = ((ImageIcon) image).getImage();
            if (img instanceof BufferedImage) {
                return bufferedImageToBytes((BufferedImage) img);
            } else {
                BufferedImage bufferedImage = toBufferedImage(img);
                return bufferedImageToBytes(bufferedImage);
            }
        }
        return null;
    }

    private BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }

    private byte[] bufferedImageToBytes(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos); // You can change the format if necessary
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
