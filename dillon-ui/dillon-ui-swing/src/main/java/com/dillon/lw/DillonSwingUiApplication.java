package com.dillon.lw;


import com.dillon.lw.http.RetrofitServiceManager;
import com.dillon.lw.theme.LightTheme;
import com.dillon.lw.view.frame.MainFrame;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DillonSwingUiApplication {

    public static void main(String[] args) {

        // 1️⃣ EDT 异常统一兜底（Swing 核心）
        Toolkit.getDefaultToolkit()
                .getSystemEventQueue()
                .push(new ExceptionEventQueue());

        // 2️⃣ 后台线程异常兜底
        Thread.setDefaultUncaughtExceptionHandler(
                (t, e) -> SwingExceptionHandler.handle(e)
        );
        loadApplicationProperties();
        RetrofitServiceManager.getInstance();
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

            MainFrame.getInstance().showLogin();
        });
    }


    private static void loadApplicationProperties() {
        Properties properties = new Properties();
        try (InputStreamReader in = new InputStreamReader(Resources.getResourceAsStream("/application.properties"),
                UTF_8)) {
            properties.load(in);
            properties.forEach((key, value) -> System.setProperty(
                    String.valueOf(key),
                    String.valueOf(value)
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
