package com.dillon.lw.utils;

import java.awt.*;

public class ColorUtils {

    public static Color withAlpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }
}
