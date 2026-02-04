package com.dillon;

import javax.swing.*;
import java.awt.*;

public class LiquidGlassDemo extends JFrame {

    public LiquidGlassDemo() {
        setTitle("Swing Liquid Glass Border Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));

        // --- 浅色模式面板 ---
        JPanel lightContainer = new JPanel(new GridBagLayout());
        lightContainer.setBackground(new Color(220, 230, 240)); // 淡蓝色背景
        
        JButton lightButton = new JButton("Light Glass");
        styleButton(lightButton, new Color(255, 255, 255, 100), false);
        lightContainer.add(lightButton);

        // --- 暗色模式面板 ---
        JPanel darkContainer = new JPanel(new GridBagLayout());
        darkContainer.setBackground(new Color(25, 25, 35)); // 深色背景
        
        JButton darkButton = new JButton("Dark Glass");
        styleButton(darkButton, new Color(50, 50, 60, 150), true);
        darkContainer.add(darkButton);

        add(lightContainer);
        add(darkContainer);
    }

    private void styleButton(JButton btn, Color bg, boolean isDark) {
        btn.setPreferredSize(new Dimension(150, 60));
        btn.setContentAreaFilled(false); // 关键：让背景透明，显示自定义绘制
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setForeground(isDark ? Color.WHITE : Color.DARK_GRAY);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        // 应用我们定义的边框
        btn.setBorder(new LiquidGlassBorder(25, isDark));
        
        // 简单的背景填充以配合边框
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void update(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 25, 25);
                super.update(g2, c);
                g2.dispose();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LiquidGlassDemo().setVisible(true);
        });
    }
}