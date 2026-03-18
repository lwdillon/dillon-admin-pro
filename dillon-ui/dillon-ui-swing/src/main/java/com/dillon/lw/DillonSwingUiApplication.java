package com.dillon.lw;

import com.dillon.lw.exception.ExceptionEventQueue;
import com.dillon.lw.exception.SwingExceptionHandler;
import com.dillon.lw.http.ForestConfig;
import com.dillon.lw.theme.LightTheme;
import com.dillon.lw.updater.ClientAutoUpdater;
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

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 应用程序启动入口
 */
public class DillonSwingUiApplication {

    /**
     * 静态初始化块：配置全局动画定时源
     */
    static {
        TimingSource ts = new SwingTimerTimingSource();
        Animator.setDefaultTimingSource(ts);
        ts.init();
    }

    public static void main(String[] args) {
        // 1. 加载 application.properties 配置
        loadApplicationProperties();
        // 5. 初始化网络框架 (Forest)
        ForestConfig.init();

        // 3. 针对不同操作系统进行 UI 适配
        setupPlatformEnvironment();

        // 4. 全局异常捕捉机制
        setupGlobalExceptionHandler();


        // 6. 在事件分发线程 (EDT) 中启动 UI
        SwingUtilities.invokeLater(() -> {
            // 初始化主题
            initLookAndFeel();

            // 初始化图表库设置
            initChartTheme();

            // 创建并显示主窗口
            MainFrame frame = MainFrame.getInstance();
            frame.showLogin(true);
            frame.setVisible(true);
            startAutoUpdateCheckAsync();
        });
    }

    private static void startAutoUpdateCheckAsync() {
        Thread updateThread = new Thread(() -> {
            try {
                ClientAutoUpdater.checkAndUpdateIfNeeded();
            } catch (Exception e) {
                SwingExceptionHandler.handle(e);
            }
        }, "swing-client-auto-updater");
        updateThread.setDaemon(true);
        updateThread.start();
    }

    /**
     * 加载资源文件中的配置到 System Properties
     */
    private static void loadApplicationProperties() {
        Properties properties = new Properties();
        try (InputStreamReader in = new InputStreamReader(
                Resources.getResourceAsStream("/application.properties"), UTF_8)) {
            properties.load(in);
            properties.forEach((key, value) -> System.setProperty(String.valueOf(key), String.valueOf(value)));
        } catch (IOException | NullPointerException e) {
            System.err.println("警告：未能加载 application.properties 配置文件");
        }
    }

    /**
     * 针对 macOS 和 Linux 系统的 UI 渲染参数设置
     */
    private static void setupPlatformEnvironment() {
        if (SystemInfo.isMacOS) {
            // 将菜单栏移动到屏幕顶部
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            // 设置屏幕菜单栏显示的应用名称
            System.setProperty("apple.awt.application.name", System.getProperty("app.name", "DillonSwing"));
            // 窗口标题栏外观跟随系统
            System.setProperty("apple.awt.application.appearance", "system");
        }

        if (SystemInfo.isLinux) {
            // Linux 下开启自定义窗口装饰（如圆角、深色标题栏等）
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
        }
    }

    /**
     * 配置全局异常处理器
     */
    private static void setupGlobalExceptionHandler() {
        // 1. EDT (事件分发线程) 异常兜底，防止 UI 线程因异常崩溃无响应
        Toolkit.getDefaultToolkit()
                .getSystemEventQueue()
                .push(new ExceptionEventQueue());

        // 2. 所有后台工作线程的未捕获异常统一由 SwingExceptionHandler 处理
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> SwingExceptionHandler.handle(e));
    }

    /**
     * 初始化 LookAndFeel 主题样式
     */
    private static void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(LightTheme.class.getName());
        } catch (Exception e) {
            // 如果自定义主题加载失败，回退到 FlatLaf 基础明亮主题
            FlatLightLaf.setup();
            SwingExceptionHandler.handle(e);
        }
    }

    /**
     * 配置 JFreeChart 图表库的中文支持及字体样式
     */
    private static void initChartTheme() {
        StandardChartTheme chartTheme = (StandardChartTheme) StandardChartTheme.createJFreeTheme();
        Font baseFont = new Font("Microsoft YaHei", Font.PLAIN, 12); // 推荐使用微软雅黑

        chartTheme.setExtraLargeFont(baseFont.deriveFont(Font.BOLD, 20f)); // 标题
        chartTheme.setLargeFont(baseFont.deriveFont(Font.BOLD, 15f));      // 轴标签
        chartTheme.setRegularFont(baseFont);                               // 图例/普通文字

        ChartFactory.setChartTheme(chartTheme);
    }
}
