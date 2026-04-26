package UI;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

import javax.swing.*;
import java.awt.*;

import static UI.IconCreator.createResizedIcon;
import static UI.UserFinalSettingsVar.*;

public class UIAppButtonFactory {

    public UIAppButtonFactory() {
    }

    public static IconTextButton createIconTextButton(String buttonName, String iconPath, Color backgroundColor) {
        ImageIcon imageIcon = createResizedIcon(iconPath, 20, null);
        IconTextButton button = new IconTextButton(buttonName, imageIcon, 14, backgroundColor, 0);
        button.setBackground(backgroundColor);
        button.setForeground(COLOR_WHITE);
        button.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(100, 40));
        button.setIcon(imageIcon);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);

        return button;
    }

    public static RoundedBorderButton creatRoundedBorderButton(String buttonName, String iconPath, Color baseColor, int iconBaseLine) {
        ImageIcon imageIcon = createResizedIcon(iconPath, iconBaseLine, null);
        RoundedBorderButton button = new RoundedBorderButton(buttonName, 100, 40, baseColor);
        button.setIcon(imageIcon);

        button.setBackground(Color.WHITE);
        button.setForeground(baseColor);
        button.setFont(new Font("Noto Sans Myanmar", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(100, 40));

        // Align text and icon
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);

        return button;
    }
}
