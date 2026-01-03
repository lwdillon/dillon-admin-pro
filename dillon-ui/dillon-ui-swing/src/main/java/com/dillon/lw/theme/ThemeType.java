package com.dillon.lw.theme;

import javax.swing.*;
import java.util.function.Supplier;

public enum ThemeType {

    DARK("深色", () -> new DarkTheme()),
    LIGHT("白色", () -> new LightTheme()),
    GLAZZED("玻璃", () -> new GlazzedTheme());

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