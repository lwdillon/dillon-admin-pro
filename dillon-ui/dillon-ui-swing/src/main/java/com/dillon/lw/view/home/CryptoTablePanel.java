package com.dillon.lw.view.home;

import com.dillon.lw.components.WPanel;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class CryptoTablePanel extends WPanel {

    public CryptoTablePanel() {
        // 1. 面板基础设置
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 2. 顶部标题栏
        add(createHeader(), BorderLayout.NORTH);

        // 3. 表格主体
        add(createTableArea(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Market Overview");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        // 右侧更多按钮 (三个点图标)
        JLabel moreBtn = new JLabel(" \u22EE "); // 垂直三点符号
        moreBtn.setForeground(new Color(150, 160, 180));
        moreBtn.setFont(new Font("Serif", Font.BOLD, 24));
        moreBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        header.add(titleLabel, BorderLayout.WEST);
        header.add(moreBtn, BorderLayout.EAST);
        return header;
    }

    private JScrollPane createTableArea() {
        // 数据与列定义
        String[] columns = {"SR No.", "Currency", "Price", "Pairs", "24 High", "24 Low", "Market Volume", "Volume %", "Action"};
        Object[][] data = {
                {"01", "Solana (SOL)", "$17,491.16", "XRM/USDT", "$31,578.35", "$8691.75", "$9,847,327", "+1.92%", "Trade Now"},
                {"02", "Ethereum (ETH)", "$2,491.16", "ETH/USDT", "$3,578.35", "$2491.75", "$1,847,327", "+2.45%", "Trade Now"},
                {"03", "Bitcoin (BTC)", "$47,491.16", "BTC/USDT", "$61,578.35", "$41691.75", "$5,847,327", "-0.92%", "Trade Now"},
                {"04", "Bitcoin (BTC)", "$47,491.16", "BTC/USDT", "$61,578.35", "$41691.75", "$5,847,327", "-0.92%", "Trade Now"},
                {"05", "Bitcoin (BTC)", "$47,491.16", "BTC/USDT", "$61,578.35", "$41691.75", "$5,847,327", "-0.92%", "Trade Now"},
                {"06", "Bitcoin (BTC)", "$47,491.16", "BTC/USDT", "$61,578.35", "$41691.75", "$5,847,327", "-0.92%", "Trade Now"},
                {"07", "Bitcoin (BTC)", "$47,491.16", "BTC/USDT", "$61,578.35", "$41691.75", "$5,847,327", "-0.92%", "Trade Now"},
                {"08", "Bitcoin (BTC)", "$47,491.16", "BTC/USDT", "$61,578.35", "$41691.75", "$5,847,327", "-0.92%", "Trade Now"},
                {"09", "Bitcoin (BTC)", "$47,491.16", "BTC/USDT", "$61,578.35", "$41691.75", "$5,847,327", "-0.92%", "Trade Now"},
                {"10", "Bitcoin (BTC)", "$47,491.16", "BTC/USDT", "$61,578.35", "$41691.75", "$5,847,327", "-0.92%", "Trade Now"},
                {"11", "Bitcoin (BTC)", "$47,491.16", "BTC/USDT", "$61,578.35", "$41691.75", "$5,847,327", "-0.92%", "Trade Now"},
                {"12", "Bitcoin (BTC)", "$47,491.16", "BTC/USDT", "$61,578.35", "$41691.75", "$5,847,327", "-0.92%", "Trade Now"},
                {"13", "Bitcoin (BTC)", "$47,491.16", "BTC/USDT", "$61,578.35", "$41691.75", "$5,847,327", "-0.92%", "Trade Now"},
                {"14", "Bitcoin (BTC)", "$47,491.16", "BTC/USDT", "$61,578.35", "$41691.75", "$5,847,327", "-0.92%", "Trade Now"},
                {"15", "Bitcoin (BTC)", "$47,491.16", "BTC/USDT", "$61,578.35", "$41691.75", "$5,847,327", "-0.92%", "Trade Now"},
                {"16", "Cardano (ADA)", "$1.16", "ADA/USDT", "$3.35", "$0.75", "$47,327", "+5.12%", "Trade Now"}
        };

        JTable table = new JTable(new DefaultTableModel(data, columns));

        // 样式配置
        table.setRowHeight(55);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);

        // 表头定制
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 13));

        // 应用列渲染器
        table.getColumnModel().getColumn(1).setCellRenderer(new CurrencyRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new TrendRenderer());
        table.getColumnModel().getColumn(8).setCellRenderer(new ButtonRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(23, 29, 39));
        return scroll;
    }

    // --- 自定义渲染器 ---

    private class CurrencyRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSel, boolean hasF, int row, int col) {
            JPanel p = new JPanel(new BorderLayout(7, 7));
            p.setBackground(isSel ? table.getSelectionBackground() : table.getBackground());
            p.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
            // 绘制圆形 Logo 背景
            JLabel icon = new JLabel(new FlatSVGIcon("icons/currency_bch.svg", 30, 30));

            JLabel label = new JLabel(value.toString());
            label.setForeground(new Color(64, 158, 255));
            p.add(icon, BorderLayout.WEST);
            p.add(label, BorderLayout.CENTER);
            return p;
        }
    }

    private class TrendRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSel, boolean hasF, int row, int col) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSel, hasF, row, col);
            String val = value.toString();
            // 绿色表示上升，红色表示下降
            if (val.startsWith("+")) {
                l.setForeground(new Color(45, 211, 126));
                l.setText("↗ " + val);
            } else {
                l.setForeground(new Color(245, 108, 108));
                l.setText("↘ " + val.replace("-", ""));
            }
            l.setHorizontalAlignment(CENTER);
            return l;
        }
    }

    private class ButtonRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSel, boolean hasF, int row, int col) {
            JPanel p = new JPanel(new GridBagLayout());
            p.setOpaque(true);
            p.setBackground(isSel ? table.getSelectionBackground() : table.getBackground());

            JButton btn = new JButton(value.toString());
            btn.setBackground(new Color(64, 158, 255, 100)); // 深灰蓝色按钮
            btn.setForeground(new Color(64, 158, 255));
            btn.setFont(new Font("SansSerif", Font.BOLD, 12));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

            p.add(btn);
            return p;
        }
    }


}