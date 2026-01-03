package com.dillon;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

public class RoundedButtonTest {
    public static void main(String[] args) {
        // 必须先初始化
        FlatLightLaf.setup();
        JFrame.setDefaultLookAndFeelDecorated(true);

        JFrame frame = new JFrame("圆角测试");
        frame.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        // --- 方案 A: 小圆角按钮 ---
        JButton btn1 = new JButton("小圆角 (15px)");
        btn1.putClientProperty("JButton.buttonType", "roundRect");
        btn1.putClientProperty("JButton.arc", 15);

        // --- 方案 B: 完全圆角 (胶囊形) ---
        JButton btn2 = new JButton("胶囊按钮");
        btn2.putClientProperty("JButton.buttonType", "roundRect");
        btn2.putClientProperty("JButton.arc", 999);
        btn2.setBackground(new Color(52, 152, 219));
        btn2.setForeground(Color.WHITE);

        // --- 方案 C: 放入标题栏的圆角按钮 ---
        JButton titleBtn = new JButton("⚙️");
        titleBtn.putClientProperty("JButton.buttonType", "toolBarButton"); // 工具栏样式
        titleBtn.putClientProperty("JButton.arc", 999); // 同样可以圆角

        frame.add(btn1);
        frame.add(btn2);
        frame.getRootPane().putClientProperty("JRootPane.titleBarTrailingComponent", titleBtn);

        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}