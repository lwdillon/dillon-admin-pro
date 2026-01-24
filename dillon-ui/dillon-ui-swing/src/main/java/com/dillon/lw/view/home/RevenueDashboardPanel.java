package com.dillon.lw.view.home;

import com.dillon.lw.components.WPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.*;

public class RevenueDashboardPanel extends WPanel {

    public RevenueDashboardPanel() {
        // 1. 设置主面板属性
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 10, 20));

        // 2. 添加标题栏 (带菜单按钮效果)
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titleLabel = new JLabel("Revenue");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        header.add(titleLabel, BorderLayout.WEST);
        
        // 模拟右上角的筛选按钮 (All, 1M, 6M, 1Y)
        header.add(createFilterPanel(), BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // 3. 添加中间的曲线图绘制区
        add(new RevenueChartArea(), BorderLayout.CENTER);

        // 4. 添加底部的图例标签
        add(createLegendPanel(), BorderLayout.SOUTH);
    }

    // --- 辅助组件：创建图例标签 ---
    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        panel.setOpaque(false);

        panel.add(new LegendItem(new Color(45, 211, 126), "Orders"));
        panel.add(new LegendItem(new Color(64, 158, 255), "Refunds"));

        return panel;
    }

    // --- 辅助组件：顶部筛选按钮 ---
    private JPanel createFilterPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        p.setOpaque(false);
        String[] filters = {"All", "1M", "6M", "1Y"};
        for (String f : filters) {
            JLabel btn = new JLabel(f);
            btn.setForeground(f.equals("All") ? new Color(64, 158, 255) : new Color(110, 120, 140));
            btn.setFont(new Font("SansSerif", Font.BOLD, 13));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            p.add(btn);
        }
        return p;
    }

    // --- 内部类：图例单个项目 ---
    private static class LegendItem extends JPanel {
        LegendItem(Color color, String text) {
            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));

            // 绘制小圆点图标
            JLabel dot = new JLabel(new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(color);
                    g2.fillOval(x, y + 4, 10, 10);
                    g2.dispose();
                }
                @Override public int getIconWidth() { return 10; }
                @Override public int getIconHeight() { return 18; }
            });

            JLabel label = new JLabel(text);
            label.setForeground(new Color(150, 160, 180));
            label.setFont(new Font("SansSerif", Font.PLAIN, 14));

            add(dot);
            add(label);
        }
    }

    // --- 内部类：曲线图绘制逻辑 ---
    private class RevenueChartArea extends JPanel {
        private float[] orders = {0, 15, 12, 18, 14, 22, 10, 18};
        private float[] refunds = {0, 10, 16, 8, 21, 5, 2, 23};

        public RevenueChartArea() { setOpaque(false); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int pLeft = 45, pBottom = 20, pTop = 30, pRight = 10;
            int w = getWidth() - pLeft - pRight;
            int h = getHeight() - pTop - pBottom;

            // 绘制横向参考线和坐标轴文字
            String[] yLabels = {"0k", "5k", "10k", "15k", "20k", "25k"};
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            for (int i = 0; i < yLabels.length; i++) {
                int yPos = pTop + h - (i * h / (yLabels.length - 1));
                g2.setColor(new Color(110, 120, 140));
                g2.drawString(yLabels[i], 5, yPos + 5);
                g2.setColor(new Color(255, 255, 255, 15));
                g2.drawLine(pLeft, yPos, pLeft + w, yPos);
            }

            // 绘制曲线
            drawCurve(g2, orders, pLeft, pTop, w, h, new Color(45, 211, 126), false);
            drawCurve(g2, refunds, pLeft, pTop, w, h, new Color(64, 158, 255), true);
            g2.dispose();
        }

        private void drawCurve(Graphics2D g2, float[] data, int x, int y, int w, int h, Color color, boolean dashed) {
            float step = (float) w / (data.length - 1);
            Path2D.Float path = new Path2D.Float();
            path.moveTo(x, y + h - (data[0]/25f*h));

            for (int i = 0; i < data.length - 1; i++) {
                float x1 = x + i * step, y1 = y + h - (data[i]/25f*h);
                float x2 = x + (i+1) * step, y2 = y + h - (data[i+1]/25f*h);
                path.curveTo(x1 + step/2, y1, x2 - step/2, y2, x2, y2);
            }

            if (!dashed) { // 实线带渐变
                Path2D.Float area = (Path2D.Float) path.clone();
                area.lineTo(x + w, y + h); area.lineTo(x, y + h); area.closePath();
                g2.setPaint(new GradientPaint(0, y, new Color(color.getRed(), color.getGreen(), color.getBlue(), 80), 0, y+h, new Color(0,0,0,0)));
                g2.fill(area);
                g2.setStroke(new BasicStroke(2.5f));
            } else { // 虚线
                g2.setStroke(new BasicStroke(2f, 1, 1, 1, new float[]{6, 6}, 0));
            }
            g2.setColor(color);
            g2.draw(path);
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new RevenueDashboardPanel());
        f.setSize(1000, 450);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}