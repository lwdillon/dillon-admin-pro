package com.dillon.lw;

import com.dillon.lw.exception.ExceptionEventQueue;
import com.dillon.lw.exception.SwingExceptionHandler;
import com.dillon.lw.http.ForestConfig;
import com.dillon.lw.theme.LightTheme;
import com.dillon.lw.updater.ClientAutoUpdater;
import com.dillon.lw.view.frame.MainFrame;
import com.formdev.flatlaf.FlatLaf;
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
        configurePlatformSystemProperties();
        configureLinuxWindowDecorations();

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

    /**
     * 配置平台相关系统属性。
     * <p>
     * 这些属性必须在 AWT/Swing 初始化之前设置，否则 macOS 的菜单栏与标题栏行为
     * 不会按预期生效，所以单独抽成一个最先执行的方法。
     * </p>
     */
    private static void configurePlatformSystemProperties() {
        if (!SystemInfo.isMacOS) {
            return;
        }

        // 把 Swing 菜单栏挂到 macOS 顶部系统菜单栏，而不是停留在窗口内部。
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        // 应用名称会显示在 macOS 左上角应用菜单中。
        System.setProperty("apple.awt.application.name", "Dillon-Swing-UI");

        // 告诉 macOS 标题栏始终跟随系统浅色/深色外观。
        // 这个属性要求在主线程、且在 AWT 初始化之前设置。
        System.setProperty("apple.awt.application.appearance", "system");

        // macOS 的标题栏延伸通过 apple.awt.fullWindowContent 等属性完成，
        // 不需要 FlatLaf 的 native window decorations。
        // 某些打包出来的 JAR URL 可能形如 file://Mac/...，会让 FlatLaf 在解析原生库位置时失败。
        System.setProperty("flatlaf.useNativeWindowDecorations", "false");
        FlatLaf.setUseNativeWindowDecorations(false);
    }

    /**
     * Linux 平台默认不会自动启用自定义窗口装饰，这里显式打开。
     */
    private static void configureLinuxWindowDecorations() {
        if (!SystemInfo.isLinux) {
            return;
        }

        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
    }
}
