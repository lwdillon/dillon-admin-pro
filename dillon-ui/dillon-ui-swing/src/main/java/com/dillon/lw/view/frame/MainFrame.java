package com.dillon.lw.view.frame;

import com.dillon.lw.api.system.AuthApi;
import com.dillon.lw.config.AppPrefs;
import com.dillon.lw.eventbus.EventBusCenter;
import com.dillon.lw.eventbus.event.LoginEvent;
import com.dillon.lw.swing.rx.SwingSchedulers;
import com.dillon.lw.store.AppStore;
import com.dillon.lw.theme.LightTheme;
import com.dillon.lw.view.login.LoginPane;
import com.dillon.lw.view.mainpane.MainPane;
import com.dillon.lw.websocket.WebSocketNoticeService;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.dillon.lw.components.notice.WMessage;
import com.dtflys.forest.Forest;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;
import com.google.common.eventbus.Subscribe;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import com.jidesoft.swing.JideTabbedPane;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

import static com.dillon.lw.config.AppPrefs.KEY_UI_THEME;
import static com.dillon.lw.utils.ColorUtils.withAlpha;

/**
 * 应用主窗口类
 * 采用单例模式，负责管理：
 * 1. 登录界面与主界面的切换 (CardLayout)
 * 2. 窗口装饰样式 (macOS/Windows 适配)
 * 3. 全局主题切换
 * 4. 退出逻辑
 */
public class MainFrame extends JFrame {

    // --- 常量定义 ---
    private static final String CARD_LOGIN = "login";
    private static final String CARD_MAIN = "main";
    private static final int DEFAULT_LOGIN_WIDTH = 900;
    private static final int DEFAULT_LOGIN_HEIGHT = 600;
    private static final float MAIN_SCREEN_RATIO = 0.9f;

    private static volatile MainFrame instance;

    // --- 核心组件 ---
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private LoginPane loginPane;
    private MainPane mainPane;
    private TitlePanel titlePanel;

    /**
     * 获取单例实例
     */
    public static MainFrame getInstance() {
        if (instance == null) {
            synchronized (MainFrame.class) {
                if (instance == null) instance = new MainFrame();
            }
        }
        return instance;
    }

    private MainFrame() {
        super(System.getProperty("app.name", "LwAdmin"));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initPlatformWindowStyle(); // 适配平台特性
        initInterfaceCards();      // 初始化卡片页面内容

        setContentPane(cardPanel);
        EventBusCenter.get().register(this);
    }

    /**
     * 适配不同操作系统的窗口装饰逻辑
     */
    private void initPlatformWindowStyle() {
        JRootPane root = getRootPane();

        if (SystemInfo.isMacOS) {
            // macOS 样式：融合标题栏
            root.putClientProperty("apple.awt.fullWindowContent", true);
            root.putClientProperty("apple.awt.transparentTitleBar", true);
            root.putClientProperty(FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING,
                    FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING_LARGE);

            if (SystemInfo.isJava_17_orLater) {
                root.putClientProperty("apple.awt.windowTitleVisible", false);
            } else {
                setTitle(null);
            }
        } else {
            // Windows/Linux 样式
            root.putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);
            root.putClientProperty(FlatClientProperties.TITLE_BAR_HEIGHT, 45);
        }

