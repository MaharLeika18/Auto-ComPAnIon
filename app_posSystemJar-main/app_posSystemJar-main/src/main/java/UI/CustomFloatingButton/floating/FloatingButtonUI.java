package UI.CustomFloatingButton.floating;

import UI.CustomFloatingButton.shadow.ShadowRenderer;
import UI.SvgIcon;
import org.MiniDev.Backoffice.EmployeeManagement.EmployeeManagementPage;
import org.MiniDev.Backoffice.ExpenseManagement.ExpenseManagementPage;
import org.MiniDev.Backoffice.StockManagement.StockManagementPage;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class FloatingButtonUI extends LayerUI<JComponent> {

    private Shape shape;
    private boolean mousePressed;
    private boolean mouseHovered;
    private ImageIcon image;
    private BufferedImage imageShadow;
    private String childName; // Added childName as an instance variable

    public FloatingButtonUI(String childName) {
        this.childName = childName;
        image = new SvgIcon("/PlusWhiteIcon.svg", 28).getImageIcon();
    }

    @Override
    public void installUI(JComponent jc) {
        super.installUI(jc);
        if (jc instanceof JLayer) {
            ((JLayer) jc).setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        }
    }

    @Override
    public void uninstallUI(JComponent jc) {
        super.uninstallUI(jc);
        if (jc instanceof JLayer) {
            ((JLayer) jc).setLayerEventMask(0);
        }
    }

    @Override
    public void paint(Graphics grphcs, JComponent jc) {
        super.paint(grphcs, jc);
        Graphics2D g2 = (Graphics2D) grphcs.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int size = 55;
        int x = jc.getWidth() - size - 15;
        int y = jc.getHeight() - size - 15;
        shape = new Ellipse2D.Double(x, y, size, size);

        // Set button color based on state
        if (mousePressed) {
            g2.setColor(new Color(10, 63, 85));
        } else if (mouseHovered) {
            g2.setColor(new Color(19, 105, 139));
        } else {
            g2.setColor(new Color(23, 129, 170));
        }

        if (imageShadow == null) {
            createImageShadow(size);
        }
        g2.drawImage(imageShadow, x - 5, y - 5, null);
        g2.fill(shape);

        // Draw icon in the center of the button
        int iconSize = 28;
        int iconX = (size - iconSize) / 2;
        int iconY = (size - iconSize) / 2;
        g2.drawImage(image.getImage(), x + iconX, y + iconY, iconSize, iconSize, null);
        g2.dispose();
    }

    @Override
    protected void processMouseEvent(MouseEvent me, JLayer<? extends JComponent> jlayer) {
        if (SwingUtilities.isLeftMouseButton(me)) {
            if (me.getID() == MouseEvent.MOUSE_PRESSED) {
                if (mouseHovered) {
                    mousePressed = true;
                    jlayer.repaint(shape.getBounds());
                }
            } else if (me.getID() == MouseEvent.MOUSE_RELEASED) {
                mousePressed = false;
                jlayer.repaint(shape.getBounds());
                if (mouseHovered) {
                    if (childName != null && childName.equals("NewItemEntryPage")) {
                        StockManagementPage.stockMenuCardLayout.show(StockManagementPage.mainMenuCardHoldingPanel, "NewItemEntryPage");
                    } else if (childName != null && childName.equals("EXP-NEW")) {
                        ExpenseManagementPage.expenseMenuCardLayout.show(ExpenseManagementPage.menuExpenseCardHoldingPanel, "EXP-NEW");
                    } else if (childName != null && childName.equals("EMP-NEW")) {
                        EmployeeManagementPage.employeeMenuCardLayout.show(EmployeeManagementPage.menuEmployeeCardHoldingPanel, "EMP-NEW");
                    } else {
                        System.out.println("Unknown child name: " + childName);
                    }
                }
            }
        }
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent me, JLayer<? extends JComponent> jlayer) {
        if (shape == null) return; // Prevent NullPointerException

        Point point = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), jlayer.getView());
        boolean hover = shape.contains(point);
        if (mouseHovered != hover) {
            mouseHovered = hover;
            jlayer.repaint(shape.getBounds());
            jlayer.setCursor(hover ? new Cursor(Cursor.HAND_CURSOR) : new Cursor(Cursor.DEFAULT_CURSOR));
        }
        if (mousePressed) {
            me.consume();
        }
    }


    private void createImageShadow(int size) {
        int shadowSize = 6;
        int width = size + shadowSize * 2;
        int height = size + shadowSize * 2;
        imageShadow = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imageShadow.createGraphics();
        g2.drawImage(new ShadowRenderer(shadowSize, 0.3f, Color.BLACK).createShadow(createShape(size)), 0, 0, null);
        g2.dispose();
    }

    private BufferedImage createShape(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.fill(new Ellipse2D.Double(0, 0, size, size));
        g2.dispose();
        return img;
    }
}
