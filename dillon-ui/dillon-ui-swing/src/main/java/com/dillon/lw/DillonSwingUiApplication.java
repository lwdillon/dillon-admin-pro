package com.dillon.lw;


import com.dillon.lw.config.AppPrefs;
import com.dillon.lw.http.forest.ForestConfig;
import com.dillon.lw.theme.DarkTheme;
import com.dillon.lw.theme.LightTheme;
import com.dillon.lw.view.frame.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.util.SystemInfo;
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

import static com.dillon.lw.config.AppPrefs.KEY_CURRENT_USER;
import static com.dillon.lw.config.AppPrefs.KEY_UI_THEME;
import static java.nio.charset.StandardCharsets.UTF_8;

public class DillonSwingUiApplication {
    static {
        TimingSource ts = new SwingTimerTimingSource();
        Animator.setDefaultTimingSource(ts);
        ts.init();
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


    public static void main(String[] args) {



        loadApplicationProperties();
        if( SystemInfo.isMacOS ) {
            // enable screen menu bar
            // (moves menu bar from JFrame window to top of screen)
            System.setProperty( "apple.laf.useScreenMenuBar", "true" );

            // application name used in screen menu bar
            // (in first menu after the "apple" menu)
            String title= System.getProperty("app.name");
            System.setProperty( "apple.awt.application.name", title);

            // appearance of window title bars
            // possible values:
            //   - "system": use current macOS appearance (light or dark)
            //   - "NSAppearanceNameAqua": use light appearance
            //   - "NSAppearanceNameDarkAqua": use dark appearance
            // (must be set on main thread and before AWT/Swing is initialized;
            //  setting it on AWT thread does not work)
            System.setProperty( "apple.awt.application.appearance", "system" );
        }
        // Linux
        if( SystemInfo.isLinux ) {
            // enable custom window decorations
            JFrame.setDefaultLookAndFeelDecorated( true );
            JDialog.setDefaultLookAndFeelDecorated( true );
        }

        // 1️⃣ EDT 异常统一兜底（Swing 核心）
        Toolkit.getDefaultToolkit()
                .getSystemEventQueue()
                .push(new ExceptionEventQueue());

        // 2️⃣ 后台线程异常兜底
        Thread.setDefaultUncaughtExceptionHandler(
                (t, e) -> SwingExceptionHandler.handle(e)
        );


        ForestConfig.init();

        SwingUtilities.invokeLater(() -> {

            // set look and feel
            try {
                UIManager.setLookAndFeel(LightTheme.class.getName());
            } catch (Exception e) {
                // fallback
                FlatLightLaf.setup();
                throw new RuntimeException(e);
            }

            StandardChartTheme chartTheme = (StandardChartTheme) StandardChartTheme.createJFreeTheme();
            chartTheme.setExtraLargeFont(new Font("宋体", Font.BOLD, 20)); // 设置标题字体
            chartTheme.setLargeFont(new Font("宋体", Font.BOLD, 15)); // 设置轴标签字体
            chartTheme.setRegularFont(new Font("宋体", Font.PLAIN, 12)); // 设置图例字体
            ChartFactory.setChartTheme(chartTheme);

            // create frame
            MainFrame frame = MainFrame.getInstance();
            frame.showLogin(true);
            frame.setVisible(true);
        });
    }
}
