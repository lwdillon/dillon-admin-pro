package com.lw.swing.components;

import javax.swing.*;
import java.awt.*;

/**
 * Created by liwen on 2017/5/11.
 */
public class WScrollPane extends JScrollPane {

    public WScrollPane() {
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        setOpaque(false);

        getViewport().setOpaque(false);

        getViewport().setBorder(null);

        getHorizontalScrollBar().setPreferredSize(new Dimension(0, 7));

        getVerticalScrollBar().setPreferredSize(new Dimension(7, 0));

        getVerticalScrollBar().setUI(new AerithScrollbarUI());

        getHorizontalScrollBar().setUI(new AerithScrollbarUI());

    }

    public WScrollPane(Component view) {
        this();
        setViewportView(view);
    }
}
