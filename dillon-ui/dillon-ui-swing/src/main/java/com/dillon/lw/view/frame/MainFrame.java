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
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.SystemInfo;
import com.google.common.eventbus.Subscribe;
import com.jidesoft.swing.JideTabbedPane;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CompletableFuture;

import static com.dillon.lw.config.AppPrefs.KEY_UI_THEME;

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

    /* ======================== 单例 ======================== */

    private static volatile MainFrame instance;

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

    /* ======================== CardLayout ======================== */

    private static final String CARD_LOGIN = "login";
    private static final String CARD_MAIN  = "main";

    private final JRootPane rootPane;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    /* ======================== 页面 ======================== */

    private LoginPane loginPane;
    private MainPane mainPane;
    private TitlePanel titlePanel;

    /* ======================== 构造 ======================== */

    private MainFrame() {
        super(System.getProperty("app.name"));

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.rootPane = getRootPane();

        initPlatformStyle();
        initCards();

        setContentPane(cardPanel);

        EventBusCenter.get().register(this);
    }

    /* ======================== 平台样式 ======================== */

    private void initPlatformStyle() {

        if (SystemInfo.isMacOS) {

            if (SystemInfo.isMacFullWindowContentSupported) {
                rootPane.putClientProperty("apple.awt.fullWindowContent", true);
                rootPane.putClientProperty("apple.awt.transparentTitleBar", true);
                rootPane.putClientProperty(
                        FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING,
                        FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING_LARGE
                );

                if (SystemInfo.isJava_17_orLater) {
                    rootPane.putClientProperty("apple.awt.windowTitleVisible", false);
                } else {
                    setTitle(null);
                }
            }

            if (!SystemInfo.isJava_11_orLater) {
                rootPane.putClientProperty("apple.awt.fullscreenable", true);
            }

        } else {
            rootPane.putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);
            rootPane.putClientProperty(FlatClientProperties.TITLE_BAR_HEIGHT, 45);
        }
    }

    /* ======================== 初始化页面 ======================== */

    private void initCards() {

        // 登录页
        loginPane = new LoginPane();
        cardPanel.add(loginPane, CARD_LOGIN);

        // 主界面
        titlePanel = new TitlePanel();
        mainPane = new MainPane();

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.add(titlePanel, BorderLayout.PAGE_START);
        mainContainer.add(mainPane, BorderLayout.CENTER);

        cardPanel.add(mainContainer, CARD_MAIN);
    }

    /* ======================== 页面切换 ======================== */

    public void showLogin(boolean isInit) {

        if (!isInit) {
            initTheme(LightTheme.class.getName());
        }
        setExtendedState(JFrame.NORMAL);
        setResizable(false);

        rootPane.putClientProperty(
                FlatClientProperties.TITLE_BAR_SHOW_ICONIFFY, false);
        rootPane.putClientProperty(
                FlatClientProperties.TITLE_BAR_SHOW_MAXIMIZE, false);

        cardLayout.show(cardPanel, CARD_LOGIN);
        loginPane.startLogoInfo();
        loginPane.initData();

        setSize(900, 600);
        setLocationRelativeTo(null);



    }

    public void showMain() {
        String userId = AppStore.getUserId() + "";
        String theme = AppPrefs.prefs()
                .get(KEY_UI_THEME + "_" + userId, LightTheme.class.getName());

        initTheme(theme);

        setExtendedState(JFrame.NORMAL);
        setResizable(true);

        rootPane.putClientProperty(
                FlatClientProperties.TITLE_BAR_SHOW_ICONIFFY, true);
        rootPane.putClientProperty(
                FlatClientProperties.TITLE_BAR_SHOW_MAXIMIZE, true);

        cardLayout.show(cardPanel, CARD_MAIN);
        mainPane.updateTreeTableRoot();

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) (screen.width * 0.9), (int) (screen.height * 0.9));
        setLocationRelativeTo(null);



    }

    /* ======================== 登录事件 ======================== */

    @Subscribe
    private void onLoginMain(LoginEvent event) {
        EventQueue.invokeLater(() -> {
            if (event.getCode() == 0) {
                showMain();
            } else {
                showLogin(false);
            }
        });
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

    /* ======================== 主题切换 ======================== */

    private void initTheme(String theme) {
        EventQueue.invokeLater(() -> {
//            FlatAnimatedLafChange.showSnapshot();
            try {
                UIManager.setLookAndFeel(theme);
            } catch (Exception ex) {
                LoggingFacade.INSTANCE.logSevere(null, ex);
            }
            // 4. 刷新所有窗口（关键）
            FlatLaf.updateUI();

            // 6. 动画结束
//            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        });
    }

    /* ======================== 退出 ======================== */

    @Override
    public void dispose() {

        EventBusCenter.get().unregister(this);

        CompletableFuture.runAsync(() -> {
            try {
                Forest.client(AuthApi.class).logout();
            } catch (Exception ignored) {
            }
        }).whenComplete((v, e) -> System.exit(0));

        FlatUIDefaultsInspector.hide();
        super.dispose();
    }
}
