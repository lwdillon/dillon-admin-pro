package com.dillon.lw.components;

import cn.hutool.core.convert.Convert;

import javax.swing.*;
import java.awt.*;

/**
 * @description:
 * @className: WPanel
 * @author: liwen
 * @date: 2019-05-13 16:35
 */
public class WPanel extends JPanel {

    private boolean drawBoder = false;


    public WPanel(LayoutManager layout){
        super(layout);
    }

    public WPanel() {

    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        int arc = Convert.toInt(UIManager.getInt("App.arc"), 21);
        g2.setColor(UIManager.getColor("App.baseBackground"));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        if (isDrawBoder()) {
            g2.setColor(UIManager.getColor("App.borderColor"));
            g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, arc, arc);

        }
        g2.dispose();
    }

    public boolean isDrawBoder() {
        return drawBoder;
    }

    public void setDrawBoder(boolean drawBoder) {
        this.drawBoder = drawBoder;
    }
}
