package Utils;

import javax.swing.text.*;

public class NumericDocumentFilter extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string == null) return;

        if (string.matches("[-]?\\d*\\.?\\d*")) { // Adjusted for decimals
            super.insertString(fb, offset, string, attr);
            formatDocument(fb);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr) throws BadLocationException {
        if (string == null) return;

        if (string.matches("[-]?\\d*\\.?\\d*")) { // Adjusted for decimals
            super.replace(fb, offset, length, string, attr);
            formatDocument(fb);
        }
    }

    private void formatDocument(FilterBypass fb) throws BadLocationException {
        Document doc = fb.getDocument();
        String text = doc.getText(0, doc.getLength());
        String formatted = formatNumber(text);
        fb.replace(0, doc.getLength(), formatted, null);
    }

    private String formatNumber(String number) {
        number = number.replaceAll(",", ""); // Remove commas for formatting
        if (!number.isEmpty()) {
            try {
                return String.format("%,d", Long.parseLong(number)); // Format with commas
            } catch (NumberFormatException e) {
                return number; // Return original on error
            }
        }
        return "";
    }
}
