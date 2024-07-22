package com.lw.swing;


import com.lw.swing.theme.LightTheme;
import com.lw.swing.view.MainFrame;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;

import javax.swing.*;
import java.awt.*;

public class DillonSwingUiApplication {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                TimingSource ts = new SwingTimerTimingSource();
                Animator.setDefaultTimingSource(ts);
                ts.init();
                UIManager.setLookAndFeel(LightTheme.class.getName());


            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            StandardChartTheme chartTheme = (StandardChartTheme) StandardChartTheme.createJFreeTheme();
            chartTheme.setExtraLargeFont(new Font("宋体", Font.BOLD, 20)); // 设置标题字体
            chartTheme.setLargeFont(new Font("宋体", Font.BOLD, 15)); // 设置轴标签字体
            chartTheme.setRegularFont(new Font("宋体", Font.PLAIN, 12)); // 设置图例字体
            ChartFactory.setChartTheme(chartTheme);

            MainFrame frame = MainFrame.getInstance();
            frame.setTitle("Dillon-Pro-管理系统");
            frame.showLogin();
        });
    }

}
