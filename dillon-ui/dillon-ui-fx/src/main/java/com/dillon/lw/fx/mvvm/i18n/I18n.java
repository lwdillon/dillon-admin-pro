package com.dillon.lw.fx.mvvm.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18n {
    private static final String BUNDLE_NAME = "i18n.messages";
    private static Locale currentLocale = Locale.getDefault();

    public static void setLocale(Locale locale) {
        currentLocale = locale;
    }

    public static Locale getLocale() {
        return currentLocale;
    }

    public static ResourceBundle getBundle() {
        return ResourceBundle.getBundle(BUNDLE_NAME, currentLocale);
    }

    public static String get(String key) {
        return getBundle().getString(key);
    }
}