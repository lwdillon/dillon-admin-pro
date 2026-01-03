package com.dillon;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;

public class FlatLafRoundedFrameDemo {

    private static final int CORNER_RADIUS = 16;

    public static void main(String[] args) {
        // ① FlatLaf 初始化
        FlatDarkLaf.setup();


        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("FlatLaf Rounded Window");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // ② 让 FlatLaf 接管窗口装饰（关键）
            JFrame.setDefaultLookAndFeelDecorated(true);

            // ④ 根面板（圆角）
            JPanel root = new JPanel(new BorderLayout());
            root.setBackground(new Color(23, 27, 38)); // #171B26
            root.putClientProperty(
                    FlatClientProperties.STYLE,
                    "arc: " + (CORNER_RADIUS * 2)
            );

            // ⑤ 内容区
            JPanel content = new JPanel(new GridBagLayout());
            content.setOpaque(false);

            JButton button = new JButton("测试按钮");
            button.putClientProperty(
                    FlatClientProperties.STYLE,
                    "arc: 12"
            );
            content.add(button);

            root.add(content, BorderLayout.CENTER);
            frame.setContentPane(root);

            // ⑥ 最大化 / 还原切换圆角
            frame.addWindowStateListener(e -> {
                boolean maximized =
                        (e.getNewState() & JFrame.MAXIMIZED_BOTH) != 0;

                root.putClientProperty(
                        FlatClientProperties.STYLE,
                        maximized
                                ? "arc: 0"
                                : "arc: " + (CORNER_RADIUS * 2)
                );
                root.revalidate();
                root.repaint();
            });

            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}