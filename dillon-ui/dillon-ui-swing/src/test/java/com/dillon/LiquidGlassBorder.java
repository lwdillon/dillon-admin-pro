package com.dillon;

import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class LiquidGlassBorder implements Border {
    private final int radius;
    private final boolean isDarkMode;

    public LiquidGlassBorder(int radius, boolean isDarkMode) {
        this.radius = radius;
        this.isDarkMode = isDarkMode;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. 设置颜色配置
        Color baseColor = isDarkMode ? new Color(255, 255, 255, 40) : new Color(255, 255, 255, 180);
        Color edgeColor = isDarkMode ? new Color(0, 0, 0, 100) : new Color(150, 150, 150, 80);
        Color highlightColor = isDarkMode ? new Color(255, 255, 255, 60) : new Color(255, 255, 255, 220);

        // 2. 绘制外边框渐变
        GradientPaint gradient = new GradientPaint(
                0, 0, highlightColor, 
                0, height, edgeColor
        );
        
        g2.setPaint(gradient);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);

        // 3. 绘制内部“流体”光泽感
        // 创建一个稍微缩小的区域
        g2.setStroke(new BasicStroke(0.5f));
        g2.setColor(baseColor);
        g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, radius - 2, radius - 2);

        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}