package com.lw.swing.components.ui;

import com.formdev.flatlaf.jideoss.ui.FlatJideTabbedPaneUI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MainTabbedPaneUI extends FlatJideTabbedPaneUI {

    private BufferedImage backgroundImage = null;

    @Override
    protected int calculateMaxTabHeight(int tabPlacement) {
        return 60;
    }

    @Override
    protected int calculateTabHeight(int tabPlacement, int tabIndex, FontMetrics metrics) {
        return 60;
    }



    @Override
    public void paintBackground(Graphics g, Component c) {
//        g.setColor(UIManager.getColor("TabbedPane.background"));
        super.paintBackground(g, c);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setColor(UIManager.getColor("App.tabbedPaneHeardBackground"));
        g2.fillRect(0, 0, c.getWidth(), 60);
//
//        if (backgroundImage == null || backgroundImage.getWidth() != panelWidth || backgroundImage.getHeight() != panelHeight) {
//            createBackgroundImage(c.getWidth(), c.getHeight()); // 确保背景图像已创建
//        }
//        if (backgroundImage != null) {
//            g2.drawImage(backgroundImage, 0, _maxTabHeight,c.getWidth(), c.getHeight(), null);
//        }
        g2.dispose();
    }
}
