package com.dillon.lw.theme;

import com.formdev.flatlaf.FlatLightLaf;

/**
 * @author wenli
 * @date 2024/05/09
 */
public class LightTheme extends FlatLightLaf {
    public static final String NAME = "LightTheme";

    public static boolean setup() {
        return setup(new LightTheme());
    }

    public static void installLafInfo() {
        installLafInfo(NAME, LightTheme.class);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
