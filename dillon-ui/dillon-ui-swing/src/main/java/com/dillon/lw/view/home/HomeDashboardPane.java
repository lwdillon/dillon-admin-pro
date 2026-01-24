package com.dillon.lw.view.home;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * 科技感主页仪表盘 - 完整版
 * 包含：数据卡片、概览图表、营收柱状图、项目进度表格、统计圆环
 */
public class HomeDashboardPane extends JPanel {

    public HomeDashboardPane() {
        initComponents();
    }

    private void initComponents() {
        // 1. 基础设置：透明背景以展示父容器渐变色
        setOpaque(false);
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 2. 顶部统计卡片区域 (4列)
        JPanel topPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        topPanel.setOpaque(false);
        topPanel.add(new StatCard("利润总额", "$559.25k", "+16.24%", true));
        topPanel.add(new StatCard("销售额", "8.5k", "+12.10%", false));
        topPanel.add(new StatCard("活跃用户", "38.3k", "+5.43%", false));
        topPanel.add(new StatCard("订单总量", "1,250", "+22.50%", true));

        // 3. 中间混合图表区域 (1:1 比例)
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        centerPanel.setOpaque(false);
        centerPanel.add(new ChartWrapperPanel("业务概览", createDonutChart()));
        centerPanel.add(new ChartWrapperPanel("营收趋势 (Revenue)", createBarChart()));

        // 4. 底部详细数据区域 (7:3 比例)
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // 左侧：活动项目表格
        gbc.gridx = 0; gbc.weightx = 0.7;
        bottomPanel.add(new ChartWrapperPanel("活动项目 (Active Projects)", new ProjectTablePanel()), gbc);

        // 右侧：实时统计圆环
        gbc.gridx = 1; gbc.weightx = 0.3;
        gbc.insets = new Insets(0, 15, 0, 0);
        bottomPanel.add(new ChartWrapperPanel("数据统计", createRadialChart()), gbc);

        // 组装整体布局
        JPanel contentContainer = new JPanel(new BorderLayout(0, 15));
        contentContainer.setOpaque(false);
        contentContainer.add(centerPanel, BorderLayout.NORTH);
        contentContainer.add(bottomPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(contentContainer, BorderLayout.CENTER);
    }

    // ===================================================================================
    // 内部组件类：统计卡片
    // ===================================================================================
    class StatCard extends JPanel {
        public StatCard(String title, String value, String trend, boolean showIcon) {
            setOpaque(false);
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JLabel titleLabel = new JLabel(title);
            titleLabel.setForeground(new Color(200, 200, 200));

            JLabel trendLabel = new JLabel(trend + " ↗");
            trendLabel.setForeground(new Color(100, 234, 145)); // 趋势绿

            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            top.add(titleLabel, BorderLayout.WEST);
            top.add(trendLabel, BorderLayout.EAST);

            JLabel valLabel = new JLabel(value);
            valLabel.setFont(new Font("Verdana", Font.BOLD, 26));
            valLabel.setForeground(Color.WHITE);

            add(top, BorderLayout.NORTH);
            add(valLabel, BorderLayout.CENTER);

            if (showIcon) {
                JLabel icon = new JLabel(new FlatSVGIcon("icons/dashboard/chip.svg", 30, 30));
                add(icon, BorderLayout.EAST);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 255, 255, 15)); // 半透明白
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            g2.dispose();
        }
    }

    // ===================================================================================
    // 内部组件类：图表容器包装器
    // ===================================================================================
    class ChartWrapperPanel extends JPanel {
        public ChartWrapperPanel(String title, JComponent content) {
            setOpaque(false);
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
            titleLabel.setForeground(new Color(0, 216, 255)); // 科技蓝
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

            add(titleLabel, BorderLayout.NORTH);
            add(content, BorderLayout.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(20, 20, 35, 120)); // 深色半透明背景
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            g2.setColor(new Color(255, 255, 255, 20)); // 微弱边框
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            g2.dispose();
        }
    }

