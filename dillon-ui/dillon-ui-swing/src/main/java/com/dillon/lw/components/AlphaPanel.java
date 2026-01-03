package com.dillon.lw.components;

import javax.swing.*;
import java.awt.*;

public class AlphaPanel extends JPanel {

    private float alpha = 1f;

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(
                AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, alpha
                )
        );
        super.paint(g2);   // ⚠️ 注意是 paint，不是 paintComponent
        g2.dispose();
    }
}