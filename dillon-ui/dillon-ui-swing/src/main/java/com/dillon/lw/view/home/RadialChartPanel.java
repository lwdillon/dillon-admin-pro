package com.dillon.lw.view.home;

import com.dillon.lw.components.WPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Arc2D;

/**
 * 这是一个一体化的 JPanel 统计组件
 */
public class RadialChartPanel extends WPanel {

    // 数据定义
    private final Color[] colors = {
            new Color(64, 158, 255), // 蓝色
            new Color(103, 194, 58), // 绿色
            new Color(245, 108, 108), // 红色
            new Color(230, 162, 60)  // 橙色
    };

    private final double[] percentages = {0.75, 0.62, 0.48, 0.35};
    private final String[] values = {"$161,466.24", "$56,411.33", "$81,981.22", "$12,432.51"};
    private final String[] perStrs = {"43.29%", "36.16%", "40.22%", "25.53%"};

    public RadialChartPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 2. 添加标题栏 (带菜单按钮效果)
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titleLabel = new JLabel("Current Statistic");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        header.add(titleLabel, BorderLayout.WEST);


        add(header, BorderLayout.NORTH);

        // --- 1. 添加圆环区域 ---
        add(new ChartArea(), BorderLayout.CENTER);

        // --- 2. 添加列表区域 ---
        JPanel listContainer = new JPanel();
        listContainer.setOpaque(false);
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));

        for (int i = 0; i < colors.length; i++) {
            listContainer.add(createStatRow(colors[i], perStrs[i], values[i]));
            if (i < colors.length - 1) {
                listContainer.add(Box.createVerticalStrut(15)); // 行间距
            }
        }
        add(listContainer, BorderLayout.SOUTH);
    }

    // 内部绘图类：处理圆环渲染
    private class ChartArea extends JPanel {
        ChartArea() {
            setOpaque(false);
            setPreferredSize(new Dimension(300, 300));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight()) - 40;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            int thickness = 16;
            int gap = 10;

            for (int i = 0; i < colors.length; i++) {
                int offset = i * (thickness + gap);
                int rSize = size - (offset * 2);

                // 绘制背景底环
                g2.setStroke(new BasicStroke(thickness));
                g2.setColor(UIManager.getColor("App.mainTabbedPaneBackground"));
                g2.draw(new Arc2D.Float(x + offset, y + offset, rSize, rSize, 0, 360, Arc2D.OPEN));

                // 绘制进度环
                g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(colors[i]);
                g2.draw(new Arc2D.Float(x + offset, y + offset, rSize, rSize, 90, (float) (-360 * percentages[i]), Arc2D.OPEN));
            }

            // 中心 100% 文本
            g2.setColor(UIManager.getColor("Label.foreground"));
            g2.setFont(new Font("SansSerif", Font.BOLD, 28));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString("100%", (getWidth() - fm.stringWidth("100%")) / 2, (getHeight() / 2) + (fm.getAscent() / 3));
            g2.dispose();
        }
    }

    // 私有辅助方法：创建数据行
    private JPanel createStatRow(Color color, String percent, String val) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        // 左侧部分
        JLabel leftLabel = new JLabel("  Income");
        leftLabel.setForeground(new Color(150, 160, 180));
        leftLabel.setIcon(new ColorDotIcon(color));

        // 中间百分比
        JLabel midLabel = new JLabel(" ↗ " + percent);
        midLabel.setForeground(color);
        midLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

        // 右侧数值
        JLabel rightLabel = new JLabel(val);
        rightLabel.setFont(new Font("Monospaced", Font.BOLD, 14));

        row.add(leftLabel, BorderLayout.WEST);
        row.add(midLabel, BorderLayout.CENTER);
        row.add(rightLabel, BorderLayout.EAST);
        return row;
    }

    // 绘制小圆点的图标类
    private static class ColorDotIcon implements Icon {
        private final Color color;

        ColorDotIcon(Color color) {
            this.color = color;
        }

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
            return 15;
        }

        @Override
        public int getIconHeight() {
            return 18;
        }
    }

    // --- 测试运行 ---
//    public static void main(String[] args) {
//        JFrame f = new JFrame();
//        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        f.add(new RadialChartPanel());
//        f.setSize(400, 600);
//        f.setLocationRelativeTo(null);
//        f.setVisible(true);
//    }
}