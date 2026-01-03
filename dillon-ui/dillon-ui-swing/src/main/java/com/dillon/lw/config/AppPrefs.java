package com.dillon.lw.config;

import java.util.List;
import java.util.Objects;
import java.util.prefs.Preferences;

public final class AppPrefs {

    /**
     * 应用唯一 Preferences 节点
     */
    private static final Preferences PREFS =
            Preferences.userRoot().node("com/dillon/lw/dillon-ui-swing");

    /* ========================= Keys ========================= */

    // ---------- 用户 ----------
    public static final String KEY_HISTORY_USERS = "user.history.list"; // JSON
    public static final String KEY_CURRENT_USER = "user.current.id";

    // ---------- UI ----------
    public static final String KEY_UI_THEME = "ui.theme.name";

    // ---------- App ----------
    public static final String KEY_LAST_LOGIN_OK = "app.login.last.ok";

    private AppPrefs() {
    }

    /* ========================= 基础 ========================= */

    public static Preferences prefs() {
        return PREFS;
    }

    public static void clearAll() {
        try {
            PREFS.clear();
        } catch (Exception ignored) {
        }
    }
}