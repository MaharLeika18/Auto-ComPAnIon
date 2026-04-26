package UI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DialogFadeIn {

    public static void fadeIn(JDialog dialog) {
        // Start with opacity 0
        final float[] opacity = {0f};
        dialog.setOpacity(opacity[0]);

        // Create a Timer to gradually increase the opacity
        Timer timer = new Timer(3, null);
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity[0] += 0.05f; // Increase opacity
                if (opacity[0] >= 1f) {
                    opacity[0] = 1f; // Cap opacity at 1
                    dialog.setOpacity(opacity[0]);
                    timer.stop(); // Stop the timer when fully opaque
                } else {
                    dialog.setOpacity(opacity[0]);
                }
            }
        });
        timer.start();
    }

}
