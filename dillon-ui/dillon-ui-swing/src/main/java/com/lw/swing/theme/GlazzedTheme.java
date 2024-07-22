package com.lw.swing.theme;

import com.formdev.flatlaf.FlatDarkLaf;

/**
 * @author wenli
 * @date 2024/05/09
 */
public class GlazzedTheme extends FlatDarkLaf {
    public static final String NAME = "GlazzedTheme";

    public static boolean setup() {
        return setup(new GlazzedTheme());
    }

    public static void installLafInfo() {
        installLafInfo(NAME, GlazzedTheme.class);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
