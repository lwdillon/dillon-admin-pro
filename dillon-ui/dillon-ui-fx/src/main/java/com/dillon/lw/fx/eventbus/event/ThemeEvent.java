package com.dillon.lw.fx.eventbus.event;

public class ThemeEvent {

    private String theme;
    private boolean isDarkMode;

    public ThemeEvent(String theme) {
        this(theme, false);
    }

    public ThemeEvent(String theme, boolean isDarkMode) {
        this.theme = theme;
        this.isDarkMode = isDarkMode;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public boolean isDarkMode() {
        return isDarkMode;
    }

    public void setDarkMode(boolean darkMode) {
        isDarkMode = darkMode;
    }
}
