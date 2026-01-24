package com.dillon.lw.view.home;

import com.dillon.lw.components.WPanel;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Path2D;

public class ProjectCardPanel extends WPanel {

    private float[] chartData;
    private Color themeColor;

    /**
     * @param title    标题 (如 "Active projects")
     * @param value    主数值 (如 "825")
     * @param subTitle 副标题 (如 "Projects this month")
     * @param data     折线数据点 (0.0f - 100.0f 或任意比例)
     * @param theme    主题颜色 (如绿色)
     * @param svgIcon  图标 (可以是任意 Icon 实现)
     */
    public ProjectCardPanel(String title, String value, String subTitle, float[] data, Color theme, String svgIcon) {
        this.chartData = data;
        this.themeColor = theme;

        // 1. 基本样式设置
        setLayout(new BorderLayout(15, 0));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // 2. 左侧文字容器 (使用 GridBagLayout 垂直排列文字)
        JPanel textContainer = new JPanel(new GridBagLayout());
        textContainer.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;

        // 图标标签
        JLabel iconLabel = new JLabel(new FlatSVGIcon(svgIcon,45,45));
        gbc.gridy = 0;
        gbc.gridheight = 3; // 图标跨三行
        gbc.insets = new Insets(0, 0, 0, 15);
        textContainer.add(iconLabel, gbc);

        // 标题
        gbc.gridx = 1;
        gbc.gridheight = 1;
        gbc.insets = new Insets(0, 0, 2, 0);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(new Color(150, 160, 180));
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        textContainer.add(lblTitle, gbc);

        // 数值
        gbc.gridy = 1;
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 36));
        textContainer.add(lblValue, gbc);

        // 副标题
        gbc.gridy = 2;
        JLabel lblSub = new JLabel(subTitle);
        lblSub.setForeground(new Color(110, 120, 140));
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        textContainer.add(lblSub, gbc);

        add(textContainer, BorderLayout.WEST);

        // 3. 右侧折线绘图区
        JPanel sparklineArea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawSparkline((Graphics2D) g, getWidth(), getHeight());
            }
        };
        sparklineArea.setOpaque(false);
        add(sparklineArea, BorderLayout.CENTER);
    }

    private void drawSparkline(Graphics2D g2, int w, int h) {
        if (chartData == null || chartData.length < 2) return;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        float max = 0.001f;
        for (float f : chartData) if (f > max) max = f;

        Path2D.Float path = new Path2D.Float();
        float step = (float) w / (chartData.length - 1);

        // 留出上下 10% 的缓冲边距，防止线条贴边
        float margin = h * 0.1f;
        float drawH = h - (margin * 2);

        for (int i = 0; i < chartData.length; i++) {
            float px = i * step;
            float py = margin + drawH - (chartData[i] / max * drawH);
            if (i == 0) path.moveTo(px, py);
            else path.lineTo(px, py);
        }

        // 填充渐变
        Path2D.Float area = (Path2D.Float) path.clone();
        area.lineTo(w, h);
        area.lineTo(0, h);
        area.closePath();

        g2.setPaint(new GradientPaint(0, margin, new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), 120),
                0, h, new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), 0)));
        g2.fill(area);

        // 画线
        g2.setColor(themeColor);
        g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(path);
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.getContentPane().setBackground(new Color(15, 18, 25));
        frame.setLayout(new BorderLayout());

        // 1. 模拟数据
        float[] dataPoints = {15, 40, 30, 55, 45, 80, 70, 90, 85, 100, 60, 40, 20};
        Color themeGreen = new Color(45, 211, 126);

        // 2. 创建面板 (svgIcon 参数可以传入你的 FlatSVGIcon)
        ProjectCardPanel card = new ProjectCardPanel(
                "Active projects",
                "825",
                "Projects this month",
                dataPoints,
                themeGreen,
                null // 此处替换为你的图标对象
        );

        card.setPreferredSize(new Dimension(450, 180));

        frame.add(card);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}