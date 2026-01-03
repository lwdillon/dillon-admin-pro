package com.dillon;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;

public class FlatLaf37ToolbarInTitleBar {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. 启用 FlatLaf
            FlatLightLaf.setup();

            // 2. 创建窗口
            JFrame frame = new JFrame("FlatLaf 3.7 TitleBar Demo");

            // 3. 启用自定义标题栏（关键）
            frame.getRootPane().putClientProperty(
                    FlatClientProperties.USE_WINDOW_DECORATIONS, true
            );

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new BorderLayout());

            // 4. 标题栏（JToolBar 在这里）
            frame.add(createTitleBar(), BorderLayout.NORTH);

            // 5. 主内容
            frame.add(createContent(), BorderLayout.CENTER);

            frame.setVisible(true);
        });
    }

    private static JComponent createTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        // 左侧：标题
        JLabel title = new JLabel("调度申报系统");
        title.setFont(title.getFont().deriveFont(Font.BOLD));
        title.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 12));

        // 中间：工具栏
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setOpaque(false);

        toolBar.add(new JButton("新建"));
        toolBar.add(new JButton("保存"));
        toolBar.add(new JButton("导出"));
        toolBar.addSeparator();
        toolBar.add(new JButton("校验"));
        toolBar.add(new JButton("发布"));

        // 右侧：占位，避免遮挡系统按钮
        JPanel rightGap = new JPanel();
        rightGap.setOpaque(false);
        rightGap.setPreferredSize(new Dimension(120, 1));

        titleBar.add(title, BorderLayout.WEST);
        titleBar.add(toolBar, BorderLayout.CENTER);
        titleBar.add(rightGap, BorderLayout.EAST);

        return titleBar;
    }

    private static JComponent createContent() {
        JTextArea area = new JTextArea("FlatLaf 3.7 示例\n" +
                "                - 无 TITLE_BAR\n" +
                "                - NORTH 区域自动作为标题栏\n" +
                "                - 支持窗口拖动\n" +
                "                - Win / Linux / macOS 行为一致");
        return new JScrollPane(area);
    }
}