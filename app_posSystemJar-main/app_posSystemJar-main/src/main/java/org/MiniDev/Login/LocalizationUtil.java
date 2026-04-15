package org.MiniDev.Login;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizationUtil {
    private static final String BUNDLE_NAME = "resources.MessagesBundle";

    public static ResourceBundle getBundle(Locale locale) {
        return ResourceBundle.getBundle(BUNDLE_NAME, locale);
    }
}
