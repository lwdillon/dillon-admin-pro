/*
 * Created by JFormDesigner on Fri Jan 24 09:32:20 CST 2025
 */

package com.dillon.lw.view.login;

import com.dillon.lw.api.system.AuthApi;
import com.dillon.lw.components.AutoCompleteField;
import com.dillon.lw.components.notice.WMessage;
import com.dillon.lw.config.AppPrefs;
import com.dillon.lw.config.UserHistory;
import com.dillon.lw.config.UserHistoryService;
import com.dillon.lw.eventbus.EventBusCenter;
import com.dillon.lw.eventbus.event.LoginEvent;
import com.dillon.lw.exception.SwingExceptionHandler;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthLoginReqVO;
import com.dillon.lw.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.dillon.lw.swing.rx.SwingSchedulers;
import com.dillon.lw.store.AppStore;
import com.dillon.lw.view.frame.MainFrame;
import com.dtflys.forest.Forest;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.components.FlatButton;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 登录页面板（Swing 版本）。
 * <p>
 * 注意：本类同时包含 JFormDesigner 生成代码和手写逻辑：
 * 1. `GEN-BEGIN/GEN-END` 之间由设计器维护，不要手改；
 * 2. 业务行为、监听器、接口调用放在生成区之外。
 * </p>
 */
public class LoginPane extends JPanel {
    private static final int FEATURE_SWITCH_INTERVAL_MS = 3000;
    private static final String LOGIN_BUTTON_TEXT = "登录";
    private static final String LOGIN_LOADING_TEXT = "正在请求登录...";

    /**
     * 左侧轮播展示的功能介绍。
     */
    private final List<FunctionType> functions = new ArrayList<>();

    /**
     * 左侧功能卡片轮播定时器。
     */
    private Timer timer;
    /**
     * 当前登录请求的订阅句柄。
     * <p>
     * 登录页只允许同时存在一个有效登录请求：
     * 1. 防止用户连续点击造成重复登录；
     * 2. 在页面被移除时可以主动 dispose，避免旧页面收到回调。
     * </p>
     */
    private final AtomicReference<Disposable> currentLoginDisposable = new AtomicReference<Disposable>();

    public LoginPane() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        // Generated using JFormDesigner non-commercial license
        infoPane = new JPanel();
        contentPane = new JPanel();
        logoLabel = new JLabel();
        titleLabel = new JLabel();
        subTitleLabel = new JLabel();
        progressBar = new JProgressBar();
        userNameField = new AutoCompleteField();
        passwordField = new JPasswordField();
        panel1 = new JPanel();
        delButton = new FlatButton();
        rememberCheckBox = new JCheckBox();
        loginButton = new JButton();
        corporationText = new JLabel();

        //======== this ========
        setPreferredSize(new Dimension(900, 580));
        setLayout(new MigLayout(
            "fill,hidemode 3",
            // columns
            "0[grow,fill]0" +
            "[380!,fill]0",
            // rows
            "0[]0"));

        //======== infoPane ========
        {
            infoPane.setOpaque(false);
            infoPane.setBorder(new EmptyBorder(20, 40, 20, 40));
            infoPane.setLayout(new CardLayout());
        }
        add(infoPane, "cell 0 0,grow");

