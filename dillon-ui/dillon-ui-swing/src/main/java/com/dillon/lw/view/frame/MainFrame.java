package com.dillon.lw.view.frame;

import com.dillon.lw.api.system.AuthApi;
import com.dillon.lw.config.AppPrefs;
import com.dillon.lw.eventbus.EventBusCenter;
import com.dillon.lw.eventbus.event.LoginEvent;
import com.dillon.lw.store.AppStore;
import com.dillon.lw.theme.LightTheme;
import com.dillon.lw.view.login.LoginPane;
import com.dillon.lw.view.mainpane.MainPane;
import com.dtflys.forest.Forest;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.util.SystemInfo;
import com.google.common.eventbus.Subscribe;
import com.jidesoft.swing.JideTabbedPane;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CompletableFuture;

import static com.dillon.lw.config.AppPrefs.KEY_CURRENT_USER;
import static com.dillon.lw.config.AppPrefs.KEY_UI_THEME;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * 主窗口（应用全局唯一）
 * <p>
 * 职责：
 * 1. 管理窗口生命周期
 * 2. 登录 / 主界面切换
 * 3. 窗口样式（FlatLaf / macOS）
 * 4. 登录事件响应
 */
public class MainFrame extends JFrame {

    /* ======================== 单例相关 ======================== */

    /**
     * 单例实例（volatile 保证双重检查安全）
     */
    private static volatile MainFrame instance;

    /**
     * 获取主窗口实例
     */
    public static MainFrame getInstance() {
        if (instance == null) {
            synchronized (MainFrame.class) {
                if (instance == null) {
                    instance = new MainFrame();
                }
            }
        }
        return instance;
    }

    /* ======================== UI 组件 ======================== */

    /**
     * JFrame 自带 rootPane，保存成字段避免多次 get
     */
    private final JRootPane rootPane;

    /**
     * 顶部标题栏
     */
    private TitlePanel titlePanel;

    /**
     * 主界面
     */
    private MainPane mainPane;

    /**
     * 登录界面
     */
    private LoginPane loginPane;


    /* ======================== 构造函数 ======================== */

    /**
     * 私有构造，禁止外部 new
     */
    private MainFrame() {
        super(System.getProperty("app.name"));

        // 窗口关闭策略
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 缓存 rootPane
        this.rootPane = getRootPane();

        /* ---------------- macOS / Windows 样式适配 ---------------- */

        if (SystemInfo.isMacOS) {

            // macOS 沉浸式标题栏
            if (SystemInfo.isMacFullWindowContentSupported) {
                rootPane.putClientProperty("apple.awt.fullWindowContent", true);
                rootPane.putClientProperty("apple.awt.transparentTitleBar", true);
                rootPane.putClientProperty(
                        FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING,
                        FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING_LARGE
                );

                // Java 17+ 可隐藏窗口标题
                if (SystemInfo.isJava_17_orLater) {
                    rootPane.putClientProperty("apple.awt.windowTitleVisible", false);
                } else {
                    setTitle(null);
                }
            }

            // Java 8~10 需要显式开启全屏
            if (!SystemInfo.isJava_11_orLater) {
                rootPane.putClientProperty("apple.awt.fullscreenable", true);
            }

        } else {
            // Windows / Linux
            rootPane.putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);
            rootPane.putClientProperty(FlatClientProperties.TITLE_BAR_HEIGHT, 45);
        }

        // 注册事件监听
        EventBusCenter.get().register(this);
    }

    /* ======================== 界面切换 ======================== */

    /**
     * 显示登录界面
     */
    public void showLogin() {

        // set look and feel
        try {
            UIManager.setLookAndFeel(LightTheme.class.getName());
        } catch (Exception e) {
            // fallback
            FlatLightLaf.setup();
            throw new RuntimeException(e);
        }
        // 登录页不显示最大化 / 最小化
        rootPane.putClientProperty(
                FlatClientProperties.TITLE_BAR_SHOW_ICONIFFY, false);
        rootPane.putClientProperty(
                FlatClientProperties.TITLE_BAR_SHOW_MAXIMIZE, false);

        setExtendedState(JFrame.NORMAL);


        setSize(900, 600);
        setResizable(false);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.removeAll();
        mainPane = null;
        titlePanel = null;
        loginPane = new LoginPane();
        contentPane.add(loginPane, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
        revalidate();
        repaint();

    }

    /**
     * 显示主界面
     */
    public void showMain() {


        // set look and feel
        try {
            String userId = AppStore.getUserId() + "";
            String theme = AppPrefs.prefs().get(KEY_UI_THEME + "_" + userId, "com.dillon.lw.theme.LightTheme");
            UIManager.setLookAndFeel(theme);
        } catch (Exception e) {
            // fallback
            try {
                UIManager.setLookAndFeel(LightTheme.class.getName());
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (InstantiationException ex) {
                throw new RuntimeException(ex);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            } catch (UnsupportedLookAndFeelException ex) {
                throw new RuntimeException(ex);
            }
        }

        // 主界面允许最大化 / 最小化
        rootPane.putClientProperty(
                FlatClientProperties.TITLE_BAR_SHOW_ICONIFFY, true);
        rootPane.putClientProperty(
                FlatClientProperties.TITLE_BAR_SHOW_MAXIMIZE, true);

        setResizable(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.removeAll();
        loginPane=null;
        // 创建主界面组件
        titlePanel = new TitlePanel();
        mainPane = new MainPane();
        contentPane.add(titlePanel, BorderLayout.PAGE_START);
        contentPane.add(mainPane, BorderLayout.CENTER);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        revalidate();
        repaint();
        setLocationRelativeTo(null);


    }

    /* ======================== 对外访问 ======================== */

    public JideTabbedPane getTabbedPane() {
        return mainPane != null ? mainPane.getTabbedPane() : null;
    }

    public MainPane getMainPane() {
        return mainPane;
    }

    public TitlePanel getTitlePanel() {
        return titlePanel;
    }

    /* ======================== 登录事件 ======================== */

    /**
     * 登录结果事件
     */
    @Subscribe
    private void onLoginMain(LoginEvent event) {

        EventQueue.invokeLater(() -> {

            // LAF 截图过渡（防止 UI 抖动）
            FlatAnimatedLafChange.showSnapshot();

            if (event.getCode() == 0) {
                showMain();
            } else {
                showLogin();
            }

            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        });
    }

    /* ======================== 退出处理 ======================== */

    @Override
    public void dispose() {

        // 注销事件监听
        EventBusCenter.get().unregister(this);

        // 异步登出（避免阻塞 EDT）
        CompletableFuture.runAsync(() -> {
            try {
                Forest.client(AuthApi.class).logout();
            } catch (Exception ignored) {
            }
        }).whenComplete((v, e) -> {
            // 确保退出 JVM
            System.exit(0);
        });

        FlatUIDefaultsInspector.hide();
        super.dispose();
    }
}
