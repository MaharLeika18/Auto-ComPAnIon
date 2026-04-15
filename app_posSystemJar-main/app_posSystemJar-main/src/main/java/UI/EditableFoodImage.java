package UI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.net.URL;

public class EditableFoodImage extends ImageAvatar {

    private byte[] imageBytes; // Store the image as a byte array


    public EditableFoodImage() {
        // Ensure fillRect is set to false
        setFillRect(false);


        // Add a mouse listener to detect clicks on the avatar
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chooseNewImage();
            }
        });
    }



    private void chooseNewImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose an image");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                // Convert the selected image file to a byte array
                imageBytes = Files.readAllBytes(selectedFile.toPath());

                // Set the image bytes to the customer object
                setProfilePicture(imageBytes);

                // Update the UI component with the new image
                updateImage(selectedFile);

                // Get the file name
                String fileName = selectedFile.getName();
                // Optionally, you can log or display the file name
                System.out.println("Selected file: " + fileName);
                // Or show a message dialog with the file name
                JOptionPane.showMessageDialog(this, "Selected file: " + fileName, "File Selected", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception appropriately
                JOptionPane.showMessageDialog(this, "Failed to load the image.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // Method to update the UI component with the new image
    private void updateImage(File selectedFile) {
        try {
            // Read the image from the file
            BufferedImage originalImage = ImageIO.read(selectedFile);

            // Scale the image to match the component size (96x120 in this example)
            int targetWidth = getPreferredSize().width;
            int targetHeight = getPreferredSize().height;

            BufferedImage scaledImage = scaleImage(originalImage, targetWidth, targetHeight);

            // Convert the scaled image to an ImageIcon and set it
            setImage(new ImageIcon(scaledImage));

            // Repaint the component
            repaint();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load and scale the image.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Utility method to scale an image with high quality
    private BufferedImage scaleImage(BufferedImage originalImage, int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();

        // Use high-quality rendering hints
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the scaled image
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.dispose();

        return scaledImage;
    }


    // Method to get the image as a byte array
    public byte[] getImageBytes() {
        return imageBytes;
    }

    // Placeholder for the method that sets the profile picture (if needed)
    private void setProfilePicture(byte[] imageBytes) {
        // Implement the logic to set the profile picture in your application context
    }
}
