package UI;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.*;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class IconCreator {

    public static ImageIcon createResizedIcon(String filePath, int baselineSize, Color pressedColor) {
        try {
            List<BufferedImage> images = new ArrayList<>();
            String fileExtension = getFileExtension(filePath);

            if ("svg".equalsIgnoreCase(fileExtension)) {
                // Handle SVG files using Apache Batik
                SVGDocument svgDocument = loadSVGDocument(filePath);
                if (svgDocument == null) {
                    throw new IllegalArgumentException("SVG Document not found or could not be loaded.");
                }

                double[] zoomFactors = {1.0, 1.25, 1.5, 1.75, 2.0, 2.25, 2.5, 2.75, 3.0};
                for (double zoomFactor : zoomFactors) {
                    int size = (int) (baselineSize * zoomFactor);
                    BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = image.createGraphics();

                    // Set rendering hints for quality
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);


                    // Clear background
                    g2d.setColor(new Color(0, 0, 0, 0)); // Transparent background
                    g2d.fillRect(0, 0, size, size);


                    // Create a bridge context and build a graphics node
                    UserAgent userAgent = new UserAgentAdapter();
                    DocumentLoader loader = new DocumentLoader(userAgent);
                    BridgeContext bridgeContext = new BridgeContext(userAgent, loader);
                    bridgeContext.setDynamicState(BridgeContext.DYNAMIC);
                    GVTBuilder builder = new GVTBuilder();
                    GraphicsNode graphicsNode = builder.build(bridgeContext, svgDocument);

                    // Calculate the scale factor to fit the SVG into the image
                    double scaleX = (double) size / svgDocument.getRootElement().getWidth().getBaseVal().getValue();
                    double scaleY = (double) size / svgDocument.getRootElement().getHeight().getBaseVal().getValue();
                    AffineTransform transform = AffineTransform.getScaleInstance(scaleX, scaleY);

                    // Render SVG onto the image
                    g2d.transform(transform);
                    graphicsNode.paint(g2d);

                    // Apply pressedColor with transparency if provided
                    if (pressedColor != null) {
                        g2d.setComposite(AlphaComposite.SrcAtop.derive(0.5f)); // Example: 0.5f means 50% transparency
                        g2d.setColor(pressedColor);
                        g2d.fillRect(0, 0, size, size);
                    }

                    // Dispose graphics
                    g2d.dispose();

                    images.add(image);
                }

                return new ImageIcon(new BaseMultiResolutionImage(images.toArray(new BufferedImage[0])));
            } else if ("png".equalsIgnoreCase(fileExtension)) {
                // Handle PNG files using ImageIO
                URL pngUrl = IconCreator.class.getResource(filePath);
                if (pngUrl == null) {
                    throw new IllegalArgumentException("PNG resource not found: " + filePath);
                }

                BufferedImage image = ImageIO.read(pngUrl);
                if (image == null) {
                    throw new IllegalArgumentException("Failed to read PNG image.");
                }

                // Resize PNG image
                image = resizeImage(image, baselineSize, baselineSize);
                images.add(image);

                return new ImageIcon(new BaseMultiResolutionImage(images.toArray(new BufferedImage[0])));
            } else {
                throw new IllegalArgumentException("Unsupported file type: " + fileExtension);
            }

        } catch (Exception e) {
            e.printStackTrace(); // Print the stack trace for debugging purposes
            return null; // Return null or handle the exception according to your application's needs
        }
    }

    private static String getFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filePath.length() - 1) {
            return filePath.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    private static SVGDocument loadSVGDocument(String svgPath) throws IOException {
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
        URL svgURL = IconCreator.class.getResource(svgPath);
        if (svgURL == null) {
            throw new IllegalArgumentException("SVG resource not found: " + svgPath);
        }
        try (InputStream stream = svgURL.openStream()) {
            return factory.createSVGDocument(svgURL.toString(), stream);
        }
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();
        return resizedImage;
    }
}
