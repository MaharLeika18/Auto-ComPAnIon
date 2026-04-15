package Utils;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import java.awt.*;
import java.awt.print.*;

public class PrintUtil {

    public static void printTextPane(JTextPane textPane, String printerName) {
        PrintService selectedPrinter = findPrinterByName(printerName);

        if (selectedPrinter == null) {
            JOptionPane.showMessageDialog(null, "Printer not found: " + printerName, "Printer Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        PrinterJob job = PrinterJob.getPrinterJob();
        try {
            job.setPrintService(selectedPrinter);
            job.setPrintable(new TextPanePrintable(textPane));

            // Directly print without a print dialog for a streamlined experience
            job.print();
            JOptionPane.showMessageDialog(null, "Print job sent to " + printerName + ".", "Print Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(null, "Failed to print: " + e.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private static PrintService findPrinterByName(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printer : printServices) {
            if (printer.getName().equalsIgnoreCase(printerName)) {
                return printer;
            }
        }
        return null;
    }

    // Printable class with custom page formatting
    private static class TextPanePrintable implements Printable {
        private JTextPane textPane;

        public TextPanePrintable(JTextPane textPane) {
            this.textPane = textPane;
        }

        @Override
        public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
            // Scale content to fit page size and handle multi-page printing
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            // Define page width and height with margins
            double pageWidth = pageFormat.getImageableWidth();
            double pageHeight = pageFormat.getImageableHeight();
            Dimension componentSize = textPane.getPreferredSize();

            // Scaling to fit the width of the page
            double scale = pageWidth / componentSize.getWidth();
            g2d.scale(scale, scale);

            // Determine if we need to print another page
            int totalPrintableHeight = (int) (pageHeight / scale);
            int totalHeight = componentSize.height;
            int yOffset = pageIndex * totalPrintableHeight;

            if (yOffset >= totalHeight) {
                return NO_SUCH_PAGE;
            }

            // Only print the visible portion for the current page
            g2d.translate(0, -yOffset);
            textPane.paint(g2d);

            return PAGE_EXISTS;
        }
    }
}
