package com.dillon;

import javax.swing.*;
import java.awt.*;

public class GlowingLabel extends JLabel {

    private final Color GLOW_COLOR = new Color(255, 0, 0, 150); // 红色，部分透明
    private final int GLOW_RADIUS = 30; // 光晕的最大半径

    public GlowingLabel(String text) {
        super(text);
        // 确保 JLabel 背景是透明的，这样才能看到光晕
        setOpaque(false);
        // 设置字体颜色以便在光晕上清晰显示
        setForeground(Color.WHITE); 
        setFont(new Font("SansSerif", Font.BOLD, 18));
        setHorizontalAlignment(CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 1. 调用父类方法，清空背景
        super.paintComponent(g);

        // 2. 将 Graphics 对象转换为 Graphics2D
        Graphics2D g2d = (Graphics2D) g.create();

        int w = getWidth();
        int h = getHeight();

        // 确定中心点
        float centerX = w / 2.0f;
        float centerY = h / 2.0f;

        // 确定半径
        // 确保半径大于中心到最远边缘的距离，以覆盖全部区域
        float radius = (float) Math.hypot(w / 2.0f, h / 2.0f);
        // 另一种简单做法：float radius = Math.max(w, h) / 2.0f;

        // 3. 创建径向渐变 (Radial Gradient)

        // 颜色分布：
        float[] fractions = {
                0.0f,     // 中心 (0%)
                1.0f      // 边缘 (100%)
        };

        // 颜色数组：
        Color[] colors = {
                new Color(255, 0, 0, 89), // 中心：完全不透明的红色 (Alpha=255)
                new Color(255, 0, 0, 0)    // 边缘：完全透明 (Alpha=0)
        };

        // 创建 RadialGradientPaint
        RadialGradientPaint rgp = new RadialGradientPaint(
                centerX,
                centerY,
                radius,
                fractions,
                colors,
                MultipleGradientPaint.CycleMethod.NO_CYCLE
        );

        // 4. 设置画笔为径向渐变
        g2d.setPaint(rgp);

        // 5. 填充整个组件区域
        g2d.fillRect(0, 0, w, h);

        // 6. 释放 Graphics2D 资源
        g2d.dispose();
    }
    
    // --- 示例用法 (main方法) ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("光晕效果示例");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            // 为了更好地展示光晕，使用一个深色背景的 JPanel
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(Color.DARK_GRAY);

            // 创建自定义的 GlowingLabel 实例
            JLabel titleLabel = new GlowingLabel("后台管理系统");
            titleLabel.setPreferredSize(new Dimension(200, 100)); // 设定一个大小
            
            panel.add(titleLabel);
            
            frame.add(panel);
            frame.pack();
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null); // 居中显示
            frame.setVisible(true);
        });
    }
}