package com.dillon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.*;
import java.text.DecimalFormat;

public class SolanaInteractiveCard extends JPanel {

    // --- 颜色常量 ---
    private final Color BACKGROUND_COLOR = new Color(20, 22, 28);
    private final Color TEXT_WHITE = new Color(255, 255, 255);
    private final Color TEXT_GRAY = new Color(140, 145, 160);
    private final Color ACCENT_RED = new Color(255, 70, 85);
    private final Color CHART_RED = new Color(255, 90, 110);
    private final Color CHART_CYAN = new Color(20, 241, 149);
    private final Color ACCENT_BLUE = new Color(0, 102, 255, 30);
    private final Color AXIS_COLOR = new Color(255, 255, 255, 25);

    // 模拟数据
    private final double[] dataPoints1 = {0.4, 0.35, 0.5, 0.75, 0.5, 0.6, 0.3, 0.4, 0.7, 0.65, 0.55};
    private final double[] dataPoints2 = {0.6, 0.55, 0.7, 0.50, 0.4, 0.3, 0.45, 0.35, 0.5, 0.4, 0.3};
    
    private final DecimalFormat df = new DecimalFormat("###.00");
    private int mouseX = -1;

    public SolanaInteractiveCard() {
        setPreferredSize(new Dimension(400, 500));
        setOpaque(false);
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        // 极致渲染设置
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int w = getWidth();
        int h = getHeight();

        drawDynamicBackground(g2, w, h);
        drawUIElements(g2, w, h);
        
        // 布局参数
        int paddingL = (int)(w * 0.15);
        int paddingR = (int)(w * 0.08);
        int chartW = w - (paddingL + paddingR);
        int chartH = (int)(h * 0.22);
        int chartY = (int)(h * 0.62);

        // 1. 绘制坐标轴
        drawAxes(g2, paddingL, chartY, chartW, chartH);

        // 2. 绘制静态曲线（不再在方法内部处理交互）
        drawPureCurve(g2, paddingL, chartW, chartH, chartY, dataPoints2, CHART_CYAN);
        drawPureCurve(g2, paddingL, chartW, chartH, chartY, dataPoints1, CHART_RED);

        // 3. 统一处理多曲线悬停交互
        drawCombinedInteraction(g2, paddingL, chartW, chartH, chartY);

        g2.dispose();
    }

    private void drawAxes(Graphics2D g2, int x, int y, int w, int h) {
        g2.setFont(new Font("Inter", Font.PLAIN, 10));
        FontMetrics fm = g2.getFontMetrics();
        
        String[] yLabels = {"$300", "$250", "$200"};
        for (int i = 0; i <= 2; i++) {
            int lineY = y + (i * h / 2);
            g2.setColor(AXIS_COLOR);
            g2.drawLine(x, lineY, x + w, lineY);
            g2.setColor(TEXT_GRAY);
            g2.drawString(yLabels[i], x - fm.stringWidth(yLabels[i]) - 8, lineY + 4);
        }

        String[] xLabels = {"00:00", "12:00", "23:59"};
        for (int i = 0; i < xLabels.length; i++) {
            int labelX = x + (i * w / (xLabels.length - 1));
            g2.drawString(xLabels[i], labelX - fm.stringWidth(xLabels[i]) / 2, y + h + 20);
        }
    }

    private void drawPureCurve(Graphics2D g2, int padding, int chartW, int chartH, int chartY, double[] points, Color color) {
        double step = (double) chartW / (points.length - 1);
        Path2D.Double path = new Path2D.Double();

        for (int i = 0; i < points.length; i++) {
            double x = padding + (i * step);
            double y = chartY + (points[i] * chartH);
            if (i == 0) path.moveTo(x, y);
            else {
                double prevX = padding + ((i - 1) * step);
                double prevY = chartY + (points[i - 1] * chartH);
                path.curveTo(prevX + step/2, prevY, x - step/2, y, x, y);
            }
        }

        // 线条发光与主体
        g2.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        g2.draw(path);

        g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(color);
        g2.draw(path);

        // 填充渐变
        Path2D.Double fillPath = (Path2D.Double) path.clone();
        fillPath.lineTo(padding + chartW, chartY + chartH + 30);
        fillPath.lineTo(padding, chartY + chartH + 30);
        fillPath.closePath();
        g2.setPaint(new GradientPaint(0, chartY, new Color(color.getRed(), color.getGreen(), color.getBlue(), 40),
                                      0, chartY + chartH + 30, new Color(0,0,0,0)));
        g2.fill(fillPath);
    }

