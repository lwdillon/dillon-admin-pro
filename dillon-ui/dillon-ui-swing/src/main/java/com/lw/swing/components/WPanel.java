package com.lw.swing.components;

import javax.swing.*;
import java.awt.*;

/**
 * @description:
 * @className: WPanel
 * @author: liwen
 * @date: 2019-05-13 16:35
 */
public class WPanel extends JPanel {

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
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(UIManager.getColor("App.background"));
        Insets insets=  getInsets();
        g2.fillRect(0,0,getWidth(),getHeight());
        g2.dispose();
    }
}
