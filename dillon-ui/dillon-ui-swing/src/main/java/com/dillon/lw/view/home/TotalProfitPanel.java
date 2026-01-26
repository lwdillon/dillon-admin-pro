package com.dillon.lw.view.home;

import com.dillon.lw.components.WPanel;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TotalProfitPanel extends WPanel {

    private String title;
    private String value;
    private String trend;
    private Color themeColor;

    /**
     * @param title      标题 (如 "Total profit")
     * @param value      主数值 (如 "$559.25k")
     * @param trend      增长率 (如 "+16.24 %")
     * @param themeColor 主题色 (如绿色)
     */
    public TotalProfitPanel(String title, String value, String trend, Color themeColor) {
        this.title = title;
        this.value = value;
        this.trend = trend;
        this.themeColor = themeColor;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. 左侧内容容器
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.anchor = GridBagConstraints.WEST;

        // 标题
        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(new Color(150, 160, 180));
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 18));
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 15, 0);
        leftPanel.add(lblTitle, gbc);

        // 主数值
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 28));
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 15, 0);
        leftPanel.add(lblValue, gbc);

        // 底部链接
        JLabel lblLink = new JLabel("<html><u>View all orders</u></html>");
        lblLink.setForeground(new Color(64, 158, 255));
        lblLink.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 0, 0);
        leftPanel.add(lblLink, gbc);

        add(leftPanel, BorderLayout.CENTER);

        // 2. 右侧趋势与图标容器
        JPanel rightPanel = new JPanel(new BorderLayout());

        // 增长趋势文字
        JLabel lblTrend = new JLabel("↗  " + trend);
        lblTrend.setForeground(themeColor);
        lblTrend.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTrend.setHorizontalAlignment(SwingConstants.RIGHT);
        rightPanel.add(lblTrend, BorderLayout.NORTH);
        // 右下角带发光效果的图标
        JLabel lbSvg =new JLabel("", JLabel.CENTER);
        lbSvg.setOpaque(true);
        lbSvg.setIcon(new FlatSVGIcon("icons/jinbi.svg",60,60));
        rightPanel.add(lbSvg, BorderLayout.CENTER);

        add(rightPanel, BorderLayout.EAST);
    }



    // 测试
//    public static void main(String[] args) {
//        JFrame f = new JFrame();
//        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        f.getContentPane().setBackground(new Color(12, 16, 22));
//        f.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 50));
//
//        TotalProfitPanel panel = new TotalProfitPanel(
//            "Total profit",
//            "$559.25k",
//            "+16.24 %",
//            new Color(45, 211, 126)
//        );
//        panel.setPreferredSize(new Dimension(400, 200));
//
//        f.add(panel);
//        f.pack();
//        f.setLocationRelativeTo(null);
//        f.setVisible(true);
//    }
}