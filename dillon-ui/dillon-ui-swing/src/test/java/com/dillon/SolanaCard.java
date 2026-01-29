package com.dillon;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class SolanaCard extends JPanel {

    // 颜色常量
    private final Color BACKGROUND_COLOR = new Color(18, 18, 22);
    private final Color TEXT_WHITE = new Color(255, 255, 255);
    private final Color TEXT_GRAY = new Color(150, 150, 160);
    private final Color ACCENT_RED = new Color(239, 83, 80);
    private final Color CHART_RED = new Color(255, 110, 90);

    public SolanaCard() {
        setPreferredSize(new Dimension(300, 350));
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // 开启抗锯齿，保证圆角和字体平滑
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 1. 绘制圆角背景
        g2.setColor(BACKGROUND_COLOR);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

        // 2. 绘制 Logo 区域 (模拟白底图标)
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(80, 40, 60, 60, 20, 20);
        
        // 3. 绘制文字信息
        g2.setColor(TEXT_GRAY);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g2.drawString("SOL", 155, 60);
        
        g2.setColor(TEXT_WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 22));
        g2.drawString("Solana", 155, 88);

        // 4. 绘制价格
        g2.setFont(new Font("SansSerif", Font.PLAIN, 42));
        g2.drawString("419.68", 40, 160);
        
        g2.setColor(TEXT_GRAY);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g2.drawString("$ 67,108.32", 40, 190);

        // 5. 绘制跌幅标签 (-3.09%)
        drawPriceTag(g2, 200, 135, "-3.09%");

        // 6. 绘制底部曲线图 (Sparkline)
        drawChart(g2);
    }

    private void drawPriceTag(Graphics2D g2, int x, int y, String text) {
        g2.setColor(ACCENT_RED);
        g2.fillRoundRect(x, y, 80, 30, 15, 15);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2.drawString(text, x + 25, y + 20);
        // 简易箭头
        g2.fillPolygon(new int[]{x+8, x+13, x+18}, new int[]{y+12, y+22, y+12}, 3);
    }

    private void drawChart(Graphics2D g2) {
        int chartY = 280;
        int chartHeight = 40;
        
        Path2D.Double path = new Path2D.Double();
        // 模拟数据点
        double[] points = {0.1, 0.5, 0.2, 0.3, 0.1, 0.4, 0.3, 0.6, 0.8, 0.9};
        
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(CHART_RED);
        
        path.moveTo(0, chartY);
        // 使用二阶贝塞尔曲线连接点实现平滑感
        for (int i = 0; i < points.length - 1; i++) {
            double x1 = i * (getWidth() / (double)points.length);
            double y1 = chartY + (points[i] * chartHeight);
            double x2 = (i + 1) * (getWidth() / (double)points.length);
            double y2 = chartY + (points[i+1] * chartHeight);
            path.curveTo(x1 + 10, y1, x2 - 10, y2, x2, y2);
        }
        g2.draw(path);
        
        // 绘制高亮的小圆点
        g2.fillOval(250, chartY + 35, 8, 8);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(30, 30, 35)); // 外部背景
        frame.setLayout(new GridBagLayout());
        frame.add(new SolanaCard());
        frame.setSize(500, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}