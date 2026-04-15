package UI;

import raven.datetime.component.date.DatePicker;
import raven.swing.slider.PanelSlider;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;

public class CustomSmallDatePicker extends DatePicker {

    public CustomSmallDatePicker() {
        super(); // Call the constructor of the parent class
        customizePanelSlider(); // Customize after construction
    }

    private void customizePanelSlider() {
        try {
            // Use reflection to access the private field `panelSlider`
            Field panelSliderField = DatePicker.class.getDeclaredField("panelSlider");
            panelSliderField.setAccessible(true); // Make it accessible
            PanelSlider panelSlider = (PanelSlider) panelSliderField.get(this);

            // Customize the panelSlider size
            panelSlider.setPreferredSize(new Dimension(200, 200)); // Example size
            panelSlider.revalidate();
            panelSlider.repaint();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Unable to customize DatePicker panel size.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
