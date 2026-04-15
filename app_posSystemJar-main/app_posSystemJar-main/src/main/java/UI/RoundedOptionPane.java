package UI;

import javax.swing.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedOptionPane {

    public static void showRoundedOptionPane(String message, String title, int arc) {
        // Create a JOptionPane
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);

        // Create a custom dialog
        JDialog dialog = optionPane.createDialog(title);
        setRoundedCorners(dialog, arc);

        // Show the dialog
        dialog.setVisible(true);
    }

    public static void setRoundedCorners(JDialog dialog, int arc) {
        // Set the shape of the dialog to a rounded rectangle
        dialog.setShape(new RoundRectangle2D.Double(0, 0, dialog.getWidth(), dialog.getHeight(), arc, arc));
    }
}


