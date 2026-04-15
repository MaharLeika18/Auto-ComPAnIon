package UI;

import org.MiniDev.Customer.CreateCustomerPanel;
import org.MiniDev.OOP.Customers;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class EditableAvatar extends ImageAvatar {

    private byte[] imageBytes; // Store the image as a byte array

    public EditableAvatar() {
        // Ensure fillRect is set to false
        setFillRect(false);

        // Add a mouse listener to detect clicks on the avatar
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chooseNewImage(CreateCustomerPanel.lastClickedCustomer);
            }
        });
    }

    // New method: Choose and set an image from the file system
    public void chooseAndSetImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select an Image");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                BufferedImage img = ImageIO.read(selectedFile);
                setImage(new ImageIcon(img));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading image!", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    // Method to open a file chooser and select a new image
    private void chooseNewImage(Customers lastClickedCustomer) {
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
                lastClickedCustomer.setProfilePicture(imageBytes);

                // Optionally, update the UI component with the new image
                setImage(new ImageIcon(selectedFile.getAbsolutePath()));
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception appropriately
                JOptionPane.showMessageDialog(this, "Failed to load the image.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method to get the image as a byte array
    public byte[] getImageBytes() {
        return imageBytes;
    }
}
