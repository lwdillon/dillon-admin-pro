package com.lw.swing.components;

import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import java.awt.*;

public class ReflectionRepaintManager extends RepaintManager {
    @Override
    public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {

        int lastDeltaX = c.getX();
        int lastDeltaY = c.getY();

        LookAndFeel laf = UIManager.getLookAndFeel();
        boolean isDark = false;
        if (laf instanceof FlatLaf) {
            isDark = ((FlatLaf) laf).isDark();
        }

        if (isDark) {
            Container parent = c.getParent();
            while (parent instanceof JComponent) {
                if (!parent.isVisible()) {
                    return;
                }

                if (parent instanceof WScrollPane) {
                    x += lastDeltaX;
                    y += lastDeltaY;


                    lastDeltaX = lastDeltaY = 0;

                    c = (JComponent) parent;
                }

                lastDeltaX += parent.getX();
                lastDeltaY += parent.getY();

                parent = parent.getParent();
            }
        }


        super.addDirtyRegion(c, x, y, w, h);
    }
}