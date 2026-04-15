package UI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.net.URL;

import org.MiniDev.Backoffice.ExpenseManagement.TransactionPage.TrnhistTableActionPages.ExpTrnHistViewPage;
import org.apache.batik.swing.JSVGCanvas; // Ensure the Batik library is included

public class EditableAttachmentImage extends JPanel {

    private byte[] imageBytes; // Store the image as a byte array
    protected JSVGCanvas svgCanvas; // SVG canvas for rendering SVG images
    protected JLabel imageLabel; // Label for displaying the selected image
    private static final String DEFAULT_IMAGE_PATH = "/AddFileIllustration.svg"; // Default SVG image path

    public EditableAttachmentImage() {
        setLayout(new BorderLayout());
        svgCanvas = new JSVGCanvas(); // Create the SVG canvas
        imageLabel = new JLabel(); // Initialize the image label
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);

        // Load the default SVG image on initialization
        loadDefaultImage();

        // Add a mouse listener to detect clicks on the SVG canvas
        svgCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chooseNewImage(); // Open file chooser on click
            }
        });

        add(svgCanvas, BorderLayout.CENTER); // Add the SVG canvas to the panel
    }

    // Load the default SVG image
    private void loadDefaultImage() {
        try {
            URL defaultImageUrl = getClass().getResource(DEFAULT_IMAGE_PATH);
            if (defaultImageUrl != null) {
                svgCanvas.setURI(defaultImageUrl.toString()); // Set the SVG to the canvas
                imageLabel.setVisible(false); // Hide the image label if showing default
            } else {
                showError("Default image not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load the default image.");
        }
    }

    // Open a file chooser to select a new image
    public void chooseNewImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose an image");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                imageBytes = Files.readAllBytes(selectedFile.toPath()); // Store the image as a byte array
                updateImage(selectedFile); // Update the UI with the new image
            } catch (IOException e) {
                e.printStackTrace();
                showError("Failed to load the image.");
            }
        }
    }

    // Update the UI component with the new image
    private void updateImage(File selectedFile) {
        ImageIcon originalIcon = new ImageIcon(selectedFile.getAbsolutePath());
        Image image = originalIcon.getImage();

        // Maintain aspect ratio
        double aspectRatio = (double) image.getWidth(null) / image.getHeight(null);
        int panelWidth = getWidth();
        int newHeight = (int) (panelWidth / aspectRatio);
        Image scaledImage = image.getScaledInstance(panelWidth, newHeight, Image.SCALE_SMOOTH);

        // Update the image label
        imageLabel.setIcon(new ImageIcon(scaledImage));
        imageLabel.setVisible(true); // Show the image label

        remove(svgCanvas); // Remove the SVG canvas
        add(imageLabel, BorderLayout.CENTER); // Add the new image label

        revalidate(); // Refresh the panel
        repaint();
    }

    // Remove the current image and reset to default
    public void removeImage() {
        imageBytes = null; // Clear the byte array
        loadDefaultImage(); // Load the default image
        remove(imageLabel); // Remove the image label
        add(svgCanvas, BorderLayout.CENTER); // Re-add the SVG canvas
        revalidate(); // Refresh the panel
        repaint();
    }

    // Get the image as a byte array
    public byte[] getImageBytes() {
        return imageBytes;
    }

    // Show error message dialog
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void setNewBytes(byte[] newImageBytes) {
        this.imageBytes = newImageBytes; // Store image bytes

        if (newImageBytes != null) {
            ImageIcon icon = new ImageIcon(newImageBytes);
            Image image = icon.getImage();

            // Attempt to use parent panel dimensions
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            // If the dimensions are not ready, set a default size
            if (panelWidth <= 0 || panelHeight <= 0) {
                panelWidth = 580;  // Default width
                panelHeight = 170; // Default height
            }

            // Scale the image while maintaining aspect ratio
            Image scaledImage = getScaledImage(image, panelWidth, panelHeight);

            // Update the image label with the scaled image
            imageLabel.setIcon(new ImageIcon(scaledImage));
            imageLabel.setVisible(true);

            remove(svgCanvas); // Remove the SVG canvas if used
            add(imageLabel, BorderLayout.CENTER); // Add the new image label

            revalidate(); // Refresh the UI
            repaint();

            // Add a listener to handle future resizes dynamically
            addResizeListener(image);
        } else {
            removeImage(); // Handle null case by reverting to a default image
        }
    }

    // Helper method to scale the image while maintaining the aspect ratio
    private Image getScaledImage(Image image, int targetWidth, int targetHeight) {
        double aspectRatio = (double) image.getWidth(null) / image.getHeight(null);

        int newWidth = targetWidth;
        int newHeight = (int) (targetWidth / aspectRatio);

        // If the new height exceeds the target height, scale by height instead
        if (newHeight > targetHeight) {
            newHeight = targetHeight;
            newWidth = (int) (targetHeight * aspectRatio);
        }

        return image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
    }

    // Adds a resize listener to dynamically adjust the image on panel resizing
    private void addResizeListener(Image originalImage) {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Get new dimensions after resize
                int newWidth = getWidth();
                int newHeight = getHeight();

                // Re-scale the image and update the label
                Image resizedImage = getScaledImage(originalImage, newWidth, newHeight);
                imageLabel.setIcon(new ImageIcon(resizedImage));

                revalidate();
                repaint();
            }
        });
    }


    public void downloadImage() {
        if (imageBytes == null) {
            showError("No image to download.");
            return;
        }

        // Create a JFileChooser for saving the image
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Image");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "png", "jpg", "jpeg"));
        fileChooser.setSelectedFile(new File("downloaded_image.png")); // Default file name

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Determine the file extension
            String filePath = fileToSave.getAbsolutePath();
            String extension = getFileExtension(filePath);

            try {
                // Convert byte array to BufferedImage
                ImageIcon icon = new ImageIcon(imageBytes);
                BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics g = bufferedImage.createGraphics();
                g.drawImage(icon.getImage(), 0, 0, null);
                g.dispose();

                // Save the BufferedImage based on the selected format
                ImageIO.write(bufferedImage, extension, fileToSave);
                JOptionPane.showMessageDialog(this, "Image saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                showError("Failed to save the image.");
            }
        }
    }

    // Method to get file extension from file path
    private String getFileExtension(String filePath) {
        if (filePath.lastIndexOf(".") != -1 && filePath.lastIndexOf(".") != 0) {
            return filePath.substring(filePath.lastIndexOf(".") + 1);
        }
        return "png"; // Default to PNG if no extension is found
    }

    // Method to show the image in full view
    public void showFullView() {
        if (imageBytes == null) {
            showError("No image available for full view.");
            return;
        }

        // Create a new dialog for full view
        JDialog fullViewDialog = new JDialog(JOptionPane.getFrameForComponent(ExpTrnHistViewPage.expViewDialog), "Full View", true);
        fullViewDialog.setLayout(new BorderLayout());

        // Get screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        fullViewDialog.setSize(screenSize.width, screenSize.height); // Set dialog size to full screen

        // Create a label to display the full image
        ImageIcon fullImageIcon = new ImageIcon(imageBytes);
        JLabel fullImageLabel = new JLabel(fullImageIcon);
        fullImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        fullImageLabel.setVerticalAlignment(SwingConstants.CENTER);

        // Ensure the image fits within the screen size
        fullImageLabel.setPreferredSize(new Dimension(screenSize.width, screenSize.height));
        fullViewDialog.add(new JScrollPane(fullImageLabel), BorderLayout.CENTER); // Add the label to a scroll pane

        // Add a close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> fullViewDialog.dispose());
        fullViewDialog.add(closeButton, BorderLayout.SOUTH);

        // Set the dialog to appear above expViewDialog
        fullViewDialog.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);

        // Center the dialog relative to expViewDialog
        if (ExpTrnHistViewPage.expViewDialog != null) {
            fullViewDialog.setLocationRelativeTo(ExpTrnHistViewPage.expViewDialog);
        } else {
            fullViewDialog.setLocationRelativeTo(null); // Center on screen if expViewDialog is not available
        }

        fullViewDialog.setVisible(true); // Show the dialog
    }


}
