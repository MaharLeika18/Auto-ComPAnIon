package UI;


import javax.swing.text.*;
import java.text.NumberFormat;
import java.text.ParseException;

public class NumberFormatDocumentFilter extends DocumentFilter {

    private final NumberFormat numberFormat;

    public NumberFormatDocumentFilter(NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        // Format the text as it is inserted
        String text = fb.getDocument().getText(0, fb.getDocument().getLength()) + string;
        String formattedText = formatText(text);
        fb.replace(0, fb.getDocument().getLength(), formattedText, attr);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr) throws BadLocationException {
        // Format the text as it is replaced
        String existingText = fb.getDocument().getText(0, fb.getDocument().getLength());
        String formattedText = formatText(existingText.substring(0, offset) + text + existingText.substring(offset + length));
        fb.replace(0, fb.getDocument().getLength(), formattedText, attr);
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        // Format the text as it is removed
        String existingText = fb.getDocument().getText(0, fb.getDocument().getLength());
        String formattedText = formatText(existingText.substring(0, offset) + existingText.substring(offset + length));
        fb.replace(0, fb.getDocument().getLength(), formattedText, null);
    }

    private String formatText(String text) {
        try {
            Number number = numberFormat.parse(text);
            return numberFormat.format(number);
        } catch (ParseException e) {
            return text;
        }
    }
}