        // 设置通用的标题栏属性
        root.putClientProperty(FlatClientProperties.TITLE_BAR_BACKGROUND, Color.GRAY);
    }

    /**
     * 初始化登录与主界面的容器
     */
    private void initInterfaceCards() {
        // 1. 初始化登录页
        loginPane = new LoginPane();
        cardPanel.add(loginPane, CARD_LOGIN);

        // 2. 初始化主界面容器 (包含渐变背景绘制)
        titlePanel = new TitlePanel();
        mainPane = new MainPane();
        mainPane.setOpaque(false);

        BackgroundPanel mainContainer = new BackgroundPanel();
        mainContainer.setLayout(new BorderLayout());
        mainContainer.add(titlePanel, BorderLayout.PAGE_START);
        mainContainer.add(mainPane, BorderLayout.CENTER);

        cardPanel.add(mainContainer, CARD_MAIN);

        // 注册 WebSocket 消息回调：统一在主窗口层处理推送消息展示。
        WebSocketNoticeService.getInstance().setMessageListener(this::onWebSocketMessage);
    }

    // ===================================================================================
    // 界面切换控制逻辑
    // ===================================================================================

    /**
     * 展示登录界面
     *
     * @param isInit 是否为应用启动时的第一次展示
     */
    public void showLogin(boolean isInit) {
        // 回到登录页时立即关闭 websocket，避免旧 token 继续收消息。
        WebSocketNoticeService.getInstance().stop();
        AppStore.clearSession();
        if (titlePanel != null) {
            titlePanel.resetUnreadNoticeCount();
        }
        if (!isInit) initTheme(LightTheme.class.getName());

        configureLoginWindow();
        mainPane.closeAllTab();
        cardLayout.show(cardPanel, CARD_LOGIN);
        loginPane.startLogoInfo();
        loginPane.initData();

        centerLoginWindow();
    }

    /**
     * 展示主业务界面
     */
    public void showMain() {
        String theme = resolveUserTheme();
        initTheme(theme);
        if (titlePanel != null) {
            titlePanel.resetUnreadNoticeCount();
        }

        configureMainWindow();
        cardLayout.show(cardPanel, CARD_MAIN);
        mainPane.updateTreeTableRoot(AppStore.getMenus());
        maximizeToMainRatio();

        // 登录成功后开启 websocket，使用当前 accessToken 建立连接。
        WebSocketNoticeService.getInstance().start(AppStore.getAccessToken());
    }

    /**
     * 处理 WebSocket 推送消息。
     * 当前重点处理 notice-push；其它类型可按需扩展。
     */
    private void onWebSocketMessage(String rawMessage) {
        try {
            JSONObject frame = JSONUtil.parseObj(rawMessage);
            String type = frame.getStr("type");
            String content = frame.getStr("content");
            if (!"notice-push".equals(type)) {
                return;
            }
            String title = "公告推送";
            if (content != null) {
                try {
                    JSONObject notice = JSONUtil.parseObj(content);
                    if (notice.containsKey("title")) {
                        title = notice.getStr("title");
                    }
                } catch (Exception ignored) {
                    // content 不是 JSON 时，走默认文案
                }
            }
            final String finalTitle = title;
            EventQueue.invokeLater(() -> {
                if (titlePanel != null) {
                    titlePanel.increaseUnreadNoticeCount();
                }
                WMessage.showMessageInfo(MainFrame.getInstance(), "收到消息：" + finalTitle);
            });
        } catch (Exception ignored) {
            // 非标准消息帧直接忽略，避免影响 UI 主流程。
        }
    }

    private String resolveUserTheme() {
        String userId = String.valueOf(AppStore.getUserId());
        return AppPrefs.prefs().get(KEY_UI_THEME + "_" + userId, LightTheme.class.getName());
    }

    private void configureLoginWindow() {
        configureWindowTitleBar(false); // 隐藏缩放按钮
        setResizable(false);
    }

    private void centerLoginWindow() {
        setSize(DEFAULT_LOGIN_WIDTH, DEFAULT_LOGIN_HEIGHT);
        setLocationRelativeTo(null);
    }

    private void configureMainWindow() {
        configureWindowTitleBar(true); // 显示缩放按钮
        setResizable(true);
    }

    private void maximizeToMainRatio() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) (screen.width * MAIN_SCREEN_RATIO), (int) (screen.height * MAIN_SCREEN_RATIO));
        setLocationRelativeTo(null);
    }

    /**
     * 统一配置原生标题栏按钮的可见性
     */
    private void configureWindowTitleBar(boolean showFullControls) {
        JRootPane root = getRootPane();
        root.putClientProperty(FlatClientProperties.TITLE_BAR_SHOW_ICONIFFY, showFullControls);
        root.putClientProperty(FlatClientProperties.TITLE_BAR_SHOW_MAXIMIZE, showFullControls);
    }

    // ===================================================================================
    // 事件监听与主题管理
    // ===================================================================================

    /**
     * 响应登录事件总线
     */
    @Subscribe
    private void onLoginMain(LoginEvent event) {
        EventQueue.invokeLater(() -> {
            if (event.getCode() == LoginEvent.LOGIN_SUCCESS) {
                showMain();
            } else {
                showLogin(false);
            }
        });
    }

    /**
     * 切换全局 LookAndFeel 主题
     */
    private void initTheme(String theme) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(theme);
                FlatLaf.updateUI(); // 刷新所有组件 UI
            } catch (Exception ex) {
                LoggingFacade.INSTANCE.logSevere("Theme switching failed", ex);
            }
        });
    }

    // ===================================================================================
    // 内部类与生命周期
    // ===================================================================================

    /**
     * 带有径向渐变背景的容器面板
     */
    private class BackgroundPanel extends JPanel {
        private RadialGradientPaint gradientPaint;
        private int lastWidth = -1;
        private int lastHeight = -1;
        private Color lastAccent;
        private final float[] dist = {0.0f, 0.35f, 0.55f, 0.75f, 1.0f};

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 1. 填充基础背景色
                g2.setPaint(UIManager.getColor("App.mainBackground"));
                g2.fillRect(0, 0, getWidth(), getHeight());

                // 2. 检查主题强调色是否变更或尺寸变更
                Color accent = UIManager.getColor("Islands.accent");
                if (gradientPaint == null || lastWidth != getWidth() ||
                        lastHeight != getHeight() || !accent.equals(lastAccent)) {

                    updateGradient(accent);
                }

                g2.setPaint(gradientPaint);
                g2.fillRect(0, 0, getWidth(), getHeight());
            } finally {
                g2.dispose();
            }
        }

        private void updateGradient(Color base) {
            Point2D center = new Point2D.Float(UIScale.scale(200), UIScale.scale(-200));
            float radius = 400f; // 固定光晕半径，可根据需要调大
            Color[] colors = {
                    withAlpha(base, 140),
                    withAlpha(base, 90),
                    withAlpha(base, 70),
                    withAlpha(base, 40),
                    withAlpha(base, 0)
            };
            gradientPaint = new RadialGradientPaint(center, radius, dist, colors);
            lastWidth = getWidth();
            lastHeight = getHeight();
            lastAccent = base;
        }
    }

    @Override
    public void dispose() {
        EventBusCenter.get().unregister(this);
        WebSocketNoticeService.getInstance().stop();
        /*
         * 关闭主窗口时尽量通知服务端退出当前会话，
         * 但无论请求成功还是失败，都不阻塞客户端真正退出。
         */
        Completable
                .fromAction(() -> Forest.client(AuthApi.class).logout())
                .subscribeOn(Schedulers.io())
                .doFinally(() -> SwingSchedulers.runOnEdt(() -> System.exit(0)))
                .subscribe(() -> {
                }, ignored -> {
                });

        FlatUIDefaultsInspector.hide();
        super.dispose();
    }

    // --- Getters ---
    public JideTabbedPane getTabbedPane() {
        return mainPane != null ? mainPane.getTabbedPane() : null;
    }

    public MainPane getMainPane() {
        return mainPane;
    }

    public TitlePanel getTitlePanel() {
        return titlePanel;
    }
}
