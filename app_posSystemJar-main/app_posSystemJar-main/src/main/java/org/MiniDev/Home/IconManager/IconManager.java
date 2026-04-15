package org.MiniDev.Home.IconManager;

import UI.SvgIcon;
import org.MiniDev.Home.MiniDevPOS;

import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class IconManager {
    private static final Map<JButton, String> defaultIconMap = new HashMap<>();
    private static final Map<JButton, String> activeIconMap = new HashMap<>();

    static {
        defaultIconMap.put(MiniDevPOS.homeButton, "/HomeIcon.svg");
        defaultIconMap.put(MiniDevPOS.customersButton, "/CustomersIcon.svg");
        defaultIconMap.put(MiniDevPOS.tablesButton, "/GridTableIcon.svg");
        defaultIconMap.put(MiniDevPOS.cashierButton, "/CashierIcon.svg");
        defaultIconMap.put(MiniDevPOS.ordersButton, "/OrderListsIcon.svg");
        defaultIconMap.put(MiniDevPOS.reportsButton, "/ReportIcon.svg");
        defaultIconMap.put(MiniDevPOS.settingsButton, "/SettingIcon.svg");

        activeIconMap.put(MiniDevPOS.homeButton, "/HomeIconActive.svg");
        activeIconMap.put(MiniDevPOS.customersButton, "/CustomersIconActive.svg");
        activeIconMap.put(MiniDevPOS.tablesButton, "/GridTableIconActive.svg");
        activeIconMap.put(MiniDevPOS.cashierButton, "/CashierIconActive.svg");
        activeIconMap.put(MiniDevPOS.ordersButton, "/OrderListsIconActive.svg");
        activeIconMap.put(MiniDevPOS.reportsButton, "/ReportIconActive.svg");
        activeIconMap.put(MiniDevPOS.settingsButton, "/SettingIconActive.svg");
    }

    private static ImageIcon getIcon(JButton button, boolean isActive) {
        String path = isActive ? activeIconMap.get(button) : defaultIconMap.get(button);
        return path != null ? new SvgIcon(path, 20).getImageIcon() : null;
    }

    public static ImageIcon getDefaultIcon(JButton button) {
        return getIcon(button, false);
    }

    public static ImageIcon getActiveIcon(JButton button) {
        return getIcon(button, true);
    }
}