    private void drawCombinedInteraction(Graphics2D g2, int padding, int w, int h, int yStart) {
        double step = (double) w / (dataPoints1.length - 1);
        if (mouseX >= padding && mouseX <= padding + w) {
            int index = (int) Math.round((mouseX - padding) / step);
            if (index >= 0 && index < dataPoints1.length) {
                double targetX = padding + (index * step);
                
                // 1. 绘制垂直共享指引线
                g2.setStroke(new BasicStroke(1.2f, 0, 0, 10, new float[]{6}, 0));
                g2.setColor(new Color(255, 255, 255, 80));
                g2.draw(new Line2D.Double(targetX, yStart - 20, targetX, yStart + h + 5));

                // 2. 绘制每个数据点的小圆圈
                double val1 = dataPoints1[index];
                double val2 = dataPoints2[index];
                drawPointHighlight(g2, targetX, yStart + val1 * h, CHART_RED);
                drawPointHighlight(g2, targetX, yStart + val2 * h, CHART_CYAN);

                // 3. 绘制组合 Tooltip
                String txt1 = "RED: $" + df.format(250 - val1 * 50);
                String txt2 = "CYAN: $" + df.format(250 - val2 * 50);
                drawCombinedTooltip(g2, (int)targetX, (int)(yStart + Math.min(val1, val2) * h), txt1, txt2);
            }
        }
    }

    private void drawPointHighlight(Graphics2D g2, double x, double y, Color color) {
        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
        g2.fill(new Ellipse2D.Double(x - 8, y - 8, 16, 16));
        g2.setColor(Color.WHITE);
        g2.fill(new Ellipse2D.Double(x - 4, y - 4, 8, 8));
    }

    private void drawCombinedTooltip(Graphics2D g2, int x, int y, String t1, String t2) {
        g2.setFont(new Font("Inter", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();
        int width = Math.max(fm.stringWidth(t1), fm.stringWidth(t2)) + 24;
        int height = 50;
        int tx = x - width / 2;
        int ty = y - height - 15;

        // 背景
        g2.setColor(new Color(30, 32, 38, 250));
        g2.fillRoundRect(tx, ty, width, height, 12, 12);
        g2.setColor(new Color(255, 255, 255, 40));
        g2.drawRoundRect(tx, ty, width, height, 12, 12);

        // 文字内容
        g2.setColor(CHART_RED);
        g2.drawString(t1, tx + 12, ty + 20);
        g2.setColor(CHART_CYAN);
        g2.drawString(t2, tx + 12, ty + 40);
    }

    // 背景绘制和UI元素绘制保持不变...
    private void drawDynamicBackground(Graphics2D g2, int w, int h) {
        g2.setColor(BACKGROUND_COLOR);
        g2.fillRoundRect(10, 10, w - 20, h - 20, 40, 40);
        RadialGradientPaint topGlow = new RadialGradientPaint(
            new Point2D.Float(w * 0.3f, 0), h * 0.8f, new float[]{0f, 1f},
            new Color[]{new Color(153, 69, 255, 80), new Color(0, 0, 0, 0)});
        g2.setPaint(topGlow);
        g2.fillRoundRect(10, 10, w - 20, h - 20, 40, 40);
        RadialGradientPaint bottomGlow = new RadialGradientPaint(
            new Point2D.Float(w * 0.8f, h), h * 0.7f, new float[]{0f, 1f},
            new Color[]{ACCENT_BLUE, new Color(0, 0, 0, 0)});
        g2.setPaint(bottomGlow);
        g2.fillRoundRect(10, 10, w - 20, h - 20, 40, 40);
        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(new Color(255, 255, 255, 15));
        g2.drawRoundRect(10, 10, w - 20, h - 20, 40, 40);
    }

    private void drawUIElements(Graphics2D g2, int w, int h) {
        int logoSize = (int)(h * 0.11);
        g2.setPaint(new GradientPaint(30, 40, new Color(153, 69, 255), 80, 90, CHART_CYAN));
        g2.fillRoundRect(30, 40, logoSize, logoSize, 18, 18);
        g2.setColor(TEXT_GRAY);
        g2.setFont(new Font("Inter", Font.PLAIN, (int)(h * 0.032)));
        g2.drawString("SOL / USD", 30 + logoSize + 15, 58);
        g2.setColor(TEXT_WHITE);
        g2.setFont(new Font("Inter", Font.BOLD, (int)(h * 0.048)));
        g2.drawString("Solana Network", 30 + logoSize + 15, 60 + (int)(h * 0.048));
        g2.setFont(new Font("Inter", Font.BOLD, (int)(h * 0.09)));
        g2.drawString("238.14", 30, (int)(h * 0.32));
        g2.setColor(TEXT_GRAY);
        g2.setFont(new Font("Inter", Font.PLAIN, (int)(h * 0.038)));
        g2.drawString("Vol: $ 102,491,204", 30, (int)(h * 0.38));
        int badgeW = 85;
        g2.setColor(ACCENT_RED);
        g2.fillRoundRect(w - badgeW - 40, (int)(h * 0.26), badgeW, 32, 10, 10);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Inter", Font.BOLD, 14));
        g2.drawString("-4.20%", w - badgeW - 22, (int)(h * 0.26) + 21);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Solana Combined Tooltip");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().setBackground(new Color(10, 10, 12));
            frame.setLayout(new BorderLayout());
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setOpaque(false);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
            contentPanel.add(new SolanaInteractiveCard());
            frame.add(contentPanel);
            frame.setSize(500, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}