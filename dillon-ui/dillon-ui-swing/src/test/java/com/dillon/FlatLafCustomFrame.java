package com.dillon;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

public class FlatLafCustomFrame {

    public static void main(String[] args) {
        // 1. 设置 Look and Feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        // 2. 必须在创建 JFrame 前开启装饰
        JFrame.setDefaultLookAndFeelDecorated(true);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("FlatLaf 自定义标题栏");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);

            // 3. 创建自定义按钮
            JButton settingsBtn = new JButton("⚙️");
            settingsBtn.setToolTipText("打开设置");
            // 消除按钮边框，使其更符合标题栏风格
            settingsBtn.putClientProperty("JButton.buttonType", "toolBarButton");
            
            settingsBtn.addActionListener(e -> {
                JOptionPane.showMessageDialog(frame, "设置按钮被点击了！");
            });

            // 4. 将按钮添加到标题栏
            // "JRootPane.titleBarLeadingComponent" -> 放在左侧（图标旁）
            // "JRootPane.titleBarTrailingComponent" -> 放在右侧（最小化按钮旁）
            JRootPane rootPane = frame.getRootPane();
            rootPane.putClientProperty("JRootPane.titleBarTrailingComponent", settingsBtn);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}