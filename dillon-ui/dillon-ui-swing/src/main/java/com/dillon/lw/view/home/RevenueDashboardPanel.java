package com.dillon.lw.view.home;

import com.dillon.lw.components.WPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;

public class RevenueDashboardPanel extends WPanel {

    private RevenueChartArea chartArea;

    public RevenueDashboardPanel() {
        setLayout(new BorderLayout());
        // 外部边距：顶部/左/右各25，底部缩减到 5 以留出更多空间给图表
        setBorder(new EmptyBorder(25, 25, 5, 25));

        // 1. 顶部：Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titleLabel = new JLabel("Revenue");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        header.add(titleLabel, BorderLayout.WEST);
        header.add(createFilterPanel(), BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // 2. 中间：图表 (占据最大化空间)
        chartArea = new RevenueChartArea();
        add(chartArea, BorderLayout.CENTER);

        // 3. 底部：图例 (压缩高度)
        add(createLegendPanel(), BorderLayout.SOUTH);
    }

    private JPanel createFilterPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
        p.setOpaque(false);
        String[] filters = {"All", "1M", "6M", "1Y"};
        for (String f : filters) {
            JLabel btn = new JLabel(f);
            btn.setForeground(f.equals("All") ? new Color(64, 158, 255) : new Color(110, 120, 140));
            btn.setFont(new Font("SansSerif", Font.BOLD, 13));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    for (Component c : p.getComponents()) c.setForeground(new Color(110, 120, 140));
                    btn.setForeground(new Color(64, 158, 255));
                    updateData(f);
                }
            });
            p.add(btn);
        }
        return p;
    }

    private JPanel createLegendPanel() {
        // 垂直间距设为 0，水平间距 20
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setOpaque(false);
        // 关键点：显式限制首选高度，让 BorderLayout.SOUTH 变窄
        panel.setPreferredSize(new Dimension(0, 30));

        panel.add(new LegendItem(new Color(45, 211, 126), "Orders"));
        panel.add(new LegendItem(new Color(64, 158, 255), "Refunds"));
        return panel;
    }

    private void updateData(String filter) {
        chartArea.resetSelection();
        switch (filter) {
            case "1M" ->
                    chartArea.setData(new float[]{10.5f, 45.2f, 18.8f, 28.1f}, new float[]{5.2f, 12.0f, 34.5f, 8.9f});
            case "6M" -> chartArea.setData(new float[]{5, 12, 18, 15, 25, 20}, new float[]{2, 8, 10, 5, 12, 15});
            case "1Y" -> chartArea.setData(new float[]{5, 10, 8, 15, 12, 20, 18, 25, 22, 30, 28, 45},
                    new float[]{2, 5, 4, 10, 8, 15, 14, 20, 18, 25, 22, 30});
            default -> chartArea.setData(new float[]{0, 15, 42, 10, 29, 48, 10, 28},
                    new float[]{0, 10, 16, 8, 21, 5, 2, 23});
        }
    }

    private class RevenueChartArea extends JPanel {
        private float[] orders = {0, 15, 42, 10, 29, 48, 10, 28};
        private float[] refunds = {0, 10, 16, 8, 21, 5, 2, 23};
        private int selectedIndex = -1;
        private final float MAX_VAL = 50f;

        private float animX = -100, animY = -100, opacity = 0f;
        private boolean isHovered = false;
        private Timer animTimer;

        public RevenueChartArea() {
            setOpaque(false);
            animTimer = new Timer(16, e -> updateAnimation());
            animTimer.start();

            MouseAdapter adapter = new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    calculateIndex(e.getX());
                    isHovered = true;
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    selectedIndex = -1;
                }
            };
            addMouseMotionListener(adapter);
        }

        private void updateAnimation() {
            if (isHovered && selectedIndex != -1) {
                opacity = Math.min(1.0f, opacity + 0.12f);
            } else {
                opacity = Math.max(0.0f, opacity - 0.12f);
            }

            if (selectedIndex >= 0 && selectedIndex < orders.length) {
                // 内部 Padding：上下缩减为 20，让绘图区更高
                int pL = 55, pT = 20, w = getWidth() - 80, h = getHeight() - 40;
                float step = (float) w / (orders.length - 1);

                float targetX = pL + (selectedIndex * step);
                float y1 = pT + h - (orders[selectedIndex] / MAX_VAL * h);
                float y2 = pT + h - (refunds[selectedIndex] / MAX_VAL * h);
                float targetY = Math.min(y1, y2);

                if (animX < 0) {
                    animX = targetX;
                    animY = targetY;
                } else {
                    animX += (targetX - animX) * 0.25f;
                    animY += (targetY - animY) * 0.25f;
                }
            }
            if (opacity > 0) repaint();
        }

        private void calculateIndex(int x) {
            int drawWidth = getWidth() - 80;
            if (drawWidth <= 0) return;
            float step = (float) drawWidth / (orders.length - 1);
            int idx = Math.round((float) (x - 55) / step);
            selectedIndex = Math.max(0, Math.min(orders.length - 1, idx));
        }

        public void resetSelection() {
            this.selectedIndex = -1;
            this.isHovered = false;
            this.opacity = 0f;
        }

        public void setData(float[] o, float[] r) {
            this.orders = o;
            this.refunds = r;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 绘图边距
            int pL = 55, pB = 20, pT = 20, pR = 25;
            int w = getWidth() - pL - pR, h = getHeight() - pT - pB;

            drawGrid(g2, pL, pT, w, h);
            drawCurve(g2, orders, pL, pT, w, h, new Color(45, 211, 126), 0.65f);
            drawCurve(g2, refunds, pL, pT, w, h, new Color(64, 158, 255), 0.6f);

            if (opacity > 0.01f && selectedIndex >= 0 && selectedIndex < orders.length) {
                drawInteraction(g2, pL, pT, w, h);
            }
            g2.dispose();
        }

        private void drawInteraction(Graphics2D g2, int x, int y, int w, int h) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            float step = (float) w / (orders.length - 1);
            int snapX = x + (int) (selectedIndex * step);

            g2.setColor(UIManager.getColor("App.hoverBackground"));
            g2.drawLine(snapX, y, snapX, y + h);

            float dotY1 = y + h - (orders[selectedIndex] / MAX_VAL * h);
            float dotY2 = y + h - (refunds[selectedIndex] / MAX_VAL * h);
            drawFocusDot(g2, snapX, dotY1, new Color(45, 211, 126));
            drawFocusDot(g2, snapX, dotY2, new Color(64, 158, 255));
            drawTooltip(g2, (int) animX, (int) animY, x, y, w, h);
        }

        private void drawFocusDot(Graphics2D g2, float dx, float dy, Color c) {
            g2.setColor(Color.WHITE);
            g2.fillOval((int) dx - 6, (int) dy - 6, 12, 12);
            g2.setColor(c);
            g2.fillOval((int) dx - 4, (int) dy - 4, 8, 8);
        }

        private void drawTooltip(Graphics2D g2, int currentX, int currentY, int x, int y, int w, int h) {
            String v1 = String.format("%.2fk", orders[selectedIndex]);
            String v2 = String.format("%.2fk", refunds[selectedIndex]);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            int tipW = Math.max(fm.stringWidth("Orders: " + v1), fm.stringWidth("Refunds: " + v2)) + 40;
            int tipH = 60;

            int tipX = (currentX + tipW + 20 > x + w) ? currentX - tipW - 15 : currentX + 15;
            int tipY = currentY - tipH - 15;
            if (tipY < y) tipY = currentY + 20;

            g2.setColor(UIManager.getColor("App.mainTabbedPaneBackground"));
            g2.fillRoundRect(tipX, tipY, tipW, tipH, 20, 20);
            g2.setColor(new Color(255, 255, 255, 50));
            g2.drawRoundRect(tipX, tipY, tipW, tipH, 20, 20);

            drawRow(g2, "Orders", v1, new Color(45, 211, 126), tipX + 10, tipY + 23);
            drawRow(g2, "Refunds", v2, new Color(64, 158, 255), tipX + 10, tipY + 45);
        }

        private void drawCurve(Graphics2D g2, float[] data, int x, int y, int w, int h, Color color, float op) {
            if (data.length < 2) return;
            float step = (float) w / (data.length - 1);
            Path2D.Float path = new Path2D.Float();

            // 计算起始点
            float firstY = y + h - (data[0] / MAX_VAL * h);
            path.moveTo(x, firstY);

            for (int i = 0; i < data.length - 1; i++) {
                float x1 = x + i * step, y1 = y + h - (data[i] / MAX_VAL * h);
                float x2 = x + (i + 1) * step, y2 = y + h - (data[i + 1] / MAX_VAL * h);
                path.curveTo(x1 + step / 2, y1, x2 - step / 2, y2, x2, y2);
            }

            // --- 显著增强渐变效果 ---

            // 1. 创建填充区域
            Path2D.Float area = (Path2D.Float) path.clone();
            area.lineTo(x + w, y + h);
            area.lineTo(x, y + h);
            area.closePath();

            // 2. 增强色彩饱满度
            // 增加 op (opacity) 的值会更明显，建议传入 0.4f 或更高
            Color startColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (op * 255));
            // 终点颜色：保持色彩相同，但透明度设为 0，这比向背景色过渡更亮丽
            Color endColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0);

            // 3. 动态渐变：从图表顶部 (y) 到 底部 (y + h)
            // 如果想要更明显的视觉冲击，可以将渐变终点设为 y + h * 0.8f，让颜色更集中在上方
            g2.setPaint(new GradientPaint(0, y, startColor, 0, y + h, endColor));
            g2.fill(area);

            // 4. 线条增强：加粗线条并开启平滑
            g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(color);
            g2.draw(path);
        }

        private void drawRow(Graphics2D g2, String l, String v, Color c, int x, int y) {
            g2.setColor(c);
            g2.fillOval(x, y - 8, 8, 8);
            g2.setColor(UIManager.getColor("Label.disabledForeground"));
            g2.drawString(l + ":", x + 15, y);
            g2.setColor(UIManager.getColor("Label.foreground"));
            g2.drawString(v, x + 15 + g2.getFontMetrics().stringWidth(l + ": "), y);
        }

        private void drawGrid(Graphics2D g2, int x, int y, int w, int h) {
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            for (int i = 0; i <= 5; i++) {
                int yPos = y + h - (i * h / 5);
                g2.setColor(UIManager.getColor("Label.disabledForeground"));
                g2.drawString((i * 10) + "k", 10, yPos + 5);
                g2.setColor(UIManager.getColor("App.mainBackground"));
                g2.drawLine(x, yPos, x + w, yPos);
            }
        }
    }

    private static class LegendItem extends JPanel {
        LegendItem(Color color, String text) {
            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
            JLabel dot = new JLabel(new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(color);
                    g2.fillOval(x, y + 4, 10, 10);
                    g2.dispose();
                }

                @Override
                public int getIconWidth() {
                    return 10;
                }

                @Override
                public int getIconHeight() {
                    return 18;
                }
            });
            JLabel label = new JLabel(text);
            label.setForeground(new Color(150, 160, 180));
            label.setFont(new Font("SansSerif", Font.PLAIN, 14));
            add(dot);
            add(label);
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Revenue Dashboard");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new RevenueDashboardPanel());
        f.setSize(950, 500);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}