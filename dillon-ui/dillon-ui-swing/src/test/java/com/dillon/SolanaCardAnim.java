package com.dillon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;

public class SolanaCardAnim extends JPanel {

    private final Color BACKGROUND_COLOR = new Color(18, 18, 22);
    private final Color TEXT_WHITE = new Color(255, 255, 255);
    private final Color TEXT_GRAY = new Color(150, 150, 160);
    private final Color ACCENT_RED = new Color(239, 83, 80);
    private final Color CHART_RED = new Color(255, 110, 90);

    // 动画相关变量
    private float glowAlpha = 0f; // 0.0 到 1.0 之间的发光强度
    private Timer timer;

    public SolanaCardAnim() {
        setPreferredSize(new Dimension(300, 350));
        setOpaque(false);

        // 初始化动画定时器 (约 60 FPS)
        timer = new Timer(16, e -> {
            boolean changed = false;
            if (isMouseInside) {
                if (glowAlpha < 1f) {
                    glowAlpha += 0.05f; // 渐显速度
                    changed = true;
                }
            } else {
                if (glowAlpha > 0f) {
                    glowAlpha -= 0.05f; // 渐隐速度
                    changed = true;
                }
            }
            if (glowAlpha > 1f) glowAlpha = 1f;
            if (glowAlpha < 0f) glowAlpha = 0f;
            if (changed) repaint();
        });
        timer.start();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isMouseInside = true;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isMouseInside = false;
            }
        });
    }

    private boolean isMouseInside = false;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create(); // 使用副本防止干扰其他组件
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1. 绘制主体背景
        g2.setColor(BACKGROUND_COLOR);
        g2.fillRoundRect(0, 0, w, h, 40, 40);

        // 2. 绘制向下的光源效果 (关键部分)
        if (glowAlpha > 0) {
            // 定义渐变：中心在顶部中点 (w/2, 0)，向四周扩散
            float radius = h * 0.8f;
            float[] dist = {0.0f, 1.0f};
            Color[] colors = {
                    new Color(1f, 1f, 1f, 0.15f * glowAlpha), // 中心点颜色 (淡白)
                    new Color(1f, 1f, 1f, 0f)                 // 边缘透明
            };
            RadialGradientPaint rgp = new RadialGradientPaint(
                    new Point2D.Float(w / 2f, 0), radius, dist, colors);

            g2.setPaint(rgp);
            // 剪裁区域限制在圆角矩形内
            g2.setClip(new RoundRectangle2D.Float(0, 0, w, h, 40, 40));
            g2.fillRect(0, 0, w, h);
            g2.setClip(null); // 恢复剪裁
        }

        // --- 以下为原有的静态元素 ---
        drawStaticElements(g2);

        g2.dispose();
    }

    private void drawStaticElements(Graphics2D g2) {
        // Logo
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(80, 40, 60, 60, 20, 20);

        // 文字
        g2.setColor(TEXT_GRAY);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g2.drawString("SOL", 155, 60);
        g2.setColor(TEXT_WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 22));
        g2.drawString("Solana", 155, 88);

        // 价格
        g2.setFont(new Font("SansSerif", Font.PLAIN, 42));
        g2.drawString("419.68", 40, 160);
        g2.setColor(TEXT_GRAY);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g2.drawString("$ 67,108.32", 40, 190);

        // 跌幅标签和曲线
        drawPriceTag(g2, 200, 135, "-3.09%");
        drawChart(g2);
    }

    private void drawPriceTag(Graphics2D g2, int x, int y, String text) {
        g2.setColor(ACCENT_RED);
        g2.fillRoundRect(x, y, 80, 30, 15, 15);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2.drawString(text, x + 25, y + 20);
        g2.fillPolygon(new int[]{x + 8, x + 13, x + 18}, new int[]{y + 12, y + 22, y + 12}, 3);
    }

    private void drawChart(Graphics2D g2) {
        int chartY = 280;
        int chartHeight = 40;
        Path2D.Double path = new Path2D.Double();
        double[] points = {0.1, 0.5, 0.2, 0.3, 0.1, 0.4, 0.3, 0.6, 0.8, 0.9};
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(CHART_RED);
        path.moveTo(0, chartY);
        for (int i = 0; i < points.length - 1; i++) {
            double x1 = i * (getWidth() / (double) points.length);
            double y1 = chartY + (points[i] * chartHeight);
            double x2 = (i + 1) * (getWidth() / (double) points.length);
            double y2 = chartY + (points[i + 1] * chartHeight);
            path.curveTo(x1 + 10, y1, x2 - 10, y2, x2, y2);
        }
        g2.draw(path);
        g2.fillOval(250, chartY + 35, 8, 8);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Solana Animation Card");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(30, 30, 35));
        frame.setLayout(new GridBagLayout());
        frame.add(new SolanaCardAnim());
        frame.setSize(500, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}