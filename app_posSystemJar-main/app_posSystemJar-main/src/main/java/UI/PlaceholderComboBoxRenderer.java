package UI;

import javax.swing.*;
import java.awt.*;

public class PlaceholderComboBoxRenderer extends DefaultListCellRenderer {
    private final String placeholderText;
    private final Color placeholderColor;

    public PlaceholderComboBoxRenderer(String placeholderText, Color placeholderColor) {
        this.placeholderText = placeholderText;
        this.placeholderColor = placeholderColor;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value == null || value.equals(placeholderText)) {
            component.setForeground(placeholderColor);
            setText(placeholderText);
        } else {
            component.setForeground(Color.BLACK);
            setText(value.toString());
        }

        return component;
    }
}