    // ===================================================================================
    // 内部组件类：项目表格
    // ===================================================================================
    class ProjectTablePanel extends JPanel {
        public ProjectTablePanel() {
            setOpaque(false);
            setLayout(new BorderLayout());

            String[] columns = {"项目名称", "负责人", "进度", "状态", "截止日期"};
            DefaultTableModel model = new DefaultTableModel(new Object[][]{
                    {"Brand Logo Design", "Bessie Cooper", "45%", "进行中", "2023-09-09"},
                    {"UI/UX Refactor", "Albert Flores", "80%", "待审核", "2023-10-12"},
                    {"Mobile App Dev", "Devin Brown", "30%", "进行中", "2023-12-01"},
                    {"Security Audit", "Jane Cooper", "100%", "已完成", "2023-08-20"}
            }, columns);

            JTable table = new JTable(model);
            table.setRowHeight(40);
            table.setShowGrid(false);
            table.setOpaque(false);
            table.setSelectionBackground(new Color(255, 255, 255, 20));

            // 自定义渲染器使表格透明并居中
            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSel, boolean hasF, int row, int col) {
                    Component c = super.getTableCellRendererComponent(table, value, isSel, hasF, row, col);
                    c.setForeground(new Color(220, 220, 220));
                    ((JLabel)c).setHorizontalAlignment(SwingConstants.CENTER);
                    return c;
                }
            };
            renderer.setOpaque(false);
            table.setDefaultRenderer(Object.class, renderer);

            JScrollPane sp = new JScrollPane(table);
            sp.setOpaque(false);
            sp.getViewport().setOpaque(false);
            sp.setBorder(BorderFactory.createEmptyBorder());

            add(sp, BorderLayout.CENTER);
        }
    }

    // ===================================================================================
    // 图表生成私有方法 (使用 JFreeChart)
    // ===================================================================================

    private ChartPanel createBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(45, "Revenue", "Jan");
        dataset.addValue(70, "Revenue", "Feb");
        dataset.addValue(55, "Revenue", "Mar");
        dataset.addValue(85, "Revenue", "Apr");

        JFreeChart chart = ChartFactory.createBarChart(null, null, null, dataset, PlotOrientation.VERTICAL, false, false, false);
        chart.setBackgroundPaint(new Color(0,0,0,0));

        BarRenderer renderer = (BarRenderer) chart.getCategoryPlot().getRenderer();
        renderer.setSeriesPaint(0, new Color(45, 140, 255));
        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter()); // 去除渐变阴影
        renderer.setShadowVisible(false);

        chart.getCategoryPlot().setBackgroundPaint(new Color(0,0,0,0));
        chart.getCategoryPlot().setOutlineVisible(false);
        chart.getCategoryPlot().setRangeGridlinePaint(new Color(255,255,255,30));

        return new ChartPanel(chart);
    }

    private ChartPanel createDonutChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("已完成", 45.7);
        dataset.setValue("剩余", 54.3);

        JFreeChart chart = ChartFactory.createPieChart(null, dataset, false, false, false);
        chart.setBackgroundPaint(new Color(0,0,0,0));

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(0,0,0,0));
        plot.setOutlineVisible(false);
        plot.setSectionPaint("已完成", new Color(0, 216, 255));
        plot.setSectionPaint("剩余", new Color(255, 255, 255, 20));
        plot.setShadowPaint(null);
        plot.setLabelGenerator(null); // 隐藏标签

        return new ChartPanel(chart);
    }

    private ChartPanel createRadialChart() {
        // 简化版：使用 XYArea 图表模拟多层圆环统计
        XYSeries series = new XYSeries("Stats");
        for(int i=0; i<10; i++) series.add(i, Math.random() * 10);

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(null, null, null, dataset);
        chart.setBackgroundPaint(new Color(0,0,0,0));

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(new Color(0,0,0,0));
        plot.setOutlineVisible(false);
        plot.setRenderer(new XYAreaRenderer());
        plot.getRenderer().setSeriesPaint(0, new Color(255, 100, 100, 150));

        return new ChartPanel(chart);
    }
}