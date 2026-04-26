package UI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.RoundRectangle2D;

public class RoundedTextArea extends JTextArea {
    private final int borderRadius;
    private final Color borderColor;
    private String placeholder;
    private boolean showingPlaceholder;


    public RoundedTextArea(int borderRadius, Color borderColor, int maxLength) {
        this.borderRadius = borderRadius;
        this.borderColor = borderColor;
        setOpaque(false); // Make the JTextArea non-opaque to paint custom background
        // Set up text area properties
        setLineWrap(true);
        setWrapStyleWord(true);
        setRows(2); // Set the number of visible rows

        // Focus listener to manage placeholder visibility
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (getText().isEmpty()) {
                    showingPlaceholder = false;
                    repaint();
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) {
                    showingPlaceholder = true;
                    repaint();
                }
            }
        });

        // Initialize showingPlaceholder state
        showingPlaceholder = true;

        // Document listener to manage placeholder visibility
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkPlaceholder();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkPlaceholder();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkPlaceholder();
            }

            private void checkPlaceholder() {
                showingPlaceholder = getText().isEmpty() && !hasFocus();
                repaint();
            }
        });

        // Apply the character length document filter
        ((AbstractDocument) getDocument()).setDocumentFilter(new LengthLimitDocumentFilter(maxLength));
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Paint the background with the current background color
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), borderRadius, borderRadius));

        // Call the superclass method to paint the text and other components
        super.paintComponent(g2);

        // Paint the placeholder text if necessary
        if (showingPlaceholder && placeholder != null) {
            g2.setColor(Color.GRAY);

            // Calculate placeholder position considering insets
            FontMetrics fm = g2.getFontMetrics();
            int x = getInsets().left;
            int y = fm.getAscent() + getInsets().top;

            g2.drawString(placeholder, x, y);
        }

        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(borderColor);
        g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, borderRadius, borderRadius));
        g2.dispose();
    }

    @Override
    public Insets getInsets() {
        Insets insets = super.getInsets();
        return new Insets(insets.top + borderRadius, insets.left + borderRadius, insets.bottom + borderRadius, insets.right + borderRadius);
    }

    // Custom DocumentFilter to limit text length
    private static class LengthLimitDocumentFilter extends DocumentFilter {
        private final int maxLength;

        public LengthLimitDocumentFilter(int maxLength) {
            this.maxLength = maxLength;
        }

        @Override
        public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (isValidLength(fb.getDocument(), offset, string)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (isValidLength(fb.getDocument(), offset, text)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        private boolean isValidLength(Document doc, int offset, String text) throws BadLocationException {
            int length = doc.getLength();
            if (text != null) {
                length += text.length();
            }
            return length <= maxLength;
        }
    }
}