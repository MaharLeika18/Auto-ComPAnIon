package Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.print.*;


public class VoucherPrinter {

    public static void printVoucher(JTextPane textPane) {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) {
                return Printable.NO_SUCH_PAGE;
            }

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            g2d.scale(pageFormat.getImageableWidth() / textPane.getWidth(), pageFormat.getImageableHeight() / textPane.getHeight());
            textPane.printAll(g2d);

            return Printable.PAGE_EXISTS;
        });

        boolean printDialogResult = printerJob.printDialog();
        if (printDialogResult) {
            try {
                printerJob.print();
            } catch (PrinterException e) {
                e.printStackTrace();
            }
        }
    }
}
