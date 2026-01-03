package com.dillon;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;

public class FlatLaf37WorkingDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // 1. 必须最先设置 LAF
            FlatLightLaf.setup();

            // 2. 创建窗口
            JFrame frame = new JFrame("FlatLaf 3.7 Demo");

            // ❗关键：关闭系统标题栏
            frame.setUndecorated(true);

            // ❗关键：启用 FlatLaf 自绘窗口装饰
            frame.getRootPane().putClientProperty(
                    FlatClientProperties.USE_WINDOW_DECORATIONS, true
            );

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new BorderLayout());

            // 标题栏（工具栏在这里）
            frame.add(createTitleBar(), BorderLayout.NORTH);

            // 主内容
            frame.add(new JScrollPane(new JTextArea("主内容区域")), BorderLayout.CENTER);

            frame.setVisible(true);
        });
    }

    private static JComponent createTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        JLabel title = new JLabel("调度申报系统");
        title.setFont(title.getFont().deriveFont(Font.BOLD));
        title.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 12));

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setOpaque(false);

        toolBar.add(new JButton("新建"));
        toolBar.add(new JButton("保存"));
        toolBar.add(new JButton("导出"));

        JPanel rightGap = new JPanel();
        rightGap.setOpaque(false);
        rightGap.setPreferredSize(new Dimension(120, 1));

        titleBar.add(title, BorderLayout.WEST);
        titleBar.add(toolBar, BorderLayout.CENTER);
        titleBar.add(rightGap, BorderLayout.EAST);

        return titleBar;
    }
}