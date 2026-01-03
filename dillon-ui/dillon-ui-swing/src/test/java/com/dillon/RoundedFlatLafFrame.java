package com.dillon;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

public class RoundedFlatLafFrame {
    public static void main(String[] args) {
        // 1. 初始化皮肤
        FlatLightLaf.setup();
        
        // 2. 开启自定义装饰
        JFrame.setDefaultLookAndFeelDecorated(true);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("圆角组件示例");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);

            // --- 创建圆角搜索框 ---
            JTextField searchField = new JTextField(15);
//            searchField.setPlaceholder("搜索..."); // FlatLaf 特有属性
            // 设置圆角弧度为 20
            searchField.putClientProperty("JTextField.arc", 20);
            searchField.putClientProperty("JTextField.showClearButton", true);

            // --- 创建圆角按钮 ---
            JButton actionBtn = new JButton("发送");
            // 设置为胶囊形状（圆角半径极大）
            actionBtn.putClientProperty("JButton.arc", 999);
            actionBtn.setBackground(new Color(0, 120, 215));
            actionBtn.setForeground(Color.WHITE);

            // --- 将组件放入容器并添加到标题栏 ---
            JPanel container = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
            container.setOpaque(false);
            container.add(searchField);
            container.add(actionBtn);

            JRootPane rootPane = frame.getRootPane();
            rootPane.putClientProperty("JRootPane.titleBarTrailingComponent", container);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}