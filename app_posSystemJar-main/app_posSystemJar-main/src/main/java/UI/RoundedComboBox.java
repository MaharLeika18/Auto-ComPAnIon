package UI;

import org.MiniDev.combo_suggestion.ComboSuggestionUI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.RoundRectangle2D;

import static UI.UserFinalSettingsVar.COLOR_SELECT_BLUE;

public class RoundedComboBox<E> extends JComboBox<E> {
    private final int borderRadius;
    private final int borderThickness;
    private final Color defaultBorderColor;
    private final Color focusBorderColor = COLOR_SELECT_BLUE; // Border color when focused
    private Color currentBorderColor;
    private String placeholder; // Placeholder field
    private boolean showingPlaceholder = true; // Track if placeholder is being shown

    public RoundedComboBox(int borderRadius, int borderThickness, Color borderColor) {
        this.borderRadius = borderRadius;
        this.borderThickness = borderThickness;
        this.defaultBorderColor = borderColor;
        this.currentBorderColor = borderColor;
        this.placeholder = ""; // Initialize placeholder
        setOpaque(false); // Make JComboBox non-opaque to paint the custom background
        setBorder(null); // Remove default border
        setUI(new ComboSuggestionUI()); // Use custom ComboBox UI

        // Add focus listener to the combo box
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                updateBorderColorOnFocus(true);
            }

            @Override
            public void focusLost(FocusEvent e) {
                updateBorderColorOnFocus(false);
            }
        });

        // Add focus listener to the editor component if editable
        if (isEditable()) {
            JTextField editorField = (JTextField) getEditor().getEditorComponent();
            editorField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    updateBorderColorOnFocus(true);
                }

                @Override
                public void focusLost(FocusEvent e) {
                    updateBorderColorOnFocus(false);
                }
            });
        }
    }

    private void updateBorderColorOnFocus(boolean hasFocus) {
        currentBorderColor = hasFocus ? focusBorderColor : defaultBorderColor;
        repaint(); // Repaint the component to update the border color
    }

    // Setter for placeholder
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint(); // Repaint to show the placeholder
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Paint combo box components
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Paint the rounded rectangle background
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), borderRadius, borderRadius));

        // Draw placeholder if no item is selected
        if (getSelectedItem() == null || showingPlaceholder) {
            g2.setColor(Color.GRAY); // Placeholder color
            g2.setFont(getFont().deriveFont(Font.ITALIC)); // Italic for placeholder
            FontMetrics fm = g2.getFontMetrics();
            int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(placeholder, 10, textY); // Draw placeholder text at 10px from left
        }

        g2.dispose();
    }

    @Override
    public void setSelectedItem(Object item) {
        super.setSelectedItem(item);
        showingPlaceholder = (item == null || "".equals(item)); // Update placeholder visibility

        // Update font color based on selection
        JTextField editorField = (JTextField) getEditor().getEditorComponent();
        if (editorField != null) {
            if (showingPlaceholder) {
                editorField.setForeground(Color.GRAY); // Placeholder color
            } else {
                editorField.setForeground(Color.BLACK); // Item selected color
            }
        }

        repaint(); // Repaint to update the placeholder
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the rounded border
        g2.setColor(currentBorderColor); // Use the current border color
        g2.setStroke(new BasicStroke(borderThickness));
        g2.draw(new RoundRectangle2D.Float(
                borderThickness / 2f,
                borderThickness / 2f,
                getWidth() - borderThickness,
                getHeight() - borderThickness,
                borderRadius,
                borderRadius
        ));

        g2.dispose();
    }

    @Override
    public Insets getInsets() {
        // Adjust padding as per your needs
        return new Insets(borderThickness + 5, 5, borderThickness + 5, 5);
    }

    @Override
    public void setBorder(Border border) {
    }
}
