package com.dillon.lw.components;

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
        g2.setColor(UIManager.getColor("App.background.card1"));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        if (isDrawBoder()) {
            g2.setColor(UIManager.getColor("App.borderColor"));
            g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);

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
