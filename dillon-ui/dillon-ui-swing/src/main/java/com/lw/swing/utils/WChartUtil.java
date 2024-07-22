package com.lw.swing.utils;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;

/**
 * @author wenli
 * @date 2024/05/09
 */
public class WChartUtil {

    public static void createChartUI(JFreeChart chart) {
        Color color = new Color(0f, 0f, 0f, .0f);
        Color foreground = UIManager.getColor("Label.foreground");
        chart.setTextAntiAlias(true);
        chart.getTitle().setPaint(foreground);
        chart.setBackgroundPaint(color);
        chart.getPlot().setBackgroundPaint(color);
        chart.getXYPlot().setDomainGridlineStroke(new BasicStroke());
        chart.getXYPlot().setOutlinePaint(color);
        chart.getXYPlot().setRangeGridlinePaint(foreground);
        chart.getXYPlot().setRangeGridlinesVisible(true);
        chart.getXYPlot().setDomainGridlinePaint(color);
        chart.getXYPlot().setDomainGridlinesVisible(false);
        chart.getXYPlot().getDomainAxis().setAxisLinePaint(foreground);
        chart.getXYPlot().getDomainAxis().setTickLabelFont(new Font("宋体", Font.BOLD, 16));
        chart.getXYPlot().getRangeAxis().setTickLabelFont(new Font("宋体", Font.BOLD, 16));
        chart.getXYPlot().getDomainAxis().setTickLabelPaint(foreground);
        chart.getXYPlot().getRangeAxis().setAxisLinePaint(foreground);
        chart.getXYPlot().getRangeAxis().setTickLabelPaint(foreground);
        chart.getLegend().setBackgroundPaint(color);
        chart.setAntiAlias(true);
        chart.getLegend().setBorder(0, 0, 0, 0);
        chart.getLegend().setItemPaint(foreground);
        chart.getLegend().setPosition(RectangleEdge.TOP);
    }

    public static Double[] getRangeValue(TimeSeriesCollection chartData) {

        double min = 0d;
        double max = 0d;

        for (int i = 0; i < chartData.getSeriesCount(); i++) {
            TimeSeries timeSeries = chartData.getSeries(i);
            for (int j = 0; j < timeSeries.getItemCount(); j++) {
                Number number = timeSeries.getValue(j);
                if (ObjectUtil.isNull(number)) {
                    continue;
                }
                min = NumberUtil.min(min, number.doubleValue());
                max = NumberUtil.max(max, number.doubleValue());
            }
        }

        return new Double[]{min - 50D, max + 50D};

    }
}
