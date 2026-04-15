package Security;

import SqlLoadersAndUpdater.FetchTellerDrawer;
import org.MiniDev.Cashier.CreateCashDrawerPanel;

import org.MiniDev.OOP.Drawer;

public class BatchUserDrawerChecking {
    public static Drawer currentDrawer;

    public BatchUserDrawerChecking() {
        updateDrawer();
    }

    public static void updateDrawer() {
        FetchTellerDrawer ft = new FetchTellerDrawer();
        currentDrawer = ft.getDrawerByTellerID(CreateCashDrawerPanel.tellerID);
    }

    public static boolean isClosed() {
        return currentDrawer.getClosedStatusYN() == 'Y';
    }

    public static boolean isOpened() {
        return currentDrawer.getClosedStatusYN() == 'N';
    }

    public static void closeDrawer() {
        currentDrawer.setClosedStatusYN('N');
    }

    public static void openDrawer() {
        currentDrawer.setClosedStatusYN('Y');
    }
}
