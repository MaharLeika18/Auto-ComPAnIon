package UI;

import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import javax.swing.*;

public class SvgIcon {
    private BaseMultiResolutionImage b;

    // Constructor: accepts a path to an SVG file and the baseline dimensions
    public SvgIcon(String svgPath, int baselineSize) {
        this.b = this.createMultiImage(svgPath, baselineSize);
    }

    // Return an image icon with the multi-resolution image
    public ImageIcon getImageIcon() {
        return new ImageIcon(this.b);
    }

    // Create a multi-resolution image for nine common scaling levels
    private BaseMultiResolutionImage createMultiImage(String svgPath, int baselineSize) {
        double[] zoomFactors = {1.0, 1.25, 1.5, 1.75, 2.0, 2.25, 2.5, 2.75, 3.0};
        List<BufferedImage> images = new ArrayList<>();
        for (double zoomFactor : zoomFactors) {
            int size = (int) (baselineSize * zoomFactor);
            BufferedImage image = transcodeSvgToImage(svgPath, size, size);
            if (image != null) {
                images.add(image);
            } else {
                // Handle the case where image is null
                System.err.println("Failed to load image for zoom factor: " + zoomFactor);
            }
        }
        return new BaseMultiResolutionImage(images.toArray(new BufferedImage[0]));
    }

    private BufferedImage transcodeSvgToImage(String svgPath, int width, int height) {
        URL resource = getClass().getResource(svgPath);
        if (resource == null) {
            System.err.println("SVG file not found: " + svgPath);
            return null;
        }

        InputStream inputStream = null;
        try {
            inputStream = resource.openStream();
            BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
            transcoder.addTranscodingHint(ImageTranscoder.KEY_WIDTH, (float) width);
            transcoder.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, (float) height);
            TranscoderInput input = new TranscoderInput(inputStream);
            TranscoderOutput output = new TranscoderOutput();
            transcoder.transcode(input, output);
            return transcoder.getImage();
        } catch (Exception e) {
            System.err.println("Error transcoding SVG to image: " + e.getMessage());
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // Handle the IOException
                }
            }
        }
    }

    private static class BufferedImageTranscoder extends ImageTranscoder {
        private BufferedImage image = null;

        public BufferedImage getImage() {
            return image;
        }

        @Override
        public BufferedImage createImage(int w, int h) {
            return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        }

        @Override
        public void writeImage(BufferedImage img, TranscoderOutput output) {
            image = img;
        }
    }
}
