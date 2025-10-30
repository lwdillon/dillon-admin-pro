package com.dillon.lw.components;

import org.jdesktop.swingx.VerticalLayout;

import java.awt.*;

public class CenterLayout extends VerticalLayout {

    /**
     * Message 距离窗口顶部的偏移量
     */
    private int offset = 60;

    public CenterLayout() {
    }

    public CenterLayout(int gap, int offset) {
        super(gap);
        this.offset = offset;
    }


    @Override
    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();
        Dimension size = parent.getSize();
        int width = size.width ;
        int height = insets.top + offset;

        for (int i = 0, c = parent.getComponentCount(); i < c; i++) {
            Component m = parent.getComponent(i);
            if (m.isVisible()) {
                m.setBounds((width - m.getWidth()) / 2, height, m.getWidth(), m.getHeight());
                height += m.getHeight() + getGap();
            }
        }
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}