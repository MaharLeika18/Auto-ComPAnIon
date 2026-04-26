package Utils;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DrawTextOnBarcodeFactory {

    private static final Font TEXT_FONT = new Font("Noto Sans Myanmar", Font.PLAIN, 14);  // Consistent font across all styles

    private static final int FIXED_WIDTH = 300;  // Fixed width of the image
    private static final int FIXED_HEIGHT = 150; // Fixed height of the image

    /**
     * Draws text on top of the barcode with high-quality rendering and proper scaling.
     *
     * @param barcodeImage The barcode image to render the text onto.
     * @param text         The text to be drawn on the barcode.
     * @return A new image with the barcode and text rendered on it.
     */
    public static BufferedImage drawTextOnBarcodeStyle1(BufferedImage barcodeImage, String text) {
        BufferedImage resultImage = new BufferedImage(FIXED_WIDTH, FIXED_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resultImage.createGraphics();
        enableHighQualityRendering(g);

        // Scale the barcode image to fit within the fixed width and height
        g.drawImage(barcodeImage, 0, 0, FIXED_WIDTH, FIXED_HEIGHT, null);

        // Draw text with scaling adjustments
        drawTextWithScaling(g, text, resultImage);

        g.dispose();
        return resultImage;
    }

    /**
     * Style 2: Barcode on the left, text details on the right with padding.
     *
     * @param barcodeImage The barcode image to render the text onto.
     * @param productName  The product name to be displayed.
     * @param productPrice The product price to be displayed.
     * @param issueDate    The product issue date.
     * @param expireDate   The product expiry date.
     * @return A new image with the barcode and text details rendered on it.
     */
    public static BufferedImage drawTextOnBarcodeStyle2(String currencyShortForm, BufferedImage barcodeImage, String productName, String productPrice, String issueDate, String expireDate) {
        int padding = 10;
        int extraWidth = 120;  // Provide enough space for the text details

        // Set fixed dimensions for the result image
        int width = 450;
        int height = FIXED_HEIGHT;

        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resultImage.createGraphics();
        enableHighQualityRendering(g);

        // Draw barcode on the left with padding and limit the width for barcode
        int barcodeWidth = width - padding * 2 - extraWidth; // Ensure the barcode fits with space for text
        g.drawImage(barcodeImage, padding, padding, barcodeWidth, height - padding * 2, null);

        String[] details;
        if (expireDate.isEmpty()) {
            details = new String[]{productName, "Price: " + (productPrice + " " + currencyShortForm), "Issue Date: " + issueDate};
        } else {
            details = new String[]{productName, "Price: " + (productPrice + " " + currencyShortForm), "Issue Date: " + issueDate, "Expire Date: " + expireDate};
        }

        // Reduce gap between barcode and text more (for example, by 4 pixels)
        int x = padding + barcodeWidth + padding - 2;  // Reduce the gap by 4 pixels

        // Set consistent font for the details text
        g.setFont(TEXT_FONT);

        // Draw text details to the right of the barcode
        drawTextDetails(g, details, x, padding + 15, extraWidth);

        g.dispose();
        return resultImage;
    }


    /**
     * Style 3: Barcode with product name on top and barcode text at the bottom.
     *
     * @param barcodeImage The barcode image to render the text onto.
     * @param productName  The product name to be displayed at the top.
     * @param text         Additional text to be displayed below the barcode.
     * @return A new image with the barcode and text rendered on it.
     */
    public static BufferedImage drawTextOnBarcodeStyle3(BufferedImage barcodeImage, String productName, String text) {
        BufferedImage resultImage = new BufferedImage(FIXED_WIDTH, FIXED_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resultImage.createGraphics();
        enableHighQualityRendering(g);

        // Set consistent font for the product name
        g.setFont(TEXT_FONT);

        // Draw product name centered at the top
        drawProductName(g, productName, resultImage);

        // Adjusted gap between product name and barcode
        int gapBetweenNameAndBarcode = 35;
        g.drawImage(barcodeImage, 0, gapBetweenNameAndBarcode, FIXED_WIDTH, FIXED_HEIGHT - gapBetweenNameAndBarcode, null);

        // Draw barcode text
        g.setFont(TEXT_FONT);
        drawBarcodeText(g, text, resultImage);

        g.dispose();
        return resultImage;
    }

    // Helper method to enable high-quality rendering
    private static void enableHighQualityRendering(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    // Helper method to draw text with scaling adjustments
    private static void drawTextWithScaling(Graphics2D g, String text, BufferedImage image) {
        g.setFont(TEXT_FONT);
        FontMetrics metrics = g.getFontMetrics();

        // Adjust font size to fit the text within the image width
        while (metrics.stringWidth(text) > image.getWidth() - 40) {
            g.setFont(g.getFont().deriveFont(g.getFont().getSize2D() - 1f));
            metrics = g.getFontMetrics();
        }

        // Calculate position and draw text
        int x = (image.getWidth() - metrics.stringWidth(text)) / 2;
        int y = image.getHeight() - 10;

        // Background rectangle for text readability
        g.setColor(Color.WHITE);
        g.fillRect(x - 10, y - metrics.getHeight(), metrics.stringWidth(text) + 20, metrics.getHeight() + 10);

        // Draw text
        g.setColor(Color.BLACK);
        g.drawString(text.toUpperCase(), x, y);
    }

    // Helper method to draw product name text centered at the top
    private static void drawProductName(Graphics2D g, String productName, BufferedImage image) {
        g.setFont(TEXT_FONT);
        FontMetrics metrics = g.getFontMetrics();

        int productNameX = (image.getWidth() - metrics.stringWidth(productName)) / 2;
        int productNameY = 20;

        g.setColor(Color.BLACK);
        g.drawString(productName.toUpperCase(), productNameX, productNameY);
    }

    // Helper method to draw barcode-related text below the barcode image
    private static void drawBarcodeText(Graphics2D g, String text, BufferedImage image) {
        g.setFont(TEXT_FONT);
        FontMetrics metrics = g.getFontMetrics();

        // Adjust font size to fit the text
        while (metrics.stringWidth(text) > image.getWidth() - 40) {
            g.setFont(g.getFont().deriveFont(g.getFont().getSize2D() - 1f));
            metrics = g.getFontMetrics();
        }

        // Position for the text below the barcode
        int x = (image.getWidth() - metrics.stringWidth(text)) / 2;
        int y = image.getHeight() - 10;

        // Background rectangle for readability
        g.setColor(Color.WHITE);
        g.fillRect(x - 10, y - metrics.getHeight(), metrics.stringWidth(text) + 20, metrics.getHeight() + 10);

        // Draw bold text
        g.setColor(Color.BLACK);
        g.drawString(text.toUpperCase(), x, y);
    }

    // Helper method to draw text details (e.g., product name, price, dates) to the right of the barcode
    private static void drawTextDetails(Graphics2D g, String[] details, int x, int y, int maxWidth) {
        FontMetrics metrics = g.getFontMetrics();

        for (String text : details) {
            while (metrics.stringWidth(text) > maxWidth - 20) {
                g.setFont(g.getFont().deriveFont(g.getFont().getSize2D() - 1f));
                metrics = g.getFontMetrics();
            }

            g.setColor(Color.BLACK);
            g.drawString(text.toUpperCase(), x, y);
            y += metrics.getHeight() + 5; // Vertical spacing between text lines
        }
    }
}