        //======== contentPane ========
        {
            contentPane.setBorder(new EmptyBorder(5, 30, 5, 45));
            contentPane.setLayout(new MigLayout(
                "fill,hidemode 3",
                // columns
                "[fill]",
                // rows
                "[fill]" +
                "[45!]0" +
                "[30!]0" +
                "[10!]0" +
                "[50!]" +
                "[50!]0" +
                "[40!,grow,center]0" +
                "[50!]" +
                "[]"));

            //---- logoLabel ----
            logoLabel.setText("text");
            contentPane.add(logoLabel, "cell 0 0,align center bottom,grow 0 0");

            //---- titleLabel ----
            titleLabel.setText("Dillon-Admin-Pro");
            titleLabel.setFont(new Font(".AppleSystemUIFont", Font.BOLD, 30));
            contentPane.add(titleLabel, "cell 0 1,align center bottom,grow 0 0");

            //---- subTitleLabel ----
            subTitleLabel.setText("\u6b22\u8fce\u767b\u5f55");
            contentPane.add(subTitleLabel, "cell 0 2,align center top,grow 0 0");
            contentPane.add(progressBar, "cell 0 3");

            //---- userNameField ----
            userNameField.setText("admin");
            contentPane.add(userNameField, "cell 0 4,growy");

            //---- passwordField ----
            passwordField.setText("admin123");
            contentPane.add(passwordField, "cell 0 5,growy");

            //======== panel1 ========
            {
                panel1.setOpaque(false);
                panel1.setLayout(new MigLayout(
                    "fill,hidemode 3",
                    // columns
                    "[fill]",
                    // rows
                    "0[40!,grow,center]0"));

                //---- delButton ----
                delButton.setText("\u79fb\u9664\u5e10\u53f7");
                panel1.add(delButton, "cell 0 0");

                //---- rememberCheckBox ----
                rememberCheckBox.setText("\u8bb0\u4f4f\u5bc6\u7801");
                panel1.add(rememberCheckBox, "cell 0 0");
            }
            contentPane.add(panel1, "cell 0 6,alignx right,growx 0");

            //---- loginButton ----
            loginButton.setText("\u767b\u5f55");
            loginButton.setMinimumSize(new Dimension(49, 34));
            contentPane.add(loginButton, "cell 0 7,growy");

            //---- corporationText ----
            corporationText.setText("@liwen");
            corporationText.setForeground(new Color(0xcecece));
            contentPane.add(corporationText, "cell 0 8,align center bottom,grow 0 0");
        }
        add(contentPane, "cell 1 0,growy");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
        // JFormDesigner 之外的逻辑统一放在这里，避免影响 .jfd 维护。
        configureAutoCompleteField();
        configureButtons();
        loadSvg();
        initCustomComponents();
        initListeners();
    }

    /**
     * 刷新历史账号并回填最近一次成功登录账号。
     */
    public void initData() {
        List<UserHistory> users = UserHistoryService.loadUsers();
        userNameField.setDataList(users);

        String currentUserId = AppPrefs.prefs().get(AppPrefs.KEY_CURRENT_USER, "");
        if (currentUserId.isEmpty()) {
            clearLoginForm();
            return;
        }

        UserHistory current = findUserById(users, currentUserId);
        if (current == null) {
            clearLoginForm();
            return;
        }

        userNameField.setText(current.getUsername());
        boolean canFillPassword = AppPrefs.prefs().getBoolean(AppPrefs.KEY_LAST_LOGIN_OK, false);
        passwordField.setText(canFillPassword ? current.getPasswrod() : "");
        rememberCheckBox.setSelected(canFillPassword);
    }

    private UserHistory findUserById(List<UserHistory> users, String userId) {
        for (UserHistory user : users) {
            if (Objects.equals(user.getUserId(), userId)) {
                return user;
            }
        }
        return null;
    }

    private void clearLoginForm() {
        userNameField.setText("");
        passwordField.setText("");
        rememberCheckBox.setSelected(false);
    }

    private void configureButtons() {
        delButton.setButtonType(FlatButton.ButtonType.toolBarButton);
        delButton.setFocusable(false);
        delButton.putClientProperty("FlatLaf.internal.testing.ignore", true);
    }

    /**
     * 配置账号历史下拉行为（展示文本、搜索字段、选中回填密码）。
     */
    private void configureAutoCompleteField() {
        userNameField.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value == null ? "" : value.toString());
            if (value instanceof UserHistory) {
                label.setText(value.getUsername());
            }
            label.setOpaque(true);
            if (isSelected) {
                label.setForeground(list.getSelectionForeground());
                label.setBackground(list.getSelectionBackground());
            } else {
                label.setForeground(list.getForeground());
                label.setBackground(list.getBackground());
            }
            return label;
        });
        userNameField.setMapper(UserHistory::getUsername);
        userNameField.getList().setFixedCellHeight(40);
    }

    private void loadSvg() {
        functions.add(new FunctionType("用户管理", "用户管理功能允许管理员轻松管理系统中的用户", "icons/user-manage.svg"));
        functions.add(new FunctionType("数据分析与报表", "数据分析与报表功能提供了系统内部数据的分析和可视化工具", "icons/baobiaofenxi.svg"));
        functions.add(new FunctionType("权限管理", "权限管理功能允许管理员灵活设置系统内各项功能的访问权限", "icons/quanxian.svg"));
        functions.add(new FunctionType("日志审计与监控", "日志审计与监控功能记录和跟踪系统内的操作日志", "icons/jiankong.svg"));
    }

    private void initCustomComponents() {
        logoLabel.setText("");
        logoLabel.setIcon(new FlatSVGIcon("icons/guanli.svg", 60, 60));
        subTitleLabel.putClientProperty("FlatLaf.style", "font: bold 20");

        this.setBackground(new Color(0x32BAF6));
        progressBar.setVisible(false);
        userNameField.setFont(UIManager.getFont("h3.font"));
        clearLoginForm();
        userNameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "用户名");
        userNameField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("icons/icon_username.svg", 35, 35));
        userNameField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);

        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "用户密码");
        passwordField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("icons/mima.svg", 35, 35));
        passwordField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        passwordField.setFont(UIManager.getFont("h3.font"));

        loginButton.setForeground(new Color(0xffffff));
        loginButton.setBackground(new Color(0x256EF6));


        // 创建面板数组
        for (FunctionType function : functions) {
            JPanel panel = new JPanel(new BorderLayout(20, 30));
            panel.setOpaque(false);
            JPanel panel2 = new JPanel(new BorderLayout(10, 0));
            panel2.setOpaque(false);

            JLabel label = new JLabel(new FlatSVGIcon(function.getIcon(), 400, 400));
            JLabel mainLabel = new JLabel(function.getTitle());
            mainLabel.setFont(UIManager.getFont("h0.font"));
            mainLabel.setHorizontalAlignment(SwingConstants.CENTER);
            JLabel subLabel = new JLabel("<html>" + function.getDescription() + "</html>");
            subLabel.setFont(UIManager.getFont("h3.font"));
            subLabel.setHorizontalAlignment(SwingConstants.CENTER);
            label.setHorizontalAlignment(SwingConstants.CENTER);

            panel2.add(mainLabel, BorderLayout.NORTH);
            panel2.add(subLabel, BorderLayout.CENTER);

            panel.add(label, BorderLayout.NORTH);
            panel.add(panel2, BorderLayout.CENTER);

            infoPane.add(panel);

        }

    }

    /**
     * 统一注册交互监听器，便于后续维护。
     */
    private void initListeners() {
        loginButton.addActionListener(e -> login());
        installEnterSubmitShortcut();
        timer = new Timer(FEATURE_SWITCH_INTERVAL_MS, e -> {
            CardLayout cardLayout = (CardLayout) infoPane.getLayout();
            cardLayout.next(infoPane);
        });

        userNameField.getList().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;

            UserHistory selectedUser = userNameField.getSelectedValue();
            if (selectedUser == null) return;

            // 仅用于 UI 预览，不代表最终提交密码。
            passwordField.setText(selectedUser.getPasswrod());
        });

        delButton.addActionListener(e -> {
            UserHistory selectedUser = userNameField.getSelectedValue();
            if (selectedUser != null) {
                UserHistoryService.removeUser(selectedUser.getUserId());
                clearLoginForm();
                userNameField.setDataList(UserHistoryService.loadUsers());
            }
        });
    }

    private void installEnterSubmitShortcut() {
        KeyAdapter submitOnEnter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && loginButton.isEnabled()) {
                    loginButton.doClick();
                }
            }
        };
        userNameField.addKeyListener(submitOnEnter);
        passwordField.addKeyListener(submitOnEnter);
    }

    /**
     * 登录流程：
     * 1. 输入校验
     * 2. 登录获取 Token
     * 3. 拉取权限菜单
     * 4. 切换主界面并按需记录历史
     * <p>
     * 这里使用 RxJava 的原因是把“后台调用”和“回到 EDT 更新界面”拆得很清楚：
     * 1. `subscribeOn(Schedulers.io())` 负责把网络请求放到 IO 线程；
     * 2. `observeOn(SwingSchedulers.edt())` 之后的 UI 状态恢复、成功提示都固定运行在 EDT；
     * 3. `Disposable` 让页面销毁时能安全取消回调链。
     * </p>
     */
    private void login() {
        if (!validateCredentials()) {
            return;
        }

        if (isLoginRunning()) {
            return;
        }

        final AuthLoginReqVO loginRequest = createLoginRequest();
        final AtomicReference<Disposable> loginDisposableRef = new AtomicReference<Disposable>();
        Single
                /*
                 * 第一步：登录接口本身是同步 HTTP 调用，用 fromCallable 包装后，
                 * 可以把它交给 RxJava 放到后台线程执行，并自动把异常转换成 onError。
                 */
                .fromCallable(() -> Forest.client(AuthApi.class).login(loginRequest).getCheckedData())
                /*
                 * 登录成功后先把 token 写入全局状态。
                 * 这样下面串行请求权限信息时，会天然使用这次最新的登录态。
                 */
                .doOnSuccess(AppStore::setAuthLoginRespVO)
                /*
                 * 第二步：串行拉取权限信息。
                 * 这里继续用 fromCallable 包同步调用，保持整条链的写法一致。
                 */
                .flatMap(ignored -> Single.fromCallable(() -> Forest.client(AuthApi.class).getPermissionInfo().getCheckedData()))
                .subscribeOn(Schedulers.io())
                .observeOn(SwingSchedulers.edt())//后续切到UI线程中运行
                .doOnSubscribe(disposable -> {
                    loginDisposableRef.set(disposable);
                    currentLoginDisposable.set(disposable);
                    /*
                     * doOnSubscribe 不保证天然发生在 EDT，所以按钮和进度条状态要显式切回 UI 线程。
                     */
                    SwingSchedulers.runOnEdt(this::updateUiBeforeRequest);
                })
                .doFinally(() -> {
                    clearLoginDisposable(loginDisposableRef.get());
                    /*
                     * doFinally 也可能出现在 dispose/异常等非 EDT 路径，
                     * 因此统一用 SwingSchedulers.runOnEdt(...) 恢复界面状态。
                     */
                    SwingSchedulers.runOnEdt(this::resetUiAfterRequest);
                })
                .subscribe(
                        permission -> handleLoginSuccess(permission, loginRequest),
                        SwingExceptionHandler::handle
                );
    }

    private boolean isLoginRunning() {
        Disposable runningDisposable = currentLoginDisposable.get();
        return runningDisposable != null && !runningDisposable.isDisposed();
    }

    /**
     * 清理当前登录句柄。
     * <p>
     * 使用 compare-and-set 是为了避免“旧请求完成时把新请求句柄误清空”。
     * </p>
     */
    private void clearLoginDisposable(Disposable disposable) {
        currentLoginDisposable.compareAndSet(disposable, null);
    }

    private AuthLoginReqVO createLoginRequest() {
        AuthLoginReqVO reqVO = new AuthLoginReqVO();
        reqVO.setUsername(userNameField.getText().trim());
        reqVO.setPassword(new String(passwordField.getPassword()));
        return reqVO;
    }

    private boolean validateCredentials() {
        String username = userNameField.getText() == null ? "" : userNameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty()) {
            WMessage.showMessageWarning(MainFrame.getInstance(), "请输入用户名");
            userNameField.requestFocusInWindow();
            return false;
        }
        if (password.isEmpty()) {
            WMessage.showMessageWarning(MainFrame.getInstance(), "请输入密码");
            passwordField.requestFocusInWindow();
            return false;
        }
        return true;
    }

    private void updateUiBeforeRequest() {
        loginButton.setText(LOGIN_LOADING_TEXT);
        loginButton.setEnabled(false);
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
    }

    /**
     * 无论成功还是失败，都要把按钮和进度条恢复到初始状态。
     */
    private void resetUiAfterRequest() {
        loginButton.setEnabled(true);
        loginButton.setText(LOGIN_BUTTON_TEXT);
        progressBar.setVisible(false);
        progressBar.setIndeterminate(false);
    }

    /**
     * 登录成功后的统一处理：
     * 1. 缓存权限信息
     * 2. 停止登录页轮播
     * 3. 异步加载字典
     * 4. 发布登录成功事件
     * 5. 根据“记住密码”设置维护历史账号
     */
    private void handleLoginSuccess(AuthPermissionInfoRespVO authPermissionInfo, AuthLoginReqVO loginRequest) {
        AppStore.setAuthPermissionInfoRespVO(authPermissionInfo);
        timer.stop();
        AppStore.loadDictData();
        EventBusCenter.get().post(new LoginEvent(LoginEvent.LOGIN_SUCCESS));

        if (rememberCheckBox.isSelected()) {
            UserHistoryService.recordLogin(
                    new UserHistory(
                            String.valueOf(authPermissionInfo.getUser().getId()),
                            authPermissionInfo.getUser().getUsername(),
                            loginRequest.getPassword()
                    ),
                    true
            );
            return;
        }

        UserHistoryService.removeUser(String.valueOf(authPermissionInfo.getUser().getId()));
    }

    public void startLogoInfo() {
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    /**
     * 登录页左侧功能介绍模型，仅用于 UI 展示。
     */
    private static class FunctionType {
        private final String title;
        private final String description;
        private final String icon;

        public FunctionType(String title, String description, String icon) {
            this.title = title;
            this.description = description;
            this.icon = icon;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }
    }

    @Override
    public void removeNotify() {
        Disposable loginDisposable = currentLoginDisposable.getAndSet(null);
        if (loginDisposable != null && !loginDisposable.isDisposed()) {
            loginDisposable.dispose();
        }
        timer.stop();
        super.removeNotify();
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner non-commercial license
    private JPanel infoPane;
    private JPanel contentPane;
    private JLabel logoLabel;
    private JLabel titleLabel;
    private JLabel subTitleLabel;
    private JProgressBar progressBar;
    private AutoCompleteField<UserHistory> userNameField;
    private JPasswordField passwordField;
    private JPanel panel1;
    private FlatButton delButton;
    private JCheckBox rememberCheckBox;
    private JButton loginButton;
    private JLabel corporationText;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
