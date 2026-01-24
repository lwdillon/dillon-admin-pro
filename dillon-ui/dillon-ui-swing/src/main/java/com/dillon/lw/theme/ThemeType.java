package com.dillon.lw.theme;

import javax.swing.*;
import java.util.function.Supplier;

public enum ThemeType {

    LIGHT("亮色", () -> new LightTheme()),
    DARK("深色", () -> new DarkTheme()),
    GLAZZED("蓝色", () -> new GlazzedTheme());

    private final String text;
    private final Supplier<LookAndFeel> lafSupplier;

    ThemeType(String text, Supplier<LookAndFeel> lafSupplier) {
        this.text = text;
        this.lafSupplier = lafSupplier;
    }

    public LookAndFeel createLaf() {
        return lafSupplier.get();
    }

    public String getText() {
        return text;
    }
}