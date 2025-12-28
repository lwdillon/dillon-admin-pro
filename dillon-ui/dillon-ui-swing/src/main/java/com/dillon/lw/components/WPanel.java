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

    private boolean drawBoder = true;

    public WPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
        this.setOpaque(false);
    }

    public WPanel(LayoutManager layout) {

        this(layout, true);
    }

    public WPanel(boolean isDoubleBuffered) {
        this(new FlowLayout(), isDoubleBuffered);
    }

    public WPanel() {
        this(true);
        this.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(UIManager.getColor("App.background.card"));
        g2.fillRect(0, 0, getWidth(), getHeight());
        if (isDrawBoder()) {
            g2.setColor(UIManager.getColor("App.borderColor"));
            g2.drawRect(1, 1, getWidth() - 2, getHeight() - 2);

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
