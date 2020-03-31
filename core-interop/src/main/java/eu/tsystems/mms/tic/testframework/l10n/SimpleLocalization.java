package eu.tsystems.mms.tic.testframework.l10n;

import eu.tsystems.mms.tic.testframework.common.UTF8ResourceBundleControl;

import java.util.Locale;
import java.util.ResourceBundle;

public class SimpleLocalization {
    private static final String languageFilesPath = "lang";
    private static Locale currentLocale;
    private static ResourceBundle currentResourceBundle;

    static {
        updateResourceBundle();
    }

    private static ResourceBundle getResourceBundle() {
        if (Locale.getDefault() != currentLocale) {
            updateResourceBundle();
        }
        return currentResourceBundle;
    }

    private static void updateResourceBundle() {
        currentResourceBundle = ResourceBundle.getBundle(languageFilesPath, new UTF8ResourceBundleControl());
        currentLocale = Locale.getDefault();
    }

    public static String getText(final String label) {
        return getResourceBundle().getString(label);
    }
}
